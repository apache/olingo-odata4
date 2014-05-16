/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core;

import java.io.InputStream;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.parser.Parser;

public class ODataHandler {

  private final OData server;
  private final Edm edm;

  public ODataHandler(final OData server, final Edm edm) {
    this.server = server;
    this.edm = edm;
  }

  public ODataResponse process(final ODataRequest odRequest) {
    try {
      ODataResponse response = new ODataResponse();

      Parser parser = new Parser();
      String odUri =
          odRequest.getRawODataPath() + (odRequest.getRawQueryPath() == null ? "" : "?" + odRequest.getRawQueryPath());
      UriInfo uriInfo = parser.parseUri(odUri, edm);

      ODataSerializer serializer;
      InputStream responseEntity;
      switch (uriInfo.getKind()) {
      case metadata:
        serializer = server.createSerializer(ODataFormat.XML);
        responseEntity = serializer.metadataDocument(edm);
        
        response.setStatusCode(200);
        response.setHeader("Content-Type", "application/xml");
        response.setContent(responseEntity);
        break;
      case service:
        serializer = server.createSerializer(ODataFormat.JSON);
        responseEntity = serializer.serviceDocument(edm, odRequest.getRawBaseUri());
        
        response.setStatusCode(200);
        response.setHeader("Content-Type", "application/json");
        response.setContent(responseEntity);
        break;
      default:
        throw new ODataRuntimeException("not implemented");
      }

      return response;
    } catch (Exception e) {
      // TODO OData error message handling
      throw new RuntimeException(e);
    }
  }
}
