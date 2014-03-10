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
package org.apache.olingo.odata4.client.core.deserializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.odata4.client.api.deserializer.ComplexValue;
import org.apache.olingo.odata4.client.api.deserializer.Entity;
import org.apache.olingo.odata4.client.api.deserializer.EntitySet;
import org.apache.olingo.odata4.client.api.deserializer.Property;
import org.apache.olingo.odata4.client.api.deserializer.Reader;
import org.apache.olingo.odata4.client.api.deserializer.StructuralProperty;
import org.apache.olingo.odata4.client.api.deserializer.Value;
import org.junit.Test;

public class JsonReaderTest {

  @Test
  public void testEntitySet() throws Exception {
    Reader consumer = new JsonReader();

    EntitySet entitySet = consumer.readEntitySet(
            JsonReaderTest.class.getResourceAsStream("/fullEntitySet.json"));

    List<Entity> entities = entitySet.getEntities();
    validateEntitySet(entitySet);
    assertNotNull(entities);
    Entity entity = entities.get(0);
    assertNotNull(entity);
    validateEntityAlfki(entity);

    StructuralProperty structuralProperty = entity.getProperty("Address", StructuralProperty.class);
    assertEquals("Address", structuralProperty.getName());

    Value value = structuralProperty.getValue();
    assertTrue(value.isComplex());
    ComplexValue complexValue = (ComplexValue) value;
    validateAddressBerlin(complexValue);
    validateComplexValueNavigationLinks(complexValue);
  }

  @Test
  public void testEntitySetWithTwoEntities() throws Exception {
    Reader consumer = new JsonReader();

    EntitySet entitySet = consumer.readEntitySet(getJson("/fullEntitySetWithTwoEntities.json"));

    List<Entity> entities = entitySet.getEntities();
    validateEntitySet(entitySet);
    assertNotNull(entities);
    assertEquals(2, entities.size());
    //
    Entity firstEntity = entities.get(0);
    assertNotNull(firstEntity);
    validateEntityAlfki(firstEntity);

    StructuralProperty structuralProperty = firstEntity.getProperty("Address", StructuralProperty.class);
    assertEquals("Address", structuralProperty.getName());

    Value value = structuralProperty.getValue();
    assertTrue(value.isComplex());
    ComplexValue complexValue = (ComplexValue) value;
    validateAddressBerlin(complexValue);
    validateComplexValueNavigationLinks(complexValue);

    //
    Entity secondEntity = entities.get(1);
    assertNotNull(secondEntity);
    validateEntityMuski(secondEntity);

    StructuralProperty addressMuster = secondEntity.getProperty("Address", StructuralProperty.class);
    assertEquals("Address", addressMuster.getName());
    validateAddressMuster((ComplexValue) addressMuster.getValue());
  }

  @Test
  public void streamingTestForEntitySetWithTwoEntities() throws Exception {
    Reader consumer = new JsonReader();

    EntitySet entitySet = consumer.readEntitySet(getJson("/fullEntitySetWithTwoEntities.json"));
    validateEntitySet(entitySet, false);

    for (Entity entity : entitySet) {
      if ("Customers('ALFKI')".equals(entity.getODataId())) {
        validateEntityAlfki(entity);

        StructuralProperty structuralProperty = entity.getProperty("Address", StructuralProperty.class);
        assertEquals("Address", structuralProperty.getName());

        Value value = structuralProperty.getValue();
        assertTrue(value.isComplex());
        ComplexValue complexValue = (ComplexValue) value;
        validateAddressBerlin(complexValue);
        validateComplexValueNavigationLinks(complexValue);
      } else if ("Customers('MUSKI')".equals(entity.getODataId())) {
        validateEntityMuski(entity);

        StructuralProperty addressMuster = entity.getProperty("Address", StructuralProperty.class);
        assertEquals("Address", addressMuster.getName());
        validateAddressMuster((ComplexValue) addressMuster.getValue());
      } else {
        fail("Got unknown entity with id '" + entity.getODataId() + "'.");
      }
    }

    validateEntitySet(entitySet, true);
  }

  @Test
  public void iteratorTestForEntitySetWithTwoEntities() throws Exception {
    Reader consumer = new JsonReader();

    EntitySet entitySet = consumer.readEntitySet(getJson("/fullEntitySetWithTwoEntities.json"));
    assertEquals(2, entitySet.getEntities().size());
    validateEntitySet(entitySet, true);

    for (Entity entity : entitySet) {
      if ("Customers('ALFKI')".equals(entity.getODataId())) {
        validateEntityAlfki(entity);

        StructuralProperty structuralProperty = entity.getProperty("Address", StructuralProperty.class);
        assertEquals("Address", structuralProperty.getName());

        Value value = structuralProperty.getValue();
        assertTrue(value.isComplex());
        ComplexValue complexValue = (ComplexValue) value;
        validateAddressBerlin(complexValue);
        validateComplexValueNavigationLinks(complexValue);
      } else if ("Customers('MUSKI')".equals(entity.getODataId())) {
        validateEntityMuski(entity);

        StructuralProperty addressMuster = entity.getProperty("Address", StructuralProperty.class);
        assertEquals("Address", addressMuster.getName());
        validateAddressMuster((ComplexValue) addressMuster.getValue());
      } else {
        fail("Got unknown entity with id '" + entity.getODataId() + "'.");
      }
    }
  }

