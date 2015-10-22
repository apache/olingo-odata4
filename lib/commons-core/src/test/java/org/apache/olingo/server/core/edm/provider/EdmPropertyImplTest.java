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
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.core.edm.EdmPropertyImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Test;

public class EdmPropertyImplTest {

  @Test
  public void initialProperty() {
    EdmProperty property = new EdmPropertyImpl(mock(Edm.class), new CsdlProperty());

    assertTrue(property.isNullable());
    assertFalse(property.isCollection());
    assertNull(property.getName());
    assertNull(property.getMapping());
    assertNull(property.getMaxLength());
    assertNull(property.getPrecision());
    assertNull(property.getScale());
    assertNull(property.getSrid());
    assertNotNull(property.getAnnotations());
    assertTrue(property.getAnnotations().isEmpty());

    try {
      property.getType();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Property null must hava a full qualified type.", e.getMessage());
    }
   
    try {
      property.isPrimitive();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Property null must hava a full qualified type.", e.getMessage());
    }
  }
  
  @Test
  public void getTypeReturnsPrimitiveType() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(CsdlEdmProvider.class));
    CsdlProperty propertyProvider = new CsdlProperty();
    propertyProvider.setType(EdmPrimitiveTypeKind.Binary.getFullQualifiedName());
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    assertTrue(property.isPrimitive());
    final EdmType type = property.getType();
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
    CsdlProperty propertyProvider = new CsdlProperty();
    propertyProvider.setType(complexTypeName);
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    assertFalse(property.isCollection());
    assertFalse(property.isPrimitive());
    final EdmType type = property.getType();
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
    CsdlProperty propertyProvider = new CsdlProperty();
    propertyProvider.setType(enumTypeName);
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    assertFalse(property.isCollection());
    assertFalse(property.isPrimitive());
    final EdmType type = property.getType();
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
    CsdlProperty propertyProvider = new CsdlProperty();
    propertyProvider.setType(typeName);
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    assertFalse(property.isPrimitive());
    final EdmType type = property.getType();
    assertEquals(EdmTypeKind.DEFINITION, type.getKind());
    assertEquals("ns", type.getNamespace());
    assertEquals("definition", type.getName());
  }

  @Test(expected = EdmException.class)
  public void getTypeReturnsWrongType() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final CsdlProperty propertyProvider = new CsdlProperty()
        .setType(new FullQualifiedName("ns", "wrong"));
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    property.getType();
    fail();
  }

  @Test(expected = EdmException.class)
  public void getTypeReturnsNoTypeKind() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final CsdlProperty propertyProvider = new CsdlProperty()
        .setType(new FullQualifiedName(EdmPrimitiveType.EDM_NAMESPACE, "type"));
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    property.getType();
    fail();
  }

  @Test
  public void facets() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(CsdlEdmProvider.class));
    CsdlProperty propertyProvider = new CsdlProperty();
    propertyProvider.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
    propertyProvider.setPrecision(42);
    propertyProvider.setScale(12);
    propertyProvider.setMaxLength(128);
    propertyProvider.setUnicode(true);
    propertyProvider.setNullable(false);
    propertyProvider.setDefaultValue("x");
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    assertTrue(property.isPrimitive());
    assertNull(property.getMapping());
    assertNull(property.getMimeType());
    assertEquals(Integer.valueOf(42), property.getPrecision());
    assertEquals(Integer.valueOf(12), property.getScale());
    assertEquals(Integer.valueOf(128), property.getMaxLength());
    assertTrue(property.isUnicode());
    assertFalse(property.isNullable());
    assertEquals("x", property.getDefaultValue());
    assertNull(property.getSrid());
  }
}
