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

import org.apache.olingo.commons.api.edm.geo.Geospatial;

/**
 * Defines a value with an according type.
 */
public abstract class Valuable extends Annotatable {

  private ValueType valueType = null;
  private Object value = null;
  private String type;

  /**
   * Check if according value is <code>null</code>.
   * @return <code>true</code> if value is <code>null</code>, otherwise <code>false</code>
   */
  public boolean isNull() {
    return value == null;
  }

  /**
   * Get string representation of type (can be null if not set).
   * @return string representation of type (can be null if not set)
   */
  public String getType() {
    return type;
  }

  /**
   * Set string representation of type.
   * @param type string representation of type
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Check if Valuable contains a PRIMITIVE or COLLECTION_PRIMITIVE ValueType
   *
   * @return true if ValueType is a PRIMITIVE or COLLECTION_PRIMITIVE, otherwise false
   */
  public boolean isPrimitive() {
    return valueType == ValueType.PRIMITIVE || valueType == ValueType.COLLECTION_PRIMITIVE;
  }

  /**
   * Check if Valuable contains a GEOSPATIAL or COLLECTION_GEOSPATIAL ValueType
   *
   * @return true if ValueType is a GEOSPATIAL or COLLECTION_GEOSPATIAL, otherwise false
   */
  public boolean isGeospatial() {
    return valueType == ValueType.GEOSPATIAL || valueType == ValueType.COLLECTION_GEOSPATIAL;
  }

  /**
   * Check if Valuable contains a ENUM or COLLECTION_ENUM ValueType
   *
   * @return true if ValueType is a ENUM or COLLECTION_ENUM, otherwise false
   */
  public boolean isEnum() {
    return valueType == ValueType.ENUM || valueType == ValueType.COLLECTION_ENUM;
  }

  /**
   * Check if Valuable contains a COMPLEX or COLLECTION_COMPLEX ValueType
   *
   * @return true if ValueType is a COMPLEX or COLLECTION_COMPLEX, otherwise false
   */
  public boolean isComplex() {
    return valueType == ValueType.COMPLEX || valueType == ValueType.COLLECTION_COMPLEX;
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
    return isPrimitive() && !isCollection() ? value : null;
  }

  /**
   * Get the value in its geospatial representation or null if it is not based on a geospatial ValueType
   *
   * @return geospatial representation or null if it is not based on a geospatial ValueType
   */
  public Geospatial asGeospatial() {
    return isGeospatial() && !isCollection() ? (Geospatial) value : null;
  }

  /**
   * Get the value in its enum representation or null if it is not based on a enum ValueType
   *
   * @return enum representation or null if it is not based on a enum ValueType
   */
  public Object asEnum() {
    return isEnum() && !isCollection() ? value : null;
  }

  /**
   * Get the value in its complex representation or null if it is not based on a complex ValueType
   *
   * @return primitive complex or null if it is not based on a complex ValueType
   */
  public ComplexValue asComplex() {
    return isComplex() && !isCollection() ? (ComplexValue) value : null;
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

  /**
   * Set value and value type.
   * @param valueType value type
   * @param value value
   */
  public void setValue(final ValueType valueType, final Object value) {
    this.valueType = valueType;
    this.value = value;
  }

  /**
   * Get value type for this valuable.
   * @return value type for this valuable
   */
  public ValueType getValueType() {
    return valueType;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Valuable other = (Valuable) o;
    return getAnnotations().equals(other.getAnnotations())
        && (valueType == null ? other.valueType == null : valueType.equals(other.valueType))
        && (value == null ? other.value == null : value.equals(other.value))
        && (type == null ? other.type == null : type.equals(other.type));
  }

  @Override
  public int hashCode() {
    int result = getAnnotations().hashCode();
    result = 31 * result + (valueType == null ? 0 : valueType.hashCode());
    result = 31 * result + (value == null ? 0 : value.hashCode());
    result = 31 * result + (type == null ? 0 : type.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return value == null ? "null" : value.toString();
  }
}
