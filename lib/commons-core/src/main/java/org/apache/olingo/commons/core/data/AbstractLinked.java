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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;

public class AbstractLinked extends AbstractODataObject implements Linked{

  private final List<Link> associationLinks = new ArrayList<Link>();
  private final List<Link> navigationLinks = new ArrayList<Link>();
  private final List<Link> bindingLinks = new ArrayList<Link>();
  
  private final HashMap<String, Entity> inlineEntities = new HashMap<String, Entity>();
  private final HashMap<String, EntitySet> inlineEntitySets = new HashMap<String, EntitySet>();
  
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
  public Link getNavigationBinding(String name) {
    return getOneByTitle(name, bindingLinks);
  }

  @Override
  public List<Link> getNavigationBindings() {
    return bindingLinks;
  }
  
  @Override
  public Entity getInlineEntity(String name) {
    return inlineEntities.get(name);
  }

  @Override
  public Map<String, Entity> getAllInlineEntities() {
    return inlineEntities;
  }

  @Override
  public void addInlineEntity(String name, Entity entity) {
    inlineEntities.put(name, entity);
  }

  @Override
  public EntitySet getInlineEntitySet(String name) {
    return inlineEntitySets.get(name);
  }

  @Override
  public Map<String, EntitySet> getAllInlineEntitySets() {
    return inlineEntitySets;
  }

  @Override
  public void addInlineEntitySet(String name, EntitySet entitySet) {
    inlineEntitySets.put(name, entitySet);
  }
}
