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

import org.apache.olingo.client.api.communication.request.invoke.InvokeRequestFactory;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInvokeResult;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

import java.net.URI;
import java.util.Map;

public abstract class AbstractInvokeRequestFactory implements InvokeRequestFactory {

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getFunctionInvokeRequest(
          final URI uri, final Class<RES> resultRef) {

    return getFunctionInvokeRequest(uri, resultRef, null);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getFunctionInvokeRequest(
          final URI uri, final Class<RES> resultRef, final Map<String, ODataValue> parameters) {

    return getInvokeRequest(HttpMethod.GET, uri, resultRef, parameters);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getActionInvokeRequest(
          final URI uri, final Class<RES> resultRef) {

    return getActionInvokeRequest(uri, resultRef, null);
  }

  @Override
  public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getActionInvokeRequest(
          final URI uri, final Class<RES> resultRef, final Map<String, ODataValue> parameters) {

    return getInvokeRequest(HttpMethod.POST, uri, resultRef, parameters);
  }

  @SuppressWarnings("unchecked")
  protected <RES extends ODataInvokeResult> Class<RES> getResultReference(final EdmReturnType returnType) {
    Class<RES> result;

    if (returnType == null) {
      result = (Class<RES>) ODataNoContent.class;
    } else {
      if (returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (Class<RES>) CommonODataEntitySet.class;
      } else if (!returnType.isCollection() && returnType.getType().getKind() == EdmTypeKind.ENTITY) {
        result = (Class<RES>) CommonODataEntity.class;
      } else {
        result = (Class<RES>) CommonODataProperty.class;
      }
    }

    return result;
  }
}
