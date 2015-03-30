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

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PrimitiveValueResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;

public class ActionRequest extends OperationRequest {
  private UriResourceAction uriResourceAction;

  public ActionRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException {

    if (!allowedMethod()) {
      methodNotAllowed();
    }
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
      if (isReturnTypePrimitive()) {
        handler.invoke(this, getETag(),
            PrimitiveValueResponse.getInstance(this, response, isCollection(), getReturnType()));
      } else if (isReturnTypeComplex()) {
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
  public boolean allowedMethod() {
    // 11.5.4.1 Invoking an Action - only allows POST
    return (isPOST());
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
}