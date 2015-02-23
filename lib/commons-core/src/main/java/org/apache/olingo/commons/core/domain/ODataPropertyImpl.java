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
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataAnnotatable;
import org.apache.olingo.commons.api.domain.ODataAnnotation;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataEnumValue;
import org.apache.olingo.commons.api.domain.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.ODataValuable;
import org.apache.olingo.commons.api.domain.ODataValue;

import java.util.ArrayList;
import java.util.List;

public class ODataPropertyImpl implements ODataProperty, ODataAnnotatable, ODataValuable {


  private final List<ODataAnnotation> annotations = new ArrayList<ODataAnnotation>();
  private final String name;
  private final ODataValue value;
  private final ODataValuable valuable;

  public ODataPropertyImpl(final String name, final ODataValue value) {
    this.name = name;
    this.value = value;
    this.valuable = new ODataValuableImpl(value);
  }

  /**
   * Returns property name.
   *
   * @return property name.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Returns property value.
   *
   * @return property value.
   */
  @Override
  public ODataValue getValue() {
    return value;
  }

  /**
   * Checks if has null value.
   *
   * @return 'TRUE' if has null value; 'FALSE' otherwise.
   */
  @Override
  public boolean hasNullValue() {
    return value == null || value.isPrimitive() && value.asPrimitive().toValue() == null;
  }

  /**
   * Checks if has primitive value.
   *
   * @return 'TRUE' if has primitive value; 'FALSE' otherwise.
   */
  @Override
  public boolean hasPrimitiveValue() {
    return !hasNullValue() && value.isPrimitive();
  }

  /**
   * Gets primitive value.
   *
   * @return primitive value if exists; null otherwise.
   */
  @Override
  public ODataPrimitiveValue getPrimitiveValue() {
    return hasPrimitiveValue() ? value.asPrimitive() : null;
  }

  /**
   * Checks if has complex value.
   *
   * @return 'TRUE' if has complex value; 'FALSE' otherwise.
   */
  @Override
  public boolean hasComplexValue() {
    return !hasNullValue() && value.isComplex();
  }

  /**
   * Checks if has collection value.
   *
   * @return 'TRUE' if has collection value; 'FALSE' otherwise.
   */
  @Override
  public boolean hasCollectionValue() {
    return !hasNullValue() && value.isCollection();
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
  public boolean hasEnumValue() {
    return valuable.hasEnumValue();
  }

  @Override
  public ODataEnumValue getEnumValue() {
    return valuable.getEnumValue();
  }

  @Override
  public ODataComplexValue<ODataProperty> getComplexValue() {
    return valuable.getComplexValue();
  }

  @Override
  public ODataLinkedComplexValue getLinkedComplexValue() {
    return valuable.getLinkedComplexValue();
  }

  @Override
  public ODataCollectionValue<ODataValue> getCollectionValue() {
    return valuable.getCollectionValue();
  }

  @Override
  public List<ODataAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public String toString() {
    return "ODataPropertyImpl{"
        + "name=" + getName()
        + ",valuable=" + valuable
        + ", annotations=" + annotations
        + '}';
  }
}
