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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.fit.util.StringHelper;
import org.apache.olingo.server.tecsvc.async.TechnicalAsyncService;
import org.junit.Test;

public class PreferHeaderForGetAndDeleteITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void preferHeaderMinimal_GetEntitySet() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_GetEntitySet() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_GetEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_GetEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }

  @Test
  public void preferHeaderRepresentation_DeleteEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.DELETE.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_DeleteEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.DELETE.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_GetComplexProperty() throws Exception {
    URL url = new URL(SERVICE_URI + "ESCompCollDerived(12345)/PropertyCompAno");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_GetSimpleProperty() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/PropertyString");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_GetNavigationProperty() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/NavPropertyETTwoPrimOne");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_GetReference() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(32767)/NavPropertyETTwoPrimOne/$ref");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_GetMediaEntitySet() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMedia");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_GetMediaEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMedia(1)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_PostMediaEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMedia");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.setRequestProperty(HttpHeader.CONTENT_TYPE, "application/json");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_PutMediaEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMedia(1)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.PUT.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderRepresentation_Count() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim/$count");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=representation");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=representation' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_UnboundFunction() throws Exception {
    URL url = new URL(SERVICE_URI + "FICRTETKeyNav()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.PREFER, "return=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Test
  public void preferHeaderMinimal_Batch() throws Exception {
    InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("basicBatchPost.batch");
    final HttpURLConnection connection = postBatch(content, "batch_8194-cf13-1f56", 1, true);

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String response = IOUtils.toString(connection.getErrorStream());
    assertTrue(response.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  private HttpURLConnection postBatch(final InputStream content, String batchBoundary, 
      int sleepTime, boolean preferHeader)
      throws IOException {

    Map<String, String> headers = new HashMap<String, String>();
    String contentTypeValue = ContentType.create(
        ContentType.MULTIPART_MIXED, "boundary", batchBoundary).toContentTypeString();
    headers.put(HttpHeader.CONTENT_TYPE, contentTypeValue);
    headers.put(HttpHeader.ACCEPT, "application/json");
    if(sleepTime >= 0 && preferHeader) {
      headers.put(HttpHeader.PREFER, "respond-async; " +
          TechnicalAsyncService.TEC_ASYNC_SLEEP + "=" + String.valueOf(sleepTime));
    }
    if (preferHeader) {
      headers.put(HttpHeader.PREFER, "return=minimal");
    }
    StringHelper.Stream s = StringHelper.toStream(content);
    final URL url = new URL(SERVICE_URI + "$batch");
    return postRequest(url, s.asString("utf-8"), headers);
  }
  
  private HttpURLConnection postRequest(final URL url, final String content, final Map<String, String> headers)
      throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.toString());
    
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    
    connection.setDoOutput(true);
    final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.append(content);
    writer.close();
    connection.connect();
    return connection;
  }
  
  @Test
  public void preferHeaderMinimal_InBatchPayload() throws Exception {
    InputStream content = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("basicBatchPostWithPreferHeader.batch");
    final HttpURLConnection connection = postBatch(content, "batch_8194-cf13-1f56", 1, false);

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    StringHelper.Stream resultBody = StringHelper.toStream(connection.getInputStream());
    String resBody = resultBody.asString();
    assertTrue(resBody.contains("The Prefer header 'return=minimal' is not supported for this HTTP Method."));
    
  }
  
  @Override
  protected ODataClient getClient() {
    return null;
  }
}
