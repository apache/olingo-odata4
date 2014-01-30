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

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriParameter;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;

public abstract class UriResourceImplKeyPred extends UriResourceImplTyped {

  protected EdmType collectionTypeFilter = null;
  protected List<UriParameterImpl> keyPredicates = null;
  protected EdmType entryTypeFilter = null;

  public UriResourceImplKeyPred(final UriResourceKind kind) {
    super(kind);
  }

  @Override
  public EdmType getTypeFilter() {
    if (entryTypeFilter != null) {
      return entryTypeFilter;
    }
    return collectionTypeFilter;
  }

  public EdmType getTypeFilterOnCollection() {
    return collectionTypeFilter;
  }

  public EdmType getTypeFilterOnEntry() {
    return entryTypeFilter;
  }

  public List<UriParameter> getKeyPredicates() {
    List<UriParameter> retList = new ArrayList<UriParameter>();
    for (UriParameterImpl item : keyPredicates) {
      retList.add(item);
    }
    return retList;
  }

  public UriResourceImplKeyPred setKeyPredicates(final List<UriParameterImpl> list) {
    keyPredicates = list;
    return this;
  }

  public void setEntryTypeFilter(final EdmType singleTypeFilter) {
    entryTypeFilter = singleTypeFilter;
  }

  public void setCollectionTypeFilter(final EdmType collectionTypeFilter) {
    this.collectionTypeFilter = collectionTypeFilter;
  }

  @Override
  public String toString(final boolean includeFilters) {

    if (includeFilters == true) {
      String tmp = "";
      if (collectionTypeFilter != null) {
        tmp += getFQN(collectionTypeFilter).toString();
      }

      if (entryTypeFilter != null) {
        if (tmp.length() == 0) {
          tmp = getFQN(entryTypeFilter).toString();
        } else {
          tmp += "/()" + getFQN(entryTypeFilter).toString();
        }
      }
      if (tmp.length() != 0) {
        return toString() + "/" + tmp;
      }
    }

    return toString();
  }

  private FullQualifiedName getFQN(final EdmType type) {
    return new FullQualifiedName(type.getNamespace(), type.getName());
  }

}