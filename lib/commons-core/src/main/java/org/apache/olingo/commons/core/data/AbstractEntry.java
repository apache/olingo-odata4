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
package org.apache.olingo.commons.core.data;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.domain.ODataOperation;

/**
 * Abstract base for classes implementing an OData entry in Atom and JSON.
 */
public abstract class AbstractEntry extends AbstractODataObject implements Entry {

  private static final long serialVersionUID = 2127764552600969783L;

  private String eTag;

  private String type;

  private Link readLink;

  private Link editLink;

  private final List<Link> associationLinks = new ArrayList<Link>();

  private final List<Link> navigationLinks = new ArrayList<Link>();

  private final List<Link> mediaEditLinks = new ArrayList<Link>();

  private final List<ODataOperation> operations = new ArrayList<ODataOperation>();

  private final List<Property> properties = new ArrayList<Property>();

  private String mediaContentSource;

  private String mediaContentType;

  @Override
  public String getETag() {
    return eTag;
  }

  public void setETag(final String eTag) {
    this.eTag = eTag;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public Link getSelfLink() {
    return readLink;
  }

  @Override
  public void setSelfLink(final Link readLink) {
    this.readLink = readLink;
  }

  @Override
  public Link getEditLink() {
    return editLink;
  }

  @Override
  public void setEditLink(final Link editLink) {
    this.editLink = editLink;
  }

  @Override
  public List<Link> getAssociationLinks() {
    return associationLinks;
  }

  @Override
  public List<Link> getNavigationLinks() {
    return navigationLinks;
  }

  @Override
  public List<Link> getMediaEditLinks() {
    return mediaEditLinks;
  }

  @Override
  public List<ODataOperation> getOperations() {
    return operations;
  }

  @Override
  public List<Property> getProperties() {
    return properties;
  }

  @Override
  public Property getProperty(final String name) {
    Property result = null;

    for (Property property : properties) {
      if (name.equals(property.getName())) {
        result = property;
      }
    }

    return result;
  }

  @Override
  public String getMediaContentType() {
    return this.mediaContentType;
  }

  @Override
  public void setMediaContentType(final String mediaContentType) {
    this.mediaContentType = mediaContentType;
  }

  @Override
  public String getMediaContentSource() {
    return this.mediaContentSource;
  }

  @Override
  public void setMediaContentSource(final String mediaContentSource) {
    this.mediaContentSource = mediaContentSource;
  }

  @Override
  public boolean isMediaEntry() {
    return StringUtils.isNotBlank(this.mediaContentSource);
  }
}
