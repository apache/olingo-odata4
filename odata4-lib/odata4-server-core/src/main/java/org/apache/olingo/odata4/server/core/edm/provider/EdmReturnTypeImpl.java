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
package org.apache.olingo.odata4.server.core.edm.provider;

import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmReturnType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.EdmImpl;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.odata4.server.api.edm.provider.ReturnType;

public class EdmReturnTypeImpl implements EdmReturnType {

  private final EdmImpl edm;
  private final ReturnType returnType;
  private EdmType typeImpl;

  public EdmReturnTypeImpl(final EdmImpl edm, final ReturnType returnType) {
    this.edm = edm;
    this.returnType = returnType;
  }

  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      FullQualifiedName typeName = returnType.getType();
      if (EdmPrimitiveType.EDM_NAMESPACE.equals(typeName.getNamespace())) {
        try {
          typeImpl = EdmPrimitiveTypeKind.valueOf(typeName.getName()).getEdmPrimitiveTypeInstance();
        } catch (IllegalArgumentException e) {
          throw new EdmException("Cannot find type with name: " + typeName, e);
        }
      } else {
        typeImpl = edm.getComplexType(typeName);
        if (typeImpl == null) {
          typeImpl = edm.getEntityType(typeName);
          if (typeImpl == null) {
            typeImpl = edm.getEnumType(typeName);
            if (typeImpl == null) {
              typeImpl = edm.getTypeDefinition(typeName);
              if (typeImpl == null) {
                throw new EdmException("Cant find type with name: " + typeName);
              }
            }
          }
        }
      }
    }
    return typeImpl;
  }

  @Override
  public boolean isCollection() {
    return returnType.isCollection();
  }

  @Override
  public Boolean isNullable() {
    return returnType.getNullable();
  }

  @Override
  public Integer getMaxLength() {
    return returnType.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return returnType.getPrecision();
  }

  @Override
  public Integer getScale() {
    return returnType.getScale();
  }

}
