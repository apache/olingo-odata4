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
package org.apache.olingo.commons.api.domain;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

import java.net.URI;

/**
 * Entry point for generating OData domain objects.
 */
public interface ODataObjectFactory {

  /**
   * Instantiates a new entity set.
   * 
   * @return entity set.
   */
  ODataEntitySet newEntitySet();

  /**
   * Instantiates a new entity set.
   * 
   * @param next next link.
   * @return entity set.
   */
  ODataEntitySet newEntitySet(URI next);

  /**
   * Instantiates a new entity.
   * 
   * @param typeName OData entity type name.
   * @return entity.
   */
  ODataEntity newEntity(FullQualifiedName typeName);

  /**
   * Instantiates a new entity.
   * 
   * @param typeName OData entity type name.
   * @param link self link.
   * @return entity.
   */
  ODataEntity newEntity(FullQualifiedName typeName, URI link);

  /**
   * Instantiates a new entity set (for deep insert).
   * 
   * @param name name.
   * @param entitySet entity set.
   * @return in-line entity set.
   */
  ODataInlineEntitySet newDeepInsertEntitySet(String name, ODataEntitySet entitySet);

  /**
   * Instantiates a new entity (for deep insert).
   * 
   * @param name name.
   * @param entity entity.
   * @return in-line entity.
   */
  ODataInlineEntity newDeepInsertEntity(String name, ODataEntity entity);

  /**
   * Instantiates a new entity set navigation link.
   * 
   * @param name name.
   * @param link link.
   * @return entity set navigation link.
   */
  ODataLink newEntitySetNavigationLink(String name, URI link);

  /**
   * Instantiates a new singleton entity.
   *
   * @param typeName OData singleton entity type name.
   * @return new singleton entity.
   */
  ODataSingleton newSingleton(FullQualifiedName typeName);

  /**
   * Instantiates a new entity navigation link.
   *
   * @param name name.
   * @param link link.
   * @return entity navigation link.
   */
  ODataLink newEntityNavigationLink(String name, URI link);

  ODataLink newAssociationLink(String name, URI link);

  ODataLink newMediaEditLink(String name, URI link);

  ODataPrimitiveValue.Builder newPrimitiveValueBuilder();

  ODataEnumValue newEnumValue(String typeName, String value);

  ODataComplexValue newComplexValue(String typeName);

  ODataCollectionValue<ODataValue> newCollectionValue(String typeName);

  /**
   * Instantiates a new primitive property.
   * 
   * @param name name.
   * @param value primitive value.
   * @return primitive property.
   */
  ODataProperty newPrimitiveProperty(String name, ODataPrimitiveValue value);

  /**
   * Instantiates a new complex property.
   * 
   * @param name name.
   * @param value value.
   * @return complex property.
   */
  ODataProperty newComplexProperty(String name, ODataComplexValue value);

  /**
   * Instantiates a new collection property.
   * 
   * @param name name.
   * @param value value.
   * @return collection property.
   */
  ODataProperty newCollectionProperty(String name, ODataCollectionValue<? extends ODataValue> value);

  /**
   * Instantiates a new enum property.
   *
   * @param name name.
   * @param value value.
   * @return new enum property.
   */
  ODataProperty newEnumProperty(String name, ODataEnumValue value);

  /**
   * Instantiates a new delta property.
   *
   * @return new delta property.
   */
  ODataDelta newDelta();

  /**
   * Instantiates a new delta property.
   *
   * @param next next link for delta property
   * @return new delta property.
   */
  ODataDelta newDelta(URI next);
}
