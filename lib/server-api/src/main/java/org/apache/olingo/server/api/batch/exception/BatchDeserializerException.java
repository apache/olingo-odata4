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
package org.apache.olingo.server.api.batch.exception;

public class BatchDeserializerException extends BatchException {
  public static enum MessageKeys implements MessageKey {
    INVALID_BOUNDARY,
    INVALID_CHANGESET_METHOD,
    INVALID_CONTENT,
    INVALID_CONTENT_LENGTH,
    INVALID_CONTENT_TRANSFER_ENCODING,
    INVALID_CONTENT_TYPE,
    INVALID_HEADER,
    INVALID_HTTP_VERSION,
    INVALID_METHOD,
    INVALID_QUERY_OPERATION_METHOD,
    INVALID_STATUS_LINE,
    INVALID_URI,
    MISSING_BLANK_LINE,
    MISSING_BOUNDARY_DELIMITER,
    MISSING_CLOSE_DELIMITER,
    MISSING_CONTENT_ID,
    MISSING_CONTENT_TRANSFER_ENCODING,
    MISSING_CONTENT_TYPE,
    MISSING_MANDATORY_HEADER,
    FORBIDDEN_HEADER, 
    FORBIDDEN_ABSOLUTE_URI;

    @Override
    public String getKey() {
      return name();
    }
  }

  private static final long serialVersionUID = -907752788975531134L;

  public BatchDeserializerException(final String developmentMessage, final MessageKey messageKey, 
      final int lineNumber) {
    this(developmentMessage, messageKey, "" + lineNumber);
  }

  public BatchDeserializerException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  @Override
  protected String getBundleName() {
    return DEFAULT_SERVER_BUNDLE_NAME;
  }
}