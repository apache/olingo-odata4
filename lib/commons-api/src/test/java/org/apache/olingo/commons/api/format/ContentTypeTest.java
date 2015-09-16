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
package org.apache.olingo.commons.api.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ContentTypeTest {

  @Test
  public void create() {
    assertEquals("a/b", ContentType.create("a/b").toContentTypeString());
    assertEquals(ContentType.create("a/b;c=d;x=y"), ContentType.create("a/b;x=y;c=d"));
    assertEquals(ContentType.create("a/b;c=d;x=y"), ContentType.create("a/b; x=y; c=d"));
    assertEquals(ContentType.create("A/B"), ContentType.create("a/b"));
  }

  @Test
  public void createFail() {
    createWrong("a");
    createWrong(" a / b ");
    createWrong("a/b;");
    createWrong("a/b;parameter");
    createWrong("a/b;parameter=");
    createWrong("a/b;=value");
    createWrong("a/b;the name=value");
    createWrong("a/b;name= value");

    createWrong("*/*");
    createWrong("*");
    createWrong("a//b");
    createWrong("///");
    createWrong("a/*");
    createWrong("*/b");

    createWrong(null);
  }

  @Test
  public void createWithParameter() {
    assertEquals(ContentType.create("a/b;c=d"), ContentType.create(ContentType.create("a/b"), "c", "d"));
    assertEquals(ContentType.create("a/b;e=f;c=d"), ContentType.create(
        ContentType.create(ContentType.create("a/b"), "c", "d"), "e", "f"));
    assertEquals(ContentType.create("a/b;e=f;c=d"), ContentType.create(
        ContentType.create(ContentType.create("A/B"), "C", "D"), "E", "F"));
  }

  @Test
  public void createAndModify() {
    ContentType ct1 = ContentType.create("a/b");
    assertEquals(ContentType.create("a/b;c=d"), ContentType.create(ct1, "c", "d"));

    ContentType ct2 = ContentType.create("a/b;c=d");
    assertEquals(ContentType.create("a/b;c=d;e=f"), ContentType.create(ct2, "e", "f"));
    assertEquals(ContentType.create("a/b;c=g"), ContentType.create(ct2, "c", "g"));

    assertFalse(ContentType.create(ct2, "c", "g").equals(ct2));
  }

  @Test
  public void parse() {
    assertEquals(ContentType.APPLICATION_OCTET_STREAM, ContentType.parse("application/octet-stream"));

    assertNull(ContentType.parse("a"));
    assertNull(ContentType.parse("a/b;c"));
    assertNull(ContentType.parse("a/b;c="));
    assertNull(ContentType.parse("a/b;c= "));
  }

  @Test
  public void charsetUtf8() {
    ContentType ct1 = ContentType.create("a/b;charset=utf8");
    ContentType ct2 = ContentType.create("a/b;charset=utf-8");

    assertNotEquals(ct1, ct2);
    assertEquals(ct1.getType(), ct2.getType());
    assertEquals(ct1.getSubtype(), ct2.getSubtype());
    assertEquals("utf8", ct1.getParameters().get(ContentType.PARAMETER_CHARSET));
    assertEquals("utf-8", ct2.getParameters().get(ContentType.PARAMETER_CHARSET));
    assertEquals("utf-8", ct2.getParameter(ContentType.PARAMETER_CHARSET));

    assertTrue(ct1.isCompatible(ct2));
  }

  @Test
  public void toContentTypeString() {
    assertEquals("application/json;a=b;c=d",
        ContentType.create(ContentType.create(ContentType.APPLICATION_JSON, "a", "b"), "c", "d")
            .toContentTypeString());
  }

  private void createWrong(final String value) {
    try {
      ContentType.create(value);
      fail("Expected exception not thrown.");
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
    }
  }
}
