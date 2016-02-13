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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.core.edm.EdmEntityTypeImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmEntityTypeImplTest {

  private EdmEntityType baseType;

  private EdmEntityType typeWithBaseType;

  private EdmEntityType typeWithComplexKey;

  @Before
  public void setupTypes() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    CsdlEntityType baseType = new CsdlEntityType();
    baseType.setName(baseName.getName());
    List<CsdlProperty> properties = new ArrayList<CsdlProperty>();
    properties.add(new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    properties.add(new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    baseType.setProperties(properties);
    List<CsdlPropertyRef> key = new ArrayList<CsdlPropertyRef>();
    key.add(new CsdlPropertyRef().setName("Id"));
    baseType.setKey(key);
    List<CsdlNavigationProperty> navigationProperties = new ArrayList<CsdlNavigationProperty>();
    navigationProperties.add(new CsdlNavigationProperty().setName("nav1"));
    baseType.setNavigationProperties(navigationProperties);
    when(provider.getEntityType(baseName)).thenReturn(baseType);

    this.baseType = new EdmEntityTypeImpl(edm, baseName, baseType);

    FullQualifiedName typeName = new FullQualifiedName("namespace", "typeName");
    CsdlEntityType type = new CsdlEntityType();
    type.setName(typeName.getName());
    type.setBaseType(baseName);
    List<CsdlProperty> typeProperties = new ArrayList<CsdlProperty>();
    typeProperties.add(new CsdlProperty().setName("address").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    typeProperties.add(new CsdlProperty().setName("email").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    type.setProperties(typeProperties);
    List<CsdlNavigationProperty> typeNavigationProperties = new ArrayList<CsdlNavigationProperty>();
    typeNavigationProperties.add(new CsdlNavigationProperty().setName("nav2"));
    type.setNavigationProperties(typeNavigationProperties);
    when(provider.getEntityType(typeName)).thenReturn(type);

    typeWithBaseType = new EdmEntityTypeImpl(edm, typeName, type);

    FullQualifiedName typeWithComplexKeyName = new FullQualifiedName("namespace", "typeName");
    CsdlEntityType typeWithComplexKeyProvider = new CsdlEntityType();
    typeWithComplexKeyProvider.setName(typeWithComplexKeyName.getName());
    List<CsdlProperty> typeWithComplexKeyProperties = new ArrayList<CsdlProperty>();
    typeWithComplexKeyProperties.add(new CsdlProperty().setName("Id").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));

    List<CsdlProperty> complexTypeProperties = new ArrayList<CsdlProperty>();
    complexTypeProperties.add(new CsdlProperty().setName("ComplexPropName").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    FullQualifiedName complexTypeName = new FullQualifiedName("namespace", "complexTypeName");
    when(provider.getComplexType(complexTypeName)).thenReturn(
        new CsdlComplexType().setName("complexTypeName").setProperties(complexTypeProperties));

    typeWithComplexKeyProperties.add(new CsdlProperty().setName("Comp").setType(complexTypeName));
    typeWithComplexKeyProvider.setProperties(typeWithComplexKeyProperties);
    List<CsdlPropertyRef> keyForTypeWithComplexKey = new ArrayList<CsdlPropertyRef>();
    keyForTypeWithComplexKey.add(new CsdlPropertyRef().setName("Id"));
    keyForTypeWithComplexKey.add(new CsdlPropertyRef().setName("Comp/ComplexPropName").setAlias("alias"));
    typeWithComplexKeyProvider.setKey(keyForTypeWithComplexKey);
    when(provider.getEntityType(typeWithComplexKeyName)).thenReturn(typeWithComplexKeyProvider);

    typeWithComplexKey = new EdmEntityTypeImpl(edm, typeWithComplexKeyName, typeWithComplexKeyProvider);
  }

  @Test
  public void testAbstractBaseTypeWithoutKey() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    CsdlEntityType baseType = new CsdlEntityType();
    baseType.setName(baseName.getName());
    List<CsdlProperty> properties = new ArrayList<CsdlProperty>();
    properties.add(new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    properties.add(new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    baseType.setProperties(properties);
    List<CsdlNavigationProperty> navigationProperties = new ArrayList<CsdlNavigationProperty>();
    navigationProperties.add(new CsdlNavigationProperty().setName("nav1"));
    baseType.setNavigationProperties(navigationProperties);
    when(provider.getEntityType(baseName)).thenReturn(baseType);
    baseType.setAbstract(true);
    EdmEntityType edmAbstarctBaseType = new EdmEntityTypeImpl(edm, baseName, baseType);

    assertEquals(2, edmAbstarctBaseType.getPropertyNames().size());
    assertEquals("Id", edmAbstarctBaseType.getPropertyNames().get(0));
    assertEquals("Name", edmAbstarctBaseType.getPropertyNames().get(1));

    FullQualifiedName typeName = new FullQualifiedName("namespace", "typeName");
    CsdlEntityType type = new CsdlEntityType();
    type.setName(typeName.getName());
    type.setBaseType(baseName);
    List<CsdlProperty> typeProperties = new ArrayList<CsdlProperty>();
    typeProperties.add(new CsdlProperty().setName("address").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    typeProperties.add(new CsdlProperty().setName("email").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    type.setProperties(typeProperties);
    List<CsdlPropertyRef> key = new ArrayList<CsdlPropertyRef>();
    key.add(new CsdlPropertyRef().setName("email"));
    type.setKey(key);
    List<CsdlNavigationProperty> typeNavigationProperties = new ArrayList<CsdlNavigationProperty>();
    typeNavigationProperties.add(new CsdlNavigationProperty().setName("nav2"));
    type.setNavigationProperties(typeNavigationProperties);
    when(provider.getEntityType(typeName)).thenReturn(type);

    EdmEntityType edmType = new EdmEntityTypeImpl(edm, typeName, type);

    assertNotNull(edmType.getBaseType());
    assertEquals(2, edmAbstarctBaseType.getPropertyNames().size());

    assertEquals(1, edmType.getKeyPropertyRefs().size());
    assertEquals("email", edmType.getKeyPredicateNames().get(0));

    assertEquals(4, edmType.getPropertyNames().size());
    assertEquals("Id", edmType.getPropertyNames().get(0));
    assertEquals("Name", edmType.getPropertyNames().get(1));
    assertEquals("address", edmType.getPropertyNames().get(2));
    assertEquals("email", edmType.getPropertyNames().get(3));

    assertEquals(2, edmType.getNavigationPropertyNames().size());
    assertEquals("nav1", edmType.getNavigationPropertyNames().get(0));
    assertEquals("nav2", edmType.getNavigationPropertyNames().get(1));
  }

  @Test
  public void testAbstractBaseTypeWithtKey() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    CsdlEntityType baseType = new CsdlEntityType();
    baseType.setName(baseName.getName());
    List<CsdlProperty> properties = new ArrayList<CsdlProperty>();
    properties.add(new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    properties.add(new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    baseType.setProperties(properties);
    List<CsdlPropertyRef> key = new ArrayList<CsdlPropertyRef>();
    key.add(new CsdlPropertyRef().setName("Id"));
    baseType.setKey(key);
    List<CsdlNavigationProperty> navigationProperties = new ArrayList<CsdlNavigationProperty>();
    navigationProperties.add(new CsdlNavigationProperty().setName("nav1"));
    baseType.setNavigationProperties(navigationProperties);
    when(provider.getEntityType(baseName)).thenReturn(baseType);
    baseType.setAbstract(true);
    EdmEntityType edmAbstarctBaseType = new EdmEntityTypeImpl(edm, baseName, baseType);

    FullQualifiedName typeName = new FullQualifiedName("namespace", "typeName");
    CsdlEntityType type = new CsdlEntityType();
    type.setName(typeName.getName());
    type.setBaseType(baseName);
    List<CsdlProperty> typeProperties = new ArrayList<CsdlProperty>();
    typeProperties.add(new CsdlProperty().setName("address").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    typeProperties.add(new CsdlProperty().setName("email").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    type.setProperties(typeProperties);
    List<CsdlNavigationProperty> typeNavigationProperties = new ArrayList<CsdlNavigationProperty>();
    typeNavigationProperties.add(new CsdlNavigationProperty().setName("nav2"));
    type.setNavigationProperties(typeNavigationProperties);
    when(provider.getEntityType(typeName)).thenReturn(type);
    EdmEntityType edmType = new EdmEntityTypeImpl(edm, typeName, type);

    assertNotNull(edmType.getBaseType());
    assertEquals(2, edmAbstarctBaseType.getPropertyNames().size());

    assertEquals(1, edmType.getKeyPropertyRefs().size());
    assertEquals("Id", edmType.getKeyPredicateNames().get(0));

    assertEquals(4, edmType.getPropertyNames().size());
    assertEquals("Id", edmType.getPropertyNames().get(0));
    assertEquals("Name", edmType.getPropertyNames().get(1));
    assertEquals("address", edmType.getPropertyNames().get(2));
    assertEquals("email", edmType.getPropertyNames().get(3));

    assertEquals(2, edmType.getNavigationPropertyNames().size());
    assertEquals("nav1", edmType.getNavigationPropertyNames().get(0));
    assertEquals("nav2", edmType.getNavigationPropertyNames().get(1));
  }

  @Test
  public void hasStream() {
    assertFalse(typeWithBaseType.hasStream());
  }

  @Test
  public void hasStreamInherited() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    CsdlEntityType baseType = new CsdlEntityType();
    baseType.setHasStream(true);
    when(provider.getEntityType(baseName)).thenReturn(baseType);

    FullQualifiedName typeName = new FullQualifiedName("namespace", "typeName");
    CsdlEntityType type = new CsdlEntityType();
    type.setBaseType(baseName);
    EdmEntityType typeWithBaseTypeWithStream = new EdmEntityTypeImpl(edm, typeName, type);
    when(provider.getEntityType(typeName)).thenReturn(type);

    assertTrue(typeWithBaseTypeWithStream.hasStream());
  }

  @Test
  public void complexKeyWithAlias() {
    List<String> keyPredicateNames = typeWithComplexKey.getKeyPredicateNames();
    assertEquals(2, keyPredicateNames.size());
    assertEquals("Id", keyPredicateNames.get(0));
    assertEquals("alias", keyPredicateNames.get(1));

    EdmKeyPropertyRef keyPropertyRef = typeWithComplexKey.getKeyPropertyRef("Id");
    assertNotNull(keyPropertyRef);
    assertEquals("Id", keyPropertyRef.getName());
    assertNull(keyPropertyRef.getAlias());
    EdmProperty keyProperty = keyPropertyRef.getProperty();
    assertNotNull(keyProperty);
    assertEquals(typeWithComplexKey.getProperty("Id"), keyProperty);

    keyPropertyRef = typeWithComplexKey.getKeyPropertyRef("alias");
    assertNotNull(keyPropertyRef);
    assertEquals("Comp/ComplexPropName", keyPropertyRef.getName());
    assertEquals("alias", keyPropertyRef.getAlias());

    keyProperty = keyPropertyRef.getProperty();
    assertNotNull(keyProperty);
    EdmElement complexProperty = typeWithComplexKey.getProperty("Comp");
    EdmComplexType complexType = (EdmComplexType) complexProperty.getType();
    assertNotNull(complexType);
    assertEquals(complexType.getProperty("ComplexPropName"), keyProperty);
  }

  @Test
  public void keyBehaviour() {
    List<String> keyPredicateNames = baseType.getKeyPredicateNames();
    assertEquals(1, keyPredicateNames.size());
    assertEquals("Id", keyPredicateNames.get(0));

    EdmKeyPropertyRef keyPropertyRef = baseType.getKeyPropertyRef("Id");
    assertNotNull(keyPropertyRef);
    assertEquals("Id", keyPropertyRef.getName());
    assertNull(keyPropertyRef.getAlias());

    EdmProperty keyProperty = keyPropertyRef.getProperty();
    assertNotNull(keyProperty);
    assertEquals(baseType.getProperty("Id"), keyProperty);

    List<EdmKeyPropertyRef> keyPropertyRefs = baseType.getKeyPropertyRefs();
    assertNotNull(keyPropertyRefs);
    assertEquals(1, keyPropertyRefs.size());
    assertEquals("Id", keyPropertyRefs.get(0).getName());
  }

  @Test
  public void keyBehaviourWithBasetype() {
    List<String> keyPredicateNames = typeWithBaseType.getKeyPredicateNames();
    assertEquals(1, keyPredicateNames.size());
    assertEquals("Id", keyPredicateNames.get(0));

    EdmKeyPropertyRef keyPropertyRef = typeWithBaseType.getKeyPropertyRef("Id");
    assertNotNull(keyPropertyRef);
    assertEquals("Id", keyPropertyRef.getName());
    assertNull(keyPropertyRef.getAlias());

    List<EdmKeyPropertyRef> keyPropertyRefs = typeWithBaseType.getKeyPropertyRefs();
    assertNotNull(keyPropertyRefs);
    assertEquals(1, keyPropertyRefs.size());
    assertEquals("Id", keyPropertyRefs.get(0).getName());
    for (int i = 0; i < keyPropertyRefs.size(); i++) {
      assertEquals(keyPropertyRefs.get(i).getName(), typeWithBaseType.getKeyPropertyRefs().get(i).getName());
    }
  }

  @Test
  public void getBaseType() {
    assertNull(baseType.getBaseType());
    assertNotNull(typeWithBaseType.getBaseType());
  }

  @Test
  public void propertiesBehaviour() {
    List<String> propertyNames = baseType.getPropertyNames();
    assertEquals(2, propertyNames.size());
    assertEquals("Id", baseType.getProperty("Id").getName());
    assertEquals("Name", baseType.getProperty("Name").getName());
  }

  @Test
  public void propertiesBehaviourWithBaseType() {
    List<String> propertyNames = typeWithBaseType.getPropertyNames();
    assertEquals(4, propertyNames.size());
    assertEquals("Id", typeWithBaseType.getProperty("Id").getName());
    assertEquals("Name", typeWithBaseType.getProperty("Name").getName());
    assertEquals("address", typeWithBaseType.getProperty("address").getName());
    assertEquals("email", typeWithBaseType.getProperty("email").getName());
  }

  @Test
  public void navigationPropertiesBehaviour() {
    List<String> navigationPropertyNames = baseType.getNavigationPropertyNames();
    assertEquals(1, navigationPropertyNames.size());
    assertEquals("nav1", baseType.getProperty("nav1").getName());
  }

  @Test
  public void navigationPropertiesBehaviourWithBaseType() {
    List<String> navigationPropertyNames = typeWithBaseType.getNavigationPropertyNames();
    assertEquals(2, navigationPropertyNames.size());
    assertEquals("nav1", typeWithBaseType.getProperty("nav1").getName());
    assertEquals("nav2", typeWithBaseType.getProperty("nav2").getName());
  }

  @Test
  public void propertyCaching() {
    EdmElement property = typeWithBaseType.getProperty("Id");
    assertTrue(property == typeWithBaseType.getProperty("Id"));

    property = typeWithBaseType.getProperty("address");
    assertTrue(property == typeWithBaseType.getProperty("address"));

    property = typeWithBaseType.getProperty("nav1");
    assertTrue(property == typeWithBaseType.getProperty("nav1"));

    property = typeWithBaseType.getProperty("nav2");
    assertTrue(property == typeWithBaseType.getProperty("nav2"));
  }

  @Test
  public void abstractTypeDoesNotNeedKey() {
    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    CsdlEntityType entityType = new CsdlEntityType().setName("n").setAbstract(true);
    new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
  }

  @Test(expected = EdmException.class)
  public void invalidBaseType() {
    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    CsdlEntityType entityType = new CsdlEntityType().setName("n").setBaseType(new FullQualifiedName("wrong", "wrong"));
    EdmEntityTypeImpl instance = new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
    instance.getBaseType();
  }

  @Test
  public void openTypeDefaultIsFalse() {
    assertFalse(baseType.isOpenType());
  }

  @Test
  public void abstractTypeWithAbstractBaseTypeDoesNotNeedKey() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    FullQualifiedName baseName = new FullQualifiedName("n", "base");
    when(provider.getEntityType(baseName)).thenReturn(new CsdlEntityType().setName("base").setAbstract(true));
    CsdlEntityType entityType = new CsdlEntityType().setName("n").setAbstract(true).setBaseType(baseName);
    new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
  }

}
