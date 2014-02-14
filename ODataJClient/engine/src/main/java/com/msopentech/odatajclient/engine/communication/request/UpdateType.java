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

import com.msopentech.odatajclient.engine.client.http.HttpMethod;

/**
 * Update type.
 */
public enum UpdateType {

    /**
     * Replace all and remove missing attributes.
     */
    REPLACE(HttpMethod.PUT),
    /**
     * Differential update with whole entity as input (non-standard).
     * Differences will be retrieved by the server itself.
     */
    MERGE(HttpMethod.MERGE),
    /**
     * Differential update with only specified input property values to be replaced.
     */
    PATCH(HttpMethod.PATCH);

    private final HttpMethod method;

    private UpdateType(final HttpMethod method) {
        this.method = method;
    }

    /**
     * Gets HTTP request method.
     *
     * @return HTTP request method.
     */
    public HttpMethod getMethod() {
        return method;
    }
}
