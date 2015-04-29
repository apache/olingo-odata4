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
package org.apache.olingo.client.api.domain;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

import java.net.URI;
import java.util.List;

/**
 * OData entity.
 */
public interface ClientEntity extends ClientLinked, ClientAnnotatable, ClientInvokeResult {

  /**
   * @return the type name of this entity.
   */
  FullQualifiedName getTypeName();

  /**
   * @return self link.
   */
  URI getLink();

  /**
   * Returns OData entity edit link.
   * 
   * @return entity edit link.
   */
  URI getEditLink();

  /**
   * Sets OData entity edit link.
   * 
   * @param editLink edit link.
   */
  void setEditLink(URI editLink);

  /**
   * Gets ETag.
   * 
   * @return ETag.
   */
  String getETag();

  /**
   * Sets ETag.
   * 
   * @param eTag ETag.
   */
  void setETag(String eTag);

  /**
   * Searches for operation with given title.
   * 
   * @param title operation to look for
   * @return operation if found with given title, <tt>null</tt> otherwise
   */
  ClientOperation getOperation(String title);

  /**
   * Gets operations.
   * 
   * @return operations.
   */
  List<ClientOperation> getOperations();

  /**
   * Searches for property with given name.
   * 
   * @param name property to look for
   * @return property if found with given name, <tt>null</tt> otherwise
   */
  ClientProperty getProperty(String name);

  /**
   * Returns OData entity properties.
   *
   * @return OData entity properties.
   */
  List<ClientProperty> getProperties();

  /**
   * To request entity references in place of the actual entities, the client issues a GET request with /$ref appended
   * to the resource path.
   * <br />
   * If the resource path does not identify an entity or a collection of entities, the service returns 404 Not Found.
   * <br />
   * If the resource path terminates on a collection, the response MUST be the format-specific representation of a
   * collection of entity references pointing to the related entities. If no entities are related, the response is the
   * format-specific representation of an empty collection.
   * <br />
   * If the resource path terminates on a single entity, the response MUST be the format-specific representation of an
   * entity reference pointing to the related single entity. If the resource path terminates on a single entity and no
   * such entity exists, the service returns 404 Not Found.
   *
   * @return entity reference.
   */
  URI getId();

  /**
   * Set id for this OData entity.
   *
   * @param id the if for this entity
   */
  void setId(URI id);

  /**
   * Gets media-edit link with given name, if available, otherwise <tt>null</tt>.
   * 
   * @param name candidate link name
   * @return media-edit link with given name, if available, otherwise <tt>null</tt>
   */
  ClientLink getMediaEditLink(final String name);

  /**
   * Returns entity media edit links.
   * 
   * @return OData entity links.
   */
  List<ClientLink> getMediaEditLinks();

  /**
   * TRUE if read-only entity.
   * 
   * @return TRUE if read-only; FALSE otherwise.
   */
  boolean isReadOnly();

  /**
   * Checks if the current entity is a media entity.
   * 
   * @return 'TRUE' if media entity; 'FALSE' otherwise.
   */
  boolean isMediaEntity();

  /**
   * Sets media entity flag.
   * 
   * @param isMediaEntity media entity flag value.
   */
  void setMediaEntity(boolean isMediaEntity);

  /**
   * Gets media content type.
   * 
   * @return media content type.
   */
  String getMediaContentType();

  /**
   * Sets media content type.
   * 
   * @param mediaContentType media content type.
   */
  void setMediaContentType(String mediaContentType);

  /**
   * Gets media content source.
   * 
   * @return media content source.
   */
  URI getMediaContentSource();

  /**
   * Sets media content source.
   * 
   * @param mediaContentSource media content source.
   */
  void setMediaContentSource(URI mediaContentSource);

  /**
   * ETag of the binary stream represented by this media entity or named stream property.
   * 
   * @return media ETag value
   */
  String getMediaETag();

  /**
   * Set media ETag.
   * 
   * @param eTag media ETag value
   */
  void setMediaETag(String eTag);

}
