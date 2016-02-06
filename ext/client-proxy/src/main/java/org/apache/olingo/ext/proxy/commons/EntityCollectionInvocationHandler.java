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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;

public class EntityCollectionInvocationHandler<T extends EntityType<?>>
        extends AbstractEntityCollectionInvocationHandler<T, EntityCollection<T, ?, ?>> {

  public EntityCollectionInvocationHandler(
          final AbstractService<?> service, final Class<? extends EntityCollection<T, ?, ?>> collItemRef) {
    this(service, new ArrayList<T>(), collItemRef, null, null);
  }

  public <EC extends EntityCollection<T, ?, ?>> EntityCollectionInvocationHandler(
          final AbstractService<?> service,
          final Collection<T> items,
          final Class<? extends EntityCollection<T, ?, ?>> collItemRef,
          final URI targetEntitySetURI,
          final URIBuilder uri) {

    super(collItemRef, service, targetEntitySetURI, uri);
    this.items = items;
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
            || "refs".equals(method.getName())
            || "execute".equals(method.getName())) {
      invokeSelfMethod(method, args);
      return proxy;
    } else if (isSelfMethod(method)) {
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
}
