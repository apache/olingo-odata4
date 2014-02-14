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
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Base implementation for working with NTLM Authentication via embedded HttpClient features: needs to be subclassed
 * in order to provide all needed login information.
 * <br/>
 * External NTLM engine such as <a href="http://jcifs.samba.org/">JCIFS</a> library developed by the
 * <a href="http://www.samba.org/">Samba</a> project as a part of their Windows interoperability suite of programs.
 *
 * @see NTCredentials
 * @see http://hc.apache.org/httpcomponents-client-ga/ntlm.html#Using_Samba_JCIFS_as_an_alternative_NTLM_engine
 */
public abstract class AbstractNTLMAuthHttpClientFactory extends DefaultHttpClientFactory {

    protected abstract String getUsername();

    protected abstract String getPassword();

    protected abstract String getWorkstation();

    protected abstract String getDomain();

    @Override
    public HttpClient createHttpClient(final HttpMethod method, final URI uri) {
        final DefaultHttpClient httpclient = (DefaultHttpClient) super.createHttpClient(method, uri);

        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new NTCredentials(getUsername(), getPassword(), getWorkstation(), getDomain()));

        httpclient.setCredentialsProvider(credsProvider);

        return httpclient;
    }
}
