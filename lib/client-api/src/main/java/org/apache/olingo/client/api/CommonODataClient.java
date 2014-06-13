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
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.batch.CommonBatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.CommonCUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.CommonUpdateType;
import org.apache.olingo.client.api.communication.request.invoke.InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.CommonRetrieveRequestFactory;
import org.apache.olingo.client.api.serialization.ClientODataDeserializer;
import org.apache.olingo.client.api.serialization.CommonODataBinder;
import org.apache.olingo.client.api.serialization.CommonODataReader;
import org.apache.olingo.client.api.serialization.ODataWriter;
import org.apache.olingo.client.api.uri.CommonFilterFactory;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.domain.CommonODataObjectFactory;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataSerializer;

/**
 * Generic client interface (common to all supported OData protocol versions).
 *
 * @param <UT> concrete update type, depending on the protocol version
 */
public interface CommonODataClient<UT extends CommonUpdateType> {

  ODataServiceVersion getServiceVersion();

  ODataHeaders newVersionHeaders();

  CommonConfiguration getConfiguration();

  ODataPreferences newPreferences();

  CommonURIBuilder<?> newURIBuilder(String serviceRoot);

  CommonFilterFactory getFilterFactory();

  ODataSerializer getSerializer(ODataFormat format);

  ClientODataDeserializer getDeserializer(ODataFormat format);

  CommonODataReader getReader();

  ODataWriter getWriter();

  CommonODataBinder getBinder();

  CommonODataObjectFactory getObjectFactory();

  CommonRetrieveRequestFactory getRetrieveRequestFactory();

  CommonCUDRequestFactory<UT> getCUDRequestFactory();

  InvokeRequestFactory getInvokeRequestFactory();

  CommonBatchRequestFactory getBatchRequestFactory();
}
