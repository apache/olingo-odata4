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

import java.io.Serializable;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.api.AbstractEntityCollection;
import org.apache.olingo.ext.proxy.api.NoResultException;
import org.apache.olingo.ext.proxy.api.NonUniqueResultException;
import org.apache.olingo.ext.proxy.api.Filter;
import org.apache.olingo.ext.proxy.api.Sort;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public class FilterImpl<T extends Serializable, EC extends AbstractEntityCollection<T>> implements Filter<T, EC> {

  private static final long serialVersionUID = -300830736753191114L;

  private final CommonODataClient<?> client;

  private final Class<T> typeRef;

  private final Class<EC> collTypeRef;

  private final EntitySetInvocationHandler<T, ?, EC> handler;

  private final URI baseURI;

  private String filter;

  private String orderBy;

  private Integer maxResults;

  private Integer firstResult;

  @SuppressWarnings("unchecked")
  FilterImpl(final CommonODataClient<?> client,
          final Class<EC> collTypeRef, final URI baseURI, final EntitySetInvocationHandler<T, ?, EC> handler) {

    this.client = client;
    this.typeRef = (Class<T>) ClassUtils.extractTypeArg(collTypeRef);
    this.collTypeRef = collTypeRef;
    this.baseURI = baseURI;
    this.handler = handler;
  }

  @Override
  public Filter<T, EC> setFilter(final String filter) {
    this.filter = filter;
    return this;
  }

  @Override
  public Filter<T, EC> setFilter(final URIFilter filter) {
    this.filter = filter.build();
    return this;
  }

  @Override
  public String getFilter() {
    return filter;
  }

  @Override
  public Filter<T, EC> setOrderBy(final Sort... sort) {
    final StringBuilder builder = new StringBuilder();
    for (Sort sortClause : sort) {
      builder.append(sortClause.getKey()).append(' ').append(sortClause.getValue()).append(',');
    }
    builder.deleteCharAt(builder.length() - 1);

    this.orderBy = builder.toString();
    return this;
  }

  @Override
  public Filter<T, EC> setOrderBy(final String orderBy) {
    this.orderBy = orderBy;
    return this;
  }

  @Override
  public String getOrderBy() {
    return orderBy;
  }

  @Override
  public Filter<T, EC> setMaxResults(final int maxResults) throws IllegalArgumentException {
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
  public Filter<T, EC> setFirstResult(final int firstResult) throws IllegalArgumentException {
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
  public EC getResult() {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(this.baseURI.toASCIIString()).
            appendDerivedEntityTypeSegment(new FullQualifiedName(
                            ClassUtils.getNamespace(typeRef), ClassUtils.getEntityTypeName(typeRef)).toString());

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

    return handler.fetchWholeEntitySet(uriBuilder.build(), typeRef, collTypeRef);
  }
}
