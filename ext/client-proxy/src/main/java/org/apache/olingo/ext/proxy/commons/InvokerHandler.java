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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmOperation;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.apache.olingo.ext.proxy.utils.ProxyUtils;

public class InvokerHandler<T, C> extends AbstractInvocationHandler {

  private final URI baseURI;

  private CommonURIBuilder<?> uri;

  private final Map<String, ODataValue> parameters;

  private final Operation operation;

  private final EdmOperation edmOperation;

  private final Class<T> targetRef;

  private final Class<?> operationRef;

  public InvokerHandler(
          final URI uri,
          final Map<String, ODataValue> parameters,
          final Operation operation,
          final EdmOperation edmOperation,
          final Type[] references,
          final AbstractService<?> service) {

    super(service);

    this.baseURI = uri;
    this.uri = this.baseURI == null ? null : service.getClient().newURIBuilder(this.baseURI.toASCIIString());
    this.parameters = parameters;
    this.edmOperation = edmOperation;
    this.operation = operation;
    if (references.length > 0) {
      this.targetRef = ClassUtils.<T>getTypeClass(references[0]);
      this.operationRef = references.length > 1 ? ClassUtils.<T>getTypeClass(references[1]) : null;
    } else {
      this.targetRef = null;
      this.operationRef = null;
    }
  }

  public C compose() {
    return null;
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
      final ODataInvokeResult result = service.getClient().getInvokeRequestFactory().getInvokeRequest(
              edmOperation instanceof EdmFunction ? HttpMethod.GET : HttpMethod.POST,
              uri.build(),
              getResultReference(edmOperation.getReturnType()),
              parameters).
              execute().getBody();

      // 3. process invoke result
      if (StringUtils.isBlank(operation.returnType())) {
        return (T) ClassUtils.returnVoid();
      }

      final EdmTypeInfo edmType = new EdmTypeInfo.Builder().
              setEdm(service.getClient().getCachedEdm()).setTypeExpression(operation.returnType()).build();

      if (edmType.isEntityType()) {
        if (edmType.isCollection()) {
          final Class<?> collItemType = ClassUtils.extractTypeArg(targetRef, EntityCollection.class);
          return (T) ProxyUtils.getEntityCollectionProxy(
                  service,
                  collItemType,
                  targetRef,
                  null,
                  (CommonODataEntitySet) result,
                  this.baseURI,
                  false);
        } else {
          return (T) ProxyUtils.getEntityProxy(
                  service,
                  (CommonODataEntity) result,
                  null,
                  targetRef,
                  null,
                  false);
        }
      } else {
        Object res;

        final Class<?> ref = ClassUtils.getTypeClass(targetRef);
        final CommonODataProperty property = (CommonODataProperty) result;

        if (property == null || property.hasNullValue()) {
          res = null;
        } else if (edmType.isCollection()) {
          if (edmType.isComplexType()) {
            final Class<?> itemRef = ClassUtils.extractTypeArg(ref, ComplexCollection.class);
            final List items = new ArrayList();

            for (ODataValue item : property.getValue().asCollection()) {
              items.add(ProxyUtils.getComplex(
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

            for (ODataValue item : property.getValue().asCollection()) {
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
          if (edmType.isComplexType()) {
            res = ProxyUtils.getComplex(
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
  private <RES extends ODataInvokeResult> Class<RES> getResultReference(final EdmReturnType returnType) {
    Class<RES> result;

    if (returnType == null) {
      result = (Class<RES>) ODataNoContent.class;
    } else {
      if (returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (Class<RES>) CommonODataEntitySet.class;
      } else if (!returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (Class<RES>) CommonODataEntity.class;
      } else {
        result = (Class<RES>) CommonODataProperty.class;
      }
    }

    return result;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtype"})
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if ("filter".equals(method.getName())
            || "orderBy".equals(method.getName())
            || "top".equals(method.getName())
            || "skip".equals(method.getName())
            || "expand".equals(method.getName())
            || "select".equals(method.getName())) {
      invokeSelfMethod(method, args);
      return proxy;
    } else if ("compose".equals(method.getName()) && ArrayUtils.isEmpty(args)) {

      final EdmTypeInfo edmType = new EdmTypeInfo.Builder().
              setEdm(service.getClient().getCachedEdm()).setTypeExpression(operation.returnType()).build();

      final OperationInvocationHandler handler;

      final URI prefixURI = URIUtils.buildInvokeRequestURI(this.baseURI, parameters, getClient().getServiceVersion());

      if (edmType.isComplexType()) {
        if (edmType.isCollection()) {
          handler = OperationInvocationHandler.getInstance(new ComplexCollectionInvocationHandler(
                  service, targetRef, getClient().newURIBuilder(prefixURI.toASCIIString())));
        } else {
          handler = OperationInvocationHandler.getInstance(ComplexInvocationHandler.getInstance(
                  targetRef, service, getClient().newURIBuilder(prefixURI.toASCIIString())));
        }
      } else {
        if (edmType.isCollection()) {
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

    } else if (isSelfMethod(method, args)) {
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
