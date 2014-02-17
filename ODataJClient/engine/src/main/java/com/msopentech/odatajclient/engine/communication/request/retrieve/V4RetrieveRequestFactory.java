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

import com.msopentech.odatajclient.engine.client.ODataV4Client;
import org.apache.commons.lang3.StringUtils;

public class V4RetrieveRequestFactory extends AbstractRetrieveRequestFactory {

    private static final long serialVersionUID = 546577958047902917L;

    public V4RetrieveRequestFactory(final ODataV4Client client) {
        super(client);
    }

    @Override
    public ODataV4MetadataRequest getMetadataRequest(final String serviceRoot) {
        return new ODataV4MetadataRequest(client, client.getURIBuilder(serviceRoot).appendMetadataSegment().build());
    }

    @Override
    public ODataServiceDocumentRequest getServiceDocumentRequest(final String serviceRoot) {
        return new ODataServiceDocumentRequest(client,
                StringUtils.isNotBlank(serviceRoot) && serviceRoot.endsWith("/")
                ? client.getURIBuilder(serviceRoot).build()
                : client.getURIBuilder(serviceRoot + "/").build());
    }
}
