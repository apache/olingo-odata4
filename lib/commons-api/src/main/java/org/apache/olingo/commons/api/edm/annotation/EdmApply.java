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
package org.apache.olingo.commons.api.edm.annotation;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotatable;

/**
 * Represents an Edm:Apply expression
 */
public interface EdmApply extends EdmDynamicExpression, EdmAnnotatable {

  /**
   * A QualifiedName specifying the name of the client-side function to apply.
   * <br/>
   * OData defines three canonical functions. Services MAY support additional functions that MUST be qualified with a
   * namespace or alias other than odata. Function names qualified with odata are reserved for this specification and
   * its future versions.
   *
   * @see org.apache.olingo.commons.api.Constants#CANONICAL_FUNCTION_CONCAT
   * @see org.apache.olingo.commons.api.Constants#CANONICAL_FUNCTION_FILLURITEMPLATE
   * @see org.apache.olingo.commons.api.Constants#CANONICAL_FUNCTION_URIENCODE
   * @return function full qualified name
   */
  String getFunction();
  
  /**
   * Returns the expressions applied to the parameters of the function
   * @return List of expression
   */
  List<EdmExpression> getParameters();
}
