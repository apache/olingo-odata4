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

import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.helper.EdmMapping;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.Property;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;

public class EdmPropertyImpl extends EdmElementImpl implements EdmProperty {

  private final Property property;
  private final boolean isPrimitive;
  private EdmType propertyType;

  public EdmPropertyImpl(final EdmProviderImpl edm, final Property property) {
    super(edm, property.getName());
    this.property = property;
    isPrimitive = EdmPrimitiveType.EDM_NAMESPACE.equals(property.getType().getNamespace());
  }

  @Override
  public EdmType getType() {
    if (propertyType == null) {
      FullQualifiedName typeName = property.getType();
      if (isPrimitive) {
        EdmPrimitiveTypeKind kind = EdmPrimitiveTypeKind.valueOf(typeName.getName());
        if (kind != null) {
          propertyType = kind.getEdmPrimitiveTypeInstance();
        } else {
          throw new EdmException("Cannot find type with name: " + typeName);
        }
      } else {
        propertyType = edm.getComplexType(typeName);
        if (propertyType == null) {
          propertyType = edm.getEnumType(typeName);
          if (propertyType == null) {
            propertyType = edm.getTypeDefinition(typeName);
            if (propertyType == null) {
              throw new EdmException("Cannot find type with name: " + typeName);
            }
          }
        }
      }
    }

    return propertyType;
  }

  @Override
  public boolean isCollection() {
    return property.isCollection();
  }

  @Override
  public EdmMapping getMapping() {
    return property.getMapping();
  }

  @Override
  public String getMimeType() {
    return property.getMimeType();
  }

  @Override
  public boolean isPrimitive() {
    return isPrimitive;
  }

  @Override
  public Boolean isNullable() {
    return property.getNullable();
  }

  @Override
  public Integer getMaxLength() {
    return property.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return property.getPrecision();
  }

  @Override
  public Integer getScale() {
    return property.getScale();
  }

  @Override
  public Boolean isUnicode() {
    return property.isUnicode();
  }

  @Override
  public String getDefaultValue() {
    return property.getDefaultValue();
  }

}
