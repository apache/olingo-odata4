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
package org.apache.olingo.client.core.it.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.communication.request.ODataStreamManager;
import org.apache.olingo.client.api.communication.request.batch.BatchStreamManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.batch.ODataRetrieve;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v3.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.request.Wrapper;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.client.core.communication.request.batch.ODataRetrieveResponseItem;
import org.apache.olingo.client.core.communication.request.retrieve.ODataEntityRequestImpl;
import org.apache.olingo.client.core.communication.request.retrieve.ODataEntityRequestImpl.ODataEntityResponseImpl;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Ignore;
import org.junit.Test;

public class BatchTestITCase extends AbstractTestITCase {

  private static final String PREFIX = "!!PREFIX!!";

  private static final String SUFFIX = "!!SUFFIX!!";

  private static final int MAX = 10000;

  @Test
  public void stringStreaming() {
    final TestStreamManager streaming = new TestStreamManager();

    new StreamingThread(streaming).start();

    streaming.addObject((PREFIX + "\n").getBytes());

    for (int i = 0; i <= MAX; i++) {
      streaming.addObject((i + ") send info\n").getBytes());
    }

    streaming.addObject(SUFFIX.getBytes());
    streaming.finalizeBody();
  }

  @Test
  public void emptyBatchRequest() {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testStaticServiceRootURL);

