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
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.api.annotations.EntityContainer;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.Singleton;
import org.apache.olingo.ext.proxy.context.EntityUUID;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public final class EntityContainerInvocationHandler extends AbstractInvocationHandler {

  protected final String namespace;
  private final String name;

  public static EntityContainerInvocationHandler getInstance(final Class<?> ref, final AbstractService<?> service) {

    final EntityContainerInvocationHandler instance = new EntityContainerInvocationHandler(ref, service);
    return instance;
  }

  private EntityContainerInvocationHandler(final Class<?> ref, final AbstractService<?> service) {
    super(service);

    final Annotation annotation = ref.getAnnotation(EntityContainer.class);
    if (!(annotation instanceof EntityContainer)) {
      throw new IllegalArgumentException(
              ref.getName() + " is not annotated as @" + EntityContainer.class.getSimpleName());
    }

    this.name = ((EntityContainer) annotation).name();
    this.namespace = ((EntityContainer) annotation).namespace();
  }

  protected AbstractService<?> getService() {
    return service;
  }

  protected String getEntityContainerName() {
    return name;
  }

  protected String getSchemaName() {
    return namespace;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else if ("flush".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      service.getPersistenceManager().flush();
      return ClassUtils.returnVoid();
    } else if ("flushAsync".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      return service.getPersistenceManager().flushAsync();
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else {
      final Class<?> returnType = method.getReturnType();

      if (returnType.isAnnotationPresent(EntitySet.class)) {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {returnType},
                EntitySetInvocationHandler.getInstance(returnType, service));
      } else if (returnType.isAnnotationPresent(org.apache.olingo.ext.proxy.api.annotations.EntityType.class)) {
        return getSingleton(method);
      }

      throw new NoSuchMethodException(method.getName());
    }
  }

  private Object getSingleton(final Method method) throws IllegalArgumentException {
    final Class<?> typeRef = method.getReturnType();
    final Singleton singleton = method.getAnnotation(Singleton.class);

    final URI uri = buildEntitySetURI(singleton.name(), service).build();
    final EntityUUID uuid = new EntityUUID(uri, typeRef);
    LOG.debug("Ask for singleton '{}'", typeRef.getSimpleName());

    EntityInvocationHandler handler = getContext().entityContext().getEntity(uuid);

    if (handler == null) {
      final ClientEntity entity = getClient().getObjectFactory().newEntity(new FullQualifiedName(
              typeRef.getAnnotation(Namespace.class).value(), ClassUtils.getEntityTypeName(typeRef)));

      handler = EntityInvocationHandler.getInstance(entity, uri, uri, typeRef, service);
    } else if (isDeleted(handler)) {
      // object deleted
      LOG.debug("Singleton '{}' has been deleted", typeRef.getSimpleName());
      handler = null;
    }

    return handler == null
            ? null
            : Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] {typeRef}, handler);
  }

  @SuppressWarnings("unchecked")
  public <NE extends EntityType<?>> NE newEntityInstance(final Class<NE> ref) {
    final EntityInvocationHandler handler = EntityInvocationHandler.getInstance(ref, getService());

    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            handler);
  }

  @SuppressWarnings("unchecked")
  public <T extends EntityType<?>, NEC extends EntityCollection<T, ?, ?>> NEC newEntityCollection(
          final Class<NEC> ref) {
    return (NEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            new EntityCollectionInvocationHandler<T>(getService(), ref));
  }

  @SuppressWarnings("unchecked")
  public <NE extends ComplexType<?>> NE newComplexInstance(final Class<NE> ref) {
    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            ComplexInvocationHandler.getInstance(ref, getService()));
  }

  @SuppressWarnings("unchecked")
  public <T extends ComplexType<?>, NEC extends ComplexCollection<T, ?, ?>> NEC newComplexCollection(
          final Class<NEC> ref) {
    final Class<T> itemRef = (Class<T>) ClassUtils.extractTypeArg(ref, ComplexCollection.class);

    return (NEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            new ComplexCollectionInvocationHandler<T>(getService(), itemRef));
  }

  @SuppressWarnings("unchecked")
  public <T extends Serializable, NEC extends PrimitiveCollection<T>> NEC newPrimitiveCollection(final Class<T> ref) {

    return (NEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {PrimitiveCollection.class},
            new PrimitiveCollectionInvocationHandler<T>(getService(), ref));
  }

  public EdmStreamValue newEdmStreamValue(
          final String contentType, final InputStream stream) {

    return EdmStreamValue.class.cast(Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {EdmStreamValue.class},
            new EdmStreamValueHandler(contentType, stream, null, getService())));
  }
}
