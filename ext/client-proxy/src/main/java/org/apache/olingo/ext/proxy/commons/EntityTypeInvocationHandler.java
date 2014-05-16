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

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataMediaFormat;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

public class EntityTypeInvocationHandler extends AbstractTypeInvocationHandler {

  private static final long serialVersionUID = 2629912294765040037L;

  protected Map<String, Object> propertyChanges = new HashMap<String, Object>();

  protected Map<NavigationProperty, Object> linkChanges = new HashMap<NavigationProperty, Object>();

  protected int propertiesTag = 0;

  protected int linksTag = 0;

  private Map<String, InputStream> streamedPropertyChanges = new HashMap<String, InputStream>();

  private InputStream stream;

  private EntityUUID uuid;

  static EntityTypeInvocationHandler getInstance(
          final CommonODataEntity entity,
          final EntitySetInvocationHandler<?, ?, ?> entitySet,
          final Class<?> typeRef) {

    return getInstance(
            entity,
            entitySet.getEntitySetName(),
            typeRef,
            entitySet.containerHandler);
  }

  static EntityTypeInvocationHandler getInstance(
          final CommonODataEntity entity,
          final String entitySetName,
          final Class<?> typeRef,
          final EntityContainerInvocationHandler containerHandler) {

    return new EntityTypeInvocationHandler(entity, entitySetName, typeRef, containerHandler);
  }

  private EntityTypeInvocationHandler(
          final CommonODataEntity entity,
          final String entitySetName,
          final Class<?> typeRef,
          final EntityContainerInvocationHandler containerHandler) {

    super(typeRef, (ODataLinked) entity, containerHandler);

    this.internal = entity;
    getEntity().setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(
            containerHandler.getEntityContainerName(),
            entitySetName,
            typeRef,
            CoreUtils.getKey(getClient(), this, typeRef, entity));
  }

  public void setEntity(final CommonODataEntity entity) {
    this.internal = entity;
    getEntity().setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(
            getUUID().getContainerName(),
            getUUID().getEntitySetName(),
            getUUID().getType(),
            CoreUtils.getKey(getClient(), this, typeRef, entity));

    this.propertyChanges.clear();
    this.linkChanges.clear();
    this.streamedPropertyChanges.clear();
    this.propertiesTag = 0;
    this.linksTag = 0;
  }

  public EntityUUID getUUID() {
    return uuid;
  }

  public String getEntityContainerName() {
    return uuid.getContainerName();
  }

  public String getEntitySetName() {
    return uuid.getEntitySetName();
  }

  public final CommonODataEntity getEntity() {
    return (CommonODataEntity) internal;
  }

  /**
   * Gets the current ETag defined into the wrapped entity.
   *
   * @return
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

  public Map<String, Object> getPropertyChanges() {
    return propertyChanges;
  }

  public Map<NavigationProperty, Object> getLinkChanges() {
    return linkChanges;
  }

  private void updatePropertiesTag(final int checkpoint) {
    if (checkpoint == propertiesTag) {
      propertiesTag = propertyChanges.hashCode();
    }
  }

  private void updateLinksTag(final int checkpoint) {
    if (checkpoint == linksTag) {
      linksTag = linkChanges.hashCode();
    }
  }

  @Override
  protected Object getPropertyValue(final String name, final Type type) {
    try {
      final CommonODataProperty property = getEntity().getProperty(name);

      Object res;
      if (propertyChanges.containsKey(name)) {
        res = propertyChanges.get(name);
      } else {
        res = CoreUtils.getValueFromProperty(getClient(), property, type, this);
        if (res != null) {
          addPropertyChanges(name, res);
        }
      }

      return res;
    } catch (Exception e) {
      throw new IllegalArgumentException("Error getting value for property '" + name + "'", e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setPropertyValue(final Property property, final Object value) {
    if (property.type().equalsIgnoreCase(EdmPrimitiveTypeKind.Stream.toString())) {
      setStreamedProperty(property, (InputStream) value);
    } else {
      addPropertyChanges(property.name(), value);

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
            if ((handler instanceof ComplexTypeInvocationHandler)
                    && ((ComplexTypeInvocationHandler) handler).getEntityHandler() == null) {
              ((ComplexTypeInvocationHandler) handler).setEntityHandler(this);
            }
          }
        }
      }
    }

    attach(AttachedEntityStatus.CHANGED);
  }

  @Override
  public boolean isChanged() {
    return this.linkChanges.hashCode() != this.linksTag
            || this.propertyChanges.hashCode() != this.propertiesTag
            || this.stream != null
            || !this.streamedPropertyChanges.isEmpty();
  }

  public void setStream(final InputStream stream) {
    if (typeRef.getAnnotation(EntityType.class).hasStream()) {
      IOUtils.closeQuietly(this.stream);
      this.stream = stream;
      attach(AttachedEntityStatus.CHANGED);
    }
  }

  public InputStream getStreamChanges() {
    return this.stream;
  }

  public Map<String, InputStream> getStreamedPropertyChanges() {
    return streamedPropertyChanges;
  }

  public InputStream getStream() {

    final URI contentSource = getEntity().getMediaContentSource();

    if (this.stream == null
            && typeRef.getAnnotation(EntityType.class).hasStream()
            && contentSource != null) {

      final String contentType =
              StringUtils.isBlank(getEntity().getMediaContentType()) ? "*/*" : getEntity().getMediaContentType();

      final ODataMediaRequest retrieveReq = getClient().getRetrieveRequestFactory().getMediaRequest(contentSource);
      retrieveReq.setFormat(ODataMediaFormat.fromFormat(contentType));

      this.stream = retrieveReq.execute().getBody();
    }

    return this.stream;
  }

  public Object getStreamedProperty(final Property property) {

    InputStream res = streamedPropertyChanges.get(property.name());

    try {
      if (res == null) {
        final URI link = URIUtils.getURI(
                getClient().getServiceRoot(),
                CoreUtils.getMediaEditLink(property.name(), getEntity()).toASCIIString());

        final ODataMediaRequest req = getClient().getRetrieveRequestFactory().getMediaRequest(link);
        res = req.execute().getBody();

      }
    } catch (Exception e) {
      res = null;
    }

    return res;

  }

  private void setStreamedProperty(final Property property, final InputStream input) {
    final Object obj = propertyChanges.get(property.name());
    if (obj instanceof InputStream) {
      IOUtils.closeQuietly((InputStream) obj);
    }

    streamedPropertyChanges.put(property.name(), input);
  }

  @Override
  protected Object getNavigationPropertyValue(final NavigationProperty property, final Method getter) {
    final Object navPropValue;

    if (linkChanges.containsKey(property)) {
      navPropValue = linkChanges.get(property);
    } else {
      navPropValue = retriveNavigationProperty(property, getter);
    }

    if (navPropValue != null) {
      addLinkChanges(property, navPropValue);
    }

    return navPropValue;
  }

  @Override
  protected void addPropertyChanges(final String name, final Object value) {
    final int checkpoint = propertyChanges.hashCode();
    propertyChanges.put(name, value);
    updatePropertiesTag(checkpoint);
  }

  @Override
  protected void addLinkChanges(final NavigationProperty navProp, final Object value) {
    final int checkpoint = linkChanges.hashCode();
    linkChanges.put(navProp, value);
    updateLinksTag(checkpoint);
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
    return obj instanceof EntityTypeInvocationHandler
            && ((EntityTypeInvocationHandler) obj).getUUID().equals(uuid);
  }
}
