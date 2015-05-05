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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class AcceptTypeTest {

  @Test
  public void testWildcard() {
    List<AcceptType> atl = AcceptType.create("*/*");

    assertEquals(1, atl.size());
    assertEquals("*/*", atl.get(0).toString());

    assertTrue(atl.get(0).matches(ContentType.create("a/a")));
    assertTrue(atl.get(0).matches(ContentType.create("b/b")));
  }

  @Test
  public void testWildcardSubtype() {
    List<AcceptType> atl = AcceptType.create("a/*");

    assertEquals(1, atl.size());
    assertEquals("a/*", atl.get(0).toString());

    assertTrue(atl.get(0).matches(ContentType.create("a/a")));
    assertFalse(atl.get(0).matches(ContentType.create("b/b")));
  }

  @Test
  public void testSingleAcceptType() {
    assertTrue(AcceptType.create("a/a").get(0).matches(ContentType.create("a/a")));
    assertTrue(AcceptType.create("a/a;q=0.2").get(0).matches(ContentType.create("a/a")));
    assertFalse(AcceptType.create("a/a;x=y;q=0.2").get(0).matches(ContentType.create("a/a")));
    assertTrue(AcceptType.create("a/a;x=y;q=0.2").get(0).matches(ContentType.create("a/a;x=y")));
    assertTrue(AcceptType.create("a/a; q=0.2").get(0).matches(ContentType.create("a/a")));

    assertEquals("a/a;q=0.2;x=y", AcceptType.create("a/a;x=y;q=0.2").get(0).toString());
  }

  @Test
  public void testAcceptTypes() {
    List<AcceptType> atl;

    atl = AcceptType.create("b/b,*/*,a/a,c/*");
    assertNotNull(atl);
    assertTrue(atl.get(0).matches(ContentType.create("b/b")));
    assertTrue(atl.get(1).matches(ContentType.create("a/a")));
    assertEquals("c", atl.get(2).getType());
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(2).getSubtype());
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(3).getType());
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(3).getSubtype());

    atl = AcceptType.create("a/a;q=0.3,*/*;q=0.1,b/b;q=0.2");
    assertNotNull(atl);
    assertTrue(atl.get(0).matches(ContentType.create("a/a")));
    assertTrue(atl.get(1).matches(ContentType.create("b/b")));
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(2).getType());
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(2).getSubtype());

    atl = AcceptType.create("a/a;q=0.3,*/*;q=0.3");
    assertNotNull(atl);
    assertTrue(atl.get(0).matches(ContentType.create("a/a")));
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(1).getType());
    assertEquals(TypeUtil.MEDIA_TYPE_WILDCARD, atl.get(1).getSubtype());

    atl = AcceptType.create("a/a;x=y;q=0.1,b/b;x=y;q=0.3");
    assertNotNull(atl);
    assertTrue(atl.get(0).matches(ContentType.create("b/b;x=y")));
    assertFalse(atl.get(0).matches(ContentType.create("b/b;x=z")));
    assertTrue(atl.get(1).matches(ContentType.create("a/a;x=y")));
    assertFalse(atl.get(1).matches(ContentType.create("a/a;x=z")));

    atl = AcceptType.create("a/a; q=0.3, */*; q=0.1, b/b; q=0.2");
    assertNotNull(atl);
  }

  @Test
  public void testWithQParameter() {
    List<AcceptType> atl = AcceptType.create("application/json;q=0.2");

    assertEquals(1, atl.size());
    assertEquals("application", atl.get(0).getType());
    assertEquals("json", atl.get(0).getSubtype());
    assertEquals("0.2", atl.get(0).getParameters().get("q"));
    assertEquals("application/json;q=0.2", atl.get(0).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void abbreviationsNotAllowed() {
    AcceptType.create("application");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongQParameter() {
    AcceptType.create(" a/a;q=z ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void incompleteParameter() {
    AcceptType.create("a/b;parameter");
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingParameterValue() {
    AcceptType.create("a/b;parameter=");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parameterValueStartingWithWhitespace() {
    AcceptType.create("a/b;name= value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void missingParameterName() {
    AcceptType.create("a/b;=value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parameterNameWithWhitespace() {
    AcceptType.create("a/b;the name=value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void trailingSemicolon() {
    AcceptType.create("a/b;");
  }
}
