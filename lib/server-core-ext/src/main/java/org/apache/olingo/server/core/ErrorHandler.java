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

import java.io.ByteArrayInputStream;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.validator.UriValidationException;

public class ErrorHandler {
  private final OData odata;
  private final ServiceMetadata metadata;
  private final CustomContentTypeSupport customContent;

  public ErrorHandler(OData odata, ServiceMetadata metadata, CustomContentTypeSupport customContent) {
    this.odata = odata;
    this.metadata = metadata;
    this.customContent = customContent;
  }

  public void handleException(Exception e, ODataRequest request, ODataResponse response) {
    if (e instanceof UriValidationException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((UriValidationException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof UriParserSemanticException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((UriParserSemanticException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof  UriParserSyntaxException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((UriParserSyntaxException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof  UriParserException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((UriParserException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof ContentNegotiatorException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((ContentNegotiatorException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof SerializerException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((SerializerException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof BatchDeserializerException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((BatchDeserializerException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof DeserializerException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((DeserializerException)e, null);
      handleServerError(request, response, serverError);
    } else if(e instanceof ODataHandlerException) {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject((ODataHandlerException)e, null);
      handleServerError(request, response, serverError);
    } else {
      ODataServerError serverError = ODataExceptionHelper.createServerErrorObject(e);
      handleServerError(request, response, serverError);
    }
  }

  void handleServerError(final ODataRequest request, final ODataResponse response,
      final ODataServerError serverError) {
    ContentType requestedContentType;
    try {
      UriInfo uriInfo = new Parser().parseUri(request.getRawODataPath(), request.getRawQueryPath(),
          null, this.metadata.getEdm());
      requestedContentType = ContentNegotiator.doContentNegotiation(uriInfo.getFormatOption(),
          request, this.customContent, RepresentationType.ERROR);
    } catch (final ContentNegotiatorException e) {
      requestedContentType = ContentType.JSON;
    } catch (UriParserException e) {
      requestedContentType = ContentType.JSON;
    }
    processError(response, serverError, requestedContentType);
  }

  void processError(ODataResponse response, ODataServerError serverError,
      ContentType requestedContentType) {
    try {
      ODataSerializer serializer = this.odata.createSerializer(requestedContentType);
      response.setContent(serializer.error(serverError).getContent());
      response.setStatusCode(serverError.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    } catch (Exception e) {
      // This should never happen but to be sure we have this catch here
      // to prevent sending a stacktrace to a client.
      String responseContent = "{\"error\":{\"code\":null,\"message\":\"An unexpected exception occurred during "
          + "error processing with message: " + e.getMessage() + "\"}}"; //$NON-NLS-1$ //$NON-NLS-2$
      response.setContent(new ByteArrayInputStream(responseContent.getBytes()));
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE,
          ContentType.APPLICATION_JSON.toContentTypeString());
    }
  }
}
