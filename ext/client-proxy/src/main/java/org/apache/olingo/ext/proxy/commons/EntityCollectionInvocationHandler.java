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
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Term;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

public class EntityCollectionInvocationHandler<T extends Serializable>
        extends AbstractInvocationHandler implements AbstractEntityCollection<T> {

  private static final long serialVersionUID = 98078202642671726L;

  private final Collection<T> items;

  private final Class<?> itemRef;

  private final URI uri;

  private final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

  private final Map<Class<? extends AbstractTerm>, Object> annotationsByTerm =
          new HashMap<Class<? extends AbstractTerm>, Object>();

  public EntityCollectionInvocationHandler(final EntityContainerInvocationHandler containerHandler,
          final Collection<T> items, final Class<?> itemRef) {

    this(containerHandler, items, itemRef, null);
  }

  public EntityCollectionInvocationHandler(final EntityContainerInvocationHandler containerHandler,
          final Collection<T> items, final Class<?> itemRef, final URI uri) {

    super(containerHandler.getClient(), containerHandler);

    this.items = items;
    this.itemRef = itemRef;
    this.uri = uri;
  }

  public void setAnnotations(final List<ODataAnnotation> annotations) {
    this.annotations.clear();
    this.annotationsByTerm.clear();
    this.annotations.addAll(annotations);
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
      throw new NoSuchMethodException(method.getName());
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
                : CoreUtils.getObjectFromODataValue(client, annotation.getValue(), null, null);
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
}
