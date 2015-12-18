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
package org.apache.olingo.server.core.uri.parser.search;

import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;

public class SearchParserException extends UriParserSyntaxException {

  private static final long serialVersionUID = 5781553037561337795L;

  public enum MessageKeys implements MessageKey {
    NO_EXPRESSION_FOUND,
    /** parameter: message */
    TOKENIZER_EXCEPTION,
    /** parameter: operatorkind */
    INVALID_NOT_OPERAND,
    /** parameters: - */
    MISSING_CLOSE,
    /** parameters: expectedToken actualToken */
    EXPECTED_DIFFERENT_TOKEN,
    /** parameter: actual token */
    INVALID_END_OF_QUERY;

    @Override
    public String getKey() {
      return name();
    }
  }

  public SearchParserException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public SearchParserException(final String developmentMessage, final Throwable cause, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }

}
