/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.api.domain;

import java.io.Serializable;

/**
 * Abstract representation of an OData entity property value.
 */
public interface ODataValue extends Serializable {

  /**
   * Gets value type name.
   *
   * @return value type name.
   */
  String getTypeName();

  /**
   * Check is is a primitive value.
   *
   * @return 'TRUE' if primitive; 'FALSE' otherwise.
   */
  boolean isPrimitive();

  /**
   * Casts to primitive value.
   *
   * @return primitive value.
   */
  ODataPrimitiveValue asPrimitive();

  /**
   * Check is is a collection value.
   *
   * @return 'TRUE' if collection; 'FALSE' otherwise.
   */
  boolean isCollection();

  /**
   * Casts to collection value.
   *
   * @param <OV> The actual ODataValue interface.
   * @return collection value.
   */
  <OV extends ODataValue> ODataCollectionValue<OV> asCollection();

  /**
   * Check is is a complex value.
   *
   * @return 'TRUE' if complex; 'FALSE' otherwise.
   */
  boolean isComplex();

  /**
   * Casts to complex value.
   *
   * @param <OP> The actual ODataProperty interface.
   * @return complex value.
   */
  <OP extends CommonODataProperty> ODataComplexValue<OP> asComplex();

}
