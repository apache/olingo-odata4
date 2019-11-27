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
package org.apache.olingo.commons.api.edm.provider.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

public class CsdlLogicalOrComparisonExpression extends CsdlDynamicExpression implements CsdlAnnotatable {

  private final LogicalOrComparisonExpressionType type;
  private CsdlExpression left;
  private CsdlExpression right;
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  public CsdlLogicalOrComparisonExpression(LogicalOrComparisonExpressionType type) {
    this.type = type;
  }

  /**
   * Type of the constant expression
   */
  public enum LogicalOrComparisonExpressionType {
    // Logical Operators
    /**
     * Type Edm.And must have two operands which must evaluate to a boolean value
     */
    And,
    /**
     * Type Edm.Or must have two operands which must evaluate to a boolean value
     */
    Or,
    /**
     * Type Edm.Or must have one operand
     */
    Not,

    // Comparison Operators
    /**
     * Type Edm.Eq must have two operands which must evaluate to a boolean value
     */
    Eq,
    /**
     * Type Edm.Ne must have two operands which must evaluate to a boolean value
     */
    Ne,
    /**
     * Type Edm.Gt must have two operands which must evaluate to a boolean value
     */
    Gt,
    /**
     * Type Edm.Ge must have two operands which must evaluate to a boolean value
     */
    Ge,
    /**
     * Type Edm.Lt must have two operands which must evaluate to a boolean value
     */
    Lt,
    /**
     * Type Edm.Le must have two operands which must evaluate to a boolean value
     */
    Le;

    /**
     * Creates a new type by a given string e.g. "And".
     * Will NOT throw an IlligalArgumentException for invalid types. If needed use the valueOf method.
     * @param value Type as string
     * @return Type type
     */
    public static LogicalOrComparisonExpressionType fromString(final String value) {
      LogicalOrComparisonExpressionType result = null;
      try {
        result = valueOf(value);
      } catch (IllegalArgumentException e) {
        // ignore
      }
      return result;
    }
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  public CsdlLogicalOrComparisonExpression setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * Returns the type of the logical expression
   * @return type of the logical expression
   */
  public LogicalOrComparisonExpressionType getType() {
    return type;
  }

  /**
   * The left expression. In case this is of type Edm.Not the left expression will be the same as the right expression.
   * @return The left expression.
   */
  public CsdlExpression getLeft() {
    return left;
  }

  public CsdlLogicalOrComparisonExpression setLeft(CsdlExpression left) {
    this.left = left;
    if (getType() == LogicalOrComparisonExpressionType.Not) {
      this.right = left;
    }
    return this;
  }

  /**
   * The right expression. In case this is of type Edm.Not the left expression will be the same as the right expression.
   * @return The right expression.
   */
  public CsdlExpression getRight() {
    return right;
  }

  public CsdlLogicalOrComparisonExpression setRight(CsdlExpression right) {
    this.right = right;
    if (getType() == LogicalOrComparisonExpressionType.Not) {
      this.left = right;
    }
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlLogicalOrComparisonExpression)) {
      return false;
    }
    CsdlLogicalOrComparisonExpression csdlLogComp = (CsdlLogicalOrComparisonExpression) obj;
    return (this.getLeft() == null ? csdlLogComp.getLeft() == null :
      this.getLeft().equals(csdlLogComp.getLeft()))
        && (this.getRight() == null ? csdlLogComp.getRight() == null : 
          this.getRight().equals(csdlLogComp.getRight()))
        && (this.getType() == null ? csdlLogComp.getType() == null : 
          this.getType().equals(csdlLogComp.getType()))
        && (this.getAnnotations() == null ? csdlLogComp.getAnnotations() == null : 
            checkAnnotations(csdlLogComp.getAnnotations()));
  }
  
  private boolean checkAnnotations(List<CsdlAnnotation> csdlLogCompAnnot) {
    if (csdlLogCompAnnot == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlLogCompAnnot.size()) {
      for (int i = 0; i < this.getAnnotations().size() ; i++) {
        if (!this.getAnnotations().get(i).equals(csdlLogCompAnnot.get(i))) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((left == null) ? 0 : left.hashCode());
    result = prime * result + ((right == null) ? 0 : right.hashCode());
    result = prime * result + ((annotations == null) ? 0 : 
      annotations.hashCode());
    return result;
  }
}
