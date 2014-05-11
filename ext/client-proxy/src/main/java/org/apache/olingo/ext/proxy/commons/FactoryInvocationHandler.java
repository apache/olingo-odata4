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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.ext.proxy.api.OperationExecutor;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FactoryInvocationHandler<C extends CommonEdmEnabledODataClient<?>> extends AbstractInvocationHandler<C>
        implements OperationExecutor {

  private static final long serialVersionUID = 2629912294765040027L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(FactoryInvocationHandler.class);

  private final EntityTypeInvocationHandler<C> entityHandler;

  private final AbstractTypeInvocationHandler<C> invokerHandler;

  @SuppressWarnings({"rawtypes", "unchecked"})
  static FactoryInvocationHandler<?> getInstance(
          final EntityTypeInvocationHandler<?> entityHandler,
          final AbstractTypeInvocationHandler<?> targetHandler) {
    return new FactoryInvocationHandler(entityHandler, targetHandler);
  }

  @SuppressWarnings("unchecked")
  private FactoryInvocationHandler(
          final EntityTypeInvocationHandler<C> entityHandler,
          final AbstractTypeInvocationHandler<C> targetHandler) {
    super(targetHandler.containerHandler.getClient(), targetHandler.containerHandler);
    this.invokerHandler = targetHandler;
    this.entityHandler = entityHandler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if (method.getName().startsWith("new")) {
      final String getterName = method.getName().replaceFirst("new", "get");
      final Method getter = invokerHandler.getTypeRef().getMethod(getterName);
      final Property property = ClassUtils.getAnnotation(Property.class, getter);
      if (property == null) {
        throw new UnsupportedOperationException("Unsupported method " + method.getName());
      }

      final ComplexTypeInvocationHandler<?> complexTypeHandler =
              ComplexTypeInvocationHandler.getInstance(client, property.name(), method.getReturnType(), entityHandler);

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {method.getReturnType()},
              complexTypeHandler);
    } else {
      throw new UnsupportedOperationException("Method not found: " + method);
    }
  }
}
