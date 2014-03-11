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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

/**
 * OData link types.
 */
public enum LinkType {

  /**
   * Entity navigation link.
   */
  ENTITY_NAVIGATION(ODataPubFormat.ATOM + ";type=entry"),
  /**
   * Entity set navigation link.
   */
  ENTITY_SET_NAVIGATION(ODataPubFormat.ATOM + ";type=feed"),
  /**
   * Association link.
   */
  ASSOCIATION(ContentType.APPLICATION_XML.getMimeType()),
  /**
   * Media-edit link.
   */
  MEDIA_EDIT("*/*");

  private String type;

  private LinkType(final String type) {
    this.type = type;
  }

  private LinkType setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets <code>LinkType</code> instance from the given rel and type.
   *
   * @param client OData client.
   * @param rel rel.
   * @param type type.
   * @return <code>ODataLinkType</code> object.
   */
  public static LinkType fromString(final ODataClient client, final String rel, final String type) {
    if (StringUtils.isNotBlank(rel) && rel.startsWith(client.getServiceVersion().getNamespaceMap().
            get(ODataServiceVersion.MEDIA_EDIT_LINK_REL))) {

      return MEDIA_EDIT.setType(StringUtils.isBlank(type) ? "*/*" : type);
    }

    if (LinkType.ENTITY_NAVIGATION.type.equals(type)) {
      return ENTITY_NAVIGATION;
    }

    if (LinkType.ENTITY_SET_NAVIGATION.type.equals(type)) {
      return ENTITY_SET_NAVIGATION;
    }

    if (LinkType.ASSOCIATION.type.equals(type)) {
      return ASSOCIATION;
    }

    throw new IllegalArgumentException("Invalid link type: " + type);
  }

  @Override
  public String toString() {
    return type;
  }
}
