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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the value of a complex property.
 */
public class ComplexValue extends Linked {

  private final List<Property> value = new ArrayList<Property>();
  
  private String typeName;

  /**
   * Get list of all values for this ComplexValue.
   *
   * @return all values for this ComplexValue (can not be null).
   */
  public List<Property> getValue() {
    return value;
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o) && value.equals(((ComplexValue) o).value);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return value.toString();
  }
  
  /**
   * Get string representation of type (can be null if not set).
   * @return string representation of type (can be null if not set)
   */
  public String getTypeName() {
    return typeName;
  }

  /**
   * Set string representation of type.
   * @param type string representation of type
   */
  public void setTypeName(final String typeName) {
    this.typeName = typeName;
  }
}
