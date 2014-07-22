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

import org.apache.olingo.commons.api.domain.AbstractODataValue;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataComplexValue;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OData complex property value.
 * 
 * @param <OP> The actual ODataProperty interface.
 */
public abstract class AbstractODataComplexValue<OP extends CommonODataProperty>
    extends AbstractODataValue implements ODataComplexValue<OP> {

  /**
   * Complex type fields.
   */
  protected final Map<String, OP> fields = new LinkedHashMap<String, OP>();

  /**
   * Constructor.
   * 
   * @param typeName type name.
   */
  public AbstractODataComplexValue(final String typeName) {
    super(typeName);
  }

  protected abstract ODataComplexValue<OP> getThis();

  /**
   * Adds field to the complex type.
   * 
   * @param field field to be added.
   */
  @Override
  @SuppressWarnings("unchecked")
  public ODataComplexValue<OP> add(final CommonODataProperty field) {
    fields.put(field.getName(), (OP) field);
    return getThis();
  }

  /**
   * Gets field.
   * 
   * @param name name of the field to be retrieved.
   * @return requested field.
   */
  @Override
  public OP get(final String name) {
    return fields.get(name);
  }

  /**
   * Complex property fields iterator.
   * 
   * @return fields iterator.
   */
  @Override
  public Iterator<OP> iterator() {
    return fields.values().iterator();
  }

  /**
   * Gets number of fields.
   * 
   * @return number of fields.
   */
  @Override
  public int size() {
    return fields.size();
  }

  @Override
  public Map<String, Object> asJavaMap() {
    final Map<String, Object> result = new LinkedHashMap<String, Object>();
    for (Map.Entry<String, OP> entry : fields.entrySet()) {
      Object value = null;
      if (entry.getValue().hasPrimitiveValue()) {
        value = entry.getValue().getPrimitiveValue().toValue();
      } else if (entry.getValue().hasComplexValue()) {
        value = entry.getValue().getValue().asComplex().asJavaMap();
      } else if (entry.getValue().hasCollectionValue()) {
        value = entry.getValue().getValue().asCollection().asJavaCollection();
      }

      result.put(entry.getKey(), value);
    }

    return result;
  }
}
