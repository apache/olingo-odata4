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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataValueFormat;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.Filter;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKey;
import org.apache.olingo.ext.proxy.api.annotations.CompoundKeyElement;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EntitySetInvocationHandler<
        T extends Serializable, KEY extends Serializable, EC extends AbstractEntityCollection<T>>
        extends AbstractInvocationHandler
        implements AbstractEntitySet<T, KEY, EC> {

  private static final long serialVersionUID = 2629912294765040027L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(EntitySetInvocationHandler.class);

  private final boolean isSingleton;

  private final Class<T> typeRef;

  private final Class<EC> collTypeRef;

  private final URI uri;

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(
          final Class<?> ref, final EntityContainerInvocationHandler containerHandler, final String entitySetName) {

    final CommonURIBuilder<?> uriBuilder = containerHandler.getClient().
            getURIBuilder(containerHandler.getFactory().getServiceRoot());

    final StringBuilder entitySetSegment = new StringBuilder();
    if (!containerHandler.isDefaultEntityContainer()) {
      entitySetSegment.append(containerHandler.getEntityContainerName()).append('.');
    }
    entitySetSegment.append(entitySetName);

    uriBuilder.appendEntitySetSegment(entitySetSegment.toString());

    return new EntitySetInvocationHandler(ref, containerHandler, entitySetName, uriBuilder.build());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static EntitySetInvocationHandler getInstance(
          final Class<?> ref, final EntityContainerInvocationHandler containerHandler, final URI uri) {

    return new EntitySetInvocationHandler(ref, containerHandler, (ref.getAnnotation(EntitySet.class)).name(), uri);
  }

  @SuppressWarnings("unchecked")
  protected EntitySetInvocationHandler(
          final Class<?> ref,
          final EntityContainerInvocationHandler containerHandler,
          final String entitySetName,
          final URI uri) {

    super(containerHandler.getClient(), containerHandler);

    this.isSingleton = AbstractSingleton.class.isAssignableFrom(ref);

    final Type[] entitySetParams = ((ParameterizedType) ref.getGenericInterfaces()[0]).getActualTypeArguments();

    this.typeRef = (Class<T>) entitySetParams[0];
    this.collTypeRef = (Class<EC>) entitySetParams[2];

    this.uri = uri;
  }

  protected Class<T> getTypeRef() {
    return typeRef;
  }

  protected Class<EC> getCollTypeRef() {
    return collTypeRef;
  }

  protected URI getEntitySetURI() {
    return uri;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
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
    final CommonODataEntity entity = client.getObjectFactory().newEntity(
            new FullQualifiedName(containerHandler.getSchemaName(), ClassUtils.getEntityTypeName(reference)));

    final EntityInvocationHandler handler =
            EntityInvocationHandler.getInstance(null, entity, uri, reference, containerHandler);
    EntityContainerFactory.getContext().entityContext().attachNew(handler);

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
    final ODataValueRequest req = client.getRetrieveRequestFactory().
            getValueRequest(client.getURIBuilder(this.uri.toASCIIString()).count().build());
    req.setFormat(ODataValueFormat.TEXT);
    return Long.valueOf(req.execute().getBody().asPrimitive().toString());
  }

  @Override
  public Boolean exists(final KEY key) throws IllegalArgumentException {
    boolean result = false;

    try {
      result = get(key) != null;
    } catch (Exception e) {
      LOG.error("Could not check existence of {}({})", this.uri, key, e);
    }

    return result;
  }

  private Map<String, Object> getCompoundKey(final Object key) {
    final Set<CompoundKeyElementWrapper> elements = new TreeSet<CompoundKeyElementWrapper>();

    for (Method method : key.getClass().getMethods()) {
      final Annotation annotation = method.getAnnotation(CompoundKeyElement.class);
      if (annotation instanceof CompoundKeyElement) {
        elements.add(new CompoundKeyElementWrapper(
                ((CompoundKeyElement) annotation).name(), method, ((CompoundKeyElement) annotation).position()));
      }
    }

    final LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

    for (CompoundKeyElementWrapper element : elements) {
      try {
        map.put(element.getName(), element.getMethod().invoke(key));
      } catch (Exception e) {
        LOG.warn("Error retrieving compound key element '{}' value", element.getName(), e);
      }
    }

    return map;
  }

  @Override
  public T get(final KEY key) throws IllegalArgumentException {
    return get(key, typeRef);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends T> S get(final KEY key, final Class<S> typeRef) throws IllegalArgumentException {
    if (key == null) {
      throw new IllegalArgumentException("Null key");
    }

    final EntityUUID uuid = new EntityUUID(containerHandler.getEntityContainerName(), uri, typeRef, key);
    LOG.debug("Ask for '{}({})'", typeRef.getSimpleName(), key);

    EntityInvocationHandler handler = EntityContainerFactory.getContext().entityContext().getEntity(uuid);

    if (handler == null) {
      // not yet attached: search against the service
      try {
        LOG.debug("Search for '{}({})' into the service", typeRef.getSimpleName(), key);
        final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(this.uri.toASCIIString());

        if (key.getClass().getAnnotation(CompoundKey.class) == null) {
          LOG.debug("Append key segment '{}'", key);
          uriBuilder.appendKeySegment(key);
        } else {
          LOG.debug("Append compound key segment '{}'", key);
          uriBuilder.appendKeySegment(getCompoundKey(key));
        }

        LOG.debug("GET {}", uriBuilder.toString());

        final ODataRetrieveResponse<CommonODataEntity> res =
                client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build()).execute();

        final String etag = res.getETag();
        final CommonODataEntity entity = res.getBody();

        if (entity == null || !key.equals(CoreUtils.getKey(client, typeRef, entity))) {
          throw new IllegalArgumentException("Invalid " + typeRef.getSimpleName() + "(" + key + ")");
        }

        handler = EntityInvocationHandler.getInstance(uriBuilder.build(), entity, this, typeRef);
        handler.setETag(etag);
      } catch (Exception e) {
        LOG.info("Entity '" + uuid + "' not found", e);
      }
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
  public <S extends T> Map.Entry<List<S>, URI> fetchPartialEntitySet(final URI uri, final Class<S> typeRef) {
    final List<CommonODataEntity> entities = new ArrayList<CommonODataEntity>();
    final URI next;

    if (isSingleton) {
      final ODataRetrieveResponse<org.apache.olingo.commons.api.domain.v4.Singleton> res =
              ((ODataClient) client).getRetrieveRequestFactory().getSingletonRequest(uri).execute();

      entities.add(res.getBody());
      next = null;
    } else {
      final ODataRetrieveResponse<CommonODataEntitySet> res =
              client.getRetrieveRequestFactory().getEntitySetRequest(uri).execute();

      final CommonODataEntitySet entitySet = res.getBody();
      entities.addAll(entitySet.getEntities());
      next = entitySet.getNext();
    }

    final List<S> items = new ArrayList<S>(entities.size());

    for (CommonODataEntity entity : entities) {
      final EntityInvocationHandler handler =
              EntityInvocationHandler.getInstance(entity.getEditLink(), entity, this, typeRef);

      final EntityInvocationHandler handlerInTheContext =
              EntityContainerFactory.getContext().entityContext().getEntity(handler.getUUID());

      items.add((S) Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {typeRef},
              handlerInTheContext == null ? handler : handlerInTheContext));
    }

    return new AbstractMap.SimpleEntry<List<S>, URI>(items, next);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> SEC fetchWholeEntitySet(
          final URI entitySetURI, final Class<S> typeRef, final Class<SEC> collTypeRef) {

    final List<S> items = new ArrayList<S>();

    URI nextURI = entitySetURI;
    while (nextURI != null) {
      final Map.Entry<List<S>, URI> entitySet = fetchPartialEntitySet(nextURI, typeRef);
      nextURI = entitySet.getValue();
      items.addAll(entitySet.getKey());
    }

    return (SEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {collTypeRef},
            new EntityCollectionInvocationHandler<S>(containerHandler, items, typeRef, entitySetURI));
  }

  @Override
  public EC getAll() {
    return getAll(collTypeRef);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <S extends T, SEC extends AbstractEntityCollection<S>> SEC getAll(final Class<SEC> collTypeRef) {
    final Class<S> ref = (Class<S>) ClassUtils.extractTypeArg(collTypeRef);
    final Class<S> oref = (Class<S>) ClassUtils.extractTypeArg(this.collTypeRef);

    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(this.uri.toASCIIString());

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
  public Filter<T, EC> createFilter() {
    return new FilterImpl<T, EC>(this.client, this.collTypeRef, this.uri, this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> Filter<S, SEC> createFilter(
          final Class<SEC> reference) {

    return new FilterImpl<S, SEC>(
            this.client, reference, this.uri, (EntitySetInvocationHandler<S, ?, SEC>) this);
  }

  @Override
  public Search<T, EC> createSearch() {
    if (client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
      throw new UnsupportedInV3Exception();
    }
    return new SearchImpl<T, EC>((EdmEnabledODataClient) this.client, this.collTypeRef, this.uri, this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends AbstractEntityCollection<S>> Search<S, SEC> createSearch(
          final Class<SEC> reference) {

    if (client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0) {
      throw new UnsupportedInV3Exception();
    }
    return new SearchImpl<S, SEC>(
            (EdmEnabledODataClient) this.client, reference, this.uri, (EntitySetInvocationHandler<S, ?, SEC>) this);
  }

  @Override
  public void delete(final KEY key) throws IllegalArgumentException {
    final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

    EntityInvocationHandler entity = entityContext.getEntity(new EntityUUID(
            containerHandler.getEntityContainerName(),
            uri,
            typeRef,
            key));

    if (entity == null) {
      // search for entity
      final T searched = get(key);
      entity = (EntityInvocationHandler) Proxy.getInvocationHandler(searched);
      entityContext.attach(entity, AttachedEntityStatus.DELETED);
    } else {
      entityContext.setStatus(entity, AttachedEntityStatus.DELETED);
    }
  }

  @Override
  public <S extends T> void delete(final Iterable<S> entities) {
    final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

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
    return EntityContainerFactory.getContext().entityContext().getStatus(handler) == AttachedEntityStatus.DELETED;
  }

  @Override
  public EntitySetIterator<T, KEY, EC> iterator() {
    return new EntitySetIterator<T, KEY, EC>(client.getURIBuilder(this.uri.toASCIIString()).build(), this);
  }
}
