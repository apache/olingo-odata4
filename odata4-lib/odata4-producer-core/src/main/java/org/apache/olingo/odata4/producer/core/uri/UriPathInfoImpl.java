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

import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfo;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
//import org.apache.olingo.api.commons.InlineCount;
//import org.apache.olingo.api.uri.NavigationPropertySegment;
//import org.apache.olingo.api.uri.NavigationSegment;
//import org.apache.olingo.api.uri.SelectItem;
//import org.apache.olingo.api.uri.expression.FilterExpression;
//import org.apache.olingo.api.uri.expression.OrderByExpression;

/**
 *  
 */
public class UriPathInfoImpl implements UriPathInfo {
  private UriPathInfoKind kind;
  private EdmEntityContainer entityContainer;
  private boolean isCollection;
  private EdmEntityType targetType;

  @Override
  public EdmEntityContainer getEntityContainer() {
    return entityContainer;
  }

  public void setEntityContainer(final EdmEntityContainer entityContainer) {
    this.entityContainer = entityContainer;
  }

  @Override
  public UriPathInfoKind getKind() {
    return kind;
  }

  public void setKind(final UriPathInfoKind kind) {
    this.kind = kind;
  }

  @Override
  public boolean isCollection() {
    return isCollection;
  }

  public void setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
  }

  public EdmEntityType getTargetType() {
    return targetType;
  }

  public void setTargetType(final EdmEntityType targetType) {
    this.targetType = targetType;
  }
}
