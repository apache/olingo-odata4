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

import java.io.Serializable;
import java.net.URI;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;

public interface CommonODataBinder extends Serializable {

  /**
   * Gets a <tt>Feed</tt> from the given OData entity set.
   *
   * @param feed OData entity set.
   * @param reference reference class.
   * @return <tt>Feed</tt> object.
   */
  Feed getFeed(CommonODataEntitySet feed, Class<? extends Feed> reference);

  /**
   * Gets an <tt>Entry</tt> from the given OData entity.
   *
   * @param entity OData entity.
   * @param reference reference class.
   * @return <tt>Entry</tt> object.
   */
  Entry getEntry(CommonODataEntity entity, Class<? extends Entry> reference);

  /**
   * Gets an <tt>Entry</tt> from the given OData entity.
   *
   * @param entity OData entity.
   * @param reference reference class.
   * @param setType whether to explicitly output type information.
   * @return <tt>Entry</tt> object.
   */
  Entry getEntry(CommonODataEntity entity, Class<? extends Entry> reference, boolean setType);

  /**
   * Gets a <tt>Link</tt> from the given OData link.
   *
   * @param link OData link.
   * @param isXML whether it is JSON or XML / Atom
   * @return <tt>Link</tt> object.
   */
  Link getLink(ODataLink link, boolean isXML);

  /**
   * Gets a <tt>Property</tt> from the given OData property.
   *
   * @param property OData property.
   * @param reference reference class.
   * @param setType whether to explicitly output type information.
   * @return <tt>Property</tt> object.
   */
  Property getProperty(CommonODataProperty property, Class<? extends Entry> reference, boolean setType);

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
   * Gets <tt>ODataEntitySet</tt> from the given feed resource.
   *
   * @param resource feed resource.
   * @return <tt>ODataEntitySet</tt> object.
   */
  CommonODataEntitySet getODataEntitySet(Feed resource);

  /**
   * Gets <tt>ODataEntitySet</tt> from the given feed resource.
   *
   * @param resource feed resource.
   * @param defaultBaseURI default base URI.
   * @return <tt>ODataEntitySet</tt> object.
   */
  CommonODataEntitySet getODataEntitySet(Feed resource, URI defaultBaseURI);

  /**
   * Gets <tt>ODataEntity</tt> from the given entry resource.
   *
   * @param resource entry resource.
   * @return <tt>ODataEntity</tt> object.
   */
  CommonODataEntity getODataEntity(Entry resource);

  /**
   * Gets <tt>ODataEntity</tt> from the given entry resource.
   *
   * @param resource entry resource.
   * @param defaultBaseURI default base URI.
   * @return <tt>ODataEntity</tt> object.
   */
  CommonODataEntity getODataEntity(Entry resource, URI defaultBaseURI);

  /**
   * Gets an <tt>ODataProperty</tt> from the given property resource.
   *
   * @param property property resource.
   * @return <tt>ODataProperty</tt> object.
   */
  CommonODataProperty getODataProperty(Property property);
}
