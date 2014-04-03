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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

public abstract class AbstractEdmEntityType extends AbstractEdmStructuredType implements EdmEntityType {

  private final boolean hasStream;

  protected EdmEntityType entityBaseType;

  private final List<String> keyPredicateNames = new ArrayList<String>();

  private final Map<String, EdmKeyPropertyRef> keyPropertyRefs = new LinkedHashMap<String, EdmKeyPropertyRef>();

  private List<EdmKeyPropertyRef> keyPropertyRefsList;

  protected AbstractEdmEntityType(final Edm edm, final FullQualifiedName typeName, final FullQualifiedName baseTypeName,
          final boolean hashStream) {

    super(edm, typeName, EdmTypeKind.ENTITY, baseTypeName);
    this.hasStream = hashStream;
  }

  protected void setEdmKeyPropertyRef(final List<EdmKeyPropertyRef> edmKey) {
    for (EdmKeyPropertyRef ref : edmKey) {
      if (ref.getAlias() == null) {
        keyPredicateNames.add(ref.getKeyPropertyName());
        keyPropertyRefs.put(ref.getKeyPropertyName(), ref);
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
  protected void checkBaseType() {
    //Current Client implementation doesn`t need this so I implemented an empty body here.
  }
}
