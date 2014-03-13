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

import org.apache.olingo.client.api.communication.request.batch.V3BatchRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.V3CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.V3InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.V3RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.streamed.V3StreamedRequestFactory;
import org.apache.olingo.client.api.op.ODataV3Deserializer;
import org.apache.olingo.client.api.uri.V3URIBuilder;
import org.apache.olingo.client.api.uri.filter.V3FilterFactory;

public interface ODataV3Client extends ODataClient {

  @Override
  V3Configuration getConfiguration();

  @Override
  V3URIBuilder getURIBuilder(String serviceRoot);

  @Override
  V3FilterFactory getFilterFactory();

  @Override
  ODataV3Deserializer getDeserializer();

  @Override
  V3RetrieveRequestFactory getRetrieveRequestFactory();

  @Override
  V3CUDRequestFactory getCUDRequestFactory();

  @Override
  V3StreamedRequestFactory getStreamedRequestFactory();

  @Override
  V3InvokeRequestFactory getInvokeRequestFactory();

  @Override
  V3BatchRequestFactory getBatchRequestFactory();
}
