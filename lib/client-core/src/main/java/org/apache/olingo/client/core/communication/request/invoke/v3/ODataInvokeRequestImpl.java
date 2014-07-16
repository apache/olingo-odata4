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
package org.apache.olingo.client.core.communication.request.invoke.v3;

import org.apache.http.client.utils.URIBuilder;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.invoke.AbstractODataInvokeRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ODataInvokeRequestImpl<T extends ODataInvokeResult> extends AbstractODataInvokeRequest<T> {

  public ODataInvokeRequestImpl(final CommonODataClient<?> odataClient, final Class<T> reference,
          final HttpMethod method, final URI uri) {

    super(odataClient, reference, method, uri);
  }

  @Override
  protected ODataFormat getPOSTParameterFormat() {
    return ODataFormat.JSON;
  }

  @Override
  protected URI buildGETURI() {
    final URIBuilder uriBuilder = new URIBuilder(this.uri);
    for (Map.Entry<String, ODataValue> param : parameters.entrySet()) {
      if (!param.getValue().isPrimitive()) {
        throw new IllegalArgumentException("Only primitive values can be passed via GET");
      }

      uriBuilder.addParameter(param.getKey(), URIUtils.escape(odataClient.getServiceVersion(), param.getValue()));
    }

    try {
      return uriBuilder.build();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("While adding GET parameters", e);
    }
  }

}
