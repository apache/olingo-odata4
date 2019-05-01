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
package org.apache.olingo.client.core.communication.response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.NoContentException;
import org.apache.olingo.client.core.ConfigurationImpl;
import org.apache.olingo.client.core.communication.util.PipedInputStream;
import org.apache.olingo.client.core.communication.util.PipedOutputStream;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchController;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchLineIteratorImpl;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchUtilities;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of an OData response.
 */
public abstract class AbstractODataResponse implements ODataResponse {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(ODataResponse.class);

  protected final ODataClient odataClient;

  private static final byte[] CRLF = {13, 10};

  /**
   * HTTP client.
   */
  protected final HttpClient httpClient;

  /**
   * HTTP response.
   */
  protected final HttpResponse res;

  /**
   * Response headers.
   */
  protected final Map<String, Collection<String>> headers =
      new TreeMap<String, Collection<String>>(String.CASE_INSENSITIVE_ORDER);

  /**
   * Response code.
   */
  protected int statusCode = -1;

  /**
   * Response message.
   */
  protected String statusMessage = null;

  /**
   * Response body/payload.
   */
  protected InputStream payload = null;

  /**
   * Initialization check.
   */
  protected boolean hasBeenInitialized = false;

  /**
   * Batch info (if to be batched).
   */
  protected ODataBatchController batchInfo = null;
  
  private byte[] inputContent = null;

  public AbstractODataResponse(
      final ODataClient odataClient, final HttpClient httpclient, final HttpResponse res) {

    this.odataClient = odataClient;
    this.httpClient = httpclient;
    this.res = res;
    if (res != null) {
      initFromHttpResponse(res);
    }
  }

  @Override
  public Collection<String> getHeaderNames() {
    return headers.keySet();
  }

  @Override
  public Collection<String> getHeader(final String name) {
    return headers.get(name);
  }

  @Override
  public String getETag() {
    final Collection<String> etag = getHeader(HttpHeader.ETAG);
    return etag == null || etag.isEmpty() ? null : etag.iterator().next();
  }

  @Override
  public String getContentType() {
    final Collection<String> contentTypes = getHeader(HttpHeader.CONTENT_TYPE);
    return contentTypes == null || contentTypes.isEmpty() ? null : contentTypes.iterator().next();
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getStatusMessage() {
    return statusMessage;
  }

  @Override
  public final ODataResponse initFromHttpResponse(final HttpResponse res) {
    try {
      this.payload = res.getEntity() == null ? null : res.getEntity().getContent();
      this.inputContent = null;
    } catch (final IllegalStateException e) {
      HttpClientUtils.closeQuietly(res);
      LOG.error("Error retrieving payload", e);
      throw new ODataRuntimeException(e);
    } catch (final IOException e) {
      HttpClientUtils.closeQuietly(res);
      LOG.error("Error retrieving payload", e);
      throw new ODataRuntimeException(e);
    }
    for (Header header : res.getAllHeaders()) {
      final Collection<String> headerValues;
      if (headers.containsKey(header.getName())) {
        headerValues = headers.get(header.getName());
      } else {
        headerValues = new HashSet<String>();
        headers.put(header.getName(), headerValues);
      }

      headerValues.add(header.getValue());
    }

    statusCode = res.getStatusLine().getStatusCode();
    statusMessage = res.getStatusLine().getReasonPhrase();

    hasBeenInitialized = true;
    return this;
  }

  @Override
  public ODataResponse initFromBatch(
      final Map.Entry<Integer, String> responseLine,
      final Map<String, Collection<String>> headers,
      final ODataBatchLineIterator batchLineIterator,
      final String boundary) {

    if (hasBeenInitialized) {
      throw new IllegalStateException("Request already initialized");
    }

    this.batchInfo = new ODataBatchController(batchLineIterator, boundary);

    this.statusCode = responseLine.getKey();
    this.statusMessage = responseLine.getValue();
    this.headers.putAll(headers);

    this.hasBeenInitialized = true;
    return this;
  }

  @Override
  public ODataResponse initFromEnclosedPart(final InputStream part) {
    try {
      if (hasBeenInitialized) {
        throw new IllegalStateException("Request already initialized");
      }

      final ODataBatchLineIteratorImpl batchLineIterator =
          new ODataBatchLineIteratorImpl(IOUtils.lineIterator(part, Constants.UTF8));

      final Map.Entry<Integer, String> partResponseLine = ODataBatchUtilities.readResponseLine(batchLineIterator);
      LOG.debug("Retrieved async item response {}", partResponseLine);

      this.statusCode = partResponseLine.getKey();
      this.statusMessage = partResponseLine.getValue();

      final Map<String, Collection<String>> partHeaders = ODataBatchUtilities.readHeaders(batchLineIterator);
      LOG.debug("Retrieved async item headers {}", partHeaders);

      this.headers.putAll(partHeaders);

      final ByteArrayOutputStream bos = new ByteArrayOutputStream();

      while (batchLineIterator.hasNext()) {
        bos.write(batchLineIterator.nextLine().getBytes(Constants.UTF8));
        bos.write(CRLF);
      }

      try {
        this.payload = new ByteArrayInputStream(bos.toByteArray());
      } catch (Exception e) {
        LOG.error("Error retrieving payload", e);
        throw new IllegalStateException(e);
      }

      this.hasBeenInitialized = true;
      return this;
    } catch (IOException e) {
      LOG.error("Error streaming payload response", e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close() {
    odataClient.getConfiguration().getHttpClientFactory().close(httpClient);

    if (batchInfo != null) {
      batchInfo.setValidBatch(false);
    }
  }

  @Override
  public InputStream getRawResponse() {


    InputStream inputStream = null;
    if (HttpStatus.SC_NO_CONTENT == getStatusCode()) {
      throw new NoContentException();
    }

    if (payload == null && batchInfo != null && batchInfo.isValidBatch()) {
      // get input stream till the end of item
      payload = new PipedInputStream(null);

      try {
        final PipedOutputStream os = new PipedOutputStream((PipedInputStream) payload,
                ConfigurationImpl.DEFAULT_BUFFER_SIZE);

        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              ODataBatchUtilities.readBatchPart(batchInfo, os, true);
            } catch (Exception e) {
              LOG.error("Error streaming batch item payload", e);
            } finally {
              IOUtils.closeQuietly(os);
            }
          }
        }).start();
      } catch (Exception e) {
        LOG.error("Error streaming payload response", e);
        throw new IllegalStateException(e);
      }
    } else if (payload != null) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try {
        org.apache.commons.io.IOUtils.copy(payload, byteArrayOutputStream);
       if(inputContent == null){
         inputContent  = byteArrayOutputStream.toByteArray();
       }
        inputStream = new ByteArrayInputStream(inputContent);
        return inputStream;
      } catch (IOException e) {
        HttpClientUtils.closeQuietly(res);
        LOG.error("Error retrieving payload", e);
        throw new ODataRuntimeException(e);
      }
    }
    return payload;
  }
}
