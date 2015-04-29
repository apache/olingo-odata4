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

import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.domain.ClientValue;

public class ClientAnnotationImpl implements ClientAnnotation {

  private final String term;

  private final ClientValuable valuable;

  public ClientAnnotationImpl(final String term, final ClientValue value) {
    this.term = term;
    valuable = new ClientValuableImpl(value);
  }

  @Override
  public String getTerm() {
    return term;
  }

  @Override
  public ClientValue getValue() {
    return valuable.getValue();
  }

  @Override
  public boolean hasNullValue() {
    return valuable.hasNullValue();
  }

  @Override
  public boolean hasPrimitiveValue() {
    return valuable.hasPrimitiveValue();
  }

  @Override
  public ClientPrimitiveValue getPrimitiveValue() {
    return valuable.getPrimitiveValue();
  }

  @Override
  public boolean hasCollectionValue() {
    return valuable.hasCollectionValue();
  }

  @Override
  public ClientCollectionValue<ClientValue> getCollectionValue() {
    return valuable.getCollectionValue();
  }

  @Override
  public boolean hasComplexValue() {
    return valuable.hasComplexValue();
  }

  @Override
  public ClientComplexValue getComplexValue() {
    return valuable.getComplexValue();
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
  public String toString() {
    return "ODataPropertyImpl{"
        + "term=" + term
        + ",valuable=" + valuable
        + '}';
  }
}
