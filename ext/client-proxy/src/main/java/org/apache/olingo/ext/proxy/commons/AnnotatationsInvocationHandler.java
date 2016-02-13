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

import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.olingo.ext.proxy.AbstractService;

public class AnnotatationsInvocationHandler extends AbstractInvocationHandler {

  private final EntityInvocationHandler entityHandler;

  private final AbstractStructuredInvocationHandler targetHandler;

  static AnnotatationsInvocationHandler getInstance(
          final EntityInvocationHandler entityHandler,
          final AbstractStructuredInvocationHandler targetHandler) {

    return new AnnotatationsInvocationHandler(
            targetHandler == null
            ? entityHandler == null ? null : entityHandler.service : targetHandler.service,
            entityHandler,
            targetHandler);
  }

  private AnnotatationsInvocationHandler(
          final AbstractService<?> service,
          final EntityInvocationHandler entityHandler,
          final AbstractStructuredInvocationHandler targetHandler) {

    super(service);
    this.targetHandler = targetHandler;
    this.entityHandler = entityHandler;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else if (method.getName().startsWith("get") && method.getName().endsWith("Annotations")) {
      final Method getter = proxy.getClass().getInterfaces()[0].getMethod(method.getName());

      String propName = null;
      String navPropName = null;

      final AnnotationsForProperty annForProp = ClassUtils.getAnnotation(AnnotationsForProperty.class, getter);
      if (annForProp == null) {
        final AnnotationsForNavigationProperty annForNavProp =
                ClassUtils.getAnnotation(AnnotationsForNavigationProperty.class, getter);
        if (annForNavProp == null) {
          throw new UnsupportedOperationException("Unsupported method " + method.getName());
        }

        navPropName = annForNavProp.name();
      } else {
        propName = annForProp.name();
      }

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {method.getReturnType()},
              new AnnotatableInvocationHandler(service, propName, navPropName, entityHandler, targetHandler));
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }
}
