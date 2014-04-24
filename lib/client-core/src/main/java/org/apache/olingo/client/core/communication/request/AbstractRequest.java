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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.core.data.JSONODataErrorImpl;
import org.apache.olingo.commons.core.data.XMLODataErrorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequest {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractRequest.class);

  private ODataError getGenericError(final int code, final String errorMsg, final boolean isXML) {
    final ODataError error;
    if (isXML) {
      error = new XMLODataErrorImpl();
      ((XMLODataErrorImpl) error).setCode(String.valueOf(code));
      ((XMLODataErrorImpl) error).setMessage(errorMsg);
    } else {
      error = new JSONODataErrorImpl();
      ((JSONODataErrorImpl) error).setCode(String.valueOf(code));
      ((JSONODataErrorImpl) error).setMessage(errorMsg);
    }

    return error;
  }

  protected <C extends CommonODataClient<?>> void checkForResponse(
          final C odataClient, final HttpResponse response, final String accept) {

    if (response.getStatusLine().getStatusCode() >= 500) {
      throw new ODataServerErrorException(response.getStatusLine());
    } else if (response.getStatusLine().getStatusCode() >= 400) {
      try {
        final HttpEntity httpEntity = response.getEntity();
        if (httpEntity == null) {
          throw new ODataClientErrorException(response.getStatusLine());
        } else {
          final boolean isXML = accept.contains("json");
          ODataError error;
          try {
            error = odataClient.getReader().readError(httpEntity.getContent(), isXML);
          } catch (IllegalArgumentException e) {
            LOG.warn("Error deserializing error response", e);
            error = getGenericError(
                    response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase(),
                    isXML);
          }

          throw new ODataClientErrorException(response.getStatusLine(), error);
        }
      } catch (IOException e) {
        throw new HttpClientException(
                "Received '" + response.getStatusLine() + "' but could not extract error body", e);
      }
    }
  }
}
