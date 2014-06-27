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

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.CollectionProcessor;
import org.apache.olingo.server.api.processor.CustomContentTypeSupport;
import org.apache.olingo.server.api.processor.FormatContentTypeMapping;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentNegotiatorTest {

  static final private String ACCEPT_CASE_JSON = "application/json;odata=verbose;q=0.2";
  static final private String ACCEPT_CASE_XML = "application/xml";
  static final private String ACCEPT_CASE_TEXT = "text/plain;q=0.5";
  static final private String ACCEPT_CASE_MULTI = "text/plain;q=0.5,application/aaa;q=0.4";

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)

  String[][] casesServiceDocument = {
      /* expected               $format           accept                 alias        ct mapping    */
      { "application/json",     null,             null,                  null         ,null             },
      { "application/json",     "json",           null,                  null         ,null             },
      { "application/json",     "json",           "application/json",    null         ,null             },
      { "a",                    "a",              null,                  "a"          ,"a/a"            },

//      { "application/json",     null,             "application/json",    null         ,null             },
//      { "application/json",     null,             ACCEPT_CASE_JSON,      null         ,null             },
//      { "application/json",     null,             "*/*",                 null         ,null             },
//      { "a/a",                  "a",               null,                 "a, b"       ,"a/a,b/b"        },
//      { "a",                    null,             "*/*",                 "a, b"       ,null             },
//      { "a",                    "a",              "*/*",                 "a, b"       ,null             },
  };                                                                                          

  String[][] casesMetadata = {                                                                 
      /* expected               $format           accept                 alias        ct mapping    */
      { "application/xml",      null,             null,                  null         ,null             },
      { "application/xml",      "xml",            null,                  null         ,null             },
      { "application/xml",      null,             "application/xml",     null         ,null             },
      { "application/xml",      "xml",            "application/xml",     null         ,null             },
      { "application/xml",      null,             ACCEPT_CASE_XML,       null         ,null             },
      { "application/xml",      null,             "*/*",                 null         ,null             },
      { "a",                    "a",              null,                  "a, b"       ,null             },
      { "a",                    "a",              null,                  "a, b"       ,null             },
      { "a",                    null,             "*/*",                 "a, b"       ,null             },
      { "a",                    "a",              "*/*",                 "a, b"       ,null             },
  };

