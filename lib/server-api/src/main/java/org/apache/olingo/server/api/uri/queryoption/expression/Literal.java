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
package org.apache.olingo.server.api.uri.queryoption.expression;

import org.apache.olingo.commons.api.edm.EdmType;

/**
 * Represents a literal expression node in the expression tree
 * Literal is not validated by default
 */
public interface Literal extends Expression {

  /**
   * @return Literal
   */
  public String getText();

  /**
   * Numeric literals without an dot and without an e return the smallest possible Edm Integer type.
   * Numeric literals without an dot, without an e and larger than 2^63 - 1 are considered as Edm.Decimal
   * Numeric literals with an e, are considered to be Edm.Double
   * Numeric literals with an dot and without an e, are supposed to be Edm.Decimal
   *
   * @return Type of the literal if detected. The type of the literal is guessed by the parser.
   */
  public EdmType getType();
  
}
