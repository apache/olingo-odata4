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

/**
 * Utility class used to communicate batch info.
 */
public class ODataBatchController {

    /**
     * Batch validity.
     */
    private boolean validBatch = true;

    /**
     * Batch boundary.
     */
    private final String boundary;

    /**
     * Batch line iterator.
     */
    private final ODataBatchLineIterator batchLineIterator;

    /**
     * Constructor.
     *
     * @param batchLineIterator batch line iterator.
     * @param boundary batch boundary.
     */
    public ODataBatchController(final ODataBatchLineIterator batchLineIterator, final String boundary) {
        this.batchLineIterator = batchLineIterator;
        this.boundary = boundary;
    }

    /**
     * Checks if batch is valid.
     *
     * @return batch validity.
     */
    public boolean isValidBatch() {
        return validBatch;
    }

    /**
     * Sets batch validity.
     *
     * @param validBatch validity.
     */
    public void setValidBatch(final boolean validBatch) {
        this.validBatch = validBatch;
    }

    /**
     * Gest batch boundary.
     *
     * @return batch boundary.
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Gest batch line iterator.
     *
     * @return batch line iterator.
     */
    public ODataBatchLineIterator getBatchLineIterator() {
        return batchLineIterator;
    }
}
