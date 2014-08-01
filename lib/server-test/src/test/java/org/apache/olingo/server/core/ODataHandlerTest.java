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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Before;
import org.junit.Test;

public class ODataHandlerTest {

  private ODataHandler handler;

  @Before
  public void before() {
    OData odata = OData.newInstance();
    Edm edm = odata.createEdm(new EdmTechProvider());

    handler = new ODataHandler(odata, edm);
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
    assertEquals(500, response.getStatusCode());
  }

  @Test
  public void testServiceDocumentDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawBaseUri("http://localhost/odata");
    request.setRawODataPath("/");

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());

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
    assertEquals(500, response.getStatusCode());
  }

  @Test
  public void testMetadataDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
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
    assertEquals(400, response.getStatusCode());
  }

  @Test
  public void testContentNegotiationSupported() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.setRawQueryPath("$format=xml");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  public void testContentNegotiationNotSupported() {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");
    request.setRawQueryPath("$format=notSupported");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(406, response.getStatusCode());
  }

  @Test
  public void testWithApplicationExceptionInProcessor() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    MetadataProcessor metadataProcessor = mock(MetadataProcessor.class);
    doThrow(new ODataApplicationException("msg", 412, Locale.ENGLISH)).when(metadataProcessor).readMetadata(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    handler.register(metadataProcessor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(412, response.getStatusCode());
  }
}
