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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBasicRequest;
import org.apache.olingo.client.api.communication.request.ODataStreamer;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.HttpMethod;

/**
 * Basic request abstract implementation.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> OData format being used.
 */
public abstract class AbstractODataBasicRequest<V extends ODataResponse, T extends Enum<T>>
        extends ODataRequestImpl<T>
        implements ODataBasicRequest<V, T> {

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param formatRef reference class for the format being used
     * @param method request method.
     * @param uri OData request URI.
     */
    public AbstractODataBasicRequest(final CommonODataClient odataClient,
            final Class<T> formatRef, final HttpMethod method, final URI uri) {

        super(odataClient, formatRef, method, uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormat(final T format) {
        if (format != null) {
            setAccept(format.toString());
            setContentType(format.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Future<V> asyncExecute() {
        return odataClient.getConfiguration().getExecutor().submit(new Callable<V>() {

            @Override
            public V call() throws Exception {
                return execute();
            }
        });
    }

    /**
     * Gets payload as an InputStream.
     *
     * @return InputStream for entire payload.
     */
    protected abstract InputStream getPayload();

    /**
     * Serializes the full request into the given batch request.
     *
     * @param req destination batch request.
     */
    public void batch(final ODataBatchRequest req) {
        batch(req, null);
    }

    /**
     * Serializes the full request into the given batch request.
     * <p>
     * This method have to be used to serialize a changeset item with the specified contentId.
     *
     * @param req destination batch request.
     * @param contentId contentId of the changeset item.
     */
    public void batch(final ODataBatchRequest req, final String contentId) {
        try {
            req.rawAppend(toByteArray());
            if (StringUtils.isNotBlank(contentId)) {
                req.rawAppend((ODataBatchConstants.CHANGESET_CONTENT_ID_NAME + ": " + contentId).getBytes());
                req.rawAppend(ODataStreamer.CRLF);
            }
            req.rawAppend(ODataStreamer.CRLF);

            final InputStream payload = getPayload();
            if (payload != null) {
                req.rawAppend(IOUtils.toByteArray(getPayload()));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
