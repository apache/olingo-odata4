/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.api;

import org.apache.olingo.client.api.communication.header.ODataHeaders;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.AsyncRequestFactory;
import org.apache.olingo.client.api.communication.request.batch.BatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.serialization.ClientODataDeserializer;
import org.apache.olingo.client.api.serialization.ODataBinder;
import org.apache.olingo.client.api.serialization.ODataMetadataValidation;
import org.apache.olingo.client.api.serialization.ODataReader;
import org.apache.olingo.client.api.serialization.ODataSerializer;
import org.apache.olingo.client.api.serialization.ODataWriter;
import org.apache.olingo.client.api.uri.FilterFactory;
import org.apache.olingo.client.api.uri.SearchFactory;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;


public interface ODataClient {

  ODataServiceVersion getServiceVersion();

  ODataHeaders newVersionHeaders();

  Configuration getConfiguration();

  ODataPreferences newPreferences();

  ODataSerializer getSerializer(ContentType contentType);

  ODataWriter getWriter();

  InvokeRequestFactory getInvokeRequestFactory();

  ClientODataDeserializer getDeserializer(ContentType contentType);

  ODataReader getReader();

  ODataBinder getBinder();

  URIBuilder newURIBuilder(String serviceRoot);

  FilterFactory getFilterFactory();

  SearchFactory getSearchFactory();

  ClientObjectFactory getObjectFactory();

  AsyncRequestFactory getAsyncRequestFactory();

  RetrieveRequestFactory getRetrieveRequestFactory();

  CUDRequestFactory getCUDRequestFactory();

  BatchRequestFactory getBatchRequestFactory();
  
  ODataMetadataValidation metadataValidation();
}
