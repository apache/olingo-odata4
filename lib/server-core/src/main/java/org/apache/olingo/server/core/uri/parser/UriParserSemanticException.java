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
    /** parameters: function-import name, function parameters */ FUNCTION_NOT_FOUND,
    /** parameter: resource part */ RESOURCE_PART_ONLY_FOR_TYPED_PARTS,
    /** parameter: resource part */ RESOURCE_PART_MUST_BE_PRECEDED_BY_STRUCTURAL_TYPE,
    /** parameter: property name */ PROPERTY_AFTER_COLLECTION,
    /** parameters: type name, property name */ PROPERTY_NOT_IN_TYPE,
    /** parameters: type name, property name */ EXPRESSION_PROPERTY_NOT_IN_TYPE,
    /** parameter: property name */ UNKNOWN_PROPERTY_TYPE,
    /** parameter: type filter */ INCOMPATIBLE_TYPE_FILTER,
    /** parameters: previous type filter, last type filter */ TYPE_FILTER_NOT_CHAINABLE,
    /** parameter: type filter */ PREVIOUS_PART_NOT_TYPED,
    /** parameter: type */ FUNCTION_PARAMETERS_EXPECTED,
    /** parameter: resource part */ UNKNOWN_PART,
    /** parameter: expression */ ONLY_FOR_TYPED_PARTS,
    /** parameter: entity type name */ UNKNOWN_ENTITY_TYPE,
    /** parameter: expression */ ONLY_FOR_COLLECTIONS,
    /** parameter: expression */ ONLY_FOR_ENTITY_TYPES,
    /** parameter: expression */ ONLY_FOR_STRUCTURAL_TYPES,
    /** parameter: expression */ ONLY_FOR_TYPED_PROPERTIES,
    /** parameter: value */ INVALID_KEY_VALUE,
    PARAMETERS_LIST_ONLY_FOR_TYPED_PARTS,
    /** parameters: expected number, actual number */ WRONG_NUMBER_OF_KEY_PROPERTIES,
    NOT_ENOUGH_REFERENTIAL_CONSTRAINTS,
    KEY_NOT_ALLOWED,
    RESOURCE_PATH_NOT_TYPED,
    ONLY_SIMPLE_AND_COMPLEX_PROPERTIES_IN_SELECT,
    COMPLEX_PROPERTY_OF_ENTITY_TYPE_EXPECTED,
    NOT_FOR_ENTITY_TYPE,
    PREVIOUS_PART_TYPED, 
    /** parameter: resource_name */ RESOURCE_NOT_FOUND;

    @Override
    public String getKey() {
      return name();
    }
  }

  public UriParserSemanticException(String developmentMessage, MessageKey messageKey, String... parameters) {
    super(developmentMessage, messageKey, parameters);
  }

  public UriParserSemanticException(String developmentMessage, Throwable cause, MessageKey messageKey,
      String... parameters) {
    super(developmentMessage, cause, messageKey, parameters);
  }
}
