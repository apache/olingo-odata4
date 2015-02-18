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

import org.apache.olingo.commons.api.domain.ODataEnumValue;
import org.apache.olingo.commons.api.domain.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.ODataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ODataCollectionValueImpl extends AbstractODataCollectionValue<ODataValue> implements ODataValue {

  public ODataCollectionValueImpl(final String typeName) {
    super(typeName);
  }

  @Override
  protected ODataCollectionValueImpl getThis() {
    return this;
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
  public boolean isLinkedComplex() {
    return false;
  }

  @Override
  public ODataLinkedComplexValue asLinkedComplex() {
    return null;
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
}
