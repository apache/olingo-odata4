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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.producer.api.uri.UriParameter;
import org.apache.olingo.odata4.producer.api.uri.UriResourceFunction;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;

/**
 * Covers Function imports and BoundFunction in URI
 */
public class UriResourceFunctionImpl extends UriResourceImplKeyPred implements UriResourceFunction {

  protected List<UriParameterImpl> parameters;
  protected EdmFunction function;
  protected EdmFunctionImport functionImport;

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

  public UriResourceFunctionImpl setParameters(List<UriParameterImpl> parameters) {
    this.parameters = parameters;
    return this;
  }

  @Override
  public EdmFunction getFunction() {
    return function;
  }

  public UriResourceFunctionImpl setFunction(EdmFunction function) {
    this.function = function;
    return this;
  }

  @Override
  public EdmFunctionImport getFunctionImport() {
    return functionImport;
  }

  public UriResourceFunctionImpl setFunctionImport(EdmFunctionImport edmFI, List<UriParameterImpl> parameters) {
    this.functionImport = edmFI;
    this.parameters = parameters;

    List<String> names = new ArrayList<String>();
    for (UriParameterImpl item : parameters) {
      names.add(item.getName());
    }

    setFunction(edmFI.getFunction(names));

    return this;
  }

  @Override
  public String toString() {
    return function.getName() + super.toString();
  }

  @Override
  public EdmType getType() {

    return function.getReturnType().getType();
     
  }

  @Override
  public boolean isCollection() {
    if (keyPredicates != null ) {
      return false;
    }
    return function.getReturnType().isCollection();
  }

  

  
}
