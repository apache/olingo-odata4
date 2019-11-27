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

package org.apache.olingo.server.core.responses;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;

public abstract class ServiceResponse {
  protected ServiceMetadata metadata;
  protected ODataResponse response;
  protected Map<String, String> preferences;
  private boolean closed;
  private boolean strictApplyPreferences = true;

  public ServiceResponse(ServiceMetadata metadata, ODataResponse response,
      Map<String, String> preferences) {
    this.metadata = metadata;
    this.response = response;
    this.preferences = preferences;
  }

  public ODataResponse getODataResponse() {
    return this.response;
  }

  protected boolean isClosed() {
    return this.closed;
  }

  protected void close() {
    if (!this.closed) {
      if (this.strictApplyPreferences && !preferences.isEmpty()) {
        assert (this.response.getAllHeaders().get("Preference-Applied") != null);
      }
      this.closed = true;
    }
  }

  public void writeNoContent(boolean closeResponse) {
    this.response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeNotFound(boolean closeResponse) {
    response.setStatusCode(HttpStatusCode.NOT_FOUND.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeServerError(boolean closeResponse) {
    response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeBadRequest(boolean closeResponse) {
    response.setStatusCode(HttpStatusCode.BAD_REQUEST.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  public void writeOK(ContentType contentType) {
    this.response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    this.response.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
  }

  public void writeHeader(String key, String value) {
    if ("Preference-Applied".equals(key)) {
      String previous = this.response.getHeader(key);
      if (previous != null) {
        value = previous+";"+value;
      }
      this.response.setHeader(key, value);
    } else {
      this.response.setHeader(key, value);
    }
  }
  
  public void writeContent(InputStream content, int statusCode, boolean closeResponse) {
    this.response.setContent(content);
    this.response.setStatusCode(statusCode);
    if (closeResponse) {
      close();
    }    
  }

  /**
   * When true; the "Preference-Applied" header is strictly checked.
   * @param flag
   */
  public void setStrictlyApplyPreferences(boolean flag) {
    this.strictApplyPreferences = flag;
  }

  public abstract void accepts(ServiceResponseVisior visitor) throws ODataLibraryException,
      ODataApplicationException;
  
  protected static Operation buildOperation(EdmFunction function, String id) {
    String fqn = function.getFullQualifiedName().getFullQualifiedNameAsString();
    Operation operation = new Operation();          
    operation.setType(Operation.Type.FUNCTION);
    operation.setTitle(fqn);
    StringBuilder params = new StringBuilder();
    StringBuilder nameFQN = new StringBuilder();
    params.append(fqn);
    nameFQN.append(fqn);
    if (!function.getParameterNames().isEmpty() && function.getParameterNames().size() > 1) {
      params.append("(");
      nameFQN.append("(");
      boolean first = true;
      for (int i = 1; i < function.getParameterNames().size(); i++) {
        String parameterName = function.getParameterNames().get(i);
        EdmParameter p = function.getParameter(parameterName);
        if (first) {
          first = false;
        } else {
          params.append(",");
          nameFQN.append(",");                
        }
        params.append(p.getName()).append("=").append("@").append(p.getName());
        nameFQN.append(p.getName());
      }            
      params.append(")");
      nameFQN.append(")");
    }
    operation.setMetadataAnchor("#"+nameFQN);
    operation.setTarget(URI.create(id+"/"+params.toString()));
    return operation;
  }

  protected Operation buildOperation(EdmAction action, String id) {
    String fqn = action.getFullQualifiedName().getFullQualifiedNameAsString();
    Operation operation = new Operation();
    operation.setMetadataAnchor("#"+fqn);
    operation.setType(Operation.Type.ACTION);
    operation.setTitle(fqn);
    operation.setTarget(URI.create(id+"/"+fqn));
    return operation;
  }  
}
