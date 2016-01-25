/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.api.communication;

import org.apache.http.StatusLine;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

/**
 * Represents a client error in OData.
 *
 * @see ODataError
 */
public class ODataClientErrorException extends ODataRuntimeException {

  private static final long serialVersionUID = -2551523202755268162L;

  private final StatusLine statusLine;

  private final ODataError error;

  /**
   * Constructor.
   *
   * @param statusLine request status info.
   */
  public ODataClientErrorException(final StatusLine statusLine) {
    super(statusLine.toString());

    this.statusLine = statusLine;
    this.error = null;
  }

  /**
   * Constructor.
   *
   * @param statusLine request status info.
   * @param error OData error to be wrapped.
   */
  public ODataClientErrorException(final StatusLine statusLine, final ODataError error) {
    super(error == null ?
        statusLine.toString() :
        (error.getCode() == null || error.getCode().isEmpty() ? "" : "(" + error.getCode() + ") ")
            + error.getMessage() + " [" + statusLine.toString() + "]");

    this.statusLine = statusLine;
    this.error = error;
  }

  /**
   * Gets request status info.
   *
   * @return request status info.
   */
  public StatusLine getStatusLine() {
    return statusLine;
  }

  /**
   * Gets OData error.
   *
   * @return OData error.
   */
  public ODataError getODataError() {
    return error;
  }
}
