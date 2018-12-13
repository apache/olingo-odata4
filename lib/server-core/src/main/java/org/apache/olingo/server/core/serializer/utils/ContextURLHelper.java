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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

public final class ContextURLHelper {

  private ContextURLHelper() { /* private ctor for helper class */}

  /**
   * Builds a list of selected Properties for the ContextURL,
   * taking care to preserve the order as defined in the EDM;
   * returns NULL if no selection has taken place.
   * @param type the structured type
   * @param expand the Expand option (from the URL's $expand query option)
   * @param select the Select option (from the URL's $select query option)
   * @return a select-list String
   * @throws SerializerException if an unsupported feature is used
   */
  public static String buildSelectList(final EdmStructuredType type,
      final ExpandOption expand, final SelectOption select) throws SerializerException {
    StringBuilder result = new StringBuilder();
    if (ExpandSelectHelper.hasSelect(select)) {
      handleSelect(type, select, result);
    }

    if (ExpandSelectHelper.hasExpand(expand) && !(null != ExpandSelectHelper.getExpandAll(expand))) {
      handleExpand(type, expand, result);
    }else if(expand != null && null != ExpandSelectHelper.getExpandAll(expand)){
      handleExpandAll(type, expand, result);
    }
    return result.length() == 0 ? null : result.toString();
  }

  private static void handleSelect(EdmStructuredType type, final SelectOption select,
      final StringBuilder result) {
    if (ExpandSelectHelper.isAll(select)) {
      result.append('*');
    } else {
      final List<SelectItem> selectItems = select.getSelectItems();
      type = getTypeFromSelectItems(selectItems, type);
      final Set<String> selectedPropertyNames = ExpandSelectHelper.getSelectedPropertyNames(selectItems);
      for (final String propertyName : type.getPropertyNames()) {
        constructSelectItemList(type, result, selectItems, selectedPropertyNames, propertyName);
      }
      for (final String propertyName : type.getNavigationPropertyNames()) {
        constructSelectItemList(type, result, selectItems, selectedPropertyNames, propertyName);
      }
      constructSelectItemListForActionsAndFunctions(type, result, selectItems);
    }
  }

  /**
   * This constructs the result based on the qualified action name
   * and qualified function name specified in the select option of the url
   * @param type
   * @param result
   * @param selectItems
   */
  private static void constructSelectItemListForActionsAndFunctions(EdmStructuredType type, StringBuilder result,
      List<SelectItem> selectItems) {
    for (SelectItem item : selectItems) {
      final UriResource resource = item.getResourcePath().getUriResourceParts().get(0);
      if (resource instanceof UriResourceAction) {
        EdmAction action = ((UriResourceAction)resource).getAction();
        if (action != null && action.isBound()) {
          String actionBindingParamType = action.getBindingParameterTypeFqn().
              getFullQualifiedNameAsString();
          if (type.getFullQualifiedName().getFullQualifiedNameAsString().
              equalsIgnoreCase(actionBindingParamType)) {
            if (result.length() > 0) {
              result.append(',');
            }
            result.append(Encoder.encode(action.getFullQualifiedName().getFullQualifiedNameAsString()));
          }
        }
      } else if (resource instanceof UriResourceFunction) {
        EdmFunction function = ((UriResourceFunction)resource).getFunction();
        if (function != null && function.isBound()) {
          String functionBindingParamType = function.getBindingParameterTypeFqn().
              getFullQualifiedNameAsString();
          if (type.getFullQualifiedName().getFullQualifiedNameAsString().
              equalsIgnoreCase(functionBindingParamType)) {
            if (result.length() > 0) {
              result.append(',');
            }
            result.append(Encoder.encode(function.getFullQualifiedName().getFullQualifiedNameAsString()));
          }
        }
      }
    }
  }

