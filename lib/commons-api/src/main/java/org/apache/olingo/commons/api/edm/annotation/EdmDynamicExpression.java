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
 * Represents a dynamic expression
 */
public interface EdmDynamicExpression extends EdmExpression {
  
  /**
   * Returns true if the expression is a logical edm:Not expression
   * @return  true if the expression is a logical edm:Not expression
   */
  boolean isNot();
  
  /**
   * Casts the expression to a {@link EdmNot} expression
   * @return EdmNot expression
   */
  EdmNot asNot();

  /**
   * Returns true if the expression is a logical edm:And expression
   * @return  true if the expression is a logical edm:And expression
   */
  boolean isAnd();
  
  /**
   * Casts the expression to a {@link EdmAnd} expression
   * @return EdmAnd expression
   */
  EdmAnd asAnd();
  
  /**
   * Returns true if the expression is a logical edm:Or expression
   * @return  true if the expression is a logical edm:Or expression
   */
  boolean isOr();
  
  /**
   * Casts the expression to a {@link EdmOr} expression
   * @return EdmOr expression
   */
  EdmOr asOr();
  
  /**
   * Returns true if the expression is a edm:Eq expression
   * @return  true if the expression is a edm:Eq expression
   */
  boolean isEq();

  /**
   * Casts the expression to a {@link EdmEq} expression
   * @return EdmEq expression
   */
  EdmEq asEq();
  
  /**
   * Returns true if the expression is a edm:Ne expression
   * @return  true if the expression is a edm:Ne expression
   */
  boolean isNe();
  
  /**
   * Casts the expression to a {@link EdmNe} expression
   * @return EdmNe expression
   */
  EdmNe asNe();
  
  /**
   * Returns true if the expression is a edm:Gt expression
   * @return  true if the expression is a edm:Gt expression
   */
  boolean isGt();
  
  /**
   * Casts the expression to a {@link EdmGt} expression
   * @return EdmGt expression
   */
  EdmGt asGt();
  
  /**
   * Returns true if the expression is a edm:Ge expression
   * @return  true if the expression is a edm:Ge expression
   */
  boolean isGe();
  
  /**
   * Casts the expression to a {@link EdmGe} expression
   * @return EdmGe expression
   */
  EdmGe asGe();
  
  /**
   * Returns true if the expression is a edm:Lt expression
   * @return  true if the expression is a edm:Lt expression
   */
  boolean isLt();
  
  /**
   * Casts the expression to a {@link EdmLt} expression
   * @return EdmLt expression
   */
  EdmLt asLt();
  
  /**
   * Returns true if the expression is a edm:Le expression
   * @return  true if the expression is a edm:Le expression
   */
  boolean isLe();
  
  /**
   * Casts the expression to a {@link EdmLe} expression
   * @return EdmLe expression
   */
  EdmLe asLe();
  
  /**
   * Returns true if the expression is a edm:AnnotationPath expression
   * @return  true if the expression is a edm:AnnotationPath expression
   */
  boolean isAnnotationPath();
  
  /**
   * Casts the expression to a {@link EdmAnnotationPath} expression
   * @return EdmAnnotationPath expression
   */
  EdmAnnotationPath asAnnotationPath();
  
  /**
   * Returns true if the expression is a edm:Apply expression
   * @return  true if the expression is a edm:Apply expression
   */
  boolean isApply();
  
  /**
   * Casts the expression to a {@link EdmApply} expression
   * @return EdmApply expression
   */
  EdmApply asApply();
  
  /**
   * Returns true if the expression is a edm:Cast expression
   * @return  true if the expression is a edm:Cast expression
   */
  boolean isCast();
  
  /**
   * Casts the expression to a {@link EdmCast} expression
   * @return EdmCast expression
   */
  EdmCast asCast();
  
  /**
   * Returns true if the expression is a edm:Collection expression
   * @return  true if the expression is a edm:Collection expression
   */
  boolean isCollection();
  
  /**
   * Casts the expression to a {@link EdmCollection} expression
   * @return EdmCollection expression
   */
  EdmCollection asCollection();
  
  /**
   * Returns true if the expression is a edm:If expression
   * @return  true if the expression is a edm:If expression
   */
  boolean isIf();
  
  /**
   * Casts the expression to a {@link EdmIf} expression
   * @return EdmIf expression
   */
  EdmIf asIf();
  
  /**
   * Returns true if the expression is a edm:IsOf expression
   * @return  true if the expression is a edm:IsOf expression
   */
  boolean isIsOf();
  
  /**
   * Casts the expression to a {@link EdmIsOf} expression
   * @return EdmIsOf expression
   */
  EdmIsOf asIsOf();
  
  /**
   * Returns true if the expression is a edm:LabeledElement expression
   * @return  true if the expression is a edm:LabeledElement expression
   */
  boolean isLabeledElement();
  
  /**
   * Casts the expression to a {@link EdmLabeledElement} expression
   * @return EdmLabeledElement expression
   */
  EdmLabeledElement asLabeledElement();
  
  /**
   * Returns true if the expression is a edm:LabeledElementReference expression
   * @return  true if the expression is a edm:LabeledElementReference expression
   */
  boolean isLabeledElementReference();
  
  /**
   * Casts the expression to a {@link EdmLabeledElementReference} expression
   * @return EdmLabeledElementReference expression
   */
  EdmLabeledElementReference asLabeledElementReference();
  
  /**
   * Returns true if the expression is a edm:Null expression
   * @return  true if the expression is a edm:Null expression
   */
  boolean isNull();
  
  /**
   * Casts the expression to a {@link EdmNull} expression
   * @return EdmNull expression
   */
  EdmNull asNull();
  
  /**
   * Returns true if the expression is a edm:NavigationPropertyPath expression
   * @return  true if the expression is a edm:NavigationPropertyPath expression
   */
  boolean isNavigationPropertyPath();

  /**
   * Casts the expression to a {@link EdmNavigationPropertyPath} expression
   * @return EdmNavigationPropertyPath expression
   */
  EdmNavigationPropertyPath asNavigationPropertyPath();
  
  /**
   * Returns true if the expression is a edm:Path expression
   * @return  true if the expression is a edm:Path expression
   */
  boolean isPath();
  
  /**
   * Casts the expression to a {@link EdmPath} expression
   * @return EdmPath expression
   */
  EdmPath asPath();
  
  /**
   * Returns true if the expression is a edm:PropertyPath expression
   * @return  true if the expression is a edm:PropertyPath expression
   */
  boolean isPropertyPath();

  /**
   * Casts the expression to a {@link EdmPropertyPath} expression
   * @return EdmPropertyPath expression
   */
  EdmPropertyPath asPropertyPath();
  
  /**
   * Returns true if the expression is a edm:PropertyValue expression
   * @return  true if the expression is a edm:PropertyValue expression
   */
  boolean isPropertyValue();
  
  /**
   * Casts the expression to a {@link EdmPropertyValue} expression
   * @return EdmPropertyValue expression
   */
  EdmPropertyValue asPropertyValue();
  
  /**
   * Returns true if the expression is a edm:Record expression
   * @return  true if the expression is a edm:Record expression
   */
  boolean isRecord();
  
  /**
   * Casts the expression to a {@link EdmRecord} expression
   * @return EdmRecord expression
   */
  EdmRecord asRecord();
  
  /**
   * Returns true if the expression is a edm:UrlRef expression
   * @return  true if the expression is a edm:UrlRef expression
   */
  boolean isUrlRef();

  /**
   * Casts the expression to a {@link EdmUrlRef} expression
   * @return EdmUrlRef expression
   */
  EdmUrlRef asUrlRef();
}
