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
package org.apache.olingo.commons.core.domain;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.olingo.commons.api.domain.AbstractODataValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.CommonODataProperty;

/**
 * OData complex property value.
 */
public class ODataComplexValueImpl extends AbstractODataValue implements ODataComplexValue {

  private static final long serialVersionUID = -1878555027714020431L;

  /**
   * Complex type fields.
   */
  private final Map<String, CommonODataProperty> fields = new LinkedHashMap<String, CommonODataProperty>();

  /**
   * Constructor.
   *
   * @param typeName type name.
   */
  public ODataComplexValueImpl(final String typeName) {
    super(typeName);
  }

  /**
   * Adds field to the complex type.
   *
   * @param field field to be added.
   */
  @Override
  public void add(final CommonODataProperty field) {
    fields.put(field.getName(), field);
  }

  /**
   * Gets field.
   *
   * @param name name of the field to be retrieved.
   * @return requested field.
   */
  @Override
  public CommonODataProperty get(final String name) {
    return fields.get(name);
  }

  /**
   * Complex property fields iterator.
   *
   * @return fields iterator.
   */
  @Override
  public Iterator<CommonODataProperty> iterator() {
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
}
