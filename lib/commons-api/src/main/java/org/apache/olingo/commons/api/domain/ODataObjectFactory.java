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
package org.apache.olingo.commons.api.domain;

import java.net.URI;

/**
 * Entry point for generating OData domain objects.
 *
 * @see ODataEntitySet
 * @see ODataEntity
 * @see ODataProperty
 * @see ODataLink
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
   * @param name OData entity name.
   * @return entity.
   */
  ODataEntity newEntity(String name);

  /**
   * Instantiates a new entity.
   *
   * @param name OData entity name.
   * @param link self link.
   * @return entity.
   */
  ODataEntity newEntity(String name, URI link);

  /**
   * Instantiates a new in-line entity set.
   *
   * @param name name.
   * @param link edit link.
   * @param entitySet entity set.
   * @return in-line entity set.
   */
  ODataInlineEntitySet newInlineEntitySet(String name, URI link, ODataEntitySet entitySet);

  /**
   * Instantiates a new in-line entity set.
   *
   * @param name name.
   * @param baseURI base URI.
   * @param href href.
   * @param entitySet entity set.
   * @return in-line entity set.
   */
  ODataInlineEntitySet newInlineEntitySet(String name, URI baseURI, String href, ODataEntitySet entitySet);

  /**
   * Instantiates a new in-line entity.
   *
   * @param name name.
   * @param link edit link.
   * @param entity entity.
   * @return in-line entity.
   */
  ODataInlineEntity newInlineEntity(String name, URI link, ODataEntity entity);

  /**
   * Instantiates a new in-line entity.
   *
   * @param name name.
   * @param baseURI base URI.
   * @param href href.
   * @param entity entity.
   * @return in-line entity.
   */
  ODataInlineEntity newInlineEntity(String name, URI baseURI, String href, ODataEntity entity);

  /**
   * Instantiates a new entity navigation link.
   *
   * @param name name.
   * @param link link.
   * @return entity navigation link.
   */
  ODataLink newEntityNavigationLink(String name, URI link);

  /**
   * Instantiates a new entity navigation link.
   *
   * @param name name.
   * @param baseURI base URI.
   * @param href href.
   * @return entity navigation link.
   */
  ODataLink newEntityNavigationLink(String name, URI baseURI, String href);

  /**
   * Instantiates a new entity set navigation link.
   *
   * @param name name.
   * @param link link.
   * @return entity set navigation link.
   */
  ODataLink newFeedNavigationLink(String name, URI link);

  /**
   * Instantiates a new entity set navigation link.
   *
   * @param name name.
   * @param baseURI base URI.
   * @param href href.
   * @return entity set navigation link.
   */
  ODataLink newFeedNavigationLink(String name, URI baseURI, String href);

  /**
   * Instantiates a new association link.
   *
   * @param name name.
   * @param link link.
   * @return association link.
   */
  ODataLink newAssociationLink(String name, URI link);

  /**
   * Instantiates a new association link.
   *
   * @param name name.
   * @param baseURI base URI.
   * @param href href.
   * @return association link.
   */
  ODataLink newAssociationLink(String name, URI baseURI, String href);

  /**
   * Instantiates a new media-edit link.
   *
   * @param name name.
   * @param link link.
   * @return media-edit link.
   */
  ODataLink newMediaEditLink(String name, URI link);

  /**
   * Instantiates a new media-edit link.
   *
   * @param name name.
   * @param baseURI base URI.
   * @param href href.
   * @return media-edit link.
   */
  ODataLink newMediaEditLink(String name, URI baseURI, String href);

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
  ODataProperty newCollectionProperty(String name, ODataCollectionValue value);
}
