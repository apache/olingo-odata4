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
package org.apache.olingo.commons.core.data;

import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

public class PropertyImpl extends AbstractValuable implements Property {

  private String name;
  private String type;

  public PropertyImpl() {}

  public PropertyImpl(final String type, final String name) {
    this.name = name;
    this.type = type;
  }

  public PropertyImpl(final String type, final String name, final ValueType valueType, final Object value) {
    this(name, type);
    setValue(valueType, value);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public boolean isNull() {
    return getValue() == null || "Edm.Null".equals(type); // TODO: improve
  }
}
