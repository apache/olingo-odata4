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
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException.MessageKeys;
import org.junit.Test;

public class HttpRequestStatusLineTest {

  private static final String HTTP_VERSION = "HTTP/1.1";
  private static final String SPACE = " ";
  private String baseUri = "http://localhost/odata";
  private String serviceResolutionUri = "";

  @Test
  public void testAbsolute() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("http://localhost/odata/Employee?$top=2");
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("/Employee", line.getRawODataPath());
    assertEquals("http://localhost/odata/Employee?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void testAbsoluteWithRelativePath() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("http://localhost/odata/../../Employee?$top=2");
    assertEquals("/../../Employee", line.getRawODataPath());
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("http://localhost/odata/../../Employee?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void testRelativeWithDots() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("../../Employee?$top=2");
    assertEquals("/../../Employee", line.getRawODataPath());
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("http://localhost/odata/../../Employee?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void testRelative() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("Employee?$top=2");
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("/Employee", line.getRawODataPath());
    assertEquals("http://localhost/odata/Employee?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void testRelativeMultipleSegements() throws BatchDeserializerException {
    final HttpRequestStatusLine line = parse("Employee/Manager/EmployeeName?$top=2");
    assertEquals("$top=2", line.getRawQueryPath());
    assertEquals("/Employee/Manager/EmployeeName", line.getRawODataPath());
    assertEquals("http://localhost/odata/Employee/Manager/EmployeeName?$top=2", line.getRawRequestUri());
    assertEquals(baseUri, line.getRawBaseUri());
    assertEquals(serviceResolutionUri, line.getRawServiceResolutionUri());
  }

  @Test
  public void testOtherBaseUri() throws BatchDeserializerException {
    parseFail("http://otherhost/odata/Employee?$top=2", MessageKeys.INVALID_BASE_URI);
  }

  @Test
  public void testInvalidRelative() throws BatchDeserializerException {
    parseFail("/Employee?$top=2", MessageKeys.INVALID_URI);
  }

  HttpRequestStatusLine parse(final String uri) throws BatchDeserializerException {
    Line statusline = new Line(HttpMethod.GET.toString().toUpperCase() + SPACE + uri + SPACE + HTTP_VERSION, 0);
    return new HttpRequestStatusLine(statusline, baseUri, serviceResolutionUri);
  }

  void parseFail(final String uri, final MessageKeys messageKey) {
    try {
      parse(uri);
      fail("Expceted exception");
    } catch (BatchDeserializerException e) {
      assertEquals(messageKey, e.getMessageKey());
    }
  }
}
