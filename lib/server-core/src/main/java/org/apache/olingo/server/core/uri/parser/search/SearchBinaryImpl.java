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
package org.apache.olingo.server.core.uri.parser.search;

import org.apache.olingo.server.api.uri.queryoption.search.SearchBinary;
import org.apache.olingo.server.api.uri.queryoption.search.SearchBinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.search.SearchExpression;

public class SearchBinaryImpl extends SearchExpressionImpl implements SearchBinary {

  private final SearchBinaryOperatorKind operator;
  private SearchExpression left;
  private SearchExpression right;

  public SearchBinaryImpl(SearchBinaryOperatorKind operator) {
    this.operator = operator;
  }

  public void setLeft(SearchExpression left) {
    this.left = left;
  }

  public void setRight(SearchExpression right) {
    this.right = right;
  }

  @Override
  public SearchBinaryOperatorKind getOperator() {
    return operator;
  }

  @Override
  public SearchExpression getLeftOperand() {
    return left;
  }

  @Override
  public SearchExpression getRightOperand() {
    return right;
  }

  @Override
  public String toString() {
    return "{" + left + " " + operator.name() + " " + right + '}';
  }
}
