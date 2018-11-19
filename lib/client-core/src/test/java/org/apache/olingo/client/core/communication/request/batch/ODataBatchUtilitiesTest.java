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
package org.apache.olingo.client.core.communication.request.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.LineIterator;
import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.ODataClientBuilder;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.core.communication.request.invoke.ODataInvokeRequestImpl;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.junit.Test;

public class ODataBatchUtilitiesTest {
  
  @Test
  public void testBatchRequest(){
    ODataBatchUtilities util = new ODataBatchUtilities();
    Map<String, Collection<String>> value = new HashMap<String, Collection<String>>();
    util.addHeaderLine("header:name", value );
    value.put("header:name", null);
    util.addHeaderLine("header:name", value );
  }
  
  @Test
  public void testBatchConstants(){
    assertEquals("boundary", ODataBatchConstants.BOUNDARY);
    assertEquals("application/http", ODataBatchConstants.ITEM_CONTENT_TYPE );
    assertEquals("Content-Type: application/http", ODataBatchConstants.ITEM_CONTENT_TYPE_LINE );
    assertEquals("Content-Transfer-Encoding: binary", 
        ODataBatchConstants.ITEM_TRANSFER_ENCODING_LINE );
    assertEquals("Content-ID", ODataBatchConstants.CHANGESET_CONTENT_ID_NAME );
  }
  
 
  @Test
  public void testChangeSet() throws URISyntaxException{
    ODataClient client = ODataClientBuilder.createClient();
    URI uri = new URI("test");
    final InputStream input = getClass().getResourceAsStream("batchResponse.batch");
    Reader reader = new InputStreamReader(input);
    ODataBatchLineIterator iterator = new ODataBatchLineIteratorImpl(new LineIterator(reader ));
    ODataBatchRequest req = new ODataBatchRequestImpl(client, uri);;
    ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem(true);
    ODataChangesetImpl change = new ODataChangesetImpl(req , expectedResItem );
    assertNotNull(change);
    ODataBatchableRequest request = new ODataInvokeRequestImpl<ClientInvokeResult>(
        client, ClientInvokeResult.class, HttpMethod.POST, uri);
    change.addRequest(request);
    assertNotNull(change.getBodyStreamWriter());
    change.close();
    change.closeItem();
    assertNotNull(change.getLastContentId());
    assertTrue(change.hasStreamedSomething());
    assertFalse(change.isOpen());
    change.streamRequestHeader(request);
    change.streamRequestHeader("1");
    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testChangeSetNeg() throws URISyntaxException{
    ODataClient client = ODataClientBuilder.createClient();
    URI uri = new URI("test");
    ODataBatchRequest req = new ODataBatchRequestImpl(client, uri);;
    ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem(true);
    ODataChangesetImpl change = new ODataChangesetImpl(req , expectedResItem );
    assertNotNull(change);
    ODataBatchableRequest request = new ODataInvokeRequestImpl<ClientInvokeResult>(
        client, ClientInvokeResult.class, HttpMethod.GET, uri);
    change.addRequest(request);
    assertNotNull(change.getBodyStreamWriter());
    change.close();
    change.closeItem();
  }
  
  @Test(expected = IllegalStateException.class)
  public void testChangeSetCloseNeg() throws URISyntaxException{
    ODataClient client = ODataClientBuilder.createClient();
    URI uri = new URI("test");
    ODataBatchRequest req = new ODataBatchRequestImpl(client, uri);;
    ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem(true);
    ODataChangesetImpl change = new ODataChangesetImpl(req , expectedResItem );
    assertNotNull(change);
    assertNotNull(change.getBodyStreamWriter());
    change.close();
    change.closeItem();
    ODataBatchableRequest request = new ODataInvokeRequestImpl<ClientInvokeResult>(
        client, ClientInvokeResult.class, HttpMethod.POST, uri);
    change.addRequest(request);
  }
  
  @Test
  public void testChangeSetResponse() throws URISyntaxException{
    ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem(true);
    expectedResItem.setUnexpected();
    assertNotNull(expectedResItem);
    expectedResItem.close();
  }
}
