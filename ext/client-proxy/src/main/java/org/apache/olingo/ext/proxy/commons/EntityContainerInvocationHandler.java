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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.ext.proxy.api.annotations.EntityContainer;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Singleton;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class EntityContainerInvocationHandler extends AbstractInvocationHandler {

  private static final long serialVersionUID = 7379006755693410764L;

  protected final String namespace;

  private final String name;

  private final boolean defaultEC;

  public static EntityContainerInvocationHandler getInstance(
          final Class<?> ref, final Service<?> factory) {

    final EntityContainerInvocationHandler instance = new EntityContainerInvocationHandler(ref, factory);
    instance.containerHandler = instance;
    return instance;
  }

  private EntityContainerInvocationHandler(final Class<?> ref, final Service<?> factory) {
    super(factory);

    final Annotation annotation = ref.getAnnotation(EntityContainer.class);
    if (!(annotation instanceof EntityContainer)) {
      throw new IllegalArgumentException(
              ref.getName() + " is not annotated as @" + EntityContainer.class.getSimpleName());
    }

    this.name = ((EntityContainer) annotation).name();
    this.defaultEC = ((EntityContainer) annotation).isDefaultEntityContainer();
    this.namespace = ((EntityContainer) annotation).namespace();
  }

  protected Service<?> getFactory() {
    return factory;
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
      factory.getPersistenceManager().flush();
      return ClassUtils.returnVoid();
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else if ("complexFactory".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              ComplexFactoryInvocationHandler.getInstance(this, null, null));
    } else {
      final Class<?> returnType = method.getReturnType();

      final EntitySet entitySet = returnType.getAnnotation(EntitySet.class);
      if (entitySet == null) {
        final Singleton singleton = returnType.getAnnotation(Singleton.class);
        if (singleton != null) {
          return Proxy.newProxyInstance(
                  Thread.currentThread().getContextClassLoader(),
                  new Class<?>[] {returnType},
                  SingletonInvocationHandler.getInstance(returnType, this, singleton.name()));
        }
      } else {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {returnType},
                EntitySetInvocationHandler.getInstance(returnType, this, entitySet.name()));
      }

      throw new NoSuchMethodException(method.getName());
    }
  }
}
