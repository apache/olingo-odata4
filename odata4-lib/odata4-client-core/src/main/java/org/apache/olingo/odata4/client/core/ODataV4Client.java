/*
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
package org.apache.olingo.odata4.client.core;

import org.apache.olingo.odata4.client.core.op.impl.v4.ODataBinderImpl;
import org.apache.olingo.odata4.client.core.op.impl.v4.ODataDeserializerImpl;
import org.apache.olingo.odata4.client.core.op.impl.v4.ODataReaderImpl;
import org.apache.olingo.odata4.client.core.op.impl.v4.ODataSerializerImpl;
import org.apache.olingo.odata4.client.core.uri.V4URIBuilder;
import org.apache.olingo.odata4.client.core.uri.filter.V4FilterFactory;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;

public class ODataV4Client extends AbstractODataClient {

  private static final long serialVersionUID = -6653176125573631964L;

  private final V4Configuration configuration = new V4Configuration();

  private final V4FilterFactory filterFactory = new V4FilterFactory();

  private final ODataDeserializerImpl deserializer = new ODataDeserializerImpl(this);

  private final ODataSerializerImpl serializer = new ODataSerializerImpl(this);

  private final ODataReaderImpl reader = new ODataReaderImpl(this);

//    private final ODataWriterImpl writer = new ODataWriterImpl(this);
  private final ODataBinderImpl binder = new ODataBinderImpl(this);

//    private final ODataObjectFactoryImpl objectFactory = new ODataObjectFactoryImpl(this);
//
//    private final V4RetrieveRequestFactory retrieveReqFact = new V4RetrieveRequestFactory(this);
//
//    private final V4CUDRequestFactory cudReqFact = new V4CUDRequestFactory(this);
//
//    private final V4StreamedRequestFactory streamedReqFact = new V4StreamedRequestFactory(this);
//
//    private final V4InvokeRequestFactory invokeReqFact = new V4InvokeRequestFactory(this);
//
//    private final V4BatchRequestFactory batchReqFact = new V4BatchRequestFactory(this);
  @Override
  public ODataServiceVersion getServiceVersion() {
    return ODataServiceVersion.V40;
  }
//
//    @Override
//    public ODataHeaders getVersionHeaders() {
//        final ODataHeaders odataHeaders = new ODataHeaders();
//        odataHeaders.setHeader(ODataHeaders.HeaderName.maxDataServiceVersion, ODataVersion.V4.toString());
//        odataHeaders.setHeader(ODataHeaders.HeaderName.dataServiceVersion, ODataVersion.V4.toString());
//        return odataHeaders;
//    }

  @Override
  public V4Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public V4URIBuilder getURIBuilder(final String serviceRoot) {
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

//    @Override
//    public ODataWriterImpl getWriter() {
//        return writer;
//    }
  @Override
  public ODataBinderImpl getBinder() {
    return binder;
  }

//    @Override
//    public ODataObjectFactoryImpl getObjectFactory() {
//        return objectFactory;
//    }
//
//    @Override
//    public V4RetrieveRequestFactory getRetrieveRequestFactory() {
//        return retrieveReqFact;
//    }
//
//    @Override
//    public V4CUDRequestFactory getCUDRequestFactory() {
//        return cudReqFact;
//    }
//
//    @Override
//    public V4StreamedRequestFactory getStreamedRequestFactory() {
//        return streamedReqFact;
//    }
//
//    @Override
//    public V4InvokeRequestFactory getInvokeRequestFactory() {
//        return invokeReqFact;
//    }
//
//    @Override
//    public V4BatchRequestFactory getBatchRequestFactory() {
//        return batchReqFact;
//    }
}
