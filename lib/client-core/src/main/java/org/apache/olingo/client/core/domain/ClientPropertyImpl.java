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
package org.apache.olingo.client.core.domain;

import org.apache.olingo.client.api.domain.ClientAnnotatable;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.domain.ClientValue;

import java.util.ArrayList;
import java.util.List;

public final class ClientPropertyImpl implements ClientProperty, ClientAnnotatable, ClientValuable {


  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();
  private final String name;
  private final ClientValue value;
  private final ClientValuable valuable;

  public ClientPropertyImpl(final String name, final ClientValue value) {
    this.name = name;
    this.value = value;
    this.valuable = new ClientValuableImpl(value);
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
  public ClientValue getValue() {
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
  public ClientPrimitiveValue getPrimitiveValue() {
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ClientPropertyImpl that = (ClientPropertyImpl) o;

    if (annotations != null ? !annotations.equals(that.annotations) : that.annotations != null) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }
    if (value != null ? !value.equals(that.value) : that.value != null) {
      return false;
    }
    return !(valuable != null ? !valuable.equals(that.valuable) : that.valuable != null);

  }

  @Override
  public int hashCode() {
    int result = annotations != null ? annotations.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (value != null ? value.hashCode() : 0);
    result = 31 * result + (valuable != null ? valuable.hashCode() : 0);
    return result;
  }

  @Override
  public boolean hasEnumValue() {
    return valuable.hasEnumValue();
  }

  @Override
  public ClientEnumValue getEnumValue() {
    return valuable.getEnumValue();
  }

  @Override
  public ClientComplexValue getComplexValue() {
    return valuable.getComplexValue();
  }

  @Override
  public ClientCollectionValue<ClientValue> getCollectionValue() {
    return valuable.getCollectionValue();
  }

  @Override
  public List<ClientAnnotation> getAnnotations() {
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
