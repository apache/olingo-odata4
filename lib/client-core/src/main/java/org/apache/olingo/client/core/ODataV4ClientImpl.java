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
package org.apache.olingo.client.core;

import org.apache.olingo.client.api.ODataV4Client;
import org.apache.olingo.client.api.V4Configuration;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.header.ODataHeaders;
import org.apache.olingo.client.api.communication.request.batch.V4BatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.V4CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.V4InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.V4RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.streamed.V4StreamedRequestFactory;
import org.apache.olingo.client.api.op.ODataBinder;
import org.apache.olingo.client.api.op.ODataReader;
import org.apache.olingo.client.api.op.ODataSerializer;
import org.apache.olingo.client.api.op.ODataV4Deserializer;
import org.apache.olingo.client.api.uri.V4URIBuilder;
import org.apache.olingo.client.api.uri.filter.V4FilterFactory;
import org.apache.olingo.client.core.communication.header.ODataHeadersImpl;
import org.apache.olingo.client.core.communication.request.batch.V4BatchRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.cud.V4CUDRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.invoke.V4InvokeRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.retrieve.V4RetrieveRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.streamed.V4StreamedRequestFactoryImpl;
import org.apache.olingo.client.core.op.impl.ODataV4BinderImpl;
import org.apache.olingo.client.core.op.impl.ODataV4DeserializerImpl;
import org.apache.olingo.client.core.op.impl.ODataV4ReaderImpl;
import org.apache.olingo.client.core.op.impl.ODataV4SerializerImpl;
import org.apache.olingo.client.core.uri.V4URIBuilderImpl;
import org.apache.olingo.client.core.uri.filter.V4FilterFactoryImpl;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class ODataV4ClientImpl extends AbstractODataClient implements ODataV4Client {

  private static final long serialVersionUID = -6653176125573631964L;

  private final V4Configuration configuration = new V4ConfigurationImpl();

  private final V4FilterFactory filterFactory = new V4FilterFactoryImpl();

  private final ODataV4Deserializer deserializer = new ODataV4DeserializerImpl(this);

  private final ODataSerializer serializer = new ODataV4SerializerImpl(this);

  private final ODataReader reader = new ODataV4ReaderImpl(this);

  private final ODataBinder binder = new ODataV4BinderImpl(this);

  private final V4RetrieveRequestFactory retrieveReqFact = new V4RetrieveRequestFactoryImpl(this);

  private final V4CUDRequestFactory cudReqFact = new V4CUDRequestFactoryImpl(this);

  private final V4StreamedRequestFactory streamedReqFact = new V4StreamedRequestFactoryImpl(this);

  private final V4InvokeRequestFactory invokeReqFact = new V4InvokeRequestFactoryImpl(this);

  private final V4BatchRequestFactory batchReqFact = new V4BatchRequestFactoryImpl(this);

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
  public V4Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public V4URIBuilder getURIBuilder(final String serviceRoot) {
    return new V4URIBuilderImpl(serviceRoot);
  }

  @Override
  public V4FilterFactory getFilterFactory() {
    return filterFactory;
  }

  @Override
  public ODataV4Deserializer getDeserializer() {
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
