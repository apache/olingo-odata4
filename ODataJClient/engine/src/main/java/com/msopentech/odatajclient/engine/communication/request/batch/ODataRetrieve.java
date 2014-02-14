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
package com.msopentech.odatajclient.engine.communication.request.batch;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.ODataRequestImpl;

/**
 * Retrieve request wrapper for the corresponding batch item.
 */
public class ODataRetrieve extends ODataBatchRequestItem {

    private final ODataRetrieveResponseItem expectedResItem;

    /**
     * Constructor.
     *
     * @param req batch request.
     * @param expectedResItem expected batch response item.
     */
    ODataRetrieve(final ODataBatchRequest req, final ODataRetrieveResponseItem expectedResItem) {
        super(req);
        this.expectedResItem = expectedResItem;
    }

    /**
     * Close item.
     */
    @Override
    protected void closeItem() {
        // nop
    }

    /**
     * Serialize and send the given request.
     * <p>
     * An IllegalArgumentException is thrown in case of no GET request.
     *
     * @param request request to be serialized.
     * @return current item instance.
     */
    public ODataRetrieve setRequest(final ODataBatchableRequest request) {
        if (!isOpen()) {
            throw new IllegalStateException("Current batch item is closed");
        }

        if (((ODataRequestImpl) request).getMethod() != HttpMethod.GET) {
            throw new IllegalArgumentException("Invalid request. Only GET method is allowed");
        }

        hasStreamedSomething = true;

        // stream the request
        streamRequestHeader(request);

        // close before in order to avoid any further setRequest calls.
        close();

        // add request to the list
        expectedResItem.addResponse(
                ODataRetrieveResponseItem.RETRIEVE_CONTENT_ID, ((ODataRequestImpl) request).getResponseTemplate());

        return this;
    }
}
