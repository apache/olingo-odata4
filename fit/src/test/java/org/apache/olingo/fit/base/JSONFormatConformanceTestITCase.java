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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

/**
 * The test cases in this class are inspired by client conformance criteria defined in the <a
 * href="http://docs.oasis-open.org/odata/odata-json-format/v4.0/os/odata-json-format-v4.0-os.html#_Toc372793094">specs
 * </a>.
 */
public class JSONFormatConformanceTestITCase extends AbstractTestITCase {
  /**
   * MUST be prepared to consume a response with full metadata.
   */
  @Test
  public void item2() {
    final URI uri = edmClient.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(102).build();
    final ODataEntityRequest<ClientEntity> req = edmClient.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(ContentType.JSON_FULL_METADATA);

    // request format (via Accept header) is set to full metadata
    assertEquals("application/json;odata.metadata=full", req.getAccept());

    final ODataRetrieveResponse<ClientEntity> res = req.execute();

    // response is odata.metadata=full
    assertTrue(res.getContentType().contains("odata.metadata=full"));

    // response payload is understood (including links, only returned with full metadata)
    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("MyPaymentInstruments").getType());
    assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("ActiveSubscriptions").getType());
  }

  /**
   * MUST be prepared to receive all data types (section 7.1)
   * <ol>
   * <li>defined in this specification (client)</li>
   * <li>exposed by the service (service)</li>
   * </ol>
   * .
   */
  @Test
  public void item3() throws Exception {
    final String fromSection71 = "{"
        + "\"NullValue\": null,"
        + "\"TrueValue\": true,"
        + "\"FalseValue\": false,"
        + "\"BinaryValue@odata.type\": \"Binary\","
        + "\"BinaryValue\": \"T0RhdGE\","
        + "\"IntegerValue\": -128,"
        + "\"DoubleValue\": 3.1415926535897931,"
        + "\"SingleValue@odata.type\": \"Single\","
        + "\"SingleValue\": \"INF\","
        + "\"DecimalValue@odata.type\": \"Decimal\","
        + "\"DecimalValue\": 34.95,"
        + "\"StringValue\": \"Say \\\"Hello\\\",\\nthen go\","
        + "\"DateValue@odata.type\": \"Date\","
        + "\"DateValue\": \"2012-12-03\","
        + "\"DateTimeOffsetValue@odata.type\": \"DateTimeOffset\","
        + "\"DateTimeOffsetValue\": \"2012-12-03T07:16:23Z\","
        + "\"DurationValue@odata.type\": \"Duration\","
        + "\"DurationValue\": \"P12DT23H59M59.999999999999S\","
        + "\"TimeOfDayValue@odata.type\": \"TimeOfDay\","
        + "\"TimeOfDayValue\": \"07:59:59.999\","
        + "\"GuidValue@odata.type\": \"Guid\","
        + "\"GuidValue\": \"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"Int64Value@odata.type\": \"Int64\","
        + "\"Int64Value\": 0,"
        + "\"ColorEnumValue@odata.type\": \"Test.Color\","
        + "\"ColorEnumValue\": \"Yellow\","
        + "\"GeographyPoint\": {\"type\": \"Point\",\"coordinates\":[142.1,64.1]}"
        + "}";

    final ClientEntity entity = client.getReader().readEntity(IOUtils.toInputStream(fromSection71), ContentType.JSON);

    assertTrue(entity.getProperty("NullValue").hasNullValue());

    assertEquals(EdmPrimitiveTypeKind.Boolean, entity.getProperty("TrueValue").getPrimitiveValue().getTypeKind());
    assertEquals(Boolean.TRUE, entity.getProperty("TrueValue").getPrimitiveValue().toCastValue(Boolean.class));

    assertEquals(EdmPrimitiveTypeKind.Boolean, entity.getProperty("FalseValue").getPrimitiveValue().getTypeKind());
    assertEquals(Boolean.FALSE, entity.getProperty("FalseValue").getPrimitiveValue().toCastValue(Boolean.class));

    assertEquals(EdmPrimitiveTypeKind.Binary, entity.getProperty("BinaryValue").getPrimitiveValue().getTypeKind());

    assertEquals(EdmPrimitiveTypeKind.Int32, entity.getProperty("IntegerValue").getPrimitiveValue().getTypeKind());
    assertEquals(-128, entity.getProperty("IntegerValue").getPrimitiveValue().toCastValue(Integer.class), 0);

    assertEquals(EdmPrimitiveTypeKind.Double, entity.getProperty("DoubleValue").getPrimitiveValue().getTypeKind());
    assertEquals(3.1415926535897931,
        entity.getProperty("DoubleValue").getPrimitiveValue().toCastValue(Double.class), 0);

    assertEquals(EdmPrimitiveTypeKind.Single, entity.getProperty("SingleValue").getPrimitiveValue().getTypeKind());
    assertEquals(Float.POSITIVE_INFINITY,
        entity.getProperty("SingleValue").getPrimitiveValue().toCastValue(Float.class), 0);

    assertEquals(EdmPrimitiveTypeKind.Decimal, entity.getProperty("DecimalValue").getPrimitiveValue().getTypeKind());
    assertEquals(BigDecimal.valueOf(34.95),
        entity.getProperty("DecimalValue").getPrimitiveValue().toCastValue(BigDecimal.class));

    assertEquals(EdmPrimitiveTypeKind.String, entity.getProperty("StringValue").getPrimitiveValue().getTypeKind());
    assertEquals("Say \"Hello\",\nthen go",
        entity.getProperty("StringValue").getPrimitiveValue().toCastValue(String.class));

    assertEquals(EdmPrimitiveTypeKind.Date, entity.getProperty("DateValue").getPrimitiveValue().getTypeKind());

    assertEquals(EdmPrimitiveTypeKind.DateTimeOffset,
        entity.getProperty("DateTimeOffsetValue").getPrimitiveValue().getTypeKind());

    assertEquals(EdmPrimitiveTypeKind.Duration, entity.getProperty("DurationValue").getPrimitiveValue().getTypeKind());

    assertEquals(EdmPrimitiveTypeKind.TimeOfDay,
        entity.getProperty("TimeOfDayValue").getPrimitiveValue().getTypeKind());

    assertEquals(EdmPrimitiveTypeKind.Guid, entity.getProperty("GuidValue").getPrimitiveValue().getTypeKind());

    assertEquals(EdmPrimitiveTypeKind.Int64, entity.getProperty("Int64Value").getPrimitiveValue().getTypeKind());

    assertTrue(entity.getProperty("ColorEnumValue").hasEnumValue());

    assertEquals(EdmPrimitiveTypeKind.GeographyPoint,
        entity.getProperty("GeographyPoint").getPrimitiveValue().getTypeKind());
  }

  /**
   * MUST interpret all odata annotations defined according to the OData-Version header of the payload (section 4.5).
   */
  @Test
  public void item4() throws Exception {
    final String fromSection45_1 = "{"
        + "\"@odata.context\": \"http://host/service/$metadata#Customers/$entity\","
        + "\"@odata.metadataEtag\": \"W/\\\"A1FF3E230954908F\\\"\","
        + "\"@odata.etag\": \"W/\\\"A1FF3E230954908G\\\"\","
        + "\"@odata.type\": \"#Model.VipCustomer\","
        + "\"@odata.id\": \"http://host/service/Employees(PersonID=3)\","
        + "\"@odata.editLink\": \"People(976)\","
        + "\"@odata.mediaEditLink\": \"Employees(1)/$value\","
        + "\"@odata.mediaContentType\": \"image/jpeg\","
        + "\"@odata.mediaEtag\": \"W/\\\"A1FF3E230954908H\\\"\","
        + "\"Parent@odata.navigationLink\": \"People(976)/Parent\","
        + "\"Parent@odata.associationLink\": \"People(976)/Parent\""
        + "}";

    final ResWrap<Entity> entity =
        client.getDeserializer(ContentType.JSON).toEntity(IOUtils.toInputStream(fromSection45_1));

    assertEquals("http://host/service/$metadata#Customers/$entity", entity.getContextURL().toASCIIString());
    assertEquals("W/\"A1FF3E230954908F\"", entity.getMetadataETag());
    assertEquals("W/\"A1FF3E230954908G\"", entity.getPayload().getETag());
    assertEquals("Model.VipCustomer", entity.getPayload().getType());
    assertEquals("http://host/service/Employees(PersonID=3)", entity.getPayload().getId().toASCIIString());
    assertEquals("People(976)", entity.getPayload().getEditLink().getHref());
    assertEquals("Employees(1)/$value", entity.getPayload().getMediaContentSource().toASCIIString());
    assertEquals("image/jpeg", entity.getPayload().getMediaContentType());
    assertEquals("W/\"A1FF3E230954908H\"", entity.getPayload().getMediaETag());
    assertEquals("People(976)/Parent", entity.getPayload().getNavigationLink("Parent").getHref());
    assertEquals("People(976)/Parent", entity.getPayload().getAssociationLink("Parent").getHref());

    final String fromSection45_2 = "{"
        + "  \"@odata.count\": 5,"
        + "  \"value\": [],"
        + "  \"@odata.nextLink\": \"Customers?$expand=Orders&$skipToken=5\","
        + "  \"@odata.deltaLink\": \"Customers?$expand=Orders&$deltatoken=8015\""
        + "}";

    final ResWrap<EntityCollection> entitySet =
        client.getDeserializer(ContentType.JSON).toEntitySet(IOUtils.toInputStream(fromSection45_2));

    assertEquals(5, entitySet.getPayload().getCount(), 0);
    assertEquals("Customers?$expand=Orders&$skipToken=5", entitySet.getPayload().getNext().toASCIIString());
    assertEquals("Customers?$expand=Orders&$deltatoken=8015", entitySet.getPayload().getDeltaLink().toASCIIString());
  }

  /**
   * MUST be prepared to receive any annotations, including custom annotations and <tt>odata</tt> annotations not
   * defined in the <tt>OData-Version</tt> header of the payload (section 20).
   */
  @Test
  public void item5() throws Exception {
    final String sample = "{"
        + "  \"@odata.context\": \"http://host/service/$metadata#Customers\","
        + "  \"@odata.notdefined\": 11,"
        + "  \"@com.contoso.customer.setkind\": \"VIPs\","
        + "  \"value\": ["
        + "    {"
        + "      \"@com.contoso.display.highlight\": true,"
        + "      \"ID\": \"ALFKI\","
        + "      \"CompanyName@com.contoso.display.style\": { \"title\": true, \"order\": 1 },"
        + "      \"CompanyName\": \"Alfreds Futterkiste\","
        + "      \"Orders@com.contoso.display.style\": { \"order\": 2 },"
        + "      \"Orders@odata.navigationLink\": \"People(976)/Orders\""
        + "    }"
        + "  ]"
        + "}";

    final ClientEntitySet entitySet = client.getReader().
        readEntitySet(IOUtils.toInputStream(sample), ContentType.JSON);

    assertEquals(2, entitySet.getAnnotations().size());

    final ClientAnnotation notdefined = entitySet.getAnnotations().get(0);
    assertEquals("odata.notdefined", notdefined.getTerm());
    assertEquals(11, notdefined.getPrimitiveValue().toCastValue(Integer.class), 0);

    final ClientAnnotation setkind = entitySet.getAnnotations().get(1);
    assertEquals("com.contoso.customer.setkind", setkind.getTerm());
    assertEquals("VIPs", setkind.getPrimitiveValue().toCastValue(String.class));

    final ClientEntity entity = entitySet.getEntities().get(0);
    assertEquals(1, entity.getAnnotations().size());

    final ClientAnnotation highlight = entity.getAnnotations().get(0);
    assertEquals("com.contoso.display.highlight", highlight.getTerm());
    assertEquals(Boolean.TRUE, highlight.getPrimitiveValue().toCastValue(Boolean.class));

    final ClientProperty property = entity.getProperty("CompanyName");
    assertEquals(1, property.getAnnotations().size());

    final ClientAnnotation style = property.getAnnotations().get(0);
    assertEquals("com.contoso.display.style", style.getTerm());
    assertTrue(style.hasComplexValue());
    assertEquals(Boolean.TRUE, style.getComplexValue().get("title").getPrimitiveValue().toCastValue(Boolean.class));
    assertEquals(1, style.getComplexValue().get("order").getPrimitiveValue().toCastValue(Integer.class), 0);

    final ClientLink orders = entity.getNavigationLink("Orders");
    assertEquals(1, orders.getAnnotations().size());

    final ClientAnnotation style2 = orders.getAnnotations().get(0);
    assertEquals("com.contoso.display.style", style2.getTerm());
    assertTrue(style2.hasComplexValue());
    assertEquals(2, style2.getComplexValue().get("order").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  /**
   * MUST NOT require <tt>odata.streaming=true</tt> in the <tt>Content-Type</tt> header (section 4.4).
   */
  @Test
  public void item6() throws EdmPrimitiveTypeException {
    final URI uri = edmClient.newURIBuilder().
        appendEntitySetSegment("Accounts").appendKeySegment(102).
        appendNavigationSegment("MyPaymentInstruments").appendKeySegment(102902).build();
    final ODataEntityRequest<ClientEntity> req = edmClient.getRetrieveRequestFactory().getEntityRequest(uri);

    // request format (via Accept header) does not contain odata.streaming=true
    assertEquals("application/json;odata.metadata=minimal", req.getAccept());

    final ODataRetrieveResponse<ClientEntity> res = req.execute();

    // response payload is understood
    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument", entity.getTypeName().toString());
    assertEquals(102902, entity.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
    assertEquals("Edm.DateTimeOffset", entity.getProperty("CreatedDate").getPrimitiveValue().getTypeName());
  }
}
