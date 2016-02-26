/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.proxy.commons;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractTerm;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Term;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

public class EntityInvocationHandler extends AbstractStructuredInvocationHandler implements Annotatable {

  private static final long serialVersionUID = 2629912294765040037L;

  private final Map<Class<? extends AbstractTerm>, Object> annotations =
      new HashMap<Class<? extends AbstractTerm>, Object>();

  private EdmStreamValue stream;

  private EntityUUID uuid;

  static EntityInvocationHandler getInstance(
      final ClientEntity entity,
      final EntitySetInvocationHandler<?, ?, ?> entitySet,
      final Class<?> typeRef) {

    return new EntityInvocationHandler(
        null,
        entity,
        entitySet.getURI(),
        typeRef,
        entitySet.service);
  }

  static EntityInvocationHandler getInstance(
      final Object key,
      final ClientEntity entity,
      final URI entitySetURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {

    return new EntityInvocationHandler(key, entity, entitySetURI, typeRef, service);
  }

  public static EntityInvocationHandler getInstance(
      final ClientEntity entity,
      final URI entitySetURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {

    return new EntityInvocationHandler(null, entity, entitySetURI, typeRef, service);
  }

  public static EntityInvocationHandler getInstance(
      final ClientEntity entity,
      final URI entitySetURI,
      final URI entityURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {

    return new EntityInvocationHandler(entity, entityURI, entitySetURI, typeRef, service);
  }

  public static EntityInvocationHandler getInstance(
      final URI entityURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {

    return new EntityInvocationHandler(entityURI, typeRef, service);
  }

  public static EntityInvocationHandler getInstance(
      final Class<?> typeRef,
      final AbstractService<?> service) {

    return new EntityInvocationHandler(typeRef, service);
  }

  private EntityInvocationHandler(
      final Class<?> typeRef,
      final AbstractService<?> service) {

    super(typeRef, service);

    final String name = typeRef.getAnnotation(org.apache.olingo.ext.proxy.api.annotations.EntityType.class).name();
    final String namespace = typeRef.getAnnotation(Namespace.class).value();

    this.internal = service.getClient().getObjectFactory().newEntity(new FullQualifiedName(namespace, name));
    ClientEntity.class.cast(this.internal).setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(null, typeRef, null);
  }

  private EntityInvocationHandler(
      final URI entityURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {

    super(typeRef, service);

    final String name = typeRef.getAnnotation(org.apache.olingo.ext.proxy.api.annotations.EntityType.class).name();
    final String namespace = typeRef.getAnnotation(Namespace.class).value();

    this.internal = service.getClient().getObjectFactory().newEntity(new FullQualifiedName(namespace, name));
    ClientEntity.class.cast(this.internal).setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.baseURI = entityURI;
    this.uri = entityURI == null ? null : getClient().newURIBuilder(baseURI.toASCIIString());

    this.uuid = new EntityUUID(null, typeRef, null);
  }

  private EntityInvocationHandler(
      final ClientEntity entity,
      final URI entitySetURI,
      final URI entityURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {
    super(typeRef, entity, service);

    if (entityURI != null) {
      this.baseURI = entityURI;
      this.uri = getClient().newURIBuilder(baseURI.toASCIIString());
    } else {
      this.baseURI = null;
      this.uri = null;
    }

    this.internal = entity;
    getEntity().setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(entitySetURI, typeRef, null);
  }

  private EntityInvocationHandler(
      final Object entityKey,
      final ClientEntity entity,
      final URI entitySetURI,
      final Class<?> typeRef,
      final AbstractService<?> service) {

    super(typeRef, entity, service);

    final Object key = entityKey == null ? CoreUtils.getKey(getClient(), this, typeRef, entity) : entityKey;

    if (entity.getEditLink() != null) {
      this.baseURI = entity.getEditLink();
      this.uri = getClient().newURIBuilder(baseURI.toASCIIString());
    } else if (key != null) {
      final URIBuilder uriBuilder =
          CoreUtils.buildEditLink(getClient(), entitySetURI.toASCIIString(), key);

      this.uri = uriBuilder;
      this.baseURI = this.uri.build();
      entity.setEditLink(this.baseURI);
    } else {
      this.baseURI = null;
      this.uri = null;
    }

    this.internal = entity;
    getEntity().setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(entitySetURI, typeRef, key);
  }

  public void setEntity(final ClientEntity entity) {
    this.internal = entity;
    getEntity().setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    final Object key = CoreUtils.getKey(getClient(), this, typeRef, entity);

    this.uuid = new EntityUUID(getUUID().getEntitySetURI(), getUUID().getType(), key);

    // fix for OLINGO-353
    if (this.uri == null) {
      final URIBuilder uriBuilder =
          entity.getEditLink() == null
              ? CoreUtils.buildEditLink(getClient(), getUUID().getEntitySetURI().toASCIIString(), key)
              : getClient().newURIBuilder(entity.getEditLink().toASCIIString());

      this.uri = uriBuilder;
      this.baseURI = this.uri == null ? null : this.uri.build();
    }

    this.streamedPropertyChanges.clear();
    this.streamedPropertyCache.clear();
    this.propertyChanges.clear();
    this.propertyCache.clear();
    this.linkChanges.clear();
    this.linkCache.clear();
    this.annotations.clear();
  }
  
  public EntityUUID getUUID() {
    return uuid;
  }

  public EntityUUID updateEntityUUID(final URI entitySetURI, final Class<?> type, final ClientEntity entity) {
    CoreUtils.addProperties(service.getClient(), this.getPropertyChanges(), entity);
    final Object key = CoreUtils.getKey(getClient(), this, this.getUUID().getType(), entity);
    return updateUUID(entitySetURI, type, key);
  }

  public EntityUUID updateUUID(final URI entitySetURI, final Class<?> type, final Object key) {
    this.uuid = new EntityUUID(entitySetURI, type, key);

    if (this.uri == null) {
      final URIBuilder uriBuilder =
          getEntity().getEditLink() == null
              ? CoreUtils.buildEditLink(getClient(), entitySetURI.toASCIIString(), key)
              : getClient().newURIBuilder(getEntity().getEditLink().toASCIIString());

      this.uri = uriBuilder;
      this.baseURI = this.uri == null ? null : this.uri.build();
    }

    return this.uuid;
  }

  public URI getEntitySetURI() {
    return uuid.getEntitySetURI();
  }

  public final ClientEntity getEntity() {
    return (ClientEntity) internal;
  }

  public URI getEntityURI() {
    return this.baseURI;
  }

  /**
   * Gets the current ETag defined into the wrapped entity.
   *
   * @return the current etag
   */
  public String getETag() {
    return getEntity().getETag();
  }

  /**
   * Overrides ETag value defined into the wrapped entity.
   *
   * @param eTag ETag.
   */
  public void setETag(final String eTag) {
    getEntity().setETag(eTag);
  }

  public Map<Class<? extends AbstractTerm>, Object> getAnnotations() {
    return annotations;
  }

  @Override
  public boolean isChanged() {
    return isChanged(true);
  }

  public boolean isChanged(final boolean considerStreamProperties) {
    return super.isChanged()
        || (considerStreamProperties && (stream != null
            || !streamedPropertyChanges.isEmpty()));
  }

  public void uploadStream(final EdmStreamValue stream) {
    if (typeRef.getAnnotation(EntityType.class).hasStream()) {
      if (this.stream != null) {
        this.stream.close();
      }
      this.stream = stream;
      attach(AttachedEntityStatus.CHANGED);
    }
  }

  public EdmStreamValue getStreamChanges() {
    return this.stream;
  }

  public EdmStreamValue loadStream() {
    final URI contentSource = getEntity().getMediaContentSource() == null
        ? getClient().newURIBuilder(baseURI.toASCIIString()).appendValueSegment().build()
        : getEntity().getMediaContentSource();

    if (this.stream == null
        && typeRef.getAnnotation(EntityType.class).hasStream()
        && contentSource != null) {

      final ODataMediaRequest retrieveReq =
          getClient().getRetrieveRequestFactory().getMediaEntityRequest(contentSource);

      if (StringUtils.isNotBlank(getEntity().getMediaContentType())) {
        retrieveReq.setFormat(ContentType.parse(getEntity().getMediaContentType()));
      }

      final ODataRetrieveResponse<InputStream> res = retrieveReq.execute();
      this.stream = EdmStreamValue.class.cast(Proxy.newProxyInstance(
          Thread.currentThread().getContextClassLoader(),
          new Class<?>[] { EdmStreamValue.class },
          new EdmStreamValueHandler(res.getContentType(), res.getBody(), contentSource, service)));
    }

    return this.stream;
  }

  @Override
  protected Object getNavigationPropertyValue(final NavigationProperty property, final Method getter) {
    final Object navPropValue;

    if (linkChanges.containsKey(property)) {
      navPropValue = linkChanges.get(property);
    } else if (linkCache.containsKey(property)) {
      navPropValue = linkCache.get(property);
    } else {
      navPropValue = retrieveNavigationProperty(property, getter);
    }

    if (navPropValue != null) {
      cacheLink(property, navPropValue);
      attach();
    }

    return navPropValue;
  }

  protected void cacheLink(final NavigationProperty navProp, final Object value) {
    linkCache.put(navProp, value);
  }

  @Override
  public void addAnnotation(final Class<? extends AbstractTerm> term, final Object value) {
    this.annotations.put(term, value);

    if (value != null) {
      Collection<?> coll;
      if (Collection.class.isAssignableFrom(value.getClass())) {
        coll = Collection.class.cast(value);
      } else {
        coll = Collections.singleton(value);
      }

      for (Object item : coll) {
        if (item instanceof Proxy) {
          final InvocationHandler handler = Proxy.getInvocationHandler(item);
          if ((handler instanceof ComplexInvocationHandler)
              && ((ComplexInvocationHandler) handler).getEntityHandler() == null) {
            ((ComplexInvocationHandler) handler).setEntityHandler(this);
          }
        }
      }
    }

    attach(AttachedEntityStatus.CHANGED);
  }

  @Override
  public void removeAnnotation(final Class<? extends AbstractTerm> term) {
    this.annotations.remove(term);
    attach(AttachedEntityStatus.CHANGED);
  }

  @Override
  public Object readAnnotation(final Class<? extends AbstractTerm> term) {
    Object res = null;

    if (annotations.containsKey(term)) {
      res = annotations.get(term);
    } else {
      try {
        final Term termAnn = term.getAnnotation(Term.class);
        final Namespace namespaceAnn = term.getAnnotation(Namespace.class);
        ClientAnnotation annotation = null;
        for (ClientAnnotation _annotation : getEntity().getAnnotations()) {
          if ((namespaceAnn.value() + "." + termAnn.name()).equals(_annotation.getTerm())) {
            annotation = _annotation;
          }
        }
        res = annotation == null || annotation.hasNullValue()
            ? null
            : CoreUtils.getObjectFromODataValue(annotation.getValue(), null, service);
        if (res != null) {
          annotations.put(term, res);
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Error getting annotation for term '" + term.getName() + "'", e);
      }
    }

    return res;
  }

  @Override
  public Collection<Class<? extends AbstractTerm>> readAnnotationTerms() {
    return CoreUtils.getAnnotationTerms(service, getEntity().getAnnotations());
  }

  @Override
  protected void load() {
    // Search against the service
    final Object key = uuid.getKey();

    try {
      final ODataEntityRequest<ClientEntity> req =
          getClient().getRetrieveRequestFactory().getEntityRequest(uri.build());

      req.setPrefer(getClient().newPreferences().includeAnnotations("*"));

      final ODataRetrieveResponse<ClientEntity> res = req.execute();

      final ClientEntity entity = res.getBody();
      if (entity == null) {
        throw new IllegalArgumentException("Invalid " + typeRef.getSimpleName() + "(" + key + ")");
      }

      setEntity(entity);
      setETag(res.getETag());

      if (key != null && !key.equals(CoreUtils.getKey(getClient(), this, typeRef, entity))) {
        throw new IllegalArgumentException("Invalid " + typeRef.getSimpleName() + "(" + key + ")");
      }

      if (this.stream != null) {
        this.stream.close();
        this.stream = null;
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("Entity '" + uuid + "' not found", e);
      throw e;
    } catch (Exception e) {
      LOG.warn("Error retrieving entity '" + uuid + "'", e);
      throw new IllegalArgumentException("Error retrieving " + typeRef.getSimpleName() + "(" + key + ")", e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected <T extends ClientProperty> List<T> getInternalProperties() {
    return getEntity() == null ? Collections.<T> emptyList() : (List<T>) getEntity().getProperties();
  }

  @Override
  protected ClientProperty getInternalProperty(final String name) {
    return getEntity() == null ? null : getEntity().getProperty(name);
  }

  // use read- instead of get- for .invoke() to distinguish it from entity property getter.
  public String readEntityReferenceID() {
    URI id = getEntity() == null ? null : getEntity().getId();

    return id == null ? null : id.toASCIIString();
  }

  @Override
  public String toString() {
    return uuid.toString();
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof EntityInvocationHandler
        && ((EntityInvocationHandler) obj).getUUID().equals(uuid);
  }
}
