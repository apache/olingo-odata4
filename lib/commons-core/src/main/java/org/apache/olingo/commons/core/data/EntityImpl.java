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
package org.apache.olingo.commons.core.data;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.domain.ODataOperation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing an OData entity.
 */
public class EntityImpl extends AbstractODataObject implements Entity {

  private String eTag;

  private String type;

  private Link readLink;
  private Link editLink;

  private final List<Link> associationLinks = new ArrayList<Link>();
  private final List<Link> navigationLinks = new ArrayList<Link>();
  private final List<Link> mediaEditLinks = new ArrayList<Link>();
  private final List<Link> bindingLinks = new ArrayList<Link>();

  private final List<ODataOperation> operations = new ArrayList<ODataOperation>();

  private final List<Property> properties = new ArrayList<Property>();

  private URI mediaContentSource;
  private String mediaContentType;
  private String mediaETag;

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

  private Link getOneByTitle(final String name, final List<Link> links) {
    Link result = null;

    for (Link link : links) {
      if (name.equals(link.getTitle())) {
        result = link;
      }
    }

    return result;
  }

  @Override
  public Link getAssociationLink(final String name) {
    return getOneByTitle(name, associationLinks);
  }

  @Override
  public List<Link> getAssociationLinks() {
    return associationLinks;
  }

  @Override
  public Link getNavigationLink(final String name) {
    return getOneByTitle(name, navigationLinks);
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
  public Link getNavigationBinding(String name) {
    return getOneByTitle(name, bindingLinks);
  }

  @Override
  public List<Link> getNavigationBindings() {
    return bindingLinks;
  }

  @Override
  public List<ODataOperation> getOperations() {
    return operations;
  }

  @Override
  public Entity addProperty(final Property property) {
    properties.add(property);
    return this;
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
  public boolean isMediaEntity() {
    return mediaContentSource != null;
  }
}