  /**
   * This fetches the correct EntityType if there is an entity type cast 
   * specified in the select option of the url
   * @param selectItems
   * @param type
   * @return
   */
  private static EdmStructuredType getTypeFromSelectItems(List<SelectItem> selectItems, EdmStructuredType type) {
    EdmStructuredType edmType = type;
    for (final SelectItem item : selectItems) {
      if (item.getStartTypeFilter() != null && item.getStartTypeFilter() instanceof EdmEntityType) {
        edmType = (EdmEntityType) item.getStartTypeFilter();
      }
    }
    return edmType;
  }

  /**
   * @param type
   * @param result
   * @param selectItems
   * @param selectedPropertyNames
   * @param propertyName
   */
  private static void constructSelectItemList(final EdmStructuredType type, final StringBuilder result,
      final List<SelectItem> selectItems, final Set<String> selectedPropertyNames, final String propertyName) {
    if (selectedPropertyNames.contains(propertyName)) {
      if (result.length() > 0) {
        result.append(',');
      }
      final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
      final Set<List<String>> selectedPaths = ExpandSelectHelper.
          getSelectedPathsWithTypeCasts(selectItems, propertyName);
      if (selectedPaths == null) {
        result.append(Encoder.encode(propertyName));
      } else {
        List<List<String>> complexSelectedPaths = edmProperty != null && 
            edmProperty.getType() instanceof EdmComplexType ?
            getComplexSelectedPaths(edmProperty, selectedPaths) : new ArrayList<List<String>>();
        if (complexSelectedPaths.isEmpty()) {
          for (List<String> path : selectedPaths) {
            complexSelectedPaths.add(path);
          }
          int position = getPositionToAddProperty(selectItems, propertyName, selectedPaths);
          if (position == -1) {
            complexSelectedPaths.get(0).add(propertyName);
          } else {
            complexSelectedPaths.get(0).add(position, propertyName);
          }
        }
        
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
    } else {
      if (type instanceof EdmEntityType) {
        final List<String> keyNames = ((EdmEntityType) type).getKeyPredicateNames();
        if (keyNames.contains(propertyName)) {
          if (result.length() > 0) {
            result.append(',');
          }
          result.append(Encoder.encode(propertyName));
        }
      }
    }
  }

  /**
   * If there is a type filter on a complex property (complex type cast) or
   * a type filter on a navigation property, this method finds the appropriate position 
   * to add the complex property or the navigation property in the result object
   * @param selectItems
   * @param propertyName
   * @param selectedPaths
   * @return
   */
  private static int getPositionToAddProperty(List<SelectItem> selectItems, String propertyName,
      Set<List<String>> selectedPaths) {
    for (SelectItem item : selectItems) {
      final List<UriResource> parts = item.getResourcePath().getUriResourceParts();
      int i = 0;
      for (UriResource part : parts) {
        if (part instanceof UriResourceComplexProperty && 
            ((UriResourceComplexProperty) part).getProperty().getName().equalsIgnoreCase(propertyName)) {
          if (((UriResourceComplexProperty)part).getComplexTypeFilter() != null) {
            return getComplexPropertyPosition(selectedPaths, (UriResourceComplexProperty)part);
          } else {
            return i;
          }
        } else if (part instanceof UriResourceNavigation &&
            ((UriResourceNavigation) part).getProperty().getName().equalsIgnoreCase(propertyName)) {
          return -1;
        }
        i++;
      }
    }
    return -1;
  }

  /**
   * @param selectedPaths
   * @param part
   */
  private static int getComplexPropertyPosition(Set<List<String>> selectedPaths, UriResourceComplexProperty part) {
    for (List<String> pathSel : selectedPaths) {
      int i = 0;
      for (String sel : pathSel) {
        if (sel.equalsIgnoreCase(part.getComplexTypeFilter().
            getFullQualifiedName().getFullQualifiedNameAsString())) {
          return i;
        }
        i++;
      }
    }
    return 0;
  }

  private static void handleExpand(final EdmStructuredType type, final ExpandOption expand, final StringBuilder result)
      throws SerializerException {
    final Set<String> expandedPropertyNames = ExpandSelectHelper.getExpandedPropertyNames(expand.getExpandItems());
    for (final String propertyName : type.getNavigationPropertyNames()) {
      if (expandedPropertyNames.contains(propertyName)) {


        final ExpandItem expandItem = ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
        if (ExpandSelectHelper.hasExpand(expandItem.getExpandOption())
            && !(null != ExpandSelectHelper.getExpandAll(expandItem.getExpandOption()))
            || ExpandSelectHelper.hasSelect(expandItem.getSelectOption())) {
          final String innerSelectList = buildSelectList(type.getNavigationProperty(propertyName).getType(),
              expandItem.getExpandOption(), expandItem.getSelectOption());
          if (innerSelectList != null) {
            if (result.length() > 0) {
              result.append(',');
            }
            result.append(Encoder.encode(propertyName)).append('(').append(innerSelectList).append(')');
          }
        } else {
          final List<UriResource> resourceParts = expandItem.getResourcePath().getUriResourceParts();
          if (resourceParts.size() > 1) {
            if (result.length() > 0) {
              result.append(',');
            }
            final List<String> path = getPropertyPath(resourceParts);
            String propertyPath = buildPropertyPath(path);
            result.append(Encoder.encode(propertyName));
            result.append("/").append(propertyPath);
          } else {
            appendExpandedProperty(result, propertyName);
          }
        }
      
      
      }
    }
  }
  
  private static void handleExpandAll(final EdmStructuredType type,
      final ExpandOption expand, final StringBuilder result) throws SerializerException {
    for (final String propertyName : type.getNavigationPropertyNames()) {
      appendExpandedProperty(result, propertyName);
    }
  }

  private static void appendExpandedProperty(StringBuilder result, String propertyName)
      throws SerializerException {
    if (result.length() > 0) {
      result.append(',');
    }
    result.append(Encoder.encode(propertyName) + "()");
  }

  private static List<String> getPropertyPath(final List<UriResource> path) {
    List<String> result = new LinkedList<String>();
    int index = 1;
    while (index < path.size() && path.get(index) instanceof UriResourceProperty) {
      result.add(((UriResourceProperty) path.get(index)).getProperty().getName());
      index++;
    }
    return result;
  }

  private static String buildPropertyPath(final List<String> path) {
    StringBuilder result = new StringBuilder();
    for (final String segment : path) {
      result.append(result.length() == 0 ? "" : '/').append(Encoder.encode(segment)); //$NON-NLS-1$
    }
    return result.length() == 0 ? null : result.toString();
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
      for (final String complexPropertyName : type.getNavigationPropertyNames()) {
        if (ExpandSelectHelper.isSelected(selectedPaths, complexPropertyName)) {
          List<List<String>> complexSelectedPaths = getComplexSelectedPaths(
              (EdmNavigationProperty) type.getProperty(complexPropertyName));
          for (final List<String> path : complexSelectedPaths) {
            path.add(0, edmProperty.getName());
            result.add(path);
          }
        }
      }
    }
    return result;
  }
  
  private static List<List<String>> getComplexSelectedPaths(EdmNavigationProperty edmProperty) {
    List<List<String>> result = new ArrayList<List<String>>();    
    List<String> path = new LinkedList<String>();
    path.add(edmProperty.getName());
    result.add(path);
    return result;
  }

  /**
   * Builds a key predicate for the ContextURL.
   * @param keys the keys as a list of {@link UriParameter} instances
   * @return a String with the key predicate
   */
  public static String buildKeyPredicate(final List<UriParameter> keys) throws SerializerException {
    if (keys == null || keys.isEmpty()) {
      return null;
    } else if (keys.size() == 1) {
      return Encoder.encode(keys.get(0).getText());
    } else {
      StringBuilder result = new StringBuilder();
      for (final UriParameter key : keys) {
        if (result.length() > 0) {
          result.append(',');
        }
        result.append(Encoder.encode(key.getName())).append('=').append(Encoder.encode(key.getText()));
      }
      return result.toString();
    }
  }
}
