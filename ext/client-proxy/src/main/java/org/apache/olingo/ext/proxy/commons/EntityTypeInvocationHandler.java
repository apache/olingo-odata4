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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataMediaFormat;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.EngineUtils;

public class EntityTypeInvocationHandler<C extends CommonEdmEnabledODataClient<?>>
        extends AbstractInvocationHandler<C> {

  private static final long serialVersionUID = 2629912294765040037L;

  private CommonODataEntity entity;

  private final Class<?> typeRef;

  private Map<String, Object> propertyChanges = new HashMap<String, Object>();

  private Map<String, InputStream> streamedPropertyChanges = new HashMap<String, InputStream>();

  private Map<NavigationProperty, Object> linkChanges = new HashMap<NavigationProperty, Object>();

  private InputStream stream;

  private EntityUUID uuid;

  private final EntityContext entityContext = EntityContainerFactory.getContext().entityContext();

  private int propertiesTag;

  private int linksTag;

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

    super(containerHandler.getClient(), containerHandler);
    this.typeRef = typeRef;

    this.entity = entity;
    this.entity.setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(
            containerHandler.getEntityContainerName(),
            entitySetName,
            entity.getTypeName(),
            EngineUtils.getKey(client.getCachedEdm(), typeRef, entity));

    this.stream = null;
    this.propertiesTag = 0;
    this.linksTag = 0;
  }

  public void setEntity(final CommonODataEntity entity) {
    this.entity = entity;
    this.entity.setMediaEntity(typeRef.getAnnotation(EntityType.class).hasStream());

    this.uuid = new EntityUUID(
            getUUID().getContainerName(),
            getUUID().getEntitySetName(),
            getUUID().getName(),
            EngineUtils.getKey(client.getCachedEdm(), typeRef, entity));

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

  public FullQualifiedName getName() {
    return this.entity.getTypeName();
  }

  public String getEntityContainerName() {
    return uuid.getContainerName();
  }

  public String getEntitySetName() {
    return uuid.getEntitySetName();
  }

  public Class<?> getTypeRef() {
    return typeRef;
  }

  public CommonODataEntity getEntity() {
    return entity;
  }

  public Map<String, Object> getPropertyChanges() {
    return propertyChanges;
  }

  public Map<NavigationProperty, Object> getLinkChanges() {
    return linkChanges;
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

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else if (method.getName().startsWith("get")) {
      // Assumption: for each getter will always exist a setter and viceversa.
      // get method annotation and check if it exists as expected
      final Object res;

      final Method getter = typeRef.getMethod(method.getName());

      final Property property = ClassUtils.getAnnotation(Property.class, getter);
      if (property == null) {
        final NavigationProperty navProp = ClassUtils.getAnnotation(NavigationProperty.class, getter);
        if (navProp == null) {
          throw new UnsupportedOperationException("Unsupported method " + method.getName());
        } else {
          // if the getter refers to a navigation property ... navigate and follow link if necessary
          res = getNavigationPropertyValue(navProp, getter);
        }
      } else {
        // if the getter refers to a property .... get property from wrapped entity
        res = getPropertyValue(property, getter.getGenericReturnType());
      }

      // attach the current handler
      attach();

      return res;
    } else if (method.getName().startsWith("set")) {
      // get the corresponding getter method (see assumption above)
      final String getterName = method.getName().replaceFirst("set", "get");
      final Method getter = typeRef.getMethod(getterName);

      final Property property = ClassUtils.getAnnotation(Property.class, getter);
      if (property == null) {
        final NavigationProperty navProp = ClassUtils.getAnnotation(NavigationProperty.class, getter);
        if (navProp == null) {
          throw new UnsupportedOperationException("Unsupported method " + method.getName());
        } else {
          // if the getter refers to a navigation property ... 
          if (ArrayUtils.isEmpty(args) || args.length != 1) {
            throw new IllegalArgumentException("Invalid argument");
          }

          setNavigationPropertyValue(navProp, args[0]);
        }
      } else {
        setPropertyValue(property, args[0]);
      }

      return ClassUtils.returnVoid();
    } else {
      throw new UnsupportedOperationException("Method not found: " + method);
    }
  }

  private Object getNavigationPropertyValue(final NavigationProperty property, final Method getter) {
    final Class<?> type = getter.getReturnType();
    final Class<?> collItemType;
    if (AbstractEntityCollection.class.isAssignableFrom(type)) {
      final Type[] entityCollectionParams =
              ((ParameterizedType) type.getGenericInterfaces()[0]).getActualTypeArguments();
      collItemType = (Class<?>) entityCollectionParams[0];
    } else {
      collItemType = type;
    }

    final Object navPropValue;

    if (linkChanges.containsKey(property)) {
      navPropValue = linkChanges.get(property);
    } else {
      final ODataLink link = EngineUtils.getNavigationLink(property.name(), entity);
      if (link instanceof ODataInlineEntity) {
        // return entity
        navPropValue = getEntityProxy(
                ((ODataInlineEntity) link).getEntity(),
                property.targetContainer(),
                property.targetEntitySet(),
                type,
                false);
      } else if (link instanceof ODataInlineEntitySet) {
        // return entity set
        navPropValue = getEntityCollection(
                collItemType,
                type,
                property.targetContainer(),
                ((ODataInlineEntitySet) link).getEntitySet(),
                link.getLink(),
                false);
      } else {
        // navigate
        final URI uri = URIUtils.getURI(
                containerHandler.getFactory().getServiceRoot(), link.getLink().toASCIIString());

        if (AbstractEntityCollection.class.isAssignableFrom(type)) {
          navPropValue = getEntityCollection(
                  collItemType,
                  type,
                  property.targetContainer(),
                  client.getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody(),
                  uri,
                  true);
        } else {
          final ODataRetrieveResponse<CommonODataEntity> res =
                  client.getRetrieveRequestFactory().getEntityRequest(uri).execute();

          navPropValue = getEntityProxy(
                  res.getBody(),
                  property.targetContainer(),
                  property.targetEntitySet(),
                  type,
                  res.getETag(),
                  true);
        }
      }

      if (navPropValue != null) {
        int checkpoint = linkChanges.hashCode();
        linkChanges.put(property, navPropValue);
        updateLinksTag(checkpoint);
      }
    }

    return navPropValue;
  }

  private Object getPropertyValue(final String name, final Type type) {
    try {
      final Object res;

      if (propertyChanges.containsKey(name)) {
        res = propertyChanges.get(name);
      } else {

        res = type == null
                ? EngineUtils.getValueFromProperty(
                client.getCachedEdm(), entity.getProperty(name))
                : EngineUtils.getValueFromProperty(
                client.getCachedEdm(), entity.getProperty(name), type);

        if (res != null) {
          int checkpoint = propertyChanges.hashCode();
          propertyChanges.put(name, res);
          updatePropertiesTag(checkpoint);
        }
      }

      return res;
    } catch (Exception e) {
      throw new IllegalArgumentException("Error getting value for property '" + name + "'", e);
    }
  }

  private Object getPropertyValue(final Property property, final Type type) {
    if (!(type instanceof ParameterizedType) && (Class<?>) type == InputStream.class) {
      return getStreamedProperty(property);
    } else {
      return getPropertyValue(property.name(), type);
    }
  }

  public Object getAdditionalProperty(final String name) {
    return getPropertyValue(name, null);
  }

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

  private void setNavigationPropertyValue(final NavigationProperty property, final Object value) {
    // 1) attach source entity
    attach(AttachedEntityStatus.CHANGED, false);

    // 2) attach the target entity handlers
    for (Object link : AbstractEntityCollection.class.isAssignableFrom(value.getClass())
            ? (AbstractEntityCollection) value : Collections.singleton(value)) {

      final InvocationHandler etih = Proxy.getInvocationHandler(link);
      if (!(etih instanceof EntityTypeInvocationHandler)) {
        throw new IllegalArgumentException("Invalid argument type");
      }

      @SuppressWarnings("unchecked")
      final EntityTypeInvocationHandler<C> handler = (EntityTypeInvocationHandler<C>) etih;
      if (!handler.getTypeRef().isAnnotationPresent(EntityType.class)) {
        throw new IllegalArgumentException(
                "Invalid argument type " + handler.getTypeRef().getSimpleName());
      }

      if (!entityContext.isAttached(handler)) {
        entityContext.attach(handler, AttachedEntityStatus.LINKED);
      }
    }

    // 3) add links
    linkChanges.put(property, value);
  }

  private void setPropertyValue(final Property property, final Object value) {
    if (property.type().equalsIgnoreCase("Edm.Stream")) {
      setStreamedProperty(property, (InputStream) value);
    } else {
      propertyChanges.put(property.name(), value);
    }

    attach(AttachedEntityStatus.CHANGED);
  }

  public void addAdditionalProperty(final String name, final Object value) {
    propertyChanges.put(name, value);
    attach(AttachedEntityStatus.CHANGED);
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

    final String contentSource = entity.getMediaContentSource();

    if (this.stream == null
            && typeRef.getAnnotation(EntityType.class).hasStream()
            && StringUtils.isNotBlank(contentSource)) {

      final String comntentType =
              StringUtils.isBlank(entity.getMediaContentType()) ? "*/*" : entity.getMediaContentType();

      final URI contentSourceURI = URIUtils.getURI(containerHandler.getFactory().getServiceRoot(), contentSource);

      final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(contentSourceURI);
      retrieveReq.setFormat(ODataMediaFormat.fromFormat(comntentType));

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
                EngineUtils.getEditMediaLink(property.name(), this.entity).toASCIIString());

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

  private void attach() {
    if (!entityContext.isAttached(this)) {
      entityContext.attach(this, AttachedEntityStatus.ATTACHED);
    }
  }

  private void attach(final AttachedEntityStatus status) {
    attach(status, true);
  }

  private void attach(final AttachedEntityStatus status, final boolean override) {
    if (entityContext.isAttached(this)) {
      if (override) {
        entityContext.setStatus(this, status);
      }
    } else {
      entityContext.attach(this, status);
    }
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
