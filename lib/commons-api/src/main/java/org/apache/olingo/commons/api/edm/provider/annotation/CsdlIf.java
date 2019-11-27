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

/**
 * Represents a edm:If expression
 */
public class CsdlIf extends CsdlDynamicExpression implements CsdlAnnotatable {

  private CsdlExpression guard;
  private CsdlExpression _then;
  private CsdlExpression _else;
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  public CsdlIf setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * Returns the first expression of the edm:If expression.
   * This expression represents the condition of the if expression
   *
   * @return First expression of the if expression
   */
  public CsdlExpression getGuard() {
    return guard;
  }

  public CsdlIf setGuard(final CsdlExpression guard) {
    this.guard = guard;
    return this;
  }

  /**
   * Return the second expression of the edm:If expression.
   * If the condition of the condition is evaluated to true,
   * this expression as to be executed.
   *
   * @return Second Expression of the edm:If expression
   */
  public CsdlExpression getThen() {
    return _then;
  }

  public CsdlIf setThen(final CsdlExpression _then) {
    this._then = _then;
    return this;
  }

  /**
   * Return the third expression of the edm:If expression.
   * If the condition of the condition is evaluated to false,
   * this expression as to be executed.
   *
   * @return Third Expression of the edm:If expression
   */
  public CsdlExpression getElse() {
    return _else;
  }

  public CsdlIf setElse(final CsdlExpression _else) {
    this._else = _else;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlIf)) {
      return false;
    }
    CsdlIf csdlIf = (CsdlIf) obj;
    return (this.getGuard() == null ? csdlIf.getGuard() == null :
      this.getGuard().equals(csdlIf.getGuard()))
        && (this.getThen() == null ? csdlIf.getThen() == null :
          this.getThen().equals(csdlIf.getThen()))
        && (this.getElse() == null ? csdlIf.getElse() == null :
          this.getElse().equals(csdlIf.getElse()))
        && (this.getAnnotations() == null ? csdlIf.getAnnotations() == null :
            checkAnnotations(csdlIf.getAnnotations()));
  }
  
  private boolean checkAnnotations(List<CsdlAnnotation> csdlIfAnnotations) {
    if (csdlIfAnnotations == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlIfAnnotations.size()) {
      for (int i = 0; i < this.getAnnotations().size(); i++) {
        if (!this.getAnnotations().get(i).equals(csdlIfAnnotations.get(i))) {
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
    result = prime * result + ((guard == null) ? 0 : guard.hashCode());
    result = prime * result + ((_then == null) ? 0 : _then.hashCode());
    result = prime * result + ((_else == null) ? 0 : _else.hashCode());
    result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
    return result;
  }
}
