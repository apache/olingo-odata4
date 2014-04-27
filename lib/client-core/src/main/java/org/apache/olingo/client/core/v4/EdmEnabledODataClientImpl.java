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
package org.apache.olingo.client.core.v4;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.commons.api.edm.Edm;

public class EdmEnabledODataClientImpl extends ODataClientImpl implements EdmEnabledODataClient {

  private final String serviceRoot;

  private Edm edm;

  private String metadataETag;

  public EdmEnabledODataClientImpl(final String serviceRoot) {
    super();
    
    this.serviceRoot = serviceRoot;
    this.metadataETag = StringUtils.EMPTY;
  }

  @Override
  public String getServiceRoot() {
    return serviceRoot;
  }

  @Override
  public final Edm getEdm(final String metadataETag) {
    synchronized (this) {
      if (this.metadataETag != null && !this.metadataETag.equals(metadataETag)) {
        final EdmMetadataRequest metadataReq = getRetrieveRequestFactory().getMetadataRequest(serviceRoot);
        final ODataRetrieveResponse<Edm> metadataRes = metadataReq.execute();
        this.metadataETag = metadataRes.getETag();
        this.edm = metadataRes.getBody();
      }
    }
    return this.edm;
  }
}
