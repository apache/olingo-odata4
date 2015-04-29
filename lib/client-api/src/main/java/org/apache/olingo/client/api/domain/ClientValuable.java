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
package org.apache.olingo.client.api.domain;


public interface ClientValuable {

  /**
   * Returns annotation value.
   * 
   * @return annotation value.
   */
  ClientValue getValue();

  /**
   * Checks if has null value.
   * 
   * @return 'TRUE' if has null value; 'FALSE' otherwise.
   */
  boolean hasNullValue();

  /**
   * Checks if has primitive value.
   * 
   * @return 'TRUE' if has primitive value; 'FALSE' otherwise.
   */
  boolean hasPrimitiveValue();

  /**
   * Gets primitive value.
   * 
   * @return primitive value if exists; null otherwise.
   */
  ClientPrimitiveValue getPrimitiveValue();

  /**
   * Checks if has collection value.
   * 
   * @return 'TRUE' if has collection value; 'FALSE' otherwise.
   */
  boolean hasCollectionValue();

  /**
   * Gets collection value.
   * 
   * @return collection value if exists; null otherwise.
   */
  ClientCollectionValue<ClientValue> getCollectionValue();

  /**
   * Checks if has complex value.
   * 
   * @return 'TRUE' if has complex value; 'FALSE' otherwise.
   */
  boolean hasComplexValue();

  /**
   * Gets complex value.
   * 
   * @return complex value if exists; null otherwise.
   */
  ClientComplexValue getComplexValue();

  /**
   * Checks if has enum value.
   * 
   * @return 'TRUE' if has enum value; 'FALSE' otherwise.
   */
  boolean hasEnumValue();

  /**
   * Gets enum value.
   * 
   * @return enum value if exists; null otherwise.
   */
  ClientEnumValue getEnumValue();

}
