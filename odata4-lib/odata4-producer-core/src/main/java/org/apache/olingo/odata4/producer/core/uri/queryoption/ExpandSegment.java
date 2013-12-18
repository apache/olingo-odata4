/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.uri.queryoption;

import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExceptionVisitExpand;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandVisitor;
import org.apache.olingo.odata4.producer.api.uri.queryoption.VisitableExpand;
/*TODO fix this*/
public class ExpandSegment  implements VisitableExpand {
  private EdmType typeFilter;
  private EdmElement property;

  public ExpandSegment setProperty(EdmElement property) {
    this.property = property;
    return this;
  }

  public EdmType getType() {
    return property.getType();
  }
  
  public EdmType getTypeFilter() {
    return this.typeFilter;
  }
  

  public void setFilter(EdmStructuralType type) {
    this.typeFilter = type;
  }
  
  @Override
  public <T> T accept(ExpandVisitor<T> visitor) throws ExceptionVisitExpand {
    return null;//visitor.visitExpandSegment(property, initialType, finalType); 
  }
  
}