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
package org.apache.olingo.client.api.v4;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.batch.v4.BatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.v4.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.v4.InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.v4.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.streamed.v4.StreamedRequestFactory;
import org.apache.olingo.client.api.op.v4.ODataBinder;
import org.apache.olingo.client.api.op.v4.ODataDeserializer;
import org.apache.olingo.client.api.op.v4.ODataReader;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.uri.v4.FilterFactory;
import org.apache.olingo.commons.api.domain.v4.ODataObjectFactory;

public interface ODataClient extends CommonODataClient {

  @Override
  Configuration getConfiguration();

  @Override
  ODataDeserializer getDeserializer();

  @Override
  ODataReader getReader();

  @Override
  ODataBinder getBinder();

  @Override
  URIBuilder getURIBuilder(String serviceRoot);

  @Override
  FilterFactory getFilterFactory();

  @Override
  ODataObjectFactory getObjectFactory();

  @Override
  RetrieveRequestFactory getRetrieveRequestFactory();

  @Override
  CUDRequestFactory getCUDRequestFactory();

  @Override
  StreamedRequestFactory getStreamedRequestFactory();

  @Override
  InvokeRequestFactory getInvokeRequestFactory();

  @Override
  BatchRequestFactory getBatchRequestFactory();
}
