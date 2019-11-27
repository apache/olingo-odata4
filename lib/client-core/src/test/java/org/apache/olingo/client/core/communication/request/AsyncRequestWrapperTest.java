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
package org.apache.olingo.client.core.communication.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.olingo.client.api.Configuration;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.api.http.HttpUriRequestFactory;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.communication.request.AsyncRequestWrapperImpl.AsyncResponseWrapperImpl;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchRequestImpl;
import org.apache.olingo.client.core.communication.request.invoke.ODataInvokeRequestImpl;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.junit.Test;

public class AsyncRequestWrapperTest {

  @Test
  public void testBatchReq() throws URISyntaxException {

    ODataClient client = ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    AsyncBatchRequestWrapperImpl req = new AsyncBatchRequestWrapperImpl(client,
        client.getBatchRequestFactory().getBatchRequest("root"));
    assertNotNull(req.addChangeset());
    ODataBatchableRequest request = new ODataInvokeRequestImpl<ClientInvokeResult>(
        client, ClientInvokeResult.class, HttpMethod.GET, uri);
    req.addRetrieve(request);
    req.addOutsideUpdate(request);
    assertNotNull(client.getAsyncRequestFactory().getAsyncRequestWrapper(request));
    ODataBatchRequestImpl batchRequest = new ODataBatchRequestImpl(client, uri);
    assertNotNull(client.getAsyncRequestFactory().getAsyncBatchRequestWrapper(batchRequest));
    assertNotNull(req.wait(10));
  }

  @Test
  public void testReq() throws URISyntaxException {

    ODataClient client = ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    AsyncRequestWrapperImpl req = new AsyncRequestWrapperImpl(client,
        client.getBatchRequestFactory().getBatchRequest("root"));
    assertNotNull(req);
    ODataBatchableRequest request = new ODataInvokeRequestImpl<ClientInvokeResult>(
        client, ClientInvokeResult.class, HttpMethod.GET, uri);
    req.checkRequest(client, null);
    assertNotNull(req.callback(uri));
    req.extendHeader("header", "value");
    AsyncResponseWrapperImpl res = req.new AsyncResponseWrapperImpl();
    res.forceNextMonitorCheck(uri);
  }

  private AsyncRequestWrapperImpl createAsyncRequestWrapperImplWithRetryAfter(int retryAfter)
      throws IOException {

    HttpClient httpClient = mock(HttpClient.class);
    ODataClient oDataClient = mock(ODataClient.class);
    Configuration configuration = mock(Configuration.class);
    HttpClientFactory httpClientFactory = mock(HttpClientFactory.class);
    HttpUriRequestFactory httpUriRequestFactory = mock(HttpUriRequestFactory.class);
    HttpUriRequest httpUriRequest = mock(HttpUriRequest.class);

    when(oDataClient.getConfiguration()).thenReturn(configuration);
    when(configuration.getHttpClientFactory()).thenReturn(httpClientFactory);
    when(configuration.getHttpUriRequestFactory()).thenReturn(httpUriRequestFactory);
    when(httpClientFactory.create(any(), any())).thenReturn(httpClient);
    when(httpUriRequestFactory.create(any(), any())).thenReturn(httpUriRequest);

    HttpResponseFactory factory = new DefaultHttpResponseFactory();
    HttpResponse firstResponse = factory.newHttpResponse(
        new BasicStatusLine(HttpVersion.HTTP_1_1, 202, null), null);
    firstResponse.addHeader(HttpHeader.LOCATION, "http://localhost/monitor");
    firstResponse.addHeader(HttpHeader.RETRY_AFTER, String.valueOf(retryAfter));
    when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(firstResponse);

    AbstractODataRequest oDataRequest = mock(AbstractODataRequest.class);
    ODataResponse oDataResponse = mock(ODataResponse.class);
    when(oDataRequest.getResponseTemplate()).thenReturn(oDataResponse);
    when(oDataResponse.initFromHttpResponse(any(HttpResponse.class))).thenReturn(null);

    return new AsyncRequestWrapperImpl(oDataClient, oDataRequest);
  }

  @Test
  public void testTooBigRetryAfter() throws IOException {

    AsyncRequestWrapperImpl req = createAsyncRequestWrapperImplWithRetryAfter(Integer.MAX_VALUE);
    AsyncResponseWrapper wrappedResponse = req.execute();
    assertTrue(wrappedResponse instanceof AsyncResponseWrapperImpl);
    AsyncResponseWrapperImpl wrappedResponseImpl = (AsyncResponseWrapperImpl) wrappedResponse;
    assertEquals(AsyncResponseWrapperImpl.MAX_RETRY_AFTER, wrappedResponseImpl.retryAfter);
  }

  @Test
  public void testZeroRetryAfter() throws IOException {

    AsyncRequestWrapperImpl req = createAsyncRequestWrapperImplWithRetryAfter(0);
    AsyncResponseWrapper wrappedResponse = req.execute();
    assertTrue(wrappedResponse instanceof AsyncResponseWrapperImpl);
    AsyncResponseWrapperImpl wrappedResponseImpl = (AsyncResponseWrapperImpl) wrappedResponse;
    assertEquals(0, wrappedResponseImpl.retryAfter);
  }

  @Test
  public void testNegativeRetryAfter() throws IOException {

    AsyncRequestWrapperImpl req = createAsyncRequestWrapperImplWithRetryAfter(-1);
    AsyncResponseWrapper wrappedResponse = req.execute();
    assertTrue(wrappedResponse instanceof AsyncResponseWrapperImpl);
    AsyncResponseWrapperImpl wrappedResponseImpl = (AsyncResponseWrapperImpl) wrappedResponse;
    assertEquals(AsyncResponseWrapperImpl.DEFAULT_RETRY_AFTER, wrappedResponseImpl.retryAfter);
  }

  @Test
  public void testRetryAfter() throws IOException {

    int retryAfter = 7;
    assertNotEquals(retryAfter, AsyncResponseWrapperImpl.DEFAULT_RETRY_AFTER);
    AsyncRequestWrapperImpl req = createAsyncRequestWrapperImplWithRetryAfter(retryAfter);
    AsyncResponseWrapper wrappedResponse = req.execute();
    assertTrue(wrappedResponse instanceof AsyncResponseWrapperImpl);
    AsyncResponseWrapperImpl wrappedResponseImpl = (AsyncResponseWrapperImpl) wrappedResponse;
    assertEquals(retryAfter, wrappedResponseImpl.retryAfter);
  }

  @Test
  public void testWrapper() {

    Wrapper wrap = new Wrapper();
    wrap.setWrapped("test");
    assertEquals("test", wrap.getWrapped());
  }

  @Test
  public void testException() {

    AsyncRequestException ex = new AsyncRequestException("Exception");
    assertEquals("Exception", ex.getMessage());
  }

}
