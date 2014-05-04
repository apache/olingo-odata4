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
package org.apache.olingo.client.core.edm.v3;

import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.core.edm.EdmOperationImportImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EdmActionImportProxy extends EdmOperationImportImpl implements EdmActionImport {

  private final FunctionImport functionImport;

  private FullQualifiedName actionFQN;

  public EdmActionImportProxy(final Edm edm, final EdmEntityContainer container, final String name,
          final FunctionImport functionImport) {

    super(edm, container, name, functionImport.getEntitySet());
    this.functionImport = functionImport;
  }

  public FullQualifiedName getActionFQN() {
    if (actionFQN == null) {
      actionFQN = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(functionImport.getName()).
              setDefaultNamespace(container.getNamespace()).build().getFullQualifiedName();
    }
    return actionFQN;
  }

  @Override
  public EdmAction getUnboundAction() {
    return edm.getUnboundAction(getActionFQN());
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.ActionImport;
  }

}
