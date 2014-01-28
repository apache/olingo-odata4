/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmReturnType;
import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.ReturnType;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmReturnTypeImplTest {

  @Test
  public void primitiveReturnType() {
    ReturnType providerType = new ReturnType().setType(new FullQualifiedName("Edm", "String"));

    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);

    assertEquals(EdmPrimitiveTypeKind.String.getEdmPrimitiveTypeInstance(), typeImpl.getType());
    assertFalse(typeImpl.isCollection());

    assertNull(typeImpl.getPrecision());
    assertNull(typeImpl.getMaxLength());
    assertNull(typeImpl.getScale());
    assertNull(typeImpl.isNullable());
  }

  @Test
  public void primitiveCollectionReturnType() {
    ReturnType providerType = new ReturnType().setType(new FullQualifiedName("Edm", "String")).setCollection(true);

    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);

    assertEquals(EdmPrimitiveTypeKind.String.getEdmPrimitiveTypeInstance(), typeImpl.getType());
    assertTrue(typeImpl.isCollection());
  }

  @Test(expected = EdmException.class)
  public void invalidPrimitiveType() {
    ReturnType providerType = new ReturnType().setType(new FullQualifiedName("Edm", "wrong")).setCollection(true);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);
    typeImpl.getType();
  }

  @Test
  public void complexType() {
    EdmProviderImpl mock = mock(EdmProviderImpl.class);
    FullQualifiedName baseType = new FullQualifiedName("namespace", "type");
    EdmComplexType edmType = mock(EdmComplexType.class);
    when(mock.getComplexType(baseType)).thenReturn(edmType);
    ReturnType providerType = new ReturnType().setType(baseType);
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
    ReturnType providerType = new ReturnType().setType(baseType);
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
    ReturnType providerType = new ReturnType().setType(baseType);
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
    ReturnType providerType = new ReturnType().setType(baseType);
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock, providerType);
    EdmType returnedType = typeImpl.getType();
    assertEquals(edmType, returnedType);
  }

  @Test(expected = EdmException.class)
  public void invalidType() {
    ReturnType providerType = new ReturnType().setType(new FullQualifiedName("wrong", "wrong"));
    EdmReturnType typeImpl = new EdmReturnTypeImpl(mock(EdmProviderImpl.class), providerType);
    typeImpl.getType();
  }

}
