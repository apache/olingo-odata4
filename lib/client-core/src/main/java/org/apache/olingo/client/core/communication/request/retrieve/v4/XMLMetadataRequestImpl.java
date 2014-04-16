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
package org.apache.olingo.client.core.communication.request.retrieve.v4;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v4.Include;
import org.apache.olingo.client.api.edm.xml.v4.Reference;
import org.apache.olingo.client.api.edm.xml.v4.XMLMetadata;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractMetadataRequestImpl;

public class XMLMetadataRequestImpl extends AbstractMetadataRequestImpl<List<? extends Schema>>
        implements XMLMetadataRequest {

  XMLMetadataRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, uri);
  }

  @Override
  public ODataRetrieveResponse<List<? extends Schema>> execute() {
    final SingleXMLMetadatRequestImpl rootReq = new SingleXMLMetadatRequestImpl((ODataClient) odataClient, uri);
    final ODataRetrieveResponse<XMLMetadata> rootRes = rootReq.execute();

    final XMLMetadataResponseImpl response = new XMLMetadataResponseImpl();

    final XMLMetadata rootMetadata = rootRes.getBody();
    response.getSchemas().addAll(rootMetadata.getSchemas());

    if (!rootMetadata.getReferences().isEmpty()) {
      for (Reference reference : rootMetadata.getReferences()) {
        final SingleXMLMetadatRequestImpl includeReq = new SingleXMLMetadatRequestImpl((ODataClient) odataClient,
                odataClient.getURIBuilder(reference.getUri().toASCIIString()).appendMetadataSegment().build());
        final XMLMetadata includeMetadata = includeReq.execute().getBody();
        
        for (Include include : reference.getIncludes()) {
          Schema includedSchema = includeMetadata.getSchema(include.getNamespace());
          if (includedSchema == null && StringUtils.isNotBlank(include.getAlias())) {
            includedSchema = includeMetadata.getSchema(include.getAlias());
          }
          if (includedSchema != null) {
            response.getSchemas().add(includedSchema);
          }
        }
      }
    }

    return response;
  }

  private class SingleXMLMetadatRequestImpl extends AbstractMetadataRequestImpl<XMLMetadata> {

    public SingleXMLMetadatRequestImpl(final ODataClient odataClient, final URI uri) {
      super(odataClient, uri);
    }

    @Override
    public ODataRetrieveResponse<XMLMetadata> execute() {
      return new AbstractODataRetrieveResponse(httpClient, doExecute()) {

        @Override
        public XMLMetadata getBody() {
          try {
            return ((ODataClient) odataClient).getDeserializer().toMetadata(getRawResponse());
          } finally {
            this.close();
          }
        }
      };
    }
  }

  public class XMLMetadataResponseImpl extends AbstractODataRetrieveResponse {

    private final List<Schema> schemas = new ArrayList<Schema>();

    /**
     * Constructor.
     * <br/>
     * Just to create response templates to be initialized from batch.
     */
    private XMLMetadataResponseImpl() {
      super();
    }

    @Override
    public void close() {
      // just do nothing, this is a placeholder response
    }

    public List<Schema> getSchemas() {
      return schemas;
    }

    @Override
    public List<? extends Schema> getBody() {
      return getSchemas();
    }
  }

}
