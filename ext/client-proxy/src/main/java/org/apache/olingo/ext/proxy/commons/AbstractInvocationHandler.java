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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Singleton;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.Context;
import org.apache.olingo.ext.proxy.context.EntityContext;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractInvocationHandler implements InvocationHandler {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractInvocationHandler.class);

  protected AbstractService<?> service;

  protected AbstractInvocationHandler(final AbstractService<?> service) {
    this.service = service;
  }

  protected EdmEnabledODataClient getClient() {
    return service.getClient();
  }

  protected Context getContext() {
    return service.getContext();
  }

  protected boolean isSelfMethod(final Method method) {
    final Method[] selfMethods = getClass().getMethods();

    boolean result = false;

    for (int i = 0; i < selfMethods.length && !result; i++) {
      result = method.getName().equals(selfMethods[i].getName())
          && Arrays.equals(method.getParameterTypes(), selfMethods[i].getParameterTypes());
    }

    return result;
  }

  protected Object invokeSelfMethod(final Method method, final Object[] args)
      throws Throwable {
    //Try as per https://amitstechblog.wordpress.com/2011/07/24/java-proxies-and-undeclaredthrowableexception/
    try {
      return getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(this, args);
    } catch (UndeclaredThrowableException e) {
      throw e.getCause();
    } catch(InvocationTargetException e) {
      throw e.getTargetException();
    }
  }

  protected ComplexType<?> getComplex(
      final String name,
      final ClientValue value,
      final Class<?> ref,
      final EntityInvocationHandler handler,
      final URI baseURI,
      final boolean collectionItem) {

    final URIBuilder targetURI;
    if (collectionItem) {
      targetURI = null;
    } else {
      targetURI = baseURI == null
          ? null : getClient().newURIBuilder(baseURI.toASCIIString()).appendPropertySegment(name);
    }

    final ComplexInvocationHandler complexHandler;
    Class<?> actualRef = ref;
    if (value == null) {
      complexHandler = ComplexInvocationHandler.getInstance(
          actualRef,
          service,
          targetURI);
    } else {
      actualRef = CoreUtils.getComplexTypeRef(service, value); // handle derived types
      complexHandler = ComplexInvocationHandler.getInstance(
          value.asComplex(),
          actualRef,
          service,
          targetURI);
    }

    complexHandler.setEntityHandler(handler);

    final ComplexType<?> res = ComplexType.class.cast(Proxy.newProxyInstance(
        Thread.currentThread().getContextClassLoader(),
        new Class<?>[] { actualRef }, complexHandler));

    return res;
  }

  protected boolean isDeleted(final EntityInvocationHandler handler) {
    return (getContext().entityContext().isAttached(handler)
        && getContext().entityContext().getStatus(handler) == AttachedEntityStatus.DELETED)
        || getContext().entityContext().getFurtherDeletes().contains(handler.getEntityURI());
  }

  protected <S extends EntityType<?>> void deleteEntity(final EntityInvocationHandler handler, final URI entitySetURI) {
    final EntityContext entityContext = getContext().entityContext();

    final URI baseURI = entitySetURI == null ? handler.getEntitySetURI() : entitySetURI;

    if (baseURI == null) {
      throw new IllegalStateException("Entity base URI not available");
    }

    final String name = handler.getUUID().getType().
        getAnnotation(org.apache.olingo.ext.proxy.api.annotations.EntityType.class).name();

    final String namespace = handler.getUUID().getType().getAnnotation(Namespace.class).value();

    final ClientEntity template;

    final URI entityURI;
    if (handler.getEntityURI() == null || handler.getUUID().getKey() == null) {
      template = service.getClient().getObjectFactory().newEntity(new FullQualifiedName(namespace, name));
      CoreUtils.addProperties(getClient(), handler.getPropertyChanges(), template);
      final Object key = CoreUtils.getKey(getClient(), handler, handler.getUUID().getType(), template);

      entityURI = CoreUtils.buildEditLink(getClient(), baseURI.toASCIIString(), key).build();
      template.setEditLink(entityURI);
    } else {
      entityURI = handler.getEntityURI();
      template = handler.getEntity();
    }

    // https://issues.apache.org/jira/browse/OLINGO-395
    if (entityContext.isAttached(handler)) {
      entityContext.addFurtherDeletes(entityURI);
    } else {
      if (handler.getUUID().getKey() == null) {
        // objects created ad-hoc to generate deletion requests
        handler.updateEntityUUID(baseURI, handler.getUUID().getType(), template);
      } else {
        handler.updateUUID(baseURI, handler.getUUID().getType(), handler.getUUID().getKey());
      }
      entityContext.attach(handler, AttachedEntityStatus.DELETED, true);
    }
  }

  protected static URIBuilder buildEntitySetURI(final Class<?> ref, final AbstractService<?> service) {

    final String entitySetName;
    Annotation ann = ref.getAnnotation(EntitySet.class);
    if (ann instanceof EntitySet) {
      entitySetName = EntitySet.class.cast(ann).name();
    } else {
      ann = ref.getAnnotation(Singleton.class);
      if (ann instanceof Singleton) {
        entitySetName = Singleton.class.cast(ann).name();
      } else {
        entitySetName = null;
      }
    }

    return buildEntitySetURI(entitySetName, service);
  }

  protected static URIBuilder buildEntitySetURI(final String entitySetName, final AbstractService<?> service) {

    final URIBuilder uriBuilder = service.getClient().newURIBuilder();
    final StringBuilder entitySetSegment = new StringBuilder();

    entitySetSegment.append(entitySetName);
    uriBuilder.appendEntitySetSegment(entitySetSegment.toString());
    return uriBuilder;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
