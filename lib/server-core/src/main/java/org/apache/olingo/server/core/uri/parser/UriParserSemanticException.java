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

/** Exception thrown during URI parsing in cases where an URI part is invalid according to the Entity Data Model. */
public class UriParserSemanticException extends UriParserException {

  private static final long serialVersionUID = 3850285860949809622L;

  public static enum MessageKeys implements MessageKey {
    /** parameters: function-import name, function parameters */
    FUNCTION_NOT_FOUND,
    /** parameter: resource part */
    RESOURCE_PART_MUST_BE_PRECEDED_BY_STRUCTURAL_TYPE,
    /** parameter: property name */
    PROPERTY_AFTER_COLLECTION,
    /** parameters: type name, property name */
    PROPERTY_NOT_IN_TYPE,
    /** parameters: type name, property name */
    EXPRESSION_PROPERTY_NOT_IN_TYPE,
    /** parameter: type filter */
    INCOMPATIBLE_TYPE_FILTER,
    /** parameters: previous type filter, last type filter */
    TYPE_FILTER_NOT_CHAINABLE,
    /** parameter: type filter */
    PREVIOUS_PART_NOT_TYPED,
    /** parameter: resource part */
    UNKNOWN_PART,
    /** parameter: type */
    UNKNOWN_TYPE,
    /** parameter: expression */
    ONLY_FOR_TYPED_PARTS,
    /** parameter: expression */
    ONLY_FOR_COLLECTIONS,
    /** parameter: expression */
    ONLY_FOR_ENTITY_TYPES,
    /** parameter: expression */
    ONLY_FOR_STRUCTURAL_TYPES,
    /** parameter: value */
    INVALID_KEY_VALUE,
    /** parameters: expected number, actual number */
    WRONG_NUMBER_OF_KEY_PROPERTIES,
    KEY_NOT_ALLOWED,
    /** parameter: resource_name */
    RESOURCE_NOT_FOUND,
    /** parameter: not implemented part */
    NOT_IMPLEMENTED,
    /** parameter: namespace */
    NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT,
    /** parameter: complex parameter value */
    COMPLEX_PARAMETER_IN_RESOURCE_PATH,
    /** parameters: left type, right type */
    TYPES_NOT_COMPATIBLE,
    /** parameter: addressed resource name */
    NOT_A_MEDIA_RESOURCE,
    /** parameters: property name */
    IS_PROPERTY,
    /** parameter: expression */
    ONLY_FOR_PRIMITIVE_TYPES,
    /** parameter: function name */
    FUNCTION_MUST_USE_COLLECTIONS,
    COLLECTION_NOT_ALLOWED,
    /** parameter: not implemented part for system query option $id */
    NOT_IMPLEMENTED_SYSTEM_QUERY_OPTION;

    @Override
    public String getKey() {
      return name();
    }
  }

  public UriParserSemanticException(final String developmentMessage, final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public UriParserSemanticException(final String developmentMessage, final Throwable cause,
      final MessageKey messageKey,
      final String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}
