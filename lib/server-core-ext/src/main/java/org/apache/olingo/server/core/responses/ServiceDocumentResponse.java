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

import java.util.Map;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ServiceRequest;

public class ServiceDocumentResponse extends ServiceResponse {
  private final ODataSerializer serializer;
  private final ContentType responseContentType;

  public static ServiceDocumentResponse getInstace(ServiceRequest request, ODataResponse respose,
      ContentType responseContentType) throws ContentNegotiatorException, SerializerException {
    return new ServiceDocumentResponse(request.getServiceMetaData(), respose,
        request.getSerializer(), responseContentType, request.getPreferences());
  }

  private ServiceDocumentResponse(ServiceMetadata metadata, ODataResponse respose,
      ODataSerializer serializer, ContentType responseContentType, Map<String, String> preferences) {
    super(metadata, respose, preferences);
    this.serializer = serializer;
    this.responseContentType = responseContentType;
  }

  public void writeServiceDocument(String serviceRoot)
      throws ODataLibraryException {
    assert (!isClosed());
    this.response.setContent(this.serializer.serviceDocument(metadata, serviceRoot).getContent());
    writeOK(responseContentType);
    close();
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataLibraryException,
      ODataApplicationException {
    visitor.visit(this);
  }
  
  public void writeError(ODataServerError error) {
    try {
      writeHeader(HttpHeader.CONTENT_TYPE, this.responseContentType.toContentTypeString());
      writeContent(this.serializer.error(error).getContent(), error.getStatusCode(), true);
    } catch (SerializerException e) {
      writeServerError(true);
    }
  }  
}
