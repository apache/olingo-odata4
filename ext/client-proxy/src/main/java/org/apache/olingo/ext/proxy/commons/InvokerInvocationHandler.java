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
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.invoke.ClientNoContent;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.Operations;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.apache.olingo.ext.proxy.utils.ProxyUtils;

public class InvokerInvocationHandler<T, O extends Operations> extends AbstractInvocationHandler {

  private final URI baseURI;

  private URIBuilder uri;

  private final Map<String, ClientValue> parameters;

  private final Operation operation;

  private final EdmOperation edmOperation;

  protected final Class<T> targetRef;

  private final Class<?> operationRef;

  public InvokerInvocationHandler(
          final URI uri,
          final Map<String, ClientValue> parameters,
          final Operation operation,
          final EdmOperation edmOperation,
          final Type[] references,
          final AbstractService<?> service) {

    super(service);

    this.baseURI = uri;
    this.uri = this.baseURI == null ? null : service.getClient().newURIBuilder(this.baseURI.toASCIIString());
    this.parameters = parameters;
    this.operation = operation;
    this.edmOperation = edmOperation;
    if (references.length > 0) {
      this.targetRef = ClassUtils.<T>getTypeClass(references[0]);
      this.operationRef = references.length > 1 ? ClassUtils.<T>getTypeClass(references[1]) : null;
    } else {
      this.targetRef = null;
      this.operationRef = null;
    }
  }

  public Future<T> executeAsync() {
    return service.getClient().getConfiguration().getExecutor().submit(new Callable<T>() {

      @Override
      public T call() throws Exception {
        return execute();
      }
    });
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public T execute() {
    if (operation == null || uri == null) {
      throw new IllegalStateException("Invalid operation");
    }

    try {
      // 1. IMPORTANT: flush any pending change *before* invoke if this operation is side effecting
      if (operation.type() == OperationType.ACTION) {
        service.getPersistenceManager().flush();
      }

      // 2. invoke
      final ClientInvokeResult result = service.getClient().getInvokeRequestFactory().getInvokeRequest(
              edmOperation instanceof EdmFunction ? HttpMethod.GET : HttpMethod.POST,
              uri.build(),
              getResultReference(edmOperation.getReturnType()),
              parameters).
              execute().getBody();

      // 3. process invoke result
      if (StringUtils.isBlank(operation.returnType())) {
        return (T) ClassUtils.returnVoid();
      }

      final EdmTypeInfo returnType = new EdmTypeInfo.Builder().
              setEdm(service.getClient().getCachedEdm()).setTypeExpression(operation.returnType()).build();

      if (returnType.isEntityType()) {
        if (returnType.isCollection()) {
          final Class<?> collItemType = ClassUtils.extractTypeArg(targetRef, EntityCollection.class);
          return (T) ProxyUtils.getEntityCollectionProxy(
                  service,
                  collItemType,
                  targetRef,
                  null,
                  (ClientEntitySet) result,
                  this.baseURI,
                  false);
        } else {
          return (T) ProxyUtils.getEntityProxy(
                  service,
                  (ClientEntity) result,
                  null,
                  targetRef,
                  null,
                  false);
        }
      } else {
        Object res;

        final Class<?> ref = ClassUtils.getTypeClass(targetRef);
        final ClientProperty property = (ClientProperty) result;

        if (property == null || property.hasNullValue()) {
          res = null;
        } else if (returnType.isCollection()) {
          if (returnType.isComplexType()) {
            final Class<?> itemRef = ClassUtils.extractTypeArg(ref, ComplexCollection.class);
            final List items = new ArrayList();

            for (ClientValue item : property.getValue().asCollection()) {
              items.add(ProxyUtils.getComplexProxy(
                      service,
                      property.getName(),
                      item,
                      itemRef,
                      null,
                      null,
                      true));
            }

            res = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] {ref}, new ComplexCollectionInvocationHandler(
                            service,
                            items,
                            itemRef,
                            null));
          } else {
            final List items = new ArrayList();

            for (ClientValue item : property.getValue().asCollection()) {
              items.add(item.asPrimitive().toValue());
            }

            res = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] {PrimitiveCollection.class}, new PrimitiveCollectionInvocationHandler(
                            service,
                            items,
                            null,
                            null));
          }
        } else {
          if (returnType.isComplexType()) {
            res = ProxyUtils.getComplexProxy(
                    service, property.getName(), property.getValue().asComplex(), ref, null, null, false);
          } else {
            res = CoreUtils.getObjectFromODataValue(property.getValue(), targetRef, service);
          }
        }

