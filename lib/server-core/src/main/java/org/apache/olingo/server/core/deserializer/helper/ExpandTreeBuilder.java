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

import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;

public class ExpandTreeBuilder {
  
  private ExpandItemImpl parrentItem;
  private ExpandOptionImpl expandOption;

  public ExpandTreeBuilder() {
    
  }

  protected ExpandTreeBuilder(final ExpandItemImpl item) {
    parrentItem = item;
  }

  public ExpandTreeBuilder addChild(EdmNavigationProperty edmNavigationProperty) {
    if(expandOption == null) {
      expandOption = new ExpandOptionImpl();
      if(parrentItem != null) {
        ExpandOptionImpl parentOptions = (ExpandOptionImpl) parrentItem.getExpandOption();
      }
    }
    
    final ExpandItemImpl expandItem = new ExpandItemImpl();
    final UriInfoImpl uriInfo = new UriInfoImpl();
    final UriResourceNavigationPropertyImpl uriResourceNavProperty = new UriResourceNavigationPropertyImpl();
    uriResourceNavProperty.setNavigationProperty(edmNavigationProperty);
    uriInfo.addResourcePart(uriResourceNavProperty);
    expandItem.setResourcePath(uriInfo);
    expandOption.addExpandItem(expandItem);
    
    return new ExpandTreeBuilder(expandItem);
  }

  public ExpandOption build() {
    final ExpandOptionImpl expandOption = new ExpandOptionImpl();
    if(expandOption != null) {
      expandOption.addExpandItem(expandOption);
    }
    
    return expandOption;
  }
}
