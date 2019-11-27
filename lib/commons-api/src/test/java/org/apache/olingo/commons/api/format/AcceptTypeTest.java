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
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;


public class AcceptTypeTest {

  @Test
  public void wildcard() {
    List<AcceptType> atl = AcceptType.create("*/*");

    assertEquals(1, atl.size());
    assertEquals("*/*", atl.get(0).toString());

    assertTrue(atl.get(0).matches(ContentType.create("a/a")));
    assertTrue(atl.get(0).matches(ContentType.create("b/b")));
  }

  @Test
  public void wildcardSubtype() {
    List<AcceptType> atl = AcceptType.create("a/*");

    assertEquals(1, atl.size());
    assertEquals("a/*", atl.get(0).toString());

    assertTrue(atl.get(0).matches(ContentType.create("a/a")));
    assertFalse(atl.get(0).matches(ContentType.create("b/b")));
  }

  @Test
  public void singleAcceptType() {
    assertTrue(AcceptType.create("a/a").get(0).matches(ContentType.create("a/a")));
    assertTrue(AcceptType.create("a/a;q=0.2").get(0).matches(ContentType.create("a/a")));
    assertFalse(AcceptType.create("a/a;x=y;q=0.2").get(0).matches(ContentType.create("a/a")));
    assertTrue(AcceptType.create("a/a;x=y;q=0.2").get(0).matches(ContentType.create("a/a;x=y")));
    assertTrue(AcceptType.create("a/a; q=0.2").get(0).matches(ContentType.create("a/a")));

    assertEquals("a/a;q=0.2;x=y", AcceptType.create("a/a;x=y;q=0.2").get(0).toString());
  }

