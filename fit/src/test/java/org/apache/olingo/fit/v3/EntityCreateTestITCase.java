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
package org.apache.olingo.fit.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import org.apache.http.entity.ContentType;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.v3.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.http.NoContentException;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;

import org.junit.Test;

/**
 * This is the unit test class to check create entity operations.
 */
public class EntityCreateTestITCase extends AbstractTestITCase {

  protected String getServiceRoot() {
    return testStaticServiceRootURL;
  }

  @Test
  public void createAsAtom() {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final int id = 1;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

    createEntity(getServiceRoot(), format, original, "Customer");
    final ODataEntity actual = compareEntities(getServiceRoot(), format, original, id, null);

    cleanAfterCreate(format, actual, false, getServiceRoot());
  }

  @Test
  public void createAsJSON() {
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final int id = 2;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

    createEntity(getServiceRoot(), format, original, "Customer");
    final ODataEntity actual = compareEntities(getServiceRoot(), format, original, id, null);

    cleanAfterCreate(format, actual, false, getServiceRoot());
  }

  @Test
  public void createWithInlineAsAtom() {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final int id = 3;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", true);

    createEntity(getServiceRoot(), format, original, "Customer");
    final ODataEntity actual =
            compareEntities(getServiceRoot(), format, original, id, Collections.<String>singleton("Info"));

    cleanAfterCreate(format, actual, true, getServiceRoot());
  }

  @Test
  public void createWithInlineAsJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final int id = 4;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", true);

    createEntity(getServiceRoot(), format, original, "Customer");
    final ODataEntity actual =
            compareEntities(getServiceRoot(), format, original, id, Collections.<String>singleton("Info"));

