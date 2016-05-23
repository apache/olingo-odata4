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

import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ServiceRequest;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;

public class EntitySetResponse extends ServiceResponse {
  private final ODataSerializer serializer;
  private final EntityCollectionSerializerOptions options;
  private final ContentType responseContentType;

  private EntitySetResponse(ServiceMetadata metadata, ODataResponse response, ODataSerializer serializer,
      EntityCollectionSerializerOptions options,
      ContentType responseContentType, Map<String, String> preferences) {
    super(metadata, response, preferences);
    this.serializer = serializer;
    this.options = options;
    this.responseContentType = responseContentType;
  }

  public static EntitySetResponse getInstance(ServiceRequest request, ContextURL contextURL,
      boolean referencesOnly, ODataResponse response) throws ContentNegotiatorException, SerializerException {
    EntityCollectionSerializerOptions options = request.getSerializerOptions(
        EntityCollectionSerializerOptions.class, contextURL, referencesOnly);
    return new EntitySetResponse(request.getServiceMetaData(),response, request.getSerializer(), options,
        request.getResponseContentType(), request.getPreferences());
  }

  // write collection of entities
  // TODO: server paging needs to be implemented.
  public void writeReadEntitySet(EdmEntityType entityType, EntityCollection entitySet)
      throws SerializerException {

    assert (!isClosed());

    if (entitySet == null) {
      writeNotFound(true);
      return;
    }

    if (ContentTypeHelper.isODataMetadataFull(this.responseContentType)) {
      buildOperations(entityType, entitySet);      
    }    
    // write the whole collection to response
    this.response.setContent(this.serializer.entityCollection(metadata, entityType, entitySet, this.options)
                                            .getContent());
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
    
  private void buildOperations(EdmEntityType entityType,
      EntityCollection entitySet) {
    EdmAction action = this.metadata.getEdm().getBoundActionWithBindingType(
        entityType.getFullQualifiedName(), true);
    if (action != null) {
      entitySet.getOperations().add(buildOperation(action, buildOperationTarget(options.getContextURL())));
    }
    
    action = this.metadata.getEdm().getBoundActionWithBindingType(
        entityType.getFullQualifiedName(), false);
    if (action != null) {
      for (Entity entity:entitySet.getEntities()) {
        entity.getOperations().add(buildOperation(action, entity.getId().toASCIIString()));
      }
    }      
    
    List<EdmFunction> functions = this.metadata.getEdm()
        .getBoundFunctionsWithBindingType(entityType.getFullQualifiedName(),true);
    
    for (EdmFunction function:functions) {
      entitySet.getOperations().add(buildOperation(function, buildOperationTarget(options.getContextURL())));
    }
    
    functions = this.metadata.getEdm()
        .getBoundFunctionsWithBindingType(entityType.getFullQualifiedName(),false);
    
    for (Entity entity:entitySet.getEntities()) {
      for (EdmFunction function:functions) {
        entity.getOperations().add(buildOperation(function, entity.getId().toASCIIString()));
      }
    }
  }
  
  private String buildOperationTarget(ContextURL contextURL) {
    StringBuilder result = new StringBuilder();
    if (contextURL.getServiceRoot() != null) {
      result.append(contextURL.getServiceRoot());
    }
    if (contextURL.getEntitySetOrSingletonOrType() != null) {
      if (result.length() != 0) {
        result.append("/");
      }
      result.append(Encoder.encode(contextURL.getEntitySetOrSingletonOrType()));
    }
    if (contextURL.getKeyPath() != null) {
      result.append('(').append(contextURL.getKeyPath()).append(')');
    }    
    if (contextURL.getNavOrPropertyPath() != null) {
      result.append('/').append(contextURL.getNavOrPropertyPath());
    }
    return result.toString();
  }
}
