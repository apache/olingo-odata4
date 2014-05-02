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
package org.apache.olingo.client.core.edm;

import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.edm.xml.EntityType;
import org.apache.olingo.client.api.edm.xml.PropertyRef;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmEntityType;
import org.apache.olingo.commons.core.edm.EdmStructuredTypeHelper;

public class EdmEntityTypeImpl extends AbstractEdmEntityType {

  private final EdmStructuredTypeHelper helper;

  public static EdmEntityTypeImpl getInstance(final Edm edm, final FullQualifiedName fqn, 
          final List<? extends Schema> xmlSchemas, final EntityType entityType) {
    
    final FullQualifiedName baseTypeName = entityType.getBaseType() == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(entityType.getBaseType()).build().getFullQualifiedName();
    final EdmEntityTypeImpl instance = new EdmEntityTypeImpl(edm, fqn, baseTypeName, xmlSchemas, entityType);
    instance.baseType = instance.buildBaseType(baseTypeName);

    if (instance.baseType == null) {
      instance.entityBaseType = null;

      final List<EdmKeyPropertyRef> edmKey = new ArrayList<EdmKeyPropertyRef>(
              entityType.getKey().getPropertyRefs().size());
      for (PropertyRef ref : entityType.getKey().getPropertyRefs()) {
        edmKey.add(new EdmKeyPropertyRefImpl(instance, ref));
      }
      instance.setEdmKeyPropertyRef(edmKey);
    } else {
      instance.entityBaseType = (EdmEntityType) instance.baseType;
    }

    return instance;
  }

  private EdmEntityTypeImpl(final Edm edm, final FullQualifiedName fqn, final FullQualifiedName baseTypeName,
          final List<? extends Schema> xmlSchemas, final EntityType entityType) {

    super(edm, fqn, baseTypeName, entityType.isHasStream());
    this.helper = new EdmStructuredTypeHelperImpl(edm, xmlSchemas, entityType);
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
  public boolean isOpenType() {
    return helper.isOpenType();
  }

  @Override
  public boolean isAbstract() {
    return helper.isAbstract();
  }
}
