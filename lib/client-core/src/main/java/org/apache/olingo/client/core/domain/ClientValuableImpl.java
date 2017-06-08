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

import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.domain.ClientValue;

public class ClientValuableImpl implements ClientValuable {

  protected final ClientValue value;

  public ClientValuableImpl(final ClientValue value) {
    this.value = value;
  }

  @Override
  public ClientValue getValue() {
    return value;
  }

  @Override
  public boolean hasNullValue() {
    return value == null;
  }

  @Override
  public boolean hasPrimitiveValue() {
    return value != null && value.isPrimitive();
  }

  @Override
  public ClientPrimitiveValue getPrimitiveValue() {
    return hasPrimitiveValue() ? value.asPrimitive() : null;
  }

  @Override
  public boolean hasCollectionValue() {
    return !hasNullValue() && value.isCollection();
  }

  @Override
  public ClientCollectionValue<ClientValue> getCollectionValue() {
    return hasCollectionValue() ? getValue().<ClientValue> asCollection() : null;
  }

  @Override
  public boolean hasComplexValue() {
    return !hasNullValue() && value.isComplex();
  }

  @Override
  public ClientComplexValue getComplexValue() {
    return hasComplexValue() ? getValue().asComplex() : null;
  }

  @Override
  public boolean hasEnumValue() {
    return !hasNullValue() && getValue().isEnum();
  }

  @Override
  public ClientEnumValue getEnumValue() {
    return hasEnumValue() ? getValue().asEnum() : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ClientValuableImpl that = (ClientValuableImpl) o;
    return !(value != null ? !value.equals(that.value) : that.value != null);
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "ClientValuableImpl{" + "value=" + value + '}';
  }
}
