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

public class EntityCollection extends AbstractODataObject {

  private Integer count;

  private final List<Entity> entities = new ArrayList<Entity>();

  private URI next;

  private URI deltaLink;

  /**
   * Sets number of entries.
   *
   * @param count number of entries
   */
  public void setCount(final Integer count) {
    this.count = count;
  }

  /**
   * Gets number of entries - if it was required.
   *
   * @return number of entries into the entity set.
   */
  public Integer getCount() {
    return count;
  }

  /**
   * Gets entities.
   *
   * @return entries.
   */
  public List<Entity> getEntities() {
    return entities;
  }

  /**
   * Sets next link.
   *
   * @param next next link.
   */
  public void setNext(final URI next) {
    this.next = next;
  }

  /**
   * Gets next link if exists.
   *
   * @return next link if exists; null otherwise.
   */
  public URI getNext() {
    return next;
  }

  /**
   * Gets delta link if exists.
   *
   * @return delta link if exists; null otherwise.
   */
  public URI getDeltaLink() {
    return deltaLink;
  }

  /**
   * Sets delta link.
   *
   * @param deltaLink delta link.
   */
  public void setDeltaLink(final URI deltaLink) {
    this.deltaLink = deltaLink;
  }
}
