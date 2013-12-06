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
package org.apache.olingo.odata4.commons.core.edm.provider;

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;

public abstract class EdmTypeImpl extends EdmNamedImpl implements EdmType {

  private final EdmTypeKind kind;
  private final String namespace;

  public EdmTypeImpl(final EdmProviderImpl edm, final FullQualifiedName name, final EdmTypeKind kind) {
    super(edm, name.getName());
    namespace = name.getNamespace();
    this.kind = kind;
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public EdmTypeKind getKind() {
    return kind;
  }

}
