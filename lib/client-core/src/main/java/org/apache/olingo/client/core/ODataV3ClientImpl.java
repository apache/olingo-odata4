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

import org.apache.olingo.client.api.ODataV3Client;
import org.apache.olingo.client.api.V3Configuration;
import org.apache.olingo.client.api.op.ODataBinder;
import org.apache.olingo.client.api.op.ODataReader;
import org.apache.olingo.client.api.op.ODataSerializer;
import org.apache.olingo.client.api.op.ODataV3Deserializer;
import org.apache.olingo.client.api.uri.V3URIBuilder;
import org.apache.olingo.client.api.uri.filter.V3FilterFactory;
import org.apache.olingo.client.core.op.impl.ODataV3BinderImpl;
import org.apache.olingo.client.core.op.impl.ODataV3DeserializerImpl;
import org.apache.olingo.client.core.op.impl.ODataV3ReaderImpl;
import org.apache.olingo.client.core.op.impl.ODataV3SerializerImpl;
import org.apache.olingo.client.core.uri.V3URIBuilderImpl;
import org.apache.olingo.client.core.uri.filter.V3FilterFactoryImpl;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class ODataV3ClientImpl extends AbstractODataClient implements ODataV3Client {

  private static final long serialVersionUID = -1655712193243609209L;

  private final V3Configuration configuration = new V3ConfigurationImpl();

  private final V3FilterFactory filterFactory = new V3FilterFactoryImpl();

  private final ODataV3Deserializer deserializer = new ODataV3DeserializerImpl(this);

  private final ODataSerializer serializer = new ODataV3SerializerImpl(this);

  private final ODataReader reader = new ODataV3ReaderImpl(this);

//  private final ODataWriterImpl writer = new ODataWriterImpl(this);
  private final ODataBinder binder = new ODataV3BinderImpl(this);

//  private final ODataObjectFactory objectFactory = new ODataObjectFactoryImpl(this);
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

  @Override
  public V3URIBuilder getURIBuilder(final String serviceRoot) {
    return new V3URIBuilderImpl(configuration, serviceRoot);
  }

  @Override
  public V3FilterFactory getFilterFactory() {
    return filterFactory;
  }

  @Override
  public ODataV3Deserializer getDeserializer() {
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

//  @Override
//  public ODataWriterImpl getWriter() {
//    return writer;
//  }
  @Override
  public ODataBinder getBinder() {
    return binder;
  }

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
