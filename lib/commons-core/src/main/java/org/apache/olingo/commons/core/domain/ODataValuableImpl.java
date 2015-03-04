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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataEnumValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValuable;
import org.apache.olingo.commons.api.domain.ODataValue;

public class ODataValuableImpl implements ODataValuable {

  private final ODataValue value;

  public ODataValuableImpl(final ODataValue value) {
    this.value = value;
  }

  @Override
  public ODataValue getValue() {
    return value;
  }

  @Override
  public boolean hasNullValue() {
    return value == null;
  }

  @Override
  public boolean hasPrimitiveValue() {
    return !hasNullValue() && value.isPrimitive();
  }

  @Override
  public ODataPrimitiveValue getPrimitiveValue() {
    return hasPrimitiveValue() ? value.asPrimitive() : null;
  }

  @Override
  public boolean hasCollectionValue() {
    return !hasNullValue() && value.isCollection();
  }

  @Override
  public ODataCollectionValue<ODataValue> getCollectionValue() {
    return hasCollectionValue()
        ? getValue().<ODataValue> asCollection()
        : null;
  }

  @Override
  public boolean hasComplexValue() {
    return !hasNullValue() && value.isComplex();
  }

  @Override
  public ODataComplexValue getComplexValue() {
    return hasComplexValue()
        ? getValue().asComplex()
        : null;
  }

  @Override
  public boolean hasEnumValue() {
    return !hasNullValue() && getValue().isEnum();
  }

  @Override
  public ODataEnumValue getEnumValue() {
    return hasEnumValue()
        ? getValue().asEnum()
        : null;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
