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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.StructuredType;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Term;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.olingo.ext.proxy.Service;

public class EntityCollectionInvocationHandler<T extends StructuredType>
        extends AbstractEntityCollectionInvocationHandler<T, EntityCollection<T>>
        implements EntityCollection<T> {

  private static final long serialVersionUID = 98078202642671726L;

  protected URI nextPageURI = null;

  private Collection<T> items;

  private final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

  private final Map<Class<? extends AbstractTerm>, Object> annotationsByTerm =
          new HashMap<Class<? extends AbstractTerm>, Object>();

  public EntityCollectionInvocationHandler(
          final Service<?> service,
          final Collection<T> items,
          final Class<T> itemRef) {
    this(service, items, itemRef, null, null);
  }

  public EntityCollectionInvocationHandler(
          final Service<?> service,
          final Collection<T> items,
          final Class<T> itemRef,
          final URI targetEntitySetURI,
          final CommonURIBuilder<?> uri) {

    super(itemRef, null, service, targetEntitySetURI, uri);
    this.items = items;
  }

  public void setAnnotations(final List<ODataAnnotation> annotations) {
    this.annotations.clear();
    this.annotationsByTerm.clear();
    this.annotations.addAll(annotations);
  }

  public Class<?> getEntityReference() {
    return itemRef;
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

  public void nextPage() {
    if (!hasNextPage()) {
      throw new IllegalStateException("Next page URI not found");
    }
    this.uri = getClient().newURIBuilder(nextPageURI.toASCIIString());
  }

  public boolean hasNextPage() {
    return this.nextPageURI != null;
  }

  void setNextPage(final URI next) {
    this.nextPageURI = next;
  }

  @SuppressWarnings("unchecked")
  public EntityCollection<T> execute() {
    if (this.uri != null) {
      final Triple<List<T>, URI, List<ODataAnnotation>> entitySet = fetchPartialEntitySet(this.uri.build(), itemRef);
      this.nextPageURI = entitySet.getMiddle();

      if (items == null) {
        items = entitySet.getLeft();
      } else {
        items.clear();
        items.addAll(entitySet.getLeft());
      }

      annotations.clear();
      annotations.addAll(entitySet.getRight());
    }

    return this;
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
    final EntityInvocationHandler handler = EntityInvocationHandler.class.cast(Proxy.getInvocationHandler(element));
    if (!service.getContext().entityContext().isAttached(handler) && baseURI != null) {
      handler.updateUUID(baseURI, itemRef, null);
      service.getContext().entityContext().attachNew(handler);
    }
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

  public Object getAnnotation(final Class<? extends AbstractTerm> term) {
    Object res = null;

    if (annotationsByTerm.containsKey(term)) {
      res = annotationsByTerm.get(term);
    } else {
      try {
        final Term termAnn = term.getAnnotation(Term.class);
        final Namespace namespaceAnn = term.getAnnotation(Namespace.class);
        ODataAnnotation annotation = null;
        for (ODataAnnotation _annotation : annotations) {
          if ((namespaceAnn.value() + "." + termAnn.name()).equals(_annotation.getTerm())) {
            annotation = _annotation;
          }
        }
        res = annotation == null || annotation.hasNullValue()
                ? null
                : CoreUtils.getObjectFromODataValue(annotation.getValue(), null, service);
        if (res != null) {
          annotationsByTerm.put(term, res);
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Error getting annotation for term '" + term.getName() + "'", e);
      }
    }

    return res;
  }

  public Collection<Class<? extends AbstractTerm>> getAnnotationTerms() {
    return CoreUtils.getAnnotationTerms(annotations);
  }

  @Override
  public void clearQueryOptions() {
    super.clearQueryOptions();
    this.nextPageURI = null;
  }
}
