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
import org.apache.olingo.odata4.producer.api.uri.UriParameter;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;

public abstract class UriResourceImplKeyPred extends UriResourceImplTyped {

  protected EdmType collectionTypeFilter = null;
  protected List<UriParameterImpl> keyPredicates = null;
  protected EdmType singleTypeFilter = null;

  public UriResourceImplKeyPred(final UriResourceKind kind) {
    super(kind);
  }

  public EdmType getComplexTypeFilter() {
    if (singleTypeFilter != null) {
      return singleTypeFilter;
    }
    return collectionTypeFilter;
  }

  public EdmType getTypeFilterOnCollection() {
    return collectionTypeFilter;
  }

  public EdmType getTypeFilterOnEntry() {
    return singleTypeFilter;
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

  public void setSingleTypeFilter(final EdmType singleTypeFilter) {
    this.singleTypeFilter = singleTypeFilter;
  }

  public void setCollectionTypeFilter(final EdmType collectionTypeFilter) {
    this.collectionTypeFilter = collectionTypeFilter;
  }

}