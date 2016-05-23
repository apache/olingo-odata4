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
 * Data representation for a property.
 */
public class Property extends Valuable {

  private String name;
  private final List<Operation> operations = new ArrayList<Operation>();
  
  /**
   * Creates a new property
   */
  public Property() {}
  
  /**
   * Creates a new property
   * 
   * @param type  String representation of type (can be null)
   * @param name  Name of the property
   */
  public Property(final String type, final String name) {
    this.name = name;
    super.setType(type);
  }
  
  /**
   * Creates a new property
   * 
   * @param type        String representation of type (can be null)
   * @param name        Name of the property
   * @param valueType   Kind of the property e.g. primitive property, complex property
   * @param value       Value of the property.
   */
  public Property(final String type, final String name, final ValueType valueType, final Object value) {
    this(type, name);
    setValue(valueType, value);
  }

  /**
   * Get name of property.
   * @return name of property
   */
  public String getName() {
    return name;
  }

  /**
   * Set name of property.
   * @param name name of property
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Check if this property is <code>null</code> (value == null) or the type is <code>"Edm.Null"</code>.
   * @return <code>true</code> if this property is <code>null</code> (value == null)
   *          or the type is <code>"Edm.Null"</code>. Otherwise <code>false</code>.
   */
  @Override
  public boolean isNull() {
    return getValue() == null || "Edm.Null".equals(getType());
  }
  
  /**
   * Gets operations.
   *
   * @return operations.
   */
  public List<Operation> getOperations() {
    return operations;
  }  

  @Override
  public boolean equals(final Object o) {
    return super.equals(o)
        && (name == null ? ((Property) o).name == null : name.equals(((Property) o).name));
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
