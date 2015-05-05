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

import org.apache.olingo.commons.api.edm.provider.annotation.AnnotationExpression;

public class CsdlAnnotation extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private static final long serialVersionUID = -7137313445729486860L;

  private String term;

  private String qualifier;

  private List<CsdlAnnotation> annotation = new ArrayList<CsdlAnnotation>();

  private AnnotationExpression annotationExpression;

  public AnnotationExpression getExpression() {
    return annotationExpression;
  }

  public void setExpression(final AnnotationExpression annotationExpression) {
    this.annotationExpression = annotationExpression;
  }

  public String getTerm() {
    return term;
  }

  public CsdlAnnotation setTerm(final String term) {
    this.term = term;
    return this;
  }

  public String getQualifier() {
    return qualifier;
  }

  public CsdlAnnotation setQualifier(final String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  // public List<Annotation> getAnnotation() {
  // return annotation;
  // }

  public CsdlAnnotation setAnnotations(final List<CsdlAnnotation> annotation) {
    this.annotation = annotation;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotation;
  }
}
