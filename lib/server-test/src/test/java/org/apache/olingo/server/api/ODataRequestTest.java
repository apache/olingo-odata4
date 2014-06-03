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
    
    assertEquals("cc", r.getHeader("aa").get(0));
    assertEquals("cc", r.getHeader("aA").get(0));
    assertEquals("cc", r.getHeader("AA").get(0));
    
    r.addHeader("AA", Arrays.asList("dd"));
    
    assertEquals("dd", r.getHeader("aa").get(0));
    assertEquals("dd", r.getHeader("aA").get(0));
    assertEquals("dd", r.getHeader("AA").get(0));   
  }
}
