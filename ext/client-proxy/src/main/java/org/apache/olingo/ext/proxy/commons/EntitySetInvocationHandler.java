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
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.SingleQuery;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Singleton;

class EntitySetInvocationHandler<
        T extends EntityType, KEY extends Serializable, EC extends EntityCollection<T>>
        extends AbstractEntityCollectionInvocationHandler<T, EC>
        implements AbstractEntitySet<T, KEY, EC> {

  private static final long serialVersionUID = 2629912294765040027L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntitySetInvocationHandler.class);

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(final Class<?> ref, final Service<?> service) {
    return new EntitySetInvocationHandler(ref, service, buildURI(ref, service));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(final Class<?> ref, final Service<?> service, final URI uri) {
    return new EntitySetInvocationHandler(ref, service, service.getClient().newURIBuilder(uri.toASCIIString()));
  }

  private static CommonURIBuilder<?> buildURI(
          final Class<?> ref,
          final Service<?> service) {
    final CommonURIBuilder<?> uriBuilder = service.getClient().newURIBuilder();

    final Edm edm = service.getClient().getCachedEdm();
    final String containerNS;
    final String entitySetName;
    Annotation ann = ref.getAnnotation(EntitySet.class);
    if (ann instanceof EntitySet) {
      containerNS = EntitySet.class.cast(ann).container();
      entitySetName = EntitySet.class.cast(ann).name();
    } else {
      ann = ref.getAnnotation(Singleton.class);
      if (ann instanceof Singleton) {
        containerNS = Singleton.class.cast(ann).container();
        entitySetName = Singleton.class.cast(ann).name();
      } else {
        containerNS = null;
        entitySetName = null;
      }
    }

    final StringBuilder entitySetSegment = new StringBuilder();
    if (StringUtils.isNotBlank(containerNS)) {
      final EdmEntityContainer container = edm.getEntityContainer(new FullQualifiedName(containerNS));
      if (!container.isDefault()) {
        entitySetSegment.append(container.getFullQualifiedName().toString()).append('.');
      }
    }

    entitySetSegment.append(entitySetName);
    uriBuilder.appendEntitySetSegment(entitySetSegment.toString());
    return uriBuilder;
  }

  @SuppressWarnings("unchecked")
  protected EntitySetInvocationHandler(
          final Class<?> ref,
          final Service<?> service,
          final CommonURIBuilder<?> uri) {

    super(ref, service, uri);
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
    } else {
      throw new NoSuchMethodException(method.getName());
    }
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

    final EntityUUID uuid = new EntityUUID(this.baseURI, typeRef, key);
    LOG.debug("Ask for '{}({})'", typeRef.getSimpleName(), key);

    EntityInvocationHandler handler = getContext().entityContext().getEntity(uuid);

    if (handler == null) {
      final CommonODataEntity entity = getClient().getObjectFactory().newEntity(new FullQualifiedName(
              typeRef.getAnnotation(Namespace.class).value(), ClassUtils.getEntityTypeName(typeRef)));

      handler = EntityInvocationHandler.getInstance(key, entity, this.baseURI, typeRef, service);
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

  @Override
  public EC execute() {
    return execute(collItemRef);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends EntityCollection<S>> SEC execute(final Class<SEC> collTypeRef) {
    final Class<S> ref = (Class<S>) ClassUtils.extractTypeArg(collTypeRef,
            AbstractEntitySet.class, AbstractSingleton.class, EntityCollection.class);
    final Class<S> oref = (Class<S>) ClassUtils.extractTypeArg(this.collItemRef,
            AbstractEntitySet.class, AbstractSingleton.class, EntityCollection.class);

    final CommonURIBuilder<?> uriBuilder = getClient().newURIBuilder(this.uri.build().toASCIIString());

    if (!oref.equals(ref)) {
      uriBuilder.appendDerivedEntityTypeSegment(new FullQualifiedName(
              ClassUtils.getNamespace(ref), ClassUtils.getEntityTypeName(ref)).toString());
    }

    final List<ODataAnnotation> anns = new ArrayList<ODataAnnotation>();

    final Triple<List<S>, URI, List<ODataAnnotation>> entitySet = fetchPartial(uriBuilder.build(), ref);
    anns.addAll(entitySet.getRight());

    final EntityCollectionInvocationHandler<S> entityCollectionHandler = new EntityCollectionInvocationHandler<S>(
            service, entitySet.getLeft(), collTypeRef, this.baseURI, uriBuilder);
    entityCollectionHandler.setAnnotations(anns);

    entityCollectionHandler.nextPageURI = entitySet.getMiddle();

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
  public <S extends T, SEC extends EntityCollection<S>> Search<S, SEC> createSearch(final Class<SEC> reference) {

    if (getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
      throw new UnsupportedInV3Exception();
    }
    return new SearchImpl<S, SEC>(
            (EdmEnabledODataClient) getClient(),
            reference,
            baseURI,
            (EntitySetInvocationHandler<S, ?, SEC>) this);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends EntityCollection<S>> SEC fetchWholeEntitySet(
          final CommonURIBuilder<?> uriBuilder, final Class<S> typeRef, final Class<SEC> collTypeRef) {

    final List<S> res = new ArrayList<S>();
    final List<ODataAnnotation> anns = new ArrayList<ODataAnnotation>();

    URI nextURI = uriBuilder.build();
    while (nextURI != null) {
      final Triple<List<S>, URI, List<ODataAnnotation>> entitySet = fetchPartial(nextURI, typeRef);
      res.addAll(entitySet.getLeft());
      nextURI = entitySet.getMiddle();
      anns.addAll(entitySet.getRight());
    }

    final EntityCollectionInvocationHandler<S> entityCollectionHandler =
            new EntityCollectionInvocationHandler<S>(service, res, collTypeRef, targetEntitySetURI, uriBuilder);
    entityCollectionHandler.setAnnotations(anns);

    return (SEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {collTypeRef},
            entityCollectionHandler);
  }

  @Override
  public void delete(final KEY key) throws IllegalArgumentException {
    final EntityContext entityContext = getContext().entityContext();

    EntityInvocationHandler entity = entityContext.getEntity(new EntityUUID(baseURI, itemRef, key));

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
