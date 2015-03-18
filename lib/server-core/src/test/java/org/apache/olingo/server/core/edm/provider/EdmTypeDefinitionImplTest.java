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

import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.provider.EdmTypeDefinitionImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EdmTypeDefinitionImplTest {

  @Test
  public void typeDefOnStringNoFacets() throws Exception {
    final FullQualifiedName typeDefName = new FullQualifiedName("namespace", "name");
    final TypeDefinition providerTypeDef =
        new TypeDefinition().setName("typeDef").setUnderlyingType(new FullQualifiedName("Edm", "String"));
    final EdmTypeDefinition typeDefImpl =
        new EdmTypeDefinitionImpl(mock(EdmProviderImpl.class), typeDefName, providerTypeDef);

    assertEquals("name", typeDefImpl.getName());
    assertEquals("namespace", typeDefImpl.getNamespace());
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
  }

  @Test(expected = EdmException.class)
  public void invalidTypeResultsInEdmException() throws Exception {
    FullQualifiedName typeDefName = new FullQualifiedName("namespace", "name");
    TypeDefinition providerTypeDef =
        new TypeDefinition().setName("typeDef").setUnderlyingType(new FullQualifiedName("wrong", "wrong"));
    EdmTypeDefinitionImpl def = new EdmTypeDefinitionImpl(mock(EdmProviderImpl.class), typeDefName, providerTypeDef);
    def.getUnderlyingType();
  }

}
