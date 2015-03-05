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

import java.net.URI;

import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataDelta;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataEnumValue;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataSingleton;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class ODataObjectFactoryImpl implements ODataObjectFactory {

  protected final ODataServiceVersion version;

  public ODataObjectFactoryImpl(final ODataServiceVersion version) {
    this.version = version;
  }

  @Override
  public ODataInlineEntitySet newDeepInsertEntitySet(final String name, final ODataEntitySet entitySet) {
    return new ODataInlineEntitySet(null, ODataLinkType.ENTITY_SET_NAVIGATION, name, entitySet);
  }

  @Override
  public ODataInlineEntity newDeepInsertEntity(final String name, final ODataEntity entity) {
    return new ODataInlineEntity(null, ODataLinkType.ENTITY_NAVIGATION, name, entity);
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
  public ODataSingleton newSingleton(final FullQualifiedName typeName) {
    return new ODataEntityImpl(typeName);
  }

  @Override
  public ODataLink newEntityNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setURI(link).
        setType(ODataLinkType.ENTITY_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newEntitySetNavigationLink(final String name, final URI link) {
    return new ODataLink.Builder().setURI(link).
        setType(ODataLinkType.ENTITY_SET_NAVIGATION).setTitle(name).build();
  }

  @Override
  public ODataLink newAssociationLink(final String name, final URI link) {
    return new ODataLink.Builder().setURI(link).
        setType(ODataLinkType.ASSOCIATION).setTitle(name).build();
  }

  @Override
  public ODataLink newMediaEditLink(final String name, final URI link) {
    return new ODataLink.Builder().setURI(link).
        setType(ODataLinkType.MEDIA_EDIT).setTitle(name).build();
  }

  @Override
  public ODataPrimitiveValue.Builder newPrimitiveValueBuilder() {
    return new ODataPrimitiveValueImpl.BuilderImpl();
  }

  @Override
  public ODataEnumValue newEnumValue(final String typeName, final String value) {
    return new ODataEnumValueImpl(typeName, value);
  }

  @Override
  public ODataComplexValue newComplexValue(final String typeName) {
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
  public ODataProperty newComplexProperty(final String name, final ODataComplexValue value) {

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

  @Override
  public ODataDelta newDelta() {
    return new ODataDeltaImpl();
  }

  @Override
  public ODataDelta newDelta(final URI next) {
    return new ODataDeltaImpl(next);
  }
}
