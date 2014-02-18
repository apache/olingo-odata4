/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.commons.core.edm.provider;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.provider.ReferentialConstraint;

public class EdmNavigationPropertyImpl extends EdmElementImpl implements EdmNavigationProperty {

  private final NavigationProperty navigationProperty;
  private EdmEntityType typeImpl;
  private EdmNavigationProperty partnerNavigationProperty;

  public EdmNavigationPropertyImpl(final EdmProviderImpl edm, final NavigationProperty navigationProperty) {
    super(edm, navigationProperty.getName());
    this.navigationProperty = navigationProperty;
  }

  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      typeImpl = edm.getEntityType(navigationProperty.getType());
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + navigationProperty.getType());
      }
    }
    return typeImpl;
  }

  @Override
  public boolean isCollection() {
    return navigationProperty.isCollection();
  }

  @Override
  public Boolean isNullable() {
    return navigationProperty.getNullable();
  }

  @Override
  public EdmNavigationProperty getPartner() {
    if (partnerNavigationProperty == null) {
      String partner = navigationProperty.getPartner();
      if (partner != null) {
        EdmStructuralType type = (EdmStructuralType) getType();
        EdmElement property = null;
        String[] split = partner.split("/");
        for (String element : split) {
          property = type.getProperty(element);
          if (property == null) {
            throw new EdmException("Cannot find property with name: " + element + " at type " + type.getName());
          }
          type = (EdmStructuralType) property.getType();
        }
        partnerNavigationProperty = (EdmNavigationProperty) property;
      }
    }
    return partnerNavigationProperty;
  }

  @Override
  public String getReferencingPropertyName(final String referencedPropertyName) {
    List<ReferentialConstraint> referentialConstraints = navigationProperty.getReferentialConstraints();
    if (referentialConstraints != null) {
      for (ReferentialConstraint constraint : referentialConstraints) {
        if (constraint.getReferencedProperty().equals(referencedPropertyName)) {
          return constraint.getProperty();
        }
      }
    }
    return null;
  }

}
