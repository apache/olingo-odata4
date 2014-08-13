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

import org.apache.olingo.server.api.ODataTranslatedException;

/** Exception thrown during basic request handling. */
public class ODataHandlerException extends ODataTranslatedException {
  private static final long serialVersionUID = -907752788975531134L;

  public static enum MessageKeys implements MessageKey {
    /** parameters: HTTP method, HTTP method */ AMBIGUOUS_XHTTP_METHOD,
    /** parameter: HTTP method */ HTTP_METHOD_NOT_IMPLEMENTED,
    /** parameter: processor interface */ PROCESSOR_NOT_IMPLEMENTED,
    FUNCTIONALITY_NOT_IMPLEMENTED,
    /** parameter: version */ ODATA_VERSION_NOT_SUPPORTED
  }

  public ODataHandlerException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public ODataHandlerException(final String developmentMessage, final Throwable cause, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}
