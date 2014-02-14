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

import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of a response item about a batch request.
 */
public abstract class ODataBatchResponseItem implements Iterator<ODataResponse> {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ODataBatchResponseItem.class);

    /**
     * Expected OData responses for the current batch response item.
     */
    protected final Map<String, ODataResponse> responses = new HashMap<String, ODataResponse>();

    /**
     * Expected OData responses iterator.
     */
    protected Iterator<ODataResponse> expectedItemsIterator;

    /**
     * Changeset controller.
     * Gives more information about the type of batch item.
     */
    private final boolean changeset;

    /**
     * Batch response line iterator.
     */
    protected ODataBatchLineIterator batchLineIterator;

    /**
     * Batch boundary.
     */
    protected String boundary;

    /**
     * Gives information about the batch response item status.
     */
    protected boolean closed = false;

    /**
     * Constructor.
     *
     * @param isChangeset 'TRUE' if the current batch response item is a changeset.
     */
    public ODataBatchResponseItem(boolean isChangeset) {
        this.changeset = isChangeset;
    }

    /**
     * Adds the given OData response template to the current OData batch response item.
     *
     * @param contentId changeset contentId in case of changeset; '__RETRIEVE__' in case of retrieve item.
     * @param res OData response template to be added.
     */
    void addResponse(final String contentId, final ODataResponse res) {
        if (closed) {
            throw new IllegalStateException("Invalid batch item because explicitely closed");
        }
        responses.put(contentId, res);
    }

    /**
     * Initializes ODataResponse template from batch response item part.
     *
     * @param batchLineIterator batch response line iterator.
     * @param boundary batch response boundary.
     */
    void initFromBatch(final ODataBatchLineIterator batchLineIterator, final String boundary) {
        if (closed) {
            throw new IllegalStateException("Invalid batch item because explicitely closed");
        }
        LOG.debug("Init from batch - boundary '{}'", boundary);
        this.batchLineIterator = batchLineIterator;
        this.boundary = boundary;
    }

    /**
     * Gets response about the given contentId.
     *
     * @param contentId response identifier (a specific contentId in case of changeset item).
     * @return ODataResponse corresponding to the given contentId.
     */
    protected ODataResponse getResponse(final String contentId) {
        if (closed) {
            throw new IllegalStateException("Invalid batch item because explicitely closed");
        }
        return responses.get(contentId);
    }

    /**
     * Gets OData responses iterator.
     *
     * @return OData responses iterator.
     */
    protected Iterator<ODataResponse> getResponseIterator() {
        if (closed) {
            throw new IllegalStateException("Invalid batch item because explicitely closed");
        }
        return responses.values().iterator();
    }

    /**
     * Checks if the current batch response item is a changeset.
     *
     * @return 'TRUE' if the item is a changeset; 'FALSE' otherwise.
     */
    public final boolean isChangeset() {
        return changeset;
    }

    /**
     * Closes the current batch responses item including all wrapped OData responses.
     */
    public void close() {
        for (ODataResponse response : responses.values()) {
            response.close();
        }
        closed = true;
    }
}
