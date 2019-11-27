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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientOperation;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.domain.ClientAnnotationImpl;
import org.apache.olingo.client.core.serialization.AtomDeserializer;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.custommonkey.xmlunit.Diff;
import org.junit.Test;

public class AtomTest extends JSONTest {

  @Override
  protected ContentType getODataPubFormat() {
    return ContentType.APPLICATION_ATOM_XML;
  }

  @Override
  protected ContentType getODataFormat() {
    return ContentType.APPLICATION_XML;
  }

  private String cleanup(final String input) throws Exception {
    final TransformerFactory factory = TransformerFactory.newInstance();
    final Source xslt = new StreamSource(getClass().getResourceAsStream("atom_cleanup.xsl"));
    final Transformer transformer = factory.newTransformer(xslt);

    final StringWriter result = new StringWriter();
    transformer.transform(new StreamSource(new ByteArrayInputStream(input.getBytes())), new StreamResult(result));
    return result.toString();
  }

  @Override
  protected void assertSimilar(final String filename, final String actual, 
      boolean isServerMode) throws Exception {
    final Diff diff = new Diff(cleanup(IOUtils.toString(getClass().getResourceAsStream(filename))), actual);
    diff.overrideElementQualifier(new AtomLinksQualifier());
    assertTrue(diff.similar());
  }

  @Override
  public void additionalEntities() throws Exception {
    // no test
  }

  @Override
  public void issueOLINGO390() throws Exception {
    // no test
  }

