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
package org.apache.olingo.server.core.serializer.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.olingo.server.api.serializer.ODataSerializerException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;

public abstract class ExpandSelectHelper {

  public static boolean isAll(final ExpandItem options) {
    if (options == null || options.getSelectOption() == null
        || options.getSelectOption().getSelectItems() == null
        || options.getSelectOption().getSelectItems().isEmpty()) {
      return true;
    } else {
      for (final SelectItem item : options.getSelectOption().getSelectItems()) {
        if (item.isStar()) {
          return true;
        }
      }
      return false;
    }
  }

  public static Set<String> getSelectedPropertyNames(final List<SelectItem> selectItems) {
    Set<String> selected = new HashSet<String>();
    for (final SelectItem item : selectItems) {
      final UriResource resource = item.getResourcePath().getUriResourceParts().get(0);
      if (resource instanceof UriResourceProperty) {
        selected.add(((UriResourceProperty) resource).getProperty().getName());
      }
    }
    return selected;
  }

  public static Set<List<String>> getSelectedPaths(final List<SelectItem> selectItems, final String propertyName) {
    Set<List<String>> selectedPaths = new HashSet<List<String>>();
    for (final SelectItem item : selectItems) {
      final List<UriResource> parts = item.getResourcePath().getUriResourceParts();
      final UriResource resource = parts.get(0);
      if (resource instanceof UriResourceProperty
          && propertyName.equals(((UriResourceProperty) resource).getProperty().getName())) {
        if (parts.size() > 1) {
          List<String> path = new ArrayList<String>();
          for (final UriResource part : parts.subList(1, parts.size())) {
            if (part instanceof UriResourceProperty) {
              path.add(((UriResourceProperty) part).getProperty().getName());
            }
          }
          selectedPaths.add(path);
        } else {
          return null;
        }
      }
    }
    return selectedPaths.isEmpty() ? null : selectedPaths;
  }

  public static boolean hasExpand(final ExpandItem options) {
    return options != null && options.getExpandOption() != null && options.getExpandOption().getExpandItems() != null
        && !options.getExpandOption().getExpandItems().isEmpty();
  }

  public static boolean isExpandAll(final ExpandItem options) {
    for (final ExpandItem item : options.getExpandOption().getExpandItems()) {
      if (item.isStar()) {
        return true;
      }
    }
    return false;
  }

  public static Set<String> getExpandedPropertyNames(final List<ExpandItem> expandItems)
      throws ODataSerializerException {
    Set<String> expanded = new HashSet<String>();
    for (final ExpandItem item : expandItems) {
      final List<UriResource> resourceParts = item.getResourcePath().getUriResourceParts();
      if (resourceParts.size() == 1) {
        final UriResource resource = resourceParts.get(0);
        if (resource instanceof UriResourceNavigation) {
          expanded.add(((UriResourceNavigation) resource).getProperty().getName());
        }
      } else {
        throw new ODataSerializerException("Expand is not supported within complex properties.",
            ODataSerializerException.MessageKeys.NOT_IMPLEMENTED);
      }
    }
    return expanded;
  }

  public static ExpandItem getExpandItem(final List<ExpandItem> expandItems, final String propertyName) {
    for (final ExpandItem item : expandItems) {
      final UriResource resource = item.getResourcePath().getUriResourceParts().get(0);
      if (resource instanceof UriResourceNavigation
          && propertyName.equals(((UriResourceNavigation) resource).getProperty().getName())) {
        return item;
      }
    }
    return null;
  }

}