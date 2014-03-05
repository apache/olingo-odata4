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
package org.apache.olingo.odata4.commons.core.edm;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmMapping;
import org.apache.olingo.odata4.commons.api.edm.EdmParameter;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;

public abstract class AbstractEdmParameter extends EdmElementImpl implements EdmParameter {

  private final FullQualifiedName paramType;

  private EdmType typeImpl;

  public AbstractEdmParameter(final Edm edm, final String name, final FullQualifiedName paramType) {
    super(edm, name);
    this.paramType = paramType;
  }

  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      if (EdmPrimitiveType.EDM_NAMESPACE.equals(paramType.getNamespace())) {
        try {
          typeImpl = EdmPrimitiveTypeKind.valueOf(paramType.getName()).getEdmPrimitiveTypeInstance();
        } catch (IllegalArgumentException e) {
          throw new EdmException("Cannot find type with name: " + paramType, e);
        }
      } else {
        typeImpl = edm.getComplexType(paramType);
        if (typeImpl == null) {
          typeImpl = edm.getEntityType(paramType);
          if (typeImpl == null) {
            typeImpl = edm.getEnumType(paramType);
            if (typeImpl == null) {
              typeImpl = edm.getTypeDefinition(paramType);
              if (typeImpl == null) {
                throw new EdmException("Cannot find type with name: " + paramType);
              }
            }
          }
        }
      }
    }
    return typeImpl;
  }

  @Override
  public abstract boolean isCollection();

  @Override
  public abstract EdmMapping getMapping();

  @Override
  public abstract Boolean isNullable();

  @Override
  public abstract Integer getMaxLength();

  @Override
  public abstract Integer getPrecision();

  @Override
  public abstract Integer getScale();

}
