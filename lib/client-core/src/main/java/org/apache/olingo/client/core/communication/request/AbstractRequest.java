/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.client.core.communication.request;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.core.communication.header.ODataErrorResponseChecker;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractRequest {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractRequest.class);

  protected void checkRequest(final ODataClient odataClient, final HttpUriRequest request) {
    // If using and Edm enabled client, checks that the cached service root matches the request URI
    if (odataClient instanceof EdmEnabledODataClient
            && !request.getURI().toASCIIString().startsWith(
                    ((EdmEnabledODataClient) odataClient).getServiceRoot())) {

      throw new IllegalArgumentException(
              String.format("The current request URI %s does not match the configured service root %s",
                      request.getURI().toASCIIString(),
                      ((EdmEnabledODataClient) odataClient).getServiceRoot()));
    }
  }

  protected void checkResponse(
          final ODataClient odataClient, final HttpResponse response, final String accept) {

    if (response.getStatusLine().getStatusCode() >= 400) {
      final ContentType contentType = determineContentType(response, accept);
      try {
        final ODataRuntimeException exception = ODataErrorResponseChecker.checkResponse(
                odataClient,
                response.getStatusLine(),
                response.getEntity() == null ? null : response.getEntity().getContent(),
                contentType);
        if (exception != null) {
          if (exception instanceof ODataClientErrorException) {
            ((ODataClientErrorException)exception).setHeaderInfo(response.getAllHeaders());
          }
          throw exception;
        }
      } catch (IOException e) {
        throw new ODataRuntimeException(
                "Received '" + response.getStatusLine() + "' but could not extract error body", e);
      }
    }
  }

  private static ContentType determineContentType(HttpResponse response, String accept) {
    if (response.getEntity() == null
            || response.getEntity().getContentType() == null
            || StringUtils.isBlank(response.getEntity().getContentType().getValue())) {
      return ContentType.fromAcceptHeader(accept);
    }
    try {
      return ContentType.create(response.getEntity().getContentType().getValue());
    } catch (Exception exception) {
      return ContentType.JSON;
    }
  }

}
