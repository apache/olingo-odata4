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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Builder;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.prefer.Preferences.Return;
import org.apache.olingo.server.api.prefer.PreferencesApplied;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.processor.ReferenceCollectionProcessor;
import org.apache.olingo.server.api.processor.ReferenceProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.IdOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.tecsvc.async.AsyncProcessor;
import org.apache.olingo.server.tecsvc.async.TechnicalAsyncService;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.data.RequestValidator;
import org.apache.olingo.server.tecsvc.processor.queryoptions.ExpandSystemQueryOptionHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.CountHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.FilterHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.OrderByHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.SearchHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.ServerSidePagingHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.SkipHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.TopHandler;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;

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
      final ContentType requestedContentType) throws ODataApplicationException, ODataLibraryException {
    validateOptions(uriInfo.asUriInfoResource());

    readEntityCollection(request, response, uriInfo, requestedContentType, false);
  }

  @Override
  public void countEntityCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {
    validateOptions(uriInfo.asUriInfoResource());
    getEdmEntitySet(uriInfo); // including checks
    final EntityCollection entitySetInitial = readEntityCollection(uriInfo);
    EntityCollection entitySet = new EntityCollection();

    entitySet.getEntities().addAll(entitySetInitial.getEntities());
    FilterHandler.applyFilterSystemQuery(uriInfo.getFilterOption(), entitySet, uriInfo, serviceMetadata.getEdm());
    response.setContent(odata.createFixedFormatSerializer().count(
        entitySet.getEntities().size()));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.toContentTypeString());
  }

  @Override
  public void readEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, ODataLibraryException {
    validateOptions(uriInfo.asUriInfoResource());

    readEntity(request, response, uriInfo, requestedContentType, false);
  }

  @Override
  public void readMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
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
      throws ODataApplicationException, ODataLibraryException {
    createEntity(request, response, uriInfo, requestFormat, responseFormat);
  }

  @Override
  public void createEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    if (uriInfo.asUriInfoResource().getUriResourceParts().size() > 1) {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    checkRequestFormat(requestFormat);

    if (odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).hasRespondAsync()) {
      TechnicalAsyncService asyncService = TechnicalAsyncService.getInstance();
      TechnicalEntityProcessor processor = new TechnicalEntityProcessor(dataProvider, serviceMetadata);
      processor.init(odata, serviceMetadata);
      AsyncProcessor<EntityProcessor> asyncProcessor = asyncService.register(processor, EntityProcessor.class);
      asyncProcessor.prepareFor().createEntity(request, response, uriInfo, requestFormat, responseFormat);
      String location = asyncProcessor.processAsync();
      TechnicalAsyncService.acceptedResponse(response, location);
      return;
    }

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
          odata.createDeserializer(requestFormat).entity(request.getBody(), edmEntityType);
      new RequestValidator(dataProvider, request.getRawBaseUri())
          .validate(edmEntitySet, deserializerResult.getEntity());

      entity = dataProvider.create(edmEntitySet);
      dataProvider.update(request.getRawBaseUri(), edmEntitySet, entity, deserializerResult.getEntity(), false, true);
      expand = deserializerResult.getExpandTree();
    }

    final String location = request.getRawBaseUri() + '/'
        + odata.createUriHelper().buildCanonicalURL(edmEntitySet, entity);
    final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
    if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
      response.setContent(serializeEntity(request, entity, edmEntitySet, edmEntityType, responseFormat, expand, null)
          .getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
      response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
    } else {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      response.setHeader(HttpHeader.ODATA_ENTITY_ID, location);
    }
    if (returnPreference != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
    }
    response.setHeader(HttpHeader.LOCATION, location);
    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
  }

  @Override
  public void updateEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
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

    odata.createETagHelper().checkChangePreconditions(entity.getETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
    final ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
    final Entity changedEntity = deserializer.entity(request.getBody(), edmEntitySet.getEntityType()).getEntity();

    new RequestValidator(dataProvider,
        true, // Update
        request.getMethod() == HttpMethod.PATCH,
        request.getRawBaseUri()).validate(edmEntitySet, changedEntity);

    dataProvider.update(request.getRawBaseUri(), edmEntitySet, entity, changedEntity,
        request.getMethod() == HttpMethod.PATCH, false);

    final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
    if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setContent(serializeEntity(request, entity, edmEntitySet, edmEntityType, responseFormat)
          .getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    } else {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
    if (returnPreference != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
    }
    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
  }

  @Override
  public void updateMediaEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    Entity entity = readEntity(uriInfo);
    odata.createETagHelper().checkChangePreconditions(entity.getMediaETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
    checkRequestFormat(requestFormat);
    dataProvider.setMedia(entity, odata.createFixedFormatDeserializer().binary(request.getBody()),
        requestFormat.toContentTypeString());

    final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
    if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
      response.setContent(serializeEntity(request, entity, edmEntitySet, edmEntityType, responseFormat)
          .getContent());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    } else {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
    if (returnPreference != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
    }
    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
  }

  @Override
  public void deleteEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataLibraryException, ODataApplicationException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final Entity entity = readEntity(uriInfo);

    odata.createETagHelper().checkChangePreconditions(entity.getETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
    dataProvider.delete(edmEntitySet, entity);
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void deleteMediaEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataLibraryException, ODataApplicationException {
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final Entity entity = readEntity(uriInfo);

    odata.createETagHelper().checkChangePreconditions(entity.getMediaETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH));
    dataProvider.delete(edmEntitySet, entity);
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void readReference(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, ODataLibraryException {
    readEntity(request, response, uriInfo, requestedContentType, true);
  }

  @Override
  public void createReference(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat) throws ODataApplicationException, ODataLibraryException {

    final ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
    final DeserializerResult references = deserializer.entityReferences(request.getBody());

    if (references.getEntityReferences().size() != 1) {
      throw new ODataApplicationException("A post request to a collection navigation property must "
          + "contain a single entity reference", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }

    final Entity entity = readEntity(uriInfo, true);
    final UriResourceNavigation navigationProperty = getLastNavigation(uriInfo);
    ensureNavigationPropertyNotNull(navigationProperty);
    dataProvider.createReference(entity, navigationProperty.getProperty(), references.getEntityReferences().get(0),
        request.getRawBaseUri());

    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void updateReference(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat) throws ODataApplicationException, ODataLibraryException {

    final ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
    final DeserializerResult references = deserializer.entityReferences(request.getBody());

    if (references.getEntityReferences().size() != 1) {
      throw new ODataApplicationException("A post request to a collection navigation property must "
          + "contain a single entity reference", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }

    final Entity entity = readEntity(uriInfo, true);
    final UriResourceNavigation navigationProperty = getLastNavigation(uriInfo);
    ensureNavigationPropertyNotNull(navigationProperty);
    dataProvider.createReference(entity, navigationProperty.getProperty(), references.getEntityReferences().get(0),
        request.getRawBaseUri());

    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void deleteReference(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException {

    final UriResourceNavigation lastNavigation = getLastNavigation(uriInfo);
    final IdOption idOption = uriInfo.getIdOption();

    ensureNavigationPropertyNotNull(lastNavigation);
    if (lastNavigation.isCollection() && idOption == null) {
      throw new ODataApplicationException("Id system query option must be provided",
          HttpStatusCode.BAD_REQUEST.getStatusCode(),
          Locale.ROOT);
    } else if (!lastNavigation.isCollection() && idOption != null) {
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
  public void readReferenceCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestedContentType)
      throws ODataApplicationException, ODataLibraryException {
    readEntityCollection(request, response, uriInfo, requestedContentType, true);
  }

  private void readEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedFormat, final boolean isReference)
      throws ODataApplicationException, ODataLibraryException {
    //
    if (odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).hasRespondAsync()) {
      TechnicalAsyncService asyncService = TechnicalAsyncService.getInstance();
      TechnicalEntityProcessor processor = new TechnicalEntityProcessor(dataProvider, serviceMetadata);
      processor.init(odata, serviceMetadata);
      AsyncProcessor<EntityProcessor> asyncProcessor = asyncService.register(processor, EntityProcessor.class);
      asyncProcessor.prepareFor().readEntity(request, response, uriInfo, requestedFormat);
      String location = asyncProcessor.processAsync();
      TechnicalAsyncService.acceptedResponse(response, location);
      //
      return;
    }
    //

    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final EdmEntityType edmEntityType = edmEntitySet == null ?
        (EdmEntityType) ((UriResourcePartTyped) uriInfo.getUriResourceParts()
            .get(uriInfo.getUriResourceParts().size() - 1)).getType() :
        edmEntitySet.getEntityType();

    final Entity entity = readEntity(uriInfo);

    if (odata.createETagHelper().checkReadPreconditions(entity.getETag(),
        request.getHeaders(HttpHeader.IF_MATCH),
        request.getHeaders(HttpHeader.IF_NONE_MATCH))) {
      response.setStatusCode(HttpStatusCode.NOT_MODIFIED.getStatusCode());
      response.setHeader(HttpHeader.ETAG, entity.getETag());
      return;
    }

    final ExpandOption expand = uriInfo.getExpandOption();
    final SelectOption select = uriInfo.getSelectOption();

    final ExpandSystemQueryOptionHandler expandHandler = new ExpandSystemQueryOptionHandler();
    final Entity entitySerialization = expandHandler.transformEntityGraphToTree(entity, edmEntitySet, expand, null);
    expandHandler.applyExpandQueryOptions(entitySerialization, edmEntitySet, expand, uriInfo,
        serviceMetadata.getEdm());

    final SerializerResult serializerResult = isReference ?
        serializeReference(entity, edmEntitySet, requestedFormat) :
        serializeEntity(request, entitySerialization, edmEntitySet, edmEntityType, requestedFormat, expand, select);

    if (entity.getETag() != null) {
      response.setHeader(HttpHeader.ETAG, entity.getETag());
    }
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedFormat.toContentTypeString());
  }

  private void readEntityCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestedContentType, final boolean isReference)
      throws ODataApplicationException, ODataLibraryException {
    //
    if (odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).hasRespondAsync()) {
      TechnicalAsyncService asyncService = TechnicalAsyncService.getInstance();
      TechnicalEntityProcessor processor = new TechnicalEntityProcessor(dataProvider, serviceMetadata);
      processor.init(odata, serviceMetadata);
      AsyncProcessor<EntityCollectionProcessor> asyncProcessor =
          asyncService.register(processor, EntityCollectionProcessor.class);
      asyncProcessor.prepareFor().readEntityCollection(request, response, uriInfo, requestedContentType);
      String location = asyncProcessor.processAsync();
      TechnicalAsyncService.acceptedResponse(response, location);
      //
      return;
    }
    //

    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EdmEntityType edmEntityType = edmEntitySet == null ?
        (EdmEntityType) ((UriResourcePartTyped) uriInfo.getUriResourceParts()
            .get(uriInfo.getUriResourceParts().size() - 1)).getType() :
        edmEntitySet.getEntityType();

    EntityCollection entitySetInitial = readEntityCollection(uriInfo);
    if (entitySetInitial == null) {
      entitySetInitial = new EntityCollection();
    }

    // Modifying the original entitySet means modifying the "database", so we have to make a shallow
    // copy of the entity set (new EntitySet, but exactly the same data).
    EntityCollection entitySet = new EntityCollection();
    entitySet.getEntities().addAll(entitySetInitial.getEntities());
    entitySet.getOperations().addAll(entitySetInitial.getOperations());

    // Apply system query options.
    SearchHandler.applySearchSystemQueryOption(uriInfo.getSearchOption(), entitySet);
    FilterHandler.applyFilterSystemQuery(uriInfo.getFilterOption(), entitySet, uriInfo, serviceMetadata.getEdm());
    CountHandler.applyCountSystemQueryOption(uriInfo.getCountOption(), entitySet);
    OrderByHandler.applyOrderByOption(uriInfo.getOrderByOption(), entitySet, uriInfo, serviceMetadata.getEdm());
    SkipHandler.applySkipSystemQueryHandler(uriInfo.getSkipOption(), entitySet);
    TopHandler.applyTopSystemQueryOption(uriInfo.getTopOption(), entitySet);

    final Integer pageSize = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getMaxPageSize();
    final Integer serverPageSize = ServerSidePagingHandler.applyServerSidePaging(uriInfo.getSkipTokenOption(),
        entitySet,
        edmEntitySet,
        request.getRawRequestUri(),
        pageSize);

    // Apply expand system query option
    final ExpandOption expand = uriInfo.getExpandOption();
    final SelectOption select = uriInfo.getSelectOption();

    // Transform the entity graph to a tree. The construction is controlled by the expand tree.
    // Apply all expand system query options to the tree.
    // So the expanded navigation properties can be modified for serialization,
    // without affecting the data stored in the database.
    final ExpandSystemQueryOptionHandler expandHandler = new ExpandSystemQueryOptionHandler();
    final EntityCollection entitySetSerialization = expandHandler.transformEntitySetGraphToTree(entitySet,
        edmEntitySet,
        expand, null);
    expandHandler.applyExpandQueryOptions(entitySetSerialization, edmEntitySet, expand, uriInfo,
        serviceMetadata.getEdm());
    final CountOption countOption = uriInfo.getCountOption();

    String id;
    if (edmEntitySet == null) {
      // Used for functions, function imports etc.
      id = request.getRawODataPath();
    } else {
      id = request.getRawBaseUri() + edmEntitySet.getName();
    }

    if(isReference) {
      final SerializerResult serializerResult =
          serializeReferenceCollection(entitySetSerialization, edmEntitySet, requestedContentType, countOption);
      response.setContent(serializerResult.getContent());
    } else if(isStreaming(edmEntitySet, requestedContentType)) {
      final SerializerStreamResult serializerResult =
          serializeEntityCollectionStreamed(request,
              entitySetSerialization, edmEntitySet, edmEntityType, requestedContentType,
              expand, select, countOption, id);

      response.setODataContent(serializerResult.getODataContent());
    } else {
      final SerializerResult serializerResult =
          serializeEntityCollection(request,
              entitySetSerialization, edmEntitySet, edmEntityType, requestedContentType,
              expand, select, countOption, id);
      response.setContent(serializerResult.getContent());
    }

    //
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    if (pageSize != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().maxPageSize(serverPageSize).build().toValueString());
    }
  }

  /**
   * Check is streaming is enabled for this entity set in combination with the given content type.
   * <code>TRUE</code> if the technical scenario supports streaming for this combination,
   * otherwise <code>FALSE</code>.
   *
   * @param edmEntitySet entity set of the request
   * @param contentType requested content type of the request
   * @return <code>TRUE</code> if the technical scenario supports streaming for this combination,
   *          otherwise <code>FALSE</code>.
   */
  private boolean isStreaming(EdmEntitySet edmEntitySet, ContentType contentType) {
    return ContainerProvider.ES_STREAM.equalsIgnoreCase(edmEntitySet.getName());
  }

  private SerializerResult serializeEntityCollection(final ODataRequest request, final EntityCollection
      entityCollection, final EdmEntitySet edmEntitySet, final EdmEntityType edmEntityType,
      final ContentType requestedFormat, final ExpandOption expand, final SelectOption select,
      final CountOption countOption, String id) throws ODataLibraryException {

    return odata.createSerializer(requestedFormat).entityCollection(
        serviceMetadata,
        edmEntityType,
        entityCollection,
        EntityCollectionSerializerOptions.with()
            .contextURL(isODataMetadataNone(requestedFormat) ? null :
                getContextUrl(request.getRawODataPath(), edmEntitySet, edmEntityType, false, expand, select))
            .count(countOption)
            .expand(expand).select(select)
            .id(id)
            .build());
  }

  // serialise as streamed collection
  private SerializerStreamResult serializeEntityCollectionStreamed(final ODataRequest request,
      final EntityCollection entityCollection, final EdmEntitySet edmEntitySet,
      final EdmEntityType edmEntityType,
      final ContentType requestedFormat, final ExpandOption expand, final SelectOption select,
      final CountOption countOption, final String id) throws ODataLibraryException {

    EntityIterator streamCollection = new EntityIterator() {
      Iterator<Entity> entityIterator = entityCollection.iterator();

      @Override
      public List<Operation> getOperations() {
        return entityCollection.getOperations();
      } 
      
      @Override
      public boolean hasNext() {
        return entityIterator.hasNext();
      }

      @Override
      public Entity next() {
        return addToPrimitiveProperty(entityIterator.next(), "PropertyString", "->streamed");
      }

      private Entity addToPrimitiveProperty(Entity entity, String name, Object data) {
        List<Property> properties = entity.getProperties();
        addTo(name, data, properties);
        return entity;
      }

      private void addTo(String name, Object data, List<Property> properties) {
        int pos = 0;
        for (Property property : properties) {
          if (property.isComplex()) {
            @SuppressWarnings("unchecked")
            final List<ComplexValue> cvs = property.isCollection() ?
                (List<ComplexValue>) property.asCollection() :
                Collections.singletonList(property.asComplex());
            for (ComplexValue cv : cvs) {
              final List<Property> value = cv.getValue();
              if (value != null) {
                addTo(name, data, value);
              }
            }
          }

          if (name.equals(property.getName())) {
            properties.remove(pos);
            final String old = property.getValue().toString();
            String newValue = (old == null ? "": old) + data.toString();
            properties.add(pos, new Property(null, name, ValueType.PRIMITIVE, newValue));
            break;
          }
          pos++;
        }
      }
    };

    return odata.createSerializer(requestedFormat).entityCollectionStreamed(
        serviceMetadata,
        edmEntityType,
        streamCollection,
        EntityCollectionSerializerOptions.with()
            .contextURL(isODataMetadataNone(requestedFormat) ? null :
                getContextUrl(request.getRawODataPath(), edmEntitySet, edmEntityType, false, expand, select))
            .count(countOption)
            .expand(expand).select(select)
            .id(id)
            .build());
  }

  private SerializerResult serializeReferenceCollection(final EntityCollection entityCollection,
      final EdmEntitySet edmEntitySet, final ContentType requestedFormat, final CountOption countOption)
      throws ODataLibraryException {

    return odata.createSerializer(requestedFormat)
        .referenceCollection(serviceMetadata, edmEntitySet, entityCollection,
            ReferenceCollectionSerializerOptions.with()
                .contextURL(ContextURL.with().asCollection().suffix(Suffix.REFERENCE).build())
                .count(countOption).build());
  }

  private SerializerResult serializeReference(final Entity entity, final EdmEntitySet edmEntitySet,
      final ContentType requestedFormat) throws ODataLibraryException {
    return odata.createSerializer(requestedFormat)
        .reference(serviceMetadata, edmEntitySet, entity, ReferenceSerializerOptions.with()
            .contextURL(ContextURL.with().suffix(Suffix.REFERENCE).build()).build());

  }

  private SerializerResult serializeEntity(final ODataRequest request, final Entity entity,
      final EdmEntitySet edmEntitySet, final EdmEntityType edmEntityType,
      final ContentType requestedFormat) throws ODataLibraryException {
    return serializeEntity(request, entity, edmEntitySet, edmEntityType, requestedFormat, null, null);
  }

  private SerializerResult serializeEntity(final ODataRequest request, final Entity entity,
      final EdmEntitySet edmEntitySet, final EdmEntityType edmEntityType,
      final ContentType requestedFormat,
      final ExpandOption expand, final SelectOption select)
      throws ODataLibraryException {

    ContextURL contextUrl = isODataMetadataNone(requestedFormat) ? null :
        getContextUrl(request.getRawODataPath(), edmEntitySet, edmEntityType, true, expand, null);
    return odata.createSerializer(requestedFormat).entity(
        serviceMetadata,
        edmEntityType,
        entity,
        EntitySerializerOptions.with()
            .contextURL(contextUrl)
            .expand(expand).select(select)
            .build());
  }

  private ContextURL getContextUrl(String rawODataPath, final EdmEntitySet entitySet, final EdmEntityType entityType,
      final boolean isSingleEntity, final ExpandOption expand, final SelectOption select)
      throws ODataLibraryException {
    Builder builder = ContextURL.with().oDataPath(rawODataPath);
    builder = entitySet == null ?
        isSingleEntity ? builder.type(entityType) : builder.asCollection().type(entityType) :
        builder.entitySet(entitySet);
    builder = builder
        .selectList(odata.createUriHelper().buildContextURLSelectList(entityType, expand, select))
        .suffix(isSingleEntity && entitySet != null ? Suffix.ENTITY : null);
    return builder.build();
  }

  private void ensureNavigationPropertyNotNull(final UriResourceNavigation navigationProperty)
      throws ODataApplicationException {
    if (navigationProperty == null) {
      throw new ODataApplicationException("Missing navigation segment", HttpStatusCode.BAD_REQUEST.getStatusCode(),
          Locale.ROOT);
    }
  }
}
