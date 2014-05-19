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

import java.util.Map;
import org.apache.olingo.client.api.communication.request.invoke.EdmEnabledInvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.v3.EdmEnabledODataClient;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSchema;

public class EdmEnabledInvokeRequestFactoryImpl
        extends InvokeRequestFactoryImpl implements EdmEnabledInvokeRequestFactory {

  private static final long serialVersionUID = 5854571629835831697L;

  private final EdmEnabledODataClient edmClient;

  public EdmEnabledInvokeRequestFactoryImpl(final EdmEnabledODataClient client) {
    super(client);
    this.edmClient = client;
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getFunctionImportInvokeRequest(
          final String functionImportName) {

    return getFunctionImportInvokeRequest(functionImportName, null);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getFunctionImportInvokeRequest(
          final String functionImportName, final Map<String, ODataValue> parameters) {

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

    return getInvokeRequest(
            edmClient.getURIBuilder().appendOperationCallSegment(functionImportName).build(),
            efi.getUnboundFunctions().get(0),
            parameters);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getActionImportInvokeRequest(
          final String actionImportName) {

    return getActionImportInvokeRequest(actionImportName, null);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getActionImportInvokeRequest(
          final String actionImportName, final Map<String, ODataValue> parameters) {

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

    return getInvokeRequest(
            edmClient.getURIBuilder().appendOperationCallSegment(actionImportName).build(),
            eai.getUnboundAction(),
            parameters);
  }

}
