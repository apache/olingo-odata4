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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Before;
import org.junit.Test;

public class ODataHandlerExceptionHandlingTest {
  private ODataHandler handler;

  @Before
  public void before() {
    OData odata = OData.newInstance();
    Edm edm = odata.createEdm(new EdmTechProvider());

    handler = new ODataHandler(odata, edm);
  }

  @Test
  public void wrongHttpMethodForMetadataDocument() throws Exception {
    ODataRequest request = new ODataRequest();
    request.setMethod(HttpMethod.POST);
    request.setRawODataPath("$metadata");
    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.PUT);
    request.setRawODataPath("$metadata");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.PATCH);
    request.setRawODataPath("$metadata");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.MERGE);
    request.setRawODataPath("$metadata");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.DELETE);
    request.setRawODataPath("$metadata");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());
  }

  @Test
  public void wrongHttpMethodForServiceDocument() throws Exception {
    ODataRequest request = new ODataRequest();
    request.setMethod(HttpMethod.POST);
    request.setRawODataPath("");
    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.PUT);
    request.setRawODataPath("");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.PATCH);
    request.setRawODataPath("");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.MERGE);
    request.setRawODataPath("");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());

    request = new ODataRequest();
    request.setMethod(HttpMethod.DELETE);
    request.setRawODataPath("");
    response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());
  }

  @Test
  public void testUriParserExceptionResultsInRightResponseNotFound() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("NotFound");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testUriParserExceptionResultsInRightResponseBadRequest() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("ESAllPrim('122')");

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testUriParserExceptionResultsInRightResponseEdmCause() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("EdmException");

    OData odata = OData.newInstance();
    Edm edm = odata.createEdm(new EdmProvider() {
      public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
          throws ODataException {
        throw new ODataException("msg");
      }
    });

    ODataHandler localHandler = new ODataHandler(odata, edm);

    ODataResponse response = localHandler.process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void testWithApplicationExceptionInProcessor() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    MetadataProcessor metadataProcessor = mock(MetadataProcessor.class);
    doThrow(new ODataApplicationException("msg", 425, Locale.ENGLISH)).when(metadataProcessor).readMetadata(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    handler.register(metadataProcessor);

    ODataResponse response = handler.process(request);
    assertNotNull(response);
    assertEquals(425, response.getStatusCode());
  }
}
