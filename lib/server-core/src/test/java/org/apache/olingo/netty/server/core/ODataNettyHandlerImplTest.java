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
package org.apache.olingo.netty.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.junit.Test;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

public class ODataNettyHandlerImplTest {

  @Test
  public void extractMethodForNettyRequest() throws Exception {
    String[][] mm = {
        { "GET", null, null, "GET" },
        { "GET", "xxx", "yyy", "GET" },
        { "PUT", "xxx", "yyy", "PUT" },
        { "DELETE", "xxx", "yyy", "DELETE" },
        { "PATCH", "xxx", "yyy", "PATCH" },

        { "POST", null, null, "POST" },
        { "POST", null, "GET", "GET" },
        { "POST", null, "PATCH", "PATCH" },

        { "POST", "GET", null, "GET" },
        { "POST", "PATCH", null, "PATCH" },

        { "POST", "GET", "GET", "GET" },
        { "HEAD", null, null, "HEAD" }
    };

    for (String[] m : mm) {

      HttpRequest hr = mock(HttpRequest.class);
      io.netty.handler.codec.http.HttpMethod hm = mock(io.netty.handler.codec.http.HttpMethod.class);
      when(hr.method()).thenReturn(hm);
      when(hm.name()).thenReturn(m[0]);
      HttpHeaders hh = mock(HttpHeaders.class);
      when(hr.headers()).thenReturn(hh);
      when(hh.get("X-HTTP-Method")).thenReturn(m[1]);
      when(hh.get("X-HTTP-Method-Override")).thenReturn(m[2]);

      assertEquals(HttpMethod.valueOf(m[3]), ODataNettyHandlerImpl.extractMethod(hr));
    }
  }
  
  @Test
  public void extractMethodFailForNettyRequest() throws Exception {
    String[][] mm = {
        { "POST", "bla", null },
        { "POST", "PUT", "PATCH" },
        { "OPTIONS", null, null }
    };

    for (String[] m : mm) {

      HttpRequest hr = mock(HttpRequest.class);
      io.netty.handler.codec.http.HttpMethod hm = mock(io.netty.handler.codec.http.HttpMethod.class);
      when(hr.method()).thenReturn(hm);

      when(hm.name()).thenReturn(m[0]);
      HttpHeaders hh = mock(HttpHeaders.class);
      when(hr.headers()).thenReturn(hh);
      when(hh.get("X-HTTP-Method")).thenReturn(m[1]);
      when(hh.get("X-HTTP-Method-Override")).thenReturn(m[2]);

      try {
        ODataNettyHandlerImpl.extractMethod(hr);
        fail();
      } catch (ODataLibraryException e) {
        // expected
      }
    }
  }
  
  @Test
  public void extractUriForNettyRequests() {

    //@formatter:off (Eclipse formatter)
    //CHECKSTYLE:OFF (Maven checkstyle)
    String [][] uris = {
        /* 0: cp         1: sr          2: od       3: qp        4: spl  */
        {  "",           "",          "",          "",         "0"},
        {  "",           "",          "/",         "",         "0"},
        {  "",           "",          "/od",       "",         "0"},
        {  "",           "",          "/od/",      "",         "0"},

        {  "/cp",        "",          "",          "",         "0"},
        {  "/cp",        "",          "/",         "",         "0"},
        {  "/cp",        "",          "/od",       "",         "0"},
        {  "",           "/sr",       "",          "",         "1"},
        {  "",           "/sr",       "/",         "",         "1"},
        {  "",           "/sr",       "/od",       "",         "1"},
        {  "",           "/sr/sr",    "",          "",         "2"},
        {  "",           "/sr/sr",    "/",         "",         "2"},
        {  "",           "/sr/sr",    "/od",       "",         "2"},

        {  "/cp",        "/sr",       "/",         "",         "1"},
        {  "/cp",        "/sr",       "/od",       "",         "1"},
        
        {  "",           "",          "",          "qp",       "0"},
        {  "",           "",          "/",         "qp",       "0"},
        {  "/cp",        "/sr",       "/od",       "qp",       "1"},

        {  "/c%20p",     "/s%20r",    "/o%20d",    "p+q",      "1"},
    };
    //@formatter:on
    // CHECKSTYLE:on

    for (String[] p : uris) {
      HttpRequest hr = mock(HttpRequest.class);

      String requestUrl = p[0] + p[1] + p[2];
      if (!p[3].equals("") || p[3].length() > 0) {
    	  requestUrl += "?$" + p[3];
      }

      when(hr.uri()).thenReturn(requestUrl);
      
      ODataRequest odr = new ODataRequest();
      ODataNettyHandlerImpl.fillUriInformationFromHttpRequest(odr, hr, Integer.parseInt(p[4]), p[0]);

      String rawBaseUri = p[0] + p[1];
      String rawODataPath = p[2];
      String rawQueryPath = "".equals(p[3]) ? null : "$" + p[3];
      String rawRequestUri = p[0] + p[1] + p[2] + ("".equals(p[3]) ? "" : "?$" + p[3]);
      String rawServiceResolutionUri = "".equals(p[1]) ? null : p[1];

      assertEquals(rawBaseUri, odr.getRawBaseUri());
      assertEquals(rawODataPath, odr.getRawODataPath());
      assertEquals(rawQueryPath, odr.getRawQueryPath());
      assertEquals(rawRequestUri, odr.getRawRequestUri());
      assertEquals(rawServiceResolutionUri, odr.getRawServiceResolutionUri());
    }
  }
}
