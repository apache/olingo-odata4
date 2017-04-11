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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class BasicBatchITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  private static final String HEADER_CONTENT_TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary";
  private static final String HEADER_CONTENT_TYPE_HTTP =
      HttpHeader.CONTENT_TYPE + ": " + ContentType.APPLICATION_HTTP.toContentTypeString();
  private static final String CONTENT_TYPE_HEADER_VALUE = " "
      + ContentType.create(ContentType.MULTIPART_MIXED, "boundary", "batch_123").toContentTypeString();
  private static final String ACCEPT_HEADER_VALUE = ContentType.APPLICATION_JSON.toContentTypeString();

  private static final String CRLF = "\r\n";

  @Test
  public void test() throws IOException {
    final String content = getRequest("ESAllPrim(32767)");
    final HttpURLConnection connection = batch(content);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    assertTrue(reader.readLine().contains("batch_"));
    checkMimeHeader(reader);
    blankLine(reader);

    assertEquals("HTTP/1.1 200 OK", reader.readLine());
    assertEquals("OData-Version: 4.0", reader.readLine());
    assertEquals("Content-Type: application/json;odata.metadata=minimal", reader.readLine());
    assertEquals("Content-Length: 605", reader.readLine());
    blankLine(reader);

    reader.close();
  }
  
  /* Tests for custom query options. Services may support additional custom query options
   * not defined in the OData specification, but they MUST NOT begin with the "$" .*/
  
  @Test
  public void testCustomQuery1() throws IOException {
    final String content = getRequest("ESAllPrim(32767)");
    final HttpURLConnection connection = batchWithCustomQuery(content, "#");
    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    assertTrue(reader.readLine().contains("batch_"));
    checkMimeHeader(reader);
    blankLine(reader);

    assertEquals("HTTP/1.1 200 OK", reader.readLine());
    assertEquals("OData-Version: 4.0", reader.readLine());
    assertEquals("Content-Type: application/json;odata.metadata=minimal", reader.readLine());
    assertEquals("Content-Length: 605", reader.readLine());
    blankLine(reader);

    reader.close();
  }
  
  @Test
  public void testCustomQuery2() throws IOException {
    final String content = getRequest("ESAllPrim(32767)");
    final HttpURLConnection connection = batchWithCustomQuery(content, "");
    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    assertTrue(reader.readLine().contains("batch_"));
    checkMimeHeader(reader);
    blankLine(reader);

    assertEquals("HTTP/1.1 200 OK", reader.readLine());
    assertEquals("OData-Version: 4.0", reader.readLine());
    assertEquals("Content-Type: application/json;odata.metadata=minimal", reader.readLine());
    assertEquals("Content-Length: 605", reader.readLine());
    blankLine(reader);

    reader.close();
  }
  
  @Test
  public void testCustomQuery3() throws IOException {
    final String content = getRequest("ESAllPrim(32767)");
    batchFailWithCustomQuery(content, "$");
  }
  


  @Test
  public void testInvalidRelativeURI() throws IOException {
    final String content = getRequest("/ESAllPrim(32767)");
    batchFail(content);
  }

  @Test
  public void testInvalidAbsoluteURI() throws IOException {
    final String content = getRequest(SERVICE_URI + "../ESAllPrim(32767)");
    HttpURLConnection connection = batch(content);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    assertTrue(reader.readLine().contains("batch_"));
    checkMimeHeader(reader);
    blankLine(reader);

    assertEquals("HTTP/1.1 400 Bad Request", reader.readLine());
  }

  @Test
  public void testNestedAbsoluteRequest() throws IOException {
    final String content = getRequest(SERVICE_URI + SERVICE_URI + "../ESAllPrim(32767)");
    HttpURLConnection connection = batch(content);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    assertTrue(reader.readLine().contains("batch_"));
    checkMimeHeader(reader);
    blankLine(reader);

    assertEquals("HTTP/1.1 400 Bad Request", reader.readLine());
  }

  @Test
  public void testInvalidHost() throws IOException {
    final String content = getRequest("http://otherhost/odata/odata.svc/ESAllPrim(32767)");
    batchFail(content);
  }

  private void checkMimeHeader(final BufferedReader reader) throws IOException {
    assertEquals(HEADER_CONTENT_TYPE_HTTP, reader.readLine());
    assertEquals(HEADER_CONTENT_TRANSFER_ENCODING_BINARY, reader.readLine());
  }

  private void blankLine(final BufferedReader reader) throws IOException {
    assertEquals("", reader.readLine()); // CRLF becomes an empty string
  }

  private String getRequest(final String uri) {
    return "--batch_123" + CRLF
        + HEADER_CONTENT_TRANSFER_ENCODING_BINARY + CRLF
        + HEADER_CONTENT_TYPE_HTTP + CRLF
        + CRLF
        + "GET " + uri + " HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_123--";
  }

  private HttpURLConnection batch(final String content) throws IOException {
    final HttpURLConnection connection = getConnection(content);

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    return connection;
  }

  private HttpURLConnection batchFail(final String content) throws IOException {
    final HttpURLConnection connection = getConnection(content);

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    return connection;
  }
  
  private HttpURLConnection batchWithCustomQuery(final String content, final String query) 
      throws IOException {
    HttpURLConnection connection = getConnectionForCustomQuery(content, query);
    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    return connection;
  }

  private HttpURLConnection batchFailWithCustomQuery(final String content, final String query) 
      throws IOException {
    final HttpURLConnection connection = getConnectionForCustomQuery(content, query);

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    return connection;
  }

  private HttpURLConnection getConnection(final String content) throws MalformedURLException, IOException,
      ProtocolException {
    final URL url = getUrl();
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.toString());
    connection.setRequestProperty(HttpHeader.CONTENT_TYPE, CONTENT_TYPE_HEADER_VALUE);
    connection.setRequestProperty(HttpHeader.ACCEPT, ACCEPT_HEADER_VALUE);
    connection.setDoOutput(true);
    final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.append(content);
    writer.close();
    connection.connect();
    return connection;
  }

  private HttpURLConnection getConnectionForCustomQuery(final String content, final String query) 
      throws MalformedURLException, IOException, ProtocolException {
    URL url = null;
    if(query.equals("$")){
      url = getUrlForCustomQueryWith$();
    }else if(query.equals("#")){
      url = getUrlWithSpecialCharacters();
    }else{
      url = getUrlForCustomQuery();
    }
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.toString());
    connection.setRequestProperty(HttpHeader.CONTENT_TYPE, CONTENT_TYPE_HEADER_VALUE);
    connection.setRequestProperty(HttpHeader.ACCEPT, ACCEPT_HEADER_VALUE);
    connection.setDoOutput(true);
    final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.append(content);
    writer.close();
    connection.connect();
    return connection;
  }
  
  private URL getUrlWithSpecialCharacters() throws MalformedURLException {
    return new URL(SERVICE_URI + "$batch" + "?#language=de");
  }
  
  private URL getUrlForCustomQuery() throws MalformedURLException {
    return new URL(SERVICE_URI + "$batch" + "?language=de");
  }
  
  private URL getUrlForCustomQueryWith$() throws MalformedURLException {
    return new URL(SERVICE_URI + "$batch" + "?$language=de");
  }
  
  private URL getUrl() throws MalformedURLException {
    return new URL(SERVICE_URI + "$batch");
  }

  @Override
  protected ODataClient getClient() {
    return null;
  }

}
