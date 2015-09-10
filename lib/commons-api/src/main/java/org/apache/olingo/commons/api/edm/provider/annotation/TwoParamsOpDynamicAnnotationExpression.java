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

/**
 * Represents a generic expression with two child exprssions
 */
public interface TwoParamsOpDynamicAnnotationExpression extends DynamicAnnotationExpression {

  /**
   * Type (Operator) of the expression
   */
  public static enum Type {
    /**
     * Logical And
     */
    And,
    /**
     * Logical Or
     */
    Or,
    /**
     * Equals
     */
    Eq,
    /**
     * Not equals
     */
    Ne,
    /**
     * Greater than
     */
    Gt,
    /**
     * Greater or equals than
     */
    Ge,
    /**
     * Less than
     */
    Lt,
    /**
     * Less or equals than
     */
    Le;

    /**
     * Creates the type(Operator) of a expressin
     * @param value Value of the operator like "And" or "Eq"
     * @return Type(Operator) of the expression
     */
    public static Type fromString(final String value) {
      Type result = null;
      for (Type type : values()) {
        if (value.equals(type.name())) {
          result = type;
        }
      }
      return result;
    }
  }

  /**
   * Returns the type of the expression result
   * @return Type of the result
   */
  Type getType();

  /**
   * Returns the first expression (left child)
   * @return Child expression
   */
  DynamicAnnotationExpression getLeftExpression();

  /**
   * Returns the second expression (right child)
   * @return Child expression
   */
  DynamicAnnotationExpression getRightExpression();
}
