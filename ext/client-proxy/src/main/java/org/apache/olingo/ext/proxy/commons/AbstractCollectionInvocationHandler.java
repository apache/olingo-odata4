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
import org.apache.olingo.client.api.uri.CommonURIBuilder;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Term;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

public abstract class AbstractCollectionInvocationHandler<T extends Serializable, EC extends Collection<T>>
        extends AbstractInvocationHandler implements Collection<T> {

  private static final long serialVersionUID = 98078202642671726L;

  protected URI nextPageURI = null;

  protected Collection<T> items;

  protected final URI baseURI;

  protected CommonURIBuilder<?> uri;

  protected final Class<T> itemRef;

  protected final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

  private final Map<Class<? extends AbstractTerm>, Object> annotationsByTerm =
          new HashMap<Class<? extends AbstractTerm>, Object>();

  public AbstractCollectionInvocationHandler(
          final Service<?> service,
          final Collection<T> items,
          final Class<T> itemRef,
          final CommonURIBuilder<?> uri) {

    super(service);

    this.itemRef = itemRef;
    this.items = items;
    this.uri = uri;
    this.baseURI = this.uri == null ? null : this.uri.build();
  }

  @SuppressWarnings("unchecked")
  public Collection<T> execute() {
    if (this.uri != null) {
      final Triple<List<T>, URI, List<ODataAnnotation>> res = fetchPartial(this.uri.build(), itemRef);
      this.nextPageURI = res.getMiddle();

      if (items == null) {
        items = res.getLeft();
      } else {
        items.clear();
        items.addAll(res.getLeft());
      }

      annotations.clear();
      annotations.addAll(res.getRight());
    }

    return this;
  }

  public abstract <S extends T> Triple<List<S>, URI, List<ODataAnnotation>> fetchPartial(
          final URI uri, final Class<S> typeRef);

  public void setAnnotations(final List<ODataAnnotation> annotations) {
    this.annotations.clear();
    this.annotationsByTerm.clear();
    this.annotations.addAll(annotations);
  }

  protected Class<T> getTypeRef() {
    return this.itemRef;
  }

  protected URI getURI() {
    return this.baseURI;
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

  @Override
  public boolean add(final T element) {
    if (element instanceof Proxy && Proxy.getInvocationHandler(element) instanceof EntityInvocationHandler) {
      final EntityInvocationHandler handler = EntityInvocationHandler.class.cast(Proxy.getInvocationHandler(element));
      if (!service.getContext().entityContext().isAttached(handler) && baseURI != null) {
        handler.updateUUID(baseURI, itemRef, null);
        service.getContext().entityContext().attachNew(handler);
      }
    }
    return items.add(element);
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

  public Collection<Class<? extends AbstractTerm>> getAnnotationTerms() {
    return CoreUtils.getAnnotationTerms(annotations);
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

  public void filter(final String filter) {
    if (this.uri != null) {
      this.uri.filter(filter);
    }
  }

  public void filter(final URIFilter filter) {
    if (this.uri != null) {
      this.uri.filter(filter);
    }
  }

  public void orderBy(final Sort... sort) {
    if (this.uri != null) {
      final StringBuilder builder = new StringBuilder();
      for (Sort sortClause : sort) {
        builder.append(sortClause.getKey()).append(' ').append(sortClause.getValue()).append(',');
      }
      builder.deleteCharAt(builder.length() - 1);

      this.uri.orderBy(builder.toString());
    }
  }

  public void orderBy(final String orderBy) {
    if (this.uri != null) {
      this.uri.orderBy(orderBy);
    }
  }

  public void top(final int top) throws IllegalArgumentException {
    if (this.uri != null) {
      this.uri.top(top);
    }
  }

  public void skip(final int skip) throws IllegalArgumentException {
    if (this.uri != null) {
      this.uri.skip(skip);
    }
  }

  public void expand(final String... expand) {
    if (this.uri != null) {
      this.uri.expand(expand);
    }
  }

  public void select(final String... select) {
    if (this.uri != null) {
      this.uri.select(select);
    }
  }

  public void clearQueryOptions() {
    this.uri = this.baseURI == null ? null : getClient().newURIBuilder(baseURI.toASCIIString());
    this.nextPageURI = null;
  }
}
