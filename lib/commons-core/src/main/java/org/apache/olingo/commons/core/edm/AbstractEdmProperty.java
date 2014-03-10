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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;

public abstract class AbstractEdmProperty extends EdmElementImpl implements EdmProperty {

  private EdmType propertyType;

  public AbstractEdmProperty(final Edm edm, final String name) {
    super(edm, name);
  }

  protected abstract FullQualifiedName getTypeFQN();

  @Override
  public boolean isPrimitive() {
    return EdmPrimitiveType.EDM_NAMESPACE.equals(getTypeFQN().getNamespace());
  }

  @Override
  public EdmType getType() {
    if (propertyType == null) {
      final FullQualifiedName typeName = getTypeFQN();
      if (isPrimitive()) {
        try {
          propertyType = EdmPrimitiveTypeKind.valueOf(typeName.getName()).getEdmPrimitiveTypeInstance();
        } catch (IllegalArgumentException e) {
          throw new EdmException("Cannot find type with name: " + typeName, e);
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
}
