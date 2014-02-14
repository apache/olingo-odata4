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

import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import com.msopentech.odatajclient.proxy.api.EntityContainerFactory;
import com.msopentech.odatajclient.proxy.api.annotations.EntityContainer;
import com.msopentech.odatajclient.proxy.api.annotations.Operation;
import com.msopentech.odatajclient.proxy.utils.ClassUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.lang3.ArrayUtils;

public class EntityContainerInvocationHandler extends AbstractInvocationHandler {

    private static final long serialVersionUID = 7379006755693410764L;

    private final EntityContainerFactory factory;

    protected final String schemaName;

    private final String entityContainerName;

    private final boolean defaultEC;

    public static EntityContainerInvocationHandler getInstance(
            final ODataV3Client client, final Class<?> ref, final EntityContainerFactory factory) {

        final EntityContainerInvocationHandler instance = new EntityContainerInvocationHandler(client, ref, factory);
        instance.containerHandler = instance;
        return instance;
    }

    private EntityContainerInvocationHandler(
            final ODataV3Client client, final Class<?> ref, final EntityContainerFactory factory) {

        super(client, null);

        final Annotation annotation = ref.getAnnotation(EntityContainer.class);
        if (!(annotation instanceof EntityContainer)) {
            throw new IllegalArgumentException(ref.getName()
                    + " is not annotated as @" + EntityContainer.class.getSimpleName());
        }

        this.factory = factory;
        this.entityContainerName = ((EntityContainer) annotation).name();
        this.defaultEC = ((EntityContainer) annotation).isDefaultEntityContainer();
        this.schemaName = ClassUtils.getNamespace(ref);
    }

    EntityContainerFactory getFactory() {
        return factory;
    }

    boolean isDefaultEntityContainer() {
        return defaultEC;
    }

    String getEntityContainerName() {
        return entityContainerName;
    }

    String getSchemaName() {
        return schemaName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (isSelfMethod(method, args)) {
            return invokeSelfMethod(method, args);
        } else if ("flush".equals(method.getName()) && ArrayUtils.isEmpty(args)) {
            new Container(client, factory).flush();
            return ClassUtils.returnVoid();
        } else {
            final Annotation[] methodAnnots = method.getAnnotations();
            // 1. access top-level entity sets
            if (methodAnnots.length == 0) {
                final Class<?> returnType = method.getReturnType();

                return Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class<?>[] { returnType },
                        EntitySetInvocationHandler.getInstance(returnType, this));
            } // 2. invoke function imports
            else if (methodAnnots[0] instanceof Operation) {
                final com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer container =
                        getFactory().getMetadata().getSchema(schemaName).getEntityContainer(entityContainerName);
                final com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport funcImp =
                        container.getFunctionImport(((Operation) methodAnnots[0]).name());

                final URIBuilder uriBuilder = client.getURIBuilder(factory.getServiceRoot()).
                        appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

                return functionImport((Operation) methodAnnots[0], method, args, uriBuilder.build(), funcImp);
            } else {
                throw new UnsupportedOperationException("Method not found: " + method);
            }
        }
    }
}
