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
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.SingleQuery;
import org.apache.olingo.ext.proxy.api.StructuredType;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class EntitySetInvocationHandler<
        T extends StructuredType, KEY extends Serializable, EC extends AbstractEntityCollection<T>>
        extends AbstractEntityCollectionInvocationHandler<T, EC>
        implements AbstractEntitySet<T, KEY, EC> {

  private static final long serialVersionUID = 2629912294765040027L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntitySetInvocationHandler.class);

  @SuppressWarnings("unchecked")
  static EntitySetInvocationHandler getInstance(
          final Class<?> itemRef,
          final Class<?> collItemRef,
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName) {

    final CommonURIBuilder<?> uriBuilder = buildURI(containerHandler, entitySetName);

    uriBuilder.appendDerivedEntityTypeSegment(new FullQualifiedName(
            ClassUtils.getNamespace(itemRef), ClassUtils.getEntityTypeName(itemRef)).toString());

    return new EntitySetInvocationHandler(itemRef, collItemRef, containerHandler, entitySetName, uriBuilder);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(
          final Class<?> ref,
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName) {

    return new EntitySetInvocationHandler(
            ref, containerHandler, entitySetName, buildURI(containerHandler, entitySetName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(
          final Class<?> ref, final EntityContainerInvocationHandler containerHandler, final URI uri) {;

    return new EntitySetInvocationHandler(ref, containerHandler, (ref.getAnnotation(EntitySet.class)).name(),
            containerHandler.getClient().newURIBuilder(uri.toASCIIString()));
  }

  private static CommonURIBuilder<?> buildURI(
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName) {
    final CommonURIBuilder<?> uriBuilder = containerHandler.getClient().newURIBuilder();

    final StringBuilder entitySetSegment = new StringBuilder();
    if (!containerHandler.isDefaultEntityContainer()) {
      entitySetSegment.append(containerHandler.getEntityContainerName()).append('.');
    }
    entitySetSegment.append(entitySetName);

    uriBuilder.appendEntitySetSegment(entitySetSegment.toString());

    return uriBuilder;
  }

  @SuppressWarnings("unchecked")
  protected EntitySetInvocationHandler(
          final Class<?> ref,
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName,
          final CommonURIBuilder<?> uri) {

    super(ref, containerHandler, uri);
  }

  @SuppressWarnings("unchecked")
  protected EntitySetInvocationHandler(
          final Class<?> itemRef,
          final Class<EC> collItemRef,
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName,
          final CommonURIBuilder<?> uri) {

    super(itemRef, collItemRef, containerHandler, uri);
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
            new EntityCollectionInvocationHandler<T>(containerHandler, new ArrayList<T>(), itemRef));
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
    return getByKey(key, itemRef);
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

  public EC execute() {
    return execute(collItemRef);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> SEC execute(final Class<SEC> collTypeRef) {
    final Class<S> ref = (Class<S>) ClassUtils.extractTypeArg(collTypeRef,
            AbstractEntitySet.class, AbstractSingleton.class, AbstractEntityCollection.class);
    final Class<S> oref = (Class<S>) ClassUtils.extractTypeArg(this.collItemRef,
            AbstractEntitySet.class, AbstractSingleton.class, AbstractEntityCollection.class);

    final CommonURIBuilder<?> uriBuilder = getClient().newURIBuilder(this.uri.build().toASCIIString());

    if (!oref.equals(ref)) {
      uriBuilder.appendDerivedEntityTypeSegment(new FullQualifiedName(
              ClassUtils.getNamespace(ref), ClassUtils.getEntityTypeName(ref)).toString());
    }

    final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();

    final Triple<List<S>, URI, List<ODataAnnotation>> entitySet = fetchPartialEntitySet(uriBuilder.build(), ref);
    annotations.addAll(entitySet.getRight());

    final EntityCollectionInvocationHandler<S> entityCollectionHandler =
            new EntityCollectionInvocationHandler<S>(containerHandler, entitySet.getLeft(), ref, uriBuilder);
    entityCollectionHandler.setAnnotations(annotations);

    entityCollectionHandler.setNextPage(entitySet.getMiddle());

    return (SEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {collTypeRef},
            entityCollectionHandler);
  }

  @Override
  public Search<T, EC> createSearch() {
    if (getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
      throw new UnsupportedInV3Exception();
    }
    return new SearchImpl<T, EC>((EdmEnabledODataClient) getClient(), this.collItemRef, this.baseURI, this);
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
            itemRef,
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
  public <S extends T> void delete(final S entity) {
    final EntityContext entityContext = getContext().entityContext();

    final EntityInvocationHandler handler = (EntityInvocationHandler) Proxy.getInvocationHandler(entity);
    if (entityContext.isAttached(handler)) {
      entityContext.setStatus(handler, AttachedEntityStatus.DELETED);
    } else {
      entityContext.attach(handler, AttachedEntityStatus.DELETED);
    }
  }

  @Override
  public <S extends T> void delete(final Iterable<S> entities) {
    for (S en : entities) {
      delete(en);
    }
  }

  private boolean isDeleted(final EntityInvocationHandler handler) {
    return getContext().entityContext().getStatus(handler) == AttachedEntityStatus.DELETED;
  }

  @Override
  public EntitySetIterator<T, KEY, EC> iterator() {
    return new EntitySetIterator<T, KEY, EC>(getClient().newURIBuilder(this.uri.build().toASCIIString()).build(), this);
  }
}