        return (T) res;
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private <RES extends ClientInvokeResult> Class<RES> getResultReference(final EdmReturnType returnType) {
    Class<RES> result;

    if (returnType == null) {
      result = (Class<RES>) ClientNoContent.class;
    } else {
      if (returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (Class<RES>) ClientEntitySet.class;
      } else if (!returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (Class<RES>) ClientEntity.class;
      } else {
        result = (Class<RES>) ClientProperty.class;
      }
    }

    return result;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("filter".equals(method.getName())
            || "orderBy".equals(method.getName())
            || "top".equals(method.getName())
            || "skip".equals(method.getName())
            || "expand".equals(method.getName())
            || "select".equals(method.getName())) {
      invokeSelfMethod(method, args);
      return proxy;
    } else if ("operations".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
      final EdmTypeInfo returnType = new EdmTypeInfo.Builder().
              setEdm(service.getClient().getCachedEdm()).setTypeExpression(operation.returnType()).build();

      final URI prefixURI = URIUtils.buildFunctionInvokeURI(this.baseURI, parameters);

      OperationInvocationHandler handler;

      if (returnType.isComplexType()) {
        if (returnType.isCollection()) {
          handler = OperationInvocationHandler.getInstance(new ComplexCollectionInvocationHandler(
                  targetRef, service, getClient().newURIBuilder(prefixURI.toASCIIString())));
        } else {
          handler = OperationInvocationHandler.getInstance(ComplexInvocationHandler.getInstance(
                  targetRef, service, getClient().newURIBuilder(prefixURI.toASCIIString())));
        }
      } else {
        if (returnType.isCollection()) {
          handler = OperationInvocationHandler.getInstance(new EntityCollectionInvocationHandler(
                  service, null, targetRef, null, getClient().newURIBuilder(prefixURI.toASCIIString())));
        } else {
          handler = OperationInvocationHandler.getInstance(EntityInvocationHandler.getInstance(
                  prefixURI, targetRef, service));
        }
      }

      return Proxy.newProxyInstance(
              Thread.currentThread().getContextClassLoader(),
              new Class<?>[] {operationRef}, handler);
    } else if (isSelfMethod(method)) {
      return invokeSelfMethod(method, args);
    } else {
      throw new NoSuchMethodException(method.getName());
    }
  }

  public void filter(final String filter) {
    if (this.uri != null) {
      this.uri.filter(filter);
    }
  }

  public void filter(final URIFilter filter) {
    if (this.uri != null) {
      this.uri.filter(filter);
    }
  }

  public void orderBy(final Sort... sort) {
    if (this.uri != null) {
      final StringBuilder builder = new StringBuilder();
      for (Sort sortClause : sort) {
        builder.append(sortClause.getKey()).append(' ').append(sortClause.getValue()).append(',');
      }
      builder.deleteCharAt(builder.length() - 1);

      this.uri.orderBy(builder.toString());
    }
  }

  public void orderBy(final String orderBy) {
    if (this.uri != null) {
      this.uri.orderBy(orderBy);
    }
  }

  public void top(final int top) throws IllegalArgumentException {
    if (this.uri != null) {
      this.uri.top(top);
    }
  }

  public void skip(final int skip) throws IllegalArgumentException {
    if (this.uri != null) {
      this.uri.skip(skip);
    }
  }

  public void expand(final String... expand) {
    if (this.uri != null) {
      this.uri.expand(expand);
    }
  }

  public void select(final String... select) {
    if (this.uri != null) {
      this.uri.select(select);
    }
  }

  public void clearQueryOptions() {
    this.uri = this.baseURI == null ? null : getClient().newURIBuilder(baseURI.toASCIIString());
  }
}
