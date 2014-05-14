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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

public class ComplexTypeInvocationHandler extends AbstractTypeInvocationHandler {

  private static final long serialVersionUID = 2629912294765040037L;

  public static ComplexTypeInvocationHandler getInstance(
          final CommonEdmEnabledODataClient<?> client,
          final String propertyName,
          final Class<?> reference,
          final EntityTypeInvocationHandler handler) {

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
            client.getObjectFactory().newComplexValue(typeName.toString());

    return (ComplexTypeInvocationHandler) ComplexTypeInvocationHandler.getInstance(
            client, complex, complexTypeRef, handler);
  }

  public static ComplexTypeInvocationHandler getInstance(
          final CommonEdmEnabledODataClient<?> client,
          final ODataComplexValue<?> complex,
          final Class<?> typeRef,
          final EntityTypeInvocationHandler handler) {

    return new ComplexTypeInvocationHandler(client, complex, typeRef, handler);
  }

  public ComplexTypeInvocationHandler(
          final CommonEdmEnabledODataClient<?> client,
          final ODataComplexValue<?> complex,
          final Class<?> typeRef,
          final EntityTypeInvocationHandler handler) {

    super(client, typeRef, complex, handler);
  }

  public void setComplex(final ODataComplexValue<?> complex) {
    this.internal = complex;
  }

  @Override
  public FullQualifiedName getName() {
    return new FullQualifiedName(((ODataComplexValue<?>) this.internal).getTypeName());
  }

  @SuppressWarnings("unchecked")
  public ODataComplexValue<CommonODataProperty> getComplex() {
    return (ODataComplexValue<CommonODataProperty>) this.internal;
  }

  @Override
  protected Object getPropertyValue(final String name, final Type type) {
    try {
      return CoreUtils.getValueFromProperty(client, getComplex().get(name), type, targetHandler);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error getting value for property '" + name + "'", e);
    }
  }

  @Override
  public Collection<String> getAdditionalPropertyNames() {
    final Set<String> res = new HashSet<String>();
    final Set<String> propertyNames = new HashSet<String>();
    for (Method method : typeRef.getMethods()) {
      final Annotation ann = method.getAnnotation(Property.class);
      if (ann != null) {
        final String property = ((Property) ann).name();
        propertyNames.add(property);
      }
    }

    for (Iterator<? extends CommonODataProperty> itor = getComplex().iterator(); itor.hasNext();) {
      final CommonODataProperty property = itor.next();
      if (!propertyNames.contains(property.getName())) {
        res.add(property.getName());
      }
    }

    return res;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setPropertyValue(final Property property, final Object value) {
    final FullQualifiedName fqn =
            new FullQualifiedName(ClassUtils.getNamespace(typeRef), typeRef.getAnnotation(ComplexType.class).name());

    final EdmElement edmProperty = client.getCachedEdm().getComplexType(fqn).getProperty(property.name());

    final Object toBeAdded;

    if (value == null) {
      toBeAdded = null;
    } else if (Collection.class.isAssignableFrom(value.getClass())) {
      toBeAdded = new ArrayList<Object>();
      for (Object obj : (Collection) value) {
        ((Collection) toBeAdded).add(obj instanceof Proxy ? Proxy.getInvocationHandler(obj) : obj);
      }
    } else if (value instanceof Proxy) {
      toBeAdded = Proxy.getInvocationHandler(value);
    } else {
      toBeAdded = value;
    }

    final EdmTypeInfo type = new EdmTypeInfo.Builder().
            setEdm(client.getCachedEdm()).setTypeExpression(
                    edmProperty.isCollection() ? "Collection(" + property.type() + ")" : property.type()).build();

    client.getBinder().add(
            getComplex(), CoreUtils.getODataProperty(client, property.name(), type, toBeAdded));

    if (targetHandler != null && !entityContext.isAttached(targetHandler)) {
      entityContext.attach(targetHandler, AttachedEntityStatus.CHANGED);
    }
  }

  @Override
  protected Object getNavigationPropertyValue(final NavigationProperty property, final Method getter) {
    if (!(internal instanceof ODataLinked)) {
      throw new UnsupportedOperationException("Internal object is not navigable");
    }

    return retriveNavigationProperty(property, getter);
  }

  @Override
  protected void addPropertyChanges(final String name, final Object value) {
    // do nothing ....
  }

  @Override
  protected void addLinkChanges(final NavigationProperty navProp, final Object value) {
    // do nothing ....
  }

  @Override
  public boolean isChanged() {
    return targetHandler == null ? false : targetHandler.isChanged();
  }
}
