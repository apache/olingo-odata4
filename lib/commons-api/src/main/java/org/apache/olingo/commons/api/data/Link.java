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

public interface Link extends Annotatable {

  /**
   * Gets rel info.
   * 
   * @return rel info.
   */
  String getRel();

  /**
   * Sets rel info.
   * 
   * @param rel rel info.
   */
  void setRel(String rel);

  /**
   * Gets type.
   * 
   * @return type.
   */
  String getType();

  /**
   * Sets type.
   * 
   * @param type type.
   */
  void setType(String type);

  /**
   * Gets title.
   * 
   * @return title.
   */
  String getTitle();

  /**
   * Sets title.
   * 
   * @param title title.
   */
  void setTitle(String title);

  /**
   * Gets href.
   * 
   * @return href.
   */
  String getHref();

  /**
   * Sets href.
   * 
   * @param href href.
   */
  void setHref(String href);

  /**
   * Gets Media ETag.
   * 
   * @return media ETag
   */
  String getMediaETag();

  /**
   * Sets Media ETag.
   * 
   * @param etag media ETag
   */
  void setMediaETag(String etag);

  /**
   * Gets in-line entity.
   * 
   * @return in-line entity.
   */
  Entity getInlineEntity();

  /**
   * Sets in-line entity.
   * 
   * @param entity entity.
   */
  void setInlineEntity(Entity entity);

  /**
   * Gets in-line entity set.
   * 
   * @return in-line entity set.
   */
  EntitySet getInlineEntitySet();

  /**
   * Sets in-line entity set.
   * 
   * @param entitySet entity set.
   */
  void setInlineEntitySet(EntitySet entitySet);

}
