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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.uri.FilterArgFactory;
import org.apache.olingo.client.api.uri.FilterFactory;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;

public class FilterSystemQueryITCase extends AbstractParamTecSvcITCase {

  private static final String ES_COMP_ALL_PRIM = "ESCompAllPrim";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String ES_MIX_ENUM_DEF_COLL_COMP = "ESMixEnumDefCollComp";

  
  @Test
  public void useFilterFactory() throws URISyntaxException, IOException {
    final URIFilter filter = getFilterFactory().eq(
        getFilterArgFactory().property("PropertyInt16"), getFilterArgFactory().literal(new Integer(0)));

    final URIBuilder uriBuilder =
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).filter(filter);

    final ODataEntitySetRequest<ClientEntitySet> req =
        getClient().getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());

    final ODataRetrieveResponse<ClientEntitySet> result = req.execute();
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }
  
  private FilterFactory getFilterFactory() {
    return getClient().getFilterFactory();
  }

  private FilterArgFactory getFilterArgFactory() {
    return getFilterFactory().getArgFactory();
  }

  
  @Test
  public void timeOfDayLiteral() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "PropertyTimeOfDay eq 03:26:05");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void booleanLiteral() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyBoolean eq false");
    assertEquals(3, response.getBody().getEntities().size());

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
  public void dateLiteral() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "PropertyDate eq 2012-12-03");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dateTimeOffsetLiteral() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:23Z");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void int64Literal() throws IOException, URISyntaxException {
    long value = Integer.MAX_VALUE + 1L;
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 gt " + value);
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void doubleLiteral() throws IOException, URISyntaxException {
    Double value = -17900000000000000000.0;

    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDouble le " + value.toString());
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void simpleEq() throws IOException, URISyntaxException {
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
  public void binaryIntegerOperations() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add 1 eq (1 sub 3) div 2 mul 3 add 7");

    assertEquals(1, result.getBody().getEntities().size());
    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void clientEscaping() throws IOException, URISyntaxException {
    final String filter = getClient().getFilterFactory().eq(
        getClient().getFilterFactory().getArgFactory().property("PropertyString"),
        getClient().getFilterFactory().getArgFactory().literal("First Resource - positive values")).build();

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .filter(filter)
        .build();

    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(uri);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertEquals(1, response.getBody().getEntities().size());
    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void stringProperty() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq '2'");

    assertEquals(1, result.getBody().getEntities().size());
    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertEquals("2", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void booleanOperator() throws IOException, URISyntaxException {
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

    result = sendRequest(ES_MIX_ENUM_DEF_COLL_COMP, "PropertyEnumString eq Namespace1_Alias.ENString'String2'");
    assertTrue(result.getBody().getEntities().isEmpty());
  }

  @Test
  public void booleanOperatorWithNull() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 eq null");
    assertEquals(0, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "null eq null");
    assertEquals(4, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "null ne null");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void unaryWithNullLiterals() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "PropertyComp/PropertyComp/PropertyBoolean eq not null");
    assertEquals(0, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyComp/PropertyComp/PropertyInt16 eq 0 add -(5 add null)");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void unaryWithWrongTypes() {
    fail(ES_ALL_PRIM, "PropertyInt16 eq 6 add - 'test'", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "PropertyBoolean eq not 'test'", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void methodCallsWithNull() throws IOException, URISyntaxException {
    // One representative of "stringFuntion" "residue class"
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "endswith(PropertyString,null) eq null"); // null eq null => true
    assertEquals(4, result.getBody().getEntities().size());

    // One representative of "stringifiedValueFunction" "residue class"
    result = sendRequest(ES_ALL_PRIM, "substring(PropertyString,null) eq null"); // null eq null => true
    assertEquals(4, result.getBody().getEntities().size());

    // Substring
    result = sendRequest(ES_ALL_PRIM, "hour(null) eq null"); // null eq null => true
    assertEquals(4, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "substring(PropertyString,0,null) eq null"); // null eq null => true
    assertEquals(4, result.getBody().getEntities().size());
  }

  @Test
  public void substringWithNegativeValues() throws IOException, URISyntaxException {
    // See OASIS JIRA ODATA-781

    // -1 should be treated as 0
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "substring(PropertyString,-1,1) eq 'F'");
    assertEquals(1, response.getBody().getEntities().size());
    assertShortOrInt(32767, response.getBody().getEntities().get(0).getProperty("PropertyInt16")
          .getPrimitiveValue().toValue());

    // -1 should be treated as 0, Same values substring(PropertyString, 0, 0) returns the empty String
    response = sendRequest(ES_ALL_PRIM, "substring(PropertyString,0,-1) eq ''");
    assertEquals(4, response.getBody().getEntities().size());
  }

  @Test
  public void unknownLiteral() {
    // Check if the error code is equals to 400
    fail(ES_ALL_PRIM, "PropertyInt16 eq ThisIsNotAValidLiteral", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void errorCodeArithmetic() {
    fail(ES_ALL_PRIM, "PropertyInt16 eq 'hey' add 5", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "PropertyDate eq 5.0 add 2012-12-03", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "PropertyDouble mod 5 eq 0", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "UnkownProperty eq null", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void numericBinaryOperationWithNullValues() throws IOException, URISyntaxException {
    // Create new Entries
    final String filterString = "PropertyString eq null";

    ClientEntity entity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim"));

    entity.getProperties().add(
        getFactory().newPrimitiveProperty("PropertyInt16", getFactory().newPrimitiveValueBuilder()
            .buildInt16((short) 1)));
    entity.addLink(getFactory().newEntityNavigationLink("NavPropertyETTwoPrimOne",
        getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoPrim")
        .appendKeySegment(32766)
        .build()));

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    ODataEntityCreateResponse<ClientEntity> createResponse =
        getClient().getCUDRequestFactory().getEntityCreateRequest(uri, entity).execute();

    ODataRetrieveResponse<ClientEntitySet> filterResponse = sendRequest(ES_ALL_PRIM, filterString,
        createResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    assertEquals(1, filterResponse.getBody().getEntities().size());
  }

  @Test
  public void numericComparisonOperators() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 ge 1");
    assertEquals(4, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt 1");
    assertEquals(2, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 lt 2");
    assertEquals(2, result.getBody().getEntities().size());

    result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 le 2");
    assertEquals(3, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble ge -179000");
    assertEquals(3, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble gt -179000");
    assertEquals(2, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble lt -179000");
    assertEquals(1, result.getBody().getEntities().size());

    result = sendRequest(ES_ALL_PRIM, "PropertyDouble le -179000");
    assertEquals(2, result.getBody().getEntities().size());
  }

  @Test
  public void binaryOperationIntegerDecimalWithPromotion() throws IOException, URISyntaxException {
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
  public void notOperator() throws IOException, URISyntaxException {
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
  public void unaryMinusOperator() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt -2 add --3");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void unaryMinusOperatorDecimal() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 gt -2.0 add --3.0");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(2, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(3, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("1", clientEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void stringPropertyEqualsNull() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyString eq null");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void addNullLiteral() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add null eq 1");
    assertEquals(0, result.getBody().getEntities().size());
  }

  @Test
  public void addNullLiteralEqualsNull() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_TWO_KEY_NAV, "PropertyInt16 add null eq null");
    assertEquals(4, result.getBody().getEntities().size());
  }

  @Test
  public void substringStartAndEndGiven() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "substring(PropertyString,length('First') add 1,8) eq ('Resource')");

    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void substringStartGiven() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_TWO_KEY_NAV, "substring(PropertyComp/PropertyComp/PropertyString,6) eq 'Value'");

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
  public void substringDouble() {
    fail(ES_ALL_PRIM,
        "substring(PropertyString,length('First') add 1,2.0 * 4) eq ('Resource')",
        HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void yearFunctionDate() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "year(PropertyDate) eq 2015");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void yearFunctionDateTimeOffset() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "year(PropertyDateTimeOffset) eq 2012");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void monthFunctionDateTimeOffset() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "month(PropertyDateTimeOffset) eq 12");
    assertEquals(4, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(2);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void monthFunctionDate() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "month(PropertyDate) eq 11");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dayFunctionDateTimeOffset() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "day(PropertyDateTimeOffset) eq 3");
    assertEquals(4, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(2);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dayFunctionDate() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "day(PropertyDate) eq 5");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void hourFunctionDateTimeOffset() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "hour(PropertyDateTimeOffset) eq 7");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void hourFunctionTimeOfDay() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "hour(PropertyTimeOfDay) eq 3");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void minuteFunctionDateTimeOffset() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "minute(PropertyDateTimeOffset) eq 17");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void minuteFunctionTimeOfDay() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "minute(PropertyTimeOfDay) eq 49");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void secondFunctionDateTimeOffset() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "second(PropertyDateTimeOffset) eq 8");
    assertEquals(1, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void secondFunctionTimeOfDay() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "second(PropertyTimeOfDay) eq 14");
    assertEquals(1, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void fractionalsecondsDateTimeOffset() throws IOException, URISyntaxException {
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
  public void fractionalsecondsDateOfTime() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "fractionalseconds(PropertyTimeOfDay) eq 0");
    assertEquals(4, response.getBody().getEntities().size());
  }

  @Test
  public void dateTimeFunctionsNull() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response;

    response = sendRequest(ES_ALL_PRIM, "year(null) eq null");
    assertEquals(4, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "month(null) eq null");
    assertEquals(4, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "day(null) eq null");
    assertEquals(4, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "hour(null) eq null");
    assertEquals(4, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "minute(null) eq null");
    assertEquals(4, response.getBody().getEntities().size());

    response = sendRequest(ES_ALL_PRIM, "second(null) eq null");
    assertEquals(4, response.getBody().getEntities().size());
  }

  @Test
  public void floor() throws IOException, URISyntaxException {
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
  public void ceiling() throws IOException, URISyntaxException {
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
  public void round() throws IOException, URISyntaxException {
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
  public void endsWith() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "endswith(PropertyString,'values')");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());

    clientEntity = result.getBody().getEntities().get(1);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void indexOf() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "indexof(PropertyString,'positive') eq 17");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void startsWith() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "startswith(PropertyString,'First')");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void toLower() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "contains(PropertyString,tolower('POSITIVE'))");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void toUpper() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "contains(PropertyString,concat(toupper('f'),'irst'))");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void trim() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "trim(substring(PropertyString,0,6)) eq 'First'");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void decimalDiv() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyDouble eq 0 sub (358000 div 2)");
    assertEquals(1, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(-32768, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void numericPromotionToInt64() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 eq 0");
    assertEquals(2, result.getBody().getEntities().size());

    ClientEntity clientEntity = result.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void castEdm64ToDouble() throws IOException, URISyntaxException {
    double value = Float.MAX_VALUE + 1;
    ODataRetrieveResponse<ClientEntitySet> result =
        sendRequest(ES_ALL_PRIM, "PropertyInt64 lt " + value);
    assertEquals(4, result.getBody().getEntities().size());
  }

  @Test
  public void dateTimeOffsetAddDuration() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:19Z add duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void durationAddDuration() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq duration'PT2S' add duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void durationLiteral() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq duration'P1DT'");
    assertEquals(0, response.getBody().getEntities().size());
  }

  @Test
  public void dateAddDuration() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDate eq 2012-12-01 add duration'P1DT27H16M23S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dateTimeOffsetSubDuration() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDateTimeOffset eq 2012-12-03T07:16:27Z sub duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void durationSubDuration() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration sub duration'PT2S' eq duration'PT4S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dateSubDuration() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDate eq 2012-12-03 sub duration'P0DT16H43M37S'");
    assertEquals(1, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(32767, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dateSubDate() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq 2012-12-04 sub 2012-12-04");
    assertEquals(2, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void dateTimeOffsetSubDateTimeOffset() throws URISyntaxException, IOException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyDuration eq 2005-12-03T00:00:00Z sub 2005-12-03T00:00:00Z");
    assertEquals(2, response.getBody().getEntities().size());

    final ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertShortOrInt(0, clientEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
  }

  @Test
  public void numericPromotion() throws URISyntaxException, IOException {
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
  public void nullComplexProperty() throws URISyntaxException, IOException {
    // Create a new entry.The complex property PropertyCompComp is set to null. So the structure of the property
    // is still there, but filled is null values (primitive types)
    // We define a filter, which returns all entry where PropertyCompComp/PropertyComp/PropertyInt16 is equals to 1

    ClientEntity newEntity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETKeyNav"));
    newEntity.getProperties().add(getFactory().newComplexProperty("PropertyCompCompNav", null));
    newEntity.getProperties().add(getFactory().newPrimitiveProperty("PropertyInt16",
        getFactory().newPrimitiveValueBuilder().buildInt16((short) 4)));
    newEntity.getProperties().add(getFactory().newPrimitiveProperty("PropertyString",
        getFactory().newPrimitiveValueBuilder().buildString("Test")));
    newEntity.getProperties().add(
        getFactory().newComplexProperty("PropertyCompAllPrim",
            getFactory().newComplexValue(SERVICE_NAMESPACE+"."+"CTAllPrim")
            .add(getFactory().newPrimitiveProperty(
                "PropertyString",
                getFactory().newPrimitiveValueBuilder().buildString("Test 3")))));

    newEntity.getProperties().add(
        getFactory().newComplexProperty("PropertyCompTwoPrim",
            getFactory().newComplexValue(SERVICE_NAMESPACE+"."+"CTTwoPrim")
            .add(getFactory().newPrimitiveProperty(
                "PropertyInt16",
                getFactory().newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(getFactory().newPrimitiveProperty(
                    "PropertyString",
                    getFactory().newPrimitiveValueBuilder().buildString("Test2")))));

    newEntity.addLink(getFactory().newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 1L;

          {
            put("PropertyInt16", 1);
            put("PropertyString", "1");
          }
        })
        .build()));

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESKeyNav").build();
    ODataEntityCreateRequest<ClientEntity> request =
        getClient().getCUDRequestFactory().getEntityCreateRequest(uri, newEntity);
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
  public void filterNotBooleanExpression() {
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
  public void comparisonOnStringOperands() throws IOException, URISyntaxException {
    // If check if the expression is true => All entry are returned
    ODataRetrieveResponse<ClientEntitySet> result = sendRequest(ES_ALL_PRIM, "'Tes' lt 'Test'");
    assertEquals(4, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test' le 'Test'");
    assertEquals(4, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test1' le 'Test'");
    assertEquals(0, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test1' gt 'Test'");
    assertEquals(4, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Tes' gt 'Test'");
    assertEquals(0, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test' ge 'Test'");
    assertEquals(4, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test' eq 'Test'");
    assertEquals(4, result.getBody().getEntities().size());
    
    result = sendRequest(ES_ALL_PRIM, "'Test1' ne 'Test'");
    assertEquals(4, result.getBody().getEntities().size());
  }

  @Test
  public void castException() {
    fail("ESAllPrim", "PropertyInt16 eq '1'", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq 03:26:05", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq true", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq 2012-12-03T07:16:23Z", HttpStatusCode.BAD_REQUEST);
    fail("ESAllPrim", "PropertyInt16 eq duration'PT4S'", HttpStatusCode.BAD_REQUEST);
  }

  @Test
  public void stringFunctionWithoutStringParameters() {
    fail("ESServerSidePaging", "contains(PropertyInt16, 3) eq 'hallo'", HttpStatusCode.BAD_REQUEST);
  }
  
  private ODataRetrieveResponse<ClientEntitySet> sendRequest(final String entitySet, final String filterString)
          throws URISyntaxException, IOException {
    return sendRequest(entitySet, filterString, null);
  }

  private ODataRetrieveResponse<ClientEntitySet> sendRequest(final String entitySet, final String filterString,
      final String cookie) throws URISyntaxException, IOException {
    final URI uri =
        getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(entitySet)
        .filter(filterString)
        .build();

    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
    if (cookie == null) {
      setCookieHeader(request);
    } else {
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
    }

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    if (cookie == null) {
      saveCookieHeader(response);
    }
    return response;
  }
  
  private void fail(final String entitySet, final String filterString, final HttpStatusCode errorCode) {
    try {
      sendRequest(entitySet, filterString);
      Assert.fail();
    } catch (ODataClientErrorException e) {
      assertEquals(errorCode.getStatusCode(), e.getStatusLine().getStatusCode());
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }
  
  @Test
  public void substringOf() throws IOException, URISyntaxException {
    ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "substringof('Second',PropertyString)");
    assertEquals(1, response.getBody().getEntities().size());
    assertEquals("Second Resource - negative values", 
        response.getBody().getEntities().get(0).getProperty("PropertyString")
          .getPrimitiveValue().toValue());
  }
  
  @Test
  public void substringOfWithNegativeValues() {
    fail(ES_ALL_PRIM, "substringof(123,PropertyString)", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "substringof(PropertyString,123)", HttpStatusCode.BAD_REQUEST);
    fail(ES_ALL_PRIM, "substringof(123,123)", HttpStatusCode.BAD_REQUEST);
  }
}
