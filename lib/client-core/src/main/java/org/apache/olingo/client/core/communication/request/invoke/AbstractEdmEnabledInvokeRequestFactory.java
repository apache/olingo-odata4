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
import java.util.ArrayList;
import java.util.Map;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.communication.request.invoke.EdmEnabledInvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpMethod;

public abstract class AbstractEdmEnabledInvokeRequestFactory extends AbstractInvokeRequestFactory
        implements EdmEnabledInvokeRequestFactory {

  private final EdmEnabledODataClient edmClient;

  public AbstractEdmEnabledInvokeRequestFactory(final EdmEnabledODataClient edmClient) {
    this.edmClient = edmClient;
  }

  @Override
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getFunctionImportInvokeRequest(
          final String functionImportName) {

    return getFunctionImportInvokeRequest(functionImportName, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getFunctionImportInvokeRequest(
          final String functionImportName, final Map<String, ClientValue> parameters) {

    EdmFunctionImport efi = null;
    for (EdmSchema schema : edmClient.getCachedEdm().getSchemas()) {
      final EdmEntityContainer container = schema.getEntityContainer();
      if (container != null) {
        efi = container.getFunctionImport(functionImportName);
      }
    }
    if (efi == null) {
      throw new IllegalArgumentException("Could not find FunctionImport for name " + functionImportName);
    }

    final EdmFunction function = edmClient.getCachedEdm().
            getUnboundFunction(efi.getFunctionFqn(),
                    parameters == null ? null : new ArrayList<String>(parameters.keySet()));
    if (function == null) {
      throw new IllegalArgumentException("Could not find Function " + efi.getFunctionFqn());
    }

    return (ODataInvokeRequest<RES>) getInvokeRequest(HttpMethod.GET,
            edmClient.newURIBuilder().appendOperationCallSegment(functionImportName).build(),
            getResultReference(function.getReturnType()),
            parameters);
  }

  @Override
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getActionImportInvokeRequest(
          final String actionImportName) {

    return getActionImportInvokeRequest(actionImportName, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getActionImportInvokeRequest(
          final String actionImportName, final Map<String, ClientValue> parameters) {

    EdmActionImport eai = null;
    for (EdmSchema schema : edmClient.getCachedEdm().getSchemas()) {
      final EdmEntityContainer container = schema.getEntityContainer();
      if (container != null) {
        eai = container.getActionImport(actionImportName);
      }
    }
    if (eai == null) {
      throw new IllegalArgumentException("Could not find ActionImport for name " + actionImportName);
    }

    return (ODataInvokeRequest<RES>) getInvokeRequest(HttpMethod.POST,
            edmClient.newURIBuilder().appendOperationCallSegment(actionImportName).build(),
            getResultReference(eai.getUnboundAction().getReturnType()),
            parameters);
  }

  @Override
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getBoundFunctionInvokeRequest(
          final URI bindingParameterURI, final FullQualifiedName functionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    return getBoundFunctionInvokeRequest(
            bindingParameterURI, functionName, bindingParameterTypeName, isBindingParameterCollection, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getBoundFunctionInvokeRequest(
          final URI bindingParameterURI, final FullQualifiedName functionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection,
          final Map<String, ClientValue> parameters) {

    final EdmFunction function = edmClient.getCachedEdm().getBoundFunction(
            functionName, bindingParameterTypeName, isBindingParameterCollection,
            parameters == null ? null : new ArrayList<String>(parameters.keySet()));
    if (function == null) {
      throw new IllegalArgumentException("Could not find Function for name " + functionName);
    }

    return (ODataInvokeRequest<RES>) getInvokeRequest(HttpMethod.GET,
            edmClient.newURIBuilder(bindingParameterURI.toASCIIString()).
            appendOperationCallSegment(function.getFullQualifiedName().toString()).build(),
            getResultReference(function.getReturnType()),
            parameters);
  }

  @Override
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getBoundActionInvokeRequest(
          final URI bindingParameterURI, final FullQualifiedName actionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    return getBoundActionInvokeRequest(
            bindingParameterURI, actionName, bindingParameterTypeName, isBindingParameterCollection, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <RES extends ClientInvokeResult> ODataInvokeRequest<RES> getBoundActionInvokeRequest(
          final URI bindingParameterURI, final FullQualifiedName actionName,
          final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection,
          final Map<String, ClientValue> parameters) {

    final EdmAction action = edmClient.getCachedEdm().getBoundAction(
            actionName, bindingParameterTypeName, isBindingParameterCollection);
    if (action == null) {
      throw new IllegalArgumentException("Could not find Action for name " + actionName);
    }

    return (ODataInvokeRequest<RES>) getInvokeRequest(HttpMethod.POST,
            edmClient.newURIBuilder(bindingParameterURI.toASCIIString()).
            appendOperationCallSegment(action.getFullQualifiedName().toString()).build(),
            getResultReference(action.getReturnType()),
            parameters);
  }
}
