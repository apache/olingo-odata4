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
 * The edm:Null expression returns an untyped null value.
 */
public class CsdlNull extends CsdlDynamicExpression implements CsdlAnnotatable {

  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  public CsdlNull setAnnotations(List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlNull)) {
      return false;
    }
    CsdlNull csdlNull = (CsdlNull) obj;
    return (this.getAnnotations() == null ? csdlNull.getAnnotations() == null :
        checkAnnotations(csdlNull.getAnnotations()));
  }
  
  private boolean checkAnnotations(List<CsdlAnnotation> csdlNullAnnot) {
    if (csdlNullAnnot == null) {
      return false;
    }
    if (this.getAnnotations().size() == csdlNullAnnot.size()) {
      for (int i = 0; i < this.getAnnotations().size() ; i++) {
        if (!this.getAnnotations().get(i).equals(csdlNullAnnot.get(i))) {
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
    result = prime * result + ((annotations == null) ? 0 : 
      annotations.hashCode());
    return result;
  }
}
