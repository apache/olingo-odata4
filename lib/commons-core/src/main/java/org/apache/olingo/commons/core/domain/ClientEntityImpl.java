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
package org.apache.olingo.commons.core.domain;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.domain.AbstractClientPayload;
import org.apache.olingo.commons.api.domain.ClientAnnotation;
import org.apache.olingo.commons.api.domain.ClientEntity;
import org.apache.olingo.commons.api.domain.ClientLink;
import org.apache.olingo.commons.api.domain.ClientOperation;
import org.apache.olingo.commons.api.domain.ClientProperty;
import org.apache.olingo.commons.api.domain.ClientSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class ClientEntityImpl extends AbstractClientPayload implements ClientEntity, ClientSingleton {

  /**
   * Entity id.
   */
  private URI id;
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
  private URI mediaContentSource;
  /**
   * Media ETag.
   */
  private String mediaETag;
  /**
   * Edit link.
   */
  private URI editLink;

  private final List<ClientProperty> properties = new ArrayList<ClientProperty>();

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  private final FullQualifiedName typeName;
  /**
   * Navigation links (might contain in-line entities or entity sets).
   */
  private final List<ClientLink> navigationLinks = new ArrayList<ClientLink>();
  /**
   * Association links.
   */
  private final List<ClientLink> associationLinks = new ArrayList<ClientLink>();
  /**
   * Media edit links.
   */
  private final List<ClientLink> mediaEditLinks = new ArrayList<ClientLink>();
  /**
   * Operations (legacy, functions, actions).
   */
  private final List<ClientOperation> operations = new ArrayList<ClientOperation>();

  public ClientEntityImpl(final FullQualifiedName typeName) {
    super(typeName == null ? null : typeName.toString());
    this.typeName = typeName;
  }

  @Override
  public FullQualifiedName getTypeName() {
    return typeName;
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
  public ClientOperation getOperation(final String title) {
    ClientOperation result = null;
    for (ClientOperation operation : operations) {
      if (title.equals(operation.getTitle())) {
        result = operation;
        break;
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
  public List<ClientOperation> getOperations() {
    return operations;
  }


  @Override
  public ClientProperty getProperty(final String name) {
    ClientProperty result = null;

    if (StringUtils.isNotBlank(name)) {
      for (ClientProperty property : getProperties()) {
        if (name.equals(property.getName())) {
          result = property;
          break;
        }
      }
    }

    return result;
  }

  @Override
  public boolean addLink(final ClientLink link) {
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
        result = mediaEditLinks.contains(link) ? false : mediaEditLinks.add(link);
        break;

      default:
    }

    return result;
  }

  @Override
  public boolean removeLink(final ClientLink link) {
    return associationLinks.remove(link) || navigationLinks.remove(link);
  }

  private ClientLink getLink(final List<ClientLink> links, final String name) {
    ClientLink result = null;
    for (ClientLink link : links) {
      if (name.equals(link.getName())) {
        result = link;
        break;
      }
    }

    return result;
  }

  @Override
  public ClientLink getNavigationLink(final String name) {
    return getLink(navigationLinks, name);
  }

  @Override
  public List<ClientLink> getNavigationLinks() {
    return navigationLinks;
  }

  @Override
  public ClientLink getAssociationLink(final String name) {
    return getLink(associationLinks, name);
  }

  @Override
  public List<ClientLink> getAssociationLinks() {
    return associationLinks;
  }

  @Override
  public ClientLink getMediaEditLink(final String name) {
    return getLink(mediaEditLinks, name);
  }

  @Override
  public List<ClientLink> getMediaEditLinks() {
    return mediaEditLinks;
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
    mediaEntity = isMediaEntity;
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
  public URI getMediaContentSource() {
    return mediaContentSource;
  }

  @Override
  public void setMediaContentSource(final URI mediaContentSource) {
    this.mediaContentSource = mediaContentSource;
  }

  @Override
  public String getMediaETag() {
    return mediaETag;
  }

  @Override
  public void setMediaETag(final String eTag) {
    mediaETag = eTag;
  }

  @Override
  public URI getId() {
    return id;
  }

  @Override
  public void setId(final URI id) {
    this.id = id;
  }

  @Override
  public List<ClientProperty> getProperties() {
    return properties;
  }

  @Override
  public List<ClientAnnotation> getAnnotations() {
    return annotations;
  }
}
