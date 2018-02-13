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
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.serialization.AtomDeserializer;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
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
  protected void assertSimilar(final String filename, final String actual) throws Exception {
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
}
