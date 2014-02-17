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
package com.msopentech.odatajclient.engine.client;

import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.batch.BatchRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.invoke.InvokeRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.streamed.StreamedRequestFactory;
import com.msopentech.odatajclient.engine.data.ODataDeserializer;
import com.msopentech.odatajclient.engine.data.ODataBinder;
import com.msopentech.odatajclient.engine.data.ODataGeospatialValue;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataReader;
import com.msopentech.odatajclient.engine.data.ODataWriter;
import com.msopentech.odatajclient.engine.data.ODataSerializer;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.uri.filter.FilterFactory;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.Serializable;

public interface ODataClient extends Serializable {

    ODataVersion getWorkingVersion();

    ODataHeaders getVersionHeaders();

    Configuration getConfiguration();

    URIBuilder getURIBuilder(String serviceRoot);

    FilterFactory getFilterFactory();

    ODataPrimitiveValue.Builder getPrimitiveValueBuilder();

    ODataGeospatialValue.Builder getGeospatialValueBuilder();

    ODataSerializer getSerializer();

    ODataDeserializer getDeserializer();

    ODataReader getReader();

    ODataWriter getWriter();

    ODataBinder getBinder();

    ODataObjectFactory getObjectFactory();

    RetrieveRequestFactory getRetrieveRequestFactory();

    CUDRequestFactory getCUDRequestFactory();

    StreamedRequestFactory getStreamedRequestFactory();

    InvokeRequestFactory<?, ?, ?, ?, ?, ?, ?, ?> getInvokeRequestFactory();

    BatchRequestFactory getBatchRequestFactory();
}
