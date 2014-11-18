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
package org.apache.olingo.server.core.deserializer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;
import org.apache.olingo.server.core.deserializer.batch.Header;
import org.apache.olingo.server.core.deserializer.batch.BufferedReaderIncludingLineEndings.Line;
import org.junit.Test;

public class BatchParserCommonTest {

  private static final String CRLF = "\r\n";

  @Test
  public void testMultipleHeader() throws BatchException {
    String[] messageRaw = new String[] {
        "Content-Id: 1" + CRLF,
        "Content-Id: 2" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
    assertNotNull(header);
    
    final List<String> contentIdHeaders = header.getHeaders(BatchParserCommon.HTTP_CONTENT_ID);
    assertNotNull(contentIdHeaders);
    assertEquals(2, contentIdHeaders.size());
    assertEquals("1", contentIdHeaders.get(0));
    assertEquals("2", contentIdHeaders.get(1));
  }
  
  @Test
  public void testMultipleHeaderSameValue() throws BatchException {
    String[] messageRaw = new String[] {
        "Content-Id: 1" + CRLF,
        "Content-Id: 1" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
    assertNotNull(header);
    
    final List<String> contentIdHeaders = header.getHeaders(BatchParserCommon.HTTP_CONTENT_ID);
    assertNotNull(contentIdHeaders);
    assertEquals(1, contentIdHeaders.size());
    assertEquals("1", contentIdHeaders.get(0));
  }
  
  @Test
  public void testHeaderSperatedByComma() throws BatchException {
    String[] messageRaw = new String[] {
        "Content-Id: 1" + CRLF,
        "Upgrade: HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
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
  public void testMultipleAcceptHeader() throws BatchException {
    String[] messageRaw = new String[] {
        "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF,
        "Accept: text/plain;q=0.3" + CRLF,
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
    assertNotNull(header);
    
    final List<String> acceptHeader = header.getHeaders(HttpHeader.ACCEPT);
    assertNotNull(acceptHeader);
    assertEquals(4, acceptHeader.size());
  }
  
  @Test
  public void testMultipleAcceptHeaderSameValue() throws BatchException {
    String[] messageRaw = new String[] {
        "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF,
        "Accept: application/atomsvc+xml;q=0.8" + CRLF,
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
    assertNotNull(header);
    
    final List<String> acceptHeader = header.getHeaders(HttpHeader.ACCEPT);
    assertNotNull(acceptHeader);
    assertEquals(3, acceptHeader.size());
  }
  
  @Test
  public void testMultipleAccepLanguagetHeader() throws BatchException {
    String[] messageRaw = new String[] {
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "Accept-Language: de-DE;q=0.3" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
    assertNotNull(header);
    
    final List<String> acceptLanguageHeader = header.getHeaders(HttpHeader.ACCEPT_LANGUAGE);
    assertNotNull(acceptLanguageHeader);
    assertEquals(4, acceptLanguageHeader.size());
  }
  
  @Test
  public void testMultipleAccepLanguagetHeaderSameValue() throws BatchException {
    String[] messageRaw = new String[] {
        "Accept-Language:en-US,en;q=0.7,en-UK;q=0.9" + CRLF,
        "Accept-Language:en-US,en;q=0.7" + CRLF,
        "content-type: Application/http" + CRLF,
        "content-transfer-encoding: Binary" + CRLF
      };
    List<Line> message = toLineList(messageRaw);
    
    final Header header = BatchParserCommon.consumeHeaders(message);
    assertNotNull(header);
    
    final List<String> acceptLanguageHeader = header.getHeaders(HttpHeader.ACCEPT_LANGUAGE);
    assertNotNull(acceptLanguageHeader);
    assertEquals(3, acceptLanguageHeader.size());
  }
  
  @Test
  public void testRemoveEndingCRLF() {
    String line = "Test\r\n";
    assertEquals("Test", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveLastEndingCRLF() {
    String line = "Test\r\n\r\n";
    assertEquals("Test\r\n", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveEndingCRLFWithWS() {
    String line = "Test\r\n            ";
    assertEquals("Test", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveEndingCRLFNothingToRemove() {
    String line = "Hallo\r\nBla";
    assertEquals("Hallo\r\nBla", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveEndingCRLFAll() {
    String line = "\r\n";
    assertEquals("", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveEndingCRLFSpace() {
    String line = "\r\n                      ";
    assertEquals("", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveLastEndingCRLFWithWS() {
    String line = "Test            \r\n";
    assertEquals("Test            ", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }

  @Test
  public void testRemoveLastEndingCRLFWithWSLong() {
    String line = "Test            \r\nTest2    \r\n";
    assertEquals("Test            \r\nTest2    ", BatchParserCommon.removeEndingCRLF(new Line(line,1)).toString());
  }
  
  private List<Line> toLineList(String[] messageRaw) {
    final List<Line> lineList = new ArrayList<Line>();
    int counter = 1;
    
    for(final String currentLine : messageRaw) {
      lineList.add(new Line(currentLine, counter++));
    }
    
    return lineList;
  }
}
