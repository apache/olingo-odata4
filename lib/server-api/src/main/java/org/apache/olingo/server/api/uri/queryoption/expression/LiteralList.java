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

import java.util.List;

/**
 * Represents a literal expression node in the expression tree
 * Literal is not validated by default
 *
 *  E.g. for
 *   $filter=style_code in ('AB','CD')
 *   $filter=style_value in (123,345)
 */
public interface LiteralList extends Expression {

  /**
   * @return Literal
   */
  public List<String> getText();

  /**
   * Numeric literals without an dot and without an e return the smallest possible Edm Integer type.
   * 没有点且没有e的数字文字返回尽可能小的Edm Integer类型。
   * Numeric literals without an dot, without an e and larger than 2^63 - 1 are considered as Edm.Decimal
   * 没有点，没有e且大于2 ^ 63-1的数字文字被认为是Edm.Decimal
   * Numeric literals with an e, are considered to be Edm.Double
   * 带有e的数字文字被认为是Edm.Double
   * Numeric literals with an dot and without an e, are supposed to be Edm.Decimal
   * 带有点而没有e的数字文字应该是 Edm.Decimal
   *
   * @return Type of the literal if detected. The type of the literal is guessed by the parser.
   */
  public EdmType getType();
  
}
