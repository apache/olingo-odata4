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
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.annotation.CsdlExpression;

/**
 * Represents a CSDL annotation
 */
public class CsdlAnnotation extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private String term;

  private String qualifier;

  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  private CsdlExpression annotationExpression;

  /**
   * Returns the annotated expression
   * @return expression annotated expression
   */
  public CsdlExpression getExpression() {
    return annotationExpression;
  }

  /**
   * Sets the annotated expression
   * @param annotationExpression annotated expression
   */
  public CsdlAnnotation setExpression(final CsdlExpression annotationExpression) {
    this.annotationExpression = annotationExpression;
    return this;
  }

  /**
   * Returns the annotated term
   * @return Term term
   */
  public String getTerm() {
    return term;
  }

  /**
   * Sets the annotated expression
   * @param term term
   * @return this instance
   */
  public CsdlAnnotation setTerm(final String term) {
    this.term = term;
    return this;
  }

  /**
   * Returns the annotated qualifier
   * @return annotated qualifier
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * Sets the annotated qualifier
   * @param qualifier annotated qualifier
   * @return this instance
   */
  public CsdlAnnotation setQualifier(final String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  /**
   * Sets a list of annotations
   * @param annotations list of annotations
   * @return this instance
   */
  public CsdlAnnotation setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
}
