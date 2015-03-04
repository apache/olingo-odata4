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
package org.apache.olingo.commons.core.domain;

import org.apache.olingo.commons.api.domain.AbstractODataValue;
import org.apache.olingo.commons.api.domain.ODataEnumValue;

public class ODataEnumValueImpl extends AbstractODataValue implements ODataEnumValue {

  private final String value;

  public ODataEnumValueImpl(final String typeName, final String value) {
    super(typeName);
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public boolean isEnum() {
    return true;
  }

  @Override
  public ODataEnumValue asEnum() {
    return this;
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public String toString() {
    return getTypeName() + "'" + getValue() + "'";

  }

}
