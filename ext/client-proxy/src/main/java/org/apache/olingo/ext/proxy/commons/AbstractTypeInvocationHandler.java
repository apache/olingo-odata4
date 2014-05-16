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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTypeInvocationHandler extends AbstractInvocationHandler {

  private static final long serialVersionUID = 2629912294765040037L;

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractTypeInvocationHandler.class);

  protected final Class<?> typeRef;

  protected EntityTypeInvocationHandler entityHandler;

  protected Object internal;

  protected AbstractTypeInvocationHandler(
          final Class<?> typeRef,
          final Object internal,
          final EntityContainerInvocationHandler containerHandler) {

    super(containerHandler);
    this.internal = internal;
    this.typeRef = typeRef;
    this.entityHandler = EntityTypeInvocationHandler.class.cast(this);
  }

  protected AbstractTypeInvocationHandler(
          final Class<?> typeRef,
          final Object internal,
          final EntityTypeInvocationHandler entityHandler) {

    super(entityHandler == null ? null : entityHandler.containerHandler);
    this.internal = internal;
    this.typeRef = typeRef;
    this.entityHandler = entityHandler;
  }

  public EntityTypeInvocationHandler getEntityHandler() {
    return entityHandler;
  }

  public void setEntityHandler(final EntityTypeInvocationHandler entityHandler) {
    this.entityHandler = entityHandler;
  }

  public Class<?> getTypeRef() {
    return typeRef;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(entityHandler));
    } else if ("factory".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              ComplexFactoryInvocationHandler.getInstance(entityHandler, this));
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
      throw new NoSuchMethodException(method.getName());
    }
  }

  protected void attach() {
    if (entityHandler != null && !getContext().entityContext().isAttached(entityHandler)) {
      getContext().entityContext().attach(entityHandler, AttachedEntityStatus.ATTACHED);
    }
  }

  protected void attach(final AttachedEntityStatus status) {
    attach(status, true);
  }

  protected void attach(final AttachedEntityStatus status, final boolean override) {
    if (getContext().entityContext().isAttached(entityHandler)) {
      if (override) {
        getContext().entityContext().setStatus(entityHandler, status);
      }
    } else {
      getContext().entityContext().attach(entityHandler, status);
    }
  }

  protected abstract Object getNavigationPropertyValue(final NavigationProperty property, final Method getter);

  protected Object retriveNavigationProperty(final NavigationProperty property, final Method getter) {
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

    final ODataLink link = ((ODataLinked) internal).getNavigationLink(property.name());
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
      final URI uri = URIUtils.getURI(getClient().getServiceRoot(), link.getLink().toASCIIString());

      if (AbstractEntityCollection.class.isAssignableFrom(type)) {
        navPropValue = getEntityCollection(
                collItemType,
                type,
                property.targetContainer(),
                getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody(),
                uri,
                true);
      } else {
        final ODataRetrieveResponse<CommonODataEntity> res =
                getClient().getRetrieveRequestFactory().getEntityRequest(uri).execute();

        navPropValue = getEntityProxy(
                res.getBody(),
                property.targetContainer(),
                property.targetEntitySet(),
                type,
                res.getETag(),
                true);
      }
    }

    return navPropValue;
  }

  protected abstract Object getPropertyValue(final String name, final Type type);

  private Object getPropertyValue(final Property property, final Type type) {
    return getPropertyValue(property.name(), type);
  }

  public void addAdditionalProperty(final String name, final Object value) {
    addPropertyChanges(name, value);
    attach(AttachedEntityStatus.CHANGED);
  }

  public Object getAdditionalProperty(final String name) {
    return getPropertyValue(name, null);
  }

  private void setNavigationPropertyValue(final NavigationProperty property, final Object value) {
    // 1) attach source entity
    if (!getContext().entityContext().isAttached(entityHandler)) {
      getContext().entityContext().attach(entityHandler, AttachedEntityStatus.CHANGED);
    }

    // 2) attach the target entity handlers
    for (Object link : AbstractEntityCollection.class.isAssignableFrom(value.getClass())
            ? (AbstractEntityCollection) value : Collections.singleton(value)) {

      final InvocationHandler etih = Proxy.getInvocationHandler(link);
      if (!(etih instanceof EntityTypeInvocationHandler)) {
        throw new IllegalArgumentException("Invalid argument type");
      }

      final EntityTypeInvocationHandler linkedHandler = (EntityTypeInvocationHandler) etih;
      if (!linkedHandler.getTypeRef().isAnnotationPresent(EntityType.class)) {
        throw new IllegalArgumentException("Invalid argument type " + linkedHandler.getTypeRef().getSimpleName());
      }

      if (!getContext().entityContext().isAttached(linkedHandler)) {
        getContext().entityContext().attach(linkedHandler, AttachedEntityStatus.LINKED);
      }
    }

    // 3) add links
    addLinkChanges(property, value);
  }

  protected abstract void setPropertyValue(final Property property, final Object value);

  protected abstract void addPropertyChanges(final String name, final Object value);

  protected abstract void addLinkChanges(final NavigationProperty navProp, final Object value);

  public abstract boolean isChanged();
}
