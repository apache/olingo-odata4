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

import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;

/**
 * Covers Functionimports and BoundFunction in URI
 */
public class UriPathInfoFunctionImpl extends UriPathInfoImpl {

  private UriParameterlist parameters;
  private EdmFunction function;
  private UriKeyPredicateList keyPredicates;

  public UriPathInfoFunctionImpl() {
    this.setKind(UriPathInfoKind.function);
  }

  public UriPathInfoFunctionImpl setParameters(UriParameterlist parameters) {
    this.parameters = parameters;
    return this;
  }

  public UriParameterlist getParameters() {
    return parameters;
  }

  public UriPathInfoFunctionImpl setFunction(EdmFunction function) {
    this.function = function;
    super.setType(function.getReturnType().getType());
    super.setCollection(function.getReturnType().isCollection());
    return this;
  }

  public UriPathInfoFunctionImpl setKeyPredicates(UriKeyPredicateList keyPredicates) {
    this.keyPredicates = keyPredicates;
    return this;
  }
}
