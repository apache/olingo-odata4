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
package org.apache.olingo.server.core.uri.queryoption.expression;

import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.Binary;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;

public class BinaryImpl implements Binary {

  private final Expression left;
  private final BinaryOperatorKind operator;
  private final Expression right;
  private final EdmType type;

  public BinaryImpl(final Expression left, final BinaryOperatorKind operator, final Expression right,
      final EdmType type) {
    this.left = left;
    this.operator = operator;
    this.right = right;
    this.type = type;
  }

  @Override
  public BinaryOperatorKind getOperator() {
    return operator;
  }

  @Override
  public Expression getLeftOperand() {
    return left;
  }

  @Override
  public Expression getRightOperand() {
    return right;
  }

  public EdmType getType() {
    return type;
  }

  @Override
  public <T> T accept(final ExpressionVisitor<T> visitor) throws ExpressionVisitException, ODataApplicationException {
    T localLeft = this.left.accept(visitor);
    T localRight = this.right.accept(visitor);
    return visitor.visitBinaryOperator(operator, localLeft, localRight);
  }

  @Override
  public String toString() {
    return "{" + left + " " + operator.name() + " " + right + '}';
  }
}
