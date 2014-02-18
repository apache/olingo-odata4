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
package org.apache.olingo.odata4.commons.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmElement;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.odata4.commons.api.edm.EdmProperty;
import org.apache.olingo.odata4.commons.api.edm.provider.ComplexType;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.odata4.commons.api.edm.provider.Property;
import org.apache.olingo.odata4.commons.api.edm.provider.PropertyRef;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.junit.Before;
import org.junit.Test;

public class EdmEntityTypeImplTest {

  private EdmEntityType baseType;
  private EdmEntityType typeWithBaseType;
  private EdmEntityType typeWithComplexKey;

  @Before
  public void setupTypes() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    EntityType baseType = new EntityType();
    baseType.setName(baseName.getName());
    List<Property> properties = new ArrayList<Property>();
    properties.add(new Property().setName("Id").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    properties.add(new Property().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    baseType.setProperties(properties);
    List<PropertyRef> key = new ArrayList<PropertyRef>();
    key.add(new PropertyRef().setPropertyName("Id"));
    baseType.setKey(key);
    List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();
    navigationProperties.add(new NavigationProperty().setName("nav1"));
    baseType.setNavigationProperties(navigationProperties);
    when(provider.getEntityType(baseName)).thenReturn(baseType);

    this.baseType = new EdmEntityTypeImpl(edm, baseName, baseType);

    FullQualifiedName typeName = new FullQualifiedName("namespace", "typeName");
    EntityType type = new EntityType();
    type.setName(typeName.getName());
    type.setBaseType(baseName);
    List<Property> typeProperties = new ArrayList<Property>();
    typeProperties.add(new Property().setName("address").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    typeProperties.add(new Property().setName("email").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    type.setProperties(typeProperties);
    List<NavigationProperty> typeNavigationProperties = new ArrayList<NavigationProperty>();
    typeNavigationProperties.add(new NavigationProperty().setName("nav2"));
    type.setNavigationProperties(typeNavigationProperties);
    when(provider.getEntityType(typeName)).thenReturn(type);

    typeWithBaseType = new EdmEntityTypeImpl(edm, typeName, type);

    FullQualifiedName typeWithComplexKeyName = new FullQualifiedName("namespace", "typeName");
    EntityType typeWithComplexKeyProvider = new EntityType();
    typeWithComplexKeyProvider.setName(typeWithComplexKeyName.getName());
    List<Property> typeWithComplexKeyProperties = new ArrayList<Property>();
    typeWithComplexKeyProperties.add(new Property().setName("Id").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));

    List<Property> complexTypeProperties = new ArrayList<Property>();
    complexTypeProperties.add(new Property().setName("ComplexPropName").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    FullQualifiedName complexTypeName = new FullQualifiedName("namespace", "complexTypeName");
    when(provider.getComplexType(complexTypeName)).thenReturn(
        new ComplexType().setName("complexTypeName").setProperties(complexTypeProperties));

    typeWithComplexKeyProperties.add(new Property().setName("Comp").setType(complexTypeName));
    typeWithComplexKeyProvider.setProperties(typeWithComplexKeyProperties);
    List<PropertyRef> keyForTypeWithComplexKey = new ArrayList<PropertyRef>();
    keyForTypeWithComplexKey.add(new PropertyRef().setPropertyName("Id"));
    keyForTypeWithComplexKey.add(new PropertyRef().setPropertyName("ComplexPropName").setAlias("alias").setPath(
        "Comp/ComplexPropName"));
    typeWithComplexKeyProvider.setKey(keyForTypeWithComplexKey);
    when(provider.getEntityType(typeWithComplexKeyName)).thenReturn(typeWithComplexKeyProvider);

    typeWithComplexKey = new EdmEntityTypeImpl(edm, typeWithComplexKeyName, typeWithComplexKeyProvider);
  }

  @Test
  public void hasStream() {
    assertFalse(typeWithBaseType.hasStream());
  }

  @Test
  public void complexKeyWithAlias() {
    List<String> keyPredicateNames = typeWithComplexKey.getKeyPredicateNames();
    assertEquals(2, keyPredicateNames.size());
    assertEquals("Id", keyPredicateNames.get(0));
    assertEquals("alias", keyPredicateNames.get(1));

    EdmKeyPropertyRef keyPropertyRef = typeWithComplexKey.getKeyPropertyRef("Id");
    assertNotNull(keyPropertyRef);
    assertEquals("Id", keyPropertyRef.getKeyPropertyName());
    assertNull(keyPropertyRef.getAlias());
    EdmProperty keyProperty = keyPropertyRef.getProperty();
    assertNotNull(keyProperty);
    assertEquals(typeWithComplexKey.getProperty("Id"), keyProperty);

    keyPropertyRef = typeWithComplexKey.getKeyPropertyRef("alias");
    assertNotNull(keyPropertyRef);
    assertEquals("ComplexPropName", keyPropertyRef.getKeyPropertyName());
    assertEquals("alias", keyPropertyRef.getAlias());
    assertEquals("Comp/ComplexPropName", keyPropertyRef.getPath());

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
    assertEquals("Id", keyPropertyRef.getKeyPropertyName());
    assertNull(keyPropertyRef.getAlias());

    EdmProperty keyProperty = keyPropertyRef.getProperty();
    assertNotNull(keyProperty);
    assertEquals(baseType.getProperty("Id"), keyProperty);

    List<EdmKeyPropertyRef> keyPropertyRefs = baseType.getKeyPropertyRefs();
    assertNotNull(keyPropertyRefs);
    assertEquals(1, keyPropertyRefs.size());
    assertEquals("Id", keyPropertyRefs.get(0).getKeyPropertyName());
  }

  @Test
  public void keyBehaviourWithBasetype() {
    List<String> keyPredicateNames = typeWithBaseType.getKeyPredicateNames();
    assertEquals(1, keyPredicateNames.size());
    assertEquals("Id", keyPredicateNames.get(0));

    EdmKeyPropertyRef keyPropertyRef = typeWithBaseType.getKeyPropertyRef("Id");
    assertNotNull(keyPropertyRef);
    assertEquals("Id", keyPropertyRef.getKeyPropertyName());
    assertNull(keyPropertyRef.getAlias());

    List<EdmKeyPropertyRef> keyPropertyRefs = typeWithBaseType.getKeyPropertyRefs();
    assertNotNull(keyPropertyRefs);
    assertEquals(1, keyPropertyRefs.size());
    assertEquals("Id", keyPropertyRefs.get(0).getKeyPropertyName());
    assertTrue(keyPropertyRefs == typeWithBaseType.getKeyPropertyRefs());
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

  @Test(expected = EdmException.class)
  public void noKeyOnTypeWithoutBaseTypeMustResultInException() {
    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    EntityType entityType = new EntityType().setName("n");
    new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
  }

  @Test
  public void abstractTypeDoesNotNeedKey() {
    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    EntityType entityType = new EntityType().setName("n").setAbstract(true);
    new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
  }

  @Test(expected = EdmException.class)
  public void invalidBaseType() {
    EdmProviderImpl edm = mock(EdmProviderImpl.class);
    EntityType entityType = new EntityType().setName("n").setBaseType(new FullQualifiedName("wrong", "wrong"));
    new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
  }

  @Test
  public void abstractTypeWithAbstractBaseTypeDoesNotNeedKey() throws Exception {
    EdmProvider provider = mock(EdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    FullQualifiedName baseName = new FullQualifiedName("n", "base");
    when(provider.getEntityType(baseName)).thenReturn(new EntityType().setName("base").setAbstract(true));
    EntityType entityType = new EntityType().setName("n").setAbstract(true).setBaseType(baseName);
    new EdmEntityTypeImpl(edm, new FullQualifiedName("n", "n"), entityType);
  }

}
