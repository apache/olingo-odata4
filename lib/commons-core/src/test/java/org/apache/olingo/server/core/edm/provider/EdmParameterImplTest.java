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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.core.edm.EdmParameterImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Test;

public class EdmParameterImplTest {

  @Test
  public void initialParameter() {
    EdmParameterImpl parameter = new EdmParameterImpl(mock(Edm.class), new CsdlParameter());

    assertTrue(parameter.isNullable());
    assertFalse(parameter.isCollection());
    assertNull(parameter.getName());
    assertNull(parameter.getMapping());
    assertNull(parameter.getMaxLength());
    assertNull(parameter.getPrecision());
    assertNull(parameter.getScale());
    assertNull(parameter.getSrid());
    assertNotNull(parameter.getAnnotations());
    assertTrue(parameter.getAnnotations().isEmpty());

    try {
      parameter.getType();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Parameter null must hava a full qualified type.", e.getMessage());
    }
  }

  @Test
  public void getTypeReturnsPrimitiveType() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(CsdlEdmProvider.class));
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(EdmPrimitiveTypeKind.Binary.getFullQualifiedName());
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    final EdmType type = parameter.getType();
    assertEquals(EdmTypeKind.PRIMITIVE, type.getKind());
    assertEquals(EdmPrimitiveType.EDM_NAMESPACE, type.getNamespace());
    assertEquals(EdmPrimitiveTypeKind.Binary.toString(), type.getName());
  }

  @Test
  public void getTypeReturnsComplexType() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final FullQualifiedName complexTypeName = new FullQualifiedName("ns", "complex");
    CsdlComplexType complexTypeProvider = new CsdlComplexType();
    when(provider.getComplexType(complexTypeName)).thenReturn(complexTypeProvider);
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(complexTypeName);
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    assertFalse(parameter.isCollection());
    final EdmType type = parameter.getType();
    assertEquals(EdmTypeKind.COMPLEX, type.getKind());
    assertEquals("ns", type.getNamespace());
    assertEquals("complex", type.getName());
  }

  @Test
  public void getTypeReturnsEnumType() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final FullQualifiedName enumTypeName = new FullQualifiedName("ns", "enum");
    CsdlEnumType enumTypeProvider = new CsdlEnumType();
    when(provider.getEnumType(enumTypeName)).thenReturn(enumTypeProvider);
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(enumTypeName);
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    assertFalse(parameter.isCollection());
    final EdmType type = parameter.getType();
    assertEquals(EdmTypeKind.ENUM, type.getKind());
    assertEquals("ns", type.getNamespace());
    assertEquals("enum", type.getName());
  }

  @Test
  public void getTypeReturnsTypeDefinition() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final FullQualifiedName typeName = new FullQualifiedName("ns", "definition");
    CsdlTypeDefinition typeProvider =
        new CsdlTypeDefinition().setUnderlyingType(new FullQualifiedName("Edm", "String"));
    when(provider.getTypeDefinition(typeName)).thenReturn(typeProvider);
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(typeName);
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    final EdmType type = parameter.getType();
    assertEquals(EdmTypeKind.DEFINITION, type.getKind());
    assertEquals("ns", type.getNamespace());
    assertEquals("definition", type.getName());
  }

  @Test
  public void facets() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(CsdlEdmProvider.class));
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
    parameterProvider.setPrecision(42);
    parameterProvider.setScale(12);
    parameterProvider.setMaxLength(128);
    parameterProvider.setNullable(false);
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    assertNull(parameter.getMapping());
    assertEquals(Integer.valueOf(42), parameter.getPrecision());
    assertEquals(Integer.valueOf(12), parameter.getScale());
    assertEquals(Integer.valueOf(128), parameter.getMaxLength());
    assertFalse(parameter.isNullable());
    assertNull(parameter.getSrid());
  }

  @Test(expected = EdmException.class)
  public void getTypeWithInvalidSimpleType() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(CsdlEdmProvider.class));
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(new FullQualifiedName("Edm", "wrong"));
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    parameter.getType();
  }

  @Test(expected = EdmException.class)
  public void getTypeWithNonexistingType() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(CsdlEdmProvider.class));
    CsdlParameter parameterProvider = new CsdlParameter();
    parameterProvider.setType(new FullQualifiedName("wrong", "wrong"));
    final EdmParameter parameter = new EdmParameterImpl(edm, parameterProvider);
    parameter.getType();
  }

}
