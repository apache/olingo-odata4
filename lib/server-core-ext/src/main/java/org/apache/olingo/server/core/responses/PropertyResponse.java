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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ServiceRequest;

public class PropertyResponse extends ServiceResponse {
  private PrimitiveSerializerOptions primitiveOptions;
  private ComplexSerializerOptions complexOptions;
  private final ContentType responseContentType;
  private final ODataSerializer serializer;
  private final boolean collection;

  public static PropertyResponse getInstance(ServiceRequest request, ODataResponse response,
      EdmType edmType, ContextURL contextURL, boolean collection) throws ContentNegotiatorException,
      SerializerException {
    if (edmType.getKind() == EdmTypeKind.PRIMITIVE) {
      PrimitiveSerializerOptions options = request.getSerializerOptions(
          PrimitiveSerializerOptions.class, contextURL, false);
      ContentType type = request.getResponseContentType();
      return new PropertyResponse(request.getServiceMetaData(), request.getSerializer(), response,
          options, type, collection, request.getPreferences());
    }
    ComplexSerializerOptions options = request.getSerializerOptions(ComplexSerializerOptions.class,
        contextURL, false);
    ContentType type = request.getResponseContentType();
    return new PropertyResponse(request.getServiceMetaData(), request.getSerializer(), response,
        options, type, collection, request.getPreferences());
  }

  private PropertyResponse(ServiceMetadata metadata, ODataSerializer serializer,
      ODataResponse response, PrimitiveSerializerOptions options, ContentType contentType,
      boolean collection, Map<String, String> preferences) {
    super(metadata, response, preferences);
    this.serializer = serializer;
    this.primitiveOptions = options;
    this.responseContentType = contentType;
    this.collection = collection;
  }

  private PropertyResponse(ServiceMetadata metadata, ODataSerializer serializer, ODataResponse response,
      ComplexSerializerOptions options, ContentType contentType, boolean collection,
      Map<String, String> preferences) {
    super(metadata, response, preferences);
    this.serializer = serializer;
    this.complexOptions = options;
    this.responseContentType = contentType;
    this.collection = collection;
  }

  public void writeProperty(EdmType edmType, Property property) throws SerializerException {
    assert (!isClosed());

    if (property == null) {
      writeNotFound(true);
      return;
    }

    if (property.getValue() == null) {
      writeNoContent(true);
      return;
    }

    if (edmType.getKind() == EdmTypeKind.PRIMITIVE) {
      writePrimitiveProperty((EdmPrimitiveType) edmType, property);
    } else {
      writeComplexProperty((EdmComplexType) edmType, property);
    }
  }

  private void writeComplexProperty(EdmComplexType type, Property property)
      throws SerializerException {
    if (this.collection) {
      this.response.setContent(this.serializer.complexCollection(this.metadata, type, property,
          this.complexOptions));
    } else {
      this.response.setContent(this.serializer.complex(this.metadata, type, property,
          this.complexOptions));
    }
    writeOK(this.responseContentType.toContentTypeString());
    close();
  }

  private void writePrimitiveProperty(EdmPrimitiveType type, Property property)
      throws SerializerException {
    if(this.collection) {
      this.response.setContent(this.serializer.primitiveCollection(type, property, this.primitiveOptions));
    } else {
      this.response.setContent(this.serializer.primitive(type, property, this.primitiveOptions));
    }
    writeOK(this.responseContentType.toContentTypeString());
    close();
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataTranslatedException,
      ODataApplicationException {
    visitor.visit(this);
  }

  public void writePropertyUpdated() {
    // spec says just success response; so either 200 or 204. 200 typically has
    // payload
    writeNoContent(true);
  }

  public void writePropertyDeleted() {
    writeNoContent(true);
  }
}
