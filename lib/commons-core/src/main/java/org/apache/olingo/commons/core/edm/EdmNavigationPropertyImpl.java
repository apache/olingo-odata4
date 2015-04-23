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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmReferentialConstraint;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.commons.api.edm.provider.ReferentialConstraint;

public class EdmNavigationPropertyImpl extends AbstractEdmNamed implements EdmElement, EdmNavigationProperty {

  private final FullQualifiedName structuredTypeName;
  private final NavigationProperty navigationProperty;
  private List<EdmReferentialConstraint> referentialConstraints;
  private EdmEntityType typeImpl;
  private EdmNavigationProperty partnerNavigationProperty;

  public EdmNavigationPropertyImpl(
      final Edm edm, final FullQualifiedName structuredTypeName, final NavigationProperty navigationProperty) {
    super(edm, navigationProperty.getName(), navigationProperty);
    this.structuredTypeName = structuredTypeName;
    this.navigationProperty = navigationProperty;
  }

  @Override
  public boolean isCollection() {
    return navigationProperty.isCollection();
  }

  @Override
  public boolean isNullable() {
    return navigationProperty.isNullable();
  }

  @Override
  public boolean containsTarget() {
    return navigationProperty.isContainsTarget();
  }

  @Override
  public EdmEntityType getType() {
    if (typeImpl == null) {
      typeImpl = edm.getEntityType(navigationProperty.getTypeFQN());
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + navigationProperty.getTypeFQN());
      }
    }
    return typeImpl;
  }

  @Override
  public EdmNavigationProperty getPartner() {
    if (partnerNavigationProperty == null) {
      String partner = navigationProperty.getPartner();
      if (partner != null) {
        EdmStructuredType type = getType();
        EdmNavigationProperty property = null;
        final String[] split = partner.split("/");
        for (String element : split) {
          property = type.getNavigationProperty(element);
          if (property == null) {
            throw new EdmException("Cannot find navigation property with name: " + element
                + " at type " + type.getName());
          }
          type = property.getType();
        }
        partnerNavigationProperty = property;
      }
    }
    return partnerNavigationProperty;
  }

  @Override
  public String getReferencingPropertyName(final String referencedPropertyName) {
    final List<ReferentialConstraint> referentialConstraints = navigationProperty.getReferentialConstraints();
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
      final List<EdmReferentialConstraint> referentialConstraintsLocal = new ArrayList<EdmReferentialConstraint>();
      if (providerConstraints != null) {
        for (ReferentialConstraint constraint : providerConstraints) {
          referentialConstraintsLocal.add(new EdmReferentialConstraintImpl(edm, constraint));
        }
      }
      
      referentialConstraints = referentialConstraintsLocal;
    }
    return Collections.unmodifiableList(referentialConstraints);
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.NavigationProperty;
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return structuredTypeName;
  }
}
