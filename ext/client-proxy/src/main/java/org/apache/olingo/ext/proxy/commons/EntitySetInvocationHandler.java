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
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.SingleQuery;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.ext.proxy.api.StructuredType;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EntitySetInvocationHandler<
        T extends StructuredType, KEY extends Serializable, EC extends AbstractEntityCollection<T>>
        extends AbstractInvocationHandler
        implements AbstractEntitySet<T, KEY, EC> {

  private static final long serialVersionUID = 2629912294765040027L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntitySetInvocationHandler.class);

  private final boolean isSingleton;

  private Class<T> typeRef = null;

  private Class<EC> collTypeRef = null;

  private final URI baseURI;

  private CommonURIBuilder<?> uri;

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(
          final Class<?> ref, final EntityContainerInvocationHandler containerHandler, final String entitySetName) {

    final CommonURIBuilder<?> uriBuilder = containerHandler.getClient().newURIBuilder();

    final StringBuilder entitySetSegment = new StringBuilder();
    if (!containerHandler.isDefaultEntityContainer()) {
      entitySetSegment.append(containerHandler.getEntityContainerName()).append('.');
    }
    entitySetSegment.append(entitySetName);

    uriBuilder.appendEntitySetSegment(entitySetSegment.toString());

    return new EntitySetInvocationHandler(ref, containerHandler, entitySetName, uriBuilder);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(
          final Class<?> ref, final EntityContainerInvocationHandler containerHandler, final URI uri) {;

    return new EntitySetInvocationHandler(ref, containerHandler, (ref.getAnnotation(EntitySet.class)).name(),
            containerHandler.getClient().newURIBuilder(uri.toASCIIString()));
  }

  @SuppressWarnings("unchecked")
  protected EntitySetInvocationHandler(
          final Class<?> ref,
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName,
          final CommonURIBuilder<?> uri) {

    super(containerHandler);

    this.uri = uri;
    this.baseURI = uri.build();

    this.isSingleton = AbstractSingleton.class.isAssignableFrom(ref);

    final Type[] entitySetParams = ClassUtils.extractGenericType(ref, AbstractEntitySet.class, AbstractSingleton.class);
    this.typeRef = (Class<T>) entitySetParams[0];
    this.collTypeRef = (Class<EC>) entitySetParams[2];
  }

  protected Class<T> getTypeRef() {
    return typeRef;
  }

  protected Class<EC> getCollTypeRef() {
    return collTypeRef;
  }

  protected URI getEntitySetURI() {
    return this.baseURI;
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
    } else if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if (method.getName().startsWith("new") && ArrayUtils.isEmpty(args)) {
      if (method.getName().endsWith("Collection")) {
        return newEntityCollection(method.getReturnType());
      } else {
        return newEntity(method.getReturnType());
      }
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  @SuppressWarnings("unchecked")
  private <NE> NE newEntity(final Class<NE> reference) {
    final CommonODataEntity entity = getClient().getObjectFactory().newEntity(
            new FullQualifiedName(containerHandler.getSchemaName(), ClassUtils.getEntityTypeName(reference)));

    final EntityInvocationHandler handler =
            EntityInvocationHandler.getInstance(entity, this.baseURI, reference, containerHandler);
    getContext().entityContext().attachNew(handler);

    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {reference},
            handler);
  }

  @SuppressWarnings("unchecked")
  private <NEC> NEC newEntityCollection(final Class<NEC> reference) {
    return (NEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {reference},
            new EntityCollectionInvocationHandler<T>(containerHandler, new ArrayList<T>(), typeRef));
  }

  @Override
  public Long count() {
    final ODataValueRequest req = getClient().getRetrieveRequestFactory().
            getValueRequest(getClient().newURIBuilder(this.uri.build().toASCIIString()).count().build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    return Long.valueOf(req.execute().getBody().asPrimitive().toString());
  }

  @Override
  public Boolean exists(final KEY key) throws IllegalArgumentException {
    try {
      SingleQuery.class.cast(getByKey(key)).load();
      return true;
    } catch (Exception e) {
      LOG.error("Could not check existence of {}({})", this.uri, key, e);
      return false;
    }
  }

  @Override
  public T getByKey(final KEY key) throws IllegalArgumentException {
    return getByKey(key, typeRef);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends T> S getByKey(final KEY key, final Class<S> typeRef) throws IllegalArgumentException {
    if (key == null) {
      throw new IllegalArgumentException("Null key");
    }

    final EntityUUID uuid = new EntityUUID(containerHandler.getEntityContainerName(), this.baseURI, typeRef, key);
    LOG.debug("Ask for '{}({})'", typeRef.getSimpleName(), key);

    EntityInvocationHandler handler = getContext().entityContext().getEntity(uuid);

    if (handler == null) {
      final CommonODataEntity entity = getClient().getObjectFactory().newEntity(
              new FullQualifiedName(containerHandler.getSchemaName(), ClassUtils.getEntityTypeName(typeRef)));

      handler = EntityInvocationHandler.getInstance(key, entity, this.baseURI, typeRef, containerHandler);

    } else if (isDeleted(handler)) {
      // object deleted
      LOG.debug("Object '{}({})' has been deleted", typeRef.getSimpleName(), uuid);
      handler = null;
    }

    return handler == null ? null : (S) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {typeRef},
            handler);
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
      final EntityInvocationHandler handler = EntityInvocationHandler.getInstance(entity, this, typeRef);

      final EntityInvocationHandler handlerInTheContext = getContext().entityContext().getEntity(handler.getUUID());

      items.add((S) Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {typeRef},
              handlerInTheContext == null ? handler : handlerInTheContext));
    }

    return new ImmutableTriple<List<S>, URI, List<ODataAnnotation>>(items, next, annotations);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> SEC fetchWholeEntitySet(
          final URI entitySetURI, final Class<S> typeRef, final Class<SEC> collTypeRef) {

    final List<S> items = new ArrayList<S>();
    final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

    URI nextURI = entitySetURI;
    while (nextURI != null) {
      final Triple<List<S>, URI, List<ODataAnnotation>> entitySet = fetchPartialEntitySet(nextURI, typeRef);
      items.addAll(entitySet.getLeft());
      nextURI = entitySet.getMiddle();
      annotations.addAll(entitySet.getRight());
    }

    final EntityCollectionInvocationHandler<S> entityCollectionHandler =
            new EntityCollectionInvocationHandler<S>(containerHandler, items, typeRef, entitySetURI);
    entityCollectionHandler.setAnnotations(annotations);

    return (SEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {collTypeRef},
            entityCollectionHandler);
  }

  public EC execute() {
    return execute(collTypeRef);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> SEC execute(final Class<SEC> collTypeRef) {
    final Class<S> ref = (Class<S>) ClassUtils.extractTypeArg(collTypeRef,
            AbstractEntitySet.class, AbstractSingleton.class, AbstractEntityCollection.class);
    final Class<S> oref = (Class<S>) ClassUtils.extractTypeArg(this.collTypeRef);

    final CommonURIBuilder<?> uriBuilder = getClient().newURIBuilder(this.uri.build().toASCIIString());

    final URI entitySetURI;
    if (oref.equals(ref)) {
      entitySetURI = uriBuilder.build();
    } else {
      entitySetURI = uriBuilder.appendDerivedEntityTypeSegment(new FullQualifiedName(
              ClassUtils.getNamespace(ref), ClassUtils.getEntityTypeName(ref)).toString()).build();
    }

    return fetchWholeEntitySet(entitySetURI, ref, collTypeRef);
  }

  @Override
  public Search<T, EC> createSearch() {
    if (getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
      throw new UnsupportedInV3Exception();
    }
    return new SearchImpl<T, EC>((EdmEnabledODataClient) getClient(), this.collTypeRef, this.baseURI, this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> Search<S, SEC> createSearch(
          final Class<SEC> reference) {

    if (getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
      throw new UnsupportedInV3Exception();
    }
    return new SearchImpl<S, SEC>(
            (EdmEnabledODataClient) getClient(),
            reference,
            baseURI,
            (EntitySetInvocationHandler<S, ?, SEC>) this);
  }

  @Override
  public void delete(final KEY key) throws IllegalArgumentException {
    final EntityContext entityContext = getContext().entityContext();

    EntityInvocationHandler entity = entityContext.getEntity(new EntityUUID(
            containerHandler.getEntityContainerName(),
            baseURI,
            typeRef,
            key));

    if (entity == null) {
      // search for entity
      final T searched = getByKey(key);
      entity = (EntityInvocationHandler) Proxy.getInvocationHandler(searched);
      entityContext.attach(entity, AttachedEntityStatus.DELETED);
    } else {
      entityContext.setStatus(entity, AttachedEntityStatus.DELETED);
    }
  }

  @Override
  public <S extends T> void delete(final Iterable<S> entities) {
    final EntityContext entityContext = getContext().entityContext();

    for (T en : entities) {
      final EntityInvocationHandler entity = (EntityInvocationHandler) Proxy.getInvocationHandler(en);
      if (entityContext.isAttached(entity)) {
        entityContext.setStatus(entity, AttachedEntityStatus.DELETED);
      } else {
        entityContext.attach(entity, AttachedEntityStatus.DELETED);
      }
    }
  }

  private boolean isDeleted(final EntityInvocationHandler handler) {
    return getContext().entityContext().getStatus(handler) == AttachedEntityStatus.DELETED;
  }

  @Override
  public EntitySetIterator<T, KEY, EC> iterator() {
    return new EntitySetIterator<T, KEY, EC>(getClient().newURIBuilder(this.uri.build().toASCIIString()).build(), this);
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

  public void clear() {
    this.uri = getClient().newURIBuilder(baseURI.toASCIIString());
  }
}
