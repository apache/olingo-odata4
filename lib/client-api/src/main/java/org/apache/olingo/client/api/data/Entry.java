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
package org.apache.olingo.client.api.data;

import java.net.URI;
import java.util.List;
import org.w3c.dom.Element;

public interface Entry {

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
   * Gest entry ID.
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
  List<Operation> getOperations();

  /**
   * Gets content.
   *
   * @return content.
   */
  Element getContent();

  /**
   * Sets content.
   *
   * @param content content.
   */
  void setContent(Element content);

  /**
   * Gets media entry properties.
   *
   * @return media entry properties.
   */
  Element getMediaEntryProperties();

  /**
   * Sets media entry properties.
   *
   * @param content media entry properties.
   */
  void setMediaEntryProperties(Element content);

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
   * Checks if the current entry is a media entry.
   *
   * @return 'TRUE' if is a media entry; 'FALSE' otherwise.
   */
  boolean isMediaEntry();
}
