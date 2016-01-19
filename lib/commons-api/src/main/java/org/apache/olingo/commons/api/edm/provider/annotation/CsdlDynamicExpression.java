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

import java.util.Collection;

public abstract class CsdlDynamicExpression extends CsdlExpression {

  /**
   * Returns true if the expression is a logical expression
   * @return true if the expression is a logical expression
   */
  public boolean isLogicalOrComparison() {
    return this instanceof CsdlLogicalOrComparisonExpression;
  }

  /**
   * Casts the expression to a {@link CsdlLogicalOrComparisonExpression} expression
   * @return CsdlLogicalExpression expression
   */
  public CsdlLogicalOrComparisonExpression asLogicalOrComparison() {
    return isLogicalOrComparison() ? (CsdlLogicalOrComparisonExpression) this : null;
  }

  /**
   * Returns true if the expression is a edm:AnnotationPath expression
   * @return true if the expression is a edm:AnnotationPath expression
   */
  public boolean isAnnotationPath() {
    return this instanceof CsdlAnnotationPath;
  }

  /**
   * Casts the expression to a {@link CsdlAnnotationPath} expression
   * @return AnnotationPath expression
   */
  public CsdlAnnotationPath asAnnotationPath() {
    return isAnnotationPath() ? (CsdlAnnotationPath) this : null;
  }

  /**
   * Returns true if the expression is a edm:Apply expression
   * @return true if the expression is a edm:Apply expression
   */
  public boolean isApply() {
    return this instanceof CsdlApply;
  }

  /**
   * Casts the expression to a {@link CsdlApply} expression
   * @return Apply expression
   */
  public CsdlApply asApply() {
    return isApply() ? (CsdlApply) this : null;
  }

  /**
   * Returns true if the expression is a edm:Cast expression
   * @return true if the expression is a edm:Cast expression
   */
  public boolean isCast() {
    return this instanceof CsdlCast;
  }

  /**
   * Casts the expression to a {@link CsdlCast} expression
   * @return Cast expression
   */
  public CsdlCast asCast() {
    return isCast() ? (CsdlCast) this : null;
  }

  /**
   * Returns true if the expression is a edm:Collection expression
   * @return true if the expression is a edm:Collection expression
   */
  public boolean isCollection() {
    return this instanceof CsdlCollection;
  }

  /**
   * Casts the expression to a {@link Collection} expression
   * @return Collection expression
   */
  public CsdlCollection asCollection() {
    return isCollection() ? (CsdlCollection) this : null;
  }

  /**
   * Returns true if the expression is a edm:If expression
   * @return true if the expression is a edm:If expression
   */
  public boolean isIf() {
    return this instanceof CsdlIf;
  }

  /**
   * Casts the expression to a {@link CsdlIf} expression
   * @return If expression
   */
  public CsdlIf asIf() {
    return isIf() ? (CsdlIf) this : null;
  }

  /**
   * Returns true if the expression is a edm:IsOf expression
   * @return true if the expression is a edm:IsOf expression
   */
  public boolean isIsOf() {
    return this instanceof CsdlIsOf;
  }

  /**
   * Casts the expression to a {@link CsdlIsOf} expression
   * @return IsOf expression
   */
  public CsdlIsOf asIsOf() {
    return isIsOf() ? (CsdlIsOf) this : null;
  }

  /**
   * Returns true if the expression is a edm:LabeledElement expression
   * @return true if the expression is a edm:LabeledElement expression
   */
  public boolean isLabeledElement() {
    return this instanceof CsdlLabeledElement;
  }

  /**
   * Casts the expression to a {@link CsdlLabeledElement} expression
   * @return LabeledElement expression
   */
  public CsdlLabeledElement asLabeledElement() {
    return isLabeledElement() ? (CsdlLabeledElement) this : null;
  }

  /**
   * Returns true if the expression is a edm:LabeledElementReference expression
   * @return true if the expression is a edm:LabeledElementReference expression
   */
  public boolean isLabeledElementReference() {
    return this instanceof CsdlLabeledElementReference;
  }

  /**
   * Casts the expression to a {@link CsdlLabeledElementReference} expression
   * @return LabeledElementReference expression
   */
  public CsdlLabeledElementReference asLabeledElementReference() {
    return isLabeledElementReference() ? (CsdlLabeledElementReference) this : null;
  }

  /**
   * Returns true if the expression is a edm:Null expression
   * @return true if the expression is a edm:Null expression
   */
  public boolean isNull() {
    return this instanceof CsdlNull;
  }

  /**
   * Casts the expression to a {@link CsdlNull} expression
   * @return Null expression
   */
  public CsdlNull asNull() {
    return isNull() ? (CsdlNull) this : null;
  }

  /**
   * Returns true if the expression is a edm:NavigationPropertyPath expression
   * @return true if the expression is a edm:NavigationPropertyPath expression
   */
  public boolean isNavigationPropertyPath() {
    return this instanceof CsdlNavigationPropertyPath;
  }

  /**
   * Casts the expression to a {@link CsdlNavigationPropertyPath} expression
   * @return NavigationPropertyPath expression
   */
  public CsdlNavigationPropertyPath asNavigationPropertyPath() {
    return isNavigationPropertyPath() ? (CsdlNavigationPropertyPath) this : null;
  }

  /**
   * Returns true if the expression is a edm:Path expression
   * @return true if the expression is a edm:Path expression
   */
  public boolean isPath() {
    return this instanceof CsdlPath;
  }

  /**
   * Casts the expression to a {@link CsdlPath} expression
   * @return Path expression
   */
  public CsdlPath asPath() {
    return isPath() ? (CsdlPath) this : null;
  }

  /**
   * Returns true if the expression is a edm:PropertyPath expression
   * @return true if the expression is a edm:PropertyPath expression
   */
  public boolean isPropertyPath() {
    return this instanceof CsdlPropertyPath;
  }

  /**
   * Casts the expression to a {@link CsdlPropertyPath} expression
   * @return PropertyPath expression
   */
  public CsdlPropertyPath asPropertyPath() {
    return isPropertyPath() ? (CsdlPropertyPath) this : null;
  }

  /**
   * Returns true if the expression is a edm:Record expression
   * @return true if the expression is a edm:Record expression
   */
  public boolean isRecord() {
    return this instanceof CsdlRecord;
  }

  /**
   * Casts the expression to a {@link CsdlRecord} expression
   * @return Record expression
   */
  public CsdlRecord asRecord() {
    return isRecord() ? (CsdlRecord) this : null;
  }

  /**
   * Returns true if the expression is a edm:UrlRef expression
   * @return true if the expression is a edm:UrlRef expression
   */
  public boolean isUrlRef() {
    return this instanceof CsdlUrlRef;
  }

  /**
   * Casts the expression to a {@link CsdlUrlRef} expression
   * @return UrlRef expression
   */
  public CsdlUrlRef asUrlRef() {
    return isUrlRef() ? (CsdlUrlRef) this : null;
  }
}
