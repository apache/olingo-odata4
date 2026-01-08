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
package org.apache.olingo.ext.proxy.commons;

import org.apache.olingo.client.api.communication.response.ODataResponse;

final class ResponseStatusLine{

  private final int statusCode;
  private final String reasonPhrase;

  public ResponseStatusLine(final ODataResponse response) {
    this.statusCode = response == null ? 0 : response.getStatusCode();
    this.reasonPhrase = response == null ? "" : response.getStatusMessage();
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

  @Override
  public String toString() {
    return statusCode + " " + reasonPhrase;
  }
}
