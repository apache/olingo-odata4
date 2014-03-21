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
package org.apache.olingo.client.core.communication.request.invoke.v4;

import java.net.URI;
import java.util.LinkedHashMap;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.v4.InvokeRequestFactory;
import org.apache.olingo.client.api.domain.ODataInvokeResult;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.client.core.communication.request.invoke.AbstractInvokeRequestFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class InvokeRequestFactoryImpl extends AbstractInvokeRequestFactory implements InvokeRequestFactory {

  private static final long serialVersionUID = 8452737360003104372L;

  public InvokeRequestFactoryImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(final URI uri, final Edm edm,
          final FullQualifiedName container, final String functionImport,
          final LinkedHashMap<String, ODataValue> parameters) {

    throw new NotImplementedException("Not available yet.");
  }
}
