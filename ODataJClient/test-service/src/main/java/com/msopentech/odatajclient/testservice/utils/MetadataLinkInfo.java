/**
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
package com.msopentech.odatajclient.testservice.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetadataLinkInfo {

    protected final static Set<String> feed = new HashSet<String>();

    protected final static Map<String, String> entitySetAlias = new HashMap<String, String>();

    private Map<String, EntitySet> entitySets = new HashMap<String, EntitySet>();

    public void addLinkName(final String entitySetName, final String linkName) {
        final EntitySet entitySet;
        if (entitySets.containsKey(entitySetName)) {
            entitySet = entitySets.get(entitySetName);
        } else {
            entitySet = new EntitySet(entitySetName);
            entitySets.put(entitySetName, entitySet);
        }

        entitySet.add(linkName);
    }

    public Collection<EntitySet> getEntitySets() {
        return entitySets.values();
    }

    public Set<String> getNavigationLinkNames(final String entitySetName) {
        return entitySets.containsKey(entitySetName)
                ? entitySets.get(entitySetName).getLinks() : Collections.<String>emptySet();
    }

    public boolean exists(final String entitySetName, final String linkName) {
        return getNavigationLinkNames(entitySetName).contains(linkName);
    }

    private static class EntitySet {

        private String name;

        private Set<String> links;

        public EntitySet(final String name) {
            this.name = name;
            links = new HashSet<String>();
        }

        public void add(final String linkName) {
            links.add(linkName);
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Set<String> getLinks() {
            return links;
        }

        public void setLinks(final Set<String> links) {
            this.links = links;
        }

        @Override
        public String toString() {
            return name + ": " + links;
        }
    }
}
