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
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * This class implements an OData update request.
 */
public class ODataEntityUpdateRequest extends AbstractODataBasicRequestImpl<ODataEntityUpdateResponse, ODataPubFormat>
        implements ODataBatchableRequest {

    /**
     * Changes to be applied.
     */
    private final ODataEntity changes;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param method request method.
     * @param uri URI of the entity to be updated.
     * @param changes changes to be applied.
     */
    ODataEntityUpdateRequest(final ODataClient odataClient,
            final HttpMethod method, final URI uri, final ODataEntity changes) {

        super(odataClient, ODataPubFormat.class, method, uri);
        this.changes = changes;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataEntityUpdateResponse execute() {
        final InputStream input = getPayload();
        ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        try {
            return new ODataEntityUpdateResponseImpl(httpClient, doExecute());
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected InputStream getPayload() {
        return odataClient.getWriter().writeEntity(changes, ODataPubFormat.fromString(getContentType()));
    }

    /**
     * Response class about an ODataEntityUpdateRequest.
     */
    private class ODataEntityUpdateResponseImpl extends ODataResponseImpl implements ODataEntityUpdateResponse {

        /**
         * Changes.
         */
        private ODataEntity entity = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataEntityUpdateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataEntityUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc ]
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
