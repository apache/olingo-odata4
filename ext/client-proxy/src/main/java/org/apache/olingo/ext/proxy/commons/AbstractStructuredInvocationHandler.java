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
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinked;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.apache.olingo.ext.proxy.utils.ProxyUtils;

public abstract class AbstractStructuredInvocationHandler extends AbstractInvocationHandler {

  protected URIBuilder uri;

  protected URI baseURI;

  protected final Class<?> typeRef;

  protected EntityInvocationHandler entityHandler;

  protected Object internal;

  private final Map<String, AnnotatableInvocationHandler> propAnnotatableHandlers =
          new HashMap<String, AnnotatableInvocationHandler>();

  private final Map<String, AnnotatableInvocationHandler> navPropAnnotatableHandlers =
          new HashMap<String, AnnotatableInvocationHandler>();

  protected final Map<String, Object> propertyChanges = new HashMap<String, Object>();

  protected final Map<String, Object> propertyCache = new HashMap<String, Object>();

  protected final Map<NavigationProperty, Object> linkChanges = new HashMap<NavigationProperty, Object>();

  protected final Map<NavigationProperty, Object> linkCache = new HashMap<NavigationProperty, Object>();

  protected final Map<String, EdmStreamValue> streamedPropertyChanges = new HashMap<String, EdmStreamValue>();

  protected final Map<String, EdmStreamValue> streamedPropertyCache = new HashMap<String, EdmStreamValue>();

  protected AbstractStructuredInvocationHandler(
          final Class<?> typeRef,
          final AbstractService<?> service) {

    super(service);
    this.internal = null;
    this.typeRef = typeRef;
    this.entityHandler = null;
  }

  protected AbstractStructuredInvocationHandler(
          final Class<?> typeRef,
          final Object internal,
          final AbstractService<?> service) {

    super(service);
    this.internal = internal;
    this.typeRef = typeRef;
    this.entityHandler = null;
  }

  protected AbstractStructuredInvocationHandler(
          final Class<?> typeRef,
          final Object internal,
          final EntityInvocationHandler entityHandler) {

    super(entityHandler == null ? null : entityHandler.service);
    this.internal = internal;
    this.typeRef = typeRef;
    // prevent memory leak
    this.entityHandler = entityHandler == this ? null : entityHandler;
  }

  public Object getInternal() {
    return internal;
  }

  public EntityInvocationHandler getEntityHandler() {
    return entityHandler == null
            ? this instanceof EntityInvocationHandler
            ? EntityInvocationHandler.class.cast(this)
            : null
            : entityHandler;
  }

  public void setEntityHandler(final EntityInvocationHandler entityHandler) {
    // prevents memory leak
    this.entityHandler = entityHandler == this ? null : entityHandler;
  }

