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

import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmItem;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotatable;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

/**
 * The edm:PropertyValue element supplies a value to a property on the type instantiated by an 
 * edm:Record expression (See {@link org.apache.olingo.commons.api.edm.annotation.EdmRecord}). 
 * The value is obtained by evaluating an expression.
 */
public class CsdlPropertyValue extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private String property;
  private CsdlExpression value;
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  public CsdlPropertyValue setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  
  /**
   * Property name
   * @return Property name
   */
  public String getProperty() {
    return property;
  }

  public CsdlPropertyValue setProperty(final String property) {
    this.property = property;
    return this;
  }

  /**
   * Evaluated value of the expression (property value)
   * @return evaluated value of the expression
   */
  public CsdlExpression getValue() {
    return value;
  }

  public CsdlPropertyValue setValue(final CsdlExpression value) {
    this.value = value;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlPropertyValue)) {
      return false;
    }
    CsdlPropertyValue csdlPropertyValue = (CsdlPropertyValue) obj;
    
    return (this.getProperty() == null ? csdlPropertyValue.getProperty() == null :
      this.getProperty().equalsIgnoreCase(csdlPropertyValue.getProperty()))
        && (this.getValue() == null ? csdlPropertyValue.getValue() == null :
          this.getValue().equals(csdlPropertyValue.getValue()))
        && (this.getAnnotations() == null ? csdlPropertyValue.getAnnotations() == null :
            checkAnnotations(csdlPropertyValue.getAnnotations()));
  }
  
  private boolean checkAnnotations(List<CsdlAnnotation> csdlPropertyValueAnnot) {
    if (csdlPropertyValueAnnot == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlPropertyValueAnnot.size()) {
      for (int i = 0; i < this.getAnnotations().size() ; i++) {
        if (!this.getAnnotations().get(i).equals(
            csdlPropertyValueAnnot.get(i))) {
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
    result = prime * result + ((property == null) ? 0 : property.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result = prime * result + ((annotations == null) ? 0 : 
      annotations.hashCode());
    return result;
  }
}
