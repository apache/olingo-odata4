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
package org.apache.olingo.client.core.communication.header;

import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class ODataErrorResponseChecker {

  protected static final Logger LOG = LoggerFactory.getLogger(ODataErrorResponseChecker.class);

  private static ODataError getGenericError(final int code, final String errorMsg) {
    final ODataError error = new ODataError();
    error.setCode(String.valueOf(code));
    error.setMessage(errorMsg);
    return error;
  }

  public static ODataRuntimeException checkResponse(
      final ODataClient odataClient, final StatusLine statusLine, final InputStream entity,
      final String accept) {

    ODataRuntimeException result = null;

    if (entity == null) {
      result = new ODataClientErrorException(statusLine);
    } else {
      final ContentType contentType = accept.contains("xml") ? ContentType.APPLICATION_ATOM_XML : ContentType.JSON;

      ODataError error = new ODataError();
      if (!accept.contains("text/plain")) {
        try {
          error = odataClient.getReader().readError(entity, contentType);
          if (error != null) {
            Map<String, String> innerError = error.getInnerError();
            if (innerError != null) {
              if (innerError.get("internalexception") != null) {
                error.setMessage(error.getMessage() + innerError.get("internalexception"));
              } else {
                error.setMessage(error.getMessage() + innerError.get("message"));
              }
            }
          }
        } catch (final RuntimeException | ODataDeserializerException e) {
          LOG.warn("Error deserializing error response", e);
          error = getGenericError(
              statusLine.getStatusCode(),
              statusLine.getReasonPhrase());
        }
      } else {
        error.setCode(String.valueOf(statusLine.getStatusCode()));
        error.setTarget(statusLine.getReasonPhrase());
        try {
          error.setMessage(IOUtils.toString(entity));
        } catch (IOException e) {
          LOG.warn("Error deserializing error response", e);
          error = getGenericError(
              statusLine.getStatusCode(),
              statusLine.getReasonPhrase());
        }
      }

      if (statusLine.getStatusCode() >= 500 && error != null
              && (error.getCode() == null || error.getCode().isEmpty())
              && (error.getDetails() == null || error.getDetails().isEmpty())
              && (error.getInnerError() == null || error.getInnerError().size() == 0)) {
        result = new ODataServerErrorException(statusLine);
      } else {
        result = new ODataClientErrorException(statusLine, error);
      }
    }

    return result;
  }

  private ODataErrorResponseChecker() {
    // private constructor for static utility class
  }
}
