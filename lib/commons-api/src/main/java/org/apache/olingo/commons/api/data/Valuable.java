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

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.commons.api.edm.geo.Geospatial;

public abstract class Valuable extends Annotatable {

  private ValueType valueType = null;
  private Object value = null;
  private String type;

  public boolean isNull() {
    return value == null;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Check if Valuable contains a PRIMITIVE or COLLECTION_PRIMITIVE ValueType
   *
   * @return true if ValueType is a PRIMITIVE or COLLECTION_PRIMITIVE, otherwise false
   */
  public boolean isPrimitive() {
    if (isCollection()) {
      return valueType.getBaseType() == ValueType.PRIMITIVE;
    }
    return valueType == ValueType.PRIMITIVE;
  }

  /**
   * Check if Valuable contains a GEOSPATIAL or COLLECTION_GEOSPATIAL ValueType
   *
   * @return true if ValueType is a GEOSPATIAL or COLLECTION_GEOSPATIAL, otherwise false
   */
  public boolean isGeospatial() {
    if (isCollection()) {
      return valueType.getBaseType() == ValueType.GEOSPATIAL;
    }
    return valueType == ValueType.GEOSPATIAL;
  }

  /**
   * Check if Valuable contains a ENUM or COLLECTION_ENUM ValueType
   *
   * @return true if ValueType is a ENUM or COLLECTION_ENUM, otherwise false
   */
  public boolean isEnum() {
    if (isCollection()) {
      return valueType.getBaseType() == ValueType.ENUM;
    }
    return valueType == ValueType.ENUM;
  }

  /**
   * Check if Valuable contains a COMPLEX or COLLECTION_COMPLEX ValueType
   *
   * @return true if ValueType is a COMPLEX or COLLECTION_COMPLEX, otherwise false
   */
  public boolean isComplex() {
    if (isCollection()) {
      return valueType.getBaseType() == ValueType.COMPLEX;
    }
    return valueType == ValueType.COMPLEX;
  }

  /**
   * Check if Valuable contains a COLLECTION_* ValueType
   *
   * @return true if ValueType is a COLLECTION_*, otherwise false
   */
  public boolean isCollection() {
    return valueType != null && valueType != valueType.getBaseType();
  }

  /**
   * Get the value in its primitive representation or null if it is not based on a primitive ValueType
   *
   * @return primitive representation or null if it is not based on a primitive ValueType
   */
  public Object asPrimitive() {
    if (isCollection()) {
      return null;
    }
    return isPrimitive() ? value : null;
  }

  /**
   * Get the value in its geospatial representation or null if it is not based on a geospatial ValueType
   *
   * @return geospatial representation or null if it is not based on a geospatial ValueType
   */
  public Geospatial asGeospatial() {
    if (isCollection()) {
      return null;
    }
    return isGeospatial() ? (Geospatial) value : null;
  }

  /**
   * Get the value in its enum representation or null if it is not based on a enum ValueType
   *
   * @return enum representation or null if it is not based on a enum ValueType
   */
  public Object asEnum() {
    if (isCollection()) {
      return null;
    }
    return isEnum() ? value : null;
  }

  /**
   * Get the value in its complex representation or null if it is not based on a complex ValueType
   *
   * @return primitive complex or null if it is not based on a complex ValueType
   */
  public ComplexValue asComplex() {
    if (isCollection()) {
      return null;
    }
    return isComplex() ? (ComplexValue) value : null;
  }

  /**
   * Get the value as collection or null if it is not a collection ValueType
   *
   * @return collection or null if it is not a collection ValueType
   */
  public List<?> asCollection() {
    return isCollection() ? (List<?>) value : null;
  }

  /**
   * Get the value
   *
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  public void setValue(final ValueType valueType, final Object value) {
    this.valueType = valueType;
    this.value = value;
  }

  public ValueType getValueType() {
    return valueType;
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
