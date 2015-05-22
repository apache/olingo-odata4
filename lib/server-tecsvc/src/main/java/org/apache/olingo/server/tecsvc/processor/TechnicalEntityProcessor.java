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

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Builder;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
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
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.processor.ReferenceCollectionProcessor;
import org.apache.olingo.server.api.processor.ReferenceProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceValue;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.IdOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.tecsvc.data.DataProvider;
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
    implements EntityCollectionProcessor, CountEntityCollectionProcessor, EntityProcessor, MediaEntityProcessor,
    ReferenceCollectionProcessor, ReferenceProcessor {

  public TechnicalEntityProcessor(final DataProvider dataProvider, final ServiceMetadata serviceMetadata) {
    super(dataProvider, serviceMetadata);
  }

  @Override
  public void readEntityCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());

    readEntityCollection(request, response, uriInfo, requestedContentType, false);
  }

  @Override
  public void countEntityCollection(final ODataRequest request, final ODataResponse response, final UriInfo
      uriInfo)
      throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo); // including checks
    final EntityCollection entitySetInitial = readEntityCollection(uriInfo);
    EntityCollection entitySet = new EntityCollection();
    
    entitySet.getEntities().addAll(entitySetInitial.getEntities());
    FilterHandler.applyFilterSystemQuery(uriInfo.getFilterOption(), entitySet, edmEntitySet);
    response.setContent(odata.createFixedFormatSerializer().count(
        entitySet.getEntities().size()));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, HttpContentType.TEXT_PLAIN);
  }

  @Override
  public void readEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());

    readEntity(request, response, uriInfo, requestedContentType, false);
  }

  @Override
  public void readMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType responseFormat) throws ODataApplicationException, SerializerException {
    getEdmEntitySet(uriInfo); // including checks
    final Entity entity = readEntity(uriInfo);
    
    response.setContent(odata.createFixedFormatSerializer().binary(dataProvider.readMedia(entity)));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, entity.getMediaContentType());
    if (entity.getMediaETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getMediaETag());
    }
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
      dataProvider.update(request.getRawBaseUri(), edmEntitySet, entity, deserializerResult.getEntity(), false,

          true);
      expand = deserializerResult.getExpandTree();
    }

    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    response.setContent(serializeEntity(entity, edmEntitySet, edmEntityType, format, expand, null)
        .getContent());
    response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    response.setHeader(HttpHeader.LOCATION,
        request.getRawBaseUri() + '/' + odata.createUriHelper().buildCanonicalURL(edmEntitySet, entity));
    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
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

    checkChangePreconditions(entity.getETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
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

    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    response.setContent(serializeEntity(entity, edmEntitySet, edmEntityType, format, null, null)
        .getContent());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
  }

  @Override
  public void updateMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    Entity entity = readEntity(uriInfo);
    checkChangePreconditions(entity.getMediaETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
    checkRequestFormat(requestFormat);
    dataProvider.setMedia(entity, odata.createFixedFormatDeserializer().binary(request.getBody()),
        requestFormat.toContentTypeString());

    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    response.setContent(serializeEntity(entity, edmEntitySet, edmEntityType, format, null, null)
        .getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
  }

  @Override
  public void deleteEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final Entity entity = readEntity(uriInfo);
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    final boolean isValue = resourcePaths.get(resourcePaths.size() - 1) instanceof UriResourceValue;

    checkChangePreconditions(isValue ? entity.getMediaETag() : entity.getETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
    dataProvider.delete(edmEntitySet, entity);
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void readReference(final ODataRequest request, ODataResponse response, final UriInfo uriInfo, 
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    readEntity(request, response, uriInfo, requestedContentType, true);
  }

  @Override
  public void createReference(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat) throws ODataApplicationException, DeserializerException {
    
    final ODataDeserializer deserializer = odata.createDeserializer(ODataFormat.fromContentType(requestFormat));
    final DeserializerResult references = deserializer.entityReferences(request.getBody());

    if (references.getEntityReferences().size() != 1) {
      throw new ODataApplicationException("A post request to a collection navigation property must "
          + "contain a single entity reference", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
    
    final Entity entity = readEntity(uriInfo, true);
    final UriResourceNavigation navigationProperty = getLastNavigation(uriInfo);
    dataProvider.createReference(entity, navigationProperty.getProperty(), references.getEntityReferences().get(0), 
        request.getRawBaseUri());
    
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }
  
  @Override
  public void updateReference(final ODataRequest request, ODataResponse response, final UriInfo uriInfo, 
      final ContentType requestFormat) throws ODataApplicationException, DeserializerException {

    final ODataDeserializer deserializer = odata.createDeserializer(ODataFormat.fromContentType(requestFormat));
    final DeserializerResult references = deserializer.entityReferences(request.getBody());

    if (references.getEntityReferences().size() != 1) {
      throw new ODataApplicationException("A post request to a collection navigation property must "
          + "contain a single entity reference", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
    
    final Entity entity = readEntity(uriInfo, true);
    final UriResourceNavigation navigationProperty = getLastNavigation(uriInfo);
    dataProvider.createReference(entity, navigationProperty.getProperty(), references.getEntityReferences().get(0), 
        request.getRawBaseUri());
    
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }
  
  @Override
  public void deleteReference(final ODataRequest request, ODataResponse response, final UriInfo uriInfo) 
      throws ODataApplicationException {

    final UriResourceNavigation lastNavigation = getLastNavigation(uriInfo);
    final IdOption idOption = uriInfo.getIdOption();
    
    if(lastNavigation.isCollection() && idOption == null) {
      throw new ODataApplicationException("Id system query option must be provided", 
          HttpStatusCode.BAD_REQUEST.getStatusCode(), 
          Locale.ROOT);
    } else if(!lastNavigation.isCollection() && idOption != null) {
      throw new ODataApplicationException("Id system query option must not be provided", 
          HttpStatusCode.BAD_REQUEST.getStatusCode(), 
          Locale.ROOT);
    }
    
    final Entity entity = readEntity(uriInfo, true);
    dataProvider.deleteReference(entity, 
                                 lastNavigation.getProperty(), 
                                 (uriInfo.getIdOption() != null) ? uriInfo.getIdOption().getValue() : null, 
                                 request.getRawBaseUri());
    
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void readReferenceCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo, 
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    readEntityCollection(request, response, uriInfo, requestedContentType, true);
  }

  private void readEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo, 
      final ContentType requestedContentType, final boolean isReference)
      throws ODataApplicationException, SerializerException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet == null ?
        (EdmEntityType) ((UriResourceFunction) uriInfo.getUriResourceParts()
            .get(uriInfo.getUriResourceParts().size() - 1)).getType() :
        edmEntitySet.getEntityType();

    final Entity entity = readEntity(uriInfo);

    checkReadPreconditions(entity.getETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));

    final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
    final ExpandOption expand = uriInfo.getExpandOption();
    final SelectOption select = uriInfo.getSelectOption();

    final ExpandSystemQueryOptionHandler expandHandler = new ExpandSystemQueryOptionHandler();
    final Entity entitySerialization = expandHandler.transformEntityGraphToTree(entity, edmEntitySet, expand);
    expandHandler.applyExpandQueryOptions(entitySerialization, edmEntitySet, expand);

    final SerializerResult serializerResult = isReference ?
        serializeReference(entity, edmEntitySet, format) :
        serializeEntity(entitySerialization, edmEntitySet, edmEntityType, format, expand, select);

    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
  }
  
  private void readEntityCollection(final ODataRequest request, final ODataResponse response, 
      final UriInfo uriInfo, final ContentType requestedContentType, final boolean isReference) 
          throws ODataApplicationException, SerializerException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EdmEntityType edmEntityType = edmEntitySet == null ?
        (EdmEntityType) ((UriResourceFunction) uriInfo.getUriResourceParts()
            .get(uriInfo.getUriResourceParts().size() - 1)).getType() :
        edmEntitySet.getEntityType();

    EntityCollection entitySetInitial = readEntityCollection(uriInfo);

    if(entitySetInitial == null) {
      entitySetInitial = new EntityCollection();
    }
    
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
    final CountOption countOption = uriInfo.getCountOption();

    // Serialize
    final SerializerResult serializerResult = (isReference) ? 
        serializeReferenceCollection(entitySetSerialization, edmEntitySet, format) :
        serializeEntityCollection(entitySetSerialization, edmEntitySet, edmEntityType, format,
            expand, select, countOption);
    
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
  }

  private SerializerResult serializeEntityCollection(final EntityCollection entityCollection, 
      final EdmEntitySet edmEntitySet, final EdmEntityType edmEntityType, final ODataFormat format, 
      final ExpandOption expand, final SelectOption select, final CountOption countOption) 
          throws SerializerException {
    return odata.createSerializer(format).entityCollection(
        serviceMetadata,
        edmEntityType,
        entityCollection,
        EntityCollectionSerializerOptions.with()
            .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                getContextUrl(edmEntitySet, edmEntityType, false, expand, select))
            .count(countOption)
            .expand(expand).select(select)
            .build());
  }

  private SerializerResult serializeReferenceCollection(final EntityCollection entityCollection, 
      final EdmEntitySet edmEntitySet, final ODataFormat format) throws SerializerException {
    return odata.createSerializer(format)
        .referenceCollection(serviceMetadata, edmEntitySet, entityCollection,
            ContextURL.with().asCollection().suffix(Suffix.REFERENCE).build());
  }

  private SerializerResult serializeReference(final Entity entity, final EdmEntitySet edmEntitySet, 
      final ODataFormat format ) throws SerializerException {
    return odata.createSerializer(format)
        .reference(serviceMetadata, edmEntitySet, entity,
            ContextURL.with().suffix(Suffix.REFERENCE).build());
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
}
