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

import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmOperationImport;
import org.apache.olingo.odata4.commons.api.edm.provider.OperationImport;
import org.apache.olingo.odata4.commons.api.edm.provider.Target;

public abstract class EdmOperationImportImpl extends EdmNamedImpl implements EdmOperationImport {

  private final OperationImport operationImport;
  private final EdmEntityContainer container;
  private EdmEntitySet returnedEntitySet;

  public EdmOperationImportImpl(final EdmProviderImpl edm, final String name, final EdmEntityContainer container,
      final OperationImport operationImport) {
    super(edm, name);
    this.container = container;
    this.operationImport = operationImport;
  }

  @Override
  public EdmEntitySet getReturnedEntitySet() {
    Target target = operationImport.getEntitySet();
    if (target != null && returnedEntitySet == null) {
      EdmEntityContainer entityContainer = edm.getEntityContainer(target.getEntityContainer());
      if (entityContainer == null) {
        throw new EdmException("Can´t find entity container with name: " + target.getEntityContainer());
      }
      returnedEntitySet = entityContainer.getEntitySet(target.getTargetName());
      if (returnedEntitySet == null) {
        throw new EdmException("Can´t find entity set with name: " + target.getTargetName());
      }
    }
    return returnedEntitySet;
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    return container;
  }

}
