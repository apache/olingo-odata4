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

/**
 * Data representation for a parameter.
 */
public class Parameter extends Valuable {

  private String name;

  /**
   * Gets the name of the parameter.
   * @return name of the parameter
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the parameter.
   * @param name of the parameter
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Checks whether this parameter value is of the value type ENTITY or COLLECTION_ENTITY.
   * @return true if the value type is ENTITY or COLLECTION_ENTITY, otherwise false
   */
  public boolean isEntity() {
    return getValueType() == ValueType.ENTITY || getValueType() == ValueType.COLLECTION_ENTITY;
  }

  /**
   * Gets the value in its entity representation or null if it is not based on an entity value type.
   * @return entity representation or null if it is not based on an entity value type
   */
  public Entity asEntity() {
    return isEntity() && !isCollection() ? (Entity) getValue() : null;
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o)
        && (name == null ? ((Parameter) o).name == null : name.equals(((Parameter) o).name));
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (name == null ? 0 : name.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return (name == null ? "null" : name) + '=' + (getValue() == null ? "null" : getValue());
  }
}
