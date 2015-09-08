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

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.communication.header.ODataErrorResponseChecker;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      try {
        final ODataRuntimeException exception = ODataErrorResponseChecker.checkResponse(
                odataClient,
                response.getStatusLine(),
                response.getEntity() == null ? null : response.getEntity().getContent(),
                accept);
        if (exception != null) {
          throw exception;
        }
      } catch (IOException e) {
        throw new ODataRuntimeException(
                "Received '" + response.getStatusLine() + "' but could not extract error body", e);
      }
    }
  }
}
