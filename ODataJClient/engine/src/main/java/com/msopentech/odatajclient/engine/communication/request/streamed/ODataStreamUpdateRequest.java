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
package com.msopentech.odatajclient.engine.communication.request.streamed;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.ODataStreamManager;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchableRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataStreamUpdateRequest.StreamUpdateStreamManager;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.communication.response.ODataStreamUpdateResponse;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements an OData stream create/update request.
 * Get instance by using ODataStreamedRequestFactory.
 */
public class ODataStreamUpdateRequest
        extends AbstractODataStreamedRequestImpl<ODataStreamUpdateResponse, StreamUpdateStreamManager>
        implements ODataBatchableRequest {

    private final InputStream stream;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param method request method.
     * @param targetURI target URI.
     * @param stream stream to be updated.
     */
    ODataStreamUpdateRequest(final ODataClient odataClient,
            final HttpMethod method, final URI targetURI, final InputStream stream) {

        super(odataClient, method, targetURI);
        this.stream = stream;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected StreamUpdateStreamManager getStreamManager() {
        if (streamManager == null) {
            streamManager = new StreamUpdateStreamManager(this.stream);
        }

        return (StreamUpdateStreamManager) streamManager;
    }

    public class StreamUpdateStreamManager extends ODataStreamManager<ODataStreamUpdateResponse> {

        /**
         * Private constructor.
         *
         * @param input payload input stream.
         */
        private StreamUpdateStreamManager(final InputStream input) {
            super(ODataStreamUpdateRequest.this.futureWrapper, input);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected ODataStreamUpdateResponse getResponse(final long timeout, final TimeUnit unit) {
            finalizeBody();
            return new ODataStreamUpdateResponseImpl(httpClient, getHttpResponse(timeout, unit));
        }
    }

    /**
     * Response class about an ODataStreamUpdateRequest.
     */
    private class ODataStreamUpdateResponseImpl extends ODataResponseImpl implements ODataStreamUpdateResponse {

        private InputStream input = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataStreamUpdateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataStreamUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * Gets query result objects.
         * <br/>
         * <b>WARNING</b>: Closing this <tt>ODataResponse</tt> instance is left to the caller.
         *
         * @return query result objects as <tt>InputStream</tt>.
         */
        @Override
        public InputStream getBody() {
            if (input == null) {
                input = getRawResponse();
            }
            return input;
        }
    }
}
