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
package org.apache.olingo.server.core.uri.parser;

/** Exception thrown during URI parsing in cases where the URI violates the URI construction rules. */
public class UriParserSyntaxException extends UriParserException {

  private static final long serialVersionUID = 5887744747812478226L;

  public static enum MessageKeys implements MessageKey {
    /** parameter: segment */
    MUST_BE_LAST_SEGMENT,
    /** parameter: query-option name */
    UNKNOWN_SYSTEM_QUERY_OPTION,
    /** parameter: query-option name */
    DOUBLE_SYSTEM_QUERY_OPTION,
    /** parameters: query-option name, query-option value */
    WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION,
    /** parameter: $format option value */
    WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT,
    SYSTEM_QUERY_OPTION_LEVELS_NOT_ALLOWED_HERE,
    SYNTAX,
    /** parameter: alias name */
    DUPLICATED_ALIAS,
    /**Entity id must be followed by system query option id */
    ENTITYID_MISSING_SYSTEM_QUERY_OPTION_ID;

    @Override
    public String getKey() {
      return name();
    }
  }

  public UriParserSyntaxException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public UriParserSyntaxException(final String developmentMessage, final Throwable cause, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}
