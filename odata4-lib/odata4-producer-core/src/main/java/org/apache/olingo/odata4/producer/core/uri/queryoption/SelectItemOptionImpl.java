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

//TODO rework this 
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriResourceProperty;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectItem;

public class SelectItemOptionImpl  implements SelectItem{

  private Edm edm;
  private EdmType finalType;
  private SelectSegment lastSegment = null;

  // only one of these must me filled
  private List<SelectSegment> segments = new ArrayList<SelectSegment>();
  private EdmAction action;
  private EdmFunction function;
  private boolean isStar;
  private FullQualifiedName addOperationsInSchema;

  public class SelectSegment {
    private EdmElement property; // ia EdmProperty or EdmNavigationProperty
    private EdmType initialType;
    private EdmType typeCast;
    private EdmType finalType;
    

    public SelectSegment setProperty(EdmElement property) {
      this.property = property;
      this.initialType = property.getType();
      this.finalType = initialType;
      return this;
    }

    public EdmType getType() {
      return finalType;
    }
    
    public EdmType getTypeCast() {
      return typeCast;
    }
    
    public EdmElement getProperty() {
      return property;
    }

    public void addCast(EdmStructuralType type) {
      this.typeCast = type;
      this.finalType = type;

    }
  }

  public EdmType getType() {
    return finalType;
  }
  
  public SelectItemOptionImpl setEdm(Edm edm) {
    this.edm = edm;
    return this;
  }

  /**
   * Sets the start type used for the type validation. For example this may be the type of the
   * last resource path segment.
   * @param startType
   * @return
   */
  public SelectItemOptionImpl setStartType(EdmType startType) {
    this.finalType = startType;
    return this;
  }

  public SelectItemOptionImpl addProperty(String propertyName) {

    if (!(finalType instanceof EdmStructuralType)) {
      // TODO error
      return this;
    }

    EdmStructuralType structType = (EdmStructuralType) finalType;
    EdmElement property = (EdmElement) structType.getProperty(propertyName);
    if (property == null) {
      // TODO error
      return this;
    }

    // create new segment
    this.lastSegment = new SelectSegment().setProperty(property);
    this.segments.add(this.lastSegment);

    this.finalType = lastSegment.getType();
    return this;
  }

  public void addStar() {
    // TODO add checks
    isStar = true;
  }
  
  public void addAllOperationsInSchema(FullQualifiedName addOperationsInSchema) {
    // TODO add checks
    this.addOperationsInSchema =addOperationsInSchema;
  }

  public SelectItemOptionImpl addQualifiedThing(FullQualifiedName fullName) {
    // TODO add checks
    if (finalType instanceof EdmEntityType) {
      EdmEntityType et = edm.getEntityType(fullName);
      if (((EdmStructuralType) finalType).compatibleTo(et)) {
        this.lastSegment.addCast(et);
        this.finalType = this.lastSegment.getType();
        return this;
      }
    }

    if (finalType instanceof EdmComplexType) {
      EdmComplexType ct = edm.getComplexType(fullName);
      if (ct != null) {
        if (((EdmStructuralType) finalType).compatibleTo(ct)) {
          this.lastSegment.addCast(ct);
          this.finalType = this.lastSegment.getType();
          return this;
        }
      }
    }

    FullQualifiedName finalTypeName = new FullQualifiedName(finalType.getNamespace(), finalType.getName());

    // check for action
    EdmAction action = edm.getAction(fullName, finalTypeName, null);
    // TODO verify that null ignores if it is a collection

    if (action != null) {
      if (lastSegment != null) {
        // TODO throw error action not usable behind property cast
      }
      this.action = action;
    }

    // check for function
    EdmFunction function = edm.getFunction(fullName, finalTypeName, null, null);
    // TODO verify that null ignores if it is a collection
    // TODO verify that the second for parameters null ignores the parameters

    if (function != null) {
      if (lastSegment != null) {
        // TODO throw error action not usable behind property cast
      }
      this.function = function;
    }

    return null;

  }

  @Override
  public boolean isStar() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isAllOperationsInSchema() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getNameSpace() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EdmEntityType getEntityTypeCast() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EdmAction getAction() {
    return action;
  }

  @Override
  public EdmFunction getFunction() {
    return function;
  }

  @Override
  public List<UriResourceProperty> getPropertyChainList() {
    // TODO Auto-generated method stub
    return null;
  }
}
