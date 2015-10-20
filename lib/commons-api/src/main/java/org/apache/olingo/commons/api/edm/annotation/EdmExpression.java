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
package org.apache.olingo.commons.api.edm.annotation;

/**
 * Super type of all annotation expressions
 * A expression is either constant or dynamic
 */
public interface EdmExpression {

  enum EdmExpressionType {
    //Constant
    Binary,
    Bool,
    Date,
    DateTimeOffset,
    Decimal,
    Duration,
    EnumMember,
    Float,
    Guid,
    Int,
    String,
    TimeOfDay,
    //Dynamic
    //Logical
    And,
    Or,
    Not,
    //Comparison
    Eq,
    Ne,
    Gt,
    Ge,
    Lt,
    Le,
    //Other
    AnnotationPath,
    Apply,
    Cast,
    Collection,
    If,
    IsOf,
    LabeledElement,
    LabeledElementReference,
    Null,
    NavigationPropertyPath,
    Path,
    PropertyPath,
    Record,
    UrlRef;
  }
  
  /**
   * See {@link EdmExpressionType} for details.
   * @return the type of this expression
   */
  EdmExpressionType getExpressionType();
  
  /**
   * Will return the name of the expression e.g. Apply or Cast.
   * @return the name of the expression
   */
  String getExpressionName();

  /**
   * Return true if the expression is constant
   * @return true if the expression is constant
   */
  boolean isConstant();
  
  /**
   * Casts the expression to {@link EdmConstantExpression}
   * @return Constant Expression
   */
  EdmConstantExpression asConstant();
  
  /**
   * Return true if the expression is dynamic
   * @return true if the expression is dynamic
   */
  boolean isDynamic();
  
  /**
   * Cast the expression to {@link EdmDynamicExpression}
   * @return Dynamic Expression
   */
  EdmDynamicExpression asDynamic();
}
