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

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceKind;

/**
 * Covers Function imports and BoundFunction in URI
 */
public class UriResourceFunctionImpl extends UriResourceWithKeysImpl implements UriResourceFunction {

  private final EdmFunctionImport functionImport;
  private final EdmFunction function;
  private final List<UriParameter> parameters;

  public UriResourceFunctionImpl(final EdmFunctionImport edmFunctionImport, final EdmFunction function,
      final List<UriParameter> parameters) {
    super(UriResourceKind.function);
    this.functionImport = edmFunctionImport;
    this.function = function;
    this.parameters = parameters;
  }

  @Override
  public List<UriParameter> getParameters() {
    return parameters == null ?
        Collections.<UriParameter> emptyList() :
          Collections.unmodifiableList(parameters);
  }

  @Override
  public EdmFunction getFunction() {
    return function;
  }

  @Override
  public EdmFunctionImport getFunctionImport() {
    return functionImport;
  }

  @Override
  public EdmType getType() {
    return function.getReturnType().getType();
  }

  @Override
  public boolean isCollection() {
    return keyPredicates == null && function.getReturnType().isCollection();
  }

  @Override
  public String getSegmentValue() {
    return functionImport == null ? (function == null ? "" : function.getName()) : functionImport.getName();
  }
}