  @Test
  public void testEntity() throws Exception {
    Reader consumer = new JsonReader();

    Entity entity = consumer.readEntity(JsonReaderTest.class.getResourceAsStream("/fullEntity.json"));
    validateEntityAlfki(entity);

    StructuralProperty structuralProperty = entity.getProperty("Address", StructuralProperty.class);
    assertEquals("Address", structuralProperty.getName());

    Value value = structuralProperty.getValue();
    assertTrue(value.isComplex());
    ComplexValue complexValue = (ComplexValue) value;
    validateAddressBerlin(complexValue);
    validateComplexValueNavigationLinks(complexValue);
  }

  @Test
  public void testComplexProperty() throws Exception {
    Reader consumer = new JsonReader();

    Property property = consumer.readProperty(getJson("/complexProperty.json"));
    assertEquals("Address", property.getName());

    assertTrue(property instanceof StructuralProperty);
    StructuralProperty structuralProperty = (StructuralProperty) property;
    Value value = structuralProperty.getValue();
    assertTrue(value.isComplex());
    ComplexValue complexValue = (ComplexValue) value;
    validateAddressBerlin(complexValue);
    validateAddressBerlin(complexValue);
  }

  @Test
  public void testEntityWithCollectionOfComplexProperty() throws Exception {
    Reader consumer = new JsonReader();

    Entity entity = consumer.readEntity(getJson("/fullEntityWithCollectionOfComplexValues.json"));
    StructuralProperty address = entity.getProperty("Address", StructuralProperty.class);
    assertTrue(address.containsCollection());
    assertEquals("Address", address.getName());

    List<Value> values = address.getValues();
    assertEquals(2, values.size());

    Value value = values.get(0);
    assertTrue(value.isComplex());
    validateAddressBerlin((ComplexValue) value);
    //
    ComplexValue addressTwo = (ComplexValue) values.get(1);
    validateAddressMuster(addressTwo);
  }

  @Test
  public void testSetOfPrimitive() throws Exception {
    Reader consumer = new JsonReader();

    InputStream content = JsonReaderTest.class.getResourceAsStream("/setOfPrimitiveProperties.json");
    Property property = consumer.readProperty(content);
    assertEquals("EmailAddresses", property.getName());

    assertTrue(property instanceof StructuralProperty);
    StructuralProperty structuralProperty = (StructuralProperty) property;

    assertTrue(structuralProperty.containsCollection());
    Collection<Value> values = structuralProperty.getValues();
    Iterator<Value> valueIt = values.iterator();
    assertEquals("Julie@Swansworth.com", valueIt.next().getContent());
    assertEquals("Julie.Swansworth@work.com", valueIt.next().getContent());
  }

  @Test
  public void testSetOfComplex() throws Exception {
    Reader consumer = new JsonReader();

    InputStream content = JsonReaderTest.class.getResourceAsStream("/setOfComplexProperties.json");
    Property property = consumer.readProperty(content);
    assertEquals("PhoneNumbers", property.getName());
    assertTrue(property instanceof StructuralProperty);

    StructuralProperty structuralProperty = (StructuralProperty) property;
    assertTrue(structuralProperty.containsCollection());
    List<Value> values = structuralProperty.getValues();
    assertEquals(2, values.size());

    ComplexValue phoneNumberOne = (ComplexValue) values.get(0);
    assertEquals("425-555-1212", phoneNumberOne.getValue("Number").getContent());
    assertEquals("Home", phoneNumberOne.getValue("Type").getContent());
    assertEquals(null, phoneNumberOne.getValue("Carrier"));
    assertEquals(null, phoneNumberOne.getAnnotationProperties().get("odata.type"));

    ComplexValue phoneNumberTwo = (ComplexValue) values.get(1);
    assertEquals("425-555-0178", phoneNumberTwo.getValue("Number").getContent());
    assertEquals("Cell", phoneNumberTwo.getValue("Type").getContent());
    assertEquals("Sprint", phoneNumberTwo.getValue("Carrier").getContent());
    assertEquals("#Model.CellPhoneNumber",
            phoneNumberTwo.getAnnotationProperties().get("odata.type").getValue());

    // ComplexValue complex = consumer.parseComplexValue(content);
    //
    // Value value = complex.getValue("PhoneNumbers");
    // assertNotNull(value);
    // assertTrue(value.isComplex());
    // ComplexValue complexValue = (ComplexValue) value;
  }

