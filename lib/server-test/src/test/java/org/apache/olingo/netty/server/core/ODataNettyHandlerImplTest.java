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
package org.apache.olingo.netty.server.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.netty.server.api.ODataNetty;
import org.apache.olingo.netty.server.api.ODataNettyHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class ODataNettyHandlerImplTest {

  @Test
  public void testNettyReqResp_GetMethod() {
    MetadataProcessor processor = mock(MetadataProcessor.class);
    final ODataNetty odata = ODataNetty.newInstance();
    final ServiceMetadata metadata = odata.createServiceMetadata(
        new EdmTechProvider(), Collections.<EdmxReference> emptyList());

    ODataNettyHandler handler = odata.createNettyHandler(metadata);

    handler.register(processor);
    DefaultFullHttpRequest nettyRequest = mock(DefaultFullHttpRequest.class);
    io.netty.handler.codec.http.HttpMethod httpMethod = mock(io.netty.handler.codec.http.HttpMethod.class);
    when(httpMethod.name()).thenReturn("GET");
    when(nettyRequest.method()).thenReturn(httpMethod);
    HttpVersion httpVersion = mock(HttpVersion.class);
    when(httpVersion.text()).thenReturn("HTTP/1.0");
    when(nettyRequest.protocolVersion()).thenReturn(httpVersion);
    when(nettyRequest.uri()).thenReturn("/odata.svc/$metadata");
    HttpHeaders headers = mock(HttpHeaders.class);
    headers.add("Accept", "application/atom+xml");
    Set<String> set = new HashSet<String>();
    set.add("Accept");
    when(headers.names()).thenReturn(set);
    when(nettyRequest.headers()).thenReturn(headers);
    when(nettyRequest.content()).thenReturn(Unpooled.buffer());
    
    DefaultFullHttpResponse nettyResponse = mock(DefaultFullHttpResponse.class);
    when(nettyResponse.status()).thenReturn(HttpResponseStatus.OK);
    when(nettyResponse.headers()).thenReturn(headers);
    
    when(nettyResponse.content()).thenReturn(Unpooled.buffer());

    Map<String, String> requestParams = new HashMap<String, String>();
    requestParams.put("contextPath", "/odata.svc");
    handler.processNettyRequest(nettyRequest, nettyResponse, requestParams);
    
    nettyResponse.status();
    assertEquals(HttpStatusCode.OK.getStatusCode(), HttpResponseStatus.OK.code());
  }
  
  @Test
  public void testNettyReqResp_POSTMethod() {
    EntityProcessor processor = mock(EntityProcessor.class);
    final ODataNetty odata = ODataNetty.newInstance();
    final ServiceMetadata metadata = odata.createServiceMetadata(
        new EdmTechProvider(), Collections.<EdmxReference> emptyList());

    ODataNettyHandler handler = odata.createNettyHandler(metadata);

    handler.register(processor);
    HttpRequest nettyRequest = mock(DefaultFullHttpRequest.class);
    io.netty.handler.codec.http.HttpMethod httpMethod = mock(io.netty.handler.codec.http.HttpMethod.class);
    when(httpMethod.name()).thenReturn("POST");
    when(nettyRequest.method()).thenReturn(httpMethod);
    HttpVersion httpVersion = mock(HttpVersion.class);
    when(httpVersion.text()).thenReturn("HTTP/1.0");
    when(nettyRequest.protocolVersion()).thenReturn(httpVersion);
    when(nettyRequest.uri()).thenReturn("/odata.svc/ESAllPrim");
    HttpHeaders headers = mock(HttpHeaders.class);
    headers.set("Content-Type", "application/json");
    Set<String> set = new HashSet<String>();
    set.add("Content-Type");
    when(headers.names()).thenReturn(set);
    List<String> headerValues = new ArrayList<String>();
    headerValues.add("application/json");
    when(headers.getAll("Content-Type")).thenReturn(headerValues);
    when(nettyRequest.headers()).thenReturn(headers);
    String content = "{\"@odata.context\": \"$metadata#ESAllPrim/$entity\","
        + "\"PropertyInt16\": 32767,"
        + "\"PropertyString\": \"First Resource &&&- positive values\","
        + "\"PropertyBoolean\": true,"
        + "\"PropertyByte\": 255,"
        + "\"PropertySByte\": 127,"
    + "\"PropertyInt32\": 2147483647,"
    + "\"PropertyInt64\": 9223372036854775807,"
    + "\"PropertySingle\": 17900000,"
    + "\"PropertyDouble\": -179000,"
    + "\"PropertyDecimal\": 34,"
    + "\"PropertyBinary\": \"ASNFZ4mrze8=\","
    + "\"PropertyDate\": \"2012-12-03\","
    + "\"PropertyDateTimeOffset\": \"2012-12-03T07:16:23Z\","
    + "\"PropertyDuration\": \"PT6S\","
    + "\"PropertyGuid\": \"01234567-89ab-cdef-0123-456789abcdef\","
    + "\"PropertyTimeOfDay\": \"03:26:05\"}";
    byte[] arr = new byte[content.length()];
    arr = content.getBytes();
    
    when(((DefaultFullHttpRequest) nettyRequest).content()).thenReturn(Unpooled.copiedBuffer(arr));
    
    HttpResponse nettyResponse = mock(DefaultFullHttpResponse.class);
    when(nettyResponse.status()).thenReturn(HttpResponseStatus.CREATED);
    when(nettyResponse.headers()).thenReturn(headers);
    
    when(((DefaultFullHttpResponse) nettyResponse).content()).thenReturn(Unpooled.buffer());

    Map<String, String> requestParams = new HashMap<String, String>();
    requestParams.put("contextPath", "/odata.svc");
    handler.processNettyRequest(nettyRequest, nettyResponse, requestParams);
    
    nettyResponse.status();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), HttpResponseStatus.CREATED.code());
  }
}
