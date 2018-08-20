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

/**
 * The edm:Collection expression enables a value to be obtained from zero or more child expressions.
 * The value calculated by the collection expression is the collection of the values calculated
 * by each of the child expressions.
 */
public class CsdlCollection extends CsdlDynamicExpression {

  private List<CsdlExpression> items = new ArrayList<CsdlExpression>();

  /**
   * Returns a list of child expression
   * @return List of child expression
   */
  public List<CsdlExpression> getItems() {
    return items;
  }

  /**
   * Returns a list of child expression
   * @return List of child expression
   */
  public CsdlCollection setItems(List<CsdlExpression> items) {
    this.items = items;
    return this;
  }
  
  @Override
  public boolean equals (Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CsdlCollection)) {
      return false;
    }
    CsdlCollection annotColl = (CsdlCollection) obj;
    return (this.getItems() == null ? annotColl.getItems() == null :
      checkItems(annotColl.getItems()));
  }
  
  private boolean checkItems(List<CsdlExpression> annotCollItems) {
    if (annotCollItems == null) {
      return false;
    }
    if (this.getItems().size() == annotCollItems.size()) {
      for (int i = 0; i < this.getItems().size(); i++) {
        if (!this.getItems().get(i).equals(annotCollItems.get(i))) {
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
    result = prime * result + ((items == null) ? 0 : items.hashCode());
    return result;
  }
}
