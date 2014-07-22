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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.annotations.EntityContainer;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public final class EntityContainerInvocationHandler extends AbstractInvocationHandler {

  private static final long serialVersionUID = 7379006755693410764L;

  protected final String namespace;

  private final String name;

  private final boolean defaultEC;

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
    this.defaultEC = ((EntityContainer) annotation).isDefaultEntityContainer();
    this.namespace = ((EntityContainer) annotation).namespace();
  }

  protected AbstractService<?> getService() {
    return service;
  }

  protected boolean isDefaultEntityContainer() {
    return defaultEC;
  }

  protected String getEntityContainerName() {
    return name;
  }

  protected String getSchemaName() {
    return namespace;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if ("flush".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      return service.getPersistenceManager().flush();
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else {
      final Class<?> returnType = method.getReturnType();

      final EntitySet entitySet = returnType.getAnnotation(EntitySet.class);
      if (entitySet == null) {
        final Singleton singleton = returnType.getAnnotation(Singleton.class);
        if (singleton != null) {
          return Proxy.newProxyInstance(
                  Thread.currentThread().getContextClassLoader(),
                  new Class<?>[] {returnType},
                  SingletonInvocationHandler.getInstance(returnType, service));
        }
      } else {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {returnType},
                EntitySetInvocationHandler.getInstance(returnType, service));
      }

      throw new NoSuchMethodException(method.getName());
    }
  }

  @SuppressWarnings("unchecked")
  public <NE extends EntityType> NE newEntityInstance(final Class<NE> ref) {
    final EntityInvocationHandler handler = EntityInvocationHandler.getInstance(ref, getService());

    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            handler);
  }

  @SuppressWarnings("unchecked")
  public <T extends EntityType, NEC extends EntityCollection<T>> NEC newEntityCollection(final Class<NEC> ref) {
    return (NEC) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            new EntityCollectionInvocationHandler<T>(getService(), ref));
  }

  @SuppressWarnings("unchecked")
  public <NE extends ComplexType> NE newComplexInstance(final Class<NE> ref) {
    return (NE) Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {ref},
            ComplexInvocationHandler.getInstance(ref, getService()));
  }

  @SuppressWarnings("unchecked")
  public <T extends ComplexType, NEC extends ComplexCollection<T>> NEC newComplexCollection(final Class<NEC> ref) {
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
}
