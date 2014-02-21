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

import org.apache.olingo.odata4.client.core.data.impl.v3.ODataDeserializerImpl;
import org.apache.olingo.odata4.client.core.data.impl.v3.ODataReaderImpl;
import org.apache.olingo.odata4.client.core.data.impl.v3.ODataSerializerImpl;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;

public class ODataV3Client extends AbstractODataClient {

  private static final long serialVersionUID = -1655712193243609209L;

  private final V3Configuration configuration = new V3Configuration();

//  private final V3FilterFactory filterFactory = new V3FilterFactory();
//
  private final ODataDeserializerImpl deserializer = new ODataDeserializerImpl(this);
//

  private final ODataSerializerImpl serializer = new ODataSerializerImpl(this);

  private final ODataReaderImpl reader = new ODataReaderImpl(this);

//  private final ODataWriterImpl writer = new ODataWriterImpl(this);
//
//  private final ODataBinderImpl binder = new ODataBinderImpl(this);
//
//  private final ODataObjectFactoryImpl objectFactory = new ODataObjectFactoryImpl(this);
//
//  private final V3RetrieveRequestFactory retrieveReqFact = new V3RetrieveRequestFactory(this);
//
//  private final V3CUDRequestFactory cudReqFact = new V3CUDRequestFactory(this);
//
//  private final V3StreamedRequestFactory streamedReqFact = new V3StreamedRequestFactory(this);
//
//  private final V3InvokeRequestFactory invokeReqFact = new V3InvokeRequestFactory(this);
//
//  private final V3BatchRequestFactory batchReqFact = new V3BatchRequestFactory(this);
  @Override
  public ODataServiceVersion getServiceVersion() {
    return ODataServiceVersion.V30;
  }

//  @Override
//  public ODataHeaders getVersionHeaders() {
//    final ODataHeaders odataHeaders = new ODataHeaders();
//    odataHeaders.setHeader(ODataHeaders.HeaderName.minDataServiceVersion, ODataVersion.V3.toString());
//    odataHeaders.setHeader(ODataHeaders.HeaderName.maxDataServiceVersion, ODataVersion.V3.toString());
//    odataHeaders.setHeader(ODataHeaders.HeaderName.dataServiceVersion, ODataVersion.V3.toString());
//    return odataHeaders;
//  }
  @Override
  public V3Configuration getConfiguration() {
    return configuration;
  }

//  @Override
//  public V3URIBuilder getURIBuilder(final String serviceRoot) {
//    return new V3URIBuilder(configuration, serviceRoot);
//  }
//
//  @Override
//  public V3FilterFactory getFilterFactory() {
//    return filterFactory;
//  }
//
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

//  @Override
//  public ODataWriterImpl getWriter() {
//    return writer;
//  }
//
//  @Override
//  public ODataBinderImpl getBinder() {
//    return binder;
//  }
//
//  @Override
//  public ODataObjectFactoryImpl getObjectFactory() {
//    return objectFactory;
//  }
//
//  @Override
//  public V3RetrieveRequestFactory getRetrieveRequestFactory() {
//    return retrieveReqFact;
//  }
//
//  @Override
//  public V3CUDRequestFactory getCUDRequestFactory() {
//    return cudReqFact;
//  }
//
//  @Override
//  public V3StreamedRequestFactory getStreamedRequestFactory() {
//    return streamedReqFact;
//  }
//
//  @Override
//  public V3InvokeRequestFactory getInvokeRequestFactory() {
//    return invokeReqFact;
//  }
//
//  @Override
//  public V3BatchRequestFactory getBatchRequestFactory() {
//    return batchReqFact;
//  }
}
