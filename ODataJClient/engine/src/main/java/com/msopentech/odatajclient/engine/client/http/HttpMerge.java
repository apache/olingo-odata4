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

import java.net.URI;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 * Class identifying MERGE HTTP method.
 */
@NotThreadSafe
public class HttpMerge extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "MERGE";

    /**
     * Constructor.
     */
    public HttpMerge() {
        super();
    }

    /**
     * Constructor.
     *
     * @param uri request URI.
     */
    public HttpMerge(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * Constructor.
     *
     * @param uri request URI.
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpMerge(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    /**
     * Gets HTTP method name.
     *
     * @return HTTP method name.
     */
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
