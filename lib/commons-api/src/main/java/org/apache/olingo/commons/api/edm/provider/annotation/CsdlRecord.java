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
 * The edm:Record expression enables a new entity type or complex type instance to be constructed.
 * A record expression contains zero or more edm:PropertyValue (See {@link CsdlRecord} )elements.
 */
public class CsdlRecord extends CsdlDynamicExpression implements CsdlAnnotatable {

  private String type;
  private List<CsdlPropertyValue> propertyValues = new ArrayList<CsdlPropertyValue>();
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  public CsdlRecord setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * Returns the entity type or complex type to be constructed.
   * @return Entity type or complex type
   */
  public String getType() {
    return type;
  }

  public CsdlRecord setType(final String type) {
    this.type = type;
    return this;
  }

  /**
   * List of edm:PropertyValues (See {@link CsdlPropertyValue}
   * @return List of edm:PropertyValues (See
   */
  public List<CsdlPropertyValue> getPropertyValues() {
    return propertyValues;
  }

  public CsdlRecord setPropertyValues(List<CsdlPropertyValue> propertyValues) {
    this.propertyValues = propertyValues;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlRecord)) {
      return false;
    }
    CsdlRecord csdlRecord = (CsdlRecord) obj;
    return (this.getType() == null ? csdlRecord.getType() == null :
      this.getType().equals(csdlRecord.getType()))
        && (this.getAnnotations() == null ? csdlRecord.getAnnotations() == null :
            checkAnnotations(csdlRecord.getAnnotations()))
        && (this.getPropertyValues() == null ? csdlRecord.getPropertyValues() == null :
            checkPropertyValues(csdlRecord.getPropertyValues()));
  }
  
  private boolean checkPropertyValues(List<CsdlPropertyValue> csdlRecordpropertyValues) {
    if (csdlRecordpropertyValues == null) {
      return false;
    }
    if (this.getPropertyValues().size() == csdlRecordpropertyValues.size()) {
      for (int i = 0; i < this.getPropertyValues().size(); i++) {
        if (!this.getPropertyValues().get(i).equals(
            csdlRecordpropertyValues.get(i))) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  private boolean checkAnnotations(List<CsdlAnnotation> csdlRecordAnnot) {
    if (csdlRecordAnnot == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlRecordAnnot.size()) {
      for (int i = 0; i < this.getAnnotations().size() ; i++) {
        if (!this.getAnnotations().get(i).equals(csdlRecordAnnot.get(i))) {
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
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((propertyValues == null) ? 0 : 
      propertyValues.hashCode());
    result = prime * result + ((annotations == null) ? 0 : 
      annotations.hashCode());
    return result;
  }
}
