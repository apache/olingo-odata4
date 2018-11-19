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
package org.apache.olingo.client.core.communication.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.communication.request.AsyncRequestWrapperImpl.AsyncResponseWrapperImpl;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchRequestImpl;
import org.apache.olingo.client.core.communication.request.invoke.ODataInvokeRequestImpl;
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
    req.addRetrieve(request );
    req.addOutsideUpdate(request);
    assertNotNull(client.getAsyncRequestFactory().getAsyncRequestWrapper(request));
    ODataBatchRequestImpl batchRequest = new ODataBatchRequestImpl(client, uri);
    assertNotNull(client.getAsyncRequestFactory().getAsyncBatchRequestWrapper(batchRequest ));
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
  
  @Test
  public void testWrapper(){

    Wrapper wrap = new Wrapper();
    wrap.setWrapped("test");
    assertEquals("test", wrap.getWrapped());
  }
  
  @Test
  public void testException(){

    AsyncRequestException  ex = new AsyncRequestException ("Exception");
    assertEquals("Exception", ex.getMessage());
  }
  
}
