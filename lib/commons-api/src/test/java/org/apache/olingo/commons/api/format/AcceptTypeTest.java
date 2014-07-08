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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class AcceptTypeTest {

  @Test
  public void testMultiValueCreate() {
    List<AcceptType> atl = AcceptType.create("1/1,2/2 , 3/3 ");

    assertEquals(3, atl.size());
    assertEquals("1/1", atl.get(0).toString());
    assertEquals("2/2", atl.get(1).toString());
    assertEquals("3/3", atl.get(2).toString());
  }

  @Test
  public void testSingleValueCreate() {
    List<AcceptType> atl = AcceptType.create(" a/a ");

    assertEquals(1, atl.size());
    assertEquals("a/a", atl.get(0).toString());
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

  @Test
  public void testMatchWithQParameter() {
    List<AcceptType> atl = AcceptType.create("application/json;q=0.2");
    assertEquals(1, atl.size());
    assertTrue(atl.get(0).matches(ContentType.APPLICATION_JSON));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongQParameter() {
    AcceptType.create(" a/a;q=z ");
  }

  @Test
  public void testWildcard() {
    List<AcceptType> atl = AcceptType.create("*; q=.2");

    assertNotNull(atl);
    assertEquals(1, atl.size());
    assertEquals("*", atl.get(0).getType());
    assertEquals("*", atl.get(0).getSubtype());
    assertEquals(".2", atl.get(0).getParameters().get("q"));
    assertEquals(new Float(0.2), atl.get(0).getQuality());
  }
}
