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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.EdmTypeDefinitionImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.commons.core.edm.primitivetype.PrimitiveTypeBaseTest;
import org.junit.Test;

public class EdmTypeDefinitionImplTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = new EdmTypeDefinitionImpl(null,
      new FullQualifiedName("namespace", "def"),
      new CsdlTypeDefinition().setName("def")
          .setUnderlyingType(EdmString.getInstance().getFullQualifiedName())
          .setMaxLength(5)
          .setUnicode(false));

  @Test
  public void defaultType() throws Exception {
    assertEquals(String.class, instance.getDefaultType());
  }

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(instance));
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      if (kind != EdmPrimitiveTypeKind.String) {
        assertFalse(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(kind)));
      }
    }
  }

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("'Value'", instance.toUriLiteral("Value"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("Value", instance.fromUriLiteral("'Value'"));
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("text", instance.valueToString("text", null, null, null, null, null));

    expectFacetsErrorInValueToString(instance, "longtext", null, null, null, null, null);
    expectFacetsErrorInValueToString(instance, "text", null, 3, null, null, null);
    expectFacetsErrorInValueToString(instance, "schräg", null, null, null, null, null);
    expectFacetsErrorInValueToString(instance, "schräg", null, null, null, null, false);
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals("text", instance.valueOfString("text", null, null, null, null, null, String.class));

    expectFacetsErrorInValueOfString(instance, "longtext", null, null, null, null, null);
    expectFacetsErrorInValueOfString(instance, "text", null, 3, null, null, null);
    expectFacetsErrorInValueOfString(instance, "schräg", null, null, null, null, null);
    expectFacetsErrorInValueOfString(instance, "schräg", null, null, null, null, false);

    expectTypeErrorInValueOfString(instance, "text");
  }
  
  @Test
  public void typeDefOnStringNoFacets() throws Exception {
    final FullQualifiedName typeDefName = new FullQualifiedName("namespace", "name");
    final CsdlTypeDefinition providerTypeDef =
        new CsdlTypeDefinition().setName("typeDef").setUnderlyingType(new FullQualifiedName("Edm", "String"));
    final EdmTypeDefinition typeDefImpl =
        new EdmTypeDefinitionImpl(mock(EdmProviderImpl.class), typeDefName, providerTypeDef);

    assertEquals("name", typeDefImpl.getName());
    assertEquals("namespace", typeDefImpl.getNamespace());
    assertEquals(new FullQualifiedName("namespace.name"), typeDefImpl.getFullQualifiedName());
    assertEquals(String.class, typeDefImpl.getDefaultType());
    assertEquals(EdmTypeKind.DEFINITION, typeDefImpl.getKind());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String), typeDefImpl.getUnderlyingType());
    assertTrue(typeDefImpl.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String)));

    // String validation
    assertEquals("'StringValue'", typeDefImpl.toUriLiteral("StringValue"));
    assertEquals("String''Value", typeDefImpl.fromUriLiteral("'String''''Value'"));
    assertTrue(typeDefImpl.validate("text", null, null, null, null, null));
    assertEquals("text", typeDefImpl.valueToString("text", null, null, null, null, null));
    assertEquals("text", typeDefImpl.valueOfString("text", null, null, null, null, null, String.class));

    // Facets must be initial
    assertNull(typeDefImpl.getMaxLength());
    assertNull(typeDefImpl.getPrecision());
    assertNull(typeDefImpl.getScale());
    assertTrue(typeDefImpl.isUnicode());
    assertNull(typeDefImpl.getSrid());
  }

  @Test
  public void invalidTypeResultsInEdmException() throws Exception {
    FullQualifiedName typeDefName = new FullQualifiedName("namespace", "name");
    CsdlTypeDefinition providerTypeDef =
        new CsdlTypeDefinition().setName("typeDef").setUnderlyingType(new FullQualifiedName("wrong", "wrong"));
    EdmTypeDefinitionImpl def = new EdmTypeDefinitionImpl(mock(EdmProviderImpl.class), typeDefName, providerTypeDef);
    try {
      def.getUnderlyingType();
      fail("Expected an EdmException");
    } catch (EdmException e) {
      assertEquals("Invalid underlying type: wrong.wrong", e.getMessage());
    }
  }

  @Test
  public void nullTypeResultsInEdmException() throws Exception {
    FullQualifiedName typeDefName = new FullQualifiedName("namespace", "name");
    CsdlTypeDefinition providerTypeDef = new CsdlTypeDefinition().setName("typeDef");
    EdmTypeDefinitionImpl def = new EdmTypeDefinitionImpl(mock(EdmProviderImpl.class), typeDefName, providerTypeDef);
    try {
      def.getUnderlyingType();
      fail("Expected an EdmException");
    } catch (EdmException e) {
      assertEquals("Underlying Type for type definition: namespace.name must not be null.", e.getMessage());
    }
  }

}
