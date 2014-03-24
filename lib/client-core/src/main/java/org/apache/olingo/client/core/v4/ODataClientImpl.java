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
package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.api.v4.Configuration;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.header.ODataHeaders;
import org.apache.olingo.client.api.communication.request.batch.v4.BatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.v4.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.v4.InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.v4.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.streamed.v4.StreamedRequestFactory;
import org.apache.olingo.commons.api.op.ODataSerializer;
import org.apache.olingo.client.api.op.v4.ODataBinder;
import org.apache.olingo.client.api.op.v4.ODataDeserializer;
import org.apache.olingo.client.api.op.v4.ODataReader;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.uri.v4.FilterFactory;
import org.apache.olingo.client.core.AbstractODataClient;
import org.apache.olingo.client.core.communication.header.ODataHeadersImpl;
import org.apache.olingo.client.core.communication.request.batch.v4.BatchRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.cud.v4.CUDRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.invoke.v4.InvokeRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.retrieve.v4.RetrieveRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.streamed.v4.StreamedRequestFactoryImpl;
import org.apache.olingo.client.core.op.impl.v4.ODataBinderImpl;
import org.apache.olingo.client.core.op.impl.v4.ODataDeserializerImpl;
import org.apache.olingo.client.core.op.impl.v4.ODataReaderImpl;
import org.apache.olingo.client.core.op.impl.v4.ODataSerializerImpl;
import org.apache.olingo.client.core.uri.v4.URIBuilderImpl;
import org.apache.olingo.client.core.uri.v4.FilterFactoryImpl;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class ODataClientImpl extends AbstractODataClient implements ODataClient {

  private static final long serialVersionUID = -6653176125573631964L;

  private final Configuration configuration = new ConfigurationImpl();

  private final FilterFactory filterFactory = new FilterFactoryImpl(getServiceVersion());

  private final ODataDeserializer deserializer = new ODataDeserializerImpl(getServiceVersion());

  private final ODataSerializer serializer = new ODataSerializerImpl(getServiceVersion());

  private final ODataReader reader = new ODataReaderImpl(this);

  private final ODataBinder binder = new ODataBinderImpl(this);

  private final RetrieveRequestFactory retrieveReqFact = new RetrieveRequestFactoryImpl(this);

  private final CUDRequestFactory cudReqFact = new CUDRequestFactoryImpl(this);

  private final StreamedRequestFactory streamedReqFact = new StreamedRequestFactoryImpl(this);

  private final InvokeRequestFactory invokeReqFact = new InvokeRequestFactoryImpl(this);

  private final BatchRequestFactory batchReqFact = new BatchRequestFactoryImpl(this);

  @Override
  public ODataServiceVersion getServiceVersion() {
    return ODataServiceVersion.V40;
  }

  @Override
  public ODataHeaders getVersionHeaders() {
    final ODataHeadersImpl odataHeaders = new ODataHeadersImpl();
    odataHeaders.setHeader(HeaderName.maxDataServiceVersion, ODataServiceVersion.V40.toString());
    odataHeaders.setHeader(HeaderName.dataServiceVersion, ODataServiceVersion.V40.toString());
    return odataHeaders;
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public URIBuilder getURIBuilder(final String serviceRoot) {
    return new URIBuilderImpl(getServiceVersion(), serviceRoot);
  }

  @Override
  public FilterFactory getFilterFactory() {
    return filterFactory;
  }

  @Override
  public ODataDeserializer getDeserializer() {
    return deserializer;
  }

  @Override
  public ODataSerializer getSerializer() {
    return serializer;
  }

  @Override
  public ODataReader getReader() {
    return reader;
  }

  @Override
  public ODataBinder getBinder() {
    return binder;
  }

  @Override
  public RetrieveRequestFactory getRetrieveRequestFactory() {
    return retrieveReqFact;
  }

  @Override
  public CUDRequestFactory getCUDRequestFactory() {
    return cudReqFact;
  }

  @Override
  public StreamedRequestFactory getStreamedRequestFactory() {
    return streamedReqFact;
  }

  @Override
  public InvokeRequestFactory getInvokeRequestFactory() {
    return invokeReqFact;
  }

  @Override
  public BatchRequestFactory getBatchRequestFactory() {
    return batchReqFact;
  }
}
