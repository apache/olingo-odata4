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
package org.apache.olingo.commons.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.EnumValue;
import org.apache.olingo.commons.api.data.GeospatialValue;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.NullValue;
import org.apache.olingo.commons.api.data.PrimitiveValue;
import org.apache.olingo.commons.api.data.Value;

public abstract class AbstractValue implements Value {

  @Override
  public boolean isNull() {
    return false;
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }

  @Override
  public boolean isGeospatial() {
    return false;
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public boolean isLinkedComplex() {
    return false;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public NullValue asNull() {
    return isNull() ? (NullValue) this : null;
  }

  @Override
  public PrimitiveValue asPrimitive() {
    return isPrimitive() ? (PrimitiveValue) this : null;
  }

  @Override
  public GeospatialValue asGeospatial() {
    return isGeospatial() ? (GeospatialValue) this : null;
  }

  @Override
  public EnumValue asEnum() {
    return isEnum() ? (EnumValue) this : null;
  }

  @Override
  public ComplexValue asComplex() {
    return isComplex() ? (ComplexValue) this : null;
  }

  @Override
  public LinkedComplexValue asLinkedComplex() {
    return isLinkedComplex() ? (LinkedComplexValue) this : null;
  }

  @Override
  public CollectionValue asCollection() {
    return isCollection() ? (CollectionValue) this : null;
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
