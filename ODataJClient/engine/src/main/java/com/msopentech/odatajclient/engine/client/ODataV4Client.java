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
package com.msopentech.odatajclient.engine.client;

import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.batch.V4BatchRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.V4CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.invoke.V4InvokeRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.V4RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.streamed.V4StreamedRequestFactory;
import com.msopentech.odatajclient.engine.data.impl.v4.ODataBinderImpl;
import com.msopentech.odatajclient.engine.data.impl.v4.ODataReaderImpl;
import com.msopentech.odatajclient.engine.data.impl.v4.ODataWriterImpl;
import com.msopentech.odatajclient.engine.data.impl.v4.ODataDeserializerImpl;
import com.msopentech.odatajclient.engine.data.impl.v4.ODataObjectFactoryImpl;
import com.msopentech.odatajclient.engine.data.impl.v4.ODataSerializerImpl;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.uri.V4URIBuilder;
import com.msopentech.odatajclient.engine.uri.filter.V4FilterFactory;
import com.msopentech.odatajclient.engine.utils.ODataVersion;

public class ODataV4Client extends AbstractODataClient {

    private static final long serialVersionUID = -6653176125573631964L;

    private final V4Configuration configuration = new V4Configuration();

    private final V4FilterFactory filterFactory = new V4FilterFactory();

    private final ODataDeserializerImpl deserializer = new ODataDeserializerImpl(this);

    private final ODataSerializerImpl serializer = new ODataSerializerImpl(this);

    private final ODataReaderImpl reader = new ODataReaderImpl(this);

    private final ODataWriterImpl writer = new ODataWriterImpl(this);

    private final ODataBinderImpl binder = new ODataBinderImpl(this);

    private final ODataObjectFactoryImpl objectFactory = new ODataObjectFactoryImpl(this);

    private final V4RetrieveRequestFactory retrieveReqFact = new V4RetrieveRequestFactory(this);

    private final V4CUDRequestFactory cudReqFact = new V4CUDRequestFactory(this);

    private final V4StreamedRequestFactory streamedReqFact = new V4StreamedRequestFactory(this);

    private final V4InvokeRequestFactory invokeReqFact = new V4InvokeRequestFactory(this);

    private final V4BatchRequestFactory batchReqFact = new V4BatchRequestFactory(this);

    @Override
    public ODataVersion getWorkingVersion() {
        return ODataVersion.V4;
    }

    @Override
    public ODataHeaders getVersionHeaders() {
        final ODataHeaders odataHeaders = new ODataHeaders();
        odataHeaders.setHeader(ODataHeaders.HeaderName.maxDataServiceVersion, ODataVersion.V4.toString());
        odataHeaders.setHeader(ODataHeaders.HeaderName.dataServiceVersion, ODataVersion.V4.toString());
        return odataHeaders;
    }

    @Override
    public V4Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public URIBuilder getURIBuilder(final String serviceRoot) {
        return new V4URIBuilder(configuration, serviceRoot);
    }

    @Override
    public V4FilterFactory getFilterFactory() {
        return filterFactory;
    }

    @Override
    public ODataDeserializerImpl getDeserializer() {
        return deserializer;
    }

    @Override
    public ODataSerializerImpl getSerializer() {
        return serializer;
    }

    @Override
    public ODataReaderImpl getReader() {
        return reader;
    }

    @Override
    public ODataWriterImpl getWriter() {
        return writer;
    }

    @Override
    public ODataBinderImpl getBinder() {
        return binder;
    }

    @Override
    public ODataObjectFactoryImpl getObjectFactory() {
        return objectFactory;
    }

    @Override
    public V4RetrieveRequestFactory getRetrieveRequestFactory() {
        return retrieveReqFact;
    }

    @Override
    public V4CUDRequestFactory getCUDRequestFactory() {
        return cudReqFact;
    }

    @Override
    public V4StreamedRequestFactory getStreamedRequestFactory() {
        return streamedReqFact;
    }

    @Override
    public V4InvokeRequestFactory getInvokeRequestFactory() {
        return invokeReqFact;
    }

    @Override
    public V4BatchRequestFactory getBatchRequestFactory() {
        return batchReqFact;
    }
}
