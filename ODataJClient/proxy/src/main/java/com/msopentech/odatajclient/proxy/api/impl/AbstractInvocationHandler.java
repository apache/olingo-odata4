/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy.api.impl;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataInvokeResult;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.EdmV3Type;
import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.api.annotations.Operation;
import com.msopentech.odatajclient.proxy.api.annotations.Parameter;
import com.msopentech.odatajclient.proxy.utils.ClassUtils;
import com.msopentech.odatajclient.proxy.utils.EngineUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

abstract class AbstractInvocationHandler implements InvocationHandler {

    private static final long serialVersionUID = 358520026931462958L;

    protected final ODataV3Client client;

    protected EntityContainerInvocationHandler containerHandler;

    protected AbstractInvocationHandler(
            final ODataV3Client client, final EntityContainerInvocationHandler containerHandler) {

        this.client = client;
        this.containerHandler = containerHandler;
    }

    protected ODataV3Client getClient() {
        return client;
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object getEntityCollection(
            final Class<?> typeRef,
            final Class<?> typeCollectionRef,
            final String entityContainerName,
            final ODataEntitySet entitySet,
            final URI uri,
            final boolean checkInTheContext) {

        final List<Object> items = new ArrayList<Object>();

        for (ODataEntity entityFromSet : entitySet.getEntities()) {
            items.add(getEntityProxy(
                    entityFromSet, entityContainerName, entitySet.getName(), typeRef, checkInTheContext));
        }

        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { typeCollectionRef },
                new EntityCollectionInvocationHandler(containerHandler, items, typeRef, entityContainerName, uri));
    }

    protected <T> T getEntityProxy(
            final ODataEntity entity,
            final String entityContainerName,
            final String entitySetName,
            final Class<?> type,
            final boolean checkInTheContext) {

        return getEntityProxy(entity, entityContainerName, entitySetName, type, null, checkInTheContext);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getEntityProxy(
            final ODataEntity entity,
            final String entityContainerName,
            final String entitySetName,
            final Class<?> type,
            final String eTag,
            final boolean checkInTheContext) {

        EntityTypeInvocationHandler handler = EntityTypeInvocationHandler.getInstance(
                entity, entityContainerName, entitySetName, type, containerHandler);

        if (StringUtils.isNotBlank(eTag)) {
            // override ETag into the wrapped object.
            handler.setETag(eTag);
        }

        if (checkInTheContext && EntityContainerFactory.getContext().entityContext().isAttached(handler)) {
            handler = EntityContainerFactory.getContext().entityContext().getEntity(handler.getUUID());
        }

        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { type },
                handler);
    }

    protected Object functionImport(
            final Operation annotation, final Method method, final Object[] args, final URI target,
            final com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport funcImp)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {

        // 1. invoke params (if present)
        final Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
        if (!ArrayUtils.isEmpty(args)) {
            final Annotation[][] parAnnots = method.getParameterAnnotations();
            final Class<?>[] parTypes = method.getParameterTypes();

            for (int i = 0; i < args.length; i++) {
                if (!(parAnnots[i][0] instanceof Parameter)) {
                    throw new IllegalArgumentException("Paramter " + i + " is not annotated as @Param");
                }
                final Parameter parAnnot = (Parameter) parAnnots[i][0];

                if (!parAnnot.nullable() && args[i] == null) {
                    throw new IllegalArgumentException(
                            "Parameter " + parAnnot.name() + " is not nullable but a null value was provided");
                }

                final ODataValue paramValue = args[i] == null
                        ? null
                        : EngineUtils.getODataValue(client, containerHandler.getFactory().getMetadata(),
                                new EdmV3Type(containerHandler.getFactory().getMetadata(), parAnnot.type()), args[i]);

                parameters.put(parAnnot.name(), paramValue);
            }
        }

        // 2. IMPORTANT: flush any pending change *before* invoke if this operation is side effecting
        if (annotation.isSideEffecting()) {
            new Container(client, containerHandler.getFactory()).flush();
        }

        // 3. invoke
        final ODataInvokeResult result = client.getInvokeRequestFactory().getInvokeRequest(
                target, containerHandler.getFactory().getMetadata(), funcImp, parameters).execute().getBody();

        // 3. process invoke result
        if (StringUtils.isBlank(annotation.returnType())) {
            return ClassUtils.returnVoid();
        }

        final EdmType edmType = new EdmV3Type(containerHandler.getFactory().getMetadata(), annotation.returnType());
        if (edmType.isEnumType()) {
            throw new UnsupportedOperationException("Usupported enum type " + edmType.getTypeExpression());
        }
        if (edmType.isSimpleType() || edmType.isComplexType()) {
            return EngineUtils.getValueFromProperty(
                    containerHandler.getFactory().getMetadata(), (ODataProperty) result, method.getGenericReturnType());
        }
        if (edmType.isEntityType()) {
            if (edmType.isCollection()) {
                final ParameterizedType collType = (ParameterizedType) method.getReturnType().getGenericInterfaces()[0];
                final Class<?> collItemType = (Class<?>) collType.getActualTypeArguments()[0];
                return getEntityCollection(
                        collItemType,
                        method.getReturnType(),
                        null,
                        (ODataEntitySet) result,
                        target,
                        false);
            } else {
                return getEntityProxy(
                        (ODataEntity) result,
                        null,
                        null,
                        method.getReturnType(),
                        false);
            }
        }

        throw new IllegalArgumentException("Could not process the functionImport information");
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
