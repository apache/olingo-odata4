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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

public abstract class AbstractEdmComplexType extends AbstractEdmStructuredType implements EdmComplexType {

  public AbstractEdmComplexType(
      final Edm edm,
      final FullQualifiedName typeName,
      final FullQualifiedName baseTypeName) {
    super(edm, typeName, EdmTypeKind.COMPLEX, baseTypeName);
  }

  @Override
  protected EdmStructuredType buildBaseType(final FullQualifiedName baseTypeName) {
    EdmComplexType baseType = null;
    if (baseTypeName != null) {
      baseType = edm.getComplexType(baseTypeName);
      if (baseType == null) {
        throw new EdmException("Can't find base type with name: " + baseTypeName + " for complex type: "
            + getName());
      }
    }
    return baseType;
  }

  @Override
  public EdmComplexType getBaseType() {
    checkBaseType();
    return (EdmComplexType) baseType;
  }

  @Override
  protected void checkBaseType() {
    if (baseTypeName != null && baseType == null) {
      baseType = buildBaseType(baseTypeName);
    }
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.ComplexType;
  }
}
