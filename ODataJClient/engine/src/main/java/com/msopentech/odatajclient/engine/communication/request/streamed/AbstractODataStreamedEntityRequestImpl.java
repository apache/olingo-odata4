/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.msopentech.odatajclient.engine.communication.request.streamed;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.request.ODataStreamManager;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.net.URI;
import javax.security.auth.login.Configuration;

/**
 * Abstract class representing a request concerning a streamed entity.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> OData request payload type corresponding to the request implementation.
 */
public abstract class AbstractODataStreamedEntityRequestImpl<V extends ODataResponse, T extends ODataStreamManager<V>>
        extends AbstractODataStreamedRequestImpl<V, T> {

    private ODataPubFormat format;

    /**
     * Constructor.
     *
     * @param odataClient client instance getting this request
     * @param method HTTP request method.
     * @param uri request URI.
     */
    public AbstractODataStreamedEntityRequestImpl(final ODataClient odataClient, final HttpMethod method,
            URI uri) {
        super(odataClient, method, uri);
        setAccept(getFormat().toString());
    }

    /**
     * Returns resource representation format.
     *
     * @return the configured format (or default if not specified).
     * @see Configuration#getDefaultPubFormat()
     */
    public final ODataPubFormat getFormat() {
        return format == null ? odataClient.getConfiguration().getDefaultPubFormat() : format;
    }

    /**
     * Override configured request format.
     *
     * @param format request format.
     * @see ODataFormat
     */
    public final void setFormat(final ODataPubFormat format) {
        this.format = format;
        setAccept(format.toString());
    }
}
