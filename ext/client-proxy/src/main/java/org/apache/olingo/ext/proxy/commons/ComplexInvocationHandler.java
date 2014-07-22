/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.proxy.commons;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.ext.proxy.AbstractService;

public class ComplexInvocationHandler extends AbstractStructuredInvocationHandler {

  private static Pair<ODataComplexValue<? extends CommonODataProperty>, Class<?>> init(
          final AbstractService<?> service,
          final Class<?> reference) {

    final Class<?> complexTypeRef;
    if (Collection.class.isAssignableFrom(reference)) {
      complexTypeRef = ClassUtils.extractTypeArg(reference);
    } else {
      complexTypeRef = reference;
    }

    final ComplexType annotation = complexTypeRef.getAnnotation(ComplexType.class);
    if (annotation == null) {
      throw new IllegalArgumentException("Invalid complex type " + complexTypeRef);
    }

    final FullQualifiedName typeName =
            new FullQualifiedName(ClassUtils.getNamespace(complexTypeRef), annotation.name());

    final ODataComplexValue<? extends CommonODataProperty> complex =
            service.getClient().getObjectFactory().newComplexValue(typeName.toString());

    return new ImmutablePair<ODataComplexValue<? extends CommonODataProperty>, Class<?>>(complex, complexTypeRef);
  }

  public static ComplexInvocationHandler getInstance(
          final String propertyName,
          final Class<?> reference,
          final EntityInvocationHandler handler) {

    final Pair<ODataComplexValue<? extends CommonODataProperty>, Class<?>> init = init(handler.service, reference);
    return new ComplexInvocationHandler(init.getLeft(), init.getRight(), handler);
  }

  public static ComplexInvocationHandler getInstance(
          final ODataComplexValue<?> complex,
          final Class<?> reference,
          final AbstractService<?> service) {

    return new ComplexInvocationHandler(complex, reference, service);
  }

  public static ComplexInvocationHandler getInstance(
          final Class<?> typeRef,
          final AbstractService<?> service) {
    final Pair<ODataComplexValue<? extends CommonODataProperty>, Class<?>> init = init(service, typeRef);
    return new ComplexInvocationHandler(init.getLeft(), init.getRight(), service);
  }

  public static ComplexInvocationHandler getInstance(
          final Class<?> reference,
          final AbstractService<?> service,
          final CommonURIBuilder<?> uri) {
    final Pair<ODataComplexValue<? extends CommonODataProperty>, Class<?>> init = init(service, reference);
    return new ComplexInvocationHandler(init.getLeft(), init.getRight(), service, uri);
  }

  public static ComplexInvocationHandler getInstance(
          final ODataComplexValue<? extends CommonODataProperty> complex,
          final Class<?> reference,
          final AbstractService<?> service,
          final CommonURIBuilder<?> uri) {
    return new ComplexInvocationHandler(complex, reference, service, uri);
  }

  private ComplexInvocationHandler(
          final ODataComplexValue<? extends CommonODataProperty> complex,
          final Class<?> typeRef,
          final AbstractService<?> service,
          final CommonURIBuilder<?> uri) {

    super(typeRef, complex, service);
    this.uri = uri;
    this.baseURI = this.uri == null ? null : this.uri.build();
  }

  private ComplexInvocationHandler(
          final ODataComplexValue<? extends CommonODataProperty> complex,
          final Class<?> typeRef,
          final EntityInvocationHandler handler) {

    super(typeRef, complex, handler);
    this.uri = null;
  }

  private ComplexInvocationHandler(
          final ODataComplexValue<? extends CommonODataProperty> complex,
          final Class<?> typeRef,
          final AbstractService<?> service) {

    super(typeRef, complex, service);
    this.uri = null;
  }

  @SuppressWarnings("unchecked")
  public ODataComplexValue<CommonODataProperty> getComplex() {
    return (ODataComplexValue<CommonODataProperty>) this.internal;
  }

  @Override
  protected Object getNavigationPropertyValue(final NavigationProperty property, final Method getter) {
    if (!(internal instanceof ODataLinked)) {
      throw new UnsupportedOperationException("Internal object is not navigable");
    }

    return retrieveNavigationProperty(property, getter);
  }

  @Override
  public boolean isChanged() {
    return getEntityHandler() == null ? false : getEntityHandler().isChanged();
  }

  @Override
  protected void load() {
    try {
      if (this.uri != null) {
        final ODataPropertyRequest<CommonODataProperty> req =
                getClient().getRetrieveRequestFactory().getPropertyRequest(uri.build());

        final ODataRetrieveResponse<CommonODataProperty> res = req.execute();
        this.internal = res.getBody().getValue();
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Complex at '" + uri + "' not found", e);
      throw e;
    } catch (Exception e) {
      LOG.warn("Error retrieving complex '" + uri + "'", e);
      throw new IllegalArgumentException("Error retrieving " + typeRef.getSimpleName(), e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected <T extends CommonODataProperty> List<T> getInternalProperties() {
    final List<T> res = new ArrayList<T>();
    if (getComplex() != null) {
      for (CommonODataProperty property : getComplex()) {
        res.add((T) property);
      }
    }
    return res;
  }

  @Override
  protected CommonODataProperty getInternalProperty(String name) {
    return getComplex() == null ? null : getComplex().get(name);
  }
}
