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

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;
import org.apache.olingo.server.core.deserializer.batch.Header;
import org.junit.Test;

public class HeaderTest {

  @Test
  public void test() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 1);

    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(1, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
  }

  @Test
  public void testNotAvailable() {
    Header header = new Header(1);

    assertNull(header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(0, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals("", header.getHeaderNotNull(HttpHeader.CONTENT_TYPE));
  }

  @Test
  public void testCaseInsensitive() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 1);

    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeader("cOnTenT-TyPE"));
    assertEquals(1, header.getHeaders("cOnTenT-TyPE").size());
    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeaders("cOnTenT-TyPE").get(0));
  }

  @Test
  public void testDuplicatedAdd() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 2);

    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(1, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
  }

  @Test
  public void testMatcher() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED + ";boundary=123", 1);

    assertTrue(header.isHeaderMatching(HttpHeader.CONTENT_TYPE, BatchParserCommon.PATTERN_MULTIPART_BOUNDARY));
  }

  @Test
  public void testFieldName() {
    Header header = new Header(0);
    header.addHeader("MyFieldNamE", "myValue", 1);

    assertEquals("MyFieldNamE", header.getHeaderField("myfieldname").getFieldName());
    assertEquals("MyFieldNamE", header.toSingleMap().keySet().toArray(new String[0])[0]);
    assertEquals("MyFieldNamE", header.toMultiMap().keySet().toArray(new String[0])[0]);

    assertEquals("myValue", header.toMultiMap().get("MyFieldNamE").get(0));
    assertEquals("myValue", header.toSingleMap().get("MyFieldNamE"));
  }

  @Test
  public void testDeepCopy() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED + ";boundary=123", 1);

    Header copy = header.clone();
    assertEquals(header.getHeaders(HttpHeader.CONTENT_TYPE), copy.getHeaders(HttpHeader.CONTENT_TYPE));
    assertEquals(header.getHeader(HttpHeader.CONTENT_TYPE), copy.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(header.getHeaderField(HttpHeader.CONTENT_TYPE), copy.getHeaderField(HttpHeader.CONTENT_TYPE));

    assertTrue(header.getHeaders(HttpHeader.CONTENT_TYPE) != copy.getHeaders(HttpHeader.CONTENT_TYPE));
    assertTrue(header.getHeaderField(HttpHeader.CONTENT_TYPE) != copy.getHeaderField(HttpHeader.CONTENT_TYPE));
  }

  @Test
  public void testMatcherNoHeader() {
    Header header = new Header(1);

    assertFalse(header.isHeaderMatching(HttpHeader.CONTENT_TYPE, BatchParserCommon.PATTERN_MULTIPART_BOUNDARY));
  }

//  @Test
//  public void testMatcherFail() {
//    Header header = new Header(1);
//    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED + ";boundary=123", 1);
//
//    assertFalse(header.isHeaderMatching(HttpHeader.CONTENT_TYPE, BatchParserCommon.PATTERN_HEADER_LINE));
//  }

  @Test
  public void testDuplicatedAddList() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 1);
    header.addHeader(HttpHeader.CONTENT_TYPE, Arrays.asList(new String[] { HttpContentType.MULTIPART_MIXED,
        HttpContentType.APPLICATION_ATOM_SVC }), 2);

    assertEquals(HttpContentType.MULTIPART_MIXED + ", " + HttpContentType.APPLICATION_ATOM_SVC, header
        .getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(2, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
    assertEquals(HttpContentType.APPLICATION_ATOM_SVC, header.getHeaders(HttpHeader.CONTENT_TYPE).get(1));
  }

  @Test
  public void testRemove() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 1);
    header.removeHeader(HttpHeader.CONTENT_TYPE);

    assertNull(header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(0, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
  }

  @Test
  public void testMultipleValues() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED, 1);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.APPLICATION_ATOM_SVC, 2);
    header.addHeader(HttpHeader.CONTENT_TYPE, HttpContentType.APPLICATION_ATOM_XML, 3);

    final String fullHeaderString =
        HttpContentType.MULTIPART_MIXED + ", " + HttpContentType.APPLICATION_ATOM_SVC + ", "
            + HttpContentType.APPLICATION_ATOM_XML;

    assertEquals(fullHeaderString, header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(3, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(HttpContentType.MULTIPART_MIXED, header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
    assertEquals(HttpContentType.APPLICATION_ATOM_SVC, header.getHeaders(HttpHeader.CONTENT_TYPE).get(1));
    assertEquals(HttpContentType.APPLICATION_ATOM_XML, header.getHeaders(HttpHeader.CONTENT_TYPE).get(2));
  }
  
  @Test
  public void testSplitValues() {
    final String values = "abc, def,123,77,   99, ysd";
    List<String> splittedValues = Header.splitValuesByComma(values);

    assertEquals(6, splittedValues.size());
    assertEquals("abc", splittedValues.get(0));
    assertEquals("def", splittedValues.get(1));
    assertEquals("123", splittedValues.get(2));
    assertEquals("77", splittedValues.get(3));
    assertEquals("99", splittedValues.get(4));
    assertEquals("ysd", splittedValues.get(5));
  }
}
