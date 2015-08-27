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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.LinkedHashMap;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class FilterSystemQueryITCase extends AbstractBaseTestITCase {

  private static final String ES_COMP_ALL_PRIM = "ESCompAllPrim";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ES_ALL_PRIM = "ESAllPrim";

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    // Nothing here.
  }

  @Test
  public void testTimeOfDayLiteral() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "PropertyTimeOfDay eq 03:26:05");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }
  
  @Test
  public void testBooleanLiteral() {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyBoolean eq false");
    assertEquals(2, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    clientEntity = response.getBody().getEntities().get(1);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    response = sendRequest(ES_ALL_PRIM, "PropertyBoolean eq true");
    assertEquals(1, response.getBody().getEntities().size());

    clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDateLiteral() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "PropertyDate eq 2012-12-03");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDateTimeOffsetLiteral() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:23Z");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testInt64Literal() {
    long value = Integer.MAX_VALUE + 1L;
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 gt " + value);
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDoubleLiteral() {
    Double value = -17900000000000000000.0;

    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDouble le " + value.toString());
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testSimpleEq() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq 1");

    assertEquals(2, result.getBody().getEntities().size());
    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testBinaryIntegerOperations() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add 1 eq (1 sub 3) div 2 mul 3 add 7");

    assertEquals(1, result.getBody().getEntities().size());
    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testClientEscaping() {
    final ODataClient client = getClient();
    final String filter = client.getFilterFactory().eq(
        client.getFilterFactory().getArgFactory().property("PropertyString"),
        client.getFilterFactory().getArgFactory().literal("First Resource - positive values")).build();

    final URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .filter(filter)
        .build();

    final ODataRetrieveResponse<ClientEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri).execute();

    assertEquals(1, response.getBody().getEntities().size());
    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testStringProperty() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2'");

    assertEquals(1, result.getBody().getEntities().size());
    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testBooleanOperator() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2' and PropertyInt16 eq 1");
    assertEquals(1, result.getBody().getEntities().size());
    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2' or PropertyInt16 eq 1");
    assertEquals(2, result.getBody().getEntities().size());
    clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testBooleanOperatorWithNull() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq null");
    assertEquals(0, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "null eq null");
    assertEquals(4, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "null ne null");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testUnaryWithNullLiterals() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyComp/PropertyComp/PropertyBoolean eq not null");
    assertEquals(0, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyComp/PropertyComp/PropertyBoolean eq 0 add -(5 add null)");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testUnaryWithWrongTypes() {
    fail(ES_ALL_PRIM, "PropertyInt16 eq 6 add - 'test'", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "PropertyBoolean eq not 'test'", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void testMethodCallsWithNull() {
    // One representative of "stringFuntion" "residue class"
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "endswith(PropertyString, null) eq null"); // null eq null => true
    assertEquals(3, result.getBody().getEntities().size());

    // One representative of "stringifiedValueFunction" "residue class"
    result = sendRequest(ES_ALL_PRIM, "substring(PropertyString, null) eq null"); // null eq null => true
    assertEquals(3, result.getBody().getEntities().size());

    // Substring
    result = sendRequest(ES_ALL_PRIM, "hour(null) eq null"); // null eq null => true
    assertEquals(3, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "substring(PropertyString, 0, null) eq null"); // null eq null => true
    assertEquals(3, result.getBody().getEntities().size());
  }

  @Test
  public void testSubstringWithNegativeValues() {
    // See OASIS JIRA ODATA-781

    // -1 should be treated as 0
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "substring(PropertyString, -1, 1) eq 'F'");
    assertEquals(1, response.getBody().getEntities().size());
    assertShortOrInt(32767, response.getBody().getEntities().get(0).getProperty("PropertyInt16")
          .getPrimitiveValue().toValue());

    // -1 should be treated as 0, Same values substring(PropertyString, 0, 0) returns the empty String
    response = sendRequest(ES_ALL_PRIM, "substring(PropertyString, 0, -1) eq ''");
    assertEquals(3, response.getBody().getEntities().size());
  }

  @Test
  public void testUnknownLiteral() {
    // Check if the error code is equals to 400
    fail(ES_ALL_PRIM, "PropertyInt16 eq ThisIsNotAValidLiteral", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void testErrorCodeArithmetic() {
    fail(ES_ALL_PRIM, "PropertyInt16 eq 'hey' add 5", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "PropertyDate eq 5.0 add 2012-12-03", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "PropertyDouble mod 5 eq 0", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "UnkownProperty eq null", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void testNumericBinaryOperationWithNullValues() {
    // Create new Entries
    final String filterString = "PropertyString eq null";

    ODataClient client = getClient();
    ClientObjectFactory objectFactory = client.getObjectFactory();

    ClientEntity entity = objectFactory.newEntity(new FullQualifiedName("olingo.odata.test1.ETAllPrim"));

    entity.getProperties().add(
        objectFactory.newPrimitiveProperty("PropertyInt16", objectFactory.newPrimitiveValueBuilder()
            .buildInt16((short) 1)));
    entity.addLink(objectFactory.newEntityNavigationLink("NavPropertyETTwoPrimOne",
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoPrim")
        .appendKeySegment(32766)
        .build()));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    ODataEntityCreateResponse<ClientEntity> createResponse =
        client.getCUDRequestFactory().getEntityCreateRequest(uri, entity).execute();

    ODataRetrieveResponse<ClientEntitySet> filterResponse = sendRequest(ES_ALL_PRIM, filterString,
        createResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    assertEquals(1, filterResponse.getBody().getEntities().size());
  }

  @Test
  public void numericComparisonOperators() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 ge 1");
    assertEquals(4, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt 1");
    assertEquals(2, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 lt 2");
    assertEquals(2, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 le 2");
    assertEquals(3, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble ge -179000");
    assertEquals(2, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble gt -179000");
    assertEquals(1, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble lt -179000");
    assertEquals(1, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble le -179000");
    assertEquals(2, result.getBody().getEntities().size());
  }

  @Test
  public void testBinaryOperationIntegerDecimalWithPromotion() {
    String filterString = ""
        + "PropertyInt16 mod 2 eq " // Choose mod 2 == 1 => { 1, 3, .. }
        + "(((5 sub 1) div 5) " // Integer Division 4 / 5 == 0
        + "add 1) " // 0 + 1 = 1
        + "and "
        + "PropertyComp/PropertyInt16 eq " // Complex Property
        + "5.5 mul 2"; // Single * Int16 => Single => Int16 eq Single => Single eq Single

    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, filterString);
    assertEquals(3, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
    assertShortOrInt(11, clientEntity.getProperty("PropertyComp").getComplexValue().get("PropertyInt16")
        .getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
    assertShortOrInt(11, clientEntity.getProperty("PropertyComp").getComplexValue().get("PropertyInt16")
        .getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(2);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
    assertShortOrInt(11, clientEntity.getProperty("PropertyComp").getComplexValue().get("PropertyInt16")
        .getPrimitiveValue().toValue());
  }

  @Test
  public void testNotOperator() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "not (PropertyInt16 eq 1)");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testUnaryMinusOperator() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt -2 add - -3");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testUnaryMinusOperatorDecimal() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt -2.0 add - -3.0");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testStringPropertyEqualsNull() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq null");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testAddNullLiteral() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add null eq 1");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testAddNullLiteralEqualsNull() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add null eq null");
    assertEquals(4, result.getBody().getEntities().size());
  }

  @Test
  public void testSubstringStartAndEndGiven() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "substring(PropertyString, length('First') add 1, 8) eq ('Resource')");

    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testSubstringStartGiven() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "substring(PropertyComp/PropertyComp/PropertyString, 6) eq 'Value'");

    assertEquals(4, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(1, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(2);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(3);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testSubstringDouble() {
    fail(ES_ALL_PRIM,
        "substring(PropertyString, length('First') add 1, 2.0 * 4) eq ('Resource')",
        HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void testYearFunctionDate() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "year(PropertyDate) eq 2015");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }
  
  void assertShortOrInt(int value, Object n) {
    if (n instanceof Number) {
      assertEquals(value, ((Number)n).intValue());
    } else {
      Assert.fail();
    }
  }

  @Test
  public void testYearFunctionDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "year(PropertyDateTimeOffset) eq 2012");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testMonthFunctionDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "month(PropertyDateTimeOffset) eq 12");
    assertEquals(3, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(2);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testMonthFunctionDate() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "month(PropertyDate) eq 11");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDayFunctionDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "day(PropertyDateTimeOffset) eq 3");
    assertEquals(3, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(2);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDayFunctionDate() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "day(PropertyDate) eq 5");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testHourFunctionDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "hour(PropertyDateTimeOffset) eq 7");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testHourFuntionTimeOfDay() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "hour(PropertyTimeOfDay) eq 3");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testMinuteFunctionDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "minute(PropertyDateTimeOffset) eq 17");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testMinuteFuntionTimeOfDay() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "minute(PropertyTimeOfDay) eq 49");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testSecondFunctionDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "second(PropertyDateTimeOffset) eq 8");
    assertEquals(1, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testSecondFuntionTimeOfDay() {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "second(PropertyTimeOfDay) eq 14");
    assertEquals(1, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testFractionalsecondsDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_COMP_ALL_PRIM, "fractionalseconds(PropertyComp/PropertyDateTimeOffset) eq 0.1234567");
    assertEquals(2, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("W/\"32767\"", clientEntity.getETag());

    clientEntity = response.getBody().getEntities().get(1);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("W/\"0\"", clientEntity.getETag());
  }

  @Test
  public void testFractionalsecondsDateOfTime() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "fractionalseconds(PropertyTimeOfDay) eq 0");
    assertEquals(3, response.getBody().getEntities().size());
  }

  @Test
  public void testDateTimeFunctionsNull() {
    ODataRetrieveResponse<ClientEntitySet> response;

    response = sendRequest(ES_ALL_PRIM, "year(null) eq null");
    assertEquals(3, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "month(null) eq null");
    assertEquals(3, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "day(null) eq null");
    assertEquals(3, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "hour(null) eq null");
    assertEquals(3, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "minute(null) eq null");
    assertEquals(3, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "second(null) eq null");
    assertEquals(3, response.getBody().getEntities().size());
  }

  @Test
  public void testFloor() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq floor(3.8)");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq floor(3.1)");
    assertEquals(1, result.getBody().getEntities().size());

    clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testCeiling() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq ceiling(2.1)");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq ceiling(2.6)");
    assertEquals(1, result.getBody().getEntities().size());

    clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testRound() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(2.5)");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(2.4)");
    assertEquals(1, result.getBody().getEntities().size());

    clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(2.6)");
    assertEquals(1, result.getBody().getEntities().size());

    clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(3.1)");
    assertEquals(1, result.getBody().getEntities().size());

    clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void testEndsWith() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "endswith(PropertyString, 'values')");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testIndexOf() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "indexof(PropertyString, 'positive') eq 17");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testStartsWith() {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "startswith(PropertyString, 'First')");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testToLower() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "contains(PropertyString, tolower('POSITIVE'))");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testToUpper() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "contains(PropertyString, concat(toupper('f'), 'irst'))");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testTrim() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "trim(substring(PropertyString, 0, 6)) eq 'First'");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDecimalDiv() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDouble eq 0 sub (358000 div 2)");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testNumericPromotionToInt64() {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 eq 0");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void castEdm64ToDouble() {
    double value = Float.MAX_VALUE + 1;
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 lt " + value);
    assertEquals(3, result.getBody().getEntities().size());
  }

  @Test
  public void testDateTimeOffsetAddDuraton() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:19Z add duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDurrationAddDuration() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq duration'PT2S' add duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDurrationLiteral() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq duration'P1DT'");
    assertEquals(0, response.getBody().getEntities().size());
  }

  @Test
  public void testDateAddDuration() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-02 add duration'P1DT7H16M23S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDateTimeOffsetSubDuration() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:27Z sub duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDurrationSubDuration() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration sub duration'PT2S' eq duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDateSubDuration() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-04 sub duration'P0DT16H43M37S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDateSubDate() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq 2012-12-04 sub 2012-12-04");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testDateTimeOffsetSubDateTimeOffset() {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq 2005-12-03T00:00:00Z sub 2005-12-03T00:00:00Z");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void testNumericPromotion() {
    /*
     * The idea is use the largest possible number of a specific type and add a another number to force an
     * implicit conversion to an higher type
     */
    ODataRetrieveResponse<ClientEntitySet> response;

    // SByte => Int16
    byte byteValue = Byte.MAX_VALUE; // 2 ^ 7 -1 = 127
    response = sendRequest(ES_ALL_PRIM, "PropertyInt32 eq " + byteValue + " add " + 1);
    assertEquals(0, response.getBody().getEntities().size()); // No error occurs

    // Int16 => Int32
    short shortValue = Short.MAX_VALUE;
    response = sendRequest(ES_ALL_PRIM, "PropertyInt32 eq " + shortValue + " add " + 1);
    assertEquals(0, response.getBody().getEntities().size()); // No error occurs

    // Int32 => Int64
    int intValue = Integer.MAX_VALUE;
    response = sendRequest(ES_ALL_PRIM, "PropertyInt32 eq " + intValue + " add " + 1);
    assertEquals(0, response.getBody().getEntities().size()); // No error occurs

    // Int64 => Double
    Long longValue = Long.MAX_VALUE;
    response = sendRequest(ES_ALL_PRIM, "PropertyInt32 eq " + longValue + " add " + 1);
    assertEquals(0, response.getBody().getEntities().size()); // No error occurs

    // Single => Double
    Float floatValue = Float.MAX_VALUE;
    response = sendRequest(ES_ALL_PRIM, "PropertyInt32 eq " + floatValue.toString() + " add " + 1.0);
    assertEquals(0, response.getBody().getEntities().size()); // No error occurs
  }

  @Test
  public void testNullComplexProperty() {
    // Create a new entry.The complex property PropertyCompComp is set to null. So the structure of the property
    // is still there, but filled is null values (primitive types)
    // We define a filter, which returns all entry where PropertyCompComp/PropertyComp/PropertyInt16 is equals to 1

    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    newEntity.getProperties().add(factory.newComplexProperty("PropertyCompCompNav", null));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt16",
        factory.newPrimitiveValueBuilder().buildInt16((short) 4)));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("Test")));
    newEntity.getProperties().add(
        factory.newComplexProperty("PropertyCompAllPrim",
            factory.newComplexValue("CTAllPrim")
            .add(factory.newPrimitiveProperty(
                "PropertyString",
                factory.newPrimitiveValueBuilder().buildString("Test 3")))));

    newEntity.getProperties().add(
        factory.newComplexProperty("PropertyCompTwoPrim",
            factory.newComplexValue("CTTwoPrim")
            .add(factory.newPrimitiveProperty(
                "PropertyInt16",
                factory.newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(factory.newPrimitiveProperty(
                    "PropertyString",
                    factory.newPrimitiveValueBuilder().buildString("Test2")))));

    newEntity.addLink(factory.newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 1L;

          {
            put("PropertyInt16", 1);
            put("PropertyString", "1");
          }
        })
        .build()));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESKeyNav").build();
    ODataEntityCreateRequest<ClientEntity> request =
        client.getCUDRequestFactory().getEntityCreateRequest(uri, newEntity);
    ODataEntityCreateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());

    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    // Do the filter request
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest("ESKeyNav", "PropertyCompCompNav/PropertyCompNav/PropertyInt16 eq 1", cookie);
    assertEquals(3, result.getBody().getEntities().size());

    // Try filter all entries where PropertyCompComp is null
    result = sendRequest("ESKeyNav", "PropertyCompCompNav/PropertyCompNav/PropertyInt16 eq null", cookie);
    assertEquals(1, result.getBody().getEntities().size());
  }
  
  @Test
  public void testFilterNotBooleanExpression() {
    // Check that only boolean expression are allowed
    fail("ESAllPrim", "PropertytString", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt32", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt64", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyDate", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyDuation", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyTimeOfDay", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyByte", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyDouble", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertySingle", HttpStatusCode.BAD_REQUEST);
  }
  
  @Test
  public void testComparisonOnStringOperands() {
    // If check if the expression is true => All entry are returned
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "'Tes' lt 'Test'");
    assertEquals(3, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test' le 'Test'");
    assertEquals(3, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test1' le 'Test'");
    assertEquals(0, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test1' gt 'Test'");
    assertEquals(3, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Tes' gt 'Test'");
    assertEquals(0, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test' ge 'Test'");
    assertEquals(3, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test' eq 'Test'");
    assertEquals(3, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test1' ne 'Test'");
    assertEquals(3, result.getBody().getEntities().size());
  }
  
  @Test
  public void testCastException() {
    fail("ESAllPrim", "PropertyInt16 eq '1'", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq 03:26:05", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq true", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq 2012-12-03T07:16:23Z", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq duration'PT4S'", HttpStatusCode.BAD_REQUEST);
  }

  
  @Test
  public void testStringFunctionWithoutStringParameters() {
    fail("ESServerSidePaging", "contains(PropertyInt16, 3) eq 'hallo'", HttpStatusCode.BAD_REQUEST);
  }
  
  private ODataRetrieveResponse<ClientEntitySet> sendRequest(final String entitySet, final String filterString) {
    return sendRequest(entitySet, filterString, null);
  }

  private ODataRetrieveResponse<ClientEntitySet> sendRequest(final String entitySet, final String filterString,
      final String cookie) {
    final ODataClient client = getClient();

    final URI uri =
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(entitySet)
        .filter(filterString)
        .build();

    ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    if (cookie != null) {
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
    }

    return request.execute();
  }

  private void fail(final String entitySet, final String filterString, final HttpStatusCode errorCode) {
    try {
      sendRequest(entitySet, filterString);
      Assert.fail();
    } catch (ODataClientErrorException e) {
      assertEquals(errorCode.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    return odata;
  }

}
