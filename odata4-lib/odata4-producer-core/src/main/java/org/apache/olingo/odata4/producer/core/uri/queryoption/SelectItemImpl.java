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
package org.apache.olingo.odata4.producer.core.uri.queryoption;

//TODO rework this 
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriResourcePart;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectItem;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplKeyPred;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplTyped;
import org.apache.olingo.odata4.producer.core.uri.UriResourcePartImpl;

public class SelectItemImpl implements SelectItem {

  private UriResourceImplTyped lastResourcePart;
  private List<UriResourcePartImpl> parts = new ArrayList<UriResourcePartImpl>();
  private boolean isStar;
  private FullQualifiedName addOperationsInSchemaNameSpace;
  private EdmEntityType entityTypeCast;

  public EdmType getType() {
    if (lastResourcePart != null) {
      if (lastResourcePart instanceof UriResourceImplKeyPred) {
        UriResourceImplKeyPred lastKeyPred = (UriResourceImplKeyPred) lastResourcePart;
        if (lastKeyPred.getTypeFilterOnEntry() != null) {
          return lastKeyPred.getTypeFilterOnEntry();
        } else if (lastKeyPred.getTypeFilterOnCollection() != null) {
          return lastKeyPred.getTypeFilterOnCollection();
        }
      }
      EdmType type = lastResourcePart.getTypeFilter();
      if (type != null) {
        return type;
      }
    }
    return null;
  }

  @Override
  public boolean isStar() {
    return isStar;
  }

  public SelectItemImpl setStar(boolean isStar) {
    this.isStar = isStar;
    return this;
  }

  @Override
  public boolean isAllOperationsInSchema() {
    if (addOperationsInSchemaNameSpace == null) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public FullQualifiedName getAllOperationsInSchemaNameSpace() {
    return addOperationsInSchemaNameSpace;
  }

  public void addAllOperationsInSchema(final FullQualifiedName addOperationsInSchemaNameSpace) {
    this.addOperationsInSchemaNameSpace = addOperationsInSchemaNameSpace;
  }

  @Override
  public EdmEntityType getEntityTypeCast() {
    return entityTypeCast;
  }

  public SelectItemImpl setEntityTypeCast(EdmEntityType entityTypeCast) {
    this.entityTypeCast = entityTypeCast;
    return this;
  }

  @Override
  public List<UriResourcePart> getPropertyChainList() {

    return null;
  }


  public UriResourcePart getLastPart() {
    return lastResourcePart;
  }

  public SelectItemImpl addPath(UriResourceImplTyped resourcePart) {
    parts.add(resourcePart);
    this.lastResourcePart = resourcePart;
    return this;
  }

}
