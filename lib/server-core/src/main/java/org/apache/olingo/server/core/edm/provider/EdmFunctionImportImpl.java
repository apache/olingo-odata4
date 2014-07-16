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
package org.apache.olingo.server.core.edm.provider;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.edm.provider.FunctionImport;

import java.util.List;

public class EdmFunctionImportImpl extends EdmOperationImportImpl implements EdmFunctionImport {

  private final FunctionImport functionImport;

  public EdmFunctionImportImpl(final Edm edm, final EdmEntityContainer container, final FunctionImport functionImport) {

    super(edm, container, functionImport);
    this.functionImport = functionImport;
  }

  @Override
  public FullQualifiedName getFunctionFqn() {
    return functionImport.getFunction();
  }

  @Override
  public EdmFunction getUnboundFunction(final List<String> parameterNames) {
    return edm.getUnboundFunction(getFunctionFqn(), parameterNames);
  }

  @Override
  public List<EdmFunction> getUnboundFunctions() {
    return edm.getUnboundFunctions(getFunctionFqn());
  }

  @Override
  public boolean isIncludeInServiceDocument() {
    return functionImport.isIncludeInServiceDocument();
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.FunctionImport;
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
