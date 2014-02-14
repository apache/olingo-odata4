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
package com.msopentech.odatajclient.engine.communication.request;

import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import java.util.concurrent.Future;

/**
 * Basic OData request.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> Accepted content-type formats by the request in object.
 */
public interface ODataBasicRequest<V extends ODataResponse, T extends Enum<T>> extends ODataRequest {

    /**
     * Request execute.
     *
     * @return return an OData response.
     */
    V execute();

    /**
     * Async request execute.
     *
     * @return <code>Future&lt;ODataResponse&gt;</code> about the executed request.
     */
    Future<V> asyncExecute();

    /**
     * Override configured request format.
     *
     * @param format request format.
     * @see com.msopentech.odatajclient.engine.format.ODataFormat
     * @see com.msopentech.odatajclient.engine.format.ODataPubFormat
     */
    void setFormat(T format);
}
