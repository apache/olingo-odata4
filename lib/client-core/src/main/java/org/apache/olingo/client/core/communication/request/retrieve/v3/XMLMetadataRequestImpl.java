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
package org.apache.olingo.client.core.communication.request.retrieve.v3;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractMetadataRequestImpl;

public class XMLMetadataRequestImpl extends AbstractMetadataRequestImpl<Map<String, Schema>>
        implements XMLMetadataRequest {

  XMLMetadataRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ODataRetrieveResponse<Map<String, Schema>> execute() {
    return new XMLMetadataResponseImpl(httpClient, doExecute());
  }

  public class XMLMetadataResponseImpl extends AbstractODataRetrieveResponse {

    private Map<String, Schema> schemas;

    /**
     * Constructor.
     * <br/>
     * Just to create response templates to be initialized from batch.
     */
    private XMLMetadataResponseImpl() {
      super();
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private XMLMetadataResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    @Override
    public Map<String, Schema> getBody() {
      if (schemas == null) {
        schemas = new HashMap<String, Schema>();
        try {
          final XMLMetadata metadata = odataClient.getDeserializer().toMetadata(getRawResponse());
          for (Schema schema : metadata.getSchemas()) {
            schemas.put(schema.getNamespace(), schema);
            if (StringUtils.isNotBlank(schema.getAlias())) {
              schemas.put(schema.getAlias(), schema);
            }
          }
        } finally {
          this.close();
        }
      }
      return schemas;
    }
  }
}
