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
 * Represents a dynamic expression
 */
public interface DynamicAnnotationExpression extends AnnotationExpression {

  /**
   * Returns true if the expression is a logical edm:Not expression
   * @return true if the expression is a logical edm:Not expression
   */
  boolean isNot();

  /**
   * Casts the expression to a {@link Not} expression
   * @return Not expression
   */
  Not asNot();

  /**
   * Returns true iff the annotation is an expression with two operands.
   * If so, the expression is one of:
   * <ul>
   *  <li>And</li>
   *  <li>Or</li>
   *  <li>Eq</li>
   *  <li>Ne</li>
   *  <li>Gt</li>
   *  <li>Ge</li>
   *  <li>Lt</li>
   *  <li>Le</li>
   *
   * @return true iff the annotation is an expression with two operands.
   */
  boolean isTwoParamsOp();

  /**
   * Casts the expression as {@link TwoParamsOpDynamicAnnotationExpression}
   * @return TwoParamsOpDynamicAnnotationExpression two params op dynamic annotation expression
   */
  TwoParamsOpDynamicAnnotationExpression asTwoParamsOp();

  /**
   * Returns true if the expression is a edm:AnnotationPath expression
   * @return true if the expression is a edm:AnnotationPath expression
   */
  boolean isAnnotationPath();

  /**
   * Casts the expression to a {@link AnnotationPath} expression
   * @return AnnotationPath expression
   */
  AnnotationPath asAnnotationPath();

  /**
   * Returns true if the expression is a edm:Apply expression
   * @return true if the expression is a edm:Apply expression
   */
  boolean isApply();

  /**
   * Casts the expression to a {@link Apply} expression
   * @return Apply expression
   */
  Apply asApply();

  /**
   * Returns true if the expression is a edm:Cast expression
   * @return true if the expression is a edm:Cast expression
   */
  boolean isCast();

  /**
   * Casts the expression to a {@link Cast} expression
   * @return Cast expression
   */
  Cast asCast();

  /**
   * Returns true if the expression is a edm:Collection expression
   * @return true if the expression is a edm:Collection expression
   */
  boolean isCollection();

  /**
   * Casts the expression to a {@link Collection} expression
   * @return Collection expression
   */
  Collection asCollection();

  /**
   * Returns true if the expression is a edm:If expression
   * @return true if the expression is a edm:If expression
   */
  boolean isIf();

  /**
   * Casts the expression to a {@link If} expression
   * @return If expression
   */
  If asIf();

  /**
   * Returns true if the expression is a edm:IsOf expression
   * @return true if the expression is a edm:IsOf expression
   */
  boolean isIsOf();

  /**
   * Casts the expression to a {@link IsOf} expression
   * @return IsOf expression
   */
  IsOf asIsOf();

  /**
   * Returns true if the expression is a edm:LabeledElement expression
   * @return true if the expression is a edm:LabeledElement expression
   */
  boolean isLabeledElement();

  /**
   * Casts the expression to a {@link LabeledElement} expression
   * @return LabeledElement expression
   */
  LabeledElement asLabeledElement();

  /**
   * Returns true if the expression is a edm:LabeledElementReference expression
   * @return true if the expression is a edm:LabeledElementReference expression
   */
  boolean isLabeledElementReference();

  /**
   * Casts the expression to a {@link LabeledElementReference} expression
   * @return LabeledElementReference expression
   */
  LabeledElementReference asLabeledElementReference();

  /**
   * Returns true if the expression is a edm:Null expression
   * @return true if the expression is a edm:Null expression
   */
  boolean isNull();

  /**
   * Casts the expression to a {@link Null} expression
   * @return Null expression
   */
  Null asNull();

  /**
   * Returns true if the expression is a edm:NavigationPropertyPath expression
   * @return true if the expression is a edm:NavigationPropertyPath expression
   */
  boolean isNavigationPropertyPath();

  /**
   * Casts the expression to a {@link NavigationPropertyPath} expression
   * @return NavigationPropertyPath expression
   */
  NavigationPropertyPath asNavigationPropertyPath();

  /**
   * Returns true if the expression is a edm:Path expression
   * @return true if the expression is a edm:Path expression
   */
  boolean isPath();

  /**
   * Casts the expression to a {@link Path} expression
   * @return Path expression
   */
  Path asPath();

  /**
   * Returns true if the expression is a edm:PropertyPath expression
   * @return true if the expression is a edm:PropertyPath expression
   */
  boolean isPropertyPath();

  /**
   * Casts the expression to a {@link PropertyPath} expression
   * @return PropertyPath expression
   */
  PropertyPath asPropertyPath();

  /**
   * Returns true if the expression is a edm:PropertyValue expression
   * @return true if the expression is a edm:PropertyValue expression
   */
  boolean isPropertyValue();

  /**
   * Casts the expression to a {@link PropertyValue} expression
   * @return PropertyValue expression
   */
  PropertyValue asPropertyValue();

  /**
   * Returns true if the expression is a edm:Record expression
   * @return true if the expression is a edm:Record expression
   */
  boolean isRecord();

  /**
   * Casts the expression to a {@link Record} expression
   * @return Record expression
   */
  Record asRecord();

  /**
   * Returns true if the expression is a edm:UrlRef expression
   * @return true if the expression is a edm:UrlRef expression
   */
  boolean isUrlRef();

  /**
   * Casts the expression to a {@link UrlRef} expression
   * @return UrlRef expression
   */
  UrlRef asUrlRef();

}
