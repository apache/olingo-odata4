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
import org.apache.olingo.commons.api.data.EntitySet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class EntitySetImpl extends AbstractODataObject implements EntitySet {

  private Integer count;

  private final List<Entity> entities = new ArrayList<Entity>();

  private URI next;

  private URI deltaLink;

  @Override
  public void setCount(final Integer count) {
    this.count = count;
  }

  @Override
  public Integer getCount() {
    return count;
  }

  @Override
  public List<Entity> getEntities() {
    return entities;
  }

  @Override
  public void setNext(final URI next) {
    this.next = next;
  }

  @Override
  public URI getNext() {
    return next;
  }

  @Override
  public URI getDeltaLink() {
    return deltaLink;
  }

  @Override
  public void setDeltaLink(final URI deltaLink) {
    this.deltaLink = deltaLink;
  }

}
