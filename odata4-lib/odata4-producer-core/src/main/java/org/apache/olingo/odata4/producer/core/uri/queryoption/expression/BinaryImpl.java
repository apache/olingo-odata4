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
package org.apache.olingo.odata4.producer.core.uri.queryoption.expression;

import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.BinaryExpression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.Expression;
import org.apache.olingo.odata4.producer.api.uri.queryoption.expression.SupportedBinaryOperators;

public class BinaryImpl extends ExpressionImpl implements BinaryExpression, VisitableExression {

  private SupportedBinaryOperators operator;
  private ExpressionImpl left;
  private ExpressionImpl right;

  @Override
  public SupportedBinaryOperators getOperator() {
    return operator;
  }

  public BinaryExpression setOperator(SupportedBinaryOperators operator) {
    this.operator = operator;
    return this;
  }

  @Override
  public Expression getLeftOperand() {
    return left;
  }

  public void setLeftOperand(ExpressionImpl operand) {
    this.left = operand;
  }

  @Override
  public Expression getRightOperand() {
    return right;
  }

  public void setRightOperand(ExpressionImpl operand) {
    this.right = operand;

  }

  @Override
  public <T> T accept(ExpressionVisitor<T> visitor) throws ExceptionVisitExpression {
    T left = this.left.accept(visitor);
    T right = this.right.accept(visitor);
    return visitor.visitBinaryOperator(operator, left, right);
  }

}
