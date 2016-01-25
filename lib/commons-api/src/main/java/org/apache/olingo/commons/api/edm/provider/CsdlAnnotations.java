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

/**
 * The type Csdl annotations.
 */
public class CsdlAnnotations extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  private String target;

  private String qualifier;

  /**
   * Gets target.
   *
   * @return the target
   */
  public String getTarget() {
    return target;
  }

  /**
   * Sets target.
   *
   * @param target the target
   * @return the target
   */
  public CsdlAnnotations setTarget(final String target) {
    this.target = target;
    return this;
  }

  /**
   * Gets qualifier.
   *
   * @return the qualifier
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * Sets qualifier.
   *
   * @param qualifier the qualifier
   * @return the qualifier
   */
  public CsdlAnnotations setQualifier(final String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * Sets a list of annotations
   * @param annotations list of annotations
   * @return this instance
   */
  public CsdlAnnotations setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  
  /**
   * Gets annotation.
   *
   * @param term the term
   * @return the annotation
   */
  public CsdlAnnotation getAnnotation(final String term) {
    CsdlAnnotation result = null;
    for (CsdlAnnotation annotation : getAnnotations()) {
      if (term.equals(annotation.getTerm())) {
        result = annotation;
      }
    }
    return result;
  }
}
