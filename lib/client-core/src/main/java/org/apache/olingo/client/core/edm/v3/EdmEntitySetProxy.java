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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v3.Association;
import org.apache.olingo.client.api.edm.xml.v3.AssociationSet;
import org.apache.olingo.client.core.edm.xml.v3.EntityContainerImpl;
import org.apache.olingo.client.core.edm.xml.v3.SchemaImpl;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmBindingTarget;

public class EdmEntitySetProxy extends AbstractEdmBindingTarget implements EdmEntitySet {

  private final List<? extends Schema> xmlSchemas;

  public EdmEntitySetProxy(final Edm edm, final EdmEntityContainer container, final String name,
          final FullQualifiedName type, final List<? extends Schema> xmlSchemas) {

    super(edm, container, name, type);
    this.xmlSchemas = xmlSchemas;
  }

  @Override
  public EdmBindingTarget getRelatedBindingTarget(final String path) {
    final Map<AssociationSet, FullQualifiedName> candidateAssociationSets =
            new HashMap<AssociationSet, FullQualifiedName>();
    for (Schema schema : xmlSchemas) {
      for (EntityContainer _entityContainer : schema.getEntityContainers()) {
        final EntityContainerImpl entityContainer = (EntityContainerImpl) _entityContainer;
        for (AssociationSet associationSet : entityContainer.getAssociationSets()) {
          if (getName().equals(associationSet.getEnds().get(0).getEntitySet())
                  || getName().equals(associationSet.getEnds().get(1).getEntitySet())) {

            candidateAssociationSets.put(associationSet,
                    new FullQualifiedName(schema.getNamespace(), entityContainer.getName()));
          }
        }
      }
    }
    if (candidateAssociationSets.isEmpty()) {
      throw new EdmException("Cannot find any AssociationSet with first End: " + getName());
    }

    FullQualifiedName targetEntityContainer = null;
    String targetEntitySet = null;
    for (Map.Entry<AssociationSet, FullQualifiedName> entry : candidateAssociationSets.entrySet()) {
      for (Schema schema : xmlSchemas) {
        for (Association association : ((SchemaImpl) schema).getAssociations()) {
          final FullQualifiedName associationName = new FullQualifiedName(schema.getNamespace(), association.getName());
          if (associationName.getFullQualifiedNameAsString().equals(entry.getKey().getAssociation())
                  && (path.equals(association.getEnds().get(0).getRole())
                  || path.equals(association.getEnds().get(1).getRole()))) {

            targetEntityContainer = entry.getValue();
            if (getName().equals(entry.getKey().getEnds().get(0).getEntitySet())) {
              targetEntitySet = entry.getKey().getEnds().get(1).getEntitySet();
            } else {
              targetEntitySet = entry.getKey().getEnds().get(0).getEntitySet();
            }
          }
        }
      }
    }
    if (targetEntityContainer == null || targetEntitySet == null) {
      throw new EdmException("Cannot find Association for candidate AssociationSets and given Role");
    }

    final EdmEntityContainer entityContainer = edm.getEntityContainer(targetEntityContainer);
    if (entityContainer == null) {
      throw new EdmException("Cannot find EntityContainer with name: " + targetEntityContainer);
    }

    return entityContainer.getEntitySet(targetEntitySet);
  }

  @Override
  public boolean isIncludeInServiceDocument() {
    //V3 states that all entity sets are included in the service document
    return true;
  }

  @Override
  public List<EdmNavigationPropertyBinding> getNavigationPropertyBindings() {
    // There are no navigation property bindings in V3 so we will deliver an empty list
    return Collections.emptyList();
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.EntitySet;
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