  @Test
  public void issue1OLINGO1073() throws Exception {
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

    String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.APPLICATION_ATOM_XML));
    actual = actual.substring(actual.indexOf("<entry"));
    assertNotNull(actual);
    String expected = IOUtils.toString(getClass().getResourceAsStream("olingo1073_1.xml"));
    expected = expected.substring(expected.indexOf("<entry"));
    expected = expected.trim().replace("\n", "").replace("\r", "").replace("\t", "");
    assertEquals(expected, actual);
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
        client.getObjectFactory().newEnumValue(
            "Microsoft.OData.SampleService.Models.TripPin.PersonGender", "Male")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Concurrency", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf("636293755917400747"))));
    message.setId(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    message.setETag("W/\"08D491CCBE417AAB\"");
    message.setEditLink(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));

    InputStream inputStream = client.getWriter().writeEntity(message, ContentType.APPLICATION_ATOM_XML);
    ResWrap<Entity> entity = new AtomDeserializer().toEntity(inputStream);
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
  public void issue3OLINGO1073_WithAnnotations() throws Exception {
    InputStream inputStream = getClass().getResourceAsStream(
        "olingo1073_2" + "." + getSuffix(ContentType.APPLICATION_ATOM_XML));
    ClientEntity entity = client.getReader().readEntity(inputStream, ContentType.APPLICATION_ATOM_XML);
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
      } else if (i == 2) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.AirportLocation", _value.getTypeName());
        assertEquals(3, _value.asComplex().asJavaMap().size());
      }
      i++;
    }
  }
  
  @Test
  public void issue2OLINGO1073_WithEntitySet() throws Exception {
    final ClientEntity message = createClientEntity();
    
    InputStream inputStream = client.getWriter().writeEntity(message, ContentType.APPLICATION_ATOM_XML);
    ResWrap<Entity> entity = new AtomDeserializer().toEntity(inputStream);
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
    
    StringWriter writer = new StringWriter();
    setNavigationBindingLinkOnEntity(entity);
    
    client.getSerializer(ContentType.APPLICATION_ATOM_XML).write(writer, entity);
    assertNotNull(writer.toString());
    writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_ATOM_XML).write(writer, 
        new ResWrap<URI>(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/"), null, 
            URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')")));
    assertNotNull(writer.toString());
    assertEquals("<?xml version='1.0' "
        + "encoding='UTF-8'?>"
        + "<metadata:ref xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "metadata:context=\"http://services.odata.org/V4/"
        + "(S(fe5rsnxo3fkkkk2bvmh1nl1y))/TripPinServiceRW/\" "
        + "id=\"http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))"
        + "/TripPinServiceRW/People(&apos;russellwhyte&apos;)\"/>", writer.toString());
    
    writer = new StringWriter();
    Link linkPayload = new Link();
    linkPayload.setBindingLink("Photos");
    linkPayload.setMediaETag("xyz");
    linkPayload.setInlineEntity(createEntity());
    linkPayload.setTitle("Photos");
    linkPayload.setHref("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/Photos");
    client.getSerializer(ContentType.APPLICATION_ATOM_XML).write(writer, 
        new ResWrap<Link>(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/"), linkPayload.getMediaETag(), linkPayload));
    assertNotNull(writer.toString());
    assertEquals("<?xml version='1.0' encoding='UTF-8'?>"
        + "<links xmlns=\"http://docs.oasis-open.org/odata/ns/data\">"
        + "<uri>http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/Photos</uri></links>", writer.toString());
  }

  /**
   * @return
   */
  private ClientEntity createClientEntity() {
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
    final ClientLink messageLink1 = client.getObjectFactory().newEntityNavigationLink("Photo", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Photo"));
    final ClientAnnotation messageLink1Annotation = createAnnotation();
    messageLink1.getAnnotations().add(messageLink1Annotation);
    
    final ClientLink messageLink2 = client.getObjectFactory().newEntitySetNavigationLink("Friends", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Friends"));
    final ClientAnnotation messageLink2Annotation = createAnnotation();
    messageLink2.getAnnotations().add(messageLink2Annotation);
    
    final ClientLink messageLink3 = client.getObjectFactory().newEntitySetNavigationLink("Trips", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Trips"));
    final ClientAnnotation messageLink3Annotation = createAnnotation();
    messageLink3.getAnnotations().add(messageLink3Annotation);
        
    message.getNavigationLinks().add(messageLink1);
    message.getNavigationLinks().add(messageLink2);
    message.getNavigationLinks().add(messageLink3);
    
    final ClientAnnotation messageAnnotation = createAnnotation();
    message.getAnnotations().add(messageAnnotation);
    
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
    
    final ClientEntity innerEntity = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Photo"));
    innerEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Id", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf(123))));
    innerEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Name", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("ABC")));
    innerEntity.getAnnotations().add(createAnnotation());
    final ClientLink link = client.getObjectFactory().newDeepInsertEntity("Photos", innerEntity);
    final ClientAnnotation linkAnnotation = createAnnotation();
    link.getAnnotations().add(linkAnnotation);
    message.getNavigationLinks().add(link);
    
    final ClientLink assoLink = client.getObjectFactory().newAssociationLink("Photos", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Photo"));
    final ClientAnnotation assoLinkAnnotation = createAnnotation();
    assoLink.getAnnotations().add(assoLinkAnnotation);

    message.getAssociationLinks().add(assoLink);
    final ClientOperation operation = new ClientOperation();
    operation.setTarget(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/Photos"));
    operation.setTitle("Photos");
    message.getOperations().add(operation);
    return message;
  }

  /**
   * @param entity
   */
  private void setNavigationBindingLinkOnEntity(ResWrap<Entity> entity) {
    Link entityLink = new Link();
    Entity en = createEntity();
    
    entityLink.setBindingLink("Photos");
    entityLink.setInlineEntity(en);
    entityLink.setType("Microsoft.OData.SampleService.Models.TripPin.Photos");
    
    Link entityColLink = new Link();
    EntityCollection enCol = new EntityCollection();
    enCol.getEntities().add(en);
    
    entityColLink.setBindingLink("Friends");
    entityColLink.setInlineEntitySet(enCol);
    entityColLink.setType("Microsoft.OData.SampleService.Models.TripPin.Friends");
    
    Link link = new Link();
    link.setBindingLink("Trips");
    link.setType("Microsoft.OData.SampleService.Models.TripPin.Trips");
    
    entity.getPayload().getNavigationBindings().add(entityLink);
    entity.getPayload().getNavigationBindings().add(entityColLink);
    entity.getPayload().getNavigationBindings().add(link);
  }

  /**
   * @return
   */
  private Entity createEntity() {
    Entity en = new Entity();
    Property p1 = new Property();
    p1.setName("Id");
    p1.setType("Int64");
    p1.setValue(ValueType.PRIMITIVE, Long.valueOf(123));
    en.addProperty(p1);
    
    Property p2 = new Property();
    p2.setName("Name");
    p2.setType("String");
    p2.setValue(ValueType.PRIMITIVE, "ABC");
    en.addProperty(p2);
    return en;
  }

  /**
   * @return
   */
  private ClientAnnotation createAnnotation() {
    final ClientAnnotation messageAnnotation = 
        new ClientAnnotationImpl("Org.OData.Core.V1.Permissions", new ClientPrimitiveValue() {
      
      @Override
      public boolean isPrimitive() {
        return false;
      }
      
      @Override
      public boolean isEnum() {
        return true;
      }
      
      @Override
      public boolean isComplex() {
        return false;
      }
      
      @Override
      public boolean isCollection() {
        return false;
      }
      
      @Override
      public String getTypeName() {
        return "String";
      }
      
      @Override
      public ClientPrimitiveValue asPrimitive() {
        return null;
      }
      
      @Override
      public ClientEnumValue asEnum() {
        return client.getObjectFactory().newEnumValue("Org.OData.Core.V1.Permissions", "Read");
      }
      
      @Override
      public ClientComplexValue asComplex() {
        return null;
      }
      
      @Override
      public <T extends ClientValue> ClientCollectionValue<T> asCollection() {
        return null;
      }
      
      @Override
      public Object toValue() {
        return client.getObjectFactory().newEnumValue("Org.OData.Core.V1.Permissions", "Read");
      }
      
      @Override
      public <T> T toCastValue(Class<T> reference) throws EdmPrimitiveTypeException {
        return null;
      }
      
      @Override
      public EdmPrimitiveTypeKind getTypeKind() {
        return null;
      }
      
      @Override
      public EdmPrimitiveType getType() {
        return null;
      }
    });
    return messageAnnotation;
  }
  
  @Test
  public void testEntitySet() throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_ATOM_XML).write(writer, 
        client.getDeserializer(ContentType.APPLICATION_ATOM_XML).toEntitySet(
        getClass().getResourceAsStream("Customers.xml")));

    assertNotNull(writer.toString());
  }
  
  private void property(String fileName) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_ATOM_XML).write(writer, 
        client.getDeserializer(ContentType.APPLICATION_ATOM_XML).
        toProperty(getClass().getResourceAsStream(fileName)));

    assertNotNull(writer.toString());
  }
  
  @Test
  public void testProperties() throws Exception {
    property("Products_5_SkinColor.xml");
    property("Products_5_CoverColors.xml");
    property("Employees_3_HomeAddress.xml");
    property("Employees_3_HomeAddress.xml");
  }
  
  protected void delta(final String filename) throws Exception {
    ResWrap<Delta> resDelta = client.getDeserializer(ContentType.APPLICATION_ATOM_XML).toDelta(
        getClass().getResourceAsStream(filename));
    final Delta delta = resDelta.getPayload();
    
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
    delta("delta.xml");
  }
}
