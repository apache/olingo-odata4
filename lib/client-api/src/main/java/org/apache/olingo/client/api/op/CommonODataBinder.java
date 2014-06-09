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
package org.apache.olingo.client.api.op;

import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;

public interface CommonODataBinder {

  /**
   * Gets a <tt>EntitySet</tt> from the given OData entity set.
   *
   * @param entitySet OData entity set.
   * @return {@link EntitySet} object.
   */
  EntitySet getEntitySet(CommonODataEntitySet entitySet);

  /**
   * Gets an <tt>Entity</tt> from the given OData entity.
   *
   * @param entity OData entity.
   * @return {@link Entity} object.
   */
  Entity getEntity(CommonODataEntity entity);

  /**
   * Gets a <tt>Link</tt> from the given OData link.
   *
   * @param link OData link.
   * @return <tt>Link</tt> object.
   */
  Link getLink(ODataLink link);

  /**
   * Gets a <tt>Property</tt> from the given OData property.
   *
   * @param property OData property.
   * @return <tt>Property</tt> object.
   */
  Property getProperty(CommonODataProperty property);

  /**
   * Adds the given property to the given complex value.
   *
   * @param complex OData complex value.
   * @param property OData property.
   */
  void add(ODataComplexValue<CommonODataProperty> complex, CommonODataProperty property);

  /**
   * Adds the given property to the given entity.
   *
   * @param entity OData entity.
   * @param property OData property.
   * @return whether add was successful or not.
   */
  boolean add(CommonODataEntity entity, CommonODataProperty property);

  /**
   * Gets <tt>ODataServiceDocument</tt> from the given service document resource.
   *
   * @param resource service document resource.
   * @return <tt>ODataServiceDocument</tt> object.
   */
  ODataServiceDocument getODataServiceDocument(ServiceDocument resource);

  /**
   * Gets <tt>ODataEntitySet</tt> from the given entity set resource.
   *
   * @param resource entity set resource.
   * @return {@link CommonODataEntitySet} object.
   */
  CommonODataEntitySet getODataEntitySet(ResWrap<EntitySet> resource);

  /**
   * Gets <tt>ODataEntity</tt> from the given entity resource.
   *
   * @param resource entity resource.
   * @return {@link CommonODataEntity} object.
   */
  CommonODataEntity getODataEntity(ResWrap<Entity> resource);

  /**
   * Gets an <tt>ODataProperty</tt> from the given property resource.
   *
   * @param resource property resource.
   * @return {@link CommonODataProperty} object.
   */
  CommonODataProperty getODataProperty(ResWrap<Property> resource);
}
