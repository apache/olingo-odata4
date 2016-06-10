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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;
import org.apache.olingo.server.api.serializer.RepresentationType;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.junit.Test;

public class ContentNegotiatorTest {

  static final private String ACCEPT_CASE_MIN = ContentType.JSON.toContentTypeString();
  static final private String ACCEPT_CASE_MIN_UTF8 = "application/json;charset=UTF-8;odata.metadata=minimal";
  static final private String ACCEPT_CASE_FULL = ContentType.JSON_FULL_METADATA.toContentTypeString();
  static final private String ACCEPT_CASE_NONE = ContentType.JSON_NO_METADATA.toContentTypeString();
  static final private String ACCEPT_CASE_MIN_UTF8_IEEE754 =
      "application/json;charset=UTF-8;odata.metadata=minimal;IEEE754Compatible=true";
  static final private String ACCEPT_CASE_MIN_IEEE754 = ACCEPT_CASE_MIN + ";IEEE754Compatible=true";
  static final private String ACCEPT_CASE_JSONQ = "application/json;q=0.2";
  static final private String ACCEPT_CASE_XML = ContentType.APPLICATION_XML.toContentTypeString();
  static final private String ACCEPT_CASE_WILDCARD1 = "*/*";
  static final private String ACCEPT_CASE_WILDCARD2 = "application/*";

  //@formatter:off (Eclipse formatter)
  //CHECKSTYLE:OFF (Maven checkstyle)

  String[][] casesServiceDocument = {
      /* expected                     $format           accept                          modified content types */
      { ACCEPT_CASE_MIN,              null,             null,                           null                  },
      { ACCEPT_CASE_MIN,              "json",           null,                           null                  },
      { ACCEPT_CASE_MIN,              "json",           ACCEPT_CASE_JSONQ,              null                  },
      { ACCEPT_CASE_NONE,             ACCEPT_CASE_NONE, null,                           null                  },
      { "a/a",                        "a/a",            null,                           "a/a"                 },
      { ACCEPT_CASE_MIN,              null,             ACCEPT_CASE_JSONQ,              null                  },
      { ACCEPT_CASE_MIN,              null,             ACCEPT_CASE_WILDCARD1,          null                  },
      { ACCEPT_CASE_MIN,              null,             ACCEPT_CASE_WILDCARD2,          null                  },
      { ACCEPT_CASE_MIN,              null,             null,                           ACCEPT_CASE_MIN       },
      { "a/a",                        "a/a",            null,                           "a/a,b/b"             },
      { "a/a;x=y",                    "a/a",            ACCEPT_CASE_WILDCARD1,          "a/a;x=y"             },
      { "a/a;v=w;x=y",                null,             "a/a;x=y",                      "a/a;b=c,a/a;v=w;x=y" },
      { "a/a;v=w;x=y",                "a/a;x=y",        null,                           "a/a;b=c,a/a;v=w;x=y" },
      { ACCEPT_CASE_MIN,              "json",           ACCEPT_CASE_MIN,                null                  },
      { ACCEPT_CASE_FULL,             null,             ACCEPT_CASE_FULL,               ACCEPT_CASE_FULL      },
      { ACCEPT_CASE_MIN_UTF8,         null,             ACCEPT_CASE_MIN_UTF8,           null                  },
      { ACCEPT_CASE_MIN_IEEE754,      null,             ACCEPT_CASE_MIN_IEEE754,        null                  },
      { ACCEPT_CASE_MIN_UTF8_IEEE754, null,             ACCEPT_CASE_MIN_UTF8_IEEE754,   null                  },
      { ACCEPT_CASE_MIN_IEEE754,      ACCEPT_CASE_MIN_IEEE754, ACCEPT_CASE_MIN ,        null                  },
      { ACCEPT_CASE_XML,              "xml",            null,                           null                  },
      { ACCEPT_CASE_XML,              null,             ACCEPT_CASE_XML,                null                  }
  };

  String[][] casesMetadata = {
      /* expected               $format           accept                 modified content types */
      { ACCEPT_CASE_XML,        null,             null,                  null             },
      { ACCEPT_CASE_XML,        "xml",            null,                  null             },
      { ACCEPT_CASE_XML,        "xml",            ACCEPT_CASE_XML,       null             },
      { "a/a",                  "a/a",            null,                  "a/a"            },
      { ACCEPT_CASE_XML,        null,             ACCEPT_CASE_XML,       null             },
      { ACCEPT_CASE_XML,        null,             ACCEPT_CASE_WILDCARD1, null             },
      { ACCEPT_CASE_XML,        null,             ACCEPT_CASE_WILDCARD2, null             },
      { "a/a",                  "a/a",            null,                  "a/a,b/b"        },
      { "a/a;x=y",              "a/a",            ACCEPT_CASE_WILDCARD1, "a/a;x=y"        }
  };

