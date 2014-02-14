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
package com.msopentech.odatajclient.engine.communication.request.batch;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.ODataStreamManager;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchRequest.BatchStreamManager;
import com.msopentech.odatajclient.engine.communication.request.streamed.AbstractODataStreamedRequestImpl;
import com.msopentech.odatajclient.engine.communication.response.ODataBatchResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements a batch request.
 */
public class ODataBatchRequest extends AbstractODataStreamedRequestImpl<ODataBatchResponse, BatchStreamManager> {

    /**
     * Batch request boundary.
     */
    private final String boundary;

    /**
     * Expected batch response items.
     */
    private final List<ODataBatchResponseItem> expectedResItems = new ArrayList<ODataBatchResponseItem>();

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param uri batch request URI (http://serviceRoot/$batch)
     */
    ODataBatchRequest(final ODataClient odataClient, final URI uri) {
        super(odataClient, HttpMethod.POST, uri);

        // create a random UUID value for boundary
        boundary = "batch_" + UUID.randomUUID().toString();

        // specify the contentType header
        setContentType(ODataBatchConstants.MULTIPART_CONTENT_TYPE + ";" + ODataBatchConstants.BOUNDARY + "=" + boundary);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected BatchStreamManager getStreamManager() {
        if (streamManager == null) {
            streamManager = new BatchStreamManager(this);
        }
        return (BatchStreamManager) streamManager;
    }

    /**
     * Gets piped stream to be used to stream batch items.
     *
     * @return piped stream for the payload.
     */
    PipedOutputStream getOutputStream() {
        return getStreamManager().getBodyStreamWriter();
    }

    /**
     * Appends the given byte array to the payload.
     *
     * @param toBeStreamed byte array to be appended.
     * @return the current batch request.
     * @throws IOException in case of write errors.
     */
    public ODataBatchRequest rawAppend(final byte[] toBeStreamed) throws IOException {
        getStreamManager().getBodyStreamWriter().write(toBeStreamed);
        return this;
    }

    /**
     * Appends the given byte array to the payload.
     *
     * @param toBeStreamed byte array to be appended.
     * @param off byte array offset.
     * @param len number of byte to be streamed.
     * @return the current batch request.
     * @throws IOException in case of write errors.
     */
    public ODataBatchRequest rawAppend(final byte[] toBeStreamed, int off, int len) throws IOException {
        getStreamManager().getBodyStreamWriter().write(toBeStreamed, off, len);
        return this;
    }

    /**
     * Batch request payload management.
     */
    public class BatchStreamManager extends ODataStreamManager<ODataBatchResponse> {

        /**
         * Batch request current item.
         */
        private ODataBatchRequestItem currentItem = null;

        /**
         * batch request reference.
         */
        private final ODataBatchRequest req;

        /**
         * Private constructor.
         *
         * @param req batch request reference.
         */
        private BatchStreamManager(final ODataBatchRequest req) {
            super(ODataBatchRequest.this.futureWrapper);
            this.req = req;
        }

        /**
         * Gets a changeset batch item instance.
         * A changeset can be submitted embedded into a batch request only.
         *
         * @return ODataChangeset instance.
         */
        public ODataChangeset addChangeset() {
            closeCurrentItem();

            // stream dash boundary
            streamDashBoundary();

            final ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem();
            expectedResItems.add(expectedResItem);

            currentItem = new ODataChangeset(req, expectedResItem);

            return (ODataChangeset) currentItem;
        }

        /**
         * Gets a retrieve batch item instance.
         * A retrieve item can be submitted embedded into a batch request only.
         *
         * @return ODataRetrieve instance.
         */
        public ODataRetrieve addRetrieve() {
            closeCurrentItem();

            // stream dash boundary
            streamDashBoundary();

            final ODataRetrieveResponseItem expectedResItem = new ODataRetrieveResponseItem();
            currentItem = new ODataRetrieve(req, expectedResItem);

            expectedResItems.add(expectedResItem);

            return (ODataRetrieve) currentItem;
        }

        /**
         * Close the current streamed item.
         */
        private void closeCurrentItem() {
            if (currentItem != null) {
                currentItem.close();
            }
        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected ODataBatchResponse getResponse(final long timeout, final TimeUnit unit) {
            closeCurrentItem();
            streamCloseDelimiter();
            finalizeBody();
            return new ODataBatchResponseImpl(httpClient, getHttpResponse(timeout, unit));
        }

        /**
         * Streams dash boundary.
         */
        private void streamDashBoundary() {
            // preamble
            newLine();

            // stream batch-boundary
            stream(("--" + boundary).getBytes());
            newLine();
        }

        /**
         * Streams close delimiter.
         */
        private void streamCloseDelimiter() {
            // stream close-delimiter
            newLine();
            stream(("--" + boundary + "--").getBytes());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This operation is unsupported by a batch request.
     */
    @Override
    public void batch(ODataBatchRequest req) {
        throw new UnsupportedOperationException("A batch request is not batchable");
    }

    /**
     * This class implements a response to a batch request.
     *
     * @see com.msopentech.odatajclient.engine.communication.request.ODataBatchRequest
     */
    private class ODataBatchResponseImpl extends ODataResponseImpl implements ODataBatchResponse {

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataBatchResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<ODataBatchResponseItem> getBody() {
            return new ODataBatchResponseManager(this, expectedResItems);
        }
    }
}
