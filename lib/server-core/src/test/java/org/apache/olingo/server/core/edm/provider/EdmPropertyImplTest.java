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
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.TypeDefinition;
import org.junit.Test;

public class EdmPropertyImplTest {

  @Test
  public void getTypeReturnsPrimitiveType() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(EdmProvider.class));
    Property propertyProvider = new Property();
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
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final FullQualifiedName complexTypeName = new FullQualifiedName("ns", "complex");
    ComplexType complexTypeProvider = new ComplexType();
    when(provider.getComplexType(complexTypeName)).thenReturn(complexTypeProvider);
    Property propertyProvider = new Property();
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
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final FullQualifiedName enumTypeName = new FullQualifiedName("ns", "enum");
    EnumType enumTypeProvider = new EnumType();
    when(provider.getEnumType(enumTypeName)).thenReturn(enumTypeProvider);
    Property propertyProvider = new Property();
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
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final FullQualifiedName typeName = new FullQualifiedName("ns", "definition");
    TypeDefinition typeProvider = new TypeDefinition().setUnderlyingType(new FullQualifiedName("Edm", "String"));
    when(provider.getTypeDefinition(typeName)).thenReturn(typeProvider);
    Property propertyProvider = new Property();
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
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final Property propertyProvider = new Property()
        .setType(new FullQualifiedName("ns", "wrong"));
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    property.getType();
    fail();
  }

  @Test(expected = EdmException.class)
  public void getTypeReturnsNoTypeKind() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    final Property propertyProvider = new Property()
        .setType(new FullQualifiedName(EdmPrimitiveType.EDM_NAMESPACE, "type"));
    final EdmProperty property = new EdmPropertyImpl(edm, propertyProvider);
    property.getType();
    fail();
  }

  @Test
  public void facets() {
    EdmProviderImpl edm = new EdmProviderImpl(mock(EdmProvider.class));
    Property propertyProvider = new Property();
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
  }
}
