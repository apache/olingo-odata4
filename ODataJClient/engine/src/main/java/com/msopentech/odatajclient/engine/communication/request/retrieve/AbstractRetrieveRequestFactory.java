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
package com.msopentech.odatajclient.engine.communication.request.retrieve;

import com.msopentech.odatajclient.engine.client.ODataClient;
import java.net.URI;

public abstract class AbstractRetrieveRequestFactory implements RetrieveRequestFactory {

    private static final long serialVersionUID = -111683263158803362L;

    protected final ODataClient client;

    protected AbstractRetrieveRequestFactory(final ODataClient client) {
        this.client = client;
    }

    @Override
    public ODataEntitySetRequest getEntitySetRequest(final URI query) {
        return new ODataEntitySetRequest(client, query);
    }

    @Override
    public ODataEntitySetIteratorRequest getEntitySetIteratorRequest(final URI query) {
        return new ODataEntitySetIteratorRequest(client, query);
    }

    @Override
    public ODataEntityRequest getEntityRequest(final URI query) {
        return new ODataEntityRequest(client, query);
    }

    @Override
    public ODataPropertyRequest getPropertyRequest(final URI query) {
        return new ODataPropertyRequest(client, query);
    }

    @Override
    public ODataValueRequest getValueRequest(final URI query) {
        return new ODataValueRequest(client, query);
    }

    @Override
    public ODataLinkCollectionRequest getLinkCollectionRequest(final URI targetURI, final String linkName) {
        return new ODataLinkCollectionRequest(client, targetURI, linkName);
    }

    @Override
    public ODataMediaRequest getMediaRequest(final URI query) {
        return new ODataMediaRequest(client, query);
    }

    @Override
    public ODataRawRequest getRawRequest(final URI uri) {
        return new ODataRawRequest(client, uri);
    }

    @Override
    public ODataGenericRetrieveRequest getGenericRetrieveRequest(final URI uri) {
        return new ODataGenericRetrieveRequest(client, uri);
    }
}
