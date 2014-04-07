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
package org.apache.olingo.client.core.edm;

import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.v4.FunctionImport;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EdmFunctionImportImpl extends EdmOperationImportImpl implements EdmFunctionImport {

  private final FunctionImport functionImport;

  public EdmFunctionImportImpl(final Edm edm, final EdmEntityContainer container, final String name,
          final FunctionImport functionImport) {

    super(edm, container, name, functionImport.getEntitySet());
    this.functionImport = functionImport;
  }

  @Override
  public EdmFunction getUnboundFunction(final List<String> parameterNames) {
    return getBoundFunction(parameterNames, null, null);
  }

  @Override
  public EdmFunction getBoundFunction(final List<String> parameterNames,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    return edm.getFunction(new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(functionImport.getFunction()).
            setDefaultNamespace(container.getNamespace()).build().getFullQualifiedName(),
            bindingParameterTypeName, isBindingParameterCollection, parameterNames);
  }

  @Override
  public boolean isIncludeInServiceDocument() {
    return functionImport.isIncludeInServiceDocument();
  }

  @Override
  public FullQualifiedName getFunctionFqn() {
    return new FullQualifiedName(functionImport.getFunction());
  }
}
