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
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Base implementation for working with OAuth via <a href="https://code.google.com/p/oauth-signpost/">signpost</a>:
 * needs to be subclassed in order to provide actual OAuth authentication details.
 */
public abstract class AbstractOAuthSignPostHttpUriRequestFactory extends DefaultHttpUriRequestFactory {

    protected abstract String getConsumerKey();

    protected abstract String getConsumerSecret();

    protected abstract String getAccessToken();

    protected abstract String getTokenSecret();

    @Override
    public HttpUriRequest createHttpUriRequest(final HttpMethod method, final URI uri) {
        final HttpUriRequest request = super.createHttpUriRequest(method, uri);

        final OAuthConsumer consumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
        consumer.setTokenWithSecret(getAccessToken(), getTokenSecret());

        try {
            consumer.sign(request);
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign request via OAuth", e);
        }

        return request;
    }
}
