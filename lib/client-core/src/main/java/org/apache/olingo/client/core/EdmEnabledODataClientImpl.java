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
package org.apache.olingo.client.core;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.communication.request.invoke.EdmEnabledInvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.communication.request.invoke.EdmEnabledInvokeRequestFactoryImpl;
import org.apache.olingo.client.core.uri.URIBuilderImpl;
import org.apache.olingo.commons.api.edm.Edm;

public class EdmEnabledODataClientImpl extends ODataClientImpl implements EdmEnabledODataClient {

  private final String serviceRoot;

  private Edm edm;

  private String metadataETag;

  private EdmEnabledInvokeRequestFactory edmEnabledInvokeRequestFactory;

  public EdmEnabledODataClientImpl(final String serviceRoot, final Edm edm, final String metadataETag) {
    super();

    this.serviceRoot = serviceRoot;
    this.edm = edm;
    this.metadataETag = metadataETag;
  }

  @Override
  public String getServiceRoot() {
    return serviceRoot;
  }

  @Override
  public Edm getEdm(final String metadataETag) {
    synchronized (this) {
      if (this.edm == null || (metadataETag != null && !metadataETag.equals(this.metadataETag))) {
        final EdmMetadataRequest metadataReq = getRetrieveRequestFactory().getMetadataRequest(serviceRoot);
        final ODataRetrieveResponse<Edm> metadataRes = metadataReq.execute();
        this.metadataETag = metadataRes.getETag();
        this.edm = metadataRes.getBody();
      }
    }
    return this.edm;
  }

  @Override
  public Edm getCachedEdm() {
    if (this.edm == null) {
      getEdm(null);
    }
    return this.edm;
  }

  @Override
  public URIBuilder newURIBuilder() {
    return new URIBuilderImpl(configuration, serviceRoot);
  }

  @Override
  public EdmEnabledInvokeRequestFactory getInvokeRequestFactory() {
    if (edmEnabledInvokeRequestFactory == null) {
      edmEnabledInvokeRequestFactory = new EdmEnabledInvokeRequestFactoryImpl(this);
    }
    return edmEnabledInvokeRequestFactory;
  }
}
