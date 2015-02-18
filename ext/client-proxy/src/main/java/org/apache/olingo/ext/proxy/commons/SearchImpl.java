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

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.URISearch;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.Search;
import org.apache.olingo.ext.proxy.utils.ClassUtils;

public class SearchImpl<T extends EntityType<?>, EC extends EntityCollection<T, ?, ?>> implements Search<T, EC> {

  private static final long serialVersionUID = 4383858176507769973L;

  private final EdmEnabledODataClient client;

  private final Class<T> typeRef;

  private final Class<EC> collTypeRef;

  private final EntitySetInvocationHandler<T, ?, EC> handler;

  private final URI baseURI;

  private String search;

  @SuppressWarnings("unchecked")
  SearchImpl(EdmEnabledODataClient client,
          final Class<EC> collTypeRef, final URI baseURI, final EntitySetInvocationHandler<T, ?, EC> handler) {

    this.client = client;
    this.typeRef = (Class<T>) ClassUtils.extractTypeArg(collTypeRef, EntityCollection.class);
    this.collTypeRef = collTypeRef;
    this.baseURI = baseURI;
    this.handler = handler;
  }

  @Override
  public Search<T, EC> setSearch(final String search) {
    this.search = search;
    return this;
  }

  @Override
  public Search<T, EC> setSearch(final URISearch search) {
    this.search = search.build();
    return this;
  }

  @Override
  public String getSearch() {
    return search;
  }

  @Override
  public EC getResult() {
    final URIBuilder uriBuilder = client.newURIBuilder(this.baseURI.toASCIIString()).
            appendDerivedEntityTypeSegment(new FullQualifiedName(
            ClassUtils.getNamespace(typeRef), ClassUtils.getEntityTypeName(typeRef)).toString());

    if (StringUtils.isNotBlank(search)) {
      uriBuilder.search(search);
    }

    return handler.fetchWholeEntitySet(uriBuilder, typeRef, collTypeRef);
  }
}
