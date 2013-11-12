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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuralType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.commons.api.edm.provider.Property;
import org.apache.olingo.commons.api.edm.provider.StructuralType;
import org.junit.Before;
import org.junit.Test;

public class EdmStructuralTypeImplTest {

  private EdmStructuralType type;
  private EdmStructuralType extendedType;

  @Before
  public void setupType() {
    EdmProviderImpl mockEdm = mock(EdmProviderImpl.class);

    StructuralType structuralTypeMock = mock(StructuralType.class);
    when(structuralTypeMock.getName()).thenReturn("MockName");

    List<Property> propertyList = new ArrayList<Property>();
    Property property = new Property().setName("TestName1");
    propertyList.add(property);
    property = new Property().setName("TestName2");
    propertyList.add(property);
    when(structuralTypeMock.getProperties()).thenReturn(propertyList);

    List<NavigationProperty> navigationPropertyList = new ArrayList<NavigationProperty>();
    NavigationProperty navProperty = new NavigationProperty().setName("TestNavName1");
    navigationPropertyList.add(navProperty);
    navProperty = new NavigationProperty().setName("TestNavName2");
    navigationPropertyList.add(navProperty);
    when(structuralTypeMock.getNavigationProperties()).thenReturn(navigationPropertyList);

    type = new EdmStructuralTypeImplTester(mockEdm, new FullQualifiedName("namespace", "MockName"), structuralTypeMock,
        EdmTypeKind.COMPLEX);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "name");
    EdmComplexType baseTypeMock = mock(EdmComplexType.class);
    when(baseTypeMock.getName()).thenReturn(baseName.getName());
    when(baseTypeMock.getNamespace()).thenReturn(baseName.getNamespace());
    List<String> basePropNames = new ArrayList<String>();
    basePropNames.add("TestName3");
    when(baseTypeMock.getPropertyNames()).thenReturn(basePropNames);
    List<String> baseNavNames = new ArrayList<String>();
    baseNavNames.add("TestNavName3");
    when(baseTypeMock.getNavigationPropertyNames()).thenReturn(baseNavNames);
    EdmProperty testName3Mock = mock(EdmProperty.class);
    when(testName3Mock.getName()).thenReturn("TestName3");
    when(baseTypeMock.getProperty("TestName3")).thenReturn(testName3Mock);
    EdmNavigationProperty testNavName3Mock = mock(EdmNavigationProperty.class);
    when(testNavName3Mock.getName()).thenReturn("TestNavName3");
    when(baseTypeMock.getProperty("TestNavName3")).thenReturn(testNavName3Mock);
    when(mockEdm.getComplexType(baseName)).thenReturn(baseTypeMock);

    StructuralType typeMockWithBaseType = mock(StructuralType.class);
    when(typeMockWithBaseType.getBaseType()).thenReturn(baseName);
    when(typeMockWithBaseType.getProperties()).thenReturn(propertyList);
    when(typeMockWithBaseType.getNavigationProperties()).thenReturn(navigationPropertyList);
    when(typeMockWithBaseType.getName()).thenReturn("MockName2");
    extendedType = new EdmStructuralTypeImplTester(mockEdm, new FullQualifiedName("namespace", "MockName2"),
        typeMockWithBaseType,
        EdmTypeKind.COMPLEX);
  }

  @Test
  public void getExistingProperty() {
    EdmElement property = type.getProperty("TestName1");
    assertNotNull(property);
    assertTrue(property instanceof EdmProperty);
    assertEquals("TestName1", property.getName());
  }

  @Test
  public void getExistingCachedProperty() {
    EdmElement property = type.getProperty("TestName1");
    assertNotNull(property);

    EdmElement cachedProperty = type.getProperty("TestName1");
    assertNotNull(property);

    assertEquals(property, cachedProperty);
  }

  @Test
  public void getExistingPropertyWithBaseType() {
    EdmElement property = extendedType.getProperty("TestName3");
    assertNotNull(property);
    assertTrue(property instanceof EdmProperty);
    assertEquals("TestName3", property.getName());
  }

  @Test
  public void getNonExistingProperty() {
    EdmElement property = type.getProperty("TestNameWrong");
    assertNull(property);
  }

  @Test
  public void getExistingNavigationProperty() {
    EdmElement property = type.getProperty("TestNavName1");
    assertNotNull(property);
    assertTrue(property instanceof EdmNavigationProperty);
    assertEquals("TestNavName1", property.getName());
  }

  @Test
  public void getExistingNavigationPropertyWithBaseType() {
    EdmElement property = extendedType.getProperty("TestNavName3");
    assertNotNull(property);
    assertTrue(property instanceof EdmNavigationProperty);
    assertEquals("TestNavName3", property.getName());

  }

  @Test
  public void getAllPropertyNamesAndVerifyExistence() {
    List<String> propertyNames = type.getPropertyNames();
    assertNotNull(propertyNames);
    assertEquals(2, propertyNames.size());
    assertTrue(propertyNames.contains("TestName1"));
    assertTrue(propertyNames.contains("TestName2"));
    for (String name : propertyNames) {
      assertNotNull(type.getProperty(name));
    }
  }

  @Test
  public void getAllNavigationPropertyNamesAndVerifyExistence() {
    List<String> navigationPropertyNames = type.getNavigationPropertyNames();
    assertNotNull(navigationPropertyNames);
    assertEquals(2, navigationPropertyNames.size());
    assertTrue(navigationPropertyNames.contains("TestNavName1"));
    assertTrue(navigationPropertyNames.contains("TestNavName2"));
    for (String name : navigationPropertyNames) {
      assertNotNull(type.getProperty(name));
    }
  }

  @Test
  public void getAllPropertyNamesAndVerifyExistenceWithBaseType() {
    List<String> propertyNames = extendedType.getPropertyNames();
    assertNotNull(propertyNames);
    assertEquals(3, propertyNames.size());
    assertTrue(propertyNames.contains("TestName1"));
    assertTrue(propertyNames.contains("TestName2"));
    assertTrue(propertyNames.contains("TestName3"));
    for (String name : propertyNames) {
      assertNotNull(extendedType.getProperty(name));
    }
  }

  @Test
  public void getAllNavigationPropertyNamesAndVerifyExistenceWithBaseType() {
    List<String> navigationPropertyNames = extendedType.getNavigationPropertyNames();
    assertNotNull(navigationPropertyNames);
    assertEquals(3, navigationPropertyNames.size());
    assertTrue(navigationPropertyNames.contains("TestNavName1"));
    assertTrue(navigationPropertyNames.contains("TestNavName2"));
    assertTrue(navigationPropertyNames.contains("TestNavName3"));
    for (String name : navigationPropertyNames) {
      assertNotNull(extendedType.getProperty(name));
    }
  }

  @Test
  public void verifyBaseType() {
    assertNotNull(extendedType.getBaseType());
  }

  private class EdmStructuralTypeImplTester extends EdmStructuralTypeImpl {
    public EdmStructuralTypeImplTester(final EdmProviderImpl edm, final FullQualifiedName name,
        final StructuralType structuralType,
        final EdmTypeKind kind) {
      super(edm, name, structuralType, kind);
    }
  }

}
