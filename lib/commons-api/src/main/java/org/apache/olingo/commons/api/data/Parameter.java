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
package org.apache.olingo.commons.api.data;

public class Parameter extends Valuable {

  private String name;

  /**
   * @return name of the parameter
   */
  public String getName() {
    return name;
  }

  /**
   * @param name of the parameter
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Check if Valuable contains a ENTITY or COLLECTION_ENTITY ValueType
   *
   * @return true if ValueType is a ENTITY or COLLECTION_ENTITY, otherwise false
   */
  public boolean isEntity() {
    if (isCollection()) {
      return getValueType().getBaseType() == ValueType.ENTITY;
    }
    return getValueType() == ValueType.ENTITY;
  }

  /**
   * Get the value in its entity representation or null if it is not based on an entity ValueType
   *
   * @return entity representation or null if it is not based on an entity ValueType
   */
  public Entity asEntity() {
    if (isCollection()) {
      return null;
    }
    return isEntity() ? (Entity) getValue() : null;
  }

}
