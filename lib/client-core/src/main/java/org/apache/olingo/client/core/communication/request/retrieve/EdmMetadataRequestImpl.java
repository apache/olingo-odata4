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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.net.URI;
import java.util.Map;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.commons.api.edm.Edm;

/**
 * This class implements a metadata query request.
 */
class EdmMetadataRequestImpl extends AbstractMetadataRequestImpl<Edm> implements EdmMetadataRequest {

  private final String serviceRoot;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri metadata URI.
   */
  EdmMetadataRequestImpl(final CommonODataClient<?> odataClient, final String serviceRoot, final URI uri) {
    super(odataClient, uri);
    this.serviceRoot = serviceRoot;
  }

  @Override
  public ODataRetrieveResponse<Edm> execute() {
    final ODataRetrieveResponse<Map<String, Schema>> xmlMetadataResponse =
            odataClient.getRetrieveRequestFactory().getXMLMetadataRequest(serviceRoot).execute();

    return new AbstractODataRetrieveResponse() {
      private Edm metadata = null;

      @Override
      public void close() {
        xmlMetadataResponse.close();
      }

      @Override
      public Edm getBody() {
        if (metadata == null) {
          try {
            metadata = odataClient.getReader().readMetadata(xmlMetadataResponse.getBody());
          } finally {
            this.close();
          }
        }
        return metadata;
      }
    };
  }
}
