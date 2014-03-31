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
package org.apache.olingo.commons.api.data;

import org.apache.olingo.commons.api.domain.ODataOperation;
import java.net.URI;
import java.util.List;

public interface Entry {

  /**
   * Gets ETag.
   *
   * @return ETag.
   */
  String getETag();

  /**
   * Gets base URI.
   *
   * @return base URI.
   */
  URI getBaseURI();

  /**
   * Gets entry type.
   *
   * @return entry type.
   */
  String getType();

  /**
   * Sets entry type.
   *
   * @param type entry type.
   */
  void setType(String type);

  /**
   * Gets entry ID.
   *
   * @return entry ID.
   */
  String getId();

  /**
   * Sets entry ID.
   *
   * @param id entry ID.
   */
  void setId(String id);

  /**
   * Gets entry self link.
   *
   * @return self link.
   */
  Link getSelfLink();

  /**
   * Sets entry self link.
   *
   * @param selfLink self link.
   */
  void setSelfLink(Link selfLink);

  /**
   * Gets entry edit link.
   *
   * @return edit link.
   */
  Link getEditLink();

  /**
   * Sets entry edit link.
   *
   * @param editLink edit link.
   */
  void setEditLink(Link editLink);

  /**
   * Gets association links.
   *
   * @return association links.
   */
  List<Link> getAssociationLinks();

  /**
   * Gets navigation links.
   *
   * @return links.
   */
  List<Link> getNavigationLinks();

  /**
   * Gets media entity links.
   *
   * @return links.
   */
  List<Link> getMediaEditLinks();

  /**
   * Gets operations.
   *
   * @return operations.
   */
  List<ODataOperation> getOperations();

  /**
   * Gets properties.
   *
   * @return properties.
   */
  List<Property> getProperties();

  /**
   * Gets property with given name.
   *
   * @param name property name
   * @return property with given name if found, null otherwise
   */
  Property getProperty(String name);

  /**
   * Gets media content type.
   *
   * @return media content type.
   */
  String getMediaContentType();

  /**
   * Gets media content resource.
   *
   * @return media content resource.
   */
  String getMediaContentSource();

  /**
   * Set media content source.
   *
   * @param mediaContentSource media content source.
   */
  void setMediaContentSource(String mediaContentSource);

  /**
   * Set media content type.
   *
   * @param mediaContentType media content type.
   */
  void setMediaContentType(String mediaContentType);

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

  /**
   * Checks if the current entry is a media entry.
   *
   * @return 'TRUE' if is a media entry; 'FALSE' otherwise.
   */
  boolean isMediaEntry();
}
