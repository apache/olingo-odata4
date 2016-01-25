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
package org.apache.olingo.server.core.uri;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceKind;

public class UriResourceEntitySetImpl extends UriResourceWithKeysImpl implements UriResourceEntitySet {

  private final EdmEntitySet edmEntitySet;

  public UriResourceEntitySetImpl(final EdmEntitySet edmEntitySet) {
    super(UriResourceKind.entitySet);
    this.edmEntitySet = edmEntitySet;
  }

  @Override
  public EdmEntitySet getEntitySet() {
    return edmEntitySet;
  }

  @Override
  public EdmEntityType getEntityType() {
    return edmEntitySet.getEntityType();
  }

  @Override
  public EdmType getType() {
    return edmEntitySet.getEntityType();
  }

  @Override
  public boolean isCollection() {
    return keyPredicates == null;
  }

  @Override
  public String getSegmentValue() {
    return edmEntitySet.getName();
  }
}
