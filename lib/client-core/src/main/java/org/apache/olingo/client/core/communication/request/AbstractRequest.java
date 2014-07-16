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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractRequest {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractRequest.class);

  private ODataError getGenericError(final int code, final String errorMsg) {
    final ODataError error = new ODataError();
    error.setCode(String.valueOf(code));
    error.setMessage(errorMsg);
    return error;
  }

  protected void checkRequest(final CommonODataClient<?> odataClient, final HttpUriRequest request) {
    // If using and Edm enabled client, checks that the cached service root matches the request URI
    if (odataClient instanceof CommonEdmEnabledODataClient
            && !request.getURI().toASCIIString().startsWith(
                    ((CommonEdmEnabledODataClient<?>) odataClient).getServiceRoot())) {

      throw new IllegalArgumentException(
              String.format("The current request URI %s does not match the configured service root %s",
                      request.getURI().toASCIIString(),
                      ((CommonEdmEnabledODataClient<?>) odataClient).getServiceRoot()));
    }
  }

  protected void checkResponse(
          final CommonODataClient<?> odataClient, final HttpResponse response, final String accept) {

    if (response.getStatusLine().getStatusCode() >= 400) {
      try {
        final HttpEntity httpEntity = response.getEntity();
        if (httpEntity == null) {
          throw new ODataClientErrorException(response.getStatusLine());
        } else {
          final ODataFormat format = accept.contains("xml") ? ODataFormat.XML : ODataFormat.JSON;

          ODataError error;
          try {
            error = odataClient.getReader().readError(httpEntity.getContent(), format);
          } catch (final RuntimeException e) {
            LOG.warn("Error deserializing error response", e);
            error = getGenericError(
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase());
          } catch (final ODataDeserializerException e) {
            LOG.warn("Error deserializing error response", e);
            error = getGenericError(
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase());
          }

          if (response.getStatusLine().getStatusCode() >= 500) {
            throw new ODataServerErrorException(response.getStatusLine());
          } else {
            throw new ODataClientErrorException(response.getStatusLine(), error);
          }
        }
      } catch (IOException e) {
        throw new HttpClientException(
                "Received '" + response.getStatusLine() + "' but could not extract error body", e);
      }
    }
  }
}
