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
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.response.ODataResponseImpl;
import com.msopentech.odatajclient.engine.communication.request.AbstractODataBasicRequestImpl;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchableRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import java.io.InputStream;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

/**
 * This is an abstract representation of an OData retrieve query request returning one or more result item.
 * Get instance by using ODataRetrieveRequestFactory.
 */
abstract class AbstractODataRetrieveRequest<V, T extends Enum<T>>
        extends AbstractODataBasicRequestImpl<ODataRetrieveResponse<V>, T>
        implements ODataBatchableRequest {

    /**
     * Private constructor.
     *
     * @param odataClient client instance getting this request
     * @param formatRef reference class for the format being used
     * @param query query to be executed.
     */
    AbstractODataRetrieveRequest(final ODataClient odataClient, final Class<T> formatRef, final URI query) {
        super(odataClient, formatRef, HttpMethod.GET, query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract ODataRetrieveResponse<V> execute();

    /**
     * {@inheritDoc }
     * <p>
     * This kind of request doesn't have any payload: null will be returned.
     */
    @Override
    protected InputStream getPayload() {
        return null;
    }

    /**
     * Response abstract class about an ODataRetrieveRequest.
     */
    protected abstract class ODataRetrieveResponseImpl extends ODataResponseImpl implements ODataRetrieveResponse<V> {

        /**
         * Constructor.
         * <p>
         * Just to create response templates to be initialized from batch.
         */
        protected ODataRetrieveResponseImpl() {
            super();
        }

        /**
         * Constructor.
         *
         * @param client HTTP client.
         * @param res HTTP response.
         */
        protected ODataRetrieveResponseImpl(final HttpClient client, final HttpResponse res) {
            super(client, res);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public abstract V getBody();
    }
}
