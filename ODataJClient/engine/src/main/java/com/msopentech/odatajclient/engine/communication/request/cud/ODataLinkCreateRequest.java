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
import com.msopentech.odatajclient.engine.communication.response.ODataLinkOperationResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

/**
 * This class implements an insert link OData request.
 */
public class ODataLinkCreateRequest extends AbstractODataBasicRequestImpl<ODataLinkOperationResponse, ODataFormat>
        implements ODataBatchableRequest {

    /**
     * OData entity to be linked.
     */
    private final ODataLink link;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param targetURI entity set URI.
     * @param link entity to be linked.
     */
    ODataLinkCreateRequest(final ODataClient odataClient, final URI targetURI, final ODataLink link) {
        super(odataClient, ODataFormat.class, HttpMethod.POST, targetURI);
        // set request body
        this.link = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ODataLinkOperationResponse execute() {
        final InputStream input = getPayload();
        ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        try {
            return new ODataLinkCreateResponseImpl(httpClient, doExecute());
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream getPayload() {
        return odataClient.getWriter().writeLink(link, ODataFormat.fromString(getContentType()));
    }

    /**
     * This class implements the response to an OData link operation request.
     */
    private class ODataLinkCreateResponseImpl extends ODataResponseImpl implements ODataLinkOperationResponse {

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataLinkCreateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataLinkCreateResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }
    }
}
