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
package org.apache.olingo.client.api.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * OData collection property value.
 */
public class ODataCollectionValue extends AbstractODataValue implements Iterable<ODataValue> {

  private static final long serialVersionUID = -3665659846001987187L;

  /**
   * Type name;
   */
  private final String typeName;

  /**
   * Values.
   */
  private final List<ODataValue> values = new ArrayList<ODataValue>();

  /**
   * Constructor.
   *
   * @param typeName type name.
   */
  public ODataCollectionValue(final String typeName) {
    this.typeName = typeName;
  }

  /**
   * Adds a value to the collection.
   *
   * @param value value to be added.
   */
  public void add(final ODataValue value) {
    if (value.isPrimitive() || value.isComplex()) {
      values.add(value);
    }
  }

  /**
   * Value iterator.
   *
   * @return value iterator.
   */
  @Override
  public Iterator<ODataValue> iterator() {
    return values.iterator();
  }

  /**
   * Gets value type name.
   *
   * @return value type name.
   */
  public String getType() {
    return typeName;
  }

  /**
   * Gets collection size.
   *
   * @return collection size.
   */
  public int size() {
    return values.size();
  }

  /**
   * Checks if collection is empty.
   *
   * @return 'TRUE' if empty; 'FALSE' otherwise.
   */
  public boolean isEmpty() {
    return values.isEmpty();
  }
}
