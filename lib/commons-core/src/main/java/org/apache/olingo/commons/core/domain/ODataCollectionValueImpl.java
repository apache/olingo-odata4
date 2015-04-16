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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.commons.api.domain.AbstractODataValue;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataEnumValue;
import org.apache.olingo.commons.api.domain.ODataValue;

public class ODataCollectionValueImpl<OV extends ODataValue> extends AbstractODataValue
        implements ODataCollectionValue<OV>, ODataValue {

  /**
   * Constructor.
   *
   * @param typeName type name.
   */
  public ODataCollectionValueImpl(final String typeName) {
    super(typeName == null || typeName.startsWith("Collection(") ? typeName : "Collection(" + typeName + ")");
  }

  @Override
  public boolean isEnum() {
    return false;
  }

  @Override
  public ODataEnumValue asEnum() {
    return null;
  }

  @Override
  public boolean isComplex() {
    return false;
  }

  @Override
  public Collection<Object> asJavaCollection() {
    final List<Object> result = new ArrayList<Object>();
    for (ODataValue value : values) {
      if (value.isPrimitive()) {
        result.add(value.asPrimitive().toValue());
      } else if (value.isComplex()) {
        result.add(value.asComplex().asJavaMap());
      } else if (value.isCollection()) {
        result.add(value.asCollection().asJavaCollection());
      } else if (value.isEnum()) {
        result.add(value.asEnum().toString());
      }
    }

    return result;
  }

  /**
   * Values.
   */
  protected final List<OV> values = new ArrayList<OV>();

  /**
   * Adds a value to the collection.
   *
   * @param value value to be added.
   */
  @Override
  @SuppressWarnings("unchecked")
  public ODataCollectionValue<OV> add(final ODataValue value) {
    values.add((OV) value);
    return this;
  }

  /**
   * Value iterator.
   *
   * @return value iterator.
   */
  @Override
  public Iterator<OV> iterator() {
    return values.iterator();
  }

  /**
   * Gets collection size.
   *
   * @return collection size.
   */
  @Override
  public int size() {
    return values.size();
  }

  /**
   * Checks if collection is empty.
   *
   * @return 'TRUE' if empty; 'FALSE' otherwise.
   */
  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }


}
