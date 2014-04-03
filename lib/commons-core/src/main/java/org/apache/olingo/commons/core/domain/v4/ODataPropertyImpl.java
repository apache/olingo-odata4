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
package org.apache.olingo.commons.core.domain.v4;

import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.core.domain.AbstractODataProperty;

public class ODataPropertyImpl extends AbstractODataProperty implements ODataProperty {

  private static final long serialVersionUID = 4851331227420757747L;

  public ODataPropertyImpl(final String name, final org.apache.olingo.commons.api.domain.ODataValue value) {
    super(name, value);
  }

  @Override
  public boolean hasEnumValue() {
    return !hasNullValue() && getValue() instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
            && ((org.apache.olingo.commons.api.domain.v4.ODataValue) getValue()).isEnum();
  }

  @Override
  public ODataEnumValue getEnumValue() {
    return hasEnumValue()
            ? ((org.apache.olingo.commons.api.domain.v4.ODataValue) getValue()).asEnum()
            : null;
  }

  @Override
  public ODataComplexValue<ODataProperty> getComplexValue() {
    return hasComplexValue() ? getValue().<ODataProperty>asComplex() : null;
  }

  @Override
  public ODataLinkedComplexValue getLinkedComplexValue() {
    return hasComplexValue() && getValue() instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
            ? ((org.apache.olingo.commons.api.domain.v4.ODataValue) getValue()).asLinkedComplex()
            : null;
  }

  @Override
  public ODataCollectionValue<ODataValue> getCollectionValue() {
    return hasCollectionValue()
            ? getValue().<ODataValue>asCollection()
            : null;
  }

}
