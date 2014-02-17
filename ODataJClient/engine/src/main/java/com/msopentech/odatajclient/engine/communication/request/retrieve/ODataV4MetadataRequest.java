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
import com.msopentech.odatajclient.engine.metadata.EdmV4Metadata;
import com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType;
import com.msopentech.odatajclient.engine.metadata.edm.v4.DataServices;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Edmx;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EntityType;
import com.msopentech.odatajclient.engine.metadata.edm.v4.FunctionImport;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Schema;
import java.net.URI;

public class ODataV4MetadataRequest extends ODataMetadataRequest<
        EdmV4Metadata, Edmx, DataServices, Schema, EntityContainer, EntityType, ComplexType, FunctionImport> {

    public ODataV4MetadataRequest(final ODataClient odataClient, final URI uri) {
        super(odataClient, uri);
    }

}
