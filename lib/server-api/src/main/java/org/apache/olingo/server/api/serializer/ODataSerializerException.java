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
package org.apache.olingo.server.api.serializer;

import org.apache.olingo.server.api.ODataTranslatedException;

public class ODataSerializerException extends ODataTranslatedException {

  private static final long serialVersionUID = 5358683245923127425L;

  public static enum MessageKeys implements MessageKey {
    NOT_IMPLEMENTED,
    /** parameter: format */ UNSUPPORTED_FORMAT,
    JSON_METADATA,
    IO_EXCEPTION,
    NULL_INPUT,
    NO_CONTEXT_URL,
    /** parameter: property name */ UNSUPPORTED_PROPERTY_TYPE,
    /** parameter: property name */ INCONSISTENT_PROPERTY_TYPE,
    /** parameter: property name */ MISSING_PROPERTY,
    /** parameters: property name, property value */ WRONG_PROPERTY_VALUE;

    @Override
    public String getKey() {
      return name();
    }
  }

  public ODataSerializerException(final String developmentMessage,
      final MessageKey messageKey, final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public ODataSerializerException(final String developmentMessage, final Throwable cause,
      final MessageKey messageKey, final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }

  @Override
  protected String getBundleName() {
    return "server-core-exceptions-i18n";
  }
}
