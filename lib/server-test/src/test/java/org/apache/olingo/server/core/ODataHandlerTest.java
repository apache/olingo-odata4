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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.processor.EntitySetProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.PropertyProcessor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataHandlerTest {

  private ODataHandler handler;

  @Before
  public void before() {
    OData odata = OData.newInstance();
    ServiceMetadata metadata = odata.createServiceMetadata(
            new EdmTechProvider(), Collections.<EdmxReference>emptyList());

    handler = new ODataHandler(odata, metadata);
  }

  @Test
  public void testServiceDocumentNonDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawBaseUri("http://localhost/odata");
    request.setRawODataPath("/");

    ServiceDocumentProcessor processor = mock(ServiceDocumentProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testServiceDocumentDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawBaseUri("http://localhost/odata");
    request.setRawODataPath("/");

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    String ct = response.getHeaders().get(HttpHeader.CONTENT_TYPE);
    assertTrue(ct.contains("application/json"));
    assertTrue(ct.contains("odata.metadata=minimal"));

    assertNotNull(response.getContent());
    String doc = IOUtils.toString(response.getContent());

    assertTrue(doc.contains("\"@odata.context\" : \"http://localhost/odata/$metadata\""));
    assertTrue(doc.contains("\"value\" :"));
  }

  @Test
  public void testServiceDocumentRedirect() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawBaseUri("http://localhost/odata");
    request.setRawRequestUri("http://localhost/odata");
    request.setRawODataPath("");

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(HttpStatusCode.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusCode());
    assertEquals("http://localhost/odata/", response.getHeaders().get(HttpHeader.LOCATION));
  }

  @Test
  public void testMetadataNonDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    MetadataProcessor processor = mock(MetadataProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testMetadataDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(HttpContentType.APPLICATION_XML, response.getHeaders().get(HttpHeader.CONTENT_TYPE));

    assertNotNull(response.getContent());
    String doc = IOUtils.toString(response.getContent());

    assertTrue(doc.contains("<edmx:Edmx Version=\"4.0\""));
  }

  @Test
  public void testMaxVersionNone() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    ODataResponse response = handler.process(request);
    assertNotNull(response);

    assertEquals(ODataServiceVersion.V40.toString(), response.getHeaders().get(HttpHeader.ODATA_VERSION));
  }

  @Test
  public void testMaxVersionSupported() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.addHeader(HttpHeader.ODATA_MAX_VERSION, Arrays.asList(ODataServiceVersion.V40.toString()));

    ODataResponse response = handler.process(request);
    assertNotNull(response);

    assertEquals(ODataServiceVersion.V40.toString(), response.getHeaders().get(HttpHeader.ODATA_VERSION));
  }

  @Test
  public void testMaxVersionNotSupported() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.addHeader(HttpHeader.ODATA_MAX_VERSION, Arrays.asList(ODataServiceVersion.V30.toString()));

    ODataResponse response = handler.process(request);
    assertNotNull(response);

    assertEquals(ODataServiceVersion.V40.toString(), response.getHeaders().get(HttpHeader.ODATA_VERSION));
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testContentNegotiationSupported() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.setRawQueryPath("$format=xml");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testContentNegotiationNotSupported() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.setRawQueryPath("$format=not/Supported");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testContentNegotiationNotSupported2() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.setRawQueryPath("$format=notSupported");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testUnregisteredProcessor() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESAllPrim");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testCount() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESAllPrim/$count");

    EntitySetProcessor processor = mock(EntitySetProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    Mockito.verify(processor).countEntitySet(
        Mockito.eq(request),
        Mockito.any(ODataResponse.class),
        Mockito.any(UriInfo.class));
  }

  @Test
  public void dispatchCountWithNavigation() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESAllPrim/NavPropertyETTwoPrimMany/$count");

    EntitySetProcessor processor = mock(EntitySetProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    Mockito.verify(processor).countEntitySet(
        Mockito.eq(request),
        Mockito.any(ODataResponse.class),
        Mockito.any(UriInfo.class));
  }

  @Test
  public void dispatchAddressPrimitiveProperty() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESAllPrim/PropertyInt16");

    PropertyProcessor processor = mock(PropertyProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);

    Mockito.verify(processor).readProperty(
            Mockito.any(ODataRequest.class),
            Mockito.any(ODataResponse.class),
            Mockito.any(UriInfo.class),
            Mockito.any(ContentType.class));
  }

  @Test
  public void dispatchAddressPrimitivePropertyValue() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESAllPrim/PropertyInt16/$value");

    PropertyProcessor processor = mock(PropertyProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);

    Mockito.verify(processor).readPropertyValue(
            Mockito.any(ODataRequest.class),
            Mockito.any(ODataResponse.class),
            Mockito.any(UriInfo.class),
            Mockito.any(ContentType.class));
  }

  @Test
  public void dispatchAddressComplexProperty() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESMixPrimCollComp/PropertyComp");

    PropertyProcessor processor = mock(PropertyProcessor.class);
    handler.register(processor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);

    Mockito.verify(processor).readProperty(
            Mockito.any(ODataRequest.class),
            Mockito.any(ODataResponse.class),
            Mockito.any(UriInfo.class),
            Mockito.any(ContentType.class));
  }
}