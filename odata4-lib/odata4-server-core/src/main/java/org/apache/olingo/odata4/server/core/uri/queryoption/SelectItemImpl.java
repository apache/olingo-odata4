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
package org.apache.olingo.odata4.server.core.uri.queryoption;

import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.queryoption.SelectItem;

public class SelectItemImpl implements SelectItem {

  private UriInfoResource path;

  private boolean isStar;
  private FullQualifiedName addOperationsInSchemaNameSpace;

    @Override
  public UriInfoResource getResourceInfo() {

    return path;
  }

  public SelectItemImpl setResourceInfo(UriInfoResource path) {
    this.path = path;
    return this;
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
 

}
