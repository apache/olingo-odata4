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

import com.msopentech.odatajclient.engine.communication.request.ODataStreamer;
import com.msopentech.odatajclient.engine.utils.ODataBatchConstants;

/**
 * Abstract representation of a batch request item.
 */
public abstract class ODataBatchRequestItem extends ODataStreamer {

    /**
     * Stream started check.
     */
    protected boolean hasStreamedSomething = false;

    /**
     * Stream open check.
     */
    private boolean open = false;

    /**
     * OData batch request.
     */
    protected ODataBatchRequest req;

    /**
     * Constructor.
     *
     * @param req OData batch request.
     */
    public ODataBatchRequestItem(final ODataBatchRequest req) {
        super(req.getOutputStream());
        this.open = true;
        this.req = req;
    }

    /**
     * Checks if the current item is still opened.
     *
     * @return 'TRUE' if opened; 'FALSE' otherwise.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Closes the item.
     */
    public void close() {
        closeItem();
        open = false;
    }

    /**
     * Stream the given request header.
     * <p>
     * Use this method to stream changeset items.
     *
     * @param request request to be batched.
     * @param contentId changeset item id.
     */
    protected void streamRequestHeader(final ODataBatchableRequest request, final int contentId) {
        //stream batch content type
        stream(ODataBatchConstants.ITEM_CONTENT_TYPE_LINE.getBytes());
        newLine();
        stream(ODataBatchConstants.ITEM_TRANSFER_ENCODING_LINE.getBytes());
        newLine();
        stream((ODataBatchConstants.CHANGESET_CONTENT_ID_NAME + ":" + contentId).getBytes());
        newLine();
        newLine();
    }

    /**
     * Stream the given request header.
     *
     * @param request request to be batched.
     */
    protected void streamRequestHeader(final ODataBatchableRequest request) {
        //stream batch content type
        stream(ODataBatchConstants.ITEM_CONTENT_TYPE_LINE.getBytes());
        newLine();
        stream(ODataBatchConstants.ITEM_TRANSFER_ENCODING_LINE.getBytes());
        newLine();
        newLine();

        stream(request.toByteArray());
        newLine();
    }

    /**
     * Checks if the streaming of the current item is started yet.
     *
     * @return 'TRUE' if started; 'FALSE' otherwise.
     */
    public boolean hasStreamedSomething() {
        return hasStreamedSomething;
    }

    /**
     * Closes the current item.
     */
    protected abstract void closeItem();
}
