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
package org.apache.olingo.fit.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotFoundException;

public class MetadataLinkInfo {

  private Map<String, EntitySet> entitySets = new HashMap<String, EntitySet>();

  public void setSingleton(final String entitySetName) {
    entitySets.get(entitySetName).setSingleton(true);
  }

  public boolean isSingleton(final String entitySetName) {
    return entitySets.get(entitySetName).isSingleton();
  }

  public Set<String> getEntitySets() {
    return entitySets.keySet();
  }

  public void addEntitySet(final String entitySetName) {
    if (!entitySets.containsKey(entitySetName)) {
      entitySets.put(entitySetName, new EntitySet(entitySetName));
    }
  }

  public void addLink(
          final String entitySetName, final String linkName, final String targetName, final boolean isFeed) {
    final EntitySet entitySet;
    if (entitySets.containsKey(entitySetName)) {
      entitySet = entitySets.get(entitySetName);
    } else {
      entitySet = new EntitySet(entitySetName);
      entitySets.put(entitySetName, entitySet);
    }

    entitySet.add(linkName, targetName, isFeed);
  }

  public Set<String> getNavigationLinkNames(final String entitySetName) {
    final Set<String> res = new HashSet<String>();

    if (!entitySets.containsKey(entitySetName)) {
      throw new NotFoundException();
    }

    for (NavigationLink navigationLink : entitySets.get(entitySetName).getLinks()) {
      res.add(navigationLink.getName());
    }

    return res;
  }

  public boolean exists(final String entitySetName, final String linkName) {
    try {
      return getNavigationLinkNames(entitySetName).contains(linkName);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFeed(final String entitySetName, final String linkName) {
    return entitySets.containsKey(entitySetName) && entitySets.get(entitySetName).isFeed(linkName);
  }

  public String getTargetName(final String entitySetName, final String linkName) {
    if (!entitySets.containsKey(entitySetName)) {
      throw new NotFoundException();
    }

    final String targetName = entitySets.get(entitySetName).getLink(linkName).getTargetName();
    return targetName.substring(targetName.lastIndexOf(".") + 1);
  }

  private static class EntitySet {

    private String name;

    private Set<NavigationLink> links;

    private boolean singleton;

    public EntitySet(final String name) {
      this.name = name;
      links = new HashSet<NavigationLink>();
    }

    public void add(final String linkName, final String targetName, final boolean isFeed) {
      links.add(new NavigationLink(linkName, targetName, isFeed));
    }

    public String getName() {
      return name;
    }

    public void setName(final String name) {
      this.name = name;
    }

    public Set<NavigationLink> getLinks() {
      return links;
    }

    public NavigationLink getLink(final String linkName) {
      for (NavigationLink navigationLink : links) {
        if (linkName.equalsIgnoreCase(navigationLink.getName())) {
          return navigationLink;
        }
      }

      throw new NotFoundException();
    }

    public boolean isFeed(final String linkName) {
      try {
        return getLink(linkName).isFeed();
      } catch (Exception e) {
        return false;
      }
    }

    public void setLinks(final Set<NavigationLink> links) {
      this.links = links;
    }

    public EntitySet(boolean singleton) {
      this.singleton = singleton;
    }

    public boolean isSingleton() {
      return singleton;
    }

    public void setSingleton(boolean singleton) {
      this.singleton = singleton;
    }

    @Override
    public String toString() {
      return name + ": " + links;
    }
  }

  private static class NavigationLink {

    private final String name;

    private final String targetName;

    private final boolean feed;

    public NavigationLink(final String name, final String targetName, final boolean feed) {
      this.name = name;
      this.targetName = targetName;
      this.feed = feed;
    }

    public String getName() {
      return name;
    }

    public String getTargetName() {
      return targetName;
    }

    public boolean isFeed() {
      return feed;
    }

    @Override
    public String toString() {
      return name + "(feed: " + isFeed() + ")";
    }
  }
}
