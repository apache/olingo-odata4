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

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataValuable;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
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
    //Nothing here.
  }

  @Test
  public void testTimeOfDayLiteral() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "PropertyTimeOfDay eq 03:26:05");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }
  
  @Test
  public void testBooleanLiteral() {
    ODataRetrieveResponse<ODataEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyBoolean eq false");
    assertEquals(2, response.getBody().getEntities().size());
    
    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    
    response = sendRequest(ES_ALL_PRIM, "PropertyBoolean eq true");
    assertEquals(1, response.getBody().getEntities().size());
    
    oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }
  
  @Test
  public void testDateLiteral() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "PropertyDate eq 2012-12-03");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDateTimeOffsetLiteral() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:23Z");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testInt64Literal() {
    long value = Integer.MAX_VALUE + 1L;
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 gt " + value);
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDoubleLiteral() {
    Double value = -17900000000000000000.0;

    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDouble le " + value.toString());
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testSimpleEq() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq 1");

    assertEquals(2, result.getBody().getEntities().size());
    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testBinaryIntegerOperations() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add 1 eq (1 sub 3) div 2 mul 3 add 7");

    assertEquals(1, result.getBody().getEntities().size());
    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
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

    final ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri).execute();

    assertEquals(1, response.getBody().getEntities().size());
    ODataEntity oDataEntity = response.getBody().getEntities().get(0);

    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testStringProperty() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2'");

    assertEquals(1, result.getBody().getEntities().size());
    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testBooleanOperator() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2' and PropertyInt16 eq 1");
    assertEquals(1, result.getBody().getEntities().size());
    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2' or PropertyInt16 eq 1");
    assertEquals(2, result.getBody().getEntities().size());
    oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testBooleanOperatorWithNull() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq null");
    assertEquals(0, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "null eq null");
    assertEquals(4, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "null ne null");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testUnaryWithNullLiterals() {
    ODataRetrieveResponse<ODataEntitySet> result =
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
    ODataRetrieveResponse<ODataEntitySet> result =
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
    ODataObjectFactory objectFactory = client.getObjectFactory();

    ODataEntity entity = objectFactory.newEntity(new FullQualifiedName("olingo.odata.test1.ETAllPrim"));

    entity.getProperties().add(
        objectFactory.newPrimitiveProperty("PropertyInt16", objectFactory.newPrimitiveValueBuilder()
            .buildInt16((short) 1)));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").build();
    ODataEntityCreateResponse<ODataEntity> createResponse =
        client.getCUDRequestFactory().getEntityCreateRequest(uri, entity).execute();

    final URI receiveURI =
        client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESAllPrim")
            .filter(filterString)
            .build();

    ODataEntitySetRequest<ODataEntitySet> filterRequest =
        client.getRetrieveRequestFactory().getEntitySetRequest(receiveURI);
    filterRequest.addCustomHeader(HttpHeader.COOKIE, createResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    ODataRetrieveResponse<ODataEntitySet> filterResponse = filterRequest.execute();

    assertEquals(1, filterResponse.getBody().getEntities().size());
  }

  @Test
  public void testNumericComparisionOperators() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 ge 1");
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

    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, filterString);
    assertEquals(3, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
    assertEquals("11", ((ODataValuable) ((ODataValuable) oDataEntity.getProperty("PropertyComp")).getComplexValue()
        .get("PropertyInt16")).getValue()
        .toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
    assertEquals("11", ((ODataValuable) ((ODataValuable) oDataEntity.getProperty("PropertyComp")).getComplexValue()
        .get("PropertyInt16")).getValue()
        .toString());

    oDataEntity = result.getBody().getEntities().get(2);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
    assertEquals("11", ((ODataValuable) ((ODataValuable) oDataEntity.getProperty("PropertyComp")).getComplexValue()
        .get("PropertyInt16")).getValue()
        .toString());
  }

  @Test
  public void testNotOperator() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "not (PropertyInt16 eq 1)");
    assertEquals(2, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testUnaryMinusOperator() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt -2 add - -3");
    assertEquals(2, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testUnaryMinusOperatorDecimal() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt -2.0 add - -3.0");
    assertEquals(2, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testStringPropertyEqualsNull() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq null");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testAddNullLiteral() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add null eq 1");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void testAddNullLiteralEqualsNull() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add null eq null");
    assertEquals(4, result.getBody().getEntities().size());
  }

  @Test
  public void testSubstringStartAndEndGiven() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "substring(PropertyString, length('First') add 1, 8) eq ('Resource')");

    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testSubstringStartGiven() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "substring(PropertyComp/PropertyComp/PropertyString, 6) eq 'Value'");

    assertEquals(4, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(2);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(3);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  @SuppressWarnings("unused")
  public void testSubstringDouble() {
    try {
      ODataRetrieveResponse<ODataEntitySet> result =
          sendRequest(ES_ALL_PRIM, "substring(PropertyString, length('First')"
              + "add 1, 2.0 * 4) eq ('Resource')");
    } catch (ODataClientErrorException e) {
      assertEquals(400, e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void testYearFunctionDate() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "year(PropertyDate) eq 2015");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testYearFunctionDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "year(PropertyDateTimeOffset) eq 2012");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testMonthFunctionDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "month(PropertyDateTimeOffset) eq 12");
    assertEquals(3, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(2);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testMonthFunctionDate() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "month(PropertyDate) eq 11");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDayFunctionDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "day(PropertyDateTimeOffset) eq 3");
    assertEquals(3, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(2);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDayFunctionDate() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "day(PropertyDate) eq 5");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testHourFunctionDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "hour(PropertyDateTimeOffset) eq 7");
    assertEquals(2, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testHourFuntionTimeOfDay() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "hour(PropertyTimeOfDay) eq 3");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testMinuteFunctionDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "minute(PropertyDateTimeOffset) eq 17");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testMinuteFuntionTimeOfDay() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "minute(PropertyTimeOfDay) eq 49");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testSecondFunctionDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> response = sendRequest(ES_ALL_PRIM, "second(PropertyDateTimeOffset) eq 8");
    assertEquals(1, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testSecondFuntionTimeOfDay() {
    ODataRetrieveResponse<ODataEntitySet> response = sendRequest(ES_ALL_PRIM, "second(PropertyTimeOfDay) eq 14");
    assertEquals(1, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testFractionalsecondsDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_COMP_ALL_PRIM, "fractionalseconds(PropertyComp/PropertyDateTimeOffset) eq 0.1234567");
    assertEquals(2, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testFractionalsecondsDateOfTime() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "fractionalseconds(PropertyTimeOfDay) eq 0");
    assertEquals(3, response.getBody().getEntities().size());
  }

  @Test
  public void testDateTimeFunctionsNull() {
    ODataRetrieveResponse<ODataEntitySet> response;

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
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq floor(3.8)");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq floor(3.1)");
    assertEquals(1, result.getBody().getEntities().size());

    oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testCeiling() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq ceiling(2.1)");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq ceiling(2.6)");
    assertEquals(1, result.getBody().getEntities().size());

    oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testRound() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(2.5)");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(2.4)");
    assertEquals(1, result.getBody().getEntities().size());

    oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("2", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(2.6)");
    assertEquals(1, result.getBody().getEntities().size());

    oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq round(3.1)");
    assertEquals(1, result.getBody().getEntities().size());

    oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("3", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
    assertEquals("1", ((ODataValuable) oDataEntity.getProperty("PropertyString")).getValue().toString());
  }

  @Test
  public void testEndsWith() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "endswith(PropertyString, 'values')");
    assertEquals(2, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = result.getBody().getEntities().get(1);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testIndexOf() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "indexof(PropertyString, 'positive') eq 17");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testStartsWith() {
    ODataRetrieveResponse<ODataEntitySet> result = sendRequest(ES_ALL_PRIM, "startswith(PropertyString, 'First')");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testToLower() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "contains(PropertyString, tolower('POSITIVE'))");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testToUpper() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "contains(PropertyString, concat(toupper('f'), 'irst'))");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testTrim() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "trim(substring(PropertyString, 0, 6)) eq 'First'");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDecimalDiv() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDouble eq 0 sub (358000 div 2)");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testNumericPromotionToInt64() {
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 eq 0");
    assertEquals(1, result.getBody().getEntities().size());

    ODataEntity oDataEntity = result.getBody().getEntities().get(0);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void castEdm64ToDouble() {
    double value = Float.MAX_VALUE + 1;
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 lt " + value);
    assertEquals(3, result.getBody().getEntities().size());
  }

  @Test
  public void testDateTimeOffsetAddDuraton() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:19Z add duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDurrationAddDuration() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq duration'PT2S' add duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDurrationLiteral() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq duration'P1DT'");
    assertEquals(0, response.getBody().getEntities().size());
  }

  @Test
  public void testDateAddDuration() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-02 add duration'P1DT7H16M23S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDateTimeOffsetSubDuration() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:27Z sub duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDurrationSubDuration() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration sub duration'PT2S' eq duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDateSubDuration() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-04 sub duration'P0DT16H43M37S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDateSubDate() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq 2012-12-04 sub 2012-12-04");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testDateTimeOffsetSubDateTimeOffset() {
    ODataRetrieveResponse<ODataEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq 2005-12-03T00:00:00Z sub 2005-12-03T00:00:00Z");
    assertEquals(1, response.getBody().getEntities().size());

    final ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testNumericPromotion() {
    /*
     * The idea is use the largest possible number of a specific type and add a another number to force an
     * implicit conversion to an higher type
     */
    ODataRetrieveResponse<ODataEntitySet> response;

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
    // is still there, but filled is null value (primitive types)
    // We define a filter, which returns all entry where PropertyCompComp/PropertyComp/PropertyInt16 is equals to 1

    final ODataClient client = getClient();
    final ODataObjectFactory factory = client.getObjectFactory();
    ODataEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    newEntity.getProperties().add(factory.newComplexProperty("PropertyCompComp", null));
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

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESKeyNav").build();
    ODataEntityCreateRequest<ODataEntity> request =
        client.getCUDRequestFactory().getEntityCreateRequest(uri, newEntity);
    ODataEntityCreateResponse<ODataEntity> response = request.execute();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());

    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    // Do the filter request
    ODataRetrieveResponse<ODataEntitySet> result =
        sendRequest("ESKeyNav", "PropertyCompComp/PropertyComp/PropertyInt16 eq 1", cookie);
    assertEquals(3, result.getBody().getEntities().size());

    // Try filter all entries where PropertyCompComp is null
    result = sendRequest("ESKeyNav", "PropertyCompComp/PropertyComp/PropertyInt16 eq null", cookie);
    assertEquals(1, result.getBody().getEntities().size());
  }
  
  @Test
  public void testSringFunctionWithoutStringParameters() {
    fail("ESServerSidePaging", "filter=contains(PropertyInt16, 3) eq 'hallo'", HttpStatusCode.BAD_REQUEST);
  }

  private ODataRetrieveResponse<ODataEntitySet> sendRequest(String entitySet, String filterString) {
    return sendRequest(entitySet, filterString, null);
  }

  private ODataRetrieveResponse<ODataEntitySet> sendRequest(String entitySet, String filterString, String cookie) {
    final ODataClient client = getClient();

    final URI uri =
        client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(entitySet)
            .filter(filterString)
            .build();

    ODataEntitySetRequest<ODataEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    if (cookie != null) {
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
    }

    return request.execute();
  }

  private void fail(String entitySet, String filterString, HttpStatusCode errorCode) {
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
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }

}
