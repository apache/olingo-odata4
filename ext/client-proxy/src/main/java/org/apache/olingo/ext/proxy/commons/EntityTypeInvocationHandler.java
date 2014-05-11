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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataMediaFormat;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

public class EntityTypeInvocationHandler<C extends CommonEdmEnabledODataClient<?>>
        extends AbstractTypeInvocationHandler<C> {

  private static final long serialVersionUID = 2629912294765040037L;

  private CommonODataEntity entity;

  protected Map<String, Object> propertyChanges = new HashMap<String, Object>();

  protected Map<NavigationProperty, Object> linkChanges = new HashMap<NavigationProperty, Object>();

  protected int propertiesTag = 0;

  protected int linksTag = 0;

  private Map<String, InputStream> streamedPropertyChanges = new HashMap<String, InputStream>();

  private InputStream stream;

  private EntityUUID uuid;

  static EntityTypeInvocationHandler<?> getInstance(
          final CommonODataEntity entity,
          final EntitySetInvocationHandler<?, ?, ?, ?> entitySet,
          final Class<?> typeRef) {

    return getInstance(
            entity,
            entitySet.getEntitySetName(),
            typeRef,
            entitySet.containerHandler);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  static EntityTypeInvocationHandler<?> getInstance(
          final CommonODataEntity entity,
          final String entitySetName,
          final Class<?> typeRef,
          final EntityContainerInvocationHandler<?> containerHandler) {

    return new EntityTypeInvocationHandler(
            entity, entitySetName, typeRef, containerHandler);
  }

  private EntityTypeInvocationHandler(
          final CommonODataEntity entity,
          final String entitySetName,
          final Class<?> typeRef,
          final EntityContainerInvocationHandler<C> containerHandler) {

    super(containerHandler.getClient(), typeRef, (ODataLinked) entity, containerHandler);

    this.entity = entity;
    this.entity.setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(
            containerHandler.getEntityContainerName(),
            entitySetName,
            entity.getTypeName(),
            CoreUtils.getKey(client, typeRef, entity));

    this.stream = null;
  }

  public void setEntity(final CommonODataEntity entity) {
    this.entity = entity;
    this.entity.setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(
            getUUID().getContainerName(),
            getUUID().getEntitySetName(),
            getUUID().getName(),
            CoreUtils.getKey(client, typeRef, entity));

    this.propertyChanges.clear();
    this.linkChanges.clear();
    this.streamedPropertyChanges.clear();
    this.propertiesTag = 0;
    this.linksTag = 0;
    this.stream = null;
  }

  public EntityUUID getUUID() {
    return uuid;
  }

  @Override
  public FullQualifiedName getName() {
    return this.entity.getTypeName();
  }

  public String getEntityContainerName() {
    return uuid.getContainerName();
  }

  public String getEntitySetName() {
    return uuid.getEntitySetName();
  }

  public CommonODataEntity getEntity() {
    return entity;
  }

  /**
   * Gets the current ETag defined into the wrapped entity.
   *
   * @return
   */
  public String getETag() {
    return this.entity.getETag();
  }

  /**
   * Overrides ETag value defined into the wrapped entity.
   *
   * @param eTag ETag.
   */
  public void setETag(final String eTag) {
    this.entity.setETag(eTag);
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
      final Object res;
      final CommonODataProperty property = entity.getProperty(name);

      if (propertyChanges.containsKey(name)) {
        res = propertyChanges.get(name);
      } else if (property == null) {
        res = null;
      } else if (property.hasComplexValue()) {
        res = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {(Class<?>) type},
                ComplexTypeInvocationHandler.getInstance(
                client, property.getValue().asComplex(), (Class<?>) type, this));

        addPropertyChanges(name, res);
      } else if (property.hasCollectionValue()) {
        final ParameterizedType collType = (ParameterizedType) type;
        final Class<?> collItemClass = (Class<?>) collType.getActualTypeArguments()[0];

        final ArrayList<Object> collection = new ArrayList<Object>();

        final Iterator<ODataValue> collPropItor = property.getValue().asCollection().iterator();
        while (collPropItor.hasNext()) {
          final ODataValue value = collPropItor.next();
          if (value.isPrimitive()) {
            collection.add(CoreUtils.primitiveValueToObject(value.asPrimitive()));
          } else if (value.isComplex()) {
            final Object collItem = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] {collItemClass},
                    ComplexTypeInvocationHandler.getInstance(
                    client, value.asComplex(), collItemClass, this));

            collection.add(collItem);
          }
        }

        res = collection;

        addPropertyChanges(name, res);
      } else {
        res = type == null
                ? CoreUtils.getValueFromProperty(client, property)
                : CoreUtils.getValueFromProperty(client, property, type);

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
  public Collection<String> getAdditionalPropertyNames() {
    final Set<String> res = new HashSet<String>(propertyChanges.keySet());
    final Set<String> propertyNames = new HashSet<String>();
    for (Method method : typeRef.getMethods()) {
      final Annotation ann = method.getAnnotation(Property.class);
      if (ann != null) {
        final String property = ((Property) ann).name();
        propertyNames.add(property);

        // maybe someone could add a normal attribute to the additional set
        res.remove(property);
      }
    }

    for (CommonODataProperty property : entity.getProperties()) {
      if (!propertyNames.contains(property.getName())) {
        res.add(property.getName());
      }
    }

    return res;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void setPropertyValue(final Property property, final Object value) {
    if (property.type().equalsIgnoreCase(EdmPrimitiveTypeKind.Stream.toString())) {
      setStreamedProperty(property, (InputStream) value);
    } else {
      final Object toBeAdded;

      if (value == null) {
        toBeAdded = null;
      } else if (Collection.class.isAssignableFrom(value.getClass())) {
        toBeAdded = new ArrayList<Object>();
        for (Object obj : (Collection) value) {
          ((Collection) toBeAdded).add(obj instanceof Proxy ? Proxy.getInvocationHandler(obj) : obj);
        }
      } else if (value instanceof Proxy) {
        toBeAdded = Proxy.getInvocationHandler(value);
      } else {
        toBeAdded = value;
      }

      addPropertyChanges(property.name(), toBeAdded);
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

    final URI contentSource = entity.getMediaContentSource();

    if (this.stream == null
            && typeRef.getAnnotation(EntityType.class).hasStream()
            && contentSource != null) {

      final String contentType =
              StringUtils.isBlank(entity.getMediaContentType()) ? "*/*" : entity.getMediaContentType();

      final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(contentSource);
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
                containerHandler.getFactory().getServiceRoot(),
                CoreUtils.getEditMediaLink(property.name(), this.entity).toASCIIString());

        final ODataMediaRequest req = client.getRetrieveRequestFactory().getMediaRequest(link);
        res = req.execute().getBody();

      }
    } catch (Exception e) {
      res = null;
    }

    return res;

  }

  private void setStreamedProperty(final Property property, final InputStream input) {
    final Object obj = propertyChanges.get(property.name());
    if (obj != null && obj instanceof InputStream) {
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
  @SuppressWarnings("unchecked")
  protected void addPropertyChanges(final String name, final Object value) {
    int checkpoint = propertyChanges.hashCode();
    propertyChanges.put(name, value);
    updatePropertiesTag(checkpoint);
  }

  @Override
  protected void addLinkChanges(final NavigationProperty navProp, final Object value) {
    int checkpoint = linkChanges.hashCode();
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
