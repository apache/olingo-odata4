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

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Basic request abstract implementation.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> OData format being used.
 */
public abstract class AbstractODataBasicRequestImpl<V extends ODataResponse, T extends Enum<T>>
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
    public AbstractODataBasicRequestImpl(final ODataClient odataClient,
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
