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
package org.apache.olingo.odata4.client.core.edm.v3;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.EntityContainer;
import org.apache.olingo.odata4.client.api.edm.xml.Schema;
import org.apache.olingo.odata4.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.odata4.client.api.edm.xml.v3.Association;
import org.apache.olingo.odata4.client.api.edm.xml.v3.AssociationSet;
import org.apache.olingo.odata4.client.core.edm.xml.v3.EntityContainerImpl;
import org.apache.olingo.odata4.client.core.edm.xml.v3.SchemaImpl;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmBindingTarget;

public class EdmEntitySetProxy extends AbstractEdmBindingTarget implements EdmEntitySet {

  private final XMLMetadata xmlMetadata;

  public EdmEntitySetProxy(final Edm edm, final EdmEntityContainer container, final String name,
          final FullQualifiedName type, final XMLMetadata xmlMetadata) {

    super(edm, container, name, type);
    this.xmlMetadata = xmlMetadata;
  }

  @Override
  public EdmBindingTarget getRelatedBindingTarget(final String path) {
    final List<AssociationSet> candidateAssociationSets = new ArrayList<AssociationSet>();
    for (Schema schema : xmlMetadata.getSchemas()) {
      for (EntityContainer _entityContainer : schema.getEntityContainers()) {
        final EntityContainerImpl entityContainer = (EntityContainerImpl) _entityContainer;
        for (AssociationSet associationSet : entityContainer.getAssociationSets()) {
          if (getName().equals(associationSet.getEnds().get(0).getEntitySet())
                  || getName().equals(associationSet.getEnds().get(1).getEntitySet())) {

            candidateAssociationSets.add(associationSet);
          }
        }
      }
    }
    if (candidateAssociationSets.isEmpty()) {
      throw new EdmException("Cannot find any AssociationSet with first End: " + getName());
    }

    Schema targetSchema = null;
    String targetEntitySet = null;
    for (AssociationSet associationSet : candidateAssociationSets) {
      for (Schema schema : xmlMetadata.getSchemas()) {
        for (Association association : ((SchemaImpl) schema).getAssociations()) {
          final FullQualifiedName associationName = new FullQualifiedName(schema.getNamespace(), association.getName());
          if (associationName.getFullQualifiedNameAsString().equals(associationSet.getAssociation())
                  && (path.equals(association.getEnds().get(0).getRole())
                  || path.equals(association.getEnds().get(1).getRole()))) {

            targetSchema = schema;
            if (getName().equals(associationSet.getEnds().get(0).getEntitySet())) {
              targetEntitySet = associationSet.getEnds().get(1).getEntitySet();
            } else {
              targetEntitySet = associationSet.getEnds().get(0).getEntitySet();
            }
          }
        }
      }
    }
    if (targetSchema == null || targetEntitySet == null) {
      throw new EdmException("Cannot find Association for candidate AssociationSets and given Role");
    }

    final FullQualifiedName relatedFQN = new FullQualifiedName(targetSchema.getNamespace(), targetEntitySet);
    final EdmEntityContainer entityContainer = edm.getEntityContainer(relatedFQN);
    if (entityContainer == null) {
      throw new EdmException("Cannot find EntityContainer with name: " + relatedFQN);
    }

    return entityContainer.getEntitySet(targetEntitySet);
  }

}
