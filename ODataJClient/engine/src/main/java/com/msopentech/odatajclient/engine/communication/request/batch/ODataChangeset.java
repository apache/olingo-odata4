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
import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.ODataRequestImpl;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;
import java.util.UUID;

/**
 * Changeset wrapper for the corresponding batch item.
 */
public class ODataChangeset extends ODataBatchRequestItem {

    /**
     * ContentId.
     */
    private int contentId = 0;

    /**
     * Changeset boundary.
     */
    private final String boundary;

    /**
     * Expected changeset response items.
     */
    private final ODataChangesetResponseItem expectedResItem;

    /**
     * Constructor.
     *
     * @param req batch request.
     * @param expectedResItem expected OData response items.
     */
    ODataChangeset(final ODataBatchRequest req, final ODataChangesetResponseItem expectedResItem) {
        super(req);
        this.expectedResItem = expectedResItem;

        // create a random UUID value for boundary
        boundary = "changeset_" + UUID.randomUUID().toString();
    }

    public int getLastContentId() {
        return contentId;
    }

    /**
     * Close changeset item an send changeset request footer.
     */
    @Override
    protected void closeItem() {
        // stream close-delimiter
        if (hasStreamedSomething) {
            newLine();
            stream(("--" + boundary + "--").getBytes());
            newLine();
            newLine();
        }
    }

    /**
     * Serialize and send the given request.
     * <p>
     * An IllegalArgumentException is thrown in case of GET request.
     *
     * @param request request to be serialized.
     * @return current item instance.
     */
    public ODataChangeset addRequest(final ODataBatchableRequest request) {
        if (!isOpen()) {
            throw new IllegalStateException("Current batch item is closed");
        }

        if (request.getMethod() == HttpMethod.GET) {
            throw new IllegalArgumentException("Invalid request. GET method not allowed in changeset");
        }

        if (!hasStreamedSomething) {
            stream((ODataHeaders.HeaderName.contentType.toString() + ": "
                    + ODataBatchConstants.MULTIPART_CONTENT_TYPE + ";boundary=" + boundary).getBytes());

            newLine();
            newLine();

            hasStreamedSomething = true;
        }

        contentId++;

        // preamble
        newLine();

        // stream batch-boundary
        stream(("--" + boundary).getBytes());
        newLine();

        // stream the request
        streamRequestHeader(request, contentId);

        request.batch(req, String.valueOf(contentId));

        // add request to the list
        expectedResItem.addResponse(String.valueOf(contentId), ((ODataRequestImpl) request).getResponseTemplate());
        return this;
    }
}
