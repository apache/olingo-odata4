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
package com.msopentech.odatajclient.engine.client.http;

import org.apache.http.HttpStatus;

/**
 * Exception to be thrown when trying to read content with HTTP status 204.
 */
public class NoContentException extends HttpClientException {

    private static final long serialVersionUID = 7947066635285809192L;

    /**
     * Constructs a new client-side runtime exception, with fixed message.
     */
    public NoContentException() {
        super("No content found when HTTP status is " + HttpStatus.SC_NO_CONTENT);
    }
}
