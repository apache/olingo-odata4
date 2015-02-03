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

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;

public abstract class ServiceResponse {
  protected ODataResponse response;
  private boolean closed;

  public ServiceResponse(ODataResponse response) {
    this.response = response;
  }

  public ODataResponse getODataResponse() {
    return this.response;
  }

  protected boolean isClosed() {
    return this.closed;
  }

  protected void close() {
    if (!this.closed) {
      this.closed = true;
    }
  }

  public void writeNoContent(boolean closeResponse) {
    this.response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeNotFound(boolean closeResponse) {
    response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeServerError(boolean closeResponse) {
    response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeBadRequest(boolean closeResponse) {
    response.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeOK(String contentType) {
    this.response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    this.response.setHeader(HttpHeader.CONTENT_TYPE, contentType);
  }

  public void writeHeader(String key, String value) {
    this.response.setHeader(key, value);
  }

  public abstract void accepts(ServiceResponseVisior visitor) throws ODataTranslatedException,
      ODataApplicationException;
}
