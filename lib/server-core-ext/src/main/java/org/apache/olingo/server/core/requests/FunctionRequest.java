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

import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ODataHandlerException;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.ServiceRequest;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;

public class FunctionRequest extends OperationRequest {
  private UriResourceFunction uriResourceFunction;

  public FunctionRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {

    // check for valid HTTP Verb
    assertHttpMethod(response);

    // Functions always have return per 11.5.3
    if (isReturnTypePrimitive() || isReturnTypeComplex()) {
      // per odata-json-format/v4.0 = 11 Individual Property or Operation Response
      handler.invoke(this, getODataRequest().getMethod(), PropertyResponse.getInstance(this, response, 
          getReturnType().getType(), getContextURL(this.odata), isCollection()));
    } else {
      // returnType.getType().getKind() == EdmTypeKind.ENTITY
      if (isCollection()) {
        handler.invoke(this, getODataRequest().getMethod(),
            EntitySetResponse.getInstance(this, getContextURL(odata), false, response));
      } else {
        handler.invoke(this, getODataRequest().getMethod(),
            EntityResponse.getInstance(this, getContextURL(odata), false, response));
      }
    }
  }

  @Override
  public boolean assertHttpMethod(ODataResponse response) throws ODataHandlerException {
    // look for discussion about composable functions in odata-discussion
    // group with thread "Clarification on "Function" invocations"
    if (getFunction().isComposable()) {
      boolean allowed =  (isGET() || isPATCH() || isDELETE() || isPOST() || isPUT());
      if (!allowed) {
        return methodNotAllowed(response,httpMethod(),
            "Only composable functions are allowed PATCH, DELETE, POST and PUT methods",
            allowedMethods());
      }
    }
    return ServiceRequest.assertHttpMethod(httpMethod(), allowedMethods(), response);
  }
  
  @Override
  public HttpMethod[] allowedMethods() {
    return new HttpMethod[] { HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
        HttpMethod.PATCH, HttpMethod.DELETE };
  }  

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getSerializerOptions(Class<T> serilizerOptions, ContextURL contextUrl, boolean references)
      throws ContentNegotiatorException {
    if (serilizerOptions.isAssignableFrom(PrimitiveSerializerOptions.class)) {
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
  
  public UriResourceFunction getUriResourceFunction() {
    return uriResourceFunction;
  }

  public void setUriResourceFunction(UriResourceFunction uriResourceFunction) {
    this.uriResourceFunction = uriResourceFunction;
  }

  @Override
  public boolean isBound() {
    return this.uriResourceFunction.getFunctionImport() != null;
  }

  public EdmFunction getFunction() {
    return this.uriResourceFunction.getFunction();
  }

  public List<UriParameter> getParameters() {
    return this.uriResourceFunction.getParameters();
  }

  @Override
  public boolean isCollection() {
    return getFunction().getReturnType().isCollection();
  }

  @Override
  public EdmReturnType getReturnType() {
    return getFunction().getReturnType();
  }

  @Override
  public boolean hasReturnType() {
    // Part3 {12.1} says must have return type
    return true;
  }
}