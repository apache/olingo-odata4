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
package org.apache.olingo.client.core.edm;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.v4.NavigationProperty;
import org.apache.olingo.client.api.edm.xml.v4.ReferentialConstraint;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmReferentialConstraint;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmNavigationProperty;
import org.apache.olingo.commons.core.edm.EdmReferentialConstraintImpl;

public class EdmNavigationPropertyImpl extends AbstractEdmNavigationProperty {

  private final NavigationProperty navigationProperty;

  private final EdmTypeInfo edmTypeInfo;
  private List<EdmReferentialConstraint> referentialConstraints;

  public EdmNavigationPropertyImpl(final Edm edm, final NavigationProperty navigationProperty) {
    super(edm, navigationProperty.getName());
    this.navigationProperty = navigationProperty;
    this.edmTypeInfo = new EdmTypeInfo.Builder().setTypeExpression(navigationProperty.getType()).build();
  }

  @Override
  protected FullQualifiedName getTypeFQN() {
    return edmTypeInfo.getFullQualifiedName();
  }

  @Override
  protected String internatGetPartner() {
    return navigationProperty.getPartner();
  }

  @Override
  public boolean isCollection() {
    return edmTypeInfo.isCollection();
  }

  @Override
  public Boolean isNullable() {
    return navigationProperty.isNullable();
  }

  @Override
  public String getReferencingPropertyName(final String referencedPropertyName) {
    final List<? extends ReferentialConstraint> referentialConstraints = navigationProperty.getReferentialConstraints();
    if (referentialConstraints != null) {
      for (ReferentialConstraint constraint : referentialConstraints) {
        if (constraint.getReferencedProperty().equals(referencedPropertyName)) {
          return constraint.getProperty();
        }
      }
    }
    return null;
  }

  @Override
  public List<EdmReferentialConstraint> getReferentialConstraints() {
    if (referentialConstraints == null) {
      final List<ReferentialConstraint> providerConstraints = navigationProperty.getReferentialConstraints();
      referentialConstraints = new ArrayList<EdmReferentialConstraint>();
      if (providerConstraints != null) {
        for (ReferentialConstraint constraint : providerConstraints) {
          referentialConstraints.add(new EdmReferentialConstraintImpl(constraint.getProperty(), constraint
              .getReferencedProperty()));
        }
      }
    }
    return referentialConstraints;
  }

}
