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
package org.apache.olingo.commons.api.edm;

import java.util.List;

/**
 * A CSDL FunctionImport element
 */
public interface EdmFunctionImport extends EdmOperationImport {

  /**
   * Gets unbound functions.
   *
   * @return unbound functions
   */
  List<EdmFunction> getUnboundFunctions();

  /**
   * Gets unbound function with given parameter names.
   *
   * @param parameterNames parameter names
   * @return unbound function with given parameter names
   */
  EdmFunction getUnboundFunction(List<String> parameterNames);

  /**
   * @return the Full qualified name for the function as specified in the metadata
   */
  FullQualifiedName getFunctionFqn();

  /**
   * Returns a human readable title or null if not set.
   * @return a human readable title or null
   */
  String getTitle();
  
  /**
   * @return true if the function import must be included in the service document
   */
  boolean isIncludeInServiceDocument();

}
