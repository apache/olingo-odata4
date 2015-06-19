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
package org.apache.olingo.client.core.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.apache.olingo.client.core.serialization.ContextURLParser;
import org.apache.olingo.commons.api.data.ContextURL;
import org.junit.Test;

public class ContextURLParserTest {

  @Test
  public void collectionOfEntities() {
    ContextURL contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers"));

    assertEquals(URI.create("http://host/service/"), contextURL.getServiceRoot());
    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Orders(4711)/Items"));

    assertEquals("Orders", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertEquals("Items", contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Me/Folders('Inbox')/Messages"));

    assertEquals("Me/Folders", contextURL.getEntitySetOrSingletonOrType());
    assertEquals("Messages", contextURL.getNavOrPropertyPath());
  }

  @Test
  public void entity() {
    ContextURL contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers/$entity"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Orders(4711)/Items/$entity"));

    assertEquals("Orders/Items", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Users('user')/Messages('message')/Attachments/$entity"));

    assertEquals("Users/Messages/Attachments", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    // v3
    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Products/@Element"));

    assertEquals("Products", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());
  }

  @Test
  public void singleton() {
    ContextURL contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Contoso"));

    assertEquals("Contoso", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void collectionOfDerivedEntities() {
    final ContextURL contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Customers/Model.VipCustomer"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertEquals("Model.VipCustomer", contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void derivedEntity() {
    final ContextURL contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Customers/Model.VipCustomer/$entity"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertEquals("Model.VipCustomer", contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());
  }

  @Test
  public void collectionOfProjectedEntities() {
    final ContextURL contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Customers(Address,Orders)"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertEquals("Address,Orders", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void projectedEntity() {
    ContextURL contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Customers(Name,Rating)/$entity"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertEquals("Name,Rating", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertTrue(contextURL.isEntity());

    contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Customers(Name,Address/Country)"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertEquals("Name,Address/Country", contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void collectionOfProjectedExpandedEntities() {
    final ContextURL contextURL = ContextURLParser.parse(
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
    final ContextURL contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Customers(1)/Addresses"));

    assertEquals("Customers", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertEquals("Addresses", contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void CollectionOfComplexOrPrimitiveTypes() {
    final ContextURL contextURL = ContextURLParser.parse(
        URI.create("http://host/service/$metadata#Collection(Edm.String)"));

    assertEquals("Collection(Edm.String)", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void complexOrPrimitiveType() {
    ContextURL contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Edm.String"));

    assertEquals("Edm.String", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#ODataDemo.Address"));

    assertEquals("ODataDemo.Address", contextURL.getEntitySetOrSingletonOrType());
    assertNull(contextURL.getDerivedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }

  @Test
  public void reference() {
    ContextURL contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers/$ref"));
    assertTrue(contextURL.isReference());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
    assertFalse(contextURL.isDelta());
  }

  @Test
  public void delta() {
    ContextURL contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers/$delta"));
    assertTrue(contextURL.isDelta());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers/$deletedLink"));
    assertTrue(contextURL.isDeltaDeletedLink());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers/$link"));
    assertTrue(contextURL.isDeltaLink());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());

    contextURL = ContextURLParser.parse(URI.create("http://host/service/$metadata#Customers/$deletedEntity"));
    assertTrue(contextURL.isDeltaDeletedEntity());
    assertNull(contextURL.getSelectList());
    assertNull(contextURL.getNavOrPropertyPath());
    assertFalse(contextURL.isEntity());
  }
}
