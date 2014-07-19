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

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.ext.proxy.api.StructuredType;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.ext.proxy.Service;

public abstract class AbstractEntityCollectionInvocationHandler<
        T extends StructuredType, EC extends EntityCollection<T>>
        extends AbstractInvocationHandler {

  private static final long serialVersionUID = 98078202642671727L;

  protected final Class<T> itemRef;

  protected final Class<EC> collItemRef;

  protected final URI baseURI;

  protected URI targetEntitySetURI;

  protected CommonURIBuilder<?> uri;

  private boolean isSingleton = false;

  @SuppressWarnings("unchecked")
  public AbstractEntityCollectionInvocationHandler(
          final Class<?> ref,
          final Service<?> service,
          final CommonURIBuilder<?> uri) {
    super(service);

    this.uri = uri;
    this.baseURI = uri.build();
    this.targetEntitySetURI = uri.build();
    this.isSingleton = AbstractSingleton.class.isAssignableFrom(ref);

    final Type[] entitySetParams =
            ClassUtils.extractGenericType(ref, AbstractEntitySet.class, AbstractSingleton.class);

    this.itemRef = (Class<T>) entitySetParams[0];
    this.collItemRef = (Class<EC>) entitySetParams[2];
  }

  @SuppressWarnings("unchecked")
  public AbstractEntityCollectionInvocationHandler(
          final Class<?> itemRef,
          final Class<EC> collItemRef,
          final Service<?> service,
          final URI targetEntitySetURI,
          final CommonURIBuilder<?> uri) {
    super(service);

    this.uri = uri;
    this.baseURI = uri == null ? null : uri.build();
    this.itemRef = (Class<T>) itemRef;
    this.collItemRef = collItemRef;
    this.targetEntitySetURI = targetEntitySetURI;
  }

  protected Class<T> getTypeRef() {
    return this.itemRef;
  }

  protected URI getURI() {
    return this.baseURI;
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends EntityCollection<S>> SEC fetchWholeEntitySet(
          final CommonURIBuilder<?> uriBuilder, final Class<S> typeRef, final Class<SEC> collTypeRef) {

    final List<S> items = new ArrayList<S>();
    final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

    URI nextURI = uriBuilder.build();
    while (nextURI != null) {
      final Triple<List<S>, URI, List<ODataAnnotation>> entitySet = fetchPartialEntitySet(nextURI, typeRef);
      items.addAll(entitySet.getLeft());
      nextURI = entitySet.getMiddle();
      annotations.addAll(entitySet.getRight());
    }

    final EntityCollectionInvocationHandler<S> entityCollectionHandler =
            new EntityCollectionInvocationHandler<S>(service, items, typeRef, targetEntitySetURI, uriBuilder);
    entityCollectionHandler.setAnnotations(annotations);

    return (SEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {collTypeRef},
            entityCollectionHandler);
  }

  @SuppressWarnings("unchecked")
  public <S extends T> Triple<List<S>, URI, List<ODataAnnotation>> fetchPartialEntitySet(
          final URI uri, final Class<S> typeRef) {

    final List<CommonODataEntity> entities = new ArrayList<CommonODataEntity>();
    final URI next;
    final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

    if (isSingleton) {
      final ODataRetrieveResponse<org.apache.olingo.commons.api.domain.v4.ODataSingleton> res =
              ((ODataClient) getClient()).getRetrieveRequestFactory().getSingletonRequest(uri).execute();

      entities.add(res.getBody());
      next = null;
    } else {
      final ODataEntitySetRequest<CommonODataEntitySet> req =
              getClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
      if (getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) > 0) {
        req.setPrefer(getClient().newPreferences().includeAnnotations("*"));
      }

      final ODataRetrieveResponse<CommonODataEntitySet> res = req.execute();

      final CommonODataEntitySet entitySet = res.getBody();
      entities.addAll(entitySet.getEntities());
      next = entitySet.getNext();
      if (entitySet instanceof ODataEntitySet) {
        annotations.addAll(((ODataEntitySet) entitySet).getAnnotations());
      }
    }

    final List<S> items = new ArrayList<S>(entities.size());

    for (CommonODataEntity entity : entities) {
      final EntityInvocationHandler handler =
              this instanceof EntitySetInvocationHandler
              ? EntityInvocationHandler.getInstance(
              entity,
              EntitySetInvocationHandler.class.cast(this),
              typeRef)
              : EntityInvocationHandler.getInstance(
              entity,
              targetEntitySetURI,
              typeRef,
              service);

      final EntityInvocationHandler handlerInTheContext = getContext().entityContext().getEntity(handler.getUUID());

      items.add((S) Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {typeRef},
              handlerInTheContext == null ? handler : handlerInTheContext));
    }

    return new ImmutableTriple<List<S>, URI, List<ODataAnnotation>>(items, next, annotations);
  }

  public void filter(final String filter) {
    this.uri.filter(filter);
  }

  public void filter(final URIFilter filter) {
    this.uri.filter(filter);
  }

  public void orderBy(final Sort... sort) {
    final StringBuilder builder = new StringBuilder();
    for (Sort sortClause : sort) {
      builder.append(sortClause.getKey()).append(' ').append(sortClause.getValue()).append(',');
    }
    builder.deleteCharAt(builder.length() - 1);

    this.uri.orderBy(builder.toString());
  }

  public void orderBy(final String orderBy) {
    this.uri.orderBy(orderBy);
  }

  public void top(final int top) throws IllegalArgumentException {
    this.uri.top(top);
  }

  public void skip(final int skip) throws IllegalArgumentException {
    this.uri.skip(skip);
  }

  public void expand(final String... expand) {
    this.uri.expand(expand);
  }

  public void select(final String... select) {
    this.uri.select(select);
  }

  public void clearQueryOptions() {
    this.uri = this.baseURI == null ? null : getClient().newURIBuilder(baseURI.toASCIIString());
  }
}
