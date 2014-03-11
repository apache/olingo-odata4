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
package org.apache.olingo.client.api;

import org.apache.olingo.client.api.domain.ODataGeospatialValue;
import org.apache.olingo.client.api.domain.ODataObjectFactory;
import org.apache.olingo.client.api.domain.ODataPrimitiveValue;
import org.apache.olingo.client.api.op.ODataBinder;
import org.apache.olingo.client.api.op.ODataDeserializer;
import org.apache.olingo.client.api.op.ODataReader;
import org.apache.olingo.client.api.op.ODataSerializer;
import org.apache.olingo.client.api.op.ODataWriter;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.filter.FilterFactory;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public interface ODataClient {

  ODataServiceVersion getServiceVersion();

  //ODataHeaders getVersionHeaders();
  Configuration getConfiguration();

  URIBuilder<?> getURIBuilder(String serviceRoot);

  FilterFactory getFilterFactory();

  ODataPrimitiveValue.Builder getPrimitiveValueBuilder();

  ODataGeospatialValue.Builder getGeospatialValueBuilder();

  ODataSerializer getSerializer();

  ODataDeserializer getDeserializer();

  ODataReader getReader();

  ODataWriter getWriter();

  ODataBinder getBinder();

  ODataObjectFactory getObjectFactory();
//  RetrieveRequestFactory getRetrieveRequestFactory();
//  CUDRequestFactory getCUDRequestFactory();
//  StreamedRequestFactory getStreamedRequestFactory();
//  InvokeRequestFactory<?, ?, ?, ?, ?, ?, ?, ?> getInvokeRequestFactory();
//  BatchRequestFactory getBatchRequestFactory();
}
