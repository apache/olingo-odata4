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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.junit.Test;

public class HeaderTest {

  @Test
  public void test() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 1);

    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(), header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(1, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
  }

  @Test
  public void notAvailable() {
    Header header = new Header(1);

    assertNull(header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(0, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
  }

  @Test
  public void caseInsensitive() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 1);

    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(), header.getHeader("cOnTenT-TyPE"));
    assertEquals(1, header.getHeaders("cOnTenT-TyPE").size());
    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(), header.getHeaders("cOnTenT-TyPE").get(0));
  }

  @Test
  public void duplicatedAdd() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 2);

    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(), header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(1, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
  }

  @Test
  public void fieldName() {
    Header header = new Header(0);
    header.addHeader("MyFieldNamE", "myValue", 1);

    assertEquals("MyFieldNamE", header.getHeaderField("myfieldname").getFieldName());
    assertEquals("MyFieldNamE", header.toSingleMap().keySet().toArray(new String[0])[0]);
    assertEquals("MyFieldNamE", header.toMultiMap().keySet().toArray(new String[0])[0]);

    assertEquals("myValue", header.toMultiMap().get("MyFieldNamE").get(0));
    assertEquals("myValue", header.toSingleMap().get("MyFieldNamE"));
  }

  @Test
  public void deepCopy() throws Exception {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary=123", 1);

    Header copy = header.clone();
    assertEquals(header.getHeaders(HttpHeader.CONTENT_TYPE), copy.getHeaders(HttpHeader.CONTENT_TYPE));
    assertEquals(header.getHeader(HttpHeader.CONTENT_TYPE), copy.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(header.getHeaderField(HttpHeader.CONTENT_TYPE), copy.getHeaderField(HttpHeader.CONTENT_TYPE));

    assertTrue(header.getHeaders(HttpHeader.CONTENT_TYPE) != copy.getHeaders(HttpHeader.CONTENT_TYPE));
    assertTrue(header.getHeaderField(HttpHeader.CONTENT_TYPE) != copy.getHeaderField(HttpHeader.CONTENT_TYPE));
  }

  @Test
  public void deepCopyHeaderField() throws Exception {
    List<String> values = new ArrayList<String>();
    values.add("abc");
    values.add("def");
    HeaderField field = new HeaderField("name", values, 17);

    HeaderField clone = field.clone();
    assertEquals(field.getFieldName(), clone.getFieldName());
    assertEquals(field.getLineNumber(), clone.getLineNumber());
    assertEquals(field.getValues(), clone.getValues());

    assertTrue(field.getValues() != clone.getValues());
  }

  @Test
  public void duplicatedAddList() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 1);
    header.addHeader(HttpHeader.CONTENT_TYPE, Arrays.asList(new String[] {
        ContentType.MULTIPART_MIXED.toContentTypeString(),
        ContentType.APPLICATION_ATOM_SVC.toContentTypeString() }), 2);

    assertEquals(ContentType.MULTIPART_MIXED + ", " + ContentType.APPLICATION_ATOM_SVC, header
        .getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(2, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
    assertEquals(ContentType.APPLICATION_ATOM_SVC.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(1));
  }

  @Test
  public void remove() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 1);
    header.removeHeader(HttpHeader.CONTENT_TYPE);

    assertNull(header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(0, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
  }

  @Test
  public void multipleValues() {
    Header header = new Header(1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(), 1);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_ATOM_SVC.toContentTypeString(), 2);
    header.addHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_ATOM_XML.toContentTypeString(), 3);

    final String fullHeaderString =
        ContentType.MULTIPART_MIXED + ", " + ContentType.APPLICATION_ATOM_SVC + ", "
            + ContentType.APPLICATION_ATOM_XML;

    assertEquals(fullHeaderString, header.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(3, header.getHeaders(HttpHeader.CONTENT_TYPE).size());
    assertEquals(ContentType.MULTIPART_MIXED.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(0));
    assertEquals(ContentType.APPLICATION_ATOM_SVC.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(1));
    assertEquals(ContentType.APPLICATION_ATOM_XML.toContentTypeString(),
        header.getHeaders(HttpHeader.CONTENT_TYPE).get(2));
  }

  @Test
  public void splitValues() {
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
  
  @Test
  public void testHashCode() {
    HeaderField header = new HeaderField("filed", 0);
    assertNotNull(header.hashCode());
  }
}
