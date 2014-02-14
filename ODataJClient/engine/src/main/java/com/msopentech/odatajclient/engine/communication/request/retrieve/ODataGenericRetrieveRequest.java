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
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataObjectWrapper;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This class implements a generic OData retrieve query request.
 */
public class ODataGenericRetrieveRequest extends ODataRawRequest {

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param uri query request.
     */
    public ODataGenericRetrieveRequest(final ODataClient odataClient, final URI uri) {
        super(odataClient, uri);
    }

    /**
     * Sets accepted format.
     *
     * @param format format.
     */
    public void setFormat(final String format) {
        setAccept(format);
        setContentType(format);
    }

    /**
     * Executes the query.
     *
     * @return query response.
     */
    public ODataRetrieveResponse<ODataObjectWrapper> execute() {
        return new ODataGenericResponseImpl(httpClient, doExecute());
    }

    /**
     * Query response implementation about a generic query request.
     */
    protected class ODataGenericResponseImpl extends ODataResponseImpl
            implements ODataRetrieveResponse<ODataObjectWrapper> {

        /**
         * Retrieved object wrapper.
         */
        private ODataObjectWrapper obj = null;

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        private ODataGenericResponseImpl() {
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        private ODataGenericResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public ODataObjectWrapper getBody() {
            if (obj == null) {
                try {
                    obj = new ODataObjectWrapper(odataClient.getReader(), getRawResponse(), getContentType());
                } finally {
                    this.close();
                }
            }
            return obj;
        }
    }
}
