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
package org.apache.olingo.odata4.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.server.api.edm.provider.PropertyRef;
import org.junit.Test;

public class EdmKeyPropertyRefImplTest {

  @Test
  public void noAlias() {
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    when(etMock.getProperty("Id")).thenReturn(keyPropertyMock);
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(etMock, providerRef);
    assertEquals("Id", ref.getKeyPropertyName());
    assertNull(ref.getAlias());
    assertNull(ref.getPath());

    EdmProperty property = ref.getProperty();
    assertNotNull(property);
    assertTrue(property == keyPropertyMock);
    assertTrue(property == ref.getProperty());
  }

  @Test
  public void aliasForPropertyInComplexPropertyOneLevel() {
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id").setAlias("alias").setPath("comp/Id");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    EdmElement compMock = mock(EdmProperty.class);
    EdmComplexType compTypeMock = mock(EdmComplexType.class);
    when(compTypeMock.getProperty("Id")).thenReturn(keyPropertyMock);
    when(compMock.getType()).thenReturn(compTypeMock);
    when(etMock.getProperty("comp")).thenReturn(compMock);
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(etMock, providerRef);
    assertEquals("alias", ref.getAlias());
    assertEquals("comp/Id", ref.getPath());

    EdmProperty property = ref.getProperty();
    assertNotNull(property);
    assertTrue(property == keyPropertyMock);
  }

  @Test(expected = EdmException.class)
  public void aliasForPropertyInComplexPropertyButWrongPath() {
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id").setAlias("alias").setPath("comp/wrong");
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
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id").setAlias("alias").setPath("wrong/Id");
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
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id").setAlias("alias").setPath("comp/comp2/Id");
    EdmEntityType etMock = mock(EdmEntityType.class);
    EdmProperty keyPropertyMock = mock(EdmProperty.class);
    EdmElement compMock = mock(EdmProperty.class);
    EdmComplexType compTypeMock = mock(EdmComplexType.class);
    EdmElement comp2Mock = mock(EdmProperty.class);
    EdmComplexType comp2TypeMock = mock(EdmComplexType.class);
    when(comp2TypeMock.getProperty("Id")).thenReturn(keyPropertyMock);
    when(comp2Mock.getType()).thenReturn(comp2TypeMock);
    when(compTypeMock.getProperty("comp2")).thenReturn(comp2Mock);
    when(compMock.getType()).thenReturn(compTypeMock);
    when(etMock.getProperty("comp")).thenReturn(compMock);
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(etMock, providerRef);

    EdmProperty property = ref.getProperty();
    assertNotNull(property);
    assertTrue(property == keyPropertyMock);
  }

  @Test(expected = EdmException.class)
  public void oneKeyNoAliasButInvalidProperty() {
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id");
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(mock(EdmEntityType.class), providerRef);
    ref.getProperty();
  }

  @Test(expected = EdmException.class)
  public void aliasButNoPath() {
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id").setAlias("alias");
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(mock(EdmEntityType.class), providerRef);
    ref.getProperty();
  }

  @Test(expected = EdmException.class)
  public void aliasButEmptyPath() {
    PropertyRef providerRef = new PropertyRef().setPropertyName("Id").setAlias("alias").setPath("");
    EdmKeyPropertyRef ref = new EdmKeyPropertyRefImpl(mock(EdmEntityType.class), providerRef);
    ref.getProperty();
  }
}
