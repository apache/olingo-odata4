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
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.Operations;
import org.apache.olingo.ext.proxy.api.annotations.Operation;

public class StructuredComposableInvokerInvocationHandler<T, O extends Operations>
        extends InvokerInvocationHandler<T, O> {

  private AbstractStructuredInvocationHandler structuredHandler;

  public StructuredComposableInvokerInvocationHandler(
          final URI uri,
          final Map<String, ClientValue> parameters,
          final Operation operation,
          final EdmOperation edmOperation,
          final Type[] references,
          final EdmTypeInfo returnType,
          final AbstractService<?> service) {

    super(uri, parameters, operation, edmOperation, references, service);

    if (!edmOperation.getReturnType().isCollection()) {
      if (returnType.isEntityType()) {
        this.structuredHandler = EntityInvocationHandler.getInstance(
                uri, targetRef, service);
      }
      if (returnType.isComplexType()) {
        this.structuredHandler = ComplexInvocationHandler.getInstance(
                targetRef, service, service.getClient().newURIBuilder(uri.toASCIIString()));
      }
    }
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    if (method.getName().startsWith("get")) {
      return structuredHandler.invoke(proxy, method, args);
    } else if (method.getName().startsWith("set")) {
      return structuredHandler.invoke(proxy, method, args);
    } else if ("filter".equals(method.getName())
            || "orderBy".equals(method.getName())
            || "top".equals(method.getName())
            || "skip".equals(method.getName())
            || "expand".equals(method.getName())
            || "select".equals(method.getName())) {

      return super.invoke(proxy, method, args);
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      return super.invoke(proxy, method, args);
    } else if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

}
