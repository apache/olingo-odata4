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
package org.apache.olingo.commons.core.edm.annotation;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.annotation.EdmExpression;
import org.apache.olingo.commons.api.edm.annotation.EdmLogicalOrComparisonExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression;
//CHECKSTYLE:OFF
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlLogicalOrComparisonExpression.LogicalOrComparisonExpressionType;

//CHECKSTYLE:ON

public abstract class AbstractEdmLogicalOrComparisonExpression
    extends AbstractEdmAnnotatableDynamicExpression implements EdmLogicalOrComparisonExpression {

  private EdmExpression left;
  private EdmExpression right;
  private CsdlLogicalOrComparisonExpression csdlExp;

  public AbstractEdmLogicalOrComparisonExpression(Edm edm, CsdlLogicalOrComparisonExpression csdlExp) {
    super(edm, csdlExp.getType().toString(), csdlExp);
    this.csdlExp = csdlExp;
  }

  @Override
  public EdmExpressionType getExpressionType() {
    switch (csdlExp.getType()) {
    case And:
      return EdmExpressionType.And;
    case Or:
      return EdmExpressionType.Or;
    case Not:
      return EdmExpressionType.Not;
    case Eq:
      return EdmExpressionType.Eq;
    case Ne:
      return EdmExpressionType.Ne;
    case Gt:
      return EdmExpressionType.Gt;
    case Ge:
      return EdmExpressionType.Ge;
    case Lt:
      return EdmExpressionType.Lt;
    case Le:
      return EdmExpressionType.Le;
    default:
      throw new EdmException("Invalid Expressiontype for logical or comparison expression: " + csdlExp.getType());
    }
  }

  @Override
  public EdmExpression getLeftExpression() {
    if (left == null) {
      if (csdlExp.getLeft() == null) {
        throw new EdmException("Comparison Or Logical expression MUST have a left and right expression.");
      }
      left = AbstractEdmExpression.getExpression(edm, csdlExp.getLeft());
      if (csdlExp.getType() == LogicalOrComparisonExpressionType.Not) {
        right = left;
      }
    }
    return left;
  }

  @Override
  public EdmExpression getRightExpression() {
    if (right == null) {
      if (csdlExp.getRight() == null) {
        throw new EdmException("Comparison Or Logical expression MUST have a left and right expression.");
      }
      right = AbstractEdmExpression.getExpression(edm, csdlExp.getRight());
      if (csdlExp.getType() == LogicalOrComparisonExpressionType.Not) {
        left = right;
      }
    }
    return right;
  }
}
