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
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.producer.api.uri.UriResourceProperty;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectItem;

public class SelectItemOptionImpl implements SelectItem {

  private SelectSegmentImpl lastSegment = null;

  // only one of these must me filled
  private List<SelectSegmentImpl> segments = new ArrayList<SelectSegmentImpl>();

  public EdmType getType() {
    if (lastSegment != null) {
      EdmType type = lastSegment.getTypeCast();
      if (type != null) {
        return type;
      }
      return lastSegment.getType();
    }
    return null;
  }


  public void addSegment(SelectSegmentImpl newSegment) {
    segments.add(newSegment);
    lastSegment = newSegment;

  }

  public void addStar() {}

  public void addAllOperationsInSchema(final FullQualifiedName addOperationsInSchema) {}



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
  public List<UriResourceProperty> getPropertyChainList() {
    // TODO Auto-generated method stub
    return null;
  }


  public SelectSegmentImpl getLastSegment() {
    return lastSegment;
  }


  public void setEntityTypeCast(EdmEntityType et) {
    // TODO Auto-generated method stub
    
  }

}
