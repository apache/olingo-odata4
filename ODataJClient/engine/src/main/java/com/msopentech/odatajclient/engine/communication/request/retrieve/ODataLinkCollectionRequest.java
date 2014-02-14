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
import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataLinkCollection;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements an OData link query request.
 */
public class ODataLinkCollectionRequest extends AbstractODataRetrieveRequest<ODataLinkCollection, ODataFormat> {

    /**
     * Private constructor.
     *
     * @param odataClient client instance getting this request
     * @param targetURI target URI.
     * @param linkName link name.
     */
    ODataLinkCollectionRequest(final ODataClient odataClient, final URI targetURI, final String linkName) {
        super(odataClient, ODataFormat.class,
                odataClient.getURIBuilder(targetURI.toASCIIString()).appendLinksSegment(linkName).build());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataRetrieveResponse<ODataLinkCollection> execute() {
        return new ODataLinkCollectionResponseImpl(httpClient, doExecute());
    }

    protected class ODataLinkCollectionResponseImpl extends ODataRetrieveResponseImpl {

        private ODataLinkCollection links = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataLinkCollectionResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataLinkCollectionResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ODataLinkCollection getBody() {
            if (links == null) {
                try {
                    links = odataClient.getReader().readLinks(
                            res.getEntity().getContent(), ODataFormat.fromString(getContentType()));
                } catch (IOException e) {
                    throw new HttpClientException(e);
                } finally {
                    this.close();
                }
            }
            return links;
        }
    }
}
