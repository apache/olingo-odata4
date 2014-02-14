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

import com.msopentech.odatajclient.engine.communication.request.ODataRequest;

/**
 * Object request that can be sent embedded into a batch request.
 */
public interface ODataBatchableRequest extends ODataRequest {

    /**
     * Writes (and consume) the request onto the given batch stream.
     * <p>
     * Please note that this method will consume the request (execution won't be possible anymore).
     *
     * @param req destination batch request.
     */
    void batch(final ODataBatchRequest req);

    /**
     * Writes (and consume) the request onto the given batch stream.
     * <p>
     * Please note that this method will consume the request (execution won't be possible anymore).
     *
     * @param req destination batch request.
     * @param contentId ContentId header value to be added to the serialization.
     * Use this in case of changeset items.
     */
    void batch(final ODataBatchRequest req, final String contentId);
}
