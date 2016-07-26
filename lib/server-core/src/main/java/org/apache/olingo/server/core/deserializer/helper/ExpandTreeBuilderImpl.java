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
package org.apache.olingo.server.core.deserializer.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;

public class ExpandTreeBuilderImpl extends ExpandTreeBuilder {

  private final Map<String, ExpandTreeBuilder> childBuilderCache = new HashMap<String, ExpandTreeBuilder>();
  private final ExpandItemImpl parentItem;
  private ExpandOptionImpl expandOption = null;

  private ExpandTreeBuilderImpl(final ExpandItemImpl parentItem) {
    this.parentItem = parentItem;
  }
  
  
  @Override
  public ExpandTreeBuilder expand(final EdmNavigationProperty edmNavigationProperty) {
    if (expandOption == null) {
      expandOption = new ExpandOptionImpl();
      if(parentItem != null && parentItem.getExpandOption() == null){
        parentItem.setSystemQueryOption(expandOption);
      }
    }
    
    ExpandTreeBuilder builder = childBuilderCache.get(edmNavigationProperty.getName());
    if(builder == null){
      ExpandItemImpl expandItem = buildExpandItem(edmNavigationProperty);
      expandOption.addExpandItem(expandItem);
      builder = new ExpandTreeBuilderImpl(expandItem);
      childBuilderCache.put(edmNavigationProperty.getName(), builder);
    }
    
    return builder;
  }

  @Override
  public ExpandOption build() {
    return expandOption;
  }
  
  public static ExpandTreeBuilder create(){
    return new ExpandTreeBuilderImpl(null);
  }
}