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

import com.msopentech.odatajclient.proxy.api.AbstractEntityCollection;
import com.msopentech.odatajclient.proxy.api.annotations.Operation;
import com.msopentech.odatajclient.proxy.utils.ClassUtils;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang3.ArrayUtils;

public class EntityCollectionInvocationHandler<T extends Serializable>
        extends AbstractInvocationHandler implements AbstractEntityCollection<T> {

    private static final long serialVersionUID = 98078202642671726L;

    private final Collection<T> items;

    private final Class<?> itemRef;

    private final String entityContainerName;

    private final URI uri;

    public EntityCollectionInvocationHandler(final EntityContainerInvocationHandler containerHandler,
            final Collection<T> items, final Class<?> itemRef, final String entityContainerName) {

        this(containerHandler, items, itemRef, entityContainerName, null);
    }

    public EntityCollectionInvocationHandler(final EntityContainerInvocationHandler containerHandler,
            final Collection<T> items, final Class<?> itemRef, final String entityContainerName, final URI uri) {

        super(containerHandler.getClient(), containerHandler);

        this.items = items;
        this.itemRef = itemRef;
        this.entityContainerName = entityContainerName;
        this.uri = uri;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Annotation[] methodAnnots = method.getAnnotations();

        if (isSelfMethod(method, args)) {
            return invokeSelfMethod(method, args);
        } else if (!ArrayUtils.isEmpty(methodAnnots) && methodAnnots[0] instanceof Operation) {
            if (this.uri == null) {
                throw new IllegalStateException("This entity collection has not yet been flushed");
            }

            final com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer container =
                    containerHandler.getFactory().getMetadata().getSchema(ClassUtils.getNamespace(itemRef)).
                    getEntityContainer(entityContainerName);
            final com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport funcImp =
                    container.getFunctionImport(((Operation) methodAnnots[0]).name());

            return functionImport((Operation) methodAnnots[0], method, args,
                    client.getURIBuilder(this.uri.toASCIIString()).
                    appendFunctionImportSegment(((Operation) methodAnnots[0]).name()).build(),
                    funcImp);
        } else {
            throw new UnsupportedOperationException("Method not found: " + method);
        }
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(final Object object) {
        return items.contains(object);
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] array) {
        return items.toArray(array);
    }

    @Override
    public boolean add(final T element) {
        return items.add(element);
    }

    @Override
    public boolean remove(final Object object) {
        return items.remove(object);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return items.containsAll(collection);
    }

    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        return items.addAll(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return items.removeAll(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return items.retainAll(collection);
    }

    @Override
    public void clear() {
        items.clear();
    }
}
