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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.Constants;

/**
 * OData link.
 */
public class ClientLink extends ClientItem implements ClientAnnotatable {

  public static class Builder {

    private URI uri;

    private ClientLinkType type;

    private String title;
    
    private String mediaETag;
    
    public Builder setURI(final URI uri) {
      this.uri = uri;
      return this;
    }

    public Builder setURI(final URI baseURI, final String href) {
      uri = getURI(baseURI, href);
      return this;
    }

    public Builder setType(final ClientLinkType type) {
      this.type = type;
      return this;
    }

    public Builder setTitle(final String title) {
      this.title = title;
      return this;
    }

    public Builder setEtag(final String eTag) {
      this.mediaETag= eTag;
      return this;
    }

    public ClientLink build() {
      return new ClientLink(uri, type, title, mediaETag);
    }
  }

  /**
   * Build URI starting from the given base and href.
   * <br/>
   * If href is absolute or base is null then base will be ignored.
   * 
   * @param base URI prefix.
   * @param href URI suffix.
   * @return built URI.
   */
  private static URI getURI(final URI base, final String href) {
    if (href == null) {
      throw new IllegalArgumentException("Null link provided");
    }

    URI uri = URI.create(href);

    if (!uri.isAbsolute() && base != null) {
      uri = URI.create(base.toASCIIString() + "/" + href);
    }

    return uri.normalize();
  }

  /**
   * Link type.
   */
  private final ClientLinkType type;

  /**
   * Link rel.
   */
  private final String rel;

  /**
   * ETag for media edit links.
   */
  private String mediaETag;

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  /**
   * Constructor.
   * 
   * @param uri URI.
   * @param type type.
   * @param title title.
   */
  public ClientLink(final URI uri, final ClientLinkType type, final String title) {
    this(uri, type, title, null);
  }
  
  public ClientLink(final URI uri, final ClientLinkType type, final String title, final String eTag) {
    super(title);

    this.type = type;
    setLink(uri);
    this.mediaETag = eTag;

    switch (this.type) {
    case ASSOCIATION:
      rel = Constants.NS_ASSOCIATION_LINK_REL + title;
      break;

    case ENTITY_NAVIGATION:
    case ENTITY_SET_NAVIGATION:
      rel = Constants.NS_NAVIGATION_LINK_REL + title;
      break;

    case MEDIA_EDIT:
      rel = Constants.NS_MEDIA_EDIT_LINK_REL + title;
      break;

    case MEDIA_READ:
      rel = Constants.NS_MEDIA_READ_LINK_REL + title;
      break;
      
    default:
      rel = Constants.NS_MEDIA_EDIT_LINK_REL + title;
      break;
    }
  }
  
  /**
   * Constructor.
   * 
   * @param version OData service version.
   * @param baseURI base URI.
   * @param href href.
   * @param type type.
   * @param title title.
   */
  protected ClientLink(final URI baseURI, final String href, final ClientLinkType type, final String title) {

    this(getURI(baseURI, href), type, title);
  }

  /**
   * Gets link type.
   * 
   * @return link type;
   */
  public ClientLinkType getType() {
    return type;
  }

  public ClientInlineEntity asInlineEntity() {
    return (this instanceof ClientInlineEntity) ? (ClientInlineEntity) this : null;
  }

  public ClientInlineEntitySet asInlineEntitySet() {
    return (this instanceof ClientInlineEntitySet) ? (ClientInlineEntitySet) this : null;
  }

  /**
   * Gets link rel.
   * 
   * @return link rel
   */
  public String getRel() {
    return rel;
  }

  /**
   * Gets Media ETag.
   * 
   * @return media ETag
   */
  public String getMediaETag() {
    return mediaETag;
  }

  public List<ClientAnnotation> getAnnotations() {
    return annotations;
  }
}
