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

import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceCount;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.UriResourceRef;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

public abstract class ExpandSelectHelper {

  public static boolean hasSelect(final SelectOption select) {
    return select != null && select.getSelectItems() != null && !select.getSelectItems().isEmpty();
  }

  public static boolean isAll(final SelectOption select) {
    if (hasSelect(select)) {
      for (final SelectItem item : select.getSelectItems()) {
        if (item.isStar()) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  public static Set<String> getSelectedPropertyNames(final List<SelectItem> selectItems) {
    Set<String> selected = new HashSet<String>();
    for (final SelectItem item : selectItems) {
      final UriResource resource = item.getResourcePath().getUriResourceParts().get(0);
      if (resource instanceof UriResourceProperty) {
        selected.add(((UriResourceProperty) resource).getProperty().getName());
      } else if (resource instanceof UriResourceNavigation) {
        selected.add(((UriResourceNavigation) resource).getProperty().getName());
      } else if (resource instanceof UriResourceAction) {
        selected.add(((UriResourceAction) resource).getAction().getName());
      } else if (resource instanceof UriResourceFunction) {
        selected.add(((UriResourceFunction) resource).getFunction().getName());
      }
    }
    return selected;
  }
  
  /**
   * This method creates selectedPath list checking if the resource has entity type filter,
   * complex type filter, or if resource is navigation property and if it has type filter
   * @param selectItems items in the select clause
   * @param propertyName propertyName 
   * @return Set<List<String>> return a list of selected paths
   */
  public static Set<List<String>> getSelectedPathsWithTypeCasts(
      final List<SelectItem> selectItems, final String propertyName) {
    Set<List<String>> selectedPaths = new HashSet<List<String>>();
    for (final SelectItem item : selectItems) {
      final List<UriResource> parts = item.getResourcePath().getUriResourceParts();
      final UriResource resource = parts.get(0);
      if (resource instanceof UriResourceProperty
          && propertyName.equals(((UriResourceProperty) resource).getProperty().getName())) {
        List<String> path = new ArrayList<String>();
        if (item.getStartTypeFilter() != null) {
          path.add(item.getStartTypeFilter().getFullQualifiedName().getFullQualifiedNameAsString());
        }
        if (resource instanceof UriResourceComplexProperty && 
            ((UriResourceComplexProperty) resource).getComplexTypeFilter() != null) {
          path.add(((UriResourceComplexProperty) resource).getComplexTypeFilter().
              getFullQualifiedName().getFullQualifiedNameAsString());
        } else if (resource instanceof UriResourceEntitySet && 
            ((UriResourceEntitySet) resource).getTypeFilterOnCollection() != null) {
          path.add(((UriResourceEntitySet) resource).getTypeFilterOnCollection().
              getFullQualifiedName().getFullQualifiedNameAsString());
        }
        extractPathsFromResourceParts(selectedPaths, parts, path);
      } else if (resource instanceof UriResourceNavigation
          && propertyName.equals(((UriResourceNavigation) resource).getProperty().getName()) ) {
        List<String> path = new ArrayList<String>();
        if (item.getStartTypeFilter() != null) {
          path.add(item.getStartTypeFilter().getFullQualifiedName().getFullQualifiedNameAsString());
        }
        extractPathsFromResourceParts(selectedPaths, parts, path);
      }
    }
    return selectedPaths.isEmpty() ? null : selectedPaths;
  }

  /**
   * @param selectedPaths
   * @param parts
   * @param path
   */
  private static Set<List<String>> extractPathsFromResourceParts(
      Set<List<String>> selectedPaths, final List<UriResource> parts,
      List<String> path) {
    if (parts.size() > 1) {
      for (final UriResource part : parts.subList(1, parts.size())) {
        if (part instanceof UriResourceProperty) {
          path.add(((UriResourceProperty) part).getProperty().getName());
        } else if (part instanceof UriResourceNavigation) {
          path.add(((UriResourceNavigation) part).getProperty().getName());
        }
        if (part instanceof UriResourceComplexProperty &&
            ((UriResourceComplexProperty) part).getComplexTypeFilter() != null) {
          path.add(((UriResourceComplexProperty) part).getComplexTypeFilter().
              getFullQualifiedName().getFullQualifiedNameAsString());
        }
      }
      selectedPaths.add(path);
    } else if (!path.isEmpty()) {
      selectedPaths.add(path);
    } else {
      return null;
    }
    return selectedPaths.isEmpty() ? null : selectedPaths;
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
            } else if (part instanceof UriResourceNavigation) {
              path.add(((UriResourceNavigation) part).getProperty().getName());
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

  public static Set<List<String>> getSelectedPaths(final List<SelectItem> selectItems) {
    Set<List<String>> selectedPaths = new HashSet<List<String>>();
    for (final SelectItem item : selectItems) {
      final List<UriResource> parts = item.getResourcePath().getUriResourceParts();
      final UriResource resource = parts.get(0);
      if (resource instanceof UriResourceProperty) {
        if (!parts.isEmpty()) {
          List<String> path = new ArrayList<String>();
          for (final UriResource part : parts.subList(0, parts.size())) {
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
  
  public static boolean isSelected(final Set<List<String>> selectedPaths, final String propertyName) {
    for (final List<String> path : selectedPaths) {
      if (propertyName.equals(path.get(0))) {
        return true;
      }
    }
    return false;
  }

  public static Set<List<String>> getReducedSelectedPaths(final Set<List<String>> selectedPaths,
      final String propertyName) {
    Set<List<String>> reducedPaths = new HashSet<List<String>>();
    for (final List<String> path : selectedPaths) {
      if (propertyName.equals(path.get(0))) {
        if (path.size() > 1) {
          reducedPaths.add(path.subList(1, path.size()));
        } else {
          return null;
        }
      }
    }
    return reducedPaths.isEmpty() ? null : reducedPaths;
  }

  public static boolean hasExpand(final ExpandOption expand) {
    return expand != null && expand.getExpandItems() != null && !expand.getExpandItems().isEmpty();
  }

  public static ExpandItem getExpandAll(final ExpandOption expand) {
      for (final ExpandItem item : expand.getExpandItems()) {
        if (item.isStar()) {
          return item;
        } 
      }
      return null;
    }
  
  public static Set<String> getExpandedPropertyNames(final List<ExpandItem> expandItems)
      throws SerializerException {
    Set<String> expanded = new HashSet<String>();
    for (final ExpandItem item : expandItems) {
      final List<UriResource> resourceParts = item.getResourcePath().getUriResourceParts();
      final UriResource resource = resourceParts.get(0);
      if (resource instanceof UriResourceNavigation) {
        expanded.add(((UriResourceNavigation) resource).getProperty().getName());
      }
    }
    return expanded;
  }

  public static ExpandItem getExpandItem(final List<ExpandItem> expandItems, final String propertyName) {
    for (final ExpandItem item : expandItems) {
      if (item.isStar()) {
          continue;
      }
      final List<UriResource> resourceParts = item.getResourcePath().getUriResourceParts();
      UriResource resource = null;
      if (resourceParts.get(resourceParts.size() - 1) instanceof UriResourceRef ||
          resourceParts.get(resourceParts.size() - 1) instanceof UriResourceCount) {
        resource = resourceParts.get(resourceParts.size() - 2);
      } else {
        resource = resourceParts.get(resourceParts.size() - 1);
      }
      if ((resource instanceof UriResourceNavigation
          && propertyName.equals(((UriResourceNavigation) resource).getProperty().getName())) ||
          resource instanceof UriResourceProperty
          && propertyName.equals(((UriResourceProperty) resource).getProperty().getName())) {
        return item;
      }
    }
    return null;
  }

  public static Set<List<String>> getExpandedItemsPath(ExpandOption expand) {
    Set<List<String>> expandPaths = new HashSet<List<String>>();
    if (expand != null) {
      List<ExpandItem> expandItems = expand.getExpandItems();
      for (ExpandItem item : expandItems) {
        if (item.isStar()) {
          continue;
        }
        List<UriResource> resourceParts = item.getResourcePath().getUriResourceParts();
        if (resourceParts.get(0) instanceof UriResourceComplexProperty) {
          List<String> path = new ArrayList<String>();
          for (UriResource resource : resourceParts) {
            if (resource instanceof UriResourceNavigation) {
              path.add(((UriResourceNavigation) resource).getProperty().getName());
            } else if (resource instanceof UriResourceProperty) {
              path.add(((UriResourceProperty) resource).getProperty().getName());
            }
          }
          expandPaths.add(path); 
        }
      }
    }
    return expandPaths;
  }
  
  public static Set<List<String>> getReducedExpandItemsPaths(final Set<List<String>> expandItemsPaths,
      final String propertyName) {
    Set<List<String>> reducedPaths = new HashSet<List<String>>();
    for (final List<String> path : expandItemsPaths) {
      if (propertyName.equals(path.get(0))) {
        if (path.size() > 1) {
          reducedPaths.add(path.subList(1, path.size()));
        }
      } else {
        reducedPaths.add(path);
      }
    }
    return reducedPaths.isEmpty() ? null : reducedPaths;
  }
  
  /**
   * Fetches the expand Item depending upon the type
   * @param expandItems
   * @param propertyName
   * @param type
   * @param resourceName
   * @return
   */
  public static ExpandItem getExpandItemBasedOnType(final List<ExpandItem> expandItems, 
      final String propertyName, final EdmStructuredType type, String resourceName) {
    ExpandItem expandItem = null;
    for (final ExpandItem item : expandItems) {
      boolean matched = false;
      if (item.isStar()) {
          continue;
      }
      final List<UriResource> resourceParts = item.getResourcePath().getUriResourceParts();
      UriResource resource = null;
      if (resourceParts.size() == 1) {
        resource = resourceParts.get(0);
        matched = true;
        expandItem = getMatchedExpandItem(propertyName, item, matched, resource);
      } else if (resourceParts.get(resourceParts.size() - 1) instanceof UriResourceRef ||
          resourceParts.get(resourceParts.size() - 1) instanceof UriResourceCount) {
        if (resourceParts.size() == 2) {
          resource = resourceParts.get(0);
          matched = true;
          expandItem = getMatchedExpandItem(propertyName, item, matched, resource);
        } else {
          resource = resourceParts.get(resourceParts.size() - 3);
          matched = resource.getSegmentValue().equalsIgnoreCase(resourceName) ? 
              isFoundExpandItem(type, matched, resource) : false;
          expandItem = getMatchedExpandItem(propertyName, item, matched, resourceParts.get(resourceParts.size() - 2));
        }
      } else {
        resource = resourceParts.get(resourceParts.size() - 2);
        matched = resource.getSegmentValue().equalsIgnoreCase(resourceName) ? 
            isFoundExpandItem(type, matched, resource) : false;
        expandItem = getMatchedExpandItem(propertyName, item, matched, resourceParts.get(resourceParts.size() - 1));
      }
      if (expandItem != null) {
        return expandItem;
      }
    }
    return expandItem;
  }

  /**
   * @param propertyName
   * @param item
   * @param matched
   * @param resource
   */
  private static ExpandItem getMatchedExpandItem(final String propertyName, final ExpandItem item, boolean matched,
      UriResource resource) {
    if (matched && ((resource instanceof UriResourceNavigation
        && propertyName.equals(((UriResourceNavigation) resource).getProperty().getName())) ||
        resource instanceof UriResourceProperty
        && propertyName.equals(((UriResourceProperty) resource).getProperty().getName()))) {
      return item;
    }
    return null;
  }

  /**
   * @param type
   * @param matched
   * @param resource
   * @return
   */
  private static boolean isFoundExpandItem(final EdmStructuredType type, 
      boolean matched, UriResource resource) {
    if (!matched) {
      if ((resource instanceof UriResourceProperty && 
              type.compatibleTo(((UriResourceProperty) resource).getType())) ||
          (resource instanceof UriResourceNavigation && 
              type.compatibleTo(((UriResourceNavigation) resource).getType()))) {
        matched = true;
      }
    }
    return matched;
  }
}