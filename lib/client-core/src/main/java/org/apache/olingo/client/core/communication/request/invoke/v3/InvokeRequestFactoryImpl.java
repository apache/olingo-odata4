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

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.request.invoke.v3.InvokeRequestFactory;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.domain.ODataInvokeResult;
import org.apache.olingo.client.api.domain.ODataProperty;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.invoke.AbstractInvokeRequestFactory;
import org.apache.olingo.client.core.communication.request.invoke.ODataInvokeRequestImpl;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

public class InvokeRequestFactoryImpl extends AbstractInvokeRequestFactory implements InvokeRequestFactory {

  private static final long serialVersionUID = -659256862901915496L;

  public InvokeRequestFactoryImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(final URI uri, final Edm edm,
          final FullQualifiedName container, final String functionImport,
          final LinkedHashMap<String, ODataValue> parameters) {

    final EdmEntityContainer edmContainer = edm.getEntityContainer(container);
    if (edmContainer == null) {
      throw new IllegalArgumentException("Could not find container " + container.toString());
    }

    final HttpMethod method;
    final EdmReturnType returnType;
    final EdmFunctionImport edmFunctionImport = edmContainer.getFunctionImport(functionImport);
    final EdmActionImport edmActionImport = edmContainer.getActionImport(functionImport);
    if (edmFunctionImport == null && edmActionImport == null) {
      throw new IllegalArgumentException("Could not find function import " + functionImport
              + " in the given container");
    } else if (edmFunctionImport == null) {
      final EdmAction action = edmActionImport.getAction();
      if (action == null) {
        throw new IllegalArgumentException("Could not find function import " + functionImport
                + " in the given container");
      }

      method = HttpMethod.POST;
      returnType = action.getReturnType();
    } else {
      final EdmFunction function = edmFunctionImport.getFunction(
              parameters == null ? null : (List<String>) parameters.keySet());
      if (function == null) {
        throw new IllegalArgumentException("Could not find function import " + functionImport
                + " in the given container");
      }

      method = HttpMethod.GET;
      returnType = function.getReturnType();
    }

    ODataInvokeRequest<RES> result;
    if (returnType == null) {
      result = (ODataInvokeRequest<RES>) new ODataInvokeRequestImpl<ODataNoContent>(
              client, ODataNoContent.class, method, uri);
    } else {
      if (returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (ODataInvokeRequest<RES>) new ODataInvokeRequestImpl<ODataEntitySet>(
                client, ODataEntitySet.class, method, uri);
      } else if (!returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (ODataInvokeRequest<RES>) new ODataInvokeRequestImpl<ODataEntity>(
                client, ODataEntity.class, method, uri);
      } else {
        result = (ODataInvokeRequest<RES>) new ODataInvokeRequestImpl<ODataProperty>(
                client, ODataProperty.class, method, uri);
      }
    }

    return result;
  }
}
