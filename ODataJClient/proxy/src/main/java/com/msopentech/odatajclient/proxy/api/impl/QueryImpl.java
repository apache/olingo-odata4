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
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.uri.filter.ODataFilter;
import com.msopentech.odatajclient.proxy.api.AbstractEntityCollection;
import com.msopentech.odatajclient.proxy.api.NoResultException;
import com.msopentech.odatajclient.proxy.api.NonUniqueResultException;
import com.msopentech.odatajclient.proxy.api.Query;
import com.msopentech.odatajclient.proxy.api.Sort;
import com.msopentech.odatajclient.proxy.utils.ClassUtils;
import java.io.Serializable;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;

public class QueryImpl<T extends Serializable, EC extends AbstractEntityCollection<T>> implements Query<T, EC> {

    private static final long serialVersionUID = -300830736753191114L;

    private final ODataClient client;

    private final Class<T> typeRef;

    private final Class<EC> collTypeRef;

    private final EntitySetInvocationHandler handler;

    private final URI baseURI;

    private String filter;

    private String orderBy;

    private Integer maxResults;

    private Integer firstResult;

    @SuppressWarnings("unchecked")
    QueryImpl(final ODataClient client,
            final Class<EC> collTypeRef, final URI baseURI, final EntitySetInvocationHandler handler) {

        this.client = client;
        this.typeRef = (Class<T>) ClassUtils.extractTypeArg(collTypeRef);
        this.collTypeRef = collTypeRef;
        this.baseURI = baseURI;
        this.handler = handler;
    }

    @Override
    public Query<T, EC> setFilter(final String filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public Query<T, EC> setFilter(final ODataFilter filter) {
        this.filter = filter.build();
        return this;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public Query<T, EC> setOrderBy(final Sort... sort) {
        final StringBuilder builder = new StringBuilder();
        for (Sort sortClause : sort) {
            builder.append(sortClause.getKey()).append(' ').append(sortClause.getValue()).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);

        this.orderBy = builder.toString();
        return this;
    }

    @Override
    public Query<T, EC> setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public Query<T, EC> setMaxResults(final int maxResults) throws IllegalArgumentException {
        if (maxResults <= 0) {
            throw new IllegalArgumentException("maxResults must be positive");
        }

        this.maxResults = maxResults;
        return this;
    }

    @Override
    public int getMaxResults() {
        return maxResults;
    }

    @Override
    public Query<T, EC> setFirstResult(final int firstResult) throws IllegalArgumentException {
        if (firstResult <= 0) {
            throw new IllegalArgumentException("firstResult must be positive");
        }

        this.firstResult = firstResult;
        return this;
    }

    @Override
    public int getFirstResult() {
        return firstResult;
    }

    @Override
    public T getSingleResult() throws NoResultException, NonUniqueResultException {
        final EC result = getResult();
        if (result.isEmpty()) {
            throw new NoResultException();
        }
        if (result.size() > 1) {
            throw new NonUniqueResultException();
        }

        return result.iterator().next();
    }

    @Override
    @SuppressWarnings("unchecked")
    public EC getResult() {
        final URIBuilder uriBuilder = client.getURIBuilder(this.baseURI.toASCIIString()).
                appendStructuralSegment(ClassUtils.getNamespace(typeRef) + "." + ClassUtils.getEntityTypeName(typeRef));

        if (StringUtils.isNotBlank(filter)) {
            uriBuilder.filter(filter);
        }
        if (StringUtils.isNotBlank(orderBy)) {
            uriBuilder.orderBy(orderBy);
        }
        if (maxResults != null) {
            uriBuilder.top(maxResults);
        }
        if (firstResult != null) {
            uriBuilder.skip(firstResult);
        }

        return (EC) handler.fetchWholeEntitySet(uriBuilder.build(), typeRef, collTypeRef);
    }
}
