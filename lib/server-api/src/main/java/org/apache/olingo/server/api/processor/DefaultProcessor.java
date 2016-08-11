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
import java.nio.charset.Charset;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.etag.ETagHelper;
import org.apache.olingo.server.api.etag.ServiceMetadataETagSupport;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;

/**
 * <p>Processor implementation for handling default cases:
 * <ul><li>request for the metadata document</li>
 * <li>request for the service document</li>
 * <li>error handling</li></ul></p>
 * <p>This implementation is registered in the ODataHandler by default.
 * The default can be replaced by re-registering a custom implementation.</p>
 */
public class DefaultProcessor implements MetadataProcessor, ServiceDocumentProcessor, ErrorProcessor {
  private OData odata;
  private ServiceMetadata serviceMetadata;

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  @Override
  public void readServiceDocument(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, ODataLibraryException {
    boolean isNotModified = false;
    ServiceMetadataETagSupport eTagSupport = serviceMetadata.getServiceMetadataETagSupport();
    if (eTagSupport != null && eTagSupport.getServiceDocumentETag() != null) {
      // Set application etag at response
      response.setHeader(HttpHeader.ETAG, eTagSupport.getServiceDocumentETag());
      // Check if service document has been modified
      ETagHelper eTagHelper = odata.createETagHelper();
      isNotModified = eTagHelper.checkReadPreconditions(eTagSupport.getServiceDocumentETag(), request
          .getHeaders(HttpHeader.IF_MATCH), request.getHeaders(HttpHeader.IF_NONE_MATCH));
    }

    // Send the correct response
    if (isNotModified) {
      response.setStatusCode(HttpStatusCode.NOT_MODIFIED.getStatusCode());
    } else {
      // HTTP HEAD requires no payload but a 200 OK response
      if (HttpMethod.HEAD == request.getMethod()) {
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      } else {
        ODataSerializer serializer = odata.createSerializer(requestedContentType);
        response.setContent(serializer.serviceDocument(serviceMetadata, null).getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
      }
    }
  }

  @Override
  public void readMetadata(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, ODataLibraryException {
    boolean isNotModified = false;
    ServiceMetadataETagSupport eTagSupport = serviceMetadata.getServiceMetadataETagSupport();
    if (eTagSupport != null && eTagSupport.getMetadataETag() != null) {
      // Set application etag at response
      response.setHeader(HttpHeader.ETAG, eTagSupport.getMetadataETag());
      // Check if metadata document has been modified
      ETagHelper eTagHelper = odata.createETagHelper();
      isNotModified = eTagHelper.checkReadPreconditions(eTagSupport.getMetadataETag(), request
          .getHeaders(HttpHeader.IF_MATCH), request.getHeaders(HttpHeader.IF_NONE_MATCH));
    }

    // Send the correct response
    if (isNotModified) {
      response.setStatusCode(HttpStatusCode.NOT_MODIFIED.getStatusCode());
    } else {
      // HTTP HEAD requires no payload but a 200 OK response
      if (HttpMethod.HEAD == request.getMethod()) {
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      } else {
        ODataSerializer serializer = odata.createSerializer(requestedContentType);
        response.setContent(serializer.metadataDocument(serviceMetadata).getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
      }
    }
  }

  @Override
  public void processError(final ODataRequest request, final ODataResponse response,
      final ODataServerError serverError,
      final ContentType requestedContentType) {
    try {
      ODataSerializer serializer = odata.createSerializer(requestedContentType);
      response.setContent(serializer.error(serverError).getContent());
      response.setStatusCode(serverError.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    } catch (Exception e) {
      // This should never happen but to be sure we have this catch here to prevent sending a stacktrace to a client.
      String responseContent =
          "{\"error\":{\"code\":null,\"message\":\"An unexpected exception occurred during error processing\"}}";
      response.setContent(new ByteArrayInputStream(responseContent.getBytes(Charset.forName("utf-8"))));
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON.toContentTypeString());
    }
  }
}
