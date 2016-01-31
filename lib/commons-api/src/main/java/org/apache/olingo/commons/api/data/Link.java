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

import java.util.ArrayList;
import java.util.List;

/**
 * Data representation for a link.
 */
public class Link extends Annotatable {

  private String title;
  private String rel;
  private String href;
  private String type;
  private String mediaETag;
  private Entity entity;
  private EntityCollection entitySet;
  private String bindingLink;
  private List<String> bindingLinks = new ArrayList<String>();

  /**
   * Gets title.
   *
   * @return title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets title.
   *
   * @param title title.
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * Gets rel info.
   *
   * @return rel info.
   */
  public String getRel() {
    return rel;
  }

  /**
   * Sets rel info.
   *
   * @param rel rel info.
   */
  public void setRel(final String rel) {
    this.rel = rel;
  }

  /**
   * Gets href.
   *
   * @return href.
   */
  public String getHref() {
    return href;
  }

  /**
   * Sets href.
   *
   * @param href href.
   */
  public void setHref(final String href) {
    this.href = href;
  }

  /**
   * Gets type.
   *
   * @return type.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type type.
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Gets Media ETag.
   *
   * @return media ETag
   */
  public String getMediaETag() {
    return mediaETag;
  }

  /**
   * Sets Media ETag.
   *
   * @param mediaETag media ETag
   */
  public void setMediaETag(final String mediaETag) {
    this.mediaETag = mediaETag;
  }

  /**
   * Gets in-line entity.
   *
   * @return in-line entity.
   */
  public Entity getInlineEntity() {
    return entity;
  }

  /**
   * Sets in-line entity.
   *
   * @param entity entity.
   */
  public void setInlineEntity(final Entity entity) {
    this.entity = entity;
  }

  /**
   * Gets in-line entity set.
   *
   * @return in-line entity set.
   */
  public EntityCollection getInlineEntitySet() {
    return entitySet;
  }

  /**
   * Sets in-line entity set.
   *
   * @param entitySet entity set.
   */
  public void setInlineEntitySet(final EntityCollection entitySet) {
    this.entitySet = entitySet;
  }

  /**
   * If this is a "toOne" relationship this method delivers the binding link or <tt>null</tt> if not set.
   * @return String the binding link.
   */
  public String getBindingLink() {
    return bindingLink;
  }

  /**
   * If this is a "toMany" relationship this method delivers the binding links or <tt>emptyList</tt> if not set.
   * @return a list of binding links.
   */
  public List<String> getBindingLinks() {
    return bindingLinks;
  }

  /**
   * Sets the binding link.
   * @param bindingLink name of binding link
   */
  public void setBindingLink(final String bindingLink) {
    this.bindingLink = bindingLink;
  }

  /**
   * Sets the binding links. List MUST NOT be <tt>null</tt>.
   * @param bindingLinks list of binding link names
   */
  public void setBindingLinks(final List<String> bindingLinks) {
    this.bindingLinks = bindingLinks;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Link other = (Link) o;
    return getAnnotations().equals(other.getAnnotations())
        && (title == null ? other.title == null : title.equals(other.title))
        && (rel == null ? other.rel == null : rel.equals(other.rel))
        && (href == null ? other.href == null : href.equals(other.href))
        && (type == null ? other.type == null : type.equals(other.type))
        && (mediaETag == null ? other.mediaETag == null : mediaETag.equals(other.mediaETag))
        && (entity == null ? other.entity == null : entity.equals(other.entity))
        && (entitySet == null ? other.entitySet == null : entitySet.equals(other.entitySet))
        && (bindingLink == null ? other.bindingLink == null : bindingLink.equals(other.bindingLink))
        && bindingLinks.equals(other.bindingLinks);
  }

  @Override
  public int hashCode() {
    int result = getAnnotations().hashCode();
    result = 31 * result + (title == null ? 0 : title.hashCode());
    result = 31 * result + (rel == null ? 0 : rel.hashCode());
    result = 31 * result + (href == null ? 0 : href.hashCode());
    result = 31 * result + (type == null ? 0 : type.hashCode());
    result = 31 * result + (mediaETag == null ? 0 : mediaETag.hashCode());
    result = 31 * result + (entity == null ? 0 : entity.hashCode());
    result = 31 * result + (entitySet == null ? 0 : entitySet.hashCode());
    result = 31 * result + (bindingLink == null ? 0 : bindingLink.hashCode());
    result = 31 * result + bindingLinks.hashCode();
    return result;
  }
}
