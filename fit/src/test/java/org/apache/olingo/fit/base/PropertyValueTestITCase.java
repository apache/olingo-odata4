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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class PropertyValueTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveIntPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PersonID");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    assertEquals("5", req.execute().getBody().toString());
  }

  @Test
  public void retrieveBooleanPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("IsRegistered");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    assertEquals("true", req.execute().getBody().toString());
  }

  @Test
  public void retrieveStringPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("FirstName");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    assertEquals("Peter", req.execute().getBody().toString());
  }

  @Test
  public void retrieveDatePropertyValueTest() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Orders").appendKeySegment(8).appendPropertySegment("OrderDate");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    final ClientPrimitiveValue property = req.execute().getBody();
    assertEquals("2011-03-04T16:03:57Z", property.toString());
  }

  @Test
  public void retrieveDecimalPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("Height");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    final ClientPrimitiveValue property = req.execute().getBody();
    assertEquals("179", property.toString());
  }

  @Test
  public void retrieveBinaryPropertyValueTest() throws IOException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PDC");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    final ClientPrimitiveValue property = req.execute().getBody();
    assertEquals("fi653p3+MklA/LdoBlhWgnMTUUEo8tEgtbMXnF0a3CUNL9BZxXpSRiD9ebTnmNR0zWPjJ"
        + "VIDx4tdmCnq55XrJh+RW9aI/b34wAogK3kcORw=", property.toString());
  }

  @Test(expected = ODataClientErrorException.class)
  public void retrieveBinaryPropertyValueTestWithAtom() throws IOException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PDC");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setAccept(ContentType.APPLICATION_ATOM_XML.toContentTypeString());
    req.execute().getBody();
  }

  @Test(expected = ODataClientErrorException.class)
  public void retrieveBinaryPropertyValueTestWithXML() throws IOException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PDC");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setAccept(ContentType.APPLICATION_XML.toContentTypeString());
    req.execute().getBody();
  }

  @Test
  public void retrieveCollectionPropertyValueTest() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("Numbers");
    final ODataPropertyRequest<ClientProperty> req = client.getRetrieveRequestFactory().
        getPropertyRequest(uriBuilder.build());
    req.setFormat(ContentType.APPLICATION_XML);
    final ClientProperty property = req.execute().getBody();
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertTrue(((ClientValuable) property).getValue().isCollection());
    assertEquals("555-555-5555", property.getCollectionValue().iterator().next().asPrimitive().toString());
  }

  @Test
  public void retrieveNullPropertyValueTest() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("HomeAddress");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    final ClientPrimitiveValue property = req.execute().getBody();
    assertTrue(StringUtils.isBlank(property.toString()));
  }
}
