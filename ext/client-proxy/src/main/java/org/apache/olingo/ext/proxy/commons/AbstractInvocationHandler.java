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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.http.HttpMethod;
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
import org.apache.olingo.ext.proxy.Service;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.Context;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
import org.apache.olingo.ext.proxy.utils.CoreUtils;

abstract class AbstractInvocationHandler implements InvocationHandler {

  private static final long serialVersionUID = 358520026931462958L;

  protected Service<?> factory;

  protected EntityContainerInvocationHandler containerHandler;

  protected AbstractInvocationHandler(final Service<?> factory) {
    this.factory = factory;
  }

  protected AbstractInvocationHandler(final EntityContainerInvocationHandler containerHandler) {
    this.containerHandler = containerHandler;
  }

  protected CommonEdmEnabledODataClient<?> getClient() {
    return factory == null ? containerHandler.getFactory().getClient() : factory.getClient();
  }

  protected Context getContext() {
    return factory == null ? containerHandler.getFactory().getContext() : factory.getContext();
  }

  protected boolean isSelfMethod(final Method method, final Object[] args) {
    final Method[] selfMethods = getClass().getMethods();

    boolean result = false;

    for (int i = 0; i < selfMethods.length && !result; i++) {
      result = method.getName().equals(selfMethods[i].getName())
              && Arrays.equals(method.getParameterTypes(), selfMethods[i].getParameterTypes());
    }

    return result;
  }

  protected Object invokeSelfMethod(final Method method, final Object[] args)
          throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return getClass().getMethod(method.getName(), method.getParameterTypes()).invoke(this, args);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  protected Object getEntityCollectionProxy(
          final Class<?> typeRef,
          final Class<?> typeCollectionRef,
          final String entityContainerName,
          final CommonODataEntitySet entitySet,
          final URI uri,
          final boolean checkInTheContext) {

    final List<Object> items = new ArrayList<Object>();

    for (CommonODataEntity entityFromSet : entitySet.getEntities()) {
      items.add(getEntityProxy(entityFromSet, entityContainerName, null, typeRef, null, checkInTheContext));
    }

    return Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {typeCollectionRef},
            new EntityCollectionInvocationHandler(containerHandler, items, typeRef,
            uri == null ? null : getClient().newURIBuilder(uri.toASCIIString())));
  }

  protected Object getEntitySetProxy(
          final Class<?> typeRef,
          final URI uri) {

    return Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {typeRef},
            EntitySetInvocationHandler.getInstance(typeRef, containerHandler, uri));
  }

  protected Object getEntityProxy(
          final CommonODataEntity entity,
          final String entityContainerName,
          final URI entitySetURI,
          final Class<?> type,
          final String eTag,
          final boolean checkInTheContext) {

    EntityInvocationHandler handler = EntityInvocationHandler.getInstance(entity, entitySetURI, type, containerHandler);

    if (StringUtils.isNotBlank(eTag)) {
      // override ETag into the wrapped object.
      handler.setETag(eTag);
    }

    if (checkInTheContext && getContext().entityContext().isAttached(handler)) {
      handler = getContext().entityContext().getEntity(handler.getUUID());
    } else {
      handler.attach(AttachedEntityStatus.ATTACHED, false);
    }

    return Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class<?>[] {type},
            handler);
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

  protected Object invokeOperation(
          final Operation annotation,
          final Method method,
          final LinkedHashMap<Parameter, Object> parameters,
          final URI target,
          final EdmOperation edmOperation)
          throws InstantiationException, IllegalAccessException, NoSuchMethodException,
          IllegalArgumentException, InvocationTargetException {

    // 1. invoke params (if present)
    final Map<String, ODataValue> parameterValues = new LinkedHashMap<String, ODataValue>();
    if (!parameters.isEmpty()) {
      for (Map.Entry<Parameter, Object> parameter : parameters.entrySet()) {

        if (!parameter.getKey().nullable() && parameter.getValue() == null) {
          throw new IllegalArgumentException(
                  "Parameter " + parameter.getKey().name() + " is not nullable but a null value was provided");
        }

        final EdmTypeInfo type = new EdmTypeInfo.Builder().
                setEdm(getClient().getCachedEdm()).setTypeExpression(parameter.getKey().type()).build();

        final ODataValue paramValue = parameter.getValue() == null
                ? null
                : CoreUtils.getODataValue(getClient(), type, parameter.getValue());

        parameterValues.put(parameter.getKey().name(), paramValue);
      }
    }

    // 2. IMPORTANT: flush any pending change *before* invoke if this operation is side effecting
    if (annotation.type() == OperationType.ACTION) {
      containerHandler.getFactory().getPersistenceManager().flush();
    }

    // 3. invoke
    final ODataInvokeResult result = getClient().getInvokeRequestFactory().getInvokeRequest(
            edmOperation instanceof EdmFunction ? HttpMethod.GET : HttpMethod.POST,
            target,
            getResultReference(edmOperation.getReturnType()),
            parameterValues).
            execute().getBody();

    // 4. process invoke result
    if (StringUtils.isBlank(annotation.returnType())) {
      return ClassUtils.returnVoid();
    }

    final EdmTypeInfo edmType = new EdmTypeInfo.Builder().
            setEdm(getClient().getCachedEdm()).setTypeExpression(annotation.returnType()).build();

    if (edmType.isEntityType()) {
      if (edmType.isCollection()) {
        final ParameterizedType collType = (ParameterizedType) method.getReturnType().getGenericInterfaces()[0];
        final Class<?> collItemType = (Class<?>) collType.getActualTypeArguments()[0];
        return getEntityCollectionProxy(
                collItemType,
                method.getReturnType(),
                null,
                (CommonODataEntitySet) result,
                target,
                false);
      } else {
        return getEntityProxy(
                (CommonODataEntity) result,
                null,
                null,
                method.getReturnType(),
                null,
                false);
      }
    } else {
      final CommonODataProperty property = (CommonODataProperty) result;
      return property == null || property.hasNullValue()
              ? null
              : CoreUtils.getObjectFromODataValue(
              getClient(), property.getValue(), method.getGenericReturnType(), null);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