  private void validateEntityAlfki(final Entity entity) {
    assertNotNull(entity);

    assertEquals("http://host/service/$metadata#Customers/$entity", entity.getODataContext());
    assertEquals("Customers('ALFKI')", entity.getODataId());
    assertEquals("W/\"MjAxMy0wNS0yN1QxMTo1OFo=\"", entity.getODataETag());
    assertEquals("Customers('ALFKI')", entity.getODataEditLink());

    assertNotNull(entity.getNavigationProperties());
    assertTrue(entity.getNavigationProperties().containsKey("Orders"));
    assertEquals("Customers('ALFKI')/Orders",
            entity.getNavigationProperties().get("Orders").getNavigationLink());

    assertNotNull(entity.getNavigationProperties());
    assertTrue(entity.getNavigationProperties().containsKey("Orders"));
    assertEquals("Customers('ALFKI')/Orders/$ref",
            entity.getNavigationProperties().get("Orders").getAssociationLink());

    assertNotNull(entity.getPropertyContent("ID"));
    assertEquals("ALFKI", entity.getPropertyContent("ID"));
    assertEquals("Alfreds Futterkiste", entity.getPropertyContent("CompanyName"));
    assertEquals("Maria Anders", entity.getPropertyContent("ContactName"));
    assertEquals("Sales Representative", entity.getPropertyContent("ContactTitle"));
    assertEquals("030-0074321", entity.getPropertyContent("Phone"));
    assertEquals("030-0076545", entity.getPropertyContent("Fax"));
  }

  private void validateEntityMuski(final Entity entity) {
    assertNotNull(entity);

    assertEquals("http://host/service/$metadata#Customers/$entity", entity.getODataContext());
    assertEquals("Customers('MUSKI')", entity.getODataId());
    assertEquals("W/\"MjAxMy0wNS0yN1QxMTo1OFo=\"", entity.getODataETag());
    assertEquals("Customers('MUSKI')", entity.getODataEditLink());

    assertNotNull(entity.getNavigationProperties());
    assertTrue(entity.getNavigationProperties().containsKey("Orders"));
    assertEquals("Customers('MUSKI')/Orders", entity.getNavigationProperties().get("Orders").getNavigationLink());

    assertNotNull(entity.getNavigationProperties());
    assertTrue(entity.getNavigationProperties().containsKey("Orders"));
    assertEquals("Customers('MUSKI')/Orders/$ref",
            entity.getNavigationProperties().get("Orders").getAssociationLink());

    assertNotNull(entity.getPropertyContent("ID"));
    assertEquals("MUSKI", entity.getPropertyContent("ID"));
    assertEquals("Mustermanns Futterkiste", entity.getPropertyContent("CompanyName"));
    assertEquals("Mustermann Max", entity.getPropertyContent("ContactName"));
    assertEquals("Some Guy", entity.getPropertyContent("ContactTitle"));
    assertEquals("030-002222", entity.getPropertyContent("Phone"));
    assertEquals("030-004444", entity.getPropertyContent("Fax"));
  }

  private void validateAddressMuster(final ComplexValue addressTwo) {
    assertEquals("Musterstrasse 42", addressTwo.getValue("Street").getContent());
    assertEquals("Musterstadt", addressTwo.getValue("City").getContent());
    assertEquals("SomeRegion", addressTwo.getValue("Region").getContent());
    assertEquals("D-42042", addressTwo.getValue("PostalCode").getContent());
  }

  private void validateAddressBerlin(final ComplexValue complex) {
    assertNotNull(complex);

    assertEquals("Obere Str. 57", complex.getValue("Street").getContent());
    assertEquals("Berlin", complex.getValue("City").getContent());
    assertNull(complex.getValue("Region").getContent());
    assertEquals("D-12209", complex.getValue("PostalCode").getContent());
  }

  private void validateComplexValueNavigationLinks(final ComplexValue complex) {
    assertNotNull(complex.getNavigationProperties());
    assertTrue(complex.getNavigationProperties().containsKey("Country"));
    assertEquals("Customers('ALFKI')/Address/Country",
            complex.getNavigationProperties().get("Country").getNavigationLink());

    assertNotNull(complex.getNavigationProperties());
    assertTrue(complex.getNavigationProperties().containsKey("Country"));
    assertEquals("Customers('ALFKI')/Address/Country/$ref",
            complex.getNavigationProperties().get("Country").getAssociationLink());
  }

  private void validateEntitySet(final EntitySet entitySet) {
    validateEntitySet(entitySet, true);
  }

  private void validateEntitySet(final EntitySet entitySet, final boolean validateLinks) {
    assertNotNull(entitySet);

    assertEquals("http://host/service/$metadata#Customers/$entity", entitySet.getODataContext());
    assertEquals(Long.valueOf(1), entitySet.getODataCount());
    if (validateLinks) {
      assertEquals("http://host/service/EntitySet?$skiptoken=342r89", entitySet.getODataNextLink());
      assertNull(entitySet.getODataDeltaLink());
    }
  }

  private InputStream getJson(final String filename) {
    return JsonReaderTest.class.getResourceAsStream(filename);
  }
}
