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
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityCreateRequest.MediaEntityCreateStreamManager;
import com.msopentech.odatajclient.engine.communication.response.ODataMediaEntityCreateResponse;
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
public class ODataMediaEntityCreateRequest
        extends AbstractODataStreamedEntityRequestImpl<ODataMediaEntityCreateResponse, MediaEntityCreateStreamManager>
        implements ODataBatchableRequest {

    private final InputStream media;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param targetURI target entity set.
     * @param media media entity blob to be created.
     */
    ODataMediaEntityCreateRequest(final ODataClient odataClient, final URI targetURI, final InputStream media) {
        super(odataClient, HttpMethod.POST, targetURI);
        this.media = media;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected MediaEntityCreateStreamManager getStreamManager() {
        if (streamManager == null) {
            streamManager = new MediaEntityCreateStreamManager(media);
        }
        return (MediaEntityCreateStreamManager) streamManager;
    }

    /**
     * Media entity payload object.
     */
    public class MediaEntityCreateStreamManager extends ODataStreamManager<ODataMediaEntityCreateResponse> {

        /**
         * Private constructor.
         *
         * @param input media stream.
         */
        private MediaEntityCreateStreamManager(final InputStream input) {
            super(ODataMediaEntityCreateRequest.this.futureWrapper, input);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        protected ODataMediaEntityCreateResponse getResponse(final long timeout, final TimeUnit unit) {
            finalizeBody();
            return new ODataMediaEntityCreateResponseImpl(httpClient, getHttpResponse(timeout, unit));
        }
    }

    /**
     * Response class about an ODataMediaEntityCreateRequest.
     */
    private class ODataMediaEntityCreateResponseImpl extends ODataResponseImpl
            implements ODataMediaEntityCreateResponse {

        private ODataEntity entity = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataMediaEntityCreateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataMediaEntityCreateResponseImpl(final HttpClient client, final HttpResponse res) {
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
