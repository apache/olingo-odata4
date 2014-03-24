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

import org.apache.olingo.client.api.communication.header.ODataHeaders;
import org.apache.olingo.client.api.communication.request.batch.CommonBatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.CommonCUDRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.CommonInvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.CommonRetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.streamed.CommonStreamedRequestFactory;
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataGeospatialValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.client.api.op.CommonODataBinder;
import org.apache.olingo.client.api.op.CommonODataDeserializer;
import org.apache.olingo.client.api.op.CommonODataReader;
import org.apache.olingo.client.api.op.ODataSerializer;
import org.apache.olingo.client.api.op.ODataWriter;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.CommonFilterFactory;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public interface CommonODataClient {

  ODataServiceVersion getServiceVersion();

  ODataHeaders getVersionHeaders();

  CommonConfiguration getConfiguration();

  CommonURIBuilder<?> getURIBuilder(String serviceRoot);

  CommonFilterFactory getFilterFactory();

  ODataPrimitiveValue.Builder getPrimitiveValueBuilder();

  ODataGeospatialValue.Builder getGeospatialValueBuilder();

  ODataSerializer getSerializer();

  CommonODataDeserializer getDeserializer();

  CommonODataReader getReader();

  ODataWriter getWriter();

  CommonODataBinder getBinder();

  ODataObjectFactory getObjectFactory();

  CommonRetrieveRequestFactory getRetrieveRequestFactory();

  CommonCUDRequestFactory getCUDRequestFactory();

  CommonStreamedRequestFactory getStreamedRequestFactory();

  CommonInvokeRequestFactory getInvokeRequestFactory();

  CommonBatchRequestFactory getBatchRequestFactory();
}
