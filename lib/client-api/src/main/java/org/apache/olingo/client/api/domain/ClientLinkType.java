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

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * OData link types.
 */
public enum ClientLinkType {

  /**
   * Entity navigation link.
   */
  ENTITY_NAVIGATION(ContentType.APPLICATION_ATOM_XML_ENTRY),
  /**
   * Entity set navigation link.
   */
  ENTITY_SET_NAVIGATION(ContentType.APPLICATION_ATOM_XML_FEED),
  /**
   * Association link.
   */
  ASSOCIATION(ContentType.APPLICATION_XML),
  /**
   * Media-edit link.
   */
  MEDIA_EDIT("*/*"),
  
  /**
   * Media-read link
   */
  MEDIA_READ("*/*"),

  /**
   * Entity binding link.
   */
  ENTITY_BINDING(ContentType.APPLICATION_XML),

  /**
   * Entity collection binding link.
   */
  ENTITY_COLLECTION_BINDING(ContentType.APPLICATION_XML);

  private String type;

  private ClientLinkType(final String type) {
    this.type = type;
  }

  private ClientLinkType(final ContentType contentType) {
    this(contentType.toContentTypeString());
  }

  private ClientLinkType setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets
   * <code>LinkType</code> instance from the given rel and type.
   * 
   * @param rel rel.
   * @param type type.
   * @return <code>ODataLinkType</code> object.
   */
  public static ClientLinkType fromString(final String rel, final String type) {
    if (rel != null && rel.startsWith(Constants.NS_MEDIA_EDIT_LINK_REL)) {
      return MEDIA_EDIT.setType(type == null || type.isEmpty() ? "*/*" : type);
    }
    
    if (rel != null && rel.startsWith(Constants.NS_MEDIA_READ_LINK_REL)) {
      return MEDIA_READ.setType(type == null || type.isEmpty() ? "*/*" : type);
    }

    if (ClientLinkType.ENTITY_NAVIGATION.type.equals(type)) {
      return ENTITY_NAVIGATION;
    }

    if (ClientLinkType.ENTITY_SET_NAVIGATION.type.equals(type)) {
      return ENTITY_SET_NAVIGATION;
    }

    if (ClientLinkType.ASSOCIATION.type.equals(type)) {
      return ASSOCIATION;
    }

    throw new IllegalArgumentException("Invalid link type: " + type);
  }

  @Override
  public String toString() {
    return type;
  }
}