  @Test
  public void acceptTypes() {
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
  public void withQParameter() {
    List<AcceptType> acceptTypes = AcceptType.create("application/json;q=0.2");

    assertEquals(1, acceptTypes.size());
    final AcceptType acceptType = acceptTypes.get(0);
    assertEquals("application", acceptType.getType());
    assertEquals("json", acceptType.getSubtype());
    assertEquals("0.2", acceptType.getParameters().get(TypeUtil.PARAMETER_Q));
    assertEquals("0.2", acceptType.getParameter(TypeUtil.PARAMETER_Q));
    assertEquals(Float.valueOf(0.2F), acceptType.getQuality());
    assertEquals("application/json;q=0.2", acceptType.toString());
  }

  @Test
  public void formatErrors() {
    expectCreateError("/");
    expectCreateError("//");
    expectCreateError("///");
    expectCreateError("a/b/c");
    expectCreateError("a//b");
  }

  @Test
  public void abbreviationsNotAllowed() {
    expectCreateError("application");
  }

  @Test
  public void wildcardError() {
    expectCreateError("*/json");
  }

  @Test
  public void wrongQParameter() {
    expectCreateError(" a/a;q=z ");
    expectCreateError("a/a;q=42");
    expectCreateError("a/a;q=0.0001");
    expectCreateError("a/a;q='");
    expectCreateError("a/a;q=0.8,abc");
  }

  @Test
  public void parameterErrors() {
    expectCreateError("a/b;parameter");
    expectCreateError("a/b;parameter=");
    expectCreateError("a/b;name= value");
    expectCreateError("a/b;=value");
    expectCreateError("a/b;the name=value");
  }

  @Test
  public void trailingSemicolon() {
    expectCreateError("a/b;");
  }

  @Test
  public void fromContentType() {
    final List<AcceptType> acceptType = AcceptType.fromContentType(ContentType.APPLICATION_JSON);
    assertNotNull(acceptType);
    assertEquals(1, acceptType.size());
    assertEquals(ContentType.APPLICATION_JSON.toContentTypeString(), acceptType.get(0).toString());
  }

  private void expectCreateError(final String value) {
    try {
      AcceptType.create(value);
      fail("Expected exception not thrown.");
    } catch (final IllegalArgumentException e) {
      assertNotNull(e);
    }
  }
  
  @Test
  public void multipleTypeswithQParameter() {
    List<AcceptType> acceptTypes = AcceptType.create("application/json;q=0.2,application/json;q=0.2");

    assertEquals(2, acceptTypes.size());
    final AcceptType acceptType = acceptTypes.get(0);
    assertEquals("application", acceptType.getType());
    assertEquals("json", acceptType.getSubtype());
    assertEquals("0.2", acceptType.getParameters().get(TypeUtil.PARAMETER_Q));
    assertEquals("0.2", acceptType.getParameter(TypeUtil.PARAMETER_Q));
    assertEquals(Float.valueOf(0.2F), acceptType.getQuality());
    assertEquals("application/json;q=0.2", acceptType.toString());
  }
  
  @Test
  public void multipleTypeswithIllegalTypes() {
    List<AcceptType> acceptTypes = AcceptType.create("application/json;q=0.2,abc/xyz");

    assertEquals(2, acceptTypes.size());
    final AcceptType acceptType = acceptTypes.get(1);
    assertEquals("application", acceptType.getType());
    assertEquals("json", acceptType.getSubtype());
    assertEquals("0.2", acceptType.getParameters().get(TypeUtil.PARAMETER_Q));
    assertEquals("0.2", acceptType.getParameter(TypeUtil.PARAMETER_Q));
    assertEquals(Float.valueOf(0.2F), acceptType.getQuality());
    assertEquals("application/json;q=0.2", acceptType.toString());
  }
  
  @Test
  public void multipleFormatErrors() {
    expectCreateError("/,abc,a/a;parameter=");
  }
  
  @Test
  public void nullAcceptType() {
    expectCreateError(null);
  }
  
  @Test
  public void emptyAcceptType() {
    expectCreateError("");
  }
  
  @Test
  public void noTypeAcceptType() {
    expectCreateError("/json");
  }
  
  @Test
  public void withCharset() {
    List<AcceptType> acceptTypes = AcceptType.create("application/json;charset=utf-8");
    assertEquals(1, acceptTypes.size());
    final AcceptType acceptType = acceptTypes.get(0);
    assertEquals("application", acceptType.getType());
    assertEquals("json", acceptType.getSubtype());
    assertEquals("utf-8", acceptType.getParameter(ContentType.PARAMETER_CHARSET));
    
    assertTrue(acceptType.matches(ContentType.create("application/json;"
        + "odata.metadata=minimal;charset=utf-8")));
    assertFalse(acceptType.matches(ContentType.create("application/atom+xml;"
        + "odata.metadata=minimal;charset=utf-8")));
    assertFalse(acceptType.matches(ContentType.create("application/json;"
        + "odata.metadata=minimal")));
  }
  
  @Test
  public void withSubtypeStar1() {
    List<AcceptType> acceptTypes = AcceptType.create("application/json,application/*");
    assertEquals(2, acceptTypes.size());
    final AcceptType acceptType1 = acceptTypes.get(0);
    assertEquals("application", acceptType1.getType());
    assertEquals("json", acceptType1.getSubtype());
    
    final AcceptType acceptType2 = acceptTypes.get(1);
    assertEquals("application", acceptType2.getType());
    assertEquals("*", acceptType2.getSubtype());
  }
  
  @Test
  public void withSubtypeStar2() {
    List<AcceptType> acceptTypes = AcceptType.create("application/*,application/json");
    assertEquals(2, acceptTypes.size());
    final AcceptType acceptType1 = acceptTypes.get(0);
    assertEquals("application", acceptType1.getType());
    assertEquals("json", acceptType1.getSubtype());
    
    final AcceptType acceptType2 = acceptTypes.get(1);
    assertEquals("application", acceptType2.getType());
    assertEquals("*", acceptType2.getSubtype());
  }
 }
