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

/**
 * Represents an annotation path
 */
public class CsdlAnnotationPath extends CsdlDynamicExpression {

  private String value;

  public CsdlAnnotationPath setValue(final String value) {
    this.value = value;
    return this;
  }

  /**
   * Value of the path
   * @return value of the path
   */
  public String getValue() {
    return value;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlAnnotationPath)) {
      return false;
    }
    CsdlAnnotationPath csdlAnnotPath = (CsdlAnnotationPath) obj;
     
    return this.getValue() == null ? csdlAnnotPath.getValue() == null : 
      this.getValue().equals(csdlAnnotPath.getValue());
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }
}
