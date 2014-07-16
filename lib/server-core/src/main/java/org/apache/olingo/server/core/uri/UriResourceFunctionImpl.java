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

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;

import java.util.ArrayList;
import java.util.List;

/**
 * Covers Function imports and BoundFunction in URI
 */
public class UriResourceFunctionImpl extends UriResourceWithKeysImpl implements UriResourceFunction {

  protected List<UriParameterImpl> parameters;
  protected EdmFunction function;
  protected EdmFunctionImport functionImport;
  private boolean isParameterListFilled = false;

  public UriResourceFunctionImpl() {
    super(UriResourceKind.function);
  }

  @Override
  public List<UriParameter> getParameters() {
    List<UriParameter> retList = new ArrayList<UriParameter>();
    for (UriParameterImpl item : parameters) {
      retList.add(item);
    }
    return retList;
  }

  public UriResourceFunctionImpl setParameters(final List<UriParameterImpl> parameters) {
    isParameterListFilled = true;
    this.parameters = parameters;
    return this;
  }

  @Override
  public EdmFunction getFunction() {
    return function;
  }

  public UriResourceFunctionImpl setFunction(final EdmFunction function) {
    this.function = function;
    return this;
  }

  @Override
  public EdmFunctionImport getFunctionImport() {
    return functionImport;
  }

  public UriResourceFunctionImpl setFunctionImport(final EdmFunctionImport edmFI,
      final List<UriParameterImpl> parameters) {
    functionImport = edmFI;

    setParameters(parameters);

    return this;
  }

  @Override
  public EdmType getType() {
    return function.getReturnType().getType();
  }

  @Override
  public boolean isCollection() {
    if (keyPredicates != null) {
      return false;
    }
    return function.getReturnType().isCollection();
  }

  @Override
  public String toString() {
    if (functionImport != null) {
      return functionImport.getName();
    } else if (function != null) {
      return function.getName();
    }
    return "";
  }

  public boolean isParameterListFilled() {
    return isParameterListFilled;
  }

}
