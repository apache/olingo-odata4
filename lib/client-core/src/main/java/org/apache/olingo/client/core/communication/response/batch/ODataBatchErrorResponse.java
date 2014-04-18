/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.response.batch;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.NoContentException;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchController;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchUtilities;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of an OData response.
 */
public class ODataBatchErrorResponse implements ODataResponse {

  /**
   * Logger.
   */
  protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ODataResponse.class);

  /**
   * HTTP client.
   */
  protected final HttpClient client;

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
  private int statusCode = -1;

  /**
   * Response message.
   */
  private String statusMessage = null;

  /**
   * Response body/payload.
   */
  private InputStream payload = null;

  /**
   * Initialization check.
   */
  private boolean hasBeenInitialized = false;

  /**
   * Batch info (if to be batched).
   */
  private ODataBatchController batchInfo = null;

  /**
   * Constructor.
   */
  public ODataBatchErrorResponse(
          final Map.Entry<Integer, String> responseLine,
          final Map<String, Collection<String>> headers,
          final ODataBatchLineIterator batchLineIterator,
          final String boundary) {

    client = null;
    res = null;

    if (hasBeenInitialized) {
      throw new IllegalStateException("Request already initialized");
    }

    this.hasBeenInitialized = true;

    this.batchInfo = new ODataBatchController(batchLineIterator, boundary);

    this.statusCode = responseLine.getKey();
    this.statusMessage = responseLine.getValue();
    this.headers.putAll(headers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<String> getHeaderNames() {
    return headers.keySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<String> getHeader(final String name) {
    return headers.get(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<String> getHeader(final HeaderName name) {
    return headers.get(name.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContentType() {
    final Collection<String> contentTypes = getHeader(HeaderName.contentType);
    return contentTypes == null || contentTypes.isEmpty()
            ? null
            : contentTypes.iterator().next();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStatusMessage() {
    return statusMessage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ODataResponse initFromBatch(
          final Map.Entry<Integer, String> responseLine,
          final Map<String, Collection<String>> headers,
          final ODataBatchLineIterator batchLineIterator,
          final String boundary) {

    if (hasBeenInitialized) {
      throw new IllegalStateException("Request already initialized");
    }

    this.hasBeenInitialized = true;

    this.batchInfo = new ODataBatchController(batchLineIterator, boundary);

    this.statusCode = responseLine.getKey();
    this.statusMessage = responseLine.getValue();
    this.headers.putAll(headers);

    return this;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void close() {
    if (client == null) {
      IOUtils.closeQuietly(payload);
    } else {
      this.client.getConnectionManager().shutdown();
    }

    if (batchInfo != null) {
      batchInfo.setValidBatch(false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getRawResponse() {
    if (HttpStatus.SC_NO_CONTENT == getStatusCode()) {
      throw new NoContentException();
    }

    if (payload == null && batchInfo.isValidBatch()) {
      // get input stream till the end of item
      payload = new PipedInputStream();

      try {
        final PipedOutputStream os = new PipedOutputStream((PipedInputStream) payload);

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

      } catch (IOException e) {
        LOG.error("Error streaming payload response", e);
        throw new IllegalStateException(e);
      }
    }

    return payload;
  }

  @Override
  public String getEtag() {
    return null;
  }

  @Override
  public URI getContextURL() {
    return null;
  }

  @Override
  public String getMetadataETag() {
    return null;
  }
}
