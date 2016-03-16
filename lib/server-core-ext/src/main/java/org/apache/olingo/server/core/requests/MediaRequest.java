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
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.ServiceRequest;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.StreamResponse;

public class MediaRequest extends ServiceRequest {
  private UriResourceEntitySet uriResourceEntitySet;

  public MediaRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {
    
    // check for valid HTTP Verb
    assertHttpMethod(response);
    
    // POST will not be here, because the media is created as part of media
    // entity creation
    if (isGET()) {
      handler.readMediaStream(this, new StreamResponse(getServiceMetaData(), response));
    } else if (isPUT()) {
      handler.upsertMediaStream(this, getETag(), getMediaStream(), new NoContentResponse(
          getServiceMetaData(), response));
    } else if (isDELETE()) {
      handler.upsertMediaStream(this, getETag(), null, new NoContentResponse(getServiceMetaData(),
          response));
    }
  }

  @Override
  public ContentType getResponseContentType() throws ContentNegotiatorException {
    // the request must specify the content type requested.
    return getRequestContentType();
  }

  public EdmEntitySet getEntitySet() {
    return this.uriResourceEntitySet.getEntitySet();
  }

  public EdmEntityType getEntityType() {
    return this.uriResourceEntitySet.getEntitySet().getEntityType();
  }

  public void setUriResourceEntitySet(UriResourceEntitySet uriResourceEntitySet) {
    this.uriResourceEntitySet = uriResourceEntitySet;
  }

  public List<UriParameter> getKeyPredicates() {
    if (this.uriResourceEntitySet != null) {
      return this.uriResourceEntitySet.getKeyPredicates();
    }
    return null;
  }

  private InputStream getMediaStream() {
    return this.request.getBody();
  }
  
  @Override
  public HttpMethod[] allowedMethods() {
    return new HttpMethod[] { HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE };
  }   
}
