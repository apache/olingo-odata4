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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ServiceRequest.ReturnRepresentation;

public class EntityResponse extends ServiceResponse {
  private boolean referencesOnly;
  private ReturnRepresentation returnRepresentation;
  private boolean hasMediaStream;
  private final ODataSerializer serializer;
  private final EntitySerializerOptions options;
  private final ContentType responseContentType;

  private EntityResponse(ODataResponse response, ODataSerializer serializer,
      EntitySerializerOptions options, ContentType responseContentType) {
    super(response);
    this.serializer = serializer;
    this.options = options;
    this.responseContentType = responseContentType;
  }

  public static EntityResponse getInstance(ServiceRequest request, ContextURL contextURL,
      ODataResponse response) throws ContentNegotiatorException, SerializerException {
    EntitySerializerOptions options = request.getSerializerOptions(EntitySerializerOptions.class,
        contextURL);
    return new EntityResponse(response, request.getSerializer(), options,
        request.getResponseContentType());
  }

  // write single entity
  public void writeReadEntity(EdmEntityType entityType, Entity entity) throws SerializerException {

    assert (!isClosed());

    if (entity == null) {
      writeNotFound(true);
      return;
    }

    if (this.referencesOnly) {
      // TODO: need to write serializer routine to write only references,
      // i.e URL with ID
      writeServerError(true);
      return;
    }

    // write the entity to response
    this.response.setContent(this.serializer.entity(entityType, entity, this.options));
    writeOK(this.responseContentType.toContentTypeString());
    close();
  }

  public void writeCreatedEntity(EdmEntityType entityType, Entity entity, String locationHeader)
      throws SerializerException {
    // upsert/insert must created a entity, otherwise should have throw an
    // exception
    assert (entity != null);

    if (this.hasMediaStream) {
      // TODO: need to write serializer routine to write media context
      writeServerError(true);
      return;
    }

    // 8.2.8.7
    if (this.returnRepresentation == ReturnRepresentation.MINIMAL) {
      writeNoContent(false);
      writeHeader(HttpHeader.LOCATION, locationHeader);
      writeHeader("Preference-Applied", "return=minimal"); //$NON-NLS-1$ //$NON-NLS-2$
      // 8.3.3
      writeHeader("OData-EntityId", entity.getId().toASCIIString()); //$NON-NLS-1$
      close();
      return;
    }

    // return the content of the created entity
    this.response.setContent(this.serializer.entity(entityType, entity, this.options));
    writeCreated(false);
    writeHeader(HttpHeader.LOCATION, locationHeader);
    writeHeader("Preference-Applied", "return=representation"); //$NON-NLS-1$ //$NON-NLS-2$
    writeHeader(HttpHeader.CONTENT_TYPE, this.responseContentType.toContentTypeString());
    close();
  }

  public void writeUpdatedEntity() {
    // spec says just success response; so either 200 or 204. 200 typically has
    // payload
    writeNoContent(true);
  }

  public void writeDeletedEntityOrReference() {
    writeNoContent(true);
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataTranslatedException,
      ODataApplicationException {
    visitor.visit(this);
  }

  public void writeCreated(boolean closeResponse) {
    this.response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
    if (closeResponse) {
      close();
    }
  }

  protected EntityResponse sendReferences(boolean ref) {
    this.referencesOnly = ref;
    return this;
  }

  protected EntityResponse setReturnRepresentation(ReturnRepresentation returnRepresentation) {
    this.returnRepresentation = returnRepresentation;
    return this;
  }

  protected EntityResponse setReferencesOnly(boolean referencesOnly) {
    this.referencesOnly = referencesOnly;
    return this;
  }

  protected EntityResponse setHasMediaStream(boolean stream) {
    this.hasMediaStream = stream;
    return this;
  }
}
