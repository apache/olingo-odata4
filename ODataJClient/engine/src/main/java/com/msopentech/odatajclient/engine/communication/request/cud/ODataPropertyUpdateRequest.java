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
import com.msopentech.odatajclient.engine.communication.response.ODataPropertyUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.InputStreamEntity;

/**
 * This class implements an OData update entity property request.
 */
public class ODataPropertyUpdateRequest extends AbstractODataBasicRequestImpl<ODataPropertyUpdateResponse, ODataFormat>
        implements ODataBatchableRequest {

    /**
     * Value to be created.
     */
    private final ODataProperty property;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param method request method.
     * @param targetURI entity set or entity or entity property URI.
     * @param property value to be created.
     */
    ODataPropertyUpdateRequest(final ODataClient odataClient,
            final HttpMethod method, final URI targetURI, final ODataProperty property) {

        super(odataClient, ODataFormat.class, method, targetURI);
        // set request body
        this.property = property;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataPropertyUpdateResponse execute() {
        final InputStream input = getPayload();
        ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

        try {
            return new ODataPropertyUpdateResponseImpl(httpClient, doExecute());
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected InputStream getPayload() {
        return odataClient.getWriter().writeProperty(property, ODataFormat.fromString(getContentType()));
    }

    /**
     * Response class about an ODataPropertyUpdateRequest.
     */
    private class ODataPropertyUpdateResponseImpl extends ODataResponseImpl implements ODataPropertyUpdateResponse {

        private ODataProperty property = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataPropertyUpdateResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataPropertyUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ODataProperty getBody() {
            if (property == null) {
                try {
                    property = odataClient.getReader().
                            readProperty(getRawResponse(), ODataFormat.fromString(getAccept()));
                } finally {
                    this.close();
                }
            }
            return property;
        }
    }
}
