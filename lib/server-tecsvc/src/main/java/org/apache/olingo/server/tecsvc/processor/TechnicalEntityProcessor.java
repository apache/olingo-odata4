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
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.ActionEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.processor.ActionVoidProcessor;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.data.EntityActionResult;
import org.apache.olingo.server.tecsvc.data.RequestValidator;
import org.apache.olingo.server.tecsvc.processor.queryoptions.ExpandSystemQueryOptionHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.CountHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.FilterHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.OrderByHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.ServerSidePagingHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.SkipHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.TopHandler;

/**
 * Technical Processor for entity-related functionality.
 */
public class TechnicalEntityProcessor extends TechnicalProcessor
    implements EntityCollectionProcessor, ActionEntityCollectionProcessor, CountEntityCollectionProcessor,
    EntityProcessor, ActionEntityProcessor, MediaEntityProcessor,
    ActionVoidProcessor {

  public TechnicalEntityProcessor(final DataProvider dataProvider, final ServiceMetadata serviceMetadata) {
    super(dataProvider, serviceMetadata);
  }

  @Override
  public void readEntityCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());

    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EdmEntityType edmEntityType = edmEntitySet == null ?
        (EdmEntityType) ((UriResourceFunction) uriInfo.getUriResourceParts()
            .get(uriInfo.getUriResourceParts().size() - 1)).getType() :
        edmEntitySet.getEntityType();

    final EntityCollection entitySetInitial = readEntityCollection(uriInfo);
    if (entitySetInitial == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      // Modifying the original entitySet means modifying the "database", so we have to make a shallow
      // copy of the entity set (new EntitySet, but exactly the same data)
      EntityCollection entitySet = new EntityCollection();
      entitySet.getEntities().addAll(entitySetInitial.getEntities());

      // Apply system query options
      FilterHandler.applyFilterSystemQuery(uriInfo.getFilterOption(), entitySet, edmEntitySet);
      CountHandler.applyCountSystemQueryOption(uriInfo.getCountOption(), entitySet);
      OrderByHandler.applyOrderByOption(uriInfo.getOrderByOption(), entitySet, edmEntitySet);
      SkipHandler.applySkipSystemQueryHandler(uriInfo.getSkipOption(), entitySet);
      TopHandler.applyTopSystemQueryOption(uriInfo.getTopOption(), entitySet);

      ServerSidePagingHandler.applyServerSidePaging(uriInfo.getSkipTokenOption(),
          entitySet,
          edmEntitySet,
          request.getRawRequestUri());

      // Apply expand system query option
      final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
      ODataSerializer serializer = odata.createSerializer(format);
      final ExpandOption expand = uriInfo.getExpandOption();
      final SelectOption select = uriInfo.getSelectOption();

      // Transform the entity graph to a tree. The construction is controlled by the expand tree.
      // Apply all expand system query options to the tree.So the expanded navigation properties can be modified
      // for serialization,without affecting the data stored in the database.
      final ExpandSystemQueryOptionHandler expandHandler = new ExpandSystemQueryOptionHandler();
      final EntityCollection entitySetSerialization = expandHandler.transformEntitySetGraphToTree(entitySet,
          edmEntitySet,
          expand);
      expandHandler.applyExpandQueryOptions(entitySetSerialization, edmEntitySet, expand);

      // Serialize
      response.setContent(serializer.entityCollection(
          serviceMetadata,
          edmEntityType,
          entitySetSerialization,
          EntityCollectionSerializerOptions.with()
              .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                  getContextUrl(edmEntitySet, edmEntityType, false, expand, select))
              .count(uriInfo.getCountOption())
              .expand(expand).select(select)
              .build()).getContent());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    }
  }

  @Override
  public void processActionEntityCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    EdmAction action = checkBoundAndExtractAction(uriInfo);
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
        .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
            getContextUrl(edmEntitySet, type, false, null, null))
        .build();
    response.setContent(odata.createSerializer(format)
        .entityCollection(serviceMetadata, type, collection, options).getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  @Override
  public void countEntityCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo); // including checks
    final EntityCollection entitySetInitial = readEntityCollection(uriInfo);
    if (entitySetInitial == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      EntityCollection entitySet = new EntityCollection();
      entitySet.getEntities().addAll(entitySetInitial.getEntities());
      FilterHandler.applyFilterSystemQuery(uriInfo.getFilterOption(), entitySet, edmEntitySet);
      response.setContent(odata.createFixedFormatSerializer().count(
          entitySet.getEntities().size()));
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, HttpContentType.TEXT_PLAIN);
    }
  }

  @Override
  public void readEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet == null ?
        (EdmEntityType) ((UriResourceFunction) uriInfo.getUriResourceParts()
            .get(uriInfo.getUriResourceParts().size() - 1)).getType() :
        edmEntitySet.getEntityType();

    final Entity entity = readEntity(uriInfo);

    final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
    final ExpandOption expand = uriInfo.getExpandOption();
    final SelectOption select = uriInfo.getSelectOption();

    final ExpandSystemQueryOptionHandler expandHandler = new ExpandSystemQueryOptionHandler();
    final Entity entitySerialization = expandHandler.transformEntityGraphToTree(entity, edmEntitySet, expand);
    expandHandler.applyExpandQueryOptions(entitySerialization, edmEntitySet, expand);

    response.setContent(serializeEntity(entitySerialization, edmEntitySet, edmEntityType, format, expand, select)
        .getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
  }

  @Override
  public void readMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType responseFormat) throws ODataApplicationException, SerializerException {
    getEdmEntitySet(uriInfo); // including checks
    final Entity entity = readEntity(uriInfo);
    response.setContent(odata.createFixedFormatSerializer().binary(dataProvider.readMedia(entity)));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, entity.getMediaContentType());
  }

  @Override
  public void createMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    createEntity(request, response, uriInfo, requestFormat, responseFormat);
  }

  @Override
  public void createEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    if (uriInfo.asUriInfoResource().getUriResourceParts().size() > 1) {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    checkRequestFormat(requestFormat);
    final UriResourceEntitySet resourceEntitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
    final EdmEntitySet edmEntitySet = resourceEntitySet.getEntitySet();
    final EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    final Entity entity;
    ExpandOption expand = null;
    if (edmEntityType.hasStream()) { // called from createMediaEntity(...), not directly
      entity = dataProvider.create(edmEntitySet);
      dataProvider.setMedia(entity, odata.createFixedFormatDeserializer().binary(request.getBody()),
          requestFormat.toContentTypeString());
    } else {
      final DeserializerResult deserializerResult =
          odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
              .entity(request.getBody(), edmEntityType);
      new RequestValidator(dataProvider,
          odata.createUriHelper(),
          serviceMetadata.getEdm(),
          request.getRawBaseUri()).validate(edmEntitySet, deserializerResult.getEntity());

      entity = dataProvider.create(edmEntitySet);
      dataProvider.update(request.getRawBaseUri(), edmEntitySet, entity, deserializerResult.getEntity(), false, true);
      expand = deserializerResult.getExpandTree();
    }

    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    response.setContent(serializeEntity(entity, edmEntitySet, edmEntityType, format, expand, null)
        .getContent());
    response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    response.setHeader(HttpHeader.LOCATION,
        request.getRawBaseUri() + '/' + odata.createUriHelper().buildCanonicalURL(edmEntitySet, entity));
  }

  @Override
  public void updateEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    Entity entity;

    try {
      entity = readEntity(uriInfo);
    } catch (ODataApplicationException e) {
      if (e.getStatusCode() == HttpStatusCode.NOT_FOUND.getStatusCode()) {
        // Perform upsert
        createEntity(request, response, uriInfo, requestFormat, responseFormat);
        return;
      } else {
        throw e;
      }
    }
    checkRequestFormat(requestFormat);
    final ODataDeserializer deserializer = odata.createDeserializer(ODataFormat.fromContentType(requestFormat));
    final Entity changedEntity = deserializer.entity(request.getBody(), edmEntitySet.getEntityType()).getEntity();

    new RequestValidator(dataProvider,
        true, // Update
        request.getMethod() == HttpMethod.PATCH,
        odata.createUriHelper(),
        serviceMetadata.getEdm(),
        request.getRawBaseUri()).validate(edmEntitySet, changedEntity);

    dataProvider.update(request.getRawBaseUri(), edmEntitySet, entity, changedEntity,
        request.getMethod() == HttpMethod.PATCH, false);

    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    response.setContent(serializeEntity(entity, edmEntitySet, edmEntityType, format, null, null)
        .getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  @Override
  public void updateMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    Entity entity = readEntity(uriInfo);
    checkRequestFormat(requestFormat);
    dataProvider.setMedia(entity, odata.createFixedFormatDeserializer().binary(request.getBody()),
        requestFormat.toContentTypeString());

    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    response.setContent(serializeEntity(entity, edmEntitySet, edmEntityType, format, null, null)
        .getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  @Override
  public void deleteEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final Entity entity = readEntity(uriInfo);
    dataProvider.delete(edmEntitySet, entity);
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void processActionEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    final EdmAction action = checkBoundAndExtractAction(uriInfo);
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
      response.setContent(serializeEntity(entityResult.getEntity(), edmEntitySet, type, format, null, null)
          .getContent());
      response.setStatusCode((entityResult.isCreated() ? HttpStatusCode.CREATED : HttpStatusCode.OK).getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }
  }

  @Override
  public void processActionVoid(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat) throws ODataApplicationException, DeserializerException {
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

  private void checkRequestFormat(final ContentType requestFormat) throws ODataApplicationException {
    if (requestFormat == null) {
      throw new ODataApplicationException("The content type has not been set in the request.",
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  private ContextURL getContextUrl(final EdmEntitySet entitySet, final EdmEntityType entityType,
      final boolean isSingleEntity, final ExpandOption expand, final SelectOption select) throws SerializerException {
    Builder builder = ContextURL.with();
    builder = entitySet == null ?
        isSingleEntity ? builder.type(entityType) : builder.asCollection().type(entityType) :
        builder.entitySet(entitySet);
    builder = builder
        .selectList(odata.createUriHelper().buildContextURLSelectList(entityType, expand, select))
        .suffix(isSingleEntity && entitySet != null ? Suffix.ENTITY : null);
    return builder.build();
  }

  private SerializerResult serializeEntity(final Entity entity,
      final EdmEntitySet edmEntitySet, final EdmEntityType edmEntityType, final ODataFormat format,
      final ExpandOption expand, final SelectOption select) throws SerializerException {
    return odata.createSerializer(format).entity(
        serviceMetadata,
        edmEntityType,
        entity,
        EntitySerializerOptions.with()
            .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                getContextUrl(edmEntitySet, edmEntityType, true, expand, select))
            .expand(expand).select(select)
            .build());
  }
}
