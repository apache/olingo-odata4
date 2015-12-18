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
package org.apache.olingo.server.api.uri;

import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

/**
 * Represents a function parameter or key predicate when used in the URI.
 */
public interface UriParameter {

  /**
   * @return Alias name if the parameter's value is an alias, otherwise null
   */
  String getAlias();

  /**
   * @return Text of the parameter's value
   */
  String getText();

  /**
   * @return Expression if the parameter's value is an expression, otherwise null
   */
  Expression getExpression();

  /**
   * @return Name of the parameter
   */
  String getName();

  /**
   * @return Name of the referenced property when referential constraints are used
   */
  String getReferencedProperty();
}
