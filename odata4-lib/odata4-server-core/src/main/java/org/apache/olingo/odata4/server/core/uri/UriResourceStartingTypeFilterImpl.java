/*******************************************************************************
 * 2 * Licensed to the Apache Software Foundation (ASF) under one
 * 3 * or more contributor license agreements. See the NOTICE file
 * 4 * distributed with this work for additional information
 * 5 * regarding copyright ownership. The ASF licenses this file
 * 6 * to you under the Apache License, Version 2.0 (the
 * 7 * "License"); you may not use this file except in compliance
 * 8 * with the License. You may obtain a copy of the License at
 * 9 *
 * 10 * http://www.apache.org/licenses/LICENSE-2.0
 * 11 *
 * 12 * Unless required by applicable law or agreed to in writing,
 * 13 * software distributed under the License is distributed on an
 * 14 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * 15 * KIND, either express or implied. See the License for the
 * 16 * specific language governing permissions and limitations
 * 17 * under the License.
 * 18
 ******************************************************************************/

package org.apache.olingo.odata4.server.core.uri;

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.server.api.uri.UriResourceKind;

public class UriResourceStartingTypeFilterImpl extends UriResourceWithKeysImpl {

  private EdmType type;
  private boolean isCollection;

  public UriResourceStartingTypeFilterImpl() {
    super(null);
  }

  @Override
  public UriResourceKind getKind() {
    return kind;
  }

  @Override
  public EdmType getType() {
    return type;
  }

  public UriResourceStartingTypeFilterImpl setType(final EdmType type) {
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

  public UriResourceStartingTypeFilterImpl setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  @Override
  public String toString() {
    return type.getNamespace() + "." + type.getName();
  }

}