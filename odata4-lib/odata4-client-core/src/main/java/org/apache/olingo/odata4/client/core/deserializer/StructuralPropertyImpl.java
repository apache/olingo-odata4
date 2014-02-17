/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.client.core.deserializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata4.client.api.deserializer.StructuralProperty;
import org.apache.olingo.odata4.client.api.deserializer.Value;

public class StructuralPropertyImpl implements StructuralProperty {

  private final List<Value> values;
  private final String name;
  private final boolean containsCollection;

  public StructuralPropertyImpl(final String name, final Value value) {
    this(name, false, value);
  }

  public StructuralPropertyImpl(final String name, final List<Value> values) {
    // XXX: ugly -> refactore
    this(name, true, values.toArray(new Value[0]));
  }

  public StructuralPropertyImpl(final String name, final boolean asCollection, final Value... value) {
    if (value == null || value.length == 0) {
      throw new IllegalArgumentException("Missing or NULL value argument.");
    }

    containsCollection = asCollection;
    this.name = name;
    values = new ArrayList<Value>(value.length);
    for (Value v : value) {
      values.add(v);
    }
  }

  @Override
  public Value getValue() {
    return values.get(0);
  }

  @Override
  public List<Value> getValues() {
    return Collections.unmodifiableList(values);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean containsCollection() {
    return containsCollection;
  }

  @Override
  public String toString() {
    return "StructuralPropertyImpl [name=" + name + ", containsCollection=" + containsCollection
        + ", values=" + values + "]";
  }
}
