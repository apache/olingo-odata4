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
package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.EntityType;
import org.apache.olingo.commons.api.edm.provider.PropertyRef;

public class EdmEntityTypeImpl extends EdmStructuredTypeImpl implements EdmEntityType {

  private final EdmStructuredTypeHelper helper;
  private EntityType entityType;
  private boolean baseTypeChecked = false;
  private EdmAnnotationHelper annotationHelper;
  private final boolean hasStream;
  protected EdmEntityType entityBaseType;
  private final List<String> keyPredicateNames = new ArrayList<String>();
  private final Map<String, EdmKeyPropertyRef> keyPropertyRefs = new LinkedHashMap<String, EdmKeyPropertyRef>();
  private List<EdmKeyPropertyRef> keyPropertyRefsList;

  public static EdmEntityTypeImpl getInstance(final Edm edm, final FullQualifiedName name,
      final EntityType entityType) {

    return new EdmEntityTypeImpl(edm, name, entityType);
  }

  private EdmEntityTypeImpl(final Edm edm, final FullQualifiedName name, final EntityType entityType) {
    super(edm, name, EdmTypeKind.ENTITY, entityType.getBaseTypeFQN());
    this.entityType = entityType;
    helper = new EdmStructuredTypeHelperImpl(edm, name, entityType);
    hasStream = entityType.hasStream();
  }

  @Override
  protected Map<String, EdmProperty> getProperties() {
    return helper.getProperties();
  }

  @Override
  protected Map<String, EdmNavigationProperty> getNavigationProperties() {
    return helper.getNavigationProperties();
  }

  @Override
  protected void checkBaseType() {
    if (!baseTypeChecked) {
      if (baseTypeName != null) {
        baseType = buildBaseType(baseTypeName);
        entityBaseType = (EdmEntityType) baseType;
      }
      if (baseType == null
          || (baseType.isAbstract() && ((EdmEntityType) baseType).getKeyPropertyRefs().size() == 0)) {
        final List<PropertyRef> key = entityType.getKey();
        if (key != null) {
          final List<EdmKeyPropertyRef> edmKey = new ArrayList<EdmKeyPropertyRef>();
          for (PropertyRef ref : key) {
            edmKey.add(new EdmKeyPropertyRefImpl(this, ref));
          }
          setEdmKeyPropertyRef(edmKey);
        }
      }
      baseTypeChecked = true;
    }
  }

  protected void setEdmKeyPropertyRef(final List<EdmKeyPropertyRef> edmKey) {
    for (EdmKeyPropertyRef ref : edmKey) {
      if (ref.getAlias() == null) {
        keyPredicateNames.add(ref.getName());
        keyPropertyRefs.put(ref.getName(), ref);
      } else {
        keyPredicateNames.add(ref.getAlias());
        keyPropertyRefs.put(ref.getAlias(), ref);
      }
    }
  }

  @Override
  protected EdmStructuredType buildBaseType(final FullQualifiedName baseTypeName) {
    EdmEntityType baseType = null;
    if (baseTypeName != null) {
      baseType = edm.getEntityType(baseTypeName);
      if (baseType == null) {
        throw new EdmException("Cannot find base type with name: " + baseTypeName + " for entity type: " + getName());
      }
    }
    return baseType;
  }

  @Override
  public EdmEntityType getBaseType() {
    checkBaseType();
    return entityBaseType;
  }

  @Override
  public List<String> getKeyPredicateNames() {
    checkBaseType();
    if (keyPredicateNames.isEmpty() && baseType != null) {
      return entityBaseType.getKeyPredicateNames();
    }
    return keyPredicateNames;
  }

  @Override
  public List<EdmKeyPropertyRef> getKeyPropertyRefs() {
    checkBaseType();
    if (keyPropertyRefsList == null) {
      keyPropertyRefsList = new ArrayList<EdmKeyPropertyRef>(keyPropertyRefs.values());
    }
    if (keyPropertyRefsList.isEmpty() && entityBaseType != null) {
      return entityBaseType.getKeyPropertyRefs();
    }
    return keyPropertyRefsList;
  }

  @Override
  public EdmKeyPropertyRef getKeyPropertyRef(final String keyPredicateName) {
    checkBaseType();
    final EdmKeyPropertyRef edmKeyPropertyRef = keyPropertyRefs.get(keyPredicateName);
    if (edmKeyPropertyRef == null && entityBaseType != null) {
      return entityBaseType.getKeyPropertyRef(keyPredicateName);
    }
    return edmKeyPropertyRef;
  }

  @Override
  public boolean hasStream() {
    return hasStream;
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.EntityType;
  }
  
  @Override
  public boolean isOpenType() {
    return helper.isOpenType();
  }

  @Override
  public boolean isAbstract() {
    return helper.isAbstract();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return annotationHelper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return annotationHelper.getAnnotations();
  }
}
