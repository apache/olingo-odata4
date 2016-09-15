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
package org.apache.olingo.server.api.deserializer.batch;

import org.apache.olingo.server.api.deserializer.DeserializerException;

public class BatchDeserializerException extends DeserializerException {
  public static enum MessageKeys implements MessageKey {
    /** parameter: line */
    INVALID_BOUNDARY,
    /** parameter: line */
    INVALID_CHANGESET_METHOD,
    /** parameter: line */
    INVALID_CONTENT,
    /** parameter: line */
    INVALID_CONTENT_LENGTH,
    /** parameter: line */
    INVALID_CONTENT_TRANSFER_ENCODING,
    /** parameter: line */
    INVALID_CONTENT_TYPE,
    /** parameters: line, expected content type, actual content type */
    UNEXPECTED_CONTENT_TYPE,
    /** parameter: line */
    INVALID_CONTENT_ID,
    /** parameter: line */
    INVALID_HOST,
    /** parameter: line */
    INVALID_HTTP_VERSION,
    /** parameter: line */
    INVALID_METHOD,
    /** parameter: line */
    INVALID_STATUS_LINE,
    /** parameter: line */
    INVALID_URI,
    /** parameter: line */
    MISSING_BLANK_LINE,
    /** parameter: line */
    MISSING_BOUNDARY_DELIMITER,
    /** parameter: line */
    MISSING_CLOSE_DELIMITER,
    /** parameter: line */
    MISSING_CONTENT_ID,
    /** parameter: line */
    MISSING_CONTENT_TYPE,
    /** parameter: line */
    MISSING_MANDATORY_HEADER,
    /** parameter: line */
    FORBIDDEN_HEADER,
    /** parameter: line */
    INVALID_BASE_URI;

    @Override
    public String getKey() {
      return name();
    }
  }

  private static final long serialVersionUID = -907752788975531134L;

  /**
   * Creates batch deserializer exception.
   * @param developmentMessage message text as fallback and for debugging purposes
   * @param messageKey one of the {@link MessageKeys} for the exception text in the resource bundle
   * @param parameters parameters for the exception text
   */
  public BatchDeserializerException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  /**
   * Creates batch deserializer exception.
   * @param developmentMessage message text as fallback and for debugging purposes
   * @param cause the cause of this exception
   * @param messageKey one of the {@link MessageKeys} for the exception text in the resource bundle
   * @param parameters parameters for the exception text
   */
  public BatchDeserializerException(final String developmentMessage, final Throwable cause,
      final MessageKey messageKey, final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}