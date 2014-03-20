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
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.request.invoke.v3.InvokeRequestFactory;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.domain.ODataInvokeResult;
import org.apache.olingo.client.api.domain.ODataJClientEdmType;
import org.apache.olingo.client.api.domain.ODataProperty;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.invoke.AbstractInvokeRequestFactory;
import org.apache.olingo.client.core.communication.request.invoke.ODataInvokeRequestImpl;

public class InvokeRequestFactoryImpl extends AbstractInvokeRequestFactory<FunctionImport>
        implements InvokeRequestFactory {

  private static final long serialVersionUID = -659256862901915496L;

  public InvokeRequestFactoryImpl(final ODataClient client) {
    super(client);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
          final URI uri,
          final XMLMetadata metadata,
          final FunctionImport functionImport) {

    HttpMethod method = null;
    if (HttpMethod.GET.name().equals(functionImport.getHttpMethod())) {
      method = HttpMethod.GET;
    } else if (HttpMethod.POST.name().equals(functionImport.getHttpMethod())) {
      method = HttpMethod.POST;
    } else if (functionImport.getHttpMethod() == null) {
      if (functionImport.isSideEffecting()) {
        method = HttpMethod.POST;
      } else {
        method = HttpMethod.GET;
      }
    }

    ODataInvokeRequest<RES> result;
    if (StringUtils.isBlank(functionImport.getReturnType())) {
      result = (ODataInvokeRequest<RES>) new ODataInvokeRequestImpl<ODataNoContent>(
              client, ODataNoContent.class, method, uri);
    } else {
      final ODataJClientEdmType returnType = new ODataJClientEdmType(metadata, functionImport.getReturnType());

      if (returnType.isCollection() && returnType.isEntityType()) {
        result = (ODataInvokeRequest<RES>) new ODataInvokeRequestImpl<ODataEntitySet>(
                client, ODataEntitySet.class, method, uri);
      } else if (!returnType.isCollection() && returnType.isEntityType()) {
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
