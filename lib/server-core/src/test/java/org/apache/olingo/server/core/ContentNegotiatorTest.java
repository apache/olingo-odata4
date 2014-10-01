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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.CustomContentTypeSupportProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.junit.Test;

public class ContentNegotiatorTest {

  static final private String ACCEPT_CASE_MIN = "application/json;odata.metadata=minimal";
  static final private String ACCEPT_CASE_MIN_UTF8 = "application/json;charset=UTF-8;odata.metadata=minimal";
  static final private String ACCEPT_CASE_FULL = "application/json;odata.metadata=full";
  static final private String ACCEPT_CASE_NONE = "application/json;odata.metadata=none";
  static final private String ACCEPT_CASE_JSONQ = "application/json;q=0.2";
  static final private String ACCEPT_CASE_XML = "application/xml";
  static final private String ACCEPT_CASE_WILDCARD1 = "*/*";
  static final private String ACCEPT_CASE_WILDCARD2 = "application/*";

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)

  String[][] casesServiceDocument = {
      /* expected               $format           accept                 additional content types */
      { ACCEPT_CASE_MIN,        null,             null,                  null             },
      { ACCEPT_CASE_MIN,        "json",           null,                  null             },
      { ACCEPT_CASE_MIN,        "json",           ACCEPT_CASE_JSONQ,     null             },
      { ACCEPT_CASE_NONE,       ACCEPT_CASE_NONE, null,                  null             },
      { "a/a",                  "a/a",            null,                  "a/a"            },
      { ACCEPT_CASE_MIN,        null,             ACCEPT_CASE_JSONQ,     null             },
      { ACCEPT_CASE_MIN,        null,             ACCEPT_CASE_WILDCARD1, null             },
      { ACCEPT_CASE_MIN,        null,             ACCEPT_CASE_WILDCARD2, null             },
      { "a/a",                  "a/a",            null,                  "a/a,b/b"        },
      { "a/a",                  " a/a ",          null,                  " a/a , b/b "    },
      { "a/a;x=y",              "a/a",            ACCEPT_CASE_WILDCARD1, "a/a;x=y"        },
      { "a/a;v=w;x=y",          null,             "a/a;x=y",             "a/a;b=c,a/a;v=w;x=y" },
      { "a/a;v=w;x=y",          "a/a;x=y",        null,                  "a/a;b=c,a/a;v=w;x=y" },
      { ACCEPT_CASE_MIN,        "json",           ACCEPT_CASE_MIN,       null             },
      { ACCEPT_CASE_FULL,       null,             ACCEPT_CASE_FULL,      ACCEPT_CASE_FULL }, 
      { ACCEPT_CASE_MIN_UTF8,   null,             ACCEPT_CASE_MIN_UTF8,  null             }
  };                                                                                          

  String[][] casesMetadata = {                                                                 
      /* expected               $format           accept                 additional content types */
      { ACCEPT_CASE_XML,        null,             null,                  null             },
      { ACCEPT_CASE_XML,        "xml",            null,                  null             },
      { ACCEPT_CASE_XML,        "xml",            ACCEPT_CASE_XML,       null             },
      { "a/a",                  "a/a",            null,                  "a/a"            },
      { ACCEPT_CASE_XML,        null,             ACCEPT_CASE_XML,       null             },
      { ACCEPT_CASE_XML,        null,             ACCEPT_CASE_WILDCARD1, null             },
      { ACCEPT_CASE_XML,        null,             ACCEPT_CASE_WILDCARD2, null             },
      { "a/a",                  "a/a",            null,                  "a/a,b/b"        },
      { "a/a",                  " a/a ",          null,                  " a/a , b/b "    },
      { "a/a;x=y",              "a/a",            ACCEPT_CASE_WILDCARD1, "a/a;x=y"        }
  };

  String[][] casesFail = {                                                                 
      /* expected               $format           accept                 additional content types */
      { null,                   "xxx/yyy",        null,                  null             },
      { null,                   "a/a",            null,                  "b/b"            },
      { null,                   "a/a;x=y",        null,                  "a/a;v=w"        },
      { null,                   null,             "a/a;x=y",             "a/a;v=w"        },
      { null,                   "atom",           null,                  null             }, // not yet supported
      { null,                   null,             ACCEPT_CASE_FULL,      null             }, // not yet supported
      { null,                   "a/b;charset=ISO-8859-1", null,          "a/b"            },
      { null,                   null,             "a/b;charset=ISO-8859-1", "a/b"         }
  };
  //CHECKSTYLE:ON
  //@formatter:on

  @Test
  public void testServiceDocumentSingleCase() throws Exception {
    testContentNegotiation(
        new String[] { ACCEPT_CASE_MIN_UTF8, null, ACCEPT_CASE_MIN_UTF8, null },
        ServiceDocumentProcessor.class);
  }

  @Test
  public void testServiceDocument() throws Exception {
    for (String[] useCase : casesServiceDocument) {
      testContentNegotiation(useCase, ServiceDocumentProcessor.class);
    }
  }

  @Test
  public void testMetadataSingleCase() throws Exception {
    testContentNegotiation(new String[] { ACCEPT_CASE_XML, null, null, null }, MetadataProcessor.class);
  }

  @Test(expected = ContentNegotiatorException.class)
  public void testMetadataJsonFail() throws Exception {
    testContentNegotiation(new String[] { null, "json", null, null }, MetadataProcessor.class);
  }

  @Test
  public void testMetadata() throws Exception {
    for (String[] useCase : casesMetadata) {
      testContentNegotiation(useCase, MetadataProcessor.class);
    }
  }

  @Test
  public void testEntityCollectionFail() throws Exception {
    for (String[] useCase : casesFail) {
      try {
        testContentNegotiation(useCase, EntityCollectionProcessor.class);
        fail("Exception expected for '" + useCase[1] + '|' + useCase[2] + '|' + useCase[3] + "'!");
      } catch (final ContentNegotiatorException e) {}
    }
  }

  private void testContentNegotiation(final String[] useCase, final Class<? extends Processor> processorClass)
      throws ContentNegotiatorException {
    ODataRequest request = new ODataRequest();
    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("/" + (useCase[1] == null ? "" : "?$format=" + useCase[1]));

    ProcessorStub p = new ProcessorStub(createCustomContentTypes(useCase[3]));

    FormatOption fo = null;
    if (useCase[1] != null) {
      fo = mock(FormatOption.class);
      when(fo.getFormat()).thenReturn(useCase[1].trim());
    }

    if (useCase[2] != null) {
      request.addHeader(HttpHeader.ACCEPT, Arrays.asList(useCase[2]));
    }

    final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(fo, request, p, processorClass);

    assertNotNull(requestedContentType);
    if (useCase[0] != null) {
      assertEquals(ContentType.create(useCase[0]), requestedContentType);
    }
  }

  private List<ContentType> createCustomContentTypes(final String contentTypeString) {

    if (contentTypeString == null) {
      return null;
    }

    String[] contentTypes = contentTypeString.split(",");

    List<ContentType> types = new ArrayList<ContentType>();
    for (int i = 0; i < contentTypes.length; i++) {
      types.add(ContentType.create(contentTypes[i].trim()));
    }

    return types;
  }

  private class ProcessorStub implements ServiceDocumentProcessor, MetadataProcessor,
      EntityCollectionProcessor, CustomContentTypeSupportProcessor {

    List<ContentType> customTypes;

    ProcessorStub(final List<ContentType> types) {
      customTypes = types;
    }

    @Override
    public void init(final OData odata, final Edm edm) {}

    @Override
    public List<ContentType> modifySupportedContentTypes(final List<ContentType> supportedContentTypes,
        final Class<? extends Processor> processorClass) {
      if (customTypes == null) {
        return supportedContentTypes;
      } else {
        List<ContentType> modifiedTypes = new ArrayList<ContentType>(supportedContentTypes);
        modifiedTypes.addAll(customTypes);
        return modifiedTypes;
      }
    }

    @Override
    public void readServiceDocument(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
        final ContentType format) {
      response.setHeader(HttpHeader.CONTENT_TYPE, format.toContentTypeString());
    }

    @Override
    public void readCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
        final ContentType requestedContentType) {
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    }

    @Override
    public void readMetadata(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
        final ContentType requestedContentType) {
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    }

    @Override
    public void countCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo) {
      response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.toContentTypeString());
    }
  }
}
