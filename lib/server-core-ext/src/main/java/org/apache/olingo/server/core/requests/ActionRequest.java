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

package org.apache.olingo.server.core.requests;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;

public class ActionRequest extends OperationRequest {
  private UriResourceAction uriResourceAction;

  public ActionRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {

    // check for valid HTTP Verb
    assertHttpMethod(response);
    
    // Actions MAY return data but MUST NOT be further composed with additional
    // path segments.
    // On success, the response is 201 Created for actions that create entities,
    // 200 OK for actions
    // that return results or 204 No Content for action without a return type.
    // The client can request
    // whether any results from the action be returned using the Prefer header.

    if (!hasReturnType()) {
      handler.invoke(this, getETag(), new NoContentResponse(getServiceMetaData(), response));
    } else {
      if (isReturnTypePrimitive() || isReturnTypeComplex()) {
        handler.invoke(this, getETag(), PropertyResponse.getInstance(this, response,
            getReturnType().getType(), getContextURL(this.odata), isCollection()));
      } else {
        // EdmTypeKind.ENTITY
        if (isCollection()) {
          handler.invoke(this, getETag(),
              EntitySetResponse.getInstance(this, getContextURL(odata), false, response));
        } else {
          handler.invoke(this, getETag(),
              EntityResponse.getInstance(this, getContextURL(odata), false, response));
        }
      }
    }
  }

  @Override
  public HttpMethod[] allowedMethods() {
    // 11.5.4.1 Invoking an Action - only allows POST
    return new HttpMethod[] {HttpMethod.POST};
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getSerializerOptions(Class<T> serilizerOptions, ContextURL contextUrl, boolean references)
      throws ContentNegotiatorException {
    if (hasReturnType() && serilizerOptions.isAssignableFrom(PrimitiveSerializerOptions.class)) {
      return (T) PrimitiveSerializerOptions.with().contextURL(contextUrl)
          .nullable(getReturnType().isNullable())
          .maxLength(getReturnType().getMaxLength())
          .precision(getReturnType().getPrecision())
          .scale(getReturnType().getScale())
          .unicode(null)
          .build();
    }
    return super.getSerializerOptions(serilizerOptions, contextUrl, references);
  }  

  public UriResourceAction getUriResourceAction() {
    return uriResourceAction;
  }

  public void setUriResourceAction(UriResourceAction uriResourceAction) {
    this.uriResourceAction = uriResourceAction;
  }

  @Override
  public boolean isBound() {
    return this.uriResourceAction.getActionImport() != null;
  }

  public EdmAction getAction() {
    return this.uriResourceAction.getAction();
  }

  @Override
  public boolean isCollection() {
    assert (hasReturnType());
    return getAction().getReturnType().isCollection();
  }

  @Override
  public EdmReturnType getReturnType() {
    assert (hasReturnType());
    return getAction().getReturnType();
  }

  @Override
  public boolean hasReturnType() {
    return getAction().getReturnType() != null;
  }
  
  public InputStream getPayload() {
    return getODataRequest().getBody();
  }
  
  public List<Parameter> getParameters() throws DeserializerException {
    ODataDeserializer deserializer = odata.createDeserializer(getRequestContentType(), this.serviceMetadata);
    return new ArrayList<Parameter>(deserializer.actionParameters(getPayload(), getAction()).getActionParameters()
        .values());
  }
}