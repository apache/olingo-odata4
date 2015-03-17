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

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;
import org.apache.olingo.commons.core.edm.AbstractEdmTypeDefinition;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmTypeDefinitionImpl extends AbstractEdmTypeDefinition {

  private TypeDefinition typeDefinition;

  private EdmPrimitiveType edmPrimitiveTypeInstance;

  public EdmTypeDefinitionImpl(final Edm edm, final FullQualifiedName typeDefinitionName,
      final TypeDefinition typeDefinition) {

    super(edm, typeDefinitionName);
    this.typeDefinition = typeDefinition;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    if (edmPrimitiveTypeInstance == null) {
      try {
        edmPrimitiveTypeInstance = EdmPrimitiveTypeFactory.getInstance(
            EdmPrimitiveTypeKind.valueOfFQN(typeDefinition.getUnderlyingType()));
      } catch (IllegalArgumentException e) {
        throw new EdmException("Invalid underlying type: " + typeDefinition.getUnderlyingType(), e);
      }
    }
    return edmPrimitiveTypeInstance;
  }

  @Override
  public Integer getMaxLength() {
    return typeDefinition.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return typeDefinition.getPrecision();
  }

  @Override
  public Integer getScale() {
    return typeDefinition.getScale();
  }

  @Override
  public SRID getSrid() {
    return null; // TODO: provide implementation
  }

  @Override
  public Boolean isUnicode() {
    return typeDefinition.isUnicode();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
