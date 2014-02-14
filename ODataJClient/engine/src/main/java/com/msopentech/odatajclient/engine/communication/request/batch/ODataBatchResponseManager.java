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

import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.response.ODataBatchResponse;
import com.msopentech.odatajclient.engine.utils.ODataConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch response manager class.
 */
public class ODataBatchResponseManager implements Iterator<ODataBatchResponseItem> {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ODataBatchResponseManager.class);

    /**
     * Batch response line iterator.
     */
    private final ODataBatchLineIterator batchLineIterator;

    /**
     * Batch boundary.
     */
    private final String batchBoundary;

    /**
     * Expected batch response items iterator.
     */
    private final Iterator<ODataBatchResponseItem> expectedItemsIterator;

    /**
     * Last retrieved batch response item.
     */
    private ODataBatchResponseItem current = null;

    /**
     * Constructor.
     *
     * @param res OData batch response.
     * @param expectedItems expected batch response items.
     */
    public ODataBatchResponseManager(final ODataBatchResponse res, final List<ODataBatchResponseItem> expectedItems) {
        try {
            this.expectedItemsIterator = expectedItems.iterator();
            this.batchLineIterator = new ODataBatchLineIterator(
                    IOUtils.lineIterator(res.getRawResponse(), ODataConstants.UTF8));

            // search for boundary
            batchBoundary = ODataBatchUtilities.getBoundaryFromHeader(
                    res.getHeader(ODataHeaders.HeaderName.contentType));
            LOG.debug("Retrieved batch response bondary '{}'", batchBoundary);
        } catch (IOException e) {
            LOG.error("Error parsing batch response", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return expectedItemsIterator.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataBatchResponseItem next() {
        if (current != null) {
            current.close();
        }

        if (!hasNext()) {
            throw new NoSuchElementException("No item found");
        }

        current = expectedItemsIterator.next();

        final Map<String, Collection<String>> nextItemHeaders =
                ODataBatchUtilities.nextItemHeaders(batchLineIterator, batchBoundary);

        switch (ODataBatchUtilities.getItemType(nextItemHeaders)) {
            case CHANGESET:
                if (!current.isChangeset()) {
                    throw new IllegalStateException("Unexpected batch item");
                }

                current.initFromBatch(
                        batchLineIterator,
                        ODataBatchUtilities.getBoundaryFromHeader(
                        nextItemHeaders.get(ODataHeaders.HeaderName.contentType.toString())));
                break;

            case RETRIEVE:
                if (current.isChangeset()) {
                    throw new IllegalStateException("Unexpected batch item");
                }

                current.initFromBatch(
                        batchLineIterator,
                        batchBoundary);
                break;
            default:
                throw new IllegalStateException("Expected item not found");
        }

        return current;
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }
}
