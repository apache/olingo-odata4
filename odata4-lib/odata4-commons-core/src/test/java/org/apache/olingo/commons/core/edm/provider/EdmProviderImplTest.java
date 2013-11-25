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
package org.apache.olingo.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.helper.EntityContainerInfo;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Action;
import org.apache.olingo.commons.api.edm.provider.ComplexType;
import org.apache.olingo.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.commons.api.edm.provider.EntityType;
import org.apache.olingo.commons.api.edm.provider.EnumType;
import org.apache.olingo.commons.api.edm.provider.Function;
import org.apache.olingo.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;
import org.junit.Before;
import org.junit.Test;

public class EdmProviderImplTest {

  private Edm edm;
  private final FullQualifiedName FQN = new FullQualifiedName("testNamespace", "testName");
  private final FullQualifiedName WRONG_FQN = new FullQualifiedName("wrong", "wrong");

  @Before
  public void setup() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EntityContainerInfo containerInfo = new EntityContainerInfo().setContainerName(FQN);
    when(provider.getEntityContainerInfo(FQN)).thenReturn(containerInfo);
    when(provider.getEntityContainerInfo(null)).thenReturn(containerInfo);

    EnumType enumType = new EnumType().setName(FQN.getName());
    when(provider.getEnumType(FQN)).thenReturn(enumType);

    TypeDefinition typeDefinition = new TypeDefinition().setName(FQN.getName());
    when(provider.getTypeDefinition(FQN)).thenReturn(typeDefinition);

    EntityType entityType = new EntityType().setName(FQN.getName()).setKey(new ArrayList<PropertyRef>());
    when(provider.getEntityType(FQN)).thenReturn(entityType);

    ComplexType complexType = new ComplexType().setName(FQN.getName());
    when(provider.getComplexType(FQN)).thenReturn(complexType);

    Action action = new Action().setName(FQN.getName());
    when(provider.getAction(FQN, null, null)).thenReturn(action);

    Function function = new Function().setName(FQN.getName());
    when(provider.getFunction(FQN, null, null, null)).thenReturn(function);

    edm = new EdmProviderImpl(provider);
  }

  @Test
  public void getEntityContainer() {
    EdmEntityContainer entityContainer = edm.getEntityContainer(FQN);
    assertNotNull(entityContainer);
    assertEquals(FQN.getNamespace(), entityContainer.getNamespace());
    assertEquals(FQN.getName(), entityContainer.getName());

    entityContainer = edm.getEntityContainer(null);
    assertNotNull(entityContainer);
    assertEquals(FQN.getNamespace(), entityContainer.getNamespace());
    assertEquals(FQN.getName(), entityContainer.getName());

    assertNull(edm.getEntityContainer(WRONG_FQN));
  }

  @Test
  public void getEnumType() {
    EdmEnumType enumType = edm.getEnumType(FQN);
    assertNotNull(enumType);
    assertEquals(FQN.getNamespace(), enumType.getNamespace());
    assertEquals(FQN.getName(), enumType.getName());

    assertNull(edm.getEnumType(WRONG_FQN));
  }

  @Test
  public void getTypeDefinition() {
    EdmTypeDefinition typeDefinition = edm.getTypeDefinition(FQN);
    assertNotNull(typeDefinition);
    assertEquals(FQN.getNamespace(), typeDefinition.getNamespace());
    assertEquals(FQN.getName(), typeDefinition.getName());

    assertNull(edm.getTypeDefinition(WRONG_FQN));
  }

  @Test
  public void getEntityType() {
    EdmEntityType entityType = edm.getEntityType(FQN);
    assertNotNull(entityType);
    assertEquals(FQN.getNamespace(), entityType.getNamespace());
    assertEquals(FQN.getName(), entityType.getName());

    assertNull(edm.getEntityType(WRONG_FQN));
  }

  @Test
  public void getComplexType() {
    EdmComplexType complexType = edm.getComplexType(FQN);
    assertNotNull(complexType);
    assertEquals(FQN.getNamespace(), complexType.getNamespace());
    assertEquals(FQN.getName(), complexType.getName());

    assertNull(edm.getComplexType(WRONG_FQN));
  }

  @Test
  public void getAction() {
    EdmAction action = edm.getAction(FQN, null, null);
    assertNotNull(action);
    assertEquals(FQN.getNamespace(), action.getNamespace());
    assertEquals(FQN.getName(), action.getName());

    assertNull(edm.getAction(WRONG_FQN, null, null));
  }

  @Test
  public void getFunction() {
    EdmFunction function = edm.getFunction(FQN, null, null, null);
    assertNotNull(function);
    assertEquals(FQN.getNamespace(), function.getNamespace());
    assertEquals(FQN.getName(), function.getName());

    assertNull(edm.getFunction(WRONG_FQN, null, null, null));
  }

  @Test
  public void getServiceMetadata() {
    assertNotNull(edm.getServiceMetadata());
  }

}
