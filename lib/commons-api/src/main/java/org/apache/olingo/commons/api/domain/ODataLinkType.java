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

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * OData link types.
 */
public enum ODataLinkType {

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
  MEDIA_EDIT("*/*");

  private String type;

  private ODataLinkType(final String type) {
    this.type = type;
  }

  private ODataLinkType(ContentType contentType) {
    this(contentType.toContentTypeString());
  }

  private ODataLinkType setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets
   * <code>LinkType</code> instance from the given rel and type.
   *
   * @param version OData protocol version.
   * @param rel rel.
   * @param type type.
   * @return <code>ODataLinkType</code> object.
   */
  public static ODataLinkType fromString(final ODataServiceVersion version, final String rel, final String type) {
    if (StringUtils.isNotBlank(rel)
            && rel.startsWith(version.getNamespaceMap().get(ODataServiceVersion.MEDIA_EDIT_LINK_REL))) {

      return MEDIA_EDIT.setType(StringUtils.isBlank(type) ? "*/*" : type);
    }

    if (ODataLinkType.ENTITY_NAVIGATION.type.equals(type)) {
      return ENTITY_NAVIGATION;
    }

    if (ODataLinkType.ENTITY_SET_NAVIGATION.type.equals(type)) {
      return ENTITY_SET_NAVIGATION;
    }

    if (ODataLinkType.ASSOCIATION.type.equals(type)) {
      return ASSOCIATION;
    }

    throw new IllegalArgumentException("Invalid link type: " + type);
  }

  @Override
  public String toString() {
    return type;
  }
}
