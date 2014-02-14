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
package com.msopentech.odatajclient.engine.communication.request.cud;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.AbstractODataBasicRequestImpl;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchableRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

/**
 * This class implements an OData create request.
 */
public class ODataEntityCreateRequest extends AbstractODataBasicRequestImpl<ODataEntityCreateResponse, ODataPubFormat>
        implements ODataBatchableRequest {

    /**
     * Entity to be created.
     */
    private final ODataEntity entity;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param targetURI entity set URI.
     * @param entity entity to be created.
     */
    ODataEntityCreateRequest(final ODataClient odataClient, final URI targetURI, final ODataEntity entity) {
        super(odataClient, ODataPubFormat.class, HttpMethod.POST, targetURI);
        this.entity = entity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected InputStream getPayload() {
        return odataClient.getWriter().writeEntity(entity, ODataPubFormat.fromString(getContentType()));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataEntityCreateResponse execute() {
        final InputStream input = getPayload();
        ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        try {
            return new ODataEntityCreateResponseImpl(httpClient, doExecute());
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * Response class about an ODataEntityCreateRequest.
     */
    private class ODataEntityCreateResponseImpl extends ODataResponseImpl implements ODataEntityCreateResponse {

        private ODataEntity entity = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataEntityCreateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataEntityCreateResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ODataEntity getBody() {
            if (entity == null) {
                try {
                    entity = odataClient.getReader().
                            readEntity(getRawResponse(), ODataPubFormat.fromString(getAccept()));
                } finally {
                    this.close();
                }
            }
            return entity;
        }
    }
}
