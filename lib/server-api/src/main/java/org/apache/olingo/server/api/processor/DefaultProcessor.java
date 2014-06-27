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
package org.apache.olingo.server.api.processor;

import java.io.InputStream;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;

public class DefaultProcessor implements MetadataProcessor, ServiceDocumentProcessor {

  private OData odata;
  private Edm edm;

  @Override
  public void init(OData odata, Edm edm) {
    this.odata = odata;
    this.edm = edm;
  }

  @Override
  public void readServiceDocument(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
    ODataSerializer serializer;
    InputStream responseEntity;

    serializer = odata.createSerializer(ODataFormat.JSON);
    responseEntity = serializer.serviceDocument(edm, request.getRawBaseUri());

    response.setStatusCode(200);
    response.setHeader(HttpHeader.CONTENT_TYPE, HttpContentType.APPLICATION_JSON);
    response.setContent(responseEntity);

  }

  @Override
  public void readMetadata(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
    ODataSerializer serializer;
    InputStream responseEntity;

    serializer = odata.createSerializer(ODataFormat.XML);
    responseEntity = serializer.metadataDocument(edm);
    response.setStatusCode(200);
    response.setHeader(HttpHeader.CONTENT_TYPE, HttpContentType.APPLICATION_XML);
    response.setContent(responseEntity);
  }

}
