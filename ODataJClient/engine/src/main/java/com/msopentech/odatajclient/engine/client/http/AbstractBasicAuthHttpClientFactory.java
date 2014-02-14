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
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Base implementation for working with Basic Authentication: needs to be subclassed in order to provide actual username
 * and password.
 */
public abstract class AbstractBasicAuthHttpClientFactory extends DefaultHttpClientFactory {

    private static final long serialVersionUID = 7985626503125490244L;

    protected abstract String getUsername();

    protected abstract String getPassword();

    @Override
    public HttpClient createHttpClient(final HttpMethod method, final URI uri) {
        final DefaultHttpClient httpclient = (DefaultHttpClient) super.createHttpClient(method, uri);

        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(getUsername(), getPassword()));

        return httpclient;
    }
}
