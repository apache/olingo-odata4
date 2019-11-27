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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ODataResponseTest {

  @Test
  public void testResponse() {
    ODataResponse  r = new ODataResponse ();
    assertNotNull(r);
    r.addHeader("header", "value");
    List<String> list = new ArrayList<String>();
    r.addHeader("headerList", list );
    assertNotNull(r.getAllHeaders());
  }
  
  @Test
  public void testError() {
    ODataServerError  r = new ODataServerError ();
    assertNotNull(r);
    assertNull(r.getLocale());
    Map<String, String> map = new HashMap<String, String>();
    r.setInnerError(map);
    assertNotNull(r.getInnerError());
  }
}
