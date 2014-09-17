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
package org.apache.olingo.server.core.serializer.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.server.api.serializer.ODataSerializerException;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

public final class ContextURLHelper {

  /** Builds a list of selected Properties for the ContextURL,
   *  taking care to preserve the order as defined in the EDM;
   *  returns NULL if no selection has taken place.
   * @param entityType the Entity Type
   * @param expand     the Expand option (from the URL's $expand query option)
   * @param select     the Select option (from the URL's $select query option)
   * @return a select-list String
   * @throws ODataSerializerException if an unsupported feature is used
   */
  public static String buildSelectList(final EdmEntityType entityType,
      final ExpandOption expand, final SelectOption select) throws ODataSerializerException {
    StringBuilder result = new StringBuilder();
    boolean isSelected = select != null && select.getSelectItems() != null && !select.getSelectItems().isEmpty();
    if(isSelected) {
      handleSelect(entityType, select, result);
    }

    if (ExpandSelectHelper.hasExpand(expand) && !ExpandSelectHelper.isExpandAll(expand)) {
      handleExpand(entityType, expand, result);
    }
    return result.length() == 0 ? null : result.toString();
  }

  private static void handleSelect(EdmEntityType entityType, SelectOption select, StringBuilder result) {
    if (ExpandSelectHelper.isAll(select)) {
      result.append('*');
    } else {
      final List<SelectItem> selectItems = select.getSelectItems();
      final Set<String> selectedPropertyNames = ExpandSelectHelper.getSelectedPropertyNames(selectItems);
      for (final String propertyName : entityType.getPropertyNames()) {
        if (selectedPropertyNames.contains(propertyName)) {
          if (result.length() > 0) {
            result.append(',');
          }
          final EdmProperty edmProperty = (EdmProperty) entityType.getProperty(propertyName);
          final Set<List<String>> selectedPaths = ExpandSelectHelper.getSelectedPaths(selectItems, propertyName);
          if (selectedPaths == null) {
            result.append(Encoder.encode(propertyName));
          } else {
            final List<List<String>> complexSelectedPaths = getComplexSelectedPaths(edmProperty, selectedPaths);
            boolean first = true;
            for (final List<String> path : complexSelectedPaths) {
              if (first) {
                first = false;
              } else {
                result.append(',');
              }
              boolean innerFirst = true;
              for (final String name : path) {
                if (innerFirst) {
                  innerFirst = false;
                } else {
                  result.append('/');
                }
                result.append(Encoder.encode(name));
              }
            }
          }
        }
      }
    }
  }

  private static void handleExpand(EdmEntityType entityType, ExpandOption expand, StringBuilder result)
      throws ODataSerializerException {
    final Set<String> expandedPropertyNames = ExpandSelectHelper.getExpandedPropertyNames(expand.getExpandItems());
    for (final String propertyName : entityType.getNavigationPropertyNames()) {
      if (expandedPropertyNames.contains(propertyName)) {
        final ExpandItem expandItem = ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
        if (ExpandSelectHelper.hasExpand(expandItem.getExpandOption())
            && !ExpandSelectHelper.isExpandAll(expandItem.getExpandOption())
            || ExpandSelectHelper.hasSelect(expandItem.getSelectOption())) {
          final String innerSelectList = buildSelectList(entityType.getNavigationProperty(propertyName).getType(),
              expandItem.getExpandOption(), expandItem.getSelectOption());
          if (result.length() > 0) {
            result.append(',');
          }
          result.append(Encoder.encode(propertyName)).append('(').append(innerSelectList).append(')');
        }
      }
    }
  }

  private static List<List<String>> getComplexSelectedPaths(final EdmProperty edmProperty,
      final Set<List<String>> selectedPaths) {
    List<List<String>> result = new ArrayList<List<String>>();
    if (selectedPaths == null) {
      List<String> path = new LinkedList<String>();
      path.add(edmProperty.getName());
      result.add(path);
    } else {
      final EdmComplexType type = (EdmComplexType) edmProperty.getType();
      for (final String complexPropertyName : type.getPropertyNames()) {
        if (ExpandSelectHelper.isSelected(selectedPaths, complexPropertyName)) {
          List<List<String>> complexSelectedPaths = getComplexSelectedPaths(
              (EdmProperty) type.getProperty(complexPropertyName),
              ExpandSelectHelper.getReducedSelectedPaths(selectedPaths, complexPropertyName));
          for (final List<String> path : complexSelectedPaths) {
            path.add(0, edmProperty.getName());
            result.add(path);
          }
        }
      }
    }
    return result;
  }
}
