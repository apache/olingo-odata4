/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.proxy.commons;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public class InlineEntitySetInvocationHandler<
    T extends EntityType<?>, KEY extends Serializable, EC extends EntityCollection<T, ?, ?>>
    extends AbstractEntityCollectionInvocationHandler<T, EC>
    implements AbstractEntitySet<T, KEY, EC> {

  private static final long serialVersionUID = 2629912294765040027L;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static InlineEntitySetInvocationHandler getInstance(final Class<?> ref, final AbstractService<?> service,
      final URI uri,
      final List<Object> items) {
    return new InlineEntitySetInvocationHandler(ref, service, service.getClient().newURIBuilder(uri.toASCIIString()),
        items);
  }

  protected InlineEntitySetInvocationHandler(
      final Class<?> ref,
      final AbstractService<?> service,
      final URIBuilder uri,
      final Collection<T> items) {

    super(ref, service, uri);

    this.items = items;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if ("filter".equals(method.getName())
        || "orderBy".equals(method.getName())
        || "top".equals(method.getName())
        || "skip".equals(method.getName())
        || "expand".equals(method.getName())
        || "select".equals(method.getName())) {

      invokeSelfMethod(method, args);
      return proxy;
    } else if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  @Override
  public Long count() {
    return Long.valueOf(size());
  }

  @Override
  public Boolean exists(final KEY key) throws IllegalArgumentException {
    throw new UnsupportedOperationException("exists not supported on inline entity set");
  }

  @Override
  public T getByKey(final KEY key) throws IllegalArgumentException {
    return getByKey(key, itemRef);
  }

  @Override
  public <S extends T> S getByKey(final KEY key, final Class<S> typeRef) throws IllegalArgumentException {
    throw new UnsupportedOperationException("getByKey not supported on inline entity set");
  }

  @Override
  public EC execute() {
    return execute(collItemRef);
  }

  public <S extends T, SEC extends EntityCollection<S, ?, ?>> Future<SEC> executeAsync(final Class<SEC> collTypeRef) {
    return service.getClient().getConfiguration().getExecutor().submit(new Callable<SEC>() {
      @Override
      public SEC call() throws Exception {
        return execute(collTypeRef);
      }
    });
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends EntityCollection<S, ?, ?>> SEC execute(final Class<SEC> collTypeRef) {
    final Class<S> ref = (Class<S>) ClassUtils.extractTypeArg(collTypeRef,
        AbstractEntitySet.class, AbstractSingleton.class, EntityCollection.class);
    final Class<S> oref = (Class<S>) ClassUtils.extractTypeArg(collItemRef,
        AbstractEntitySet.class, AbstractSingleton.class, EntityCollection.class);

    if (!oref.equals(ref)) {
      uri.appendDerivedEntityTypeSegment(new FullQualifiedName(
          ClassUtils.getNamespace(ref), ClassUtils.getEntityTypeName(ref)).toString());
    }

    final EntityCollectionInvocationHandler<S> entityCollectionHandler = new EntityCollectionInvocationHandler<S>(
        service, (Collection<S>) items, collTypeRef, baseURI, uri);

    return (SEC) Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class<?>[] { collTypeRef },
        entityCollectionHandler);
  }

  @Override
  public Search<T, EC> createSearch() {
    throw new UnsupportedOperationException("Search not supported on inline entity set");
  }

  @Override
  public <S extends T, SEC extends EntityCollection<S, ?, ?>> Search<S, SEC> createSearch(final Class<SEC> reference) {
    throw new UnsupportedOperationException("Search not supported on inline entity set");
  }

  @Override
  public void delete(final KEY key) throws IllegalArgumentException {
    throw new UnsupportedOperationException("Delete not supported on inline entity set");
  }

  @Override
  public <S extends T> void delete(final S entity) {
    throw new UnsupportedOperationException("Delete not supported on inline entity set");
  }

  @Override
  public <S extends T> void delete(final Iterable<S> entities) {
    throw new UnsupportedOperationException("Delete not supported on inline entity set");
  }
}
