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
package org.apache.olingo.server.tecsvc.processor;

import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Builder;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.processor.ActionComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionComplexProcessor;
import org.apache.olingo.server.api.processor.ActionEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.processor.ActionVoidProcessor;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.data.EntityActionResult;

/**
 * Technical Processor for entity-related functionality.
 */
public class TechnicalActionProcessor extends TechnicalProcessor
    implements ActionEntityCollectionProcessor, ActionEntityProcessor,
    ActionPrimitiveCollectionProcessor, ActionPrimitiveProcessor,
    ActionComplexCollectionProcessor, ActionComplexProcessor,
    ActionVoidProcessor {

  public TechnicalActionProcessor(final DataProvider dataProvider, final ServiceMetadata serviceMetadata) {
    super(dataProvider, serviceMetadata);
  }

  @Override
  public void processActionEntityCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    blockBoundActions(uriInfo);
    final EdmAction action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
        .getAction();

    DeserializerResult deserializerResult =
        odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
            .actionParameters(request.getBody(), action);

    EntityCollection collection =
        dataProvider.processActionEntityCollection(action.getName(), deserializerResult.getActionParameters());

    // Collections must never be null.
    // Not nullable return types must not contain a null value.
    if (collection == null
        || collection.getEntities().contains(null) && !action.getReturnType().isNullable()) {
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EdmEntityType type = (EdmEntityType) action.getReturnType().getType();
    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions.with()
        .contextURL(format == ODataFormat.JSON_NO_METADATA ? null : getContextUrl(edmEntitySet, type, false))
        .build();
    response.setContent(odata.createSerializer(format)
        .entityCollection(serviceMetadata, type, collection, options).getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  @Override
  public void processActionEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    blockBoundActions(uriInfo);
    final EdmAction action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
        .getAction();
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EdmEntityType type = (EdmEntityType) action.getReturnType().getType();

    final DeserializerResult deserializerResult =
        odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
            .actionParameters(request.getBody(), action);

    final EntityActionResult entityResult =
        dataProvider.processActionEntity(action.getName(), deserializerResult.getActionParameters());
    if (entityResult == null || entityResult.getEntity() == null) {
      if (action.getReturnType().isNullable()) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      } else {
        // Not nullable return type so we have to give back a 500
        throw new ODataApplicationException("The action could not be executed.",
            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
      }
    } else {
      final ODataFormat format = ODataFormat.fromContentType(responseFormat);
      response.setContent(odata.createSerializer(format).entity(
          serviceMetadata,
          type,
          entityResult.getEntity(),
          EntitySerializerOptions.with()
              .contextURL(format == ODataFormat.JSON_NO_METADATA ? null : getContextUrl(edmEntitySet, type, true))
              .build())
          .getContent());
      response.setStatusCode((entityResult.isCreated() ? HttpStatusCode.CREATED : HttpStatusCode.OK)
          .getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
      if (entityResult.getEntity().getETag() != null) {
        response.setHeader(HttpHeader.ETAG, entityResult.getEntity().getETag());
      }
    }
  }

  @Override
  public void processActionPrimitiveCollection(final ODataRequest request, ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    blockBoundActions(uriInfo);
    final EdmAction action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
        .getAction();
    DeserializerResult deserializerResult =
        odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
            .actionParameters(request.getBody(), action);

    Property property =
        dataProvider.processActionPrimitiveCollection(action.getName(), deserializerResult.getActionParameters());

    if (property == null || property.isNull()) {
      // Collection Propertys must never be null
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    } else if (property.asCollection().contains(null) && !action.getReturnType().isNullable()) {
      // Not nullable return type but array contains a null value
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
    EdmPrimitiveType type = (EdmPrimitiveType) action.getReturnType().getType();
    ContextURL contextURL = ContextURL.with().type(type).asCollection().build();
    PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextURL).build();

    SerializerResult result =
        odata.createSerializer(ODataFormat.fromContentType(responseFormat))
            .primitiveCollection(serviceMetadata, type, property, options);

    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setContent(result.getContent());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  @Override
  public void processActionPrimitive(final ODataRequest request, ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    blockBoundActions(uriInfo);
    final EdmAction action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
        .getAction();
    DeserializerResult deserializerResult =
        odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
            .actionParameters(request.getBody(), action);

    Property property = dataProvider.processActionPrimitive(action.getName(), deserializerResult.getActionParameters());
    EdmPrimitiveType type = (EdmPrimitiveType) action.getReturnType().getType();
    if (property == null || property.isNull()) {
      if (action.getReturnType().isNullable()) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      } else {
        // Not nullable return type so we have to give back an Internal Server Error
        throw new ODataApplicationException("The action could not be executed.",
            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
      }
    } else {
      ContextURL contextURL = ContextURL.with().type(type).build();
      PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextURL).build();

      SerializerResult result = odata.createSerializer(ODataFormat.fromContentType(responseFormat))
          .primitive(serviceMetadata, type, property, options);

      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setContent(result.getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
  }

  @Override
  public void processActionComplexCollection(final ODataRequest request, ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    blockBoundActions(uriInfo);
    final EdmAction action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
        .getAction();
    DeserializerResult deserializerResult =
        odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
            .actionParameters(request.getBody(), action);

    Property property =
        dataProvider.processActionComplexCollection(action.getName(), deserializerResult.getActionParameters());

    if (property == null || property.isNull()) {
      // Collection Propertys must never be null
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    } else if (property.asCollection().contains(null) && !action.getReturnType().isNullable()) {
      // Not nullable return type but array contains a null value
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
    EdmComplexType type = (EdmComplexType) action.getReturnType().getType();
    ContextURL contextURL = ContextURL.with().type(type).asCollection().build();
    ComplexSerializerOptions options = ComplexSerializerOptions.with().contextURL(contextURL).build();

    SerializerResult result =
        odata.createSerializer(ODataFormat.fromContentType(responseFormat)).complexCollection(serviceMetadata, type,
            property, options);

    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setContent(result.getContent());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  @Override
  public void processActionComplex(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    blockBoundActions(uriInfo);
    final EdmAction action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
        .getAction();
    DeserializerResult deserializerResult =
        odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
            .actionParameters(request.getBody(), action);

    Property property = dataProvider.processActionComplex(action.getName(), deserializerResult.getActionParameters());
    EdmComplexType type = (EdmComplexType) action.getReturnType().getType();
    if (property == null || property.isNull()) {
      if (action.getReturnType().isNullable()) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      } else {
        // Not nullable return type so we have to give back an Internal Server Error
        throw new ODataApplicationException("The action could not be executed.",
            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
      }
    } else {
      ContextURL contextURL = ContextURL.with().type(type).build();
      ComplexSerializerOptions options = ComplexSerializerOptions.with().contextURL(contextURL).build();

      SerializerResult result =
          odata.createSerializer(ODataFormat.fromContentType(responseFormat)).complex(serviceMetadata, type, property,
              options);

      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setContent(result.getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
  }

  @Override
  public void processActionVoid(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat) throws ODataApplicationException, ODataLibraryException {
    final UriResourceAction resource =
        ((UriResourceAction) uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1));
    final EdmAction action = resource.getAction();
    if (action.getParameterNames().size() - (action.isBound() ? 1 : 0) > 0) {
      checkRequestFormat(requestFormat);
      odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
          .actionParameters(request.getBody(), action);
    }
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  private ContextURL getContextUrl(final EdmEntitySet entitySet, final EdmEntityType entityType,
      final boolean isSingleEntity) throws ODataLibraryException {
    Builder builder = ContextURL.with();
    builder = entitySet == null ?
        isSingleEntity ? builder.type(entityType) : builder.asCollection().type(entityType) :
        builder.entitySet(entitySet);
    builder = builder.suffix(isSingleEntity && entitySet != null ? Suffix.ENTITY : null);
    return builder.build();
  }
}
