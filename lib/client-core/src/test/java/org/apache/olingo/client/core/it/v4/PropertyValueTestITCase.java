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
package org.apache.olingo.client.core.it.v4;

import static org.junit.Assert.*;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.commons.api.format.ODataValueFormat;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

public class PropertyValueTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveIntPropertyValueTest() throws EdmPrimitiveTypeException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PersonID").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    assertEquals("5", req.execute().getBody().toString());
  }

  @Test
  public void retrieveBooleanPropertyValueTest() throws EdmPrimitiveTypeException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("IsRegistered").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    assertEquals("true", req.execute().getBody().toString());
  }

  @Test
  public void retrieveStringPropertyValueTest() throws EdmPrimitiveTypeException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("FirstName").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    assertEquals("Peter", req.execute().getBody().toString());
  }

  @Test
  public void retrieveDatePropertyValueTest() {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Orders").appendKeySegment(8).appendPropertySegment("OrderDate").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    final ODataPrimitiveValue property = req.execute().getBody();
    assertEquals("2011-03-04T16:03:57Z", property.toString());
  }

  @Test
  public void retrieveDecimalPropertyValueTest() throws EdmPrimitiveTypeException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("Height").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    final ODataPrimitiveValue property = req.execute().getBody();
    assertEquals("179", property.toString());
  }

  @Test
  public void retrieveBinaryPropertyValueTest() throws IOException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PDC").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    final ODataPrimitiveValue property = req.execute().getBody();
    assertEquals("fi653p3+MklA/LdoBlhWgnMTUUEo8tEgtbMXnF0a3CUNL9BZxXpSRiD9ebTnmNR0zWPjJ"
            + "VIDx4tdmCnq55XrJh+RW9aI/b34wAogK3kcORw=", property.toString());
  }

  @Test(expected = ODataClientErrorException.class)
  public void retrieveBinaryPropertyValueTestWithAtom() throws IOException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PDC").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setAccept(ODataPubFormat.ATOM.toString(ODataServiceVersion.V40));
    req.execute().getBody();
  }

  @Test(expected = ODataClientErrorException.class)
  public void retrieveBinaryPropertyValueTestWithXML() throws IOException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("PDC").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setAccept(ODataFormat.XML.toString());
    req.execute().getBody();
  }

  @Test
  public void retrieveCollectionPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("Numbers");
    final ODataPropertyRequest req = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
    req.setFormat(ODataFormat.XML);
    final ODataProperty property = req.execute().getBody();
    assertTrue(property.getValue().isCollection());
    assertEquals("555-555-5555", property.getCollectionValue().iterator().next().asPrimitive().toString());
  }

  @Test
  public void retrieveNullPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("MiddleName").
            appendValueSegment();
    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);
    final ODataPrimitiveValue property = req.execute().getBody();
    assertTrue(StringUtils.isBlank(property.toString()));
  }
}
