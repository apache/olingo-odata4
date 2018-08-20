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
 */package org.apache.olingo.commons.api.edm.provider.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

/**
 * The edm:LabeledElement expression assigns a name to a child expression. The value of the child expression can
 * then be reused elsewhere with an edm:LabeledElementReference (See {@link CsdlLabeledElementReference}) expression.
 */
public class CsdlLabeledElement extends CsdlDynamicExpression implements CsdlAnnotatable {

  private String name;
  private CsdlExpression value;
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  public CsdlLabeledElement setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  
  /**
   * Returns the assigned name
   * @return assigned name
   */
  public String getName() {
    return name;
  }

  public CsdlLabeledElement setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Returns the child expression
   *
   * @return child expression
   */
  public CsdlExpression getValue() {
    return value;
  }

  public CsdlLabeledElement setValue(final CsdlExpression value) {
    this.value = value;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlLabeledElement)) {
      return false;
    }
    CsdlLabeledElement csdlLabelledEle = (CsdlLabeledElement) obj;
    return (this.getName() == null ? csdlLabelledEle.getName() == null :
      this.getName().equals(csdlLabelledEle.getName()))
        && (this.getValue() == null ? csdlLabelledEle.getValue() == null :
          this.getValue().equals(csdlLabelledEle.getValue()))
        && (this.getAnnotations() == null ? csdlLabelledEle.getAnnotations() == null :
            checkAnnotations(csdlLabelledEle.getAnnotations()));
  }
  
  private boolean checkAnnotations(List<CsdlAnnotation> csdlLabelledEleAnnotations) {
    if (csdlLabelledEleAnnotations == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlLabelledEleAnnotations.size()) {
      for (int i = 0; i < this.getAnnotations().size() ; i++) {
        if (!this.getAnnotations().get(i).equals(
            csdlLabelledEleAnnotations.get(i))) {
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
    return result;
  }
}
