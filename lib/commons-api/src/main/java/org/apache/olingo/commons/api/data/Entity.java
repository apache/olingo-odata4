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
package org.apache.olingo.commons.api.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Data representation for a single entity.
 */
public class Entity extends Linked {

  private String eTag;
  private String type;

  private Link readLink;
  private Link editLink;

  private final List<Link> mediaEditLinks = new ArrayList<Link>();
  private final List<Operation> operations = new ArrayList<Operation>();

  private final List<Property> properties = new ArrayList<Property>();

  private URI mediaContentSource;
  private String mediaContentType;
  private String mediaETag;

  /**
   * Gets ETag.
   *
   * @return ETag.
   */
  public String getETag() {
    return eTag;
  }
  
  /**
   * Sets ETag
   * @param eTag ETag
   */
  public void setETag(final String eTag) {
    this.eTag = eTag;
  }

  /**
   * Gets entity type.
   *
   * @return entity type.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets entity type.
   *
   * @param type entity type.
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Gets entity self link.
   *
   * @return self link.
   */
  public Link getSelfLink() {
    return readLink;
  }

  /**
   * Sets entity self link.
   *
   * @param selfLink self link.
   */
  public void setSelfLink(final Link selfLink) {
    readLink = selfLink;
  }

  /**
   * Gets entity edit link.
   *
   * @return edit link.
   */
  public Link getEditLink() {
    return editLink;
  }

  /**
   * Sets entity edit link.
   *
   * @param editLink edit link.
   */
  public void setEditLink(final Link editLink) {
    this.editLink = editLink;
  }

  /**
   * Gets media entity links.
   *
   * @return links.
   */
  public List<Link> getMediaEditLinks() {
    return mediaEditLinks;
  }

  /**
   * Gets operations.
   *
   * @return operations.
   */
  public List<Operation> getOperations() {
    return operations;
  }

  /**
   * Add property to this Entity.
   *
   * @param property property which is added
   * @return this Entity for fluid/flow adding
   */
  public Entity addProperty(final Property property) {
    properties.add(property);
    return this;
  }

  /**
   * Gets properties.
   *
   * @return properties.
   */
  public List<Property> getProperties() {
    return properties;
  }

  /**
   * Gets property with given name.
   *
   * @param name property name
   * @return property with given name if found, null otherwise
   */
  public Property getProperty(final String name) {
    Property result = null;

    for (Property property : properties) {
      if (name.equals(property.getName())) {
        result = property;
        break;
      }
    }

    return result;
  }

  /**
   * Gets media content type.
   *
   * @return media content type.
   */
  public String getMediaContentType() {
    return mediaContentType;
  }

  /**
   * Set media content type.
   *
   * @param mediaContentType media content type.
   */
  public void setMediaContentType(final String mediaContentType) {
    this.mediaContentType = mediaContentType;
  }

  /**
   * Gets media content resource.
   *
   * @return media content resource.
   */
  public URI getMediaContentSource() {
    return mediaContentSource;
  }

  /**
   * Set media content source.
   *
   * @param mediaContentSource media content source.
   */
  public void setMediaContentSource(final URI mediaContentSource) {
    this.mediaContentSource = mediaContentSource;
  }

  /**
   * ETag of the binary stream represented by this media entity or named stream property.
   *
   * @return media ETag value
   */
  public String getMediaETag() {
    return mediaETag;
  }

  /**
   * Set media ETag.
   *
   * @param eTag media ETag value
   */
  public void setMediaETag(final String eTag) {
    mediaETag = eTag;
  }

  /**
   * Checks if the current entity is a media entity.
   *
   * @return 'TRUE' if is a media entity; 'FALSE' otherwise.
   */
  public boolean isMediaEntity() {
    return mediaContentSource != null;
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o)
        && (eTag == null ? ((Entity) o).eTag == null : eTag.equals(((Entity) o).eTag))
        && (type == null ? ((Entity) o).type == null : type.equals(((Entity) o).type))
        && (readLink == null ? ((Entity) o).readLink == null : readLink.equals(((Entity) o).readLink))
        && (editLink == null ? ((Entity) o).editLink == null : editLink.equals(((Entity) o).editLink))
        && mediaEditLinks.equals(((Entity) o).mediaEditLinks)
        && operations.equals(((Entity) o).operations)
        && properties.equals(((Entity) o).properties)
        && (mediaContentSource == null ?
            ((Entity) o).mediaContentSource == null :
            mediaContentSource.equals(((Entity) o).mediaContentSource))
        && (mediaContentType == null ?
            ((Entity) o).mediaContentType == null :
            mediaContentType.equals(((Entity) o).mediaContentType))
        && (mediaETag == null ? ((Entity) o).mediaETag == null : mediaETag.equals(((Entity) o).mediaETag));
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (eTag == null ? 0 : eTag.hashCode());
    result = 31 * result + (type == null ? 0 : type.hashCode());
    result = 31 * result + (readLink == null ? 0 : readLink.hashCode());
    result = 31 * result + (editLink == null ? 0 : editLink.hashCode());
    result = 31 * result + mediaEditLinks.hashCode();
    result = 31 * result + operations.hashCode();
    result = 31 * result + properties.hashCode();
    result = 31 * result + (mediaContentSource == null ? 0 : mediaContentSource.hashCode());
    result = 31 * result + (mediaContentType == null ? 0 : mediaContentType.hashCode());
    result = 31 * result + (mediaETag == null ? 0 : mediaETag.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return properties.toString();
  }
}
