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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.serialization.JsonDeserializer;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONTest extends AbstractTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  protected ContentType getODataPubFormat() {
    return ContentType.JSON;
  }

  protected ContentType getODataFormat() {
    return ContentType.JSON;
  }

  private void cleanup(final ObjectNode node) {
    if (node.has(Constants.JSON_CONTEXT)) {
      node.remove(Constants.JSON_CONTEXT);
    }
    if (node.has(Constants.JSON_ETAG)) {
      node.remove(Constants.JSON_ETAG);
    }
    if (node.has(Constants.JSON_TYPE)) {
      node.remove(Constants.JSON_TYPE);
    }
    if (node.has(Constants.JSON_EDIT_LINK)) {
      node.remove(Constants.JSON_EDIT_LINK);
    }
    if (node.has(Constants.JSON_READ_LINK)) {
      node.remove(Constants.JSON_READ_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_EDIT_LINK)) {
      node.remove(Constants.JSON_MEDIA_EDIT_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_READ_LINK)) {
      node.remove(Constants.JSON_MEDIA_READ_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      node.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }
    if (node.has(Constants.JSON_COUNT)) {
      node.remove(Constants.JSON_COUNT);
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      final String key = field.getKey();
      if (key.charAt(0) == '#'
          || key.endsWith(Constants.JSON_TYPE)
          || key.endsWith(Constants.JSON_MEDIA_EDIT_LINK)
          || key.endsWith(Constants.JSON_MEDIA_CONTENT_TYPE)
          || key.endsWith(Constants.JSON_ASSOCIATION_LINK)
          || key.endsWith(Constants.JSON_MEDIA_ETAG)) {

        toRemove.add(key);
      } else if (field.getValue().isObject()) {
        cleanup((ObjectNode) field.getValue());
      } else if (field.getValue().isArray()) {
        for (final Iterator<JsonNode> arrayItems = field.getValue().elements(); arrayItems.hasNext();) {
          final JsonNode arrayItem = arrayItems.next();
          if (arrayItem.isObject()) {
            cleanup((ObjectNode) arrayItem);
          }
        }
      }
    }
    node.remove(toRemove);
  }

  protected void assertSimilar(final String filename, final String actual) throws Exception {
    final JsonNode expected = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename)).
        replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanup(actualNode);
    assertEquals(expected, actualNode);
  }

  protected void entitySet(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilar(filename + "." + getSuffix(contentType), writer.toString());
  }

  @Test
  public void entitySets() throws Exception {
    entitySet("Customers", getODataPubFormat());
    entitySet("collectionOfEntityReferences", getODataPubFormat());
  }

  protected void entity(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntity(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    assertSimilar(filename + "." + getSuffix(contentType), writer.toString());
  }

  @Test
  public void additionalEntities() throws Exception {
    entity("entity.minimal", getODataPubFormat());
    entity("entity.primitive", getODataPubFormat());
    entity("entity.complex", getODataPubFormat());
    entity("entity.collection.primitive", getODataPubFormat());
    entity("entity.collection.complex", getODataPubFormat());
  }

  @Test
  public void entities() throws Exception {
    entity("Products_5", getODataPubFormat());
    entity("VipCustomer", getODataPubFormat());
    entity("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", getODataPubFormat());
    entity("entityReference", getODataPubFormat());
    entity("entity.withcomplexnavigation", getODataPubFormat());
    entity("annotated", getODataPubFormat());
  }

  protected void property(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).
        toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilar(filename + "." + getSuffix(contentType), writer.toString());
  }

  @Test
  public void properties() throws Exception {
    property("Products_5_SkinColor", getODataFormat());
    property("Products_5_CoverColors", getODataFormat());
    property("Employees_3_HomeAddress", getODataFormat());
    property("Employees_3_HomeAddress", getODataFormat());
  }

  @Test
  public void crossjoin() throws Exception {
    assertNotNull(client.getDeserializer(ContentType.JSON_FULL_METADATA).toEntitySet(
        getClass().getResourceAsStream("crossjoin.json")));
  }

  protected void delta(final String filename, final ContentType contentType) throws Exception {
    final Delta delta = client.getDeserializer(contentType).toDelta(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload();
    assertNotNull(delta);
    assertNotNull(delta.getDeltaLink());
    assertEquals(5, delta.getCount(), 0);

    assertEquals(1, delta.getDeletedEntities().size());
    assertTrue(delta.getDeletedEntities().get(0).getId().toASCIIString().endsWith("Customers('ANTON')"));

    assertEquals(1, delta.getAddedLinks().size());
    assertTrue(delta.getAddedLinks().get(0).getSource().toASCIIString().endsWith("Customers('BOTTM')"));
    assertEquals("Orders", delta.getAddedLinks().get(0).getRelationship());

    assertEquals(1, delta.getDeletedLinks().size());
    assertTrue(delta.getDeletedLinks().get(0).getSource().toASCIIString().endsWith("Customers('ALFKI')"));
    assertEquals("Orders", delta.getDeletedLinks().get(0).getRelationship());

    assertEquals(2, delta.getEntities().size());
    Property property = delta.getEntities().get(0).getProperty("ContactName");
    assertNotNull(property);
    assertTrue(property.isPrimitive());
    property = delta.getEntities().get(1).getProperty("ShippingAddress");
    assertNotNull(property);
    assertTrue(property.isComplex());
  }

  @Test
  public void deltas() throws Exception {
    delta("delta", getODataPubFormat());
  }

  @Test
  public void issueOLINGO390() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Exchange.Services.OData.Model.Message"));

    final ClientComplexValue toRecipient = client.getObjectFactory().
        newComplexValue("Microsoft.Exchange.Services.OData.Model.Recipient");
    toRecipient.add(client.getObjectFactory().newPrimitiveProperty("Name",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challen_olingo_client")));
    toRecipient.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challenh@microsoft.com")));
    final ClientCollectionValue<ClientValue> toRecipients = client.getObjectFactory().
        newCollectionValue("Microsoft.Exchange.Services.OData.Model.Recipient");
    toRecipients.add(toRecipient);
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("ToRecipients", toRecipients));

    final ClientComplexValue body =
        client.getObjectFactory().newComplexValue("Microsoft.Exchange.Services.OData.Model.ItemBody");
    body.add(client.getObjectFactory().newPrimitiveProperty("Content",
        client.getObjectFactory().newPrimitiveValueBuilder().
            buildString("this is a simple email body content")));
    body.add(client.getObjectFactory().newEnumProperty("ContentType",
        client.getObjectFactory().newEnumValue("Microsoft.Exchange.Services.OData.Model.BodyType", "text")));
    message.getProperties().add(client.getObjectFactory().newComplexProperty("Body", body));

    final String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON));
    final JsonNode expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo390.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
  }
  
  @Test
  public void issue1OLINGO1073() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Exchange.Services.OData.Model.Entity"));
    
    final ClientComplexValue complType1 = client.getObjectFactory().
        newComplexValue("Microsoft.Exchange.Services.OData.Model.ComplexType1");
    complType1.add(client.getObjectFactory().newPrimitiveProperty("Name1",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challen_olingo_client")));
    complType1.add(client.getObjectFactory().newPrimitiveProperty("Address1",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challenh@microsoft.com")));

    final ClientComplexValue complType2 = client.getObjectFactory().
        newComplexValue("Microsoft.Exchange.Services.OData.Model.ComplexType2");
    complType2.add(client.getObjectFactory().newPrimitiveProperty("Name2",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challen_olingo_client")));
    complType2.add(client.getObjectFactory().newPrimitiveProperty("Address2",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challenh@microsoft.com")));
    final ClientCollectionValue<ClientValue> toRecipients = client.getObjectFactory().
        newCollectionValue("Microsoft.Exchange.Services.OData.Model.Recipient");
    toRecipients.add(complType1);
    toRecipients.add(complType2);
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("ToRecipients", toRecipients));


    final String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON));
    final JsonNode expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo1073.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
  }
  
  @Test
  public void issue2OLINGO1073() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"));
    
    final ClientComplexValue cityComplexType = getCityComplexType();
    
    final ClientComplexValue locationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    locationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln.")));
    locationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));

    final ClientComplexValue eventLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.EventLocation");
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("BuildingInfo",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientComplexValue airportLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.AirportLocation");
    airportLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln123.")));
    airportLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientCollectionValue<ClientValue> collectionAddressInfo = client.getObjectFactory().
        newCollectionValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    collectionAddressInfo.add(locationComplexType);
    collectionAddressInfo.add(eventLocationComplexType);
    collectionAddressInfo.add(airportLocationComplexType);
    
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("UserName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("russellwhyte")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("FirstName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("LastName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Whyte")));
    
    final ClientCollectionValue<ClientValue> emailCollectionValue = client.getObjectFactory().
        newCollectionValue("String");
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@example.com"));
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@contoso.com"));
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("Emails", emailCollectionValue));
    
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("AddressInfo", collectionAddressInfo));
    message.getProperties().add(client.getObjectFactory().newEnumProperty("Gender", 
        client.getObjectFactory().newEnumValue("Microsoft.OData.SampleService.Models.TripPin.PersonGender", "Male")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Concurrency", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf("636293755917400747"))));
    message.setId(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    message.setETag("W/\"08D491CCBE417AAB\"");
    message.setEditLink(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));

    final String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON));
    final JsonNode expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo1073_1.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
  }
  

  /**
   * @return ClientComplexValue
   */
  private ClientComplexValue getCityComplexType() {
    final ClientComplexValue cityComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.City");
    cityComplexType.add(client.getObjectFactory().newPrimitiveProperty("CountryRegion",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("United States")));
    cityComplexType.add(client.getObjectFactory().newPrimitiveProperty("Name",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Boise")));
    cityComplexType.add(client.getObjectFactory().newPrimitiveProperty("Region",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("ID")));
    return cityComplexType;
  }
  
  @Test
  public void issue3OLINGO1073() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"));
    
    final ClientComplexValue cityComplexType = getCityComplexType();
    
    final ClientComplexValue locationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    locationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln.")));
    locationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));

    final ClientComplexValue eventLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.EventLocation");
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("BuildingInfo",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientComplexValue airportLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.AirportLocation");
    airportLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln123.")));
    airportLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientCollectionValue<ClientValue> collectionAddressInfo = client.getObjectFactory().
        newCollectionValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    collectionAddressInfo.add(locationComplexType);
    collectionAddressInfo.add(eventLocationComplexType);
    collectionAddressInfo.add(airportLocationComplexType);
    
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("UserName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("russellwhyte")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("FirstName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("LastName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Whyte")));
    
    final ClientCollectionValue<ClientValue> emailCollectionValue = client.getObjectFactory().
        newCollectionValue("String");
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@example.com"));
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@contoso.com"));
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("Emails", emailCollectionValue));
    
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("AddressInfo", collectionAddressInfo));
    message.getProperties().add(client.getObjectFactory().newEnumProperty("Gender", 
        client.getObjectFactory().newEnumValue(
            "Microsoft.OData.SampleService.Models.TripPin.PersonGender", "Male")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Concurrency", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf("636293755917400747"))));
    message.setId(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    message.setETag("W/\"08D491CCBE417AAB\"");
    message.setEditLink(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));

    InputStream inputStream = client.getWriter().writeEntity(message, ContentType.APPLICATION_JSON);
    ResWrap<Entity> entity = new JsonDeserializer(true).toEntity(inputStream);
    assertNotNull(entity);
    assertEquals(7, entity.getPayload().getProperties().size());
    assertEquals(3, entity.getPayload().getProperty("AddressInfo").asCollection().size());
    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Location", 
        ((ComplexValue)entity.getPayload().getProperty("AddressInfo").asCollection().get(0)).getTypeName());
    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.EventLocation", 
        ((ComplexValue)entity.getPayload().getProperty("AddressInfo").asCollection().get(1)).getTypeName());
    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.AirportLocation", 
        ((ComplexValue)entity.getPayload().getProperty("AddressInfo").asCollection().get(2)).getTypeName());
    assertEquals("Collection(Microsoft.OData.SampleService.Models.TripPin.Location)", 
        entity.getPayload().getProperty("AddressInfo").getType());
  }
  
  @Test
  public void issue4OLINGO1073_WithAnnotations() throws Exception {
    InputStream inputStream = getClass().getResourceAsStream(
        "olingo1073_2" + "." + getSuffix(ContentType.APPLICATION_JSON));
    ClientEntity entity = client.getReader().readEntity(inputStream, ContentType.APPLICATION_JSON);
    assertNotNull(entity);
    assertEquals(7, entity.getProperties().size());
    assertEquals(1, entity.getAnnotations().size());
    assertEquals("com.contoso.PersonalInfo.PhoneNumbers", entity.getAnnotations().get(0).getTerm());
    assertEquals(2, entity.getAnnotations().get(0).getCollectionValue().size());
    
    assertEquals("com.contoso.display.style", entity.getProperty("LastName").
        getAnnotations().get(0).getTerm());
    assertEquals(2, entity.getProperty("LastName").
        getAnnotations().get(0).getComplexValue().asComplex().asJavaMap().size());
    
    assertEquals(3, entity.getProperty("AddressInfo").getCollectionValue().asCollection().size());
    assertEquals("Collection(Microsoft.OData.SampleService.Models.TripPin.Location)", 
        entity.getProperty("AddressInfo").getCollectionValue().asCollection().getTypeName());
    assertEquals(true, entity.getProperty("AddressInfo").getCollectionValue().isCollection());
    ClientCollectionValue<ClientValue> collectionValue = entity.getProperty("AddressInfo").
        getCollectionValue().asCollection();
    int i = 0;
    for (ClientValue _value : collectionValue) {
      if (i == 0) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Location", _value.getTypeName());
        assertEquals(2, _value.asComplex().asJavaMap().size());
        assertEquals("Microsoft.OData.SampleService.Models.TripPin.City", 
            _value.asComplex().get("City").getComplexValue().getTypeName());
      } else if (i == 1) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.EventLocation", _value.getTypeName());
        assertEquals(3, _value.asComplex().asJavaMap().size());
        assertEquals("com.contoso.display.style", _value.asComplex().get("Address").getAnnotations().get(0).getTerm());
        assertEquals(2, _value.asComplex().get("Address").getAnnotations().get(0).getComplexValue().asJavaMap().size());
      } else if (i == 2) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.AirportLocation", _value.getTypeName());
        assertEquals(3, _value.asComplex().asJavaMap().size());
      }
      i++;
    }
  }
}
