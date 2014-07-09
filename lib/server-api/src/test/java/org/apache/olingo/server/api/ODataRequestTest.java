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
package org.apache.olingo.server.api;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ODataRequestTest {

  @Test
  public void testHeader() {
    ODataRequest r = new ODataRequest();

    r.addHeader("aa", Arrays.asList("cc"));

    assertEquals("cc", r.getHeaders("aa").get(0));
    assertEquals("cc", r.getHeaders("aA").get(0));
    assertEquals("cc", r.getHeaders("AA").get(0));

    assertEquals("cc", r.getHeader("aa"));
    assertEquals("cc", r.getHeader("aA"));
    assertEquals("cc", r.getHeader("AA"));

  }

  @Test
  public void testHeader2() {
    ODataRequest r = new ODataRequest();
    r.addHeader("AA", Arrays.asList("dd"));

    assertEquals("dd", r.getHeaders("aa").get(0));
    assertEquals("dd", r.getHeaders("aA").get(0));
    assertEquals("dd", r.getHeaders("AA").get(0));
  }

  @Test
  public void testMultiValueHeader() {
    ODataRequest r = new ODataRequest();

    r.addHeader("aa", Arrays.asList("a", "b"));

    assertEquals("a", r.getHeaders("aa").get(0));
    assertEquals("b", r.getHeaders("aA").get(1));

    r.addHeader("Aa", Arrays.asList("c"));

    assertEquals("a", r.getHeaders("aa").get(0));
    assertEquals("b", r.getHeaders("aA").get(1));
    assertEquals("c", r.getHeaders("aA").get(2));
  }
}
