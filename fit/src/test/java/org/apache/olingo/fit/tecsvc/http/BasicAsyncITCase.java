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

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.fit.util.StringHelper;
import org.apache.olingo.server.tecsvc.async.TechnicalAsyncService;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test support of asynchronous batch within the TecSvc without using the OData client library (only
 * use java.net.* components for plain http communication).
 */
public class BasicAsyncITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  private static final String HEADER_CONTENT_TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary";
  private static final String HEADER_CONTENT_TYPE_HTTP =
      HttpHeader.CONTENT_TYPE + ": " + ContentType.APPLICATION_HTTP.toContentTypeString();
  private static final String DEFAULT_BATCH_BOUNDARY = "batch_123";
  private static final String ACCEPT_HEADER_VALUE = ContentType.APPLICATION_JSON.toContentTypeString();

  private static final String CRLF = "\r\n";
  private static final String DEFAULT_ENCODING = "utf-8";
  public static final long SLEEP_TIMEOUT_IN_MS = 200;

  /**
   * Works
   */
  @Test
  public void batchAsync() throws Exception {
    final String content = getDefaultRequest("ESAllPrim(32767)");
    final HttpURLConnection connection = postBatch(StringHelper.encapsulate(content), DEFAULT_BATCH_BOUNDARY, 1);
    StringHelper.Stream response = StringHelper.toStream(connection.getInputStream());

    assertEquals(0, response.byteLength());

    Map<String, List<String>> headerFields = connection.getHeaderFields();
    assertEquals("HTTP/1.1 202 Accepted", headerFields.get(null).get(0));
    assertTrue(Pattern.matches("http:\\/\\/localhost:9080\\/odata-server-tecsvc\\/status\\/\\d*",
        headerFields.get("Location").get(0)));
    assertEquals("respond-async", headerFields.get("Preference-Applied").get(0));

    // get async response (still pending)
    String respondUri = headerFields.get("Location").get(0);
    HttpURLConnection statusRequest = getRequest(new URL(respondUri), Collections.<String, String>emptyMap());
    StringHelper.Stream statusBody = StringHelper.toStream(statusRequest.getInputStream());
    Map<String, List<String>> statusHeaderFields = statusRequest.getHeaderFields();
    assertEquals("HTTP/1.1 202 Accepted", statusHeaderFields.get(null).get(0));
    assertEquals(0, statusBody.byteLength());

    // get async response (now finished)
    HttpURLConnection result = waitTillDone(respondUri, 4);

    StringHelper.Stream resultBody = StringHelper.toStream(result.getInputStream());
    Map<String, List<String>> resultHeaderFields = result.getHeaderFields();
    String resBody = resultBody.asString();

    assertEquals("HTTP/1.1 200 OK", resultHeaderFields.get(null).get(0));
    assertEquals(1007, resultBody.byteLength());
    contains(resBody,
        "HTTP/1.1 200 OK",
        "OData-Version: 4.0",
        "Content-Length: 605",
        "\"@odata.context\":\"$metadata#ESAllPrim/$entity\"",
        "\"PropertyInt16\":32767",
        "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\",",
        "--batch_", "--");
  }

  /**
   * Test with changeset
   */
  @Test
  public void asyncChangesetViaPost() throws Exception {
    InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("basicBatchPost.batch");
    final HttpURLConnection connection = postBatch(content, "batch_8194-cf13-1f56", 1);
    StringHelper.Stream response = StringHelper.toStream(connection.getInputStream());
    assertEquals(0, response.byteLength());

    Map<String, List<String>> headerFields = connection.getHeaderFields();
    assertEquals("HTTP/1.1 202 Accepted", headerFields.get(null).get(0));
    // because of generated status id it is only checked that the location starts correct and contains a number
    assertTrue(Pattern.matches("http:\\/\\/localhost:9080\\/odata-server-tecsvc\\/status\\/\\d*",
        headerFields.get("Location").get(0)));
    assertEquals("respond-async", headerFields.get("Preference-Applied").get(0));

    // get async response (still pending)
    String respondUri = headerFields.get("Location").get(0);
    HttpURLConnection statusRequest = getRequest(new URL(respondUri), Collections.<String, String>emptyMap());
    StringHelper.Stream statusBody = StringHelper.toStream(statusRequest.getInputStream());
    Map<String, List<String>> statusHeaderFields = statusRequest.getHeaderFields();
    assertEquals("HTTP/1.1 202 Accepted", statusHeaderFields.get(null).get(0));
    assertEquals(0, statusBody.byteLength());

    // get async response (now finished)
    HttpURLConnection result = waitTillDone(respondUri, 4);
    StringHelper.Stream resultBody = StringHelper.toStream(result.getInputStream());
    Map<String, List<String>> resultHeaderFields = result.getHeaderFields();
    String resBody = resultBody.asString();
    assertEquals("HTTP/1.1 200 OK", resultHeaderFields.get(null).get(0));
    assertEquals(2321, resultBody.byteLength());
    contains(resBody,
        "HTTP/1.1 200 OK",
        "OData-Version: 4.0",
        "Content-Length: 605",
        "\"@odata.context\":\"$metadata#ESAllPrim/$entity\"",
        "\"PropertyInt16\":32767",
        "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\",",
        "--batch_", "--");
  }

  /**
   * Validates that the content contains all given values in same order as the parameters are given.
   * If the content does not contain a value or not in the given order <code>Assert.fail()</code> is called.
   *
   * @param content content which is checked
   * @param values values which must be in content (and in correct order)
   */
  private void contains(String content, String... values) {
    int index = 0;
    for (String value : values) {
      int currentIndex = content.indexOf(value, index);
      if(currentIndex == -1) {
        if(content.contains(value)) {
          int foundIndex = content.indexOf(value);
          fail("Expected value '" + value + "' was found but not were expected " +
              "(started to search from position '" + index + "' but found first occurrence at index '" +
              foundIndex + "').");
        } else {
          fail("Expected value '" + value + "' was not found");
        }
      }
      index = currentIndex;
    }
  }

  private String getDefaultRequest(final String uri) {
    return "--" + DEFAULT_BATCH_BOUNDARY + CRLF
        + HEADER_CONTENT_TRANSFER_ENCODING_BINARY + CRLF
        + HEADER_CONTENT_TYPE_HTTP + CRLF
        + CRLF
        + "GET " + uri + " HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--" + DEFAULT_BATCH_BOUNDARY + "--";
  }

  private HttpURLConnection waitTillDone(String location, int maxWaitInSeconds) throws Exception {
    HttpURLConnection result = null;
    int waitCounter = maxWaitInSeconds * 1000;

    while(result == null && waitCounter > 0) {
      HttpURLConnection statusRequest = getRequest(new URL(location), Collections.<String, String>emptyMap());
      Map<String, List<String>> statusHeaderFields = statusRequest.getHeaderFields();
      String statusHeader = statusHeaderFields.get(null).get(0);
      if("HTTP/1.1 202 Accepted".equals(statusHeader)) {
        TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT_IN_MS);
        waitCounter -= SLEEP_TIMEOUT_IN_MS;
      } else if("HTTP/1.1 200 OK".equals(statusHeader)) {
        result = statusRequest;
      } else {
        throw new RuntimeException("Unexpected status header ('" + statusHeader +
                "') for async status request on: " + location);
      }
    }
    return result;
  }

  private HttpURLConnection postRequest(final URL url, final String content, final Map<String, String> headers)
      throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.toString());
    //
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    //
    connection.setDoOutput(true);
    final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.append(content);
    writer.close();
    connection.connect();
    return connection;
  }

  private HttpURLConnection getRequest(URL url, Map<String, String> headers) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.toString());
    //
    for (Map.Entry<String, String> header : headers.entrySet()) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }
    //
    connection.connect();
    return connection;
  }

  private HttpURLConnection postBatch(final InputStream content, String batchBoundary, int sleepTime)
      throws IOException {

    Map<String, String> headers = new HashMap<String, String>();
    String contentTypeValue = ContentType.create(
        ContentType.MULTIPART_MIXED, "boundary", batchBoundary).toContentTypeString();
    headers.put(HttpHeader.CONTENT_TYPE, contentTypeValue);
    headers.put(HttpHeader.ACCEPT, ACCEPT_HEADER_VALUE);
    if(sleepTime >= 0) {
      headers.put(HttpHeader.PREFER, "respond-async; " +
          TechnicalAsyncService.TEC_ASYNC_SLEEP + "=" + String.valueOf(sleepTime));
    }

    StringHelper.Stream s = StringHelper.toStream(content);
    final URL url = new URL(SERVICE_URI + "$batch");
    return postRequest(url, s.asString(DEFAULT_ENCODING), headers);
  }

  @Override
  protected ODataClient getClient() {
    return null;
  }
}