  String[][] casesFail = {
      /* expected               $format           accept                 modified content types */
      { null,                   "xxx/yyy",        null,                  null             },
      { null,                   "a/a",            null,                  "b/b"            },
      { null,                   "a/a;x=y",        null,                  "a/a;v=w"        },
      { null,                   null,             "a/a;x=y",             "a/a;v=w"        },
      { null,                   null,             "*",                   null             },
      { null,                   "a/b;charset=ISO-8859-1", null,          "a/b"            },
      { null,                   null,             "a/b;charset=ISO-8859-1", "a/b"         },
      { null,                   null,             null,                  "text/plain"     }
  };
  //CHECKSTYLE:ON
  //@formatter:on

  @Test
  public void serviceDocumentSingleCase() throws Exception {
    testContentNegotiation(
        new String[] { ACCEPT_CASE_MIN_UTF8, null, ACCEPT_CASE_MIN_UTF8, null },
        RepresentationType.SERVICE);
  }

  @Test
  public void serviceDocument() throws Exception {
    for (String[] useCase : casesServiceDocument) {
      testContentNegotiation(useCase, RepresentationType.SERVICE);
    }
  }

  @Test
  public void metadataSingleCase() throws Exception {
    testContentNegotiation(new String[] { ACCEPT_CASE_XML, null, null, null }, RepresentationType.METADATA);
  }

  @Test(expected = ContentNegotiatorException.class)
  public void metadataJsonFail() throws Exception {
    testContentNegotiation(new String[] { null, "json", null, null }, RepresentationType.METADATA);
  }

  @Test
  public void metadata() throws Exception {
    for (String[] useCase : casesMetadata) {
      testContentNegotiation(useCase, RepresentationType.METADATA);
    }
  }

  @Test
  public void entityCollectionFail() throws Exception {
    for (String[] useCase : casesFail) {
      try {
        testContentNegotiation(useCase, RepresentationType.COLLECTION_ENTITY);
        fail("Exception expected for '" + useCase[1] + '|' + useCase[2] + '|' + useCase[3] + "'!");
      } catch (final ContentNegotiatorException e) {
        // Expected Exception
      }
    }
  }

  @Test
  public void checkSupport() throws Exception {
    ContentNegotiator.checkSupport(ContentType.JSON, null, RepresentationType.ENTITY);
    ContentNegotiator.checkSupport(ContentType.TEXT_PLAIN, null, RepresentationType.VALUE);
    try {
      ContentNegotiator.checkSupport(ContentType.APPLICATION_SVG_XML, null, RepresentationType.ENTITY);
      fail("Exception expected.");
    } catch (final ContentNegotiatorException e) {
      assertEquals(ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPE, e.getMessageKey());
    }

    ContentNegotiator.checkSupport(ContentType.create("a/b"), createCustomContentTypeSupport("a/b"),
        RepresentationType.ENTITY);
    ContentNegotiator.checkSupport(ContentType.create(ContentType.create("a/b"), "c", "d"),
        createCustomContentTypeSupport("a/b"),
        RepresentationType.ENTITY);
    try {
      ContentNegotiator.checkSupport(ContentType.create("a/b"), createCustomContentTypeSupport("a/b;c=d"),
          RepresentationType.ENTITY);
      fail("Exception expected.");
    } catch (final ContentNegotiatorException e) {
      assertEquals(ContentNegotiatorException.MessageKeys.UNSUPPORTED_CONTENT_TYPE, e.getMessageKey());
    }
  }

  private void testContentNegotiation(final String[] useCase, final RepresentationType representationType)
      throws ContentNegotiatorException {

    FormatOption formatOption = null;
    if (useCase[1] != null) {
      formatOption = mock(FormatOption.class);
      when(formatOption.getFormat()).thenReturn(useCase[1]);
    }

    ODataRequest request = new ODataRequest();
    if (useCase[2] != null) {
      request.addHeader(HttpHeader.ACCEPT, Arrays.asList(useCase[2]));
    }

    final CustomContentTypeSupport customContentTypeSupport = useCase[3] == null ? null :
      createCustomContentTypeSupport(useCase[3]);

    final ContentType requestedContentType = ContentNegotiator.doContentNegotiation(
        formatOption, request, customContentTypeSupport, representationType);

    assertNotNull(requestedContentType);
    if (useCase[0] != null) {
      assertEquals(ContentType.create(useCase[0]), requestedContentType);
    }
  }

  private CustomContentTypeSupport createCustomContentTypeSupport(final String contentTypeString) {
    final String[] contentTypes = contentTypeString.split(",");

    List<ContentType> types = new ArrayList<ContentType>();
    for (String contentType : contentTypes) {
      types.add(ContentType.create(contentType));
    }

    CustomContentTypeSupport customContentTypeSupport = mock(CustomContentTypeSupport.class);
    when(customContentTypeSupport.modifySupportedContentTypes(
        anyListOf(ContentType.class), any(RepresentationType.class)))
        .thenReturn(types);
    return customContentTypeSupport;
  }
}
