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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ClientPropertyImpl)) {
      return false;
    }
    ClientPropertyImpl other = (ClientPropertyImpl) obj;
    if (annotations == null) {
      if (other.annotations != null) {
        return false;
      }
    } else if (!annotations.equals(other.annotations)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (valuable == null) {
      if (other.valuable != null) {
        return false;
      }
    } else if (!valuable.equals(other.valuable)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((valuable == null) ? 0 : valuable.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