    cleanAfterCreate(format, actual, true, getServiceRoot());
  }

  @Test
  public void createInlineWithoutLinkAsAtom() {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final int id = 5;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

    original.addLink(client.getObjectFactory().newDeepInsertEntity(
            "Info", getSampleCustomerInfo(id, "Sample Customer_Info")));

    createEntity(getServiceRoot(), format, original, "Customer");
    final ODataEntity actual =
            compareEntities(getServiceRoot(), format, original, id, Collections.<String>singleton("Info"));

    boolean found = false;

    for (ODataLink link : actual.getNavigationLinks()) {
      assertNotNull(link.getLink());
      if (link.getLink().toASCIIString().endsWith("Customer(" + id + ")/Info")) {
        found = true;
      }
    }

    assertTrue(found);

    cleanAfterCreate(format, actual, true, getServiceRoot());
  }

  @Test
  public void createInlineWithoutLinkAsJSON() {
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final int id = 6;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

    original.addLink(client.getObjectFactory().newDeepInsertEntity(
            "Info", getSampleCustomerInfo(id, "Sample Customer_Info")));

    createEntity(getServiceRoot(), format, original, "Customer");
    final ODataEntity actual =
            compareEntities(getServiceRoot(), format, original, id, Collections.<String>singleton("Info"));

    boolean found = false;

    for (ODataLink link : actual.getNavigationLinks()) {
      assertNotNull(link.getLink());
      if (link.getLink().toASCIIString().endsWith("Customer(" + id + ")/Info")) {
        found = true;
      }
    }

    assertTrue(found);

    cleanAfterCreate(format, actual, true, getServiceRoot());
  }

  @Test
  public void createWithNavigationAsAtom() {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final ODataEntity actual = createWithNavigationLink(format, 5);
    cleanAfterCreate(format, actual, false, getServiceRoot());
  }

  @Test
  public void createWithNavigationAsJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final ODataEntity actual = createWithNavigationLink(format, 6);
    cleanAfterCreate(format, actual, false, getServiceRoot());
  }

  @Test
  public void createWithFeedNavigationAsAtom() throws EdmPrimitiveTypeException {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final ODataEntity actual = createWithFeedNavigationLink(format, 7);
    cleanAfterCreate(format, actual, false, getServiceRoot());
  }

  @Test
  public void createWithFeedNavigationAsJSON() throws EdmPrimitiveTypeException {
    // this needs to be full, otherwise there is no mean to recognize links
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final ODataEntity actual = createWithFeedNavigationLink(format, 8);
    cleanAfterCreate(format, actual, false, getServiceRoot());
  }

  @Test
  public void createWithBackNavigationAsAtom() throws EdmPrimitiveTypeException {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final ODataEntity actual = createWithBackNavigationLink(format, 9);
    cleanAfterCreate(format, actual, true, getServiceRoot());
  }

  @Test
  public void createWithBackNavigationAsJSON() throws EdmPrimitiveTypeException {
    // this needs to be full, otherwise there is no mean to recognize links
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final ODataEntity actual = createWithBackNavigationLink(format, 10);
    cleanAfterCreate(format, actual, true, getServiceRoot());
  }

  @Test
  public void multiKeyAsAtom() {
    multiKey(ODataPubFormat.ATOM);
  }

  @Test
  public void multiKeyAsJSON() {
    multiKey(ODataPubFormat.JSON);
  }

  @Test
  public void createReturnNoContent() {
    final int id = 1;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

    final ODataEntityCreateRequest<ODataEntity> createReq = client.getCUDRequestFactory().getEntityCreateRequest(
            client.newURIBuilder(getServiceRoot()).appendEntitySetSegment("Customer").build(), original);
    createReq.setPrefer(client.newPreferences().returnNoContent());

    final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
    assertEquals(204, createRes.getStatusCode());
    assertEquals(client.newPreferences().returnNoContent(),
            createRes.getHeader(HeaderName.preferenceApplied).iterator().next());

    try {
      createRes.getBody();
      fail();
    } catch (NoContentException e) {
      assertNotNull(e);
    }

    final ODataDeleteResponse deleteRes = client.getCUDRequestFactory().getDeleteRequest(
            client.newURIBuilder(getServiceRoot()).appendEntitySetSegment("Customer").appendKeySegment(id).build()).
            execute();
    assertEquals(204, deleteRes.getStatusCode());
  }

  @Test
  public void issue135() {
    final int id = 2;
    final ODataEntity original = getSampleCustomerProfile(id, "Sample customer for issue 135", false);

    final URIBuilder uriBuilder = client.newURIBuilder(getServiceRoot()).appendEntitySetSegment("Customer");
    final ODataEntityCreateRequest<ODataEntity> createReq =
            client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), original);
    createReq.setFormat(ODataPubFormat.JSON_FULL_METADATA);
    createReq.setContentType(ContentType.APPLICATION_ATOM_XML.getMimeType());
    createReq.setPrefer(client.newPreferences().returnContent());

    try {
      final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
      assertEquals(201, createRes.getStatusCode());
    } catch (Exception e) {
      fail(e.getMessage());
    } finally {
      final ODataDeleteResponse deleteRes = client.getCUDRequestFactory().getDeleteRequest(
              client.newURIBuilder(getServiceRoot()).appendEntitySetSegment("Customer").appendKeySegment(id).
              build()).
              execute();
      assertEquals(204, deleteRes.getStatusCode());
    }
  }

  private ODataEntity createWithFeedNavigationLink(final ODataPubFormat format, final int id)
          throws EdmPrimitiveTypeException {

    final String sampleName = "Sample customer";
    final ODataEntity original = getSampleCustomerProfile(id, sampleName, false);

    final Set<Integer> keys = new HashSet<Integer>();
    keys.add(-100);
    keys.add(-101);

    for (Integer key : keys) {
      final ODataEntity order = client.getObjectFactory().
              newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService.Order"));

      order.getProperties().add(client.getObjectFactory().newPrimitiveProperty("OrderId",
              client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(key)));
      order.getProperties().add(client.getObjectFactory().newPrimitiveProperty("CustomerId",
              client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));

      final ODataEntityCreateRequest<ODataEntity> createReq = client.getCUDRequestFactory().getEntityCreateRequest(
              client.newURIBuilder(getServiceRoot()).appendEntitySetSegment("Order").build(), order);
      createReq.setFormat(format);

      original.addLink(client.getObjectFactory().newEntitySetNavigationLink(
              "Orders",
              createReq.execute().getBody().getEditLink()));
    }

    final ODataEntity created = createEntity(getServiceRoot(), format, original, "Customer");
    // now, compare the created one with the actual one and go deeply into the associated customer info.....
    final ODataEntity actual = compareEntities(getServiceRoot(), format, created, id, null);

    final URIBuilder uriBuilder = client.newURIBuilder(getServiceRoot());
    uriBuilder.appendEntitySetSegment("Customer").appendKeySegment(id).appendEntitySetSegment("Orders");

    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
            getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final ODataEntitySet entitySet = res.getBody();
    assertNotNull(entitySet);
    assertEquals(2, entitySet.getCount());

    for (ODataEntity entity : entitySet.getEntities()) {
      final Integer key = entity.getProperty("OrderId").getPrimitiveValue().toCastValue(Integer.class);
      final Integer customerId = entity.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class);
      assertTrue(keys.contains(key));
      assertEquals(Integer.valueOf(id), customerId);
      keys.remove(key);

      final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(
              URIUtils.getURI(getServiceRoot(), entity.getEditLink().toASCIIString()));

      deleteReq.setFormat(format);
      assertEquals(204, deleteReq.execute().getStatusCode());
    }

    return actual;
  }

  private ODataEntity createWithNavigationLink(final ODataPubFormat format, final int id) {
    final String sampleName = "Sample customer";

    final ODataEntity original = getSampleCustomerProfile(id, sampleName, false);
    original.addLink(client.getObjectFactory().newEntityNavigationLink(
            "Info", URI.create(getServiceRoot() + "/CustomerInfo(12)")));

    final ODataEntity created = createEntity(getServiceRoot(), format, original, "Customer");
    // now, compare the created one with the actual one and go deeply into the associated customer info.....
    final ODataEntity actual = compareEntities(getServiceRoot(), format, created, id, null);

    final URIBuilder uriBuilder = client.newURIBuilder(getServiceRoot());
    uriBuilder.appendEntitySetSegment("Customer").appendKeySegment(id).appendEntitySetSegment("Info");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final ODataEntity info = res.getBody();
    assertNotNull(info);

    boolean found = false;

    for (ODataProperty prop : info.getProperties()) {
      if ("CustomerInfoId".equals(prop.getName())) {
        assertEquals("12", prop.getValue().toString());
        found = true;
      }
    }

    assertTrue(found);

    return actual;
  }

  private ODataEntity createWithBackNavigationLink(final ODataPubFormat format, final int id)
          throws EdmPrimitiveTypeException {

    final String sampleName = "Sample customer";

    ODataEntity customer = getSampleCustomerProfile(id, sampleName, false);
    customer = createEntity(getServiceRoot(), format, customer, "Customer");

    ODataEntity order = client.getObjectFactory().newEntity(new FullQualifiedName(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.Order"));
    getClient().getBinder().add(order,
            client.getObjectFactory().newPrimitiveProperty("CustomerId",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));
    getClient().getBinder().add(order,
            client.getObjectFactory().newPrimitiveProperty("OrderId",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));

    order.addLink(client.getObjectFactory().newEntityNavigationLink(
            "Customer", URIUtils.getURI(getServiceRoot(), customer.getEditLink().toASCIIString())));

    order = createEntity(getServiceRoot(), format, order, "Order");

    final ODataEntity changes = client.getObjectFactory().newEntity(new FullQualifiedName(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer"));
    changes.setEditLink(customer.getEditLink());
    changes.addLink(client.getObjectFactory().newEntitySetNavigationLink(
            "Orders", URIUtils.getURI(getServiceRoot(), order.getEditLink().toASCIIString())));
    update(UpdateType.PATCH, changes, format, null);

    final ODataEntityRequest<ODataEntity> customerreq = client.getRetrieveRequestFactory().getEntityRequest(
            URIUtils.getURI(getServiceRoot(), order.getEditLink().toASCIIString() + "/Customer"));
    customerreq.setFormat(format);

    customer = customerreq.execute().getBody();

    assertEquals(Integer.valueOf(id),
            customer.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class));

    final ODataEntitySetRequest<ODataEntitySet> orderreq = client.getRetrieveRequestFactory().getEntitySetRequest(
            URIUtils.getURI(getServiceRoot(), customer.getEditLink().toASCIIString() + "/Orders"));
    orderreq.setFormat(format);

    final ODataRetrieveResponse<ODataEntitySet> orderres = orderreq.execute();
    assertEquals(200, orderres.getStatusCode());

    assertEquals(Integer.valueOf(id),
            orderres.getBody().getEntities().get(0).getProperty("OrderId").getPrimitiveValue().
            toCastValue(Integer.class));

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(
            URIUtils.getURI(getServiceRoot(), customer.getEditLink().toASCIIString() + "?$expand=Orders"));
    req.setFormat(format);

    customer = req.execute().getBody();

    boolean found = false;
    for (ODataLink link : customer.getNavigationLinks()) {
      if (link instanceof ODataInlineEntitySet && "Orders".equals(link.getName())) {
        found = true;
      }
    }
    assertTrue(found);

    return customer;
  }

  private void multiKey(final ODataPubFormat format) {
    final ODataEntity message = client.getObjectFactory().newEntity(new FullQualifiedName(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.Message"));

    getClient().getBinder().add(message,
            client.getObjectFactory().newPrimitiveProperty("MessageId",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(1000)));
    getClient().getBinder().add(message,
            client.getObjectFactory().newPrimitiveProperty("FromUsername",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("1")));
    getClient().getBinder().add(message,
            client.getObjectFactory().newPrimitiveProperty("ToUsername",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("xlodhxzzusxecbzptxlfxprneoxkn")));
    getClient().getBinder().add(message,
            client.getObjectFactory().newPrimitiveProperty("Subject",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("Test subject")));
    getClient().getBinder().add(message,
            client.getObjectFactory().newPrimitiveProperty("Body",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildString("Test body")));
    getClient().getBinder().add(message,
            client.getObjectFactory().newPrimitiveProperty("IsRead",
                    client.getObjectFactory().newPrimitiveValueBuilder().buildBoolean(false)));

    final URIBuilder builder =
            client.newURIBuilder(getServiceRoot()).appendEntitySetSegment("Message");
    final ODataEntityCreateRequest<ODataEntity> req = client.getCUDRequestFactory().
            getEntityCreateRequest(builder.build(), message);
    req.setFormat(format);

    final ODataEntityCreateResponse<ODataEntity> res = req.execute();
    assertNotNull(res);
    assertEquals(201, res.getStatusCode());

    final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
    multiKey.put("FromUsername", "1");
    multiKey.put("MessageId", 1000);

    final ODataDeleteResponse deleteRes = client.getCUDRequestFactory().
            getDeleteRequest(builder.appendKeySegment(multiKey).build()).execute();
    assertEquals(204, deleteRes.getStatusCode());
  }
}
