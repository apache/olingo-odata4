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

 
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriInfoResource;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectItem;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImpl;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplKeyPred;
import org.apache.olingo.odata4.producer.core.uri.UriResourceImplTyped;
import org.apache.olingo.odata4.producer.core.uri.UriResourcePartImpl;

public class SelectItemImpl implements SelectItem {

  private UriInfoResource path;

  private List<UriResourcePartImpl> parts = new ArrayList<UriResourcePartImpl>();
  private boolean isStar;
  private FullQualifiedName addOperationsInSchemaNameSpace;
  private EdmEntityType entityTypeCast;

  public EdmType getType() {
    UriInfoImpl uriInfo = (UriInfoImpl) path;
    UriResourcePartImpl lastResourcePart = null;
    if (uriInfo != null) {
      lastResourcePart = (UriResourcePartImpl) uriInfo.getLastResourcePart();
    }

    if (lastResourcePart instanceof UriResourceImplKeyPred) {
      UriResourceImplKeyPred lastKeyPred = (UriResourceImplKeyPred) lastResourcePart;
      if (lastKeyPred.getTypeFilterOnEntry() != null) {
        return lastKeyPred.getTypeFilterOnEntry();
      } else if (lastKeyPred.getTypeFilterOnCollection() != null) {
        return lastKeyPred.getTypeFilterOnCollection();
      }
      return lastKeyPred.getType();
    } else if (lastResourcePart instanceof UriResourceImplTyped) {
      UriResourceImplTyped lastTyped = (UriResourceImplTyped) lastResourcePart;
      EdmType type = lastTyped.getTypeFilter();
      if (type != null) {
        return type;
      }
      return lastTyped.getType();
    } else {
      return null;
    }
  }

  @Override
  public UriInfoResource getResourceInfo() {
    if (this.path == null) {
      this.path = new UriInfoImpl().setKind(UriInfoKind.resource);
    }
    return path;
  }

  @Override
  public boolean isStar() {
    return isStar;
  }

  public SelectItemImpl setStar(final boolean isStar) {
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

  public SelectItemImpl setEntityTypeCast(final EdmEntityType entityTypeCast) {
    this.entityTypeCast = entityTypeCast;
    return this;
  }

}
