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
package org.apache.olingo.server.api.uri.queryoption.apply;

import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

/**
 * Represents a compute expression.
 * @see Compute
 */
public interface ComputeExpression {

  /**
   * Gets the expression to compute.
   * @return an {@link Expression} (but never <code>null</code>)
   */
  Expression getExpression();

  /**
   * Gets the name of the computation result if an alias name has been set.
   * @return an identifier String (but never <code>null</code>)
   */
  String getAlias();
}
