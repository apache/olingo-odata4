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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.ext.proxy.api.annotations.EntityContainer;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public class EntityContainerInvocationHandler<C extends CommonEdmEnabledODataClient<?>>
        extends AbstractInvocationHandler<C> {

  private static final long serialVersionUID = 7379006755693410764L;

  private final EntityContainerFactory factory;

  protected final String namespace;

  private final String name;

  private final boolean defaultEC;

  public static <C extends CommonEdmEnabledODataClient<?>> EntityContainerInvocationHandler<C> getInstance(
          final C client, final Class<?> ref, final EntityContainerFactory factory) {

    final EntityContainerInvocationHandler<C> instance = new EntityContainerInvocationHandler<C>(client, ref, factory);
    instance.containerHandler = instance;
    return instance;
  }

  private EntityContainerInvocationHandler(
          final C client, final Class<?> ref, final EntityContainerFactory factory) {

    super(client, null);

    final Annotation annotation = ref.getAnnotation(EntityContainer.class);
    if (!(annotation instanceof EntityContainer)) {
      throw new IllegalArgumentException(
              ref.getName() + " is not annotated as @" + EntityContainer.class.getSimpleName());
    }

    this.factory = factory;
    this.name = ((EntityContainer) annotation).name();
    this.defaultEC = ((EntityContainer) annotation).isDefaultEntityContainer();
    this.namespace = ((EntityContainer) annotation).namespace();
  }

  EntityContainerFactory getFactory() {
    return factory;
  }

  boolean isDefaultEntityContainer() {
    return defaultEC;
  }

  String getEntityContainerName() {
    return name;
  }

  String getSchemaName() {
    return namespace;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if ("flush".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      new Container(client, factory).flush();
      return ClassUtils.returnVoid();
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              OperationInvocationHandler.getInstance(this));
    } else {
      final Class<?> returnType = method.getReturnType();

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {returnType},
              EntitySetInvocationHandler.getInstance(returnType, this));

    }
  }
}
