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
package org.apache.olingo.fit.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class NavigationLinks {

  private final Map<String, List<InputStream>> inlines = new HashMap<String, List<InputStream>>();

  private final Map<String, List<String>> links = new HashMap<String, List<String>>();

  public NavigationLinks addInlines(final String name, final InputStream inline) {
    return addInlines(name, Collections.<InputStream> singletonList(inline));
  }

  public NavigationLinks addInlines(final String name, final List<InputStream> inline) {
    final List<InputStream> inlineStreams;
    if (inlines.containsKey(name)) {
      inlineStreams = inlines.get(name);
    } else {
      inlineStreams = new ArrayList<InputStream>();
      inlines.put(name, inlineStreams);
    }

    inlineStreams.addAll(inline);

    return this;
  }

  public NavigationLinks addLinks(final String name, final String uri) {
    return addLinks(name, Collections.<String> singletonList(uri));
  }

  public NavigationLinks addLinks(final String name, final List<String> uris) {
    final List<String> current;
    if (links.containsKey(name)) {
      current = links.get(name);
    } else {
      current = new ArrayList<String>();
      links.put(name, current);
    }

    current.addAll(uris);

    return this;
  }

  public Set<String> getInlineNames() {
    return Collections.<String> unmodifiableSet(inlines.keySet());
  }

  public Set<String> getLinkNames() {
    return Collections.<String> unmodifiableSet(links.keySet());
  }

  public List<InputStream> getInlines(final String name) {
    return Collections.<InputStream> unmodifiableList(inlines.get(name));
  }

  public List<String> getLinks(final String name) {
    return Collections.<String> unmodifiableList(links.get(name));
  }

  public Iterable<Map.Entry<String, List<InputStream>>> getInlines() {
    return inlines.entrySet();
  }

  public Iterable<Map.Entry<String, List<String>>> getLinks() {
    return links.entrySet();
  }

  public boolean contains(final String name) {
    return links.containsKey(name) || inlines.containsKey(name);
  }

  public NavigationLinks remove(final String name) {
    removeLink(name);
    removeInlines(name);
    return this;
  }

  public NavigationLinks removeLink(final String name) {
    links.remove(name);
    return this;
  }

  public NavigationLinks removeInlines(final String name) {
    if (inlines.containsKey(name)) {
      for (InputStream is : inlines.get(name)) {
        IOUtils.closeQuietly(is);
      }
    }
    links.remove(name);
    return this;
  }
}
