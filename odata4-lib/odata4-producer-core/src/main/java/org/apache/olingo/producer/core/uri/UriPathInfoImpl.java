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
package org.apache.olingo.producer.core.uri;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTyped;
import org.apache.olingo.producer.api.uri.KeyPredicate;
//import org.apache.olingo.api.commons.InlineCount;
//import org.apache.olingo.api.uri.NavigationPropertySegment;
//import org.apache.olingo.api.uri.NavigationSegment;
//import org.apache.olingo.api.uri.SelectItem;
//import org.apache.olingo.api.uri.expression.FilterExpression;
//import org.apache.olingo.api.uri.expression.OrderByExpression;

/**
 *  
 */
public class UriPathInfoImpl {

  public enum PathInfoType {
    /* only first */
    entitySet,
    singleton,
    actionImport,
    functioncall,
    /* not first */
    boundFunctioncall,
    boundActionImport,
    navicationProperty
    /* complexTypeFilter */// may be future
  }

  public static class PropertyItem {
    EdmTyped property;
    EdmType typeFilter;

    public PropertyItem(final EdmTyped property) {
      this.property = property;
    }
  }

  public static class ActualFunctionParameter {
    String name;
    String value;

    public ActualFunctionParameter(final String name, final String value) {
      this.name = name;
      this.value = value;
    }
  }

  public boolean isCollection;
  public PathInfoType type;

  public EdmEntityContainer entityContainer;

  public EdmType targetType;
  public EdmEntitySet targetEntityset;

  public EdmType typeBeforeKeyPredicates;
  public List<KeyPredicate> keyPredicates = null;
  public EdmType typeAfterKeyPredicates;

  public List<ActualFunctionParameter> functionParameter = new ArrayList<ActualFunctionParameter>();

  public List<PropertyItem> properties = new ArrayList<PropertyItem>();

  public PathInfoType getType() {
    return type;
  }

  public void setType(final PathInfoType type) {
    this.type = type;
  }

}
