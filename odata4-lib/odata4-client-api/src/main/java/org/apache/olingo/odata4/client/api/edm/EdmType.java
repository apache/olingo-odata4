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
package org.apache.olingo.odata4.client.api.edm;

import org.apache.olingo.odata4.client.api.data.EdmSimpleType;

public interface EdmType {

  /**
   * Checks if is a collection.
   *
   * @return 'TRUE' if is a collection; 'FALSE' otherwise.
   */
  boolean isCollection();

  /**
   * Checks if is a simple type.
   *
   * @return 'TRUE' if is a simple type; 'FALSE' otherwise.
   */
  boolean isSimpleType();

  /**
   * Gets type as a simple type.
   *
   * @return simple type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not a simple type.
   */
  EdmSimpleType getSimpleType();

  /**
   * Checks if is an enum type.
   *
   * @return 'TRUE' if is an enum type; 'FALSE' otherwise.
   */
  boolean isEnumType();

  /**
   * Gets type as enum type.
   *
   * @return enum type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not an enum type.
   */
  EnumType getEnumType();

  /**
   * Checks if is a complex type.
   *
   * @return 'TRUE' if is a complex type; 'FALSE' otherwise.
   */
  boolean isComplexType();

  /**
   * Gets type as complex type.
   *
   * @return complex type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not a complex type.
   */
  ComplexType getComplexType();

  /**
   * Checks if is an entity type.
   *
   * @return 'TRUE' if is an entity type; 'FALSE' otherwise.
   */
  boolean isEntityType();

  /**
   * Gets type as entity type.
   *
   * @return entity type. An <tt>EdmTypeNotFoundException</tt> will be raised if it is not an entity type.
   */
  EntityType getEntityType();

  /**
   * Gets base type.
   *
   * @return base type.
   */
  String getBaseType();

  /**
   * Gets type expression.
   *
   * @return type expression.
   */
  String getTypeExpression();

  /**
   * Gets namespace or alias retrieved from the provided type expression.
   *
   * @return namespace or alias.
   */
  String getNamespaceOrAlias();
}
