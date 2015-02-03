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
package org.apache.olingo.server.core;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

public class MediaRequest extends ServiceRequest {
  private UriResourceEntitySet uriResourceEntitySet;

  public MediaRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    if (!allowedMethod()) {
      methodNotAllowed();
    }
    // POST will not be here, because the media is created as part of media
    // entity creation
    if (isGET()) {
      handler.readMediaStream(this, new StreamResponse(response));
    } else if (isPUT()) {
      handler.updateMediaStream(this, getETag(), new StreamResponse(response));
    } else if (isDELETE()) {
      handler.deleteMediaStream(this, getETag(), new NoContentResponse(response));
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

  protected void setUriResourceEntitySet(UriResourceEntitySet uriResourceEntitySet) {
    this.uriResourceEntitySet = uriResourceEntitySet;
  }

  @Override
  public boolean allowedMethod() {
    return isGET() || isPUT() || isDELETE();
  }
}
