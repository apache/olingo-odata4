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
package org.apache.olingo.commons.api.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Abstract representation of an OData entity property value.
 */
public abstract class AbstractODataValue implements ODataValue {

  private static final long serialVersionUID = 7445422004232581877L;

  /**
   * Check is is a primitive value.
   *
   * @return 'TRUE' if primitive; 'FALSE' otherwise.
   */
  @Override
  public boolean isPrimitive() {
    return (this instanceof ODataPrimitiveValue);
  }

  /**
   * Casts to primitive value.
   *
   * @return primitive value.
   */
  @Override
  public ODataPrimitiveValue asPrimitive() {
    return isPrimitive() ? (ODataPrimitiveValue) this : null;
  }

  /**
   * Check is is a complex value.
   *
   * @return 'TRUE' if complex; 'FALSE' otherwise.
   */
  @Override
  public boolean isComplex() {
    return (this instanceof ODataComplexValue);
  }

  /**
   * Casts to complex value.
   *
   * @return complex value.
   */
  @Override
  public ODataComplexValue asComplex() {
    return isComplex() ? (ODataComplexValue) this : null;
  }

  /**
   * Check is is a collection value.
   *
   * @return 'TRUE' if collection; 'FALSE' otherwise.
   */
  @Override
  public boolean isCollection() {
    return (this instanceof ODataCollectionValue);
  }

  /**
   * Casts to collection value.
   *
   * @return collection value.
   */
  @Override
  public ODataCollectionValue asCollection() {
    return isCollection() ? (ODataCollectionValue) this : null;
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
