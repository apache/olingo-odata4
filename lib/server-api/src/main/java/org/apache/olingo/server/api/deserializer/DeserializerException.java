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
package org.apache.olingo.server.api.deserializer;

import org.apache.olingo.server.api.ODataLibraryException;

/** Exception thrown by deserializers. */
public class DeserializerException extends ODataLibraryException {

  private static final long serialVersionUID = 6341270437497060906L;

  /** Keys for exception texts in the resource bundle. */
  public static enum MessageKeys implements MessageKey {
    NOT_IMPLEMENTED,
    IO_EXCEPTION,
    /** parameter: format */
    UNSUPPORTED_FORMAT,
    JSON_SYNTAX_EXCEPTION,
    /** parameter: propertyName */
    INVALID_NULL_PROPERTY,
    /** parameter: keyName */
    UNKNOWN_CONTENT,
    /** parameter: propertyName */
    INVALID_VALUE_FOR_PROPERTY,
    /** parameter: propertyName */
    INVALID_JSON_TYPE_FOR_PROPERTY,
    VALUE_ARRAY_NOT_PRESENT,
    VALUE_TAG_MUST_BE_AN_ARRAY,
    INVALID_ENTITY,
    /** parameter: navigationPropertyName */
    INVALID_VALUE_FOR_NAVIGATION_PROPERTY,
    DUPLICATE_PROPERTY,
    DUPLICATE_JSON_PROPERTY,
    /** parameters: primitiveTypeName, propertyName */
    UNKNOWN_PRIMITIVE_TYPE,
    /** parameter: navigationPropertyName */
    NAVIGATION_PROPERTY_NOT_FOUND,
    /** parameter: annotationName */
    INVALID_ANNOTATION_TYPE,
    /** parameter: annotationName */
    INVALID_NULL_ANNOTATION,
    /** parameter: binding link */
    INVALID_ENTITY_BINDING_LINK,
    /** parameter: action parameter name */
    INVALID_ACTION_PARAMETER_TYPE,
    /** parameter: parameterName */
    INVALID_NULL_PARAMETER;

    @Override
    public String getKey() {
      return name();
    }
  }

  /**
   * Creates deserializer exception.
   * @param developmentMessage message text as fallback and for debugging purposes
   * @param messageKey one of the {@link MessageKeys} for the exception text in the resource bundle
   * @param parameters parameters for the exception text
   */
  public DeserializerException(final String developmentMessage,
      final MessageKey messageKey, final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  /**
   * Creates deserializer exception.
   * @param developmentMessage message text as fallback and for debugging purposes
   * @param cause the cause of this exception
   * @param messageKey one of the {@link MessageKeys} for the exception text in the resource bundle
   * @param parameters parameters for the exception text
   */
  public DeserializerException(final String developmentMessage, final Throwable cause,
      final MessageKey messageKey, final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }

  @Override
  protected String getBundleName() {
    return DEFAULT_SERVER_BUNDLE_NAME;
  }
}
