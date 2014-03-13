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
package org.apache.olingo.client.core.communication.request.invoke;

import java.net.URI;
import org.apache.olingo.client.api.ODataV4Client;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.V4InvokeRequestFactory;
import org.apache.olingo.client.api.domain.ODataInvokeResult;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.edm.xml.v4.FunctionImport;

public class V4InvokeRequestFactoryImpl extends AbstractInvokeRequestFactory<FunctionImport>
        implements V4InvokeRequestFactory {

  private static final long serialVersionUID = 8452737360003104372L;

  public V4InvokeRequestFactoryImpl(final ODataV4Client client) {
    super(client);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
          final URI uri, final XMLMetadata metadata, final FunctionImport functionImport) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
