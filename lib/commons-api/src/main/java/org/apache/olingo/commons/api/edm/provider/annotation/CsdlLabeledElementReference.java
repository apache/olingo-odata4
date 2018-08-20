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
 * The edm:LabeledElementReference expression returns the value of an
 * edm:LabeledElement (see {@link CsdlLabeledElement}) expression.
 */
public class CsdlLabeledElementReference extends CsdlDynamicExpression {

  private String value;

  /**
   * Returns the value of the edm:LabeledElement expression
   * @return value of the edm:LabeledElement expression
   */
  public String getValue() {
    return value;
  }

  public CsdlLabeledElementReference setValue(final String value) {
    this.value = value;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlLabeledElementReference)) {
      return false;
    }
    CsdlLabeledElementReference csdlLabelledEleRef = (CsdlLabeledElementReference) obj;
    return (this.getValue() == null ? csdlLabelledEleRef.getValue() == null :
        this.getValue().equals(csdlLabelledEleRef.getValue()));
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }
}
