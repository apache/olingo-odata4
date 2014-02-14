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
package com.msopentech.odatajclient.engine.communication.request.retrieve;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataServiceDocument;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements an OData service document request.
 */
public class ODataServiceDocumentRequest extends AbstractODataRetrieveRequest<ODataServiceDocument, ODataFormat> {

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param uri request URI.
     */
    ODataServiceDocumentRequest(final ODataClient odataClient, final URI uri) {
        super(odataClient, ODataFormat.class, uri);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataRetrieveResponse<ODataServiceDocument> execute() {
        final HttpResponse res = doExecute();
        return new ODataServiceResponseImpl(httpClient, res);
    }

    /**
     * Response class about an ODataServiceDocumentRequest.
     */
    protected class ODataServiceResponseImpl extends ODataRetrieveResponseImpl {

        private ODataServiceDocument serviceDocument = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataServiceResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataServiceResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        @Override
        public ODataServiceDocument getBody() {
            if (serviceDocument == null) {
                try {
                    serviceDocument = odataClient.getReader().readServiceDocument(
                            getRawResponse(), ODataFormat.fromString(getContentType()));
                } finally {
                    this.close();
                }
            }
            return serviceDocument;
        }
    }
}