    final BatchStreamManager payload = request.execute();
    final ODataBatchResponse response = payload.getResponse();

    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertFalse(iter.hasNext());
  }

  @Test
  public void changesetWithError() {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testStaticServiceRootURL);

    final BatchStreamManager payload = request.execute();
    final ODataChangeset changeset = payload.addChangeset();

    URIBuilder targetURI;
    ODataEntityCreateRequest<ODataEntity> createReq;

    targetURI = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customer");
    for (int i = 1; i <= 2; i++) {
      // Create Customer into the changeset
      createReq = client.getCUDRequestFactory().getEntityCreateRequest(
              targetURI.build(),
              getSampleCustomerProfile(100 + i, "Sample customer", false));
      createReq.setFormat(ODataPubFormat.JSON);
      changeset.addRequest(createReq);
    }

    targetURI = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("WrongEntitySet");
    createReq = client.getCUDRequestFactory().getEntityCreateRequest(
            targetURI.build(),
            getSampleCustomerProfile(105, "Sample customer", false));
    createReq.setFormat(ODataPubFormat.JSON);
    changeset.addRequest(createReq);

    targetURI = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customer");
    for (int i = 3; i <= 4; i++) {
      // Create Customer into the changeset
      createReq = client.getCUDRequestFactory().getEntityCreateRequest(
              targetURI.build(),
              getSampleCustomerProfile(100 + i, "Sample customer", false));
      createReq.setFormat(ODataPubFormat.ATOM);
      changeset.addRequest(createReq);
    }

    final ODataBatchResponse response = payload.getResponse();
    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    final ODataChangesetResponseItem chgResponseItem = (ODataChangesetResponseItem) iter.next();

    final ODataResponse res = chgResponseItem.next();
    assertEquals(404, res.getStatusCode());
    assertEquals("Not Found", res.getStatusMessage());
    assertEquals(Integer.valueOf(3), Integer.valueOf(
            res.getHeader(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME).iterator().next()));
    assertFalse(chgResponseItem.hasNext());
  }

  @Test
  @Ignore
  @SuppressWarnings("unchecked")
  public void changesetWithReference() throws EdmPrimitiveTypeException {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testStaticServiceRootURL);
    final BatchStreamManager streamManager = request.execute();

    final ODataChangeset changeset = streamManager.addChangeset();
    ODataEntity customer = getSampleCustomerProfile(20, "sample customer", false);

    URIBuilder uriBuilder = client.getURIBuilder(testAuthServiceRootURL).appendEntitySetSegment("Customer");

    // add create request
    final ODataEntityCreateRequest<ODataEntity> createReq =
            client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), customer);

    changeset.addRequest(createReq);

    // retrieve request reference
    int createRequestRef = changeset.getLastContentId();

    // add update request: link CustomerInfo(17) to the new customer
    final ODataEntity customerChanges = client.getObjectFactory().newEntity(customer.getTypeName());
    customerChanges.addLink(client.getObjectFactory().newEntityNavigationLink(
            "Info",
            client.getURIBuilder(testAuthServiceRootURL).appendEntitySetSegment("CustomerInfo").
            appendKeySegment(17).build()));

    final ODataEntityUpdateRequest updateReq = client.getCUDRequestFactory().getEntityUpdateRequest(
            URI.create("$" + createRequestRef), UpdateType.PATCH, customerChanges);

    changeset.addRequest(updateReq);

    final ODataBatchResponse response = streamManager.getResponse();
    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());

    // verify response payload ...
    final Iterator<ODataBatchResponseItem> iter = response.getBody();

    final ODataBatchResponseItem item = iter.next();
    assertTrue(item instanceof ODataChangesetResponseItem);

    final ODataChangesetResponseItem chgitem = (ODataChangesetResponseItem) item;

    ODataResponse res = chgitem.next();
    assertEquals(201, res.getStatusCode());
    assertTrue(res instanceof ODataEntityCreateResponse);

    customer = ((ODataEntityCreateResponse<ODataEntity>) res).getBody();

    ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(
            URIUtils.getURI(testStaticServiceRootURL, customer.getEditLink().toASCIIString() + "/Info"));

    assertEquals(Integer.valueOf(17),
            req.execute().getBody().getProperty("CustomerInfoId").getPrimitiveValue().toCastValue(Integer.class));

    res = chgitem.next();
    assertEquals(204, res.getStatusCode());
    assertTrue(res instanceof ODataEntityUpdateResponse);

    // clean ...
    assertEquals(204, client.getCUDRequestFactory().getDeleteRequest(
            URIUtils.getURI(testStaticServiceRootURL, customer.getEditLink().toASCIIString())).execute().
            getStatusCode());

    try {
      client.getRetrieveRequestFactory().getEntityRequest(
              URIUtils.getURI(testStaticServiceRootURL, customer.getEditLink().toASCIIString())).
              execute().getBody();
      fail();
    } catch (Exception e) {
      // ignore
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void batchRequest() throws EdmPrimitiveTypeException {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(testStaticServiceRootURL);

    final BatchStreamManager streamManager = request.execute();

    // -------------------------------------------
    // Add retrieve item
    // -------------------------------------------
    ODataRetrieve retrieve = streamManager.addRetrieve();

    // prepare URI
    URIBuilder targetURI = client.getURIBuilder(testStaticServiceRootURL);
    targetURI.appendEntitySetSegment("Customer").appendKeySegment(-10).
            expand("Logins").select("CustomerId,Logins/Username");

    // create new request
    ODataEntityRequest<ODataEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());
    queryReq.setFormat(ODataPubFormat.ATOM);

    retrieve.setRequest(queryReq);
    // -------------------------------------------

    // -------------------------------------------
    // Add changeset item
    // -------------------------------------------
    final ODataChangeset changeset = streamManager.addChangeset();

    // Update Product into the changeset
    targetURI = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Product").appendKeySegment(-10);
    final URI editLink = targetURI.build();

    final ODataEntity merge = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
    merge.setEditLink(editLink);

    merge.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
            "Description",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("new description from batch")));

    final ODataEntityUpdateRequest changeReq =
            client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.MERGE, merge);
    changeReq.setFormat(ODataPubFormat.JSON_FULL_METADATA);
    changeReq.setIfMatch(getETag(editLink));

    changeset.addRequest(changeReq);

    // Create Customer into the changeset
    targetURI = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customer");
    final ODataEntity original = getSampleCustomerProfile(1000, "Sample customer", false);
    final ODataEntityCreateRequest<ODataEntity> createReq =
            client.getCUDRequestFactory().getEntityCreateRequest(targetURI.build(), original);
    createReq.setFormat(ODataPubFormat.ATOM);
    changeset.addRequest(createReq);
    // -------------------------------------------

    // -------------------------------------------
    // Add retrieve item
    // -------------------------------------------
    retrieve = streamManager.addRetrieve();

    // prepare URI
    targetURI = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Product").appendKeySegment(-10);

    // create new request
    queryReq = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());

    retrieve.setRequest(queryReq);
    // -------------------------------------------

    final ODataBatchResponse response = streamManager.getResponse();
    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());
    final Iterator<ODataBatchResponseItem> iter = response.getBody();

    // retrive the first item (ODataRetrieve)
    ODataBatchResponseItem item = iter.next();
    assertTrue(item instanceof ODataRetrieveResponseItem);

    ODataRetrieveResponseItem retitem = (ODataRetrieveResponseItem) item;
    ODataResponse res = retitem.next();
    assertTrue(res instanceof ODataEntityResponseImpl);
    assertEquals(200, res.getStatusCode());
    assertEquals("OK", res.getStatusMessage());

    ODataEntityRequestImpl<ODataEntity>.ODataEntityResponseImpl entres =
            (ODataEntityRequestImpl.ODataEntityResponseImpl) res;

    ODataEntity entity = entres.getBody();
    assertEquals(-10, entity.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class), 0);

    // retrieve the second item (ODataChangeset)
    item = iter.next();
    assertTrue(item instanceof ODataChangesetResponseItem);

    final ODataChangesetResponseItem chgitem = (ODataChangesetResponseItem) item;
    res = chgitem.next();
    assertTrue(res instanceof ODataEntityUpdateResponse);
    assertEquals(204, res.getStatusCode());
    assertEquals("No Content", res.getStatusMessage());

    res = chgitem.next();
    assertTrue(res instanceof ODataEntityCreateResponse);
    assertEquals(201, res.getStatusCode());
    assertEquals("Created", res.getStatusMessage());

    final ODataEntityCreateResponse<ODataEntity> createres = (ODataEntityCreateResponse<ODataEntity>) res;
    entity = createres.getBody();
    assertEquals(new Integer(1000), entity.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class));

    // retrive the third item (ODataRetrieve)
    item = iter.next();
    assertTrue(item instanceof ODataRetrieveResponseItem);

    retitem = (ODataRetrieveResponseItem) item;
    res = retitem.next();
    assertTrue(res instanceof ODataEntityResponseImpl);
    assertEquals(200, res.getStatusCode());
    assertEquals("OK", res.getStatusMessage());

    entres = (ODataEntityRequestImpl.ODataEntityResponseImpl) res;
    entity = entres.getBody();
    assertEquals("new description from batch",
            entity.getProperty("Description").getPrimitiveValue().toCastValue(String.class));

    assertFalse(iter.hasNext());
  }

  private static class TestStreamManager extends AbstractODataStreamManager<ODataBatchResponse> {

    public TestStreamManager() {
      super(new Wrapper<Future<HttpResponse>>());
    }

    public ODataStreamManager<ODataBatchResponse> addObject(byte[] src) {
      stream(src);
      return this;
    }

    @Override
    protected ODataBatchResponse getResponse(long timeout, TimeUnit unit) {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  };

  /**
   * To be used for debug purposes.
   */
  private static class StreamingThread extends Thread {

    private final TestStreamManager streaming;

    public StreamingThread(final TestStreamManager streaming) {
      this.streaming = streaming;
    }

    @Override
    public void run() {
      try {
        final StringBuilder builder = new StringBuilder();

        byte[] buff = new byte[1024];

        int len;

        while ((len = streaming.getBody().read(buff)) >= 0) {
          builder.append(new String(buff, 0, len));
        }

        assertTrue(builder.toString().startsWith(PREFIX));
        assertTrue(builder.toString().contains((MAX / 2) + ") send info"));
        assertTrue(builder.toString().contains((MAX / 3) + ") send info"));
        assertTrue(builder.toString().contains((MAX / 20) + ") send info"));
        assertTrue(builder.toString().contains((MAX / 30) + ") send info"));
        assertTrue(builder.toString().contains(MAX + ") send info"));
        assertTrue(builder.toString().endsWith(SUFFIX));

      } catch (IOException e) {
        fail();
      }
    }
  }

  private static class BatchStreamingThread extends Thread {

    private final BatchStreamManager streaming;

    public BatchStreamingThread(final BatchStreamManager streaming) {
      this.streaming = streaming;
    }

    @Override
    public void run() {
      try {
        final StringBuilder builder = new StringBuilder();

        byte[] buff = new byte[1024];

        int len;

        while ((len = streaming.getBody().read(buff)) >= 0) {
          builder.append(new String(buff, 0, len));
        }

        LOG.debug("Batch request {}", builder.toString());

        assertTrue(builder.toString().contains("Content-Id:2"));
        assertTrue(builder.toString().contains("GET " + testStaticServiceRootURL));
      } catch (IOException e) {
        fail();
      }
    }
  }
}
