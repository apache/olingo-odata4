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
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityUpdateRequest.MediaEntityUpdateStreamManager;
import com.msopentech.odatajclient.engine.communication.response.ODataMediaEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements an OData Media Entity create request.
 * Get instance by using ODataStreamedRequestFactory.
 */
public class ODataMediaEntityUpdateRequest
        extends AbstractODataStreamedEntityRequestImpl<ODataMediaEntityUpdateResponse, MediaEntityUpdateStreamManager>
        implements ODataBatchableRequest {

    private final InputStream media;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param method request method.
     * @param editURI edit URI of the entity to be updated.
     * @param media media entity blob to be created.
     */
    ODataMediaEntityUpdateRequest(final ODataClient odataClient,
            final HttpMethod method, final URI editURI, final InputStream media) {

        super(odataClient, method, editURI);
        this.media = media;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected MediaEntityUpdateStreamManager getStreamManager() {
        if (streamManager == null) {
            streamManager = new MediaEntityUpdateStreamManager(media);
        }
        return (MediaEntityUpdateStreamManager) streamManager;
    }

    /**
     * Media entity payload object.
     */
    public class MediaEntityUpdateStreamManager extends ODataStreamManager<ODataMediaEntityUpdateResponse> {

        /**
         * Private constructor.
         *
         * @param input media stream.
         */
        private MediaEntityUpdateStreamManager(final InputStream input) {
            super(ODataMediaEntityUpdateRequest.this.futureWrapper, input);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected ODataMediaEntityUpdateResponse getResponse(final long timeout, final TimeUnit unit) {
            finalizeBody();
            return new ODataMediaEntityUpdateResponseImpl(httpClient, getHttpResponse(timeout, unit));
        }
    }

    /**
     * Response class about an ODataMediaEntityUpdateRequest.
     */
    private class ODataMediaEntityUpdateResponseImpl extends ODataResponseImpl
            implements ODataMediaEntityUpdateResponse {

        private ODataEntity entity = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataMediaEntityUpdateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataMediaEntityUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ODataEntity getBody() {
            if (entity == null) {
                try {
                    entity = odataClient.getReader().readEntity(getRawResponse(), getFormat());
                } finally {
                    this.close();
                }
            }
            return entity;
        }
    }
}
