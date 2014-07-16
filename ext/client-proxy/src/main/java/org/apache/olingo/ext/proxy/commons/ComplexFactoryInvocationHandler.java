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

import org.apache.olingo.ext.proxy.api.OperationExecutor;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class ComplexFactoryInvocationHandler extends AbstractInvocationHandler implements OperationExecutor {

  private static final long serialVersionUID = 2629912294765040027L;

  private final EntityInvocationHandler entityHandler;

  private final AbstractStructuredInvocationHandler invokerHandler;

  static ComplexFactoryInvocationHandler getInstance(
          final EntityContainerInvocationHandler containerHandler,
          final EntityInvocationHandler entityHandler,
          final AbstractStructuredInvocationHandler targetHandler) {

    return new ComplexFactoryInvocationHandler(containerHandler, entityHandler, targetHandler);
  }

  static ComplexFactoryInvocationHandler getInstance(
          final EntityInvocationHandler entityHandler,
          final AbstractStructuredInvocationHandler targetHandler) {

    return new ComplexFactoryInvocationHandler(
            targetHandler == null
            ? entityHandler == null
            ? null
            : entityHandler.containerHandler
            : targetHandler.containerHandler,
            entityHandler,
            targetHandler);
  }

  private ComplexFactoryInvocationHandler(
          final EntityContainerInvocationHandler containerHandler,
          final EntityInvocationHandler entityHandler,
          final AbstractStructuredInvocationHandler targetHandler) {

    super(containerHandler);
    this.invokerHandler = targetHandler;
    this.entityHandler = entityHandler;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method, args)) {
      return invokeSelfMethod(method, args);
    } else if (method.getName().startsWith("new")) {
      final Method getter = proxy.getClass().getInterfaces()[0].getMethod(method.getName());
      final Property property = ClassUtils.getAnnotation(Property.class, getter);
      if (property == null) {
        throw new UnsupportedOperationException("Unsupported method " + method.getName());
      }

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {method.getReturnType()},
              entityHandler == null
              ? ComplexInvocationHandler.getInstance(
                      getClient(), property.name(), method.getReturnType(), containerHandler)
              : ComplexInvocationHandler.getInstance(
                      getClient(), property.name(), method.getReturnType(), entityHandler));
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }
}
