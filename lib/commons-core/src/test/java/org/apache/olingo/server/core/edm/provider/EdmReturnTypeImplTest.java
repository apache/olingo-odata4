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
import static org.mockito.Mockito.when;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.EdmReturnTypeImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;

public class EdmReturnTypeImplTest {

  @Test
  public void initialReturnType() {
    EdmReturnType returnType = new EdmReturnTypeImpl(mock(Edm.class), new CsdlReturnType());

    assertTrue(returnType.isNullable());
    assertNull(returnType.getMaxLength());
    assertNull(returnType.getPrecision());
    assertNull(returnType.getScale());
    assertNull(returnType.getSrid());

    try {
      returnType.getType();
      fail("EdmException expected");
    } catch (EdmException e) {
      assertEquals("Return types must hava a full qualified type.", e.getMessage());
    }
  }
  
  @Test
  public void primitiveReturnType() {
    CsdlReturnType providerType = new CsdlReturnType().setType(new FullQualifiedName("Edm", "String"));

    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);

    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String), typeImpl.getType());
    assertFalse(typeImpl.isCollection());
    assertTrue(typeImpl.isNullable());

    assertNull(typeImpl.getPrecision());
    assertNull(typeImpl.getMaxLength());
    assertNull(typeImpl.getScale());
    assertNull(typeImpl.getSrid());
  }

  @Test
  public void primitiveCollectionReturnType() {
    CsdlReturnType providerType = new CsdlReturnType().setType(
        new FullQualifiedName("Edm", "String")).setCollection(true);

    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);

    EdmType cachedType = typeImpl.getType();
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String), cachedType);
    assertTrue(typeImpl.isCollection());
    assertTrue(cachedType == typeImpl.getType());
  }

  @Test(expected = EdmException.class)
  public void invalidPrimitiveType() {
    CsdlReturnType providerType = new CsdlReturnType().setType(
        new FullQualifiedName("Edm", "wrong")).setCollection(true);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);
    typeImpl.getType();
  }

  @Test
  public void complexType() {
    EdmProviderImpl mock = mock(EdmProviderImpl.class);
    FullQualifiedName baseType = new FullQualifiedName("namespace", "type");
    EdmComplexType edmType = mock(EdmComplexType.class);
    when(mock.getComplexType(baseType)).thenReturn(edmType);
    CsdlReturnType providerType = new CsdlReturnType().setType(baseType);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock, providerType);
    EdmType returnedType = typeImpl.getType();
    assertEquals(edmType, returnedType);
  }

  @Test
  public void entityType() {
    EdmProviderImpl mock = mock(EdmProviderImpl.class);
    FullQualifiedName baseType = new FullQualifiedName("namespace", "type");
    EdmEntityType edmType = mock(EdmEntityType.class);
    when(mock.getEntityType(baseType)).thenReturn(edmType);
    CsdlReturnType providerType = new CsdlReturnType().setType(baseType);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock, providerType);
    EdmType returnedType = typeImpl.getType();
    assertEquals(edmType, returnedType);
  }

  @Test
  public void enumType() {
    EdmProviderImpl mock = mock(EdmProviderImpl.class);
    FullQualifiedName baseType = new FullQualifiedName("namespace", "type");
    EdmEnumType edmType = mock(EdmEnumType.class);
    when(mock.getEnumType(baseType)).thenReturn(edmType);
    CsdlReturnType providerType = new CsdlReturnType().setType(baseType);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock, providerType);
    EdmType returnedType = typeImpl.getType();
    assertEquals(edmType, returnedType);
  }

  @Test
  public void typeDefinition() {
    EdmProviderImpl mock = mock(EdmProviderImpl.class);
    FullQualifiedName baseType = new FullQualifiedName("namespace", "type");
    EdmTypeDefinition edmType = mock(EdmTypeDefinition.class);
    when(mock.getTypeDefinition(baseType)).thenReturn(edmType);
    CsdlReturnType providerType = new CsdlReturnType().setType(baseType);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock, providerType);
    EdmType returnedType = typeImpl.getType();
    assertEquals(edmType, returnedType);
  }

  @Test(expected = EdmException.class)
  public void invalidType() {
    CsdlReturnType providerType = new CsdlReturnType().setType(new FullQualifiedName("wrong", "wrong"));
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);
    typeImpl.getType();
  }

}
