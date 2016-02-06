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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.AbstractSingleton;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.api.StructuredType;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public class EntitySetInvocationHandler<
        T extends EntityType<?>, KEY extends Serializable, EC extends EntityCollection<T, ?, ?>>
        extends AbstractEntityCollectionInvocationHandler<T, EC>
        implements AbstractEntitySet<T, KEY, EC> {

  private static final long serialVersionUID = 2629912294765040027L;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static EntitySetInvocationHandler getInstance(final Class<?> ref, final AbstractService<?> service) {
    return new EntitySetInvocationHandler(ref, service, buildEntitySetURI(ref, service));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static EntitySetInvocationHandler getInstance(
          final Class<?> ref, final AbstractService<?> service, final URI uri) {
    return new EntitySetInvocationHandler(ref, service, service.getClient().newURIBuilder(uri.toASCIIString()));
  }

  protected EntitySetInvocationHandler(
          final Class<?> ref,
          final AbstractService<?> service,
          final URIBuilder uri) {

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
    } else if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  @Override
  public Long count() {
    final ODataValueRequest req = getClient().getRetrieveRequestFactory().
            getValueRequest(getClient().newURIBuilder(this.uri.build().toASCIIString()).count().build());
    req.setFormat(ContentType.TEXT_PLAIN);
    return Long.valueOf(req.execute().getBody().asPrimitive().toString());
  }

  @Override
  public Boolean exists(final KEY key) throws IllegalArgumentException {
    try {
      StructuredType.class.cast(getByKey(key)).load();
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
      final ClientEntity entity = getClient().getObjectFactory().newEntity(new FullQualifiedName(
              typeRef.getAnnotation(Namespace.class).value(), ClassUtils.getEntityTypeName(typeRef)));

      handler = EntityInvocationHandler.getInstance(key, entity, this.baseURI, typeRef, service);
    }

    if (isDeleted(handler)) {
      // object deleted
      LOG.debug("Object '{}({})' has been deleted", typeRef.getSimpleName(), uuid);
      return null;
    } else {
      // clear query options
      handler.clearQueryOptions();
      return (S) Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {typeRef},
              handler);
    }
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
    final Class<S> oref = (Class<S>) ClassUtils.extractTypeArg(this.collItemRef,
            AbstractEntitySet.class, AbstractSingleton.class, EntityCollection.class);

    
    if (!oref.equals(ref)) {
      uri.appendDerivedEntityTypeSegment(new FullQualifiedName(
              ClassUtils.getNamespace(ref), ClassUtils.getEntityTypeName(ref)).toString());
    }

    final List<ClientAnnotation> anns = new ArrayList<ClientAnnotation>();

    final Triple<List<T>, URI, List<ClientAnnotation>> entitySet = fetchPartial(uri.build(), (Class<T>) ref);
    anns.addAll(entitySet.getRight());

    final EntityCollectionInvocationHandler<S> entityCollectionHandler = new EntityCollectionInvocationHandler<S>(
            service, (List<S>) entitySet.getLeft(), collTypeRef, this.baseURI, uri);
    entityCollectionHandler.setAnnotations(anns);

    entityCollectionHandler.nextPageURI = entitySet.getMiddle();

    return (SEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {collTypeRef},
            entityCollectionHandler);
  }

  @Override
  public Search<T, EC> createSearch() {
    return new SearchImpl<T, EC>(getClient(), this.collItemRef, this.baseURI, this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends EntityCollection<S, ?, ?>> Search<S, SEC> createSearch(final Class<SEC> reference) {
    return new SearchImpl<S, SEC>(
            getClient(),
            reference,
            baseURI,
            (EntitySetInvocationHandler<S, ?, SEC>) this);
  }

  @SuppressWarnings("unchecked")
  public <S extends T, SEC extends EntityCollection<S, ?, ?>> SEC fetchWholeEntitySet(
          final URIBuilder uriBuilder, final Class<S> typeRef, final Class<SEC> collTypeRef) {

    final List<S> res = new ArrayList<S>();
    final List<ClientAnnotation> anns = new ArrayList<ClientAnnotation>();

    URI nextURI = uriBuilder.build();
    while (nextURI != null) {
      final Triple<List<T>, URI, List<ClientAnnotation>> entitySet = fetchPartial(nextURI, (Class<T>) typeRef);
      res.addAll((List<S>) entitySet.getLeft());
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
    deleteEntity((EntityInvocationHandler) Proxy.getInvocationHandler(entity), this.baseURI);
  }

  @Override
  public <S extends T> void delete(final Iterable<S> entities) {
    for (S en : entities) {
      delete(en);
    }
  }

  @Override
  public EntitySetIterator<T, KEY, EC> iterator() {
    return new EntitySetIterator<T, KEY, EC>(getClient().newURIBuilder(this.uri.build().toASCIIString()).build(), this);
  }
}
