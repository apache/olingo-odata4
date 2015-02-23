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
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.ActionEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.processor.queryoptions.SystemQueryOptions;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.ServerSidePagingHandler;

/**
 * Technical Processor for entity-related functionality.
 */
public class TechnicalEntityProcessor extends TechnicalProcessor
    implements EntityCollectionProcessor, ActionEntityCollectionProcessor, CountEntityCollectionProcessor,
    EntityProcessor, ActionEntityProcessor, MediaEntityProcessor {

  public TechnicalEntityProcessor(final DataProvider dataProvider) {
    super(dataProvider);
  }

  @Override
  public void readEntityCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());

    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EntitySet entitySetInitial = readEntityCollection(uriInfo);
    if (entitySetInitial == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      // Modifying the original entitySet means modifying the "database", so we have to make a shallow
      // copy of the entity set (new EntitySet, but exactly the same data)
      EntitySet entitySet = new EntitySetImpl();
      entitySet.getEntities().addAll(entitySetInitial.getEntities());

      // Apply system query options
      SystemQueryOptions.applySystemQueryOptions(entitySet, edmEntitySet, uriInfo);
      ServerSidePagingHandler.applyServerSidePaging(entitySet, request.getRawRequestUri(), uriInfo);
      
      final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
      ODataSerializer serializer = odata.createSerializer(format);
      final ExpandOption expand = uriInfo.getExpandOption();
      final SelectOption select = uriInfo.getSelectOption();
      response.setContent(serializer.entityCollection(edmEntitySet.getEntityType(), entitySet,
          EntityCollectionSerializerOptions.with()
              .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                  getContextUrl(edmEntitySet, false, expand, select))
              .count(uriInfo.getCountOption())
              .expand(expand).select(select)
              .build()));
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    }
  }

  @Override
  public void processActionEntityCollection(final ODataRequest request, final ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    throw new ODataApplicationException("Process entity collection is not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  @Override
  public void countEntityCollection(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
      throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());
    getEdmEntitySet(uriInfo);  // including checks
    EntitySet entitySet = readEntityCollection(uriInfo);
    if (entitySet == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    } else {
      setCount(entitySet);
      response.setContent(odata.createFixedFormatSerializer().count(entitySet.getCount()));
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, HttpContentType.TEXT_PLAIN);
    }
  }

  @Override
  public void readEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    validateOptions(uriInfo.asUriInfoResource());
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo);
    final Entity entity = readEntity(uriInfo);

    final ODataFormat format = ODataFormat.fromContentType(requestedContentType);
    ODataSerializer serializer = odata.createSerializer(format);
    final ExpandOption expand = uriInfo.getExpandOption();
    final SelectOption select = uriInfo.getSelectOption();
    response.setContent(serializer.entity(edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                getContextUrl(edmEntitySet, true, expand, select))
            .expand(expand).select(select)
            .build()));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
  }

  @Override
  public void readMediaEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType responseFormat) throws ODataApplicationException, SerializerException {
    getEdmEntitySet(uriInfo);  // including checks
    final Entity entity = readEntity(uriInfo);
    response.setContent(odata.createFixedFormatSerializer().binary(dataProvider.readMedia(entity)));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, entity.getMediaContentType());
  }

  @Override
  public void createMediaEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, DeserializerException, SerializerException {
    createEntity(request, response, uriInfo, requestFormat, responseFormat);
  }

  @Override
  public void createEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
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

    Entity entity = dataProvider.create(edmEntitySet);
    if (edmEntityType.hasStream()) { // called from createMediaEntity(...), not directly
      dataProvider.setMedia(entity, odata.createFixedFormatDeserializer().binary(request.getBody()),
          requestFormat.toContentTypeString());
    } else {
      dataProvider.update(edmEntitySet, entity,
          odata.createDeserializer(ODataFormat.fromContentType(requestFormat))
              .entity(request.getBody(), edmEntityType),
          false);
    }

    final ODataFormat format = ODataFormat.fromContentType(responseFormat);
    ODataSerializer serializer = odata.createSerializer(format);
    response.setContent(serializer.entity(edmEntityType, entity,
        EntitySerializerOptions.with()
            .contextURL(format == ODataFormat.JSON_NO_METADATA ? null :
                getContextUrl(edmEntitySet, true, null, null))
            .build()));
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
    Entity entity = readEntity(uriInfo);
    checkRequestFormat(requestFormat);
    ODataDeserializer deserializer = odata.createDeserializer(ODataFormat.fromContentType(requestFormat));
    final Entity changedEntity = deserializer.entity(request.getBody(), edmEntitySet.getEntityType());
    dataProvider.update(edmEntitySet, entity, changedEntity, request.getMethod() == HttpMethod.PATCH);
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void updateMediaEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
          throws ODataApplicationException, DeserializerException, SerializerException {
    getEdmEntitySet(uriInfo);  // including checks
    Entity entity = readEntity(uriInfo);
    checkRequestFormat(requestFormat);
    dataProvider.setMedia(entity, odata.createFixedFormatDeserializer().binary(request.getBody()),
        requestFormat.toContentTypeString());
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  @Override
  public void deleteEntity(final ODataRequest request, ODataResponse response, final UriInfo uriInfo)
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
    throw new ODataApplicationException("Process entity is not supported yet.",
        HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
  }

  private void setCount(EntitySet entitySet) {
    // TODO: set count (correctly) and next link
    if (entitySet.getCount() == null) {
      entitySet.setCount(entitySet.getEntities().size());
    }
  }

  private void checkRequestFormat(final ContentType requestFormat) throws ODataApplicationException {
    if (requestFormat == null) {
      throw new ODataApplicationException("The content type has not been set in the request.",
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  private ContextURL getContextUrl(final EdmEntitySet entitySet, final boolean isSingleEntity,
      final ExpandOption expand, final SelectOption select) throws SerializerException {
    return ContextURL.with().entitySet(entitySet)
        .selectList(odata.createUriHelper()
            .buildContextURLSelectList(entitySet.getEntityType(), expand, select))
        .suffix(isSingleEntity ? Suffix.ENTITY : null)
        .build();
  }
}
