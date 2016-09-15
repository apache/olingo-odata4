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
package org.apache.olingo.server.core.deserializer.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException.MessageKeys;
import org.junit.Test;

public class HttpRequestStatusLineTest {

  private static final String HTTP_VERSION = "HTTP/1.1";
  private static final String SPACE = " ";
  private String baseUri = "http://localhost/odata";
  private String serviceResolutionUri = "";

  @Test
  public void absolute() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("http://localhost/odata/ESAllPrim?$top=2");
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("/ESAllPrim", line.getRawODataPath());
    assertEquals(baseUri + "/ESAllPrim?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void absoluteWithRelativePath() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("http://localhost/odata/../../ESAllPrim?$top=2");
    assertEquals("/../../ESAllPrim", line.getRawODataPath());
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals(baseUri + "/../../ESAllPrim?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void absolutePath() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("/odata/ESAllPrim");
    assertEquals("/ESAllPrim", line.getRawODataPath());
    assertEquals(baseUri + "/ESAllPrim", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void relativeWithDots() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("../../ESAllPrim?$top=2");
    assertEquals("/../../ESAllPrim", line.getRawODataPath());
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals(baseUri + "/../../ESAllPrim?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void relative() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("ESAllPrim?$top=2");
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("/ESAllPrim", line.getRawODataPath());
    assertEquals(baseUri + "/ESAllPrim?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void relativeMultipleSegments() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyComp");
    assertEquals("", line.getRawQueryPath());
    assertEquals("/ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyComp", line.getRawODataPath());
    assertEquals(baseUri + "/ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyComp", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void otherBaseUri() throws BatchDeserializerException {
    parseFail("http://otherhost/odata/ESAllPrim", MessageKeys.INVALID_BASE_URI);
  }

  @Test
  public void invalidRelative() throws BatchDeserializerException {
    parseFail("/ESAllPrim", MessageKeys.INVALID_URI);
  }

  private HttpRequestStatusLine parse(final String uri) throws BatchDeserializerException {
    Line statusline = new Line(HttpMethod.GET.name() + SPACE + uri + SPACE + HTTP_VERSION, 0);
    return new HttpRequestStatusLine(statusline, baseUri, serviceResolutionUri);
  }

  private void parseFail(final String uri, final MessageKeys messageKey) {
    try {
      parse(uri);
      fail("Expected exception");
    } catch (BatchDeserializerException e) {
      assertEquals(messageKey, e.getMessageKey());
    }
  }
}
