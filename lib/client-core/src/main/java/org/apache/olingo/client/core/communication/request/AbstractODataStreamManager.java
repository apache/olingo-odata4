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
package org.apache.olingo.client.core.communication.request;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.olingo.client.api.communication.request.ODataPayloadManager;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.core.ConfigurationImpl;
import org.apache.olingo.client.core.communication.util.PipedInputStream;
import org.apache.olingo.client.core.communication.util.PipedOutputStream;

/**
 * OData request payload management abstract class.
 *
 * @param <T> OData response type corresponding to the request implementation.
 */
public abstract class AbstractODataStreamManager<T extends ODataResponse> extends AbstractODataStreamer
        implements ODataPayloadManager<T> {

  /**
   * Body input stream.
   */
  private final PipedInputStream body;

  /**
   * Default body input stream.
   */
  private final InputStream defaultBody;

  /**
   * Wrapper for actual streamed request's future.
   */
  private final Wrapper<Future<HttpResponse>> futureWrap;

  /**
   * Constructor.
   *
   * @param futureWrap wrapper of the Future object of the HttpResponse.
   */
  public AbstractODataStreamManager(final Wrapper<Future<HttpResponse>> futureWrap) {
    this(futureWrap, new PipedOutputStream(null, ConfigurationImpl.DEFAULT_BUFFER_SIZE));
  }

  /**
   * Constructor.
   *
   * @param futureWrap wrapper of the Future object of the HttpResponse.
   * @param output stream to be piped to retrieve the payload.
   */
  public AbstractODataStreamManager(final Wrapper<Future<HttpResponse>> futureWrap, final PipedOutputStream output) {
    super(output);

    this.futureWrap = futureWrap;
    try {
      this.body = new PipedInputStream(getBodyStreamWriter(), ConfigurationImpl.DEFAULT_BUFFER_SIZE);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    this.defaultBody = this.body;
  }

  /**
   * Constructor.
   *
   * @param futureWrap wrapper of the Future object of the HttpResponse.
   * @param input stream to be used to retrieve the content.
   */
  public AbstractODataStreamManager(final Wrapper<Future<HttpResponse>> futureWrap, final InputStream input) {
    super(null);

    this.futureWrap = futureWrap;
    this.body = null;
    this.defaultBody = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getBody() {
    return this.body == null ? this.defaultBody : this.body;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void finalizeBody() {
    IOUtils.closeQuietly(getBodyStreamWriter());
  }

  /**
   * Gets HttpResponse.
   *
   * @param timeout maximum delay after which the request must be aborted.
   * @param unit time unit.
   * @return HttpResponse.
   */
  protected HttpResponse getHttpResponse(final long timeout, final TimeUnit unit) {
    try {
      return futureWrap.getWrapped().get(timeout, unit);
    } catch (Exception e) {
      LOG.error("Failure executing request");
      throw new HttpClientException(e);
    }
  }

  /**
   * Gets OData response.
   *
   * @param timeout maximum delay after which the request must be aborted.
   * @param unit time unit.
   * @return ODataResponse instance.
   */
  protected abstract T getResponse(long timeout, TimeUnit unit);

  /**
   * {@inheritDoc}
   */
  @Override
  public final T getResponse() {
    return getResponse(30, TimeUnit.SECONDS);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Future<T> getAsyncResponse() {
    return new Future<T>() {
      @Override
      public boolean cancel(final boolean mayInterruptIfRunning) {
        return futureWrap.getWrapped().cancel(mayInterruptIfRunning);
      }

      @Override
      public boolean isCancelled() {
        return futureWrap.getWrapped().isCancelled();
      }

      @Override
      public boolean isDone() {
        return futureWrap.getWrapped().isDone();
      }

      @Override
      public T get() throws InterruptedException, ExecutionException {
        return getResponse(0, TimeUnit.SECONDS);
      }

      @Override
      public T get(final long timeout, final TimeUnit unit)
              throws InterruptedException, ExecutionException, TimeoutException {

        return getResponse(timeout, unit);
      }
    };
  }
}
