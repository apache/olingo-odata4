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
package com.msopentech.odatajclient.engine.communication.response;

import com.msopentech.odatajclient.engine.data.ODataInvokeResult;

/**
 * This class implements a response to a specific invoke request.
 *
 * @see com.msopentech.odatajclient.engine.communication.request.invoke.ODataInvokeRequest
 */
public interface ODataInvokeResponse<T extends ODataInvokeResult> extends ODataResponse {

    /**
     * Gets operation return value if exists.
     *
     * @return operation return value.
     */
    T getBody();
}
