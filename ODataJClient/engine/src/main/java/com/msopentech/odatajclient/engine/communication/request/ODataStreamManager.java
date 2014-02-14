/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.communication.request;

import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.utils.Wrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

/**
 * OData request payload management abstract class.
 *
 * @param <T> OData response type corresponding to the request implementation.
 */
public abstract class ODataStreamManager<T extends ODataResponse> extends ODataStreamer {

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
    public ODataStreamManager(final Wrapper<Future<HttpResponse>> futureWrap) {
        this(futureWrap, new PipedOutputStream());
    }

    /**
     * Constructor.
     *
     * @param futureWrap wrapper of the Future object of the HttpResponse.
     * @param output stream to be piped to retrieve the payload.
     */
    public ODataStreamManager(final Wrapper<Future<HttpResponse>> futureWrap, final PipedOutputStream output) {
        super(output);

        this.futureWrap = futureWrap;
        try {
            this.body = new PipedInputStream(getBodyStreamWriter());
        } catch (IOException e) {
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
    public ODataStreamManager(final Wrapper<Future<HttpResponse>> futureWrap, final InputStream input) {
        super(null);

        this.futureWrap = futureWrap;
        this.body = null;
        this.defaultBody = input;
    }

    /**
     * Gets payload stream.
     *
     * @return payload stream.
     */
    public InputStream getBody() {
        return this.body == null ? this.defaultBody : this.body;
    }

    /**
     * Closes piped output stream.
     */
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
     * Closes the payload input stream and gets the OData response back.
     *
     * @return OData response.
     */
    public final T getResponse() {
        return getResponse(30, TimeUnit.SECONDS);
    }

    /**
     * Closes the payload input stream and ask for an asynchronous response.
     *
     * @return <code>Future&lt;ODataResponse&gt;</code> about the executed request.
     */
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