  public Class<?> getTypeRef() {
    return typeRef;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
  	if (method.getName().startsWith("get")) {  
  		// Here need check "get"/"set" first for better get-/set- performance because
  		// the below if-statements are really time-consuming, even twice slower than "get" body.

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
        res = getPropertyValue(property.name(), getter.getGenericReturnType());
      }

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
    } else if ("expand".equals(method.getName())
            || "select".equals(method.getName())
            || "refs".equals(method.getName())) {
      invokeSelfMethod(method, args);
      return proxy;
    } else if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else if ("load".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      load();
      return proxy;
    } else if ("loadAsync".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      return service.getClient().getConfiguration().getExecutor().submit(new Callable<Object>() {
        @Override
        public Object call() throws Exception {
          load();
          return proxy;
        }
      });
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(getEntityHandler()));
    } else if ("annotations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              AnnotatationsInvocationHandler.getInstance(getEntityHandler(), this));
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  public void delete(final String name) {
    if (baseURI != null) {
      getContext().entityContext().addFurtherDeletes(
              getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name).appendValueSegment().
              build());
    }
  }

  public void delete() {
    final EntityContext entityContext = getContext().entityContext();

    if (this instanceof EntityInvocationHandler) {
      deleteEntity(EntityInvocationHandler.class.cast(this), null);
    } else if (baseURI != null) {
      entityContext.addFurtherDeletes(
              getClient().newURIBuilder(baseURI.toASCIIString()).appendValueSegment().build());
    }
  }

  protected void attach() {
    attach(AttachedEntityStatus.ATTACHED, false);
  }

  protected void attach(final AttachedEntityStatus status) {
    attach(status, true);
  }

  protected void attach(final AttachedEntityStatus status, final boolean override) {
    if (getContext().entityContext().isAttached(getEntityHandler())) {
      if (override) {
        getContext().entityContext().setStatus(getEntityHandler(), status);
      }
    } else {
      getContext().entityContext().attach(getEntityHandler(), status);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected Object getPropertyValue(final String name, final Type type) {
    try {
      Object res;
      final Class<?> ref = ClassUtils.getTypeClass(type);

      if (ref == EdmStreamValue.class) {
        if (streamedPropertyCache.containsKey(name)) {
          res = streamedPropertyCache.get(name);
        } else if (streamedPropertyChanges.containsKey(name)) {
          res = streamedPropertyChanges.get(name);
        } else {
          res = Proxy.newProxyInstance(
                  Thread.currentThread().getContextClassLoader(),
                  new Class<?>[] {EdmStreamValue.class}, new EdmStreamValueHandler(
                  baseURI == null
                  ? null
                  : getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name).build(),
                  service));

          streamedPropertyCache.put(name, EdmStreamValue.class.cast(res));
        }

        return res;
      } else {
        if (propertyChanges.containsKey(name)) {
          res = propertyChanges.get(name);
        } else if (propertyCache.containsKey(name)) {
          res = propertyCache.get(name);
        } else {
          final ClientProperty property = getInternalProperty(name);

          if (ref != null && ClassUtils.getTypeClass(type).isAnnotationPresent(ComplexType.class)) {
            res = getComplex(
                    name,
                    property == null || property.hasNullValue() ? null : property.getValue(),
                    ref,
                    getEntityHandler(),
                    baseURI,
                    false);
          } else if (ref != null && ComplexCollection.class.isAssignableFrom(ref)) {
            final ComplexCollectionInvocationHandler<?> collectionHandler;
            final Class<?> itemRef = ClassUtils.extractTypeArg(ref, ComplexCollection.class);

            if (property == null || property.hasNullValue()) {
              collectionHandler = new ComplexCollectionInvocationHandler(
                      itemRef,
                      service,
                      baseURI == null
                      ? null : getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name));
            } else {
              List items = new ArrayList();

              for (ClientValue item : property.getValue().asCollection()) {
                items.add(getComplex(
                        name,
                        item,
                        itemRef,
                        getEntityHandler(),
                        null,
                        true));
              }

              collectionHandler = new ComplexCollectionInvocationHandler(
                      service,
                      items,
                      itemRef,
                      baseURI == null
                      ? null : getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name));
            }

            res = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] {ref}, collectionHandler);

          } else if (ref != null && PrimitiveCollection.class.isAssignableFrom(ref)) {
            PrimitiveCollectionInvocationHandler collectionHandler;
            if (property == null || property.hasNullValue()) {
              collectionHandler = new PrimitiveCollectionInvocationHandler(
                      service,
                      null,
                      baseURI == null
                      ? null : getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name));
            } else {
              List items = new ArrayList();
              for (ClientValue item : property.getValue().asCollection()) {
                items.add(item.asPrimitive().toValue());
              }
              collectionHandler = new PrimitiveCollectionInvocationHandler(
                      service,
                      items,
                      null,
                      baseURI == null
                      ? null : getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name));
            }

            res = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] {PrimitiveCollection.class}, collectionHandler);
          } else {
            res = property == null || property.hasNullValue()
                    ? null
                    : CoreUtils.getObjectFromODataValue(property.getValue(), type, service);
          }
        }

        if (res != null) {
          propertyCache.put(name, res);
        }

        return res;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Error getting value for property '" + name + "'", e);
    }
  }

  protected void setPropertyValue(final Property property, final Object value) {
    if (EdmPrimitiveTypeKind.Stream.getFullQualifiedName().toString().equalsIgnoreCase(property.type())) {
      setStreamedProperty(property, (EdmStreamValue) value);
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
            if ((handler instanceof ComplexInvocationHandler)
                    && ((ComplexInvocationHandler) handler).getEntityHandler() == null) {
              ((ComplexInvocationHandler) handler).setEntityHandler(getEntityHandler());
            }
          }
        }
      }
    }

    attach(AttachedEntityStatus.CHANGED);
  }

  private void setStreamedProperty(final Property property, final EdmStreamValue input) {
    final Object obj = streamedPropertyChanges.get(property.name());
    if (obj instanceof InputStream) {
      IOUtils.closeQuietly((InputStream) obj);
    }

    streamedPropertyCache.remove(property.name());
    streamedPropertyChanges.put(property.name(), input.load());
  }

  protected abstract Object getNavigationPropertyValue(final NavigationProperty property, final Method getter);

  protected Object retrieveNavigationProperty(final NavigationProperty property, final Method getter) {
    final Class<?> type = getter.getReturnType();
    final Class<?> collItemType;
    if (EntityCollection.class.isAssignableFrom(type)) {
      collItemType = ClassUtils.extractTypeArg(type, EntityCollection.class, ComplexCollection.class);
    } else {
      collItemType = type;
    }

    final Object navPropValue;

    URI targetEntitySetURI = CoreUtils.getTargetEntitySetURI(getClient(), property);
    final ClientLink link = ((ClientLinked) internal).getNavigationLink(property.name());

    if (link instanceof ClientInlineEntity) {
      // return entity
      navPropValue = ProxyUtils.getEntityProxy(
              service,
              ((ClientInlineEntity) link).getEntity(),
              targetEntitySetURI,
              type,
              null,
              false);
    } else if (link instanceof ClientInlineEntitySet) {
      if (AbstractEntitySet.class.isAssignableFrom(type)) {
        navPropValue =
            ProxyUtils.getEntitySetProxy(service, type, ((ClientInlineEntitySet) link).getEntitySet(),
                targetEntitySetURI, false);
      } else {
        // return entity set
        navPropValue = ProxyUtils.getEntityCollectionProxy(
                service,
                collItemType,
                type,
                targetEntitySetURI,
                ((ClientInlineEntitySet) link).getEntitySet(),
                targetEntitySetURI,
                false);
      }
    } else {
      // navigate
      final URI targetURI = URIUtils.getURI(getEntityHandler().getEntityURI(), property.name());

      if (EntityCollection.class.isAssignableFrom(type)) {
        navPropValue = ProxyUtils.getEntityCollectionProxy(
                service,
                collItemType,
                type,
                targetEntitySetURI,
                null,
                targetURI,
                true);
      } else if (AbstractEntitySet.class.isAssignableFrom(type)) {
        navPropValue =
                ProxyUtils.getEntitySetProxy(service, type, targetURI); // cannot be used standard target entity set URI
      } else {
        final EntityUUID uuid = new EntityUUID(targetEntitySetURI, collItemType, null);
        LOG.debug("Ask for '{}({})'", collItemType.getSimpleName(), null);

        EntityInvocationHandler handler = getContext().entityContext().getEntity(uuid);

        if (handler == null) {
          final ClientEntity entity = getClient().getObjectFactory().newEntity(new FullQualifiedName(
                  collItemType.getAnnotation(Namespace.class).value(), ClassUtils.getEntityTypeName(collItemType)));

          handler = EntityInvocationHandler.getInstance(
                  entity,
                  URIUtils.getURI(this.uri.build(), property.name()),
                  targetEntitySetURI,
                  collItemType,
                  service);

        } else if (getContext().entityContext().getStatus(handler) == AttachedEntityStatus.DELETED) {
          // object deleted
          LOG.debug("Object '{}({})' has been deleted", collItemType.getSimpleName(), uuid);
          handler = null;
        }

        navPropValue = handler == null ? null : Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {collItemType},
                handler);
      }
    }

    return navPropValue;
  }

  // use read- instead of get- for .invoke() to distinguish it from entity property getter.
  public Object readAdditionalProperty(final String name) {
    return getPropertyValue(name, null);
  }

  public Map<String, Object> getPropertyChanges() {
    Map<String, Object> changedProperties = new HashMap<String, Object>();
    changedProperties.putAll(propertyChanges);

    for (Map.Entry<String, Object> propertyCacheEntry : propertyCache.entrySet()) {
      if (hasCachedPropertyChanged(propertyCacheEntry.getValue())) {
        changedProperties.put(propertyCacheEntry.getKey(), propertyCacheEntry.getValue());
      }
    }

    return changedProperties;
  }

  protected boolean hasCachedPropertyChanged(final Object cachedValue) {
    AbstractStructuredInvocationHandler structuredInvocationHandler = getStructuredInvocationHandler(cachedValue);
    if (structuredInvocationHandler != null) {
      return structuredInvocationHandler.isChanged();
    }

    return false;
  }

  public boolean isChanged() {
    return !linkChanges.isEmpty()
        || hasPropertyChanges();
  }

  protected boolean hasPropertyChanges() {
    return !propertyChanges.isEmpty() || hasDeepPropertyChanges();
  }

  protected boolean hasDeepPropertyChanges() {
    for (Object propertyValue : propertyCache.values()) {
      if (hasCachedPropertyChanged(propertyValue)) {
        return true;
      }
    }

    return false;
  }
  
  public void applyChanges() {
    streamedPropertyCache.putAll(streamedPropertyChanges);
    streamedPropertyChanges.clear();
    propertyCache.putAll(propertyChanges);
    propertyChanges.clear();
    linkCache.putAll(linkChanges);
    linkChanges.clear();
    
    applyChangesOnChildren();
  }

  protected void applyChangesOnChildren() {
    for (Object propertyValue : propertyCache.values()) {
      applyChanges(propertyValue);
    }
  }

  protected void applyChanges(final Object cachedValue) {
    AbstractStructuredInvocationHandler structuredInvocationHandler = getStructuredInvocationHandler(cachedValue);
    if (structuredInvocationHandler != null) {
      structuredInvocationHandler.applyChanges();
    }
  }
  
  protected AbstractStructuredInvocationHandler getStructuredInvocationHandler(final Object value) {
    if (value != null && Proxy.isProxyClass(value.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(value);
      if (invocationHandler instanceof AbstractStructuredInvocationHandler) {
        return (AbstractStructuredInvocationHandler) invocationHandler;
      }
    }

    return null;
  }

  public Collection<String> readAdditionalPropertyNames() {
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

    for (ClientProperty property : getInternalProperties()) {
      if (!propertyNames.contains(property.getName())) {
        res.add(property.getName());
      }
    }

    return res;
  }

  public void addAdditionalProperty(final String name, final Object value) {
    propertyChanges.put(name, value);
    attach(AttachedEntityStatus.CHANGED);
  }

  public Map<NavigationProperty, Object> getLinkChanges() {
    return linkChanges;
  }

  public void removeAdditionalProperty(final String name) {
    propertyChanges.remove(name);
    attach(AttachedEntityStatus.CHANGED);
  }

  protected void addPropertyChanges(final String name, final Object value) {
    propertyCache.remove(name);
    propertyChanges.put(name, value);
  }

  protected void addLinkChanges(final NavigationProperty navProp, final Object value) {
    linkChanges.put(navProp, value);

    if (linkCache.containsKey(navProp)) {
      linkCache.remove(navProp);
    }
  }

  public Map<String, EdmStreamValue> getStreamedPropertyChanges() {
    return streamedPropertyChanges;
  }

  private void setNavigationPropertyValue(final NavigationProperty property, final Object value) {
    // 1) attach source entity
    if (!getContext().entityContext().isAttached(getEntityHandler())) {
      getContext().entityContext().attach(getEntityHandler(), AttachedEntityStatus.CHANGED);
    }

    // 2) add links
    addLinkChanges(property, value);
  }

  public Map<String, AnnotatableInvocationHandler> getPropAnnotatableHandlers() {
    return propAnnotatableHandlers;
  }

  public void putPropAnnotatableHandler(final String propName, final AnnotatableInvocationHandler handler) {
    propAnnotatableHandlers.put(propName, handler);
  }

  public Map<String, AnnotatableInvocationHandler> getNavPropAnnotatableHandlers() {
    return navPropAnnotatableHandlers;
  }

  public void putNavPropAnnotatableHandler(final String navPropName, final AnnotatableInvocationHandler handler) {
    navPropAnnotatableHandlers.put(navPropName, handler);
  }

  public void expand(final String... expand) {
    this.uri.replaceQueryOption(QueryOption.EXPAND, StringUtils.join(expand, ","));
  }

  public void select(final String... select) {
    this.uri.replaceQueryOption(QueryOption.SELECT, StringUtils.join(select, ","));
  }

  public void refs() {
      this.uri.appendRefSegment();
  }

  public void clearQueryOptions() {
    this.uri = baseURI == null ? null : getClient().newURIBuilder(baseURI.toASCIIString());
  }

  protected abstract void load();

  protected abstract <T extends ClientProperty> List<T> getInternalProperties();

  protected abstract ClientProperty getInternalProperty(final String name);
}
