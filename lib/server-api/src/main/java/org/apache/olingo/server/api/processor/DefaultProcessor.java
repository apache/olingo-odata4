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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializerException;
import org.apache.olingo.server.api.uri.UriInfo;

/**
 * Processor implementation for handling of metadata and service document. This implementation is registerd in the
 * ODataHandler by default. The default can be replaced by re-registering an custom implementation.
 */
public class DefaultProcessor implements MetadataProcessor, ServiceDocumentProcessor, ExceptionProcessor {

  private OData odata;
  private Edm edm;

  @Override
  public void init(final OData odata, final Edm edm) {
    this.odata = odata;
    this.edm = edm;
  }

  @Override
  public void readServiceDocument(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) {
    try {
      ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
      response.setContent(serializer.serviceDocument(edm, request.getRawBaseUri()));
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    } catch (final ODataSerializerException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  @Override
  public void readMetadata(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) {
    try {
      ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
      response.setContent(serializer.metadataDocument(edm));
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    } catch (final ODataSerializerException e) {
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    }
  }

  @Override
  public void processException(ODataRequest request, ODataResponse response, ODataServerError serverError,
      ContentType requestedContentType) {
    try {
      ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
      InputStream responseEntity = serializer.error(serverError);
      response.setStatusCode(serverError.getStatusCode());
      response.setContent(responseEntity);
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    } catch (Exception e) {
      // This should never happen but to be sure we have this catch here to prevent sending a stacktrace to a client.
      String responseContent =
          "{\"error\":{\"code\":null,\"message\":\"An unexpected exception occoured during " +
              "error processing with message: "
              + e.getMessage() + "\"}}";
      response.setContent(new ByteArrayInputStream(responseContent.getBytes()));
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON.toContentTypeString());
    }
  }
}
