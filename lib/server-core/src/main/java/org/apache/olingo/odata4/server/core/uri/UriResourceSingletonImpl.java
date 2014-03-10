/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.uri;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmSingleton;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.server.api.uri.UriResourceKind;
import org.apache.olingo.odata4.server.api.uri.UriResourceSingleton;

public class UriResourceSingletonImpl extends UriResourceTypedImpl implements UriResourceSingleton {

  private EdmSingleton singleton;

  public UriResourceSingletonImpl() {
    super(UriResourceKind.singleton);
  }

  @Override
  public EdmSingleton getSingleton() {
    return singleton;
  }

  public UriResourceSingletonImpl setSingleton(final EdmSingleton singleton) {

    this.singleton = singleton;
    return this;
  }

  @Override
  public EdmEntityType getEntityTypeFilter() {
    return (EdmEntityType) typeFilter;
  }

  @Override
  public EdmType getType() {
    return singleton.getEntityType();
  }

  @Override
  public EdmEntityType getEntityType() {
    return singleton.getEntityType();
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public String toString() {
    return singleton.getName();
  }

}
