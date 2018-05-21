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
package org.apache.olingo.fit.tecsvc.http;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class EmptyNavigationPropertiesITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void emptyNavigationManyFollowedByToOne() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/NavPropertyETTwoPrimMany(32767)"
        + "/NavPropertyETAllPrimOne?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void emptyNavigationManyFollowedByToMany() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/NavPropertyETTwoPrimMany(32767)"
        + "/NavPropertyETAllPrimMany?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void nonemptyNavigationManyFollowedByEmptyToOne() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/NavPropertyETTwoPrimMany(-365)"
        + "/NavPropertyETAllPrimOne?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void emptyNavigationOneFollowedByToMany() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim(32766)/NavPropertyETAllPrimOne"
        + "/NavPropertyETTwoPrimMany?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void nonemptyNavigationManyFollowedByToManyNonExistingKey() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/NavPropertyETTwoPrimMany(-365)"
        + "/NavPropertyETAllPrimMany(123)?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void emptyNavigationOne() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim(32766)/NavPropertyETAllPrimOne?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void nonExistingEntityToNavigationOne() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(12345)/NavPropertyETTwoPrimOne?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void emptyNavigationMany() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(-32768)/NavPropertyETTwoPrimMany?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(IOUtils.toString(connection.getInputStream()));
  }
  
  @Override
  protected ODataClient getClient() {
    return null;
  }
}
