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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;

public class PrimitiveCollectionInvocationHandler<T extends Serializable>
        extends AbstractCollectionInvocationHandler<T, PrimitiveCollection<T>>
        implements PrimitiveCollection<T> {

  private static final long serialVersionUID = 98078202642671756L;

  public PrimitiveCollectionInvocationHandler(
          final Service<?> service,
          final Class<T> itemRef) {
    this(service, new ArrayList<T>(), itemRef, null);
  }

  public PrimitiveCollectionInvocationHandler(
          final Service<?> service,
          final Class<T> itemRef,
          final CommonURIBuilder<?> uri) {
    this(service, new ArrayList<T>(), itemRef, uri);
  }

  public PrimitiveCollectionInvocationHandler(
          final Service<?> service,
          final Collection<T> items,
          final Class<T> itemRef,
          final CommonURIBuilder<?> uri) {

    super(service, items, itemRef, uri);
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if ("filter".equals(method.getName())
            || "orderBy".equals(method.getName())
            || "top".equals(method.getName())
            || "skip".equals(method.getName())
            || "expand".equals(method.getName())
            || "select".equals(method.getName())
            || "nextPage".equals(method.getName())
            || "execute".equals(method.getName())) {
      invokeSelfMethod(method, args);
      return proxy;
    } else if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <S extends T> Triple<List<S>, URI, List<ODataAnnotation>> fetchPartial(final URI uri, final Class<S> typeRef) {
    final ODataPropertyRequest<ODataProperty> req = getClient().getRetrieveRequestFactory().getPropertyRequest(uri);
    if (getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) > 0) {
      req.setPrefer(getClient().newPreferences().includeAnnotations("*"));
    }

    final ODataRetrieveResponse<ODataProperty> res = req.execute();

    List<S> resItems = new ArrayList<S>();

    final ODataProperty property = res.getBody();
    if (property != null && !property.hasNullValue()) {
      for (ODataValue item : property.getCollectionValue()) {
        resItems.add((S) getComplex(property.getName(), item, typeRef, null, null, true));
      }
    }

    return new ImmutableTriple<List<S>, URI, List<ODataAnnotation>>(
            resItems, null, Collections.<ODataAnnotation>emptyList());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Proxy) {
      final InvocationHandler handler = Proxy.getInvocationHandler(obj);
      if (handler instanceof PrimitiveCollectionInvocationHandler) {
        return items.equals(PrimitiveCollectionInvocationHandler.class.cast(handler).items);
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    return items.hashCode();
  }
}