//  String[][] casesEntitySet = {                                                               
//      /* expected               $format           accept                 supported    $formatmapping    */
//      { "application/json",     null,             null,                  null         ,null             },
//      { "application/json",     "json",           null,                  null         ,null             },
//      { "application/json",     "json",           "application/json",    null         ,null             },
//      { "application/json",     null,             "application/json",    null         ,null             },
//      { "application/json",     null,             ACCEPT_CASE_JSON,      null         ,null             },
//      { "application/json",     null,             "*/*",                 null         ,null             },
//      { "a",                    "a",              null,                  "a, b"       ,null             },
//      { "a",                    null,             "*/*",                 "a, b"       ,null             },
//      { "a",                    "a",              "*/*",                 "a, b"       ,null             },
//  };  
  //CHECKSTYLE:ON
  //@formatter:on

  private final static Logger LOG = LoggerFactory.getLogger(ContentNegotiatorTest.class);

  @Test
  public void testServiceDocumentSingleCase() {
    String[] useCase = { "application/json", null, null, null, null };

    testContentNegotiation(useCase, ServiceDocumentProcessor.class);
  }

  @Test
  public void testServiceDocumentDefault() {
    for (String[] useCase : casesServiceDocument) {
      testContentNegotiation(useCase, ServiceDocumentProcessor.class);
    }
  }

  public void testContentNegotiation(String[] useCase, Class<ServiceDocumentProcessor> processorClass) {

    LOG.debug(Arrays.asList(useCase).toString());

    ODataRequest request = new ODataRequest();
    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("/" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

    ContentNegotiator cn = new ContentNegotiator();

    ProcessorStub p = new ProcessorStub(createCustomContentTypeMapping(useCase[3], useCase[4]));

    List<FormatContentTypeMapping> supportedContentTypes =
        cn.getSupportedContentTypes(p, processorClass);

    FormatOption fo = null;
    if (useCase[1] != null) {
      fo = mock(FormatOption.class);
      when(fo.getText()).thenReturn(useCase[1]);
    }

    String requestedContentType = cn.doContentNegotiation(fo, request, supportedContentTypes);

    assertNotNull(requestedContentType);
    assertEquals(useCase[0], requestedContentType);
  }

  private List<FormatContentTypeMapping> createCustomContentTypeMapping(String formatString, String contentTypeString) {
    List<FormatContentTypeMapping> map = null;

    assertTrue(!(formatString == null ^ contentTypeString == null));

    if (formatString != null) {
      String[] formats = formatString.split(",");
      String[] contentTypes = contentTypeString.split(",");

      assertEquals(formats.length, contentTypes.length);

      map = new ArrayList<FormatContentTypeMapping>();
      for (int i = 0; i < formats.length; i++) {
        map.add(new FormatContentTypeMapping(formats[i], contentTypes[i]));
      }
    }

    return map;
  }

  @Test
  @Ignore
  public void testMetadataDefault() {

    for (String[] useCase : casesMetadata) {
      ODataRequest request = new ODataRequest();
      request.setMethod(HttpMethod.GET);
      request.setRawODataPath("/$metadata" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

//      ODataResponse response = callHandler(useCase, request);
//
//      assertEquals(useCase[0], response.getHeaders().get(HttpHeader.CONTENT_TYPE));
    }
  }

//  @Test
//  public void testEntitySet() {
//
//    for (String[] useCase : casesEntitySet) {
//      ODataRequest request = new ODataRequest();
//      request.setMethod(HttpMethod.GET);
//      request.setRawODataPath("/ESAllPrim" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));
//
//      ODataResponse response = callHandler(useCase, request, new CollectionProcessorStub());
//
//      assertEquals(useCase[0], response.getHeaders().get(HttpHeader.CONTENT_TYPE));
//    }
//  }

//  private ODataResponse callHandler(String[] useCase, ODataRequest request,
//      Processor defaultProcessor) {
//    ODataHandler handler = createHandler();
//
//    if (useCase[2] != null) {
//      request.addHeader(HttpHeader.ACCEPT, Arrays.asList(useCase[2]));
//    }
//
//    if (useCase[3] != null) {
//        String[] aliase = useCase[3].split(",");
//        String[] mappings = useCase[4].split(",");
//
//        FormatContentTypeMapping[] formatCTMap = new FormatContentTypeMapping[aliase.length];
//        
//        for(int i=0; i< formatCTMap.length; i++) {
//          formatCTMap[i] = new FormatContentTypeMapping(aliase[i], mappings[i]);
//        }
//    
//
//      ProcessorStub stub = new ProcessorStub(formatCTMap);
//      handler.register(stub);
//    } else {
//      if (defaultProcessor != null) {
//        handler.register(defaultProcessor);
//      }
//    }
//
//    ODataResponse response = handler.process(request);
//    return response;
//  }

//  ODataResponse callHandler(String[] useCase, ODataRequest request) {
//    return callHandler(useCase, request, null);
//  }

  private class CollectionProcessorStub implements CollectionProcessor {

    @Override
    public void init(OData odata, Edm edm) {}

    @Override
    public void readCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, String format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format);
    }
  }

  @Test
  public void testDefaultSupportedContentTypesServiceDocument() {
    ContentNegotiator cn = new ContentNegotiator();

    ProcessorStub p = new ProcessorStub(null);

    List<FormatContentTypeMapping> supportedContentTypes =
        cn.getSupportedContentTypes(p, ServiceDocumentProcessor.class);

    assertNotNull(supportedContentTypes);
    assertEquals(1, supportedContentTypes.size());
    assertEquals("json", supportedContentTypes.get(0).getFormatAlias());
    assertEquals("application/json", supportedContentTypes.get(0).getContentType());
  }

  @Test
  public void testDefaultSupportedContentTypesMetadata() {
    ContentNegotiator cn = new ContentNegotiator();

    ProcessorStub p = new ProcessorStub(null);

    List<FormatContentTypeMapping> supportedContentTypes = cn.getSupportedContentTypes(p, MetadataProcessor.class);

    assertNotNull(supportedContentTypes);
    assertEquals(1, supportedContentTypes.size());
    assertEquals("xml", supportedContentTypes.get(0).getFormatAlias());
    assertEquals("application/xml", supportedContentTypes.get(0).getContentType());
  }

  @Test
  public void testCustomSupportedContentTypesServiceDocument() {
    ContentNegotiator cn = new ContentNegotiator();

    ProcessorStub p = new ProcessorStub(Arrays.asList(new FormatContentTypeMapping("a", "a/a")));

    List<FormatContentTypeMapping> supportedContentTypes =
        cn.getSupportedContentTypes(p, ServiceDocumentProcessor.class);

    assertNotNull(supportedContentTypes);
    assertEquals(2, supportedContentTypes.size());
    assertEquals("json", supportedContentTypes.get(0).getFormatAlias());
    assertEquals("application/json", supportedContentTypes.get(0).getContentType());
    assertEquals("a", supportedContentTypes.get(1).getFormatAlias());
    assertEquals("a/a", supportedContentTypes.get(1).getContentType());
  }

  private class ProcessorStub implements ServiceDocumentProcessor, MetadataProcessor,
      CollectionProcessor,
      CustomContentTypeSupport {

    List<FormatContentTypeMapping> customMapping;

    ProcessorStub(List<FormatContentTypeMapping> mapping) {
      this.customMapping = mapping;
    }

    @Override
    public void init(OData odata, Edm edm) {}

    @Override
    public List<FormatContentTypeMapping> modifySupportedContentTypes(
        List<FormatContentTypeMapping> supportedContentTypes,
        Class<? extends Processor> processorClass) {
      if (customMapping != null) {
        supportedContentTypes.addAll(customMapping);
      }
      return supportedContentTypes;
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
}
