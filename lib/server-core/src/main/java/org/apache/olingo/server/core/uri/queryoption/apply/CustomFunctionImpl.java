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
package org.apache.olingo.server.core.uri.queryoption.apply;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.apply.CustomFunction;

/**
 * Represents a transformation with a custom function.
 */
public class CustomFunctionImpl implements CustomFunction {

  private EdmFunction function = null;
  private List<UriParameter> parameters;

  @Override
  public Kind getKind() {
    return Kind.CUSTOM_FUNCTION;
  }

  @Override
  public EdmFunction getFunction() {
    return function;
  }

  public CustomFunctionImpl setFunction(final EdmFunction function) {
    this.function = function;
    return this;
  }

  @Override
  public List<UriParameter> getParameters() {
    return parameters == null ?
        Collections.<UriParameter> emptyList() :
        Collections.unmodifiableList(parameters);
  }

  public CustomFunctionImpl setParameters(final List<UriParameter> parameters) {
    this.parameters = parameters;
    return this;
  }
}
