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

import org.apache.olingo.server.api.ODataLibraryException;

public class ContentNegotiatorException extends ODataLibraryException {
  private static final long serialVersionUID = -8112658467394158700L;

  public static enum MessageKeys implements MessageKey {
    /** parameter: list of content-type ranges */
    UNSUPPORTED_ACCEPT_TYPES,
    /** parameter: content type */
    UNSUPPORTED_CONTENT_TYPE,
    /** no parameter */
    NO_CONTENT_TYPE_SUPPORTED,
    /** parameter: format string */
    UNSUPPORTED_FORMAT_OPTION,
    /**parameter: accept charset */
    UNSUPPORTED_ACCEPT_CHARSET,
    /** parameter: accept header charset */
    UNSUPPORTED_ACCEPT_HEADER_CHARSET;

    @Override
    public String getKey() {
      return name();
    }
  }

  public ContentNegotiatorException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public ContentNegotiatorException(final String developmentMessage, final Throwable cause,
      final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }

  @Override
  protected String getBundleName() {
    return DEFAULT_SERVER_BUNDLE_NAME;
  }
}
