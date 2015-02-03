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
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;

public class EntitySetResponse extends ServiceResponse {
  private boolean referencesOnly;
  private final ODataSerializer serializer;
  private final EntityCollectionSerializerOptions options;
  private final ContentType responseContentType;

  private EntitySetResponse(ODataResponse response, ODataSerializer serializer,
      EntityCollectionSerializerOptions options,
      ContentType responseContentType) {
    super(response);
    this.serializer = serializer;
    this.options = options;
    this.responseContentType = responseContentType;
  }

  public static EntitySetResponse getInstance(ServiceRequest request, ContextURL contextURL,
      ODataResponse response) throws ContentNegotiatorException, SerializerException {
    EntityCollectionSerializerOptions options = request.getSerializerOptions(
        EntityCollectionSerializerOptions.class, contextURL);
    return new EntitySetResponse(response, request.getSerializer(), options,
        request.getResponseContentType());
  }

  // write collection of entities
  // TODO: server paging needs to be implemented.
  public void writeReadEntitySet(EdmEntityType entityType, EntitySet entitySet)
      throws SerializerException {

    assert (!isClosed());

    if (entitySet == null) {
      writeNotFound(true);
      return;
    }

    if (this.referencesOnly) {
      // TODO: need to write serializer routine to write only references,
      // i.e URL with ID
      writeServerError(true);
      return;
    }

    // write the whole collection to response
    this.response.setContent(this.serializer.entityCollection(entityType, entitySet, this.options));
    writeOK(this.responseContentType.toContentTypeString());
    close();
  }

  @Override
  public void accepts(ServiceResponseVisior visitor) throws ODataTranslatedException,
      ODataApplicationException {
    visitor.visit(this);
  }

  protected EntitySetResponse sendReferences(boolean ref) {
    this.referencesOnly = ref;
    return this;
  }

  protected EntitySetResponse setReferencesOnly(boolean referencesOnly) {
    this.referencesOnly = referencesOnly;
    return this;
  }
}
