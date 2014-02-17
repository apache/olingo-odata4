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

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;
import org.apache.olingo.odata4.producer.api.uri.UriResourceRoot;

public class UriResourceRootImpl extends UriResourceWithKeysImpl implements UriResourceRoot {

  private EdmType type;
  private boolean isCollection;

  public UriResourceRootImpl() {
    super(UriResourceKind.root);
  }

  @Override
  public EdmType getType() {
    return type;
  }

  public UriResourceRootImpl setType(final EdmType type) {
    this.type = type;
    return this;
  }

  @Override
  public boolean isCollection() {
    if (keyPredicates != null) {
      return false;
    }
    return isCollection;
  }

  public UriResourceRootImpl setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  @Override
  public String toString() {
    return "$root";
  }

}
