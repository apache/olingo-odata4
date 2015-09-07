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
 * Data representation for a property.
 */
public class Property extends Valuable {

  private String name;

  public Property() {}

  public Property(final String type, final String name) {
    this.name = name;
    super.setType(type);
  }

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
}
