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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.StatusLine;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.communication.header.ODataErrorResponseChecker;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.ex.ODataErrorDetail;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class ErrorTest extends AbstractTest {

  private ODataError error(final String name, final ContentType contentType) throws ODataDeserializerException {
    final ODataError error = client.getDeserializer(contentType).toError(
            getClass().getResourceAsStream(name + "." + getSuffix(contentType)));
    assertNotNull(error);
    return error;
  }

  private ODataError simple(final ContentType contentType) throws ODataDeserializerException {
    final ODataError error = error("error", contentType);
    assertEquals("501", error.getCode());
    assertEquals("Unsupported functionality", error.getMessage());
    assertEquals("query", error.getTarget());
    
    // verify details
    final ODataErrorDetail detail = error.getDetails().get(0);
    assertEquals("Code should be correct", "301", detail.getCode());
    assertEquals("Target should be correct", "$search", detail.getTarget());
    assertEquals("Message should be correct", "$search query option not supported", detail.getMessage());
    return error;
  }

  @Test
  public void jsonSimple() throws Exception {
    final ODataError error = simple(ContentType.JSON);

    // verify inner error dictionary
    final Map<String, String> innerErr = error.getInnerError();
    assertEquals("innerError dictionary size should be correct", 2, innerErr.size());
    assertEquals("innerError['context'] should be correct",
        "{\"key1\":\"for debug deployment only\"}", innerErr.get("context"));
    assertEquals("innerError['trace'] should be correct",
        "[\"callmethod1 etc\",\"callmethod2 etc\"]", innerErr.get("trace"));    
  }

  @Test
  public void atomSimple() throws Exception {
    simple(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void test1OLINGO1102() throws Exception {
    ODataClient odataClient = ODataClientFactory.getClient();
    InputStream entity = getClass().getResourceAsStream("500error." + getSuffix(ContentType.JSON));
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(500);
    when(statusLine.toString()).thenReturn("Internal Server Error");
    
    ODataClientErrorException exp = (ODataClientErrorException) ODataErrorResponseChecker.
        checkResponse(odataClient, statusLine, entity, "Json");
    assertTrue(exp.getMessage().contains("(500) Internal Server Error"));
    ODataError error = exp.getODataError();
    assertTrue(error.getMessage().startsWith("Internal Server Error"));
    assertEquals(500, Integer.parseInt(error.getCode()));
    assertEquals(2, error.getInnerError().size());
    assertEquals("\"Method does not support entities of specific type\"", error.getInnerError().get("message"));
    assertEquals("\"FaultException\"", error.getInnerError().get("type"));
    assertNull(error.getDetails());
        
  }
  
  @Test
  public void test2OLINGO1102() throws Exception {
    ODataClient odataClient = ODataClientFactory.getClient();
    InputStream entity = getClass().getResourceAsStream("500error1." + getSuffix(ContentType.JSON));
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(500);
    when(statusLine.toString()).thenReturn("Internal Server Error");
        
    ODataServerErrorException exp = (ODataServerErrorException) ODataErrorResponseChecker.
        checkResponse(odataClient, statusLine, entity, "Json");
    assertTrue(exp.getMessage().startsWith("Internal Server Error"));
  }
  
  @Test
  public void testWithNull() throws Exception {
    ODataClient odataClient = ODataClientFactory.getClient();
    StatusLine statusLine = mock(StatusLine.class);
    when(statusLine.getStatusCode()).thenReturn(500);
    when(statusLine.toString()).thenReturn("Internal Server Error");
        
    ODataRuntimeException exp = ODataErrorResponseChecker.
        checkResponse(odataClient, statusLine, null, "Json");
    assertTrue(exp.getMessage().startsWith("Internal Server Error"));
  }
}
