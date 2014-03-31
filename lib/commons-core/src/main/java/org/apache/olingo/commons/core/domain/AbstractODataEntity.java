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
package org.apache.olingo.commons.core.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.domain.AbstractODataPayload;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.domain.CommonODataProperty;

/**
 * OData entity.
 */
public abstract class AbstractODataEntity extends AbstractODataPayload implements CommonODataEntity {

  private static final long serialVersionUID = 8360640095932811034L;

  /**
   * ETag.
   */
  private String eTag;

  /**
   * Media entity flag.
   */
  private boolean mediaEntity = false;

  /**
   * In case of media entity, media content type.
   */
  private String mediaContentType;

  /**
   * In case of media entity, media content source.
   */
  private String mediaContentSource;

  /**
   * Media ETag.
   */
  private String mediaETag;

  /**
   * Edit link.
   */
  private URI editLink;

  /**
   * Navigation links (might contain in-line entities or feeds).
   */
  private final List<ODataLink> navigationLinks = new ArrayList<ODataLink>();

  /**
   * Association links.
   */
  private final List<ODataLink> associationLinks = new ArrayList<ODataLink>();

  /**
   * Media edit links.
   */
  private final List<ODataLink> editMediaLinks = new ArrayList<ODataLink>();

  /**
   * Operations (legacy, functions, actions).
   */
  private final List<ODataOperation> operations = new ArrayList<ODataOperation>();

  public AbstractODataEntity(final String name) {
    super(name);
  }

  @Override
  public String getETag() {
    return eTag;
  }

  @Override
  public void setETag(final String eTag) {
    this.eTag = eTag;
  }

  @Override
  public ODataOperation getOperation(final String title) {
    ODataOperation result = null;
    for (ODataOperation operation : operations) {
      if (title.equals(operation.getTitle())) {
        result = operation;
      }
    }

    return result;
  }

  /**
   * Gets operations.
   *
   * @return operations.
   */
  @Override
  public List<ODataOperation> getOperations() {
    return this.operations;
  }

  @Override
  public CommonODataProperty getProperty(final String name) {
    CommonODataProperty result = null;

    if (StringUtils.isNotBlank(name)) {
      for (CommonODataProperty property : getProperties()) {
        if (name.equals(property.getName())) {
          result = property;
        }
      }
    }

    return result;
  }

  @Override
  public boolean addLink(final ODataLink link) {
    boolean result = false;

    switch (link.getType()) {
      case ASSOCIATION:
        result = associationLinks.contains(link) ? false : associationLinks.add(link);
        break;

      case ENTITY_NAVIGATION:
      case ENTITY_SET_NAVIGATION:
        result = navigationLinks.contains(link) ? false : navigationLinks.add(link);
        break;

      case MEDIA_EDIT:
        result = editMediaLinks.contains(link) ? false : editMediaLinks.add(link);
        break;

      default:
    }

    return result;
  }

  @Override
  public boolean removeLink(final ODataLink link) {
    return associationLinks.remove(link) || navigationLinks.remove(link) || editMediaLinks.remove(link);
  }

  private ODataLink getLink(final List<ODataLink> links, final String name) {
    ODataLink result = null;
    for (ODataLink link : links) {
      if (name.equals(link.getName())) {
        result = link;
      }
    }

    return result;
  }

  @Override
  public ODataLink getNavigationLink(final String name) {
    return getLink(navigationLinks, name);
  }

  @Override
  public List<ODataLink> getNavigationLinks() {
    return navigationLinks;
  }

  @Override
  public ODataLink getAssociationLink(final String name) {
    return getLink(associationLinks, name);
  }

  @Override
  public List<ODataLink> getAssociationLinks() {
    return associationLinks;
  }

  @Override
  public ODataLink getEditMediaLink(final String name) {
    return getLink(editMediaLinks, name);
  }

  @Override
  public List<ODataLink> getEditMediaLinks() {
    return editMediaLinks;
  }

  @Override
  public URI getEditLink() {
    return editLink;
  }

  @Override
  public void setEditLink(final URI editLink) {
    this.editLink = editLink;
  }

  @Override
  public URI getLink() {
    return super.getLink() == null ? getEditLink() : super.getLink();
  }

  @Override
  public boolean isReadOnly() {
    return super.getLink() != null;
  }

  @Override
  public boolean isMediaEntity() {
    return mediaEntity;
  }

  @Override
  public void setMediaEntity(final boolean isMediaEntity) {
    this.mediaEntity = isMediaEntity;
  }

  @Override
  public String getMediaContentType() {
    return mediaContentType;
  }

  @Override
  public void setMediaContentType(final String mediaContentType) {
    this.mediaContentType = mediaContentType;
  }

  @Override
  public String getMediaContentSource() {
    return mediaContentSource;
  }

  @Override
  public void setMediaContentSource(final String mediaContentSource) {
    this.mediaContentSource = mediaContentSource;
  }

  @Override
  public String getMediaETag() {
    return mediaETag;
  }

  @Override
  public void setMediaETag(final String eTag) {
    this.mediaETag = eTag;
  }
}
