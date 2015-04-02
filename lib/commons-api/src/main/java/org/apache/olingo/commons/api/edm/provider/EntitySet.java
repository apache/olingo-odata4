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
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EntitySet extends BindingTarget {

  private static final long serialVersionUID = 5291570018480936643L;
  
  //Default for EntitySets is true
  private boolean includeInServiceDocument = true;

  @Override
  public EntitySet setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public EntitySet setNavigationPropertyBindings(final List<NavigationPropertyBinding> navigationPropertyBindings) {
    this.navigationPropertyBindings = navigationPropertyBindings;
    return this;
  }

  @Override
  public EntitySet setType(final String type) {
    this.type = new FullQualifiedName(type);
    return this;
  }
  
  @Override
  public EntitySet setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }

  public boolean isIncludeInServiceDocument() {
    return includeInServiceDocument;
  }

  public EntitySet setIncludeInServiceDocument(final boolean includeInServiceDocument) {
    this.includeInServiceDocument = includeInServiceDocument;
    return this;
  }
}
