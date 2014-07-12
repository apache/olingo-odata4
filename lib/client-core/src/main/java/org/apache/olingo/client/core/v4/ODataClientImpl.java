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

import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.header.ODataHeaders;
import org.apache.olingo.client.api.communication.request.batch.v4.BatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.v4.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.invoke.InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.v4.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.v4.AsyncRequestFactory;
import org.apache.olingo.client.api.serialization.v4.ODataBinder;
import org.apache.olingo.client.api.serialization.v4.ODataDeserializer;
import org.apache.olingo.client.api.serialization.v4.ODataReader;
import org.apache.olingo.client.api.uri.v4.FilterFactory;
import org.apache.olingo.client.api.uri.v4.SearchFactory;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.AbstractODataClient;
import org.apache.olingo.client.core.communication.header.ODataHeadersImpl;
import org.apache.olingo.client.core.communication.request.batch.v4.BatchRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.cud.v4.CUDRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.invoke.v4.InvokeRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.retrieve.v4.RetrieveRequestFactoryImpl;
import org.apache.olingo.client.core.communication.request.v4.AsyncRequestFactoryImpl;
import org.apache.olingo.client.core.serialization.v4.ODataBinderImpl;
import org.apache.olingo.client.core.serialization.v4.ODataDeserializerImpl;
import org.apache.olingo.client.core.serialization.v4.ODataReaderImpl;
import org.apache.olingo.client.core.uri.v4.FilterFactoryImpl;
import org.apache.olingo.client.core.uri.v4.URIBuilderImpl;
import org.apache.olingo.commons.api.domain.v4.ODataObjectFactory;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataSerializer;
import org.apache.olingo.commons.core.domain.v4.ODataObjectFactoryImpl;
import org.apache.olingo.commons.core.serialization.AtomSerializer;
import org.apache.olingo.commons.core.serialization.JsonSerializer;

public class ODataClientImpl extends AbstractODataClient<UpdateType> implements ODataClient {

  private final FilterFactory filterFactory = new FilterFactoryImpl(getServiceVersion());

  private final SearchFactory searchFactory = new SearchFactoryImpl();

  private final ODataReader reader = new ODataReaderImpl(this);

  private final ODataBinder binder = new ODataBinderImpl(this);

  private final ODataObjectFactory objectFactory = new ODataObjectFactoryImpl(getServiceVersion());

  private final AsyncRequestFactory asyncReqFact = new AsyncRequestFactoryImpl(this);

  private final RetrieveRequestFactory retrieveReqFact = new RetrieveRequestFactoryImpl(this);

  private final CUDRequestFactory cudReqFact = new CUDRequestFactoryImpl(this);

  private final InvokeRequestFactory invokeReqFact = new InvokeRequestFactoryImpl(this);

  private final BatchRequestFactory batchReqFact = new BatchRequestFactoryImpl(this);

  @Override
  public ODataServiceVersion getServiceVersion() {
    return ODataServiceVersion.V40;
  }

  @Override
  public ODataHeaders newVersionHeaders() {
    final ODataHeadersImpl odataHeaders = new ODataHeadersImpl();
    odataHeaders.setHeader(HeaderName.odataMaxVersion, ODataServiceVersion.V40.toString());
    odataHeaders.setHeader(HeaderName.odataVersion, ODataServiceVersion.V40.toString());
    return odataHeaders;
  }

  @Override
  public URIBuilder newURIBuilder(final String serviceRoot) {
    return new URIBuilderImpl(getServiceVersion(), getConfiguration(), serviceRoot);
  }

  @Override
  public FilterFactory getFilterFactory() {
    return filterFactory;
  }

  @Override
  public SearchFactory getSearchFactory() {
    return searchFactory;
  }

  @Override
  public ODataDeserializer getDeserializer(final ODataFormat format) {
    return new ODataDeserializerImpl(getServiceVersion(), false, format);
  }

  @Override
  public ODataSerializer getSerializer(final ODataFormat format) {
    return format == ODataFormat.ATOM || format == ODataFormat.XML
            ? new AtomSerializer(getServiceVersion()) : new JsonSerializer(getServiceVersion(), false);
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
  public ODataObjectFactory getObjectFactory() {
    return objectFactory;
  }

  @Override
  public AsyncRequestFactory getAsyncRequestFactory() {
    return asyncReqFact;
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
  public InvokeRequestFactory getInvokeRequestFactory() {
    return invokeReqFact;
  }

  @Override
  public BatchRequestFactory getBatchRequestFactory() {
    return batchReqFact;
  }
}
