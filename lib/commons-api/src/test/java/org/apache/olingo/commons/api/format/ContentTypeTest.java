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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ContentTypeTest {

  @Test
  public void testCreate() {
    assertEquals("a/b", ContentType.create("a/b").toContentTypeString());
    assertEquals(ContentType.create("a/b;c=d;x=y"), ContentType.create("a/b;x=y;c=d"));
    assertEquals(ContentType.create("a/b;c=d;x=y"), ContentType.create("a/b; x=y; c=d"));
    assertEquals(ContentType.create("A/B"), ContentType.create("a/b"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail1() {
    ContentType.create("a");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail2() {
    ContentType.create(" a / b ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail3() {
    ContentType.create("a/b;");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail4() {
    ContentType.create("a/b;parameter");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail5() {
    ContentType.create("a/b;parameter=");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail6() {
    ContentType.create("a/b;=value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail7() {
    ContentType.create("a/b;the name=value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFail8() {
    ContentType.create("a/b;name= value");
  }

  @Test
  public void testCreateWithParameter() {
    assertEquals(ContentType.create("a/b;c=d"), ContentType.create("a/b", "c=d"));
    assertEquals(ContentType.create("a/b;e=f;c=d"), ContentType.create("a/b", "c=d", "e=f"));
    assertEquals(ContentType.create("a/b;e=f;c=d"), ContentType.create("A/B", "C=D", "E=F"));
  }

  @Test
  public void testCreateAndModify() {
    ContentType ct1 = ContentType.create("a/b");
    assertEquals(ContentType.create("a/b;c=d"), ContentType.create(ct1, "c=d"));

    ContentType ct2 = ContentType.create("a/b;c=d");
    assertEquals(ContentType.create("a/b;c=d;e=f"), ContentType.create(ct2, "e=f"));
    assertEquals(ContentType.create("a/b;c=g"), ContentType.create(ct2, "c=g"));

    assertFalse(ContentType.create(ct2, "c=g").equals(ct2));
  }

  @Test
  public void testParse() {
    assertNull(ContentType.parse("a"));
    assertFalse(ContentType.isParseable("a"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWildcardFail() {
    ContentType.create("*/*");
  }

  @Test
  public void testCharsetUtf8() {
    ContentType ct1 = ContentType.create("a/b;charset=utf8");
    ContentType ct2 = ContentType.create("a/b;charset=utf-8");

    assertNotEquals(ct1, ct2);
    assertEquals(ct1.getType(), ct2.getType());
    assertEquals(ct1.getSubtype(), ct2.getSubtype());
    assertEquals("utf8", ct1.getParameters().get("charset"));
    assertEquals("utf-8", ct2.getParameters().get("charset"));

    assertTrue(ct1.isCompatible(ct2));
  }
}
