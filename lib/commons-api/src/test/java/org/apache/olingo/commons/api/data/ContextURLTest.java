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
package org.apache.olingo.commons.api.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLTest {

  @Test
  public void collectionOfEntities() {
    ContextURL contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers"));

    assertEquals(URI.create("http://host/service/"), contextURL.getServiceRoot());
    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Orders(4711)/Items"));

    assertEquals("Orders", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertEquals("Items", contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void entity() {
    ContextURL contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers/$entity"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Orders(4711)/Items/$entity"));

    assertEquals("Orders", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertEquals("Items", contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    // v3
    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Products/@Element"));

    assertEquals("Products", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());
  }

  @Test
  public void singleton() {
    ContextURL contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Contoso"));

    assertEquals("Contoso", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void collectionOfDerivedEntities() {
    final ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Customers/Model.VipCustomer"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertEquals("Model.VipCustomer", contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void derivedEntity() {
    final ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Customers/Model.VipCustomer/$entity"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertEquals("Model.VipCustomer", contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());
  }

  @Test
  public void collectionOfProjectedEntities() {
    final ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Customers(Address,Orders)"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertEquals("Address,Orders", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void projectedEntity() {
    ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Customers(Name,Rating)/$entity"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertEquals("Name,Rating", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Customers(Name,Address/Country)"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertEquals("Name,Address/Country", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void collectionOfProjectedExpandedEntities() {
    final ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Employees/"
            + "Sales.Manager(DirectReports,DirectReports+(FirstName,LastName))"));

    assertEquals("Employees", contextURL.getEntitySetOrSingletonOrType());
    assertEquals("Sales.Manager", contextURL.getDerivedEntity());
    assertEquals("DirectReports,DirectReports+(FirstName,LastName)", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void propertyValue() {
    final ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Customers(1)/Addresses"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertEquals("Addresses", contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void CollectionOfComplexOrPrimitiveTypes() {
    final ContextURL contextURL = ContextURL.getInstance(
        URI.create("http://host/service/$metadata#Collection(Edm.String)"));

    assertEquals("Collection(Edm.String)", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void complexOrPrimitiveType() {
    ContextURL contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Edm.String"));

    assertEquals("Edm.String", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#ODataDemo.Address"));

    assertEquals("ODataDemo.Address", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void reference() {
    ContextURL contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers/$ref"));
    assertTrue(contextURL.isReference());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
    assertFalse(contextURL.isDelta());
  }

  @Test
  public void delta() {
    ContextURL contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers/$delta"));
    assertTrue(contextURL.isDelta());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers/$deletedLink"));
    assertTrue(contextURL.isDeltaDeletedLink());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers/$link"));
    assertTrue(contextURL.isDeltaLink());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURL.getInstance(URI.create("http://host/service/$metadata#Customers/$deletedEntity"));
    assertTrue(contextURL.isDeltaDeletedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void buildServiceDocument() {
    ContextURL contextURL = ContextURL.create().serviceRoot(URI.create("http://host/service/")).build();
    assertEquals("http://host/service/$metadata", contextURL.getURI().toASCIIString());
  }

  @Test
  public void buildRelative() {
    ContextURL contextURL = ContextURL.create().build();
    assertEquals("$metadata", contextURL.getURI().toASCIIString());
  }

  @Test
  public void buildEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.create().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .build();
    assertEquals("http://host/service/$metadata#Customers", contextURL.getURI().toASCIIString());
  }

  @Test
  public void buildDerivedEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURL contextURL = ContextURL.create().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer", contextURL.getURI().toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildDerivedEntitySetWithoutEntitySet() {
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURL.create().derived(derivedType).build();
  }

  @Test
  public void buildDerivedEntity() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURL contextURL = ContextURL.create().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer/$entity",
        contextURL.getURI().toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildSuffixWithoutEntitySet() {
    ContextURL.create().suffix(Suffix.ENTITY).build();
  }

  @Test
  public void buildReference() {
    ContextURL contextURL = ContextURL.create().suffix(Suffix.REFERENCE).build();
    assertEquals("$metadata#$ref", contextURL.getURI().toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildReferenceWithEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL.create().entitySet(entitySet).suffix(Suffix.REFERENCE).build();
  }
}
