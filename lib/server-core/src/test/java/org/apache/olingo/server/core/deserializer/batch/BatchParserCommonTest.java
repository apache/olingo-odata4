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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.junit.Assert;
import org.junit.Test;

public class BatchParserCommonTest {

  private static final String CRLF = "\r\n";
  private static final String MULTIPART_MIXED = "multipart/mixed";

  @Test
  public void multipleHeaders() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Content-Id: 1" + CRLF,
        "Content-Id: 2" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> contentIdHeaders = header.getHeaders(HttpHeader.CONTENT_ID);
    assertNotNull(contentIdHeaders);
    assertEquals(2, contentIdHeaders.size());
    assertEquals("1", contentIdHeaders.get(0));
    assertEquals("2", contentIdHeaders.get(1));
  }

  @Test
  public void multipleHeadersSameValue() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Content-Id: 1" + CRLF,
        "Content-Id: 1" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> contentIdHeaders = header.getHeaders(HttpHeader.CONTENT_ID);
    assertNotNull(contentIdHeaders);
    assertEquals(1, contentIdHeaders.size());
    assertEquals("1", contentIdHeaders.get(0));
  }

  @Test
  public void headersSeparatedByComma() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Content-Id: 1" + CRLF,
        "Upgrade: HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> upgradeHeader = header.getHeaders("upgrade");
    assertNotNull(upgradeHeader);
    assertEquals(4, upgradeHeader.size());
    assertEquals("HTTP/2.0", upgradeHeader.get(0));
    assertEquals("SHTTP/1.3", upgradeHeader.get(1));
    assertEquals("IRC/6.9", upgradeHeader.get(2));
    assertEquals("RTA/x11", upgradeHeader.get(3));
  }

  @Test
  public void multipleAcceptHeaders() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF,
        "Accept: text/plain;q=0.3" + CRLF,
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> acceptHeader = header.getHeaders(HttpHeader.ACCEPT);
    assertNotNull(acceptHeader);
    assertEquals(4, acceptHeader.size());
  }

  @Test
  public void multipleAcceptHeadersSameValue() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF,
        "Accept: application/atomsvc+xml;q=0.8" + CRLF,
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> acceptHeader = header.getHeaders(HttpHeader.ACCEPT);
    assertNotNull(acceptHeader);
    assertEquals(3, acceptHeader.size());
  }

  @Test
  public void multipleAcceptLanguageHeaders() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "Accept-Language: de-DE;q=0.3" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> acceptLanguageHeader = header.getHeaders(HttpHeader.ACCEPT_LANGUAGE);
    assertNotNull(acceptLanguageHeader);
    assertEquals(4, acceptLanguageHeader.size());
  }

  @Test
  public void multipleAcceptLanguageHeadersSameValue() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "Accept-Language:en-US,en;q=0.7" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF));
    assertNotNull(header);

    final List<String> acceptLanguageHeader = header.getHeaders(HttpHeader.ACCEPT_LANGUAGE);
    assertNotNull(acceptLanguageHeader);
    assertEquals(3, acceptLanguageHeader.size());
  }

  @Test
  public void headersWithSpecialNames() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList(
        "Test0123456789: 42" + CRLF,
        "a_b: c/d" + CRLF,
        "!#$%&'*+-.^_`|~: weird" + CRLF));
    assertNotNull(header);
    assertTrue(header.exists("Test0123456789"));
    assertTrue(header.exists("a_b"));
    assertTrue(header.exists("!#$%&'*+-.^_`|~"));
    assertEquals("weird", header.getHeader("!#$%&'*+-.^_`|~"));
  }

  @Test
  public void headerWithWrongName() throws Exception {
    final Header header = BatchParserCommon.consumeHeaders(toLineList("a,b: c/d" + CRLF));
    assertNotNull(header);
    assertFalse(header.iterator().hasNext());
  }

  @Test
  public void boundaryParameter() throws Exception {
    final String boundary = "boundary";
    final String contentType = MULTIPART_MIXED + "; boundary=" + boundary + "  ";
    Assert.assertEquals(boundary, BatchParserCommon.getBoundary(contentType, 0));
  }

  @Test
  public void boundaryParameterWithQuotes() throws Exception {
    final String boundary = "batch_1.2+34:2j)0?";
    final String contentType = MULTIPART_MIXED + "; boundary=\"" + boundary + "\"";
    Assert.assertEquals(boundary, BatchParserCommon.getBoundary(contentType, 0));
  }

  @Test
  public void boundaryParameterWithSpaces() throws Exception {
    final String boundary = "        boundary";
    final String contentType = MULTIPART_MIXED + "; boundary=\"" + boundary + "\"  ";
    Assert.assertEquals(boundary, BatchParserCommon.getBoundary(contentType, 0));
  }

  @Test
  public void invalidContentType() throws Exception {
    invalidBoundary("multipart;boundary=BOUNDARY", BatchDeserializerException.MessageKeys.INVALID_CONTENT_TYPE);
  }

  @Test
  public void contentTypeCharset() throws Exception {
    final String contentType = MULTIPART_MIXED + "; charset=UTF-8;boundary=" + BatchParserCommon.BOUNDARY;
    final String boundary = BatchParserCommon.getBoundary(contentType, 0);
    Assert.assertEquals(BatchParserCommon.BOUNDARY, boundary);
  }

  @Test
  public void withoutBoundaryParameter() throws Exception {
    invalidBoundary(MULTIPART_MIXED, BatchDeserializerException.MessageKeys.MISSING_BOUNDARY_DELIMITER);
  }

  @Test
  public void boundaryParameterWithoutQuote() throws Exception {
    invalidBoundary(MULTIPART_MIXED + ";boundary=batch_1740-bb:84-2f7f",
        BatchDeserializerException.MessageKeys.INVALID_BOUNDARY);
  }

  @Test
  public void boundaryEmpty() throws Exception {
    invalidBoundary(MULTIPART_MIXED + ";boundary=\"\"", BatchDeserializerException.MessageKeys.INVALID_BOUNDARY);
  }

  @Test
  public void boundarySpace() throws Exception {
    invalidBoundary(MULTIPART_MIXED + ";boundary=\" \"", BatchDeserializerException.MessageKeys.INVALID_BOUNDARY);
  }

  @Test
  public void removeEndingCRLF() {
    String line = "Test" + CRLF;
    assertEquals("Test", BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeLastEndingCRLF() {
    String line = "Test" + CRLF + CRLF;
    assertEquals("Test" + CRLF, BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeEndingCRLFWithWS() {
    String line = "Test" + CRLF + "            ";
    assertEquals("Test", BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeEndingCRLFNothingToRemove() {
    String line = "Hallo" + CRLF + "Bla";
    assertEquals(line, BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeEndingCRLFAll() {
    String line = CRLF;
    assertEquals("", BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeEndingCRLFSpace() {
    String line = CRLF + "                      ";
    assertEquals("", BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeLastEndingCRLFWithWS() {
    String line = "Test            " + CRLF;
    assertEquals("Test            ", BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  @Test
  public void removeLastEndingCRLFWithWSLong() {
    String line = "Test            " + CRLF + "Test2    " + CRLF;
    assertEquals("Test            " + CRLF + "Test2    ",
        BatchParserCommon.removeEndingCRLF(new Line(line, 1)).toString());
  }

  private List<Line> toLineList(final String... messageRaw) {
    final List<Line> lineList = new ArrayList<Line>();
    int counter = 1;

    for (final String currentLine : messageRaw) {
      lineList.add(new Line(currentLine, counter++));
    }

    return lineList;
  }

  private void invalidBoundary(final String contentType, final BatchDeserializerException.MessageKeys messageKey) {
    try {
      BatchParserCommon.getBoundary(contentType, 0);
      Assert.fail("Expected exception not thrown.");
    } catch (final BatchDeserializerException e) {
      Assert.assertEquals(messageKey, e.getMessageKey());
    }
  }
}
