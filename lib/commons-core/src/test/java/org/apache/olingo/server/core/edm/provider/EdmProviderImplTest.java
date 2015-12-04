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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmProviderImplTest {

  private Edm edm;
  private final FullQualifiedName FQN = new FullQualifiedName("testNamespace", "testName");
  private final FullQualifiedName WRONG_FQN = new FullQualifiedName("wrong", "wrong");

  @Before
  public void setup() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    CsdlEntityContainerInfo containerInfo = new CsdlEntityContainerInfo().setContainerName(FQN);
    when(provider.getEntityContainerInfo(FQN)).thenReturn(containerInfo);
    when(provider.getEntityContainerInfo(null)).thenReturn(containerInfo);

    CsdlEnumType enumType = new CsdlEnumType().setName(FQN.getName());
    when(provider.getEnumType(FQN)).thenReturn(enumType);

    CsdlTypeDefinition typeDefinition =
        new CsdlTypeDefinition().setName(FQN.getName()).setUnderlyingType(new FullQualifiedName("Edm", "String"));
    when(provider.getTypeDefinition(FQN)).thenReturn(typeDefinition);

    CsdlEntityType entityType = new CsdlEntityType().setName(FQN.getName()).setKey(new ArrayList<CsdlPropertyRef>());
    when(provider.getEntityType(FQN)).thenReturn(entityType);

    CsdlComplexType complexType = new CsdlComplexType().setName(FQN.getName());
    when(provider.getComplexType(FQN)).thenReturn(complexType);

    List<CsdlAliasInfo> aliasInfos = new ArrayList<CsdlAliasInfo>();
    aliasInfos.add(new CsdlAliasInfo().setAlias("alias").setNamespace("namespace"));
    when(provider.getAliasInfos()).thenReturn(aliasInfos);

    CsdlAnnotations annotationsGroup = new CsdlAnnotations();
    annotationsGroup.setTarget("FQN.FQN");
    when(provider.getAnnotationsGroup(FQN, null)).thenReturn(annotationsGroup);

    edm = new EdmProviderImpl(provider);
  }

  @Test
  public void nothingSpecifiedMustNotResultInExceptions() throws Exception {
    CsdlEdmProvider localProvider = mock(CsdlEdmProvider.class);
    when(localProvider.getActions(FQN)).thenReturn(null);
    when(localProvider.getFunctions(FQN)).thenReturn(null);
    Edm localEdm = new EdmProviderImpl(localProvider);
    localEdm.getUnboundAction(FQN);
    localEdm.getUnboundFunction(FQN, null);
    localEdm.getBoundAction(FQN, FQN, true);
    localEdm.getBoundFunction(FQN, FQN, true, null);
    localEdm.getComplexType(FQN);
    localEdm.getEntityContainer(FQN);
    localEdm.getEntityType(FQN);
    localEdm.getEnumType(FQN);
    localEdm.getTypeDefinition(FQN);
  }

  @Test
  public void convertExceptionsTest() throws Exception {
    CsdlEdmProvider localProvider = mock(CsdlEdmProvider.class);
    FullQualifiedName fqn = new FullQualifiedName("namespace", "name");
    when(localProvider.getEntityContainerInfo(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getEnumType(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getTypeDefinition(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getEntityType(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getComplexType(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getActions(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getFunctions(fqn)).thenThrow(new ODataException("msg"));
    when(localProvider.getAnnotationsGroup(fqn, null)).thenThrow(new ODataException("msg"));

    Edm localEdm = new EdmProviderImpl(localProvider);

    callMethodAndExpectEdmException(localEdm, "getEntityContainer");
    callMethodAndExpectEdmException(localEdm, "getEnumType");
    callMethodAndExpectEdmException(localEdm, "getTypeDefinition");
    callMethodAndExpectEdmException(localEdm, "getEntityType");
    callMethodAndExpectEdmException(localEdm, "getComplexType");

    // seperate because of signature
    try {
      localEdm.getUnboundAction(fqn);
      fail("Expeced an EdmException");
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.commons.api.ex.ODataException: msg", e.getMessage());
    }

    try {
      localEdm.getUnboundFunction(fqn, null);
      fail("Expeced an EdmException");
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.commons.api.ex.ODataException: msg", e.getMessage());
    }
    try {
      localEdm.getBoundAction(fqn, fqn, true);
      fail("Expeced an EdmException");
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.commons.api.ex.ODataException: msg", e.getMessage());
    }

    try {
      localEdm.getBoundFunction(fqn, fqn, true, null);
      fail("Expeced an EdmException");
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.commons.api.ex.ODataException: msg", e.getMessage());
    }

    try {
      localEdm.getAnnotationGroup(fqn, null);
      fail("Expeced an EdmException");
    } catch (EdmException e) {
      assertEquals("org.apache.olingo.commons.api.ex.ODataException: msg", e.getMessage());
    }
  }

  private void callMethodAndExpectEdmException(final Edm localEdm, final String methodName) throws Exception {
    Method method = localEdm.getClass().getMethod(methodName, FullQualifiedName.class);
    try {
      method.invoke(localEdm, new FullQualifiedName("namespace", "name"));
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof EdmException) {
        return;
      }
    }
    fail("EdmException expected for method: " + methodName);
  }

  @Test(expected = EdmException.class)
  public void convertExceptionsAliasTest() throws Exception {
    CsdlEdmProvider localProvider = mock(CsdlEdmProvider.class);
    when(localProvider.getAliasInfos()).thenThrow(new ODataException("msg"));

    Edm localEdm = new EdmProviderImpl(localProvider);
    localEdm.getEntityContainer();
  }

  @Test
  public void getEntityContainer() {
    EdmEntityContainer entityContainer = edm.getEntityContainer(FQN);
    assertNotNull(entityContainer);
    assertEquals(FQN.getNamespace(), entityContainer.getNamespace());
    assertEquals(FQN.getName(), entityContainer.getName());

    entityContainer = edm.getEntityContainer();
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
  public void getAnnotations() {
    EdmAnnotations annotationGroup = edm.getAnnotationGroup(FQN, null);
    assertNotNull(annotationGroup);

    assertNull(edm.getAnnotationGroup(WRONG_FQN, null));
  }
}
