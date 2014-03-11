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
package org.apache.olingo.client.core.data;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.data.Entry;
import org.apache.olingo.client.api.data.Link;
import org.apache.olingo.client.api.domain.ODataOperation;
import org.w3c.dom.Element;

/**
 * Abstract base for classes implementing an OData entry in Atom and JSON.
 */
public abstract class AbstractEntry extends AbstractPayloadObject implements Entry {

  private static final long serialVersionUID = 2127764552600969783L;

  private String eTag;

  private String type;

  private String id;

  private Link readLink;

  private Link editLink;

  private final List<Link> associationLinks = new ArrayList<Link>();

  private final List<Link> navigationLinks = new ArrayList<Link>();

  private final List<Link> mediaEditLinks = new ArrayList<Link>();

  private final List<ODataOperation> operations = new ArrayList<ODataOperation>();

  private Element content;

  private Element mediaEntryProperties;

  private String mediaContentSource;

  private String mediaContentType;

  @Override
  public String getETag() {
    return eTag;
  }

  @Override
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
  public String getId() {
    return id;
  }

  @Override
  public void setId(final String id) {
    this.id = id;
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
  public Element getContent() {
    return content;
  }

  @Override
  public void setContent(final Element content) {
    this.content = content;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public Element getMediaEntryProperties() {
    return mediaEntryProperties;
  }

  @Override
  public void setMediaEntryProperties(final Element mediaEntryProperties) {
    this.mediaEntryProperties = mediaEntryProperties;
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
    return getMediaEntryProperties() != null || StringUtils.isNotBlank(this.mediaContentSource);
  }
}
