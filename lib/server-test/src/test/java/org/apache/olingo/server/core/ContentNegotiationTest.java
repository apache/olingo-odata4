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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.CollectionProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.processor.SupportCustomContentTypes;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Ignore;
import org.junit.Test;

public class ContentNegotiationTest {

//  static final private String ACCEPT_CASE1 = "text/plain;q=0.5";
//  static final private String ACCEPT_CASE2 = "application/json;odata=verbose;q=0.2";

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)

  String[][] casesServiceDocument = {
      /* expected            $format          accept              supported */
      { "application/json",  null,            null,               null },
      { "application/json",  "json",          null,               null },
      { "application/json",  "json",          "application/json", null },
      { "application/json",  null,            "application/json", null },
      { "application/json",  null,            "*/*",              null },
//    { "aaa",               "aaa",           null,               "aaa, bbb" },
//    { "aaa",               null,            "*/*",              "aaa, bbb" },
  };

  String[][] casesMetadata = {
      /* expected            $format          accept              supported */
      { "application/xml", null,            null,              null },
      { "application/xml", "xml",           null,              null },
      { "application/xml", null,            "application/xml", null },
      { "application/xml", "xml",           "application/xml", null },
      { "application/xml",  null,            "*/*",              null },
//    { "aaa",             "aaa",           null,              "aaa, bbb" },
  };

  String[][] casesEntitySet = {
      /* expected            $format          accept              supported */
      { "application/json",  null,            null,               null },
      { "application/json",  "json",          null,               null },
      { "application/json",  "json",          "application/json", null },
      { "application/json",  null,            "*/*",              null },
  };
  
  //CHECKSTYLE:ON
  //@formatter:on

  private ODataHandler createHandler() {
    OData odata = OData.newInstance();
    Edm edm = odata.createEdm(new EdmTechProvider());
    return new ODataHandler(odata, edm);

  }

  @Test
  public void testServiceDocumentDefault() {

    for (String[] useCase : casesServiceDocument) {
      ODataRequest request = new ODataRequest();
      request.setMethod(HttpMethod.GET);
      request.setRawODataPath("/" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

      ODataResponse response = callHandler(useCase, request);

      assertEquals(useCase[0], response.getHeaders().get(HttpHeader.CONTENT_TYPE));
    }
  }

  @Test
  public void testMetadataDefault() {

    for (String[] useCase : casesMetadata) {
      ODataRequest request = new ODataRequest();
      request.setMethod(HttpMethod.GET);
      request.setRawODataPath("/$metadata" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

      ODataResponse response = callHandler(useCase, request);

      assertEquals(useCase[0], response.getHeaders().get(HttpHeader.CONTENT_TYPE));
    }
  }

  @Test
  public void testEntitySet() {

    for (String[] useCase : casesEntitySet) {
      ODataRequest request = new ODataRequest();
      request.setMethod(HttpMethod.GET);
      request.setRawODataPath("/ESAllPrim" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

      ODataResponse response = callHandler(useCase, request, new CollectionProcessorStub());

      assertEquals(useCase[0], response.getHeaders().get(HttpHeader.CONTENT_TYPE));
    }
  }

  private ODataResponse callHandler(String[] useCase, ODataRequest request,
      Processor defaultProcessor) {
    ODataHandler handler = createHandler();

    if (useCase[3] != null) {
      ProcessorStub stub = new ProcessorStub(useCase[3].split(","));
      handler.register(stub);
    } else {
      if (defaultProcessor != null) {
        handler.register(defaultProcessor);
      }
    }

    ODataResponse response = handler.process(request);
    return response;
  }

  ODataResponse callHandler(String[] useCase, ODataRequest request) {
    return callHandler(useCase, request, null);
  }

  private class ProcessorStub implements ServiceDocumentProcessor, MetadataProcessor, CollectionProcessor,
      SupportCustomContentTypes {

    String[] formats;

    ProcessorStub(String[] strings) {
      this.formats = strings;
    }

    @Override
    public void init(OData odata, Edm edm) {}

    @Override
    public List<String> getSupportedContentTypes(Class<? extends Processor> processorClass) {
      return Arrays.asList(formats);
    }

    @Override
    public void readServiceDocument(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format);
    }

    @Override
    public void readCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format);
    }

    @Override
    public void readMetadata(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format);
    }

  }

  private class CollectionProcessorStub implements CollectionProcessor {

    @Override
    public void init(OData odata, Edm edm) {}

    @Override
    public void readCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format);
    }
  }

}
