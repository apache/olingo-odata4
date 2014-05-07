/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.olingo.ext.proxy.commons;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;

public class EntityCollectionInvocationHandler<T extends Serializable, C extends CommonEdmEnabledODataClient<?>>
        extends AbstractInvocationHandler<C> implements AbstractEntityCollection<T> {

  private static final long serialVersionUID = 98078202642671726L;

  private final Collection<T> items;

  private final Class<?> itemRef;

  private final URI uri;

  public EntityCollectionInvocationHandler(final EntityContainerInvocationHandler<C> containerHandler,
          final Collection<T> items, final Class<?> itemRef, final String entityContainerName) {

    this(containerHandler, items, itemRef, entityContainerName, null);
  }

  public EntityCollectionInvocationHandler(final EntityContainerInvocationHandler<C> containerHandler,
          final Collection<T> items, final Class<?> itemRef, final String entityContainerName, final URI uri) {

    super(containerHandler.getClient(), containerHandler);

    this.items = items;
    this.itemRef = itemRef;
    this.uri = uri;
  }

  public Class<?> getEntityReference() {
    return itemRef;
  }

  public URI getURI() {
    return uri;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else {
      throw new UnsupportedOperationException("Method not found: " + method);
    }
  }

  @Override
  public int size() {
    return items.size();
  }

  @Override
  public boolean isEmpty() {
    return items.isEmpty();
  }

  @Override
  public boolean contains(final Object object) {
    return items.contains(object);
  }

  @Override
  public Iterator<T> iterator() {
    return items.iterator();
  }

  @Override
  public Object[] toArray() {
    return items.toArray();
  }

  @Override
  public <T> T[] toArray(final T[] array) {
    return items.toArray(array);
  }

  @Override
  public boolean add(final T element) {
    return items.add(element);
  }

  @Override
  public boolean remove(final Object object) {
    return items.remove(object);
  }

  @Override
  public boolean containsAll(final Collection<?> collection) {
    return items.containsAll(collection);
  }

  @Override
  public boolean addAll(final Collection<? extends T> collection) {
    return items.addAll(collection);
  }

  @Override
  public boolean removeAll(final Collection<?> collection) {
    return items.removeAll(collection);
  }

  @Override
  public boolean retainAll(final Collection<?> collection) {
    return items.retainAll(collection);
  }

  @Override
  public void clear() {
    items.clear();
  }
}
