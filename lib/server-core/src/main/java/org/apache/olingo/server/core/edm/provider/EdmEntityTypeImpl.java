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
package org.apache.olingo.server.core.edm.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmEntityType;
import org.apache.olingo.commons.core.edm.EdmStructuredTypeHelper;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.PropertyRef;

public class EdmEntityTypeImpl extends AbstractEdmEntityType {

  private final EdmStructuredTypeHelper helper;

  private EntityType entityType;

  private boolean baseTypeChecked = false;

  public static EdmEntityTypeImpl getInstance(final Edm edm, final FullQualifiedName name,
          final EntityType entityType) {

    final EdmEntityTypeImpl instance = new EdmEntityTypeImpl(edm, name, entityType);
    return instance;
  }

  private EdmEntityTypeImpl(final Edm edm, final FullQualifiedName name, final EntityType entityType) {
    super(edm, name, entityType.getBaseType(), entityType.hasStream());
    this.entityType = entityType;
    helper = new EdmStructuredTypeHelperImpl(edm, entityType);
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
      }
      if (baseType == null) {
        entityBaseType = null;

        final List<PropertyRef> key = entityType.getKey();
        if (key != null) {
          final List<EdmKeyPropertyRef> edmKey = new ArrayList<EdmKeyPropertyRef>();
          for (PropertyRef ref : key) {
            edmKey.add(new EdmKeyPropertyRefImpl(this, ref));
          }
          setEdmKeyPropertyRef(edmKey);
        }
      } else {
        entityBaseType = (EdmEntityType) baseType;
      }
      baseTypeChecked = true;
    }
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
