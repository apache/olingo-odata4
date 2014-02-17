/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.msopentech.odatajclient.engine.communication.request.batch;

import static com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchResponseItem.LOG;

import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Changeset wrapper for the corresponding batch item.
 */
public class ODataChangesetResponseItem extends ODataBatchResponseItem {

    /**
     * Last cached OData response.
     */
    private ODataResponse current = null;

    /**
     * Constructor.
     */
    public ODataChangesetResponseItem() {
        super(true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        if (closed) {
            throw new IllegalStateException("Invalid request - the item has been closed");
        }

        if (expectedItemsIterator == null) {
            expectedItemsIterator = responses.values().iterator();
        }

        return expectedItemsIterator.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataResponse next() {

        if (current != null) {
            current.close();
        }

        if (closed) {
            throw new IllegalStateException("Invalid request - the item has been closed");
        }

        if (hasNext()) {
            // consume item for condition above (like a counter ...)
            expectedItemsIterator.next();
        } else {
            throw new NoSuchElementException("No item found");
        }

        final Map<String, Collection<String>> nextItemHeaders =
                ODataBatchUtilities.nextItemHeaders(batchLineIterator, boundary);

        if (nextItemHeaders.isEmpty()) {
            throw new IllegalStateException("Expected item not found");
        }

        final Map.Entry<Integer, String> responseLine = ODataBatchUtilities.readResponseLine(batchLineIterator);
        LOG.debug("Retrieved item response {}", responseLine);

        final Map<String, Collection<String>> headers = ODataBatchUtilities.readHeaders(batchLineIterator);
        LOG.debug("Retrieved item headers {}", headers);

        Collection<String> contentId = nextItemHeaders.get(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME);

        if (contentId == null || contentId.isEmpty()) {
            contentId = headers.get(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME);

            if (contentId == null || contentId.isEmpty()) {
                throw new IllegalStateException("Content-ID is missing");
            }
        }

        current = getResponse(contentId.iterator().next());

        if (current == null) {
            throw new IllegalStateException("Unexpected '" + contentId + "' item found");
        }

        current.initFromBatch(responseLine, headers, batchLineIterator, boundary);

        if (current.getStatusCode() >= 400) {
            // found error .... consume expeted items
            while (expectedItemsIterator.hasNext()) {
                expectedItemsIterator.next();
            }
        }

        return current;
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported operation.");
    }
}
