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

import com.msopentech.odatajclient.engine.client.ODataClient;

/**
 * OData batch request factory class.
 */
public abstract class AbstractBatchRequestFactory implements BatchRequestFactory {

    private static final long serialVersionUID = -3875283254713404483L;

    protected final ODataClient client;

    protected AbstractBatchRequestFactory(final ODataClient client) {
        this.client = client;
    }

    @Override
    public ODataBatchRequest getBatchRequest(final String serviceRoot) {
        return new ODataBatchRequest(client, client.getURIBuilder(serviceRoot).appendBatchSegment().build());
    }
}
