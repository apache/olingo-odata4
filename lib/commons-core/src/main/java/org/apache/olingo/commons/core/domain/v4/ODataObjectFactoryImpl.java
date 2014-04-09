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

import java.net.URI;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.domain.AbstractODataObjectFactory;

public class ODataObjectFactoryImpl extends AbstractODataObjectFactory implements ODataObjectFactory {

  public ODataObjectFactoryImpl(final ODataServiceVersion version) {
    super(version);
  }

  @Override
  public ODataEntitySet newEntitySet() {
    return new ODataEntitySetImpl();
  }

  @Override
  public ODataEntitySet newEntitySet(final URI next) {
    return new ODataEntitySetImpl(next);
  }

  @Override
  public ODataEntity newEntity(final FullQualifiedName typeName) {
    return new ODataEntityImpl(typeName);
  }

  @Override
  public ODataEntity newEntity(final FullQualifiedName typeName, final URI link) {
    final ODataEntityImpl result = new ODataEntityImpl(typeName);
    result.setLink(link);
    return result;
  }

  @Override
  public ODataPrimitiveValue.Builder newPrimitiveValueBuilder() {
    return new ODataPrimitiveValueImpl.BuilderImpl(version);
  }

  @Override
  public ODataEnumValue newEnumValue(final String typeName, final String value) {
    return new ODataEnumValueImpl(typeName, value);
  }

  @Override
  public ODataComplexValue<ODataProperty> newComplexValue(final String typeName) {
    return new ODataComplexValueImpl(typeName);
  }

  @Override
  public ODataLinkedComplexValue newLinkedComplexValue(final String typeName) {
    return new ODataComplexValueImpl(typeName);
  }

  @Override
  public ODataCollectionValue<ODataValue> newCollectionValue(final String typeName) {
    return new ODataCollectionValueImpl(typeName);
  }

  @Override
  public ODataProperty newPrimitiveProperty(final String name, final ODataPrimitiveValue value) {
    return new ODataPropertyImpl(name, value);
  }

  @Override
  public ODataProperty newComplexProperty(final String name,
          final ODataComplexValue<? extends CommonODataProperty> value) {

    return new ODataPropertyImpl(name, value);
  }

  @Override
  public ODataProperty newCollectionProperty(final String name,
          final ODataCollectionValue<? extends org.apache.olingo.commons.api.domain.ODataValue> value) {

    return new ODataPropertyImpl(name, value);
  }

  @Override
  public ODataProperty newEnumProperty(final String name, final ODataEnumValue value) {
    return new ODataPropertyImpl(name, value);
  }

}
