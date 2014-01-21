/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.uri;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.odata4.producer.core.uri.expression.Expression;

public abstract class UriPathInfoImpl {

  private EdmType initialType = null;
  private EdmType finalType = null;

  private UriPathInfoKind kind;
  private EdmType collectionTypeFilter = null;
  private UriKeyPredicateList keyPredicates = null;
  private EdmType singleTypeFilter = null;

  private class PathListItem {
    private EdmElement property; // ia EdmProperty or EdmNavigationProperty

    private EdmType initialType;
    private EdmType finalType;
    private boolean isCollection;
  }

  private List<PathListItem> pathList = null;
  private boolean isCollection;

  public UriPathInfoImpl setType(EdmType edmType) {
    this.initialType = edmType;
    this.finalType = edmType;
    return this;
  }

  public EdmType getType() {
    return finalType;
  }

  public EdmType getInitialType() {
    return initialType;
  }

  public EdmType getCollectionTypeFilter() {
    return collectionTypeFilter;
  }

  public EdmType getSingleTypeFilter() {
    return singleTypeFilter;
  }

  public FullQualifiedName getFullType() {
    return new FullQualifiedName(finalType.getNamespace(), finalType.getName());
  }

  public UriPathInfoImpl setKind(UriPathInfoKind kind) {
    this.kind = kind;
    return this;
  }

  public UriPathInfoKind getKind() {
    return kind;
  }

  public UriPathInfoImpl setKeyPredicates(UriKeyPredicateList keyPredicates) {
    if (this.isCollection() != true) {
      // throw exception
    }
    this.keyPredicates = keyPredicates;
    this.setCollection(false);
    return this;
  }

  public UriKeyPredicateList getKeyPredicates() {
    return this.keyPredicates;
  }

  public UriPathInfoImpl addTypeFilter(EdmStructuralType targetType) {
    // TODO if there is a navigation path the type filter musst be applied to the last
    if (pathList == null) {
      if (keyPredicates == null) {
        if (collectionTypeFilter != null) {
          // TODO exception Type filters are not directy chainable
        }
        if (targetType.compatibleTo((EdmStructuralType) finalType)) {
          collectionTypeFilter = targetType;
          finalType = targetType;
        } else {
          // TODO throw exception
        }
      } else {
        if (singleTypeFilter != null) {
          // TODO exception Type filters are not directy chainable
        }
        if (targetType.compatibleTo((EdmStructuralType) finalType)) {
          singleTypeFilter = targetType;
          finalType = targetType;
        } else {
          // TODO throw exception
        }
      }
    } else {
      PathListItem last = pathList.get(pathList.size() - 1);

      if (targetType.compatibleTo(last.finalType)) {
        last.finalType = targetType;
      }
    }
    return this;
  }

  public UriPathInfoImpl addProperty(EdmProperty property) {
    if (pathList == null) {
      pathList = new ArrayList<PathListItem>();
    }

    PathListItem newItem = new PathListItem();
    newItem.property = property;
    newItem.initialType = property.getType();
    newItem.finalType = property.getType();
    newItem.isCollection = property.isCollection();
    pathList.add(newItem);

    this.finalType = newItem.finalType;
    this.isCollection = newItem.isCollection;
    return this;
  }

  public UriPathInfoImpl addNavigationProperty(EdmNavigationProperty property) {
    if (pathList == null) {
      pathList = new ArrayList<PathListItem>();
    }
    PathListItem newItem = new PathListItem();
    newItem.property = property;
    newItem.initialType = property.getType();
    newItem.finalType = property.getType();
    newItem.isCollection = property.isCollection();
    pathList.add(newItem);

    this.finalType = newItem.finalType;
    this.isCollection = newItem.isCollection;
    return this;
  }

  public int getPropertyCount() {
    return pathList.size();
  }

  public EdmElement getProperty(int index) {
    return pathList.get(index).property;

  }

  public UriPathInfoImpl setCollection(boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  public boolean isCollection() {

    return isCollection;
  }

  @Override
  public String toString() {
    String ret = "";
    int i = 0;
    while (i < pathList.size()) {
      if (i > 0) {
        ret += "/";
      }
      ret += pathList.get(i).property.getName();
      i++;
    }
    return ret;
  }

}
