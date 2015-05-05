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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.core.edm.EdmKeyPropertyRefImpl;
import org.junit.Test;

public class EdmKeyPropertyRefImplTest {

  @Test
  public void noAlias() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("Id");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    when(etMock.getStructuralProperty("Id")).thenReturn(keyPropertyMock);
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(etMock, providerRef);
    assertEquals("Id", ref.getName());
    assertNull(ref.getAlias());

    EdmProperty property = ref.getProperty();
    assertNotNull(property);
    assertTrue(property == keyPropertyMock);
    assertTrue(property == ref.getProperty());
  }

  @Test
  public void aliasForPropertyInComplexPropertyOneLevel() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("comp/Id").setAlias("alias");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    EdmProperty compMock = mock(EdmProperty.class);
    EdmComplexType compTypeMock = mock(EdmComplexType.class);
    when(compTypeMock.getStructuralProperty("Id")).thenReturn(keyPropertyMock);
    when(compMock.getType()).thenReturn(compTypeMock);
    when(etMock.getStructuralProperty("comp")).thenReturn(compMock);
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(etMock, providerRef);
    assertEquals("alias", ref.getAlias());

    EdmProperty property = ref.getProperty();
    assertNotNull(property);
    assertTrue(property == keyPropertyMock);
  }

  @Test(expected = EdmException.class)
  public void aliasForPropertyInComplexPropertyButWrongPath() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("comp/wrong").setAlias("alias");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    EdmElement compMock = mock(EdmProperty.class);
    EdmComplexType compTypeMock = mock(EdmComplexType.class);
    when(compTypeMock.getProperty("Id")).thenReturn(keyPropertyMock);
    when(compMock.getType()).thenReturn(compTypeMock);
    when(etMock.getProperty("comp")).thenReturn(compMock);
    new EdmKeyPropertyRefImpl(etMock, providerRef).getProperty();
  }

  @Test(expected = EdmException.class)
  public void aliasForPropertyInComplexPropertyButWrongPath2() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("wrong/Id").setAlias("alias");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    EdmElement compMock = mock(EdmProperty.class);
    EdmComplexType compTypeMock = mock(EdmComplexType.class);
    when(compTypeMock.getProperty("Id")).thenReturn(keyPropertyMock);
    when(compMock.getType()).thenReturn(compTypeMock);
    when(etMock.getProperty("comp")).thenReturn(compMock);
    new EdmKeyPropertyRefImpl(etMock, providerRef).getProperty();
  }

  @Test
  public void aliasForPropertyInComplexPropertyTwoLevels() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("comp/comp2/Id").setAlias("alias");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    EdmProperty compMock = mock(EdmProperty.class);
    EdmComplexType compTypeMock = mock(EdmComplexType.class);
    EdmProperty comp2Mock = mock(EdmProperty.class);
    EdmComplexType comp2TypeMock = mock(EdmComplexType.class);
    when(comp2TypeMock.getStructuralProperty("Id")).thenReturn(keyPropertyMock);
    when(comp2Mock.getType()).thenReturn(comp2TypeMock);
    when(compTypeMock.getStructuralProperty("comp2")).thenReturn(comp2Mock);
    when(compMock.getType()).thenReturn(compTypeMock);
    when(etMock.getStructuralProperty("comp")).thenReturn(compMock);
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(etMock, providerRef);

    EdmProperty property = ref.getProperty();
    assertNotNull(property);
    assertTrue(property == keyPropertyMock);
  }

  @Test(expected = EdmException.class)
  public void oneKeyNoAliasButInvalidProperty() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("Id");
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(mock(EdmEntityType.class), providerRef);
    ref.getProperty();
  }

  @Test(expected = EdmException.class)
  public void aliasButNoPath() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("Id").setAlias("alias");
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(mock(EdmEntityType.class), providerRef);
    ref.getProperty();
  }

  @Test(expected = EdmException.class)
  public void aliasButEmptyPath() {
    CsdlPropertyRef providerRef = new CsdlPropertyRef().setName("").setAlias("alias");
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(mock(EdmEntityType.class), providerRef);
    ref.getProperty();
  }
}
