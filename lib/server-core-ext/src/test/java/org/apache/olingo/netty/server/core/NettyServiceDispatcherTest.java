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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.netty.server.api.ODataNetty;
import org.apache.olingo.netty.server.api.ODataNettyHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.MetadataParser;
import org.apache.olingo.server.core.SchemaBasedEdmProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class NettyServiceDispatcherTest {
  
  ServiceMetadata metadata = null;
  ODataNetty odata = ODataNetty.newInstance();
  SchemaBasedEdmProvider provider = null;
  
  @Before
  public void beforeTest() throws Exception {
    MetadataParser parser = new MetadataParser();
    parser.parseAnnotations(true);
    parser.useLocalCoreVocabularies(true);
    parser.implicitlyLoadCoreVocabularies(true);
    metadata = parser.buildServiceMetadata(new FileReader("src/test/resources/trippin.xml"));    
    provider = parser.buildEdmProvider(new FileReader("src/test/resources/trippin.xml"));
  }

  @Test
  public void testCreateHandler() {
    assertNotNull(odata.createHandler(metadata));
  }
  
  @Test
  public void testCreateRawHandler() {
    assertNotNull(odata.createRawHandler(metadata));
  }
  
  @Test
  public void testCreateDeserializer() throws DeserializerException {
    assertNotNull(odata.createDeserializer(ContentType.APPLICATION_JSON, metadata));
  }
  
  @Test
  public void testCreatePrimitiveTypeInstance() throws DeserializerException {
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date).getKind());
    assertEquals(EdmPrimitiveTypeKind.Date.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Binary));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Binary).getKind());
    assertEquals(EdmPrimitiveTypeKind.Binary.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Binary).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte).getKind());
    assertEquals(EdmPrimitiveTypeKind.Byte.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset).getKind());
    assertEquals(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean).getKind());
    assertEquals(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal).getKind());
    assertEquals(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double).getKind());
    assertEquals(EdmPrimitiveTypeKind.Double.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration).getKind());
    assertEquals(EdmPrimitiveTypeKind.Duration.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Geography));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Geography).getKind());
    assertEquals(EdmPrimitiveTypeKind.Geography.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Geography).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyCollection));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyCollection).getKind());
    assertEquals(EdmPrimitiveTypeKind.GeographyCollection.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyCollection).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyLineString));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyLineString).getKind());
    assertEquals(EdmPrimitiveTypeKind.GeographyLineString.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyLineString).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyMultiLineString));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyMultiLineString).getKind());
    assertEquals(EdmPrimitiveTypeKind.GeographyMultiLineString.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeographyMultiLineString).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryMultiPoint));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryMultiPoint).getKind());
    assertEquals(EdmPrimitiveTypeKind.GeometryMultiPoint.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryMultiPoint).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryMultiPolygon));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryMultiPolygon).getKind());
    assertEquals(EdmPrimitiveTypeKind.GeometryMultiPolygon.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryMultiPolygon).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid).getKind());
    assertEquals(EdmPrimitiveTypeKind.Guid.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16).getKind());
    assertEquals(EdmPrimitiveTypeKind.Int16.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32).getKind());
    assertEquals(EdmPrimitiveTypeKind.Int32.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64).getKind());
    assertEquals(EdmPrimitiveTypeKind.Int64.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte).getKind());
    assertEquals(EdmPrimitiveTypeKind.SByte.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Single));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Single).getKind());
    assertEquals(EdmPrimitiveTypeKind.Single.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Single).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Stream));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Stream).getKind());
    assertEquals(EdmPrimitiveTypeKind.Stream.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Stream).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String).getKind());
    assertEquals(EdmPrimitiveTypeKind.String.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.String).getFullQualifiedName());
    
    assertNotNull(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.TimeOfDay));
    assertEquals(EdmTypeKind.PRIMITIVE, 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.TimeOfDay).getKind());
    assertEquals(EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName(), 
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.TimeOfDay).getFullQualifiedName());
  }
  
  @Test
  public void testCreatePreferences() {
    List<String> preferHeaders = new ArrayList<String>();
    preferHeaders.add("return=representation");
    preferHeaders.add("odata.track-changes");
    assertNotNull(odata.createPreferences(preferHeaders));
  }
  
  @Test
  public void testCreateEdmAssistedSerializer() throws SerializerException {
    assertNotNull(odata.createEdmAssistedSerializer(ContentType.APPLICATION_JSON));
  }
  
  @Test
  public void testCreateEdmDeltaSerializer() throws SerializerException {
    List<String> versions = new ArrayList<String>();
    versions.add("4.01");
    assertNotNull(odata.createEdmDeltaSerializer(ContentType.APPLICATION_JSON, versions));
  }
  
  @Test
  public void testMetadata() throws Exception {
    DefaultFullHttpRequest nettyRequest = Mockito.mock(DefaultFullHttpRequest.class);
    io.netty.handler.codec.http.HttpMethod httpMethod = mock(io.netty.handler.codec.http.HttpMethod.class);
    when(httpMethod.name()).thenReturn("GET");
    when(nettyRequest.method()).thenReturn(httpMethod);
    HttpVersion httpVersion = mock(HttpVersion.class);
    when(httpVersion.text()).thenReturn("HTTP/1.0");
    when(nettyRequest.protocolVersion()).thenReturn(httpVersion);
    when(nettyRequest.uri()).thenReturn("/trippin/$metadata");
    HttpHeaders headers = mock(HttpHeaders.class);
    when(nettyRequest.headers()).thenReturn(headers);
    when(nettyRequest.content()).thenReturn(Unpooled.buffer());
    
    DefaultFullHttpResponse nettyResponse = mock(DefaultFullHttpResponse.class);
    when(nettyResponse.status()).thenReturn(HttpResponseStatus.OK);
    when(nettyResponse.headers()).thenReturn(headers);
    when(nettyResponse.content()).thenReturn(Unpooled.buffer());    
    
    Map<String, String> requestParams = new HashMap<String, String>();
    requestParams.put("contextPath", "/trippin");
    ODataNettyHandler handler = odata.createNettyHandler(metadata);
    handler.processNettyRequest(nettyRequest, nettyResponse, requestParams);
    assertNotNull(new String (nettyResponse.content().array()));
    assertEquals(200, nettyResponse.status().code());
    assertEquals("OK", nettyResponse.status().reasonPhrase());
  }
  
  @Test
  public void testCreateServiceMetadata() throws SerializerException {
    assertNotNull(odata.createServiceMetadata(provider, metadata.getReferences(), 
        metadata.getServiceMetadataETagSupport()));
  }
}
