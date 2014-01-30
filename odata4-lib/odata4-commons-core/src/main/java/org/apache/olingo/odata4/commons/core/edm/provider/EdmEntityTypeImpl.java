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
package org.apache.olingo.odata4.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.odata4.commons.api.edm.EdmStructuralType;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.PropertyRef;

public class EdmEntityTypeImpl extends EdmStructuralTypeImpl implements EdmEntityType {

  private EntityType entityType;
  private final List<String> keyPredicateNames = new ArrayList<String>();
  private final HashMap<String, EdmKeyPropertyRef> keyPropertyRefs = new HashMap<String, EdmKeyPropertyRef>();
  private final EdmEntityType entityBaseType;
  private ArrayList<EdmKeyPropertyRef> keyPropertyRefsList;

  public EdmEntityTypeImpl(final EdmProviderImpl edm, final FullQualifiedName name, final EntityType entityType) {
    super(edm, name, entityType, EdmTypeKind.ENTITY);
    this.entityType = entityType;
    if (baseType == null) {
      entityBaseType = null;
      if (entityType.getKey() == null && !entityType.isAbstract()) {
        throw new EdmException("Non-Abstract entity types must define a key.");
      }
      for (PropertyRef ref : entityType.getKey()) {
        EdmKeyPropertyRef edmKeyRef = new EdmKeyPropertyRefImpl(this, ref);
        if (ref.getAlias() != null) {
          keyPredicateNames.add(ref.getAlias());
          keyPropertyRefs.put(ref.getAlias(), edmKeyRef);
        } else {
          keyPredicateNames.add(ref.getPropertyName());
          keyPropertyRefs.put(ref.getPropertyName(), edmKeyRef);
        }
      }
    } else {
      entityBaseType = (EdmEntityType) baseType;
    }

  }

  @Override
  public boolean hasStream() {
    return entityType.hasStream();
  }

  @Override
  public EdmEntityType getBaseType() {
    return entityBaseType;
  }

  @Override
  public List<String> getKeyPredicateNames() {
    if (baseType != null) {
      return entityBaseType.getKeyPredicateNames();
    } else {
      return keyPredicateNames;
    }
  }

  @Override
  public List<EdmKeyPropertyRef> getKeyPropertyRefs() {
    if (baseType != null) {
      return entityBaseType.getKeyPropertyRefs();
    } else {
      if (keyPropertyRefsList == null) {
        keyPropertyRefsList = new ArrayList<EdmKeyPropertyRef>(keyPropertyRefs.values());
      }
      return keyPropertyRefsList;
    }
  }

  @Override
  public EdmKeyPropertyRef getKeyPropertyRef(final String keyPredicateName) {
    if (baseType != null) {
      return entityBaseType.getKeyPropertyRef(keyPredicateName);
    } else {
      return keyPropertyRefs.get(keyPredicateName);
    }
  }

  @Override
  protected EdmStructuralType buildBaseType(final FullQualifiedName baseTypeName) {
    EdmEntityType baseType = null;
    if (baseTypeName != null) {
      baseType = edm.getEntityType(baseTypeName);
      if (baseType == null) {
        throw new EdmException("Cannot find base type with name: " + baseTypeName + " for entity type: " + getName());
      }
    }
    return baseType;
  }

}
