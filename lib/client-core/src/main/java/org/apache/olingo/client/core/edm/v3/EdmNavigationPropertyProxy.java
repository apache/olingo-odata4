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
package org.apache.olingo.client.core.edm.v3;

import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v3.Association;
import org.apache.olingo.client.api.edm.xml.v3.AssociationEnd;
import org.apache.olingo.client.api.edm.xml.v3.NavigationProperty;
import org.apache.olingo.client.api.edm.xml.v3.ReferentialConstraint;
import org.apache.olingo.client.core.edm.EdmReferentialConstraintImpl;
import org.apache.olingo.client.core.edm.xml.v3.SchemaImpl;
import org.apache.olingo.client.core.edm.xml.v4.ReferentialConstraintImpl;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmReferentialConstraint;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmNavigationProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EdmNavigationPropertyProxy extends AbstractEdmNavigationProperty {

  private final NavigationProperty navigationProperty;

  private final FullQualifiedName typeFQN;

  private final boolean isCollection;

  private final boolean isNullable;

  private EdmNavigationProperty partner;

  private final ReferentialConstraint constraint;

  private List<EdmReferentialConstraint> referentialConstraints;

  public EdmNavigationPropertyProxy(final Edm edm, final List<? extends Schema> xmlSchemas,
          final NavigationProperty navigationProperty) {

    super(edm, navigationProperty.getName());
    this.navigationProperty = navigationProperty;

    final FullQualifiedName relFQN = new FullQualifiedName(navigationProperty.getRelationship());
    Schema associationSchema = null;
    for (Schema schema : xmlSchemas) {
      if (schema.getNamespace().equals(relFQN.getNamespace())) {
        associationSchema = schema;
      }
    }
    if (!(associationSchema instanceof SchemaImpl)) {
      throw new IllegalArgumentException("Could not find schema for Association " + relFQN);
    }

    final Association association = ((SchemaImpl) associationSchema).getAssociation(relFQN.getName());
    if (association == null) {
      throw new IllegalArgumentException("Could not find Association " + relFQN.getName());
    }

    AssociationEnd thisEnd = null;
    AssociationEnd partnerEnd = null;
    for (AssociationEnd _end : association.getEnds()) {
      if (_end.getRole().equals(navigationProperty.getToRole())) {
        thisEnd = _end;
      } else {
        partnerEnd = _end;
      }
    }
    if (thisEnd == null || partnerEnd == null) {
      throw new IllegalArgumentException("Could not find AssociationEnd for role " + navigationProperty.getToRole());
    }

    typeFQN = new FullQualifiedName(thisEnd.getType());
    isCollection = "*".equals(thisEnd.getMultiplicity());
    isNullable = thisEnd.getMultiplicity().charAt(0) == '0';
    constraint = association.getReferentialConstraint();

    final EdmEntityType partnerEntity = edm.getEntityType(new FullQualifiedName(thisEnd.getType()));
    for (String navPropName : partnerEntity.getNavigationPropertyNames()) {
      final EdmNavigationPropertyProxy navProp =
              (EdmNavigationPropertyProxy) partnerEntity.getNavigationProperty(navPropName);
      if (partnerEnd.getRole().equals(navProp.getXMLNavigationProperty().getToRole())) {
        partner = navProp;
      }
    }
  }

  protected NavigationProperty getXMLNavigationProperty() {
    return navigationProperty;
  }

  @Override
  protected FullQualifiedName getTypeFQN() {
    return typeFQN;
  }

  @Override
  protected String internatGetPartner() {
    // not used
    return null;
  }

  @Override
  public EdmNavigationProperty getPartner() {
    return partner == null ? this : partner;
  }

  @Override
  public String getReferencingPropertyName(final String referencedPropertyName) {
    if (constraint != null) {
      for (int i = 0; i < constraint.getPrincipal().getPropertyRefs().size(); i++) {
        if (referencedPropertyName.equals(constraint.getPrincipal().getPropertyRefs().get(i).getName())) {
          return constraint.getDependent().getPropertyRefs().get(i).getName();
        }
      }
    }
    return null;
  }

  @Override
  public boolean isCollection() {
    return isCollection;
  }

  @Override
  public Boolean isNullable() {
    return isNullable;
  }

  @Override
  public Boolean containsTarget() {
    return navigationProperty.isContainsTarget();
  }

  @Override
  public List<EdmReferentialConstraint> getReferentialConstraints() {
    if (referentialConstraints == null) {
      referentialConstraints = new ArrayList<EdmReferentialConstraint>();
      if (constraint != null) {
        for (int i = 0; i < constraint.getPrincipal().getPropertyRefs().size(); i++) {
          final ReferentialConstraintImpl referentialConstraint = new ReferentialConstraintImpl();
          referentialConstraint.setProperty(constraint.getPrincipal().getPropertyRefs().get(i).getName());
          referentialConstraint.setReferencedProperty(constraint.getDependent().getPropertyRefs().get(i).getName());
          referentialConstraints.add(new EdmReferentialConstraintImpl(edm, referentialConstraint));
        }
      }
    }
    return referentialConstraints;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return null;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return null;
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return Collections.<EdmAnnotation>emptyList();
  }

}
