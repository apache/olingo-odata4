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

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Builder;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.prefer.Preferences.Return;
import org.apache.olingo.server.api.prefer.PreferencesApplied;
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
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.data.EntityActionResult;

/**
 * Technical Processor for action-related functionality.
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
    Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    EdmAction action = null;
    EntityCollection collection = null;
    List<UriResource> resourcePaths = uriInfo.asUriInfoResource().getUriResourceParts();
    if (resourcePaths.size() > 1) {
      UriResourceEntitySet boundEntityCollection = (UriResourceEntitySet) resourcePaths.get(0);
      if (resourcePaths.get(1) instanceof UriResourceNavigation) {
        UriResourceNavigation navResource = (UriResourceNavigation) resourcePaths.get(1);
        EdmNavigationProperty navProperty = navResource.getProperty();
        action = ((UriResourceAction) resourcePaths.get(2))
            .getAction();
        parameters = readParameters(action, request.getBody(), requestFormat);
        collection =
            dataProvider.processBoundActionWithNavEntityCollection(action.getName(), parameters, 
                boundEntityCollection.getEntitySet(), navProperty);
      } else if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
        action = ((UriResourceAction) resourcePaths.get(1))
            .getAction();
        parameters = readParameters(action, request.getBody(), requestFormat);
        collection =
            dataProvider.processBoundActionEntityCollection(action.getName(), parameters, 
                boundEntityCollection.getEntitySet());
      }
    } else {
      action = ((UriResourceAction) resourcePaths.get(0))
          .getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      collection =
          dataProvider.processActionEntityCollection(action.getName(), parameters);
    }
    // Collections must never be null.
    // Not nullable return types must not contain a null value.
    if (collection == null
        || collection.getEntities().contains(null) && !action.getReturnType().isNullable()) {
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }

    final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
    if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
      final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
      final EdmEntityType type = (EdmEntityType) action.getReturnType().getType();
      final EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions.with()
          .contextURL(isODataMetadataNone(responseFormat) ? null : getContextUrl(edmEntitySet, type, false))
          .build();
      response.setContent(odata.createSerializer(responseFormat)
          .entityCollection(serviceMetadata, type, collection, options).getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    } else {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
    if (returnPreference != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
    }
  }

  @Override
  public void processActionEntity(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    EdmAction action = null;
    Map<String, Parameter> parameters = new HashMap<String, Parameter>(); 
    EntityActionResult entityResult = null;
    
    final List<UriResource> resourcePaths = uriInfo.asUriInfoResource().getUriResourceParts();
    if (resourcePaths.size() > 1) {
      UriResourceEntitySet boundEntity = (UriResourceEntitySet) resourcePaths.get(0);
      EdmEntitySet entitySet = boundEntity.getEntitySet();
      if (resourcePaths.get(1) instanceof UriResourceNavigation) {
        UriResourceNavigation navEntity = (UriResourceNavigation) resourcePaths.get(1);
        action = ((UriResourceAction) resourcePaths.get(2))
            .getAction();
        parameters = readParameters(action, request.getBody(), requestFormat);
        if (navEntity.getTypeFilterOnEntry() != null) {
          EdmEntityType edmEntityType = (EdmEntityType) navEntity.
              getTypeFilterOnEntry();
          entitySet = getTypeCastedEntitySet(entitySet, edmEntityType);
          entityResult =
              dataProvider.processBoundActionEntity(action.getName(), parameters, boundEntity.getKeyPredicates(),
                  entitySet);
        } else {
          EdmNavigationProperty navProperty = navEntity.getProperty();
          entityResult =
              dataProvider.processBoundActionWithNavigationEntity(action.getName(), 
                  parameters, boundEntity.getKeyPredicates(),
                  entitySet, navProperty);
        }
      } else if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        action = ((UriResourceAction) resourcePaths.get(1))
            .getAction();
        if (uriResourceEntitySet.getTypeFilterOnEntry() != null) {
            EdmEntityType edmEntityType = (EdmEntityType) uriResourceEntitySet.
                getTypeFilterOnEntry();
            entitySet = getTypeCastedEntitySet(entitySet, edmEntityType);
          }
        parameters = readParameters(action, request.getBody(), requestFormat);
        entityResult =
            dataProvider.processBoundActionEntity(action.getName(), parameters, boundEntity.getKeyPredicates(),
                entitySet);
      }
    } else {
      action = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
          .getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      entityResult =
          dataProvider.processActionEntity(action.getName(), parameters);
    }
    final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
    final EdmEntityType type = (EdmEntityType) action.getReturnType().getType();

    if (entityResult == null || entityResult.getEntity() == null) {
      if (action.getReturnType().isNullable()) {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      } else {
        // Not nullable return type so we have to give back a 500
        throw new ODataApplicationException("The action could not be executed.",
            HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
      }
    } else {
      final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
      if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
        response.setContent(odata.createSerializer(responseFormat).entity(
            serviceMetadata,
            type,
            entityResult.getEntity(),
            EntitySerializerOptions.with()
                .contextURL(isODataMetadataNone(responseFormat) ? null : getContextUrl(edmEntitySet, type, true))
                .build())
            .getContent());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        response.setStatusCode((entityResult.isCreated() ? HttpStatusCode.CREATED : HttpStatusCode.OK)
            .getStatusCode());
      } else {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      }
      if (returnPreference != null) {
        response.setHeader(HttpHeader.PREFERENCE_APPLIED,
            PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
      }
      if (entityResult.isCreated()) {
        final String location = request.getRawBaseUri() + '/'
            + odata.createUriHelper().buildCanonicalURL(edmEntitySet, entityResult.getEntity());
        response.setHeader(HttpHeader.LOCATION, location);
        if (returnPreference == Return.MINIMAL) {
          response.setHeader(HttpHeader.ODATA_ENTITY_ID, location);
        }
      }
      if (entityResult.getEntity().getETag() != null) {
        response.setHeader(HttpHeader.ETAG, entityResult.getEntity().getETag());
      }
    }
  }
  
  /**
   * @param entitySet
   * @param edmType
   * @param edmEntityType
   * @return
   */
  private EdmEntitySet getTypeCastedEntitySet(EdmEntitySet entitySet, EdmEntityType edmEntityType) {
    EdmEntityContainer container = serviceMetadata.getEdm().getEntityContainer();
    List<EdmEntitySet> entitySets = container.getEntitySets();
    for (EdmEntitySet enSet : entitySets) {
      if (enSet.getEntityType().getFullQualifiedName().getFullQualifiedNameAsString().equals
          (edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        entitySet = enSet;
        break;
      }
    }
    return entitySet;
  }

  @Override
  public void processActionPrimitiveCollection(final ODataRequest request, ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    EdmAction action = null;
    Map<String, Parameter> parameters = null;
    List<UriResource> uriResource = uriInfo.asUriInfoResource().getUriResourceParts();
    Property property = null;
    if (uriResource.size() > 1) {
      UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource.get(0);
      action = ((UriResourceAction) uriResource.get(uriResource.size() - 1)).getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      property = dataProvider.processBoundActionPrimitiveCollection(action.getName(), parameters, 
          uriResourceEntitySet.getEntitySet(), uriResourceEntitySet.getKeyPredicates());
    } else {
      action = ((UriResourceAction) uriResource.get(0))
          .getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      property =
          dataProvider.processActionPrimitiveCollection(action.getName(), parameters);
    }

    if (property == null || property.isNull()) {
      // Collection Propertys must never be null
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    } else if (property.asCollection().contains(null) && !action.getReturnType().isNullable()) {
      // Not nullable return type but array contains a null value
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }

    final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
    if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
      final EdmPrimitiveType type = (EdmPrimitiveType) action.getReturnType().getType();
      final ContextURL contextURL = ContextURL.with().type(type).navOrPropertyPath(action.getName())
          .asCollection().build();
      final PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextURL).build();
      final SerializerResult result =
          odata.createSerializer(responseFormat).primitiveCollection(serviceMetadata, type, property, options);
      response.setContent(result.getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    } else {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
    if (returnPreference != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
    }
  }

  @Override
  public void processActionPrimitive(final ODataRequest request, ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    EdmAction action = null;
    Map<String, Parameter> parameters = null;
    List<UriResource> uriResource = uriInfo.asUriInfoResource().getUriResourceParts();
    Property property = null;
    if (uriResource.size() > 1) {
      UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource.get(0);
      action = ((UriResourceAction) uriResource.get(uriResource.size() - 1)).getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      property = dataProvider.processBoundActionPrimitive(action.getName(), parameters, 
          uriResourceEntitySet.getEntitySet(), uriResourceEntitySet.getKeyPredicates());
    } else {
      action = ((UriResourceAction) uriResource.get(0))
          .getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      property = dataProvider.processActionPrimitive(action.getName(), parameters);
    }
    
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
      final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
      if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
        final ContextURL contextURL = ContextURL.with().type(type).build();
        final PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextURL).build();
        final SerializerResult result = odata.createSerializer(responseFormat)
            .primitive(serviceMetadata, type, property, options);
        response.setContent(result.getContent());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      } else {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      }
      if (returnPreference != null) {
        response.setHeader(HttpHeader.PREFERENCE_APPLIED,
            PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
      }
    }
  }

  @Override
  public void processActionComplexCollection(final ODataRequest request, ODataResponse response,
      final UriInfo uriInfo, final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    EdmAction action = null;
    Map<String, Parameter> parameters = null;
    Property property = null;
    final List<UriResource> resourcePaths = uriInfo.asUriInfoResource().getUriResourceParts();
    if (resourcePaths.size() > 1) {
      if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet entitySet = uriResourceEntitySet.getEntitySet();
        action = ((UriResourceAction) resourcePaths.get(resourcePaths.size() - 1))
            .getAction();
        parameters = readParameters(action, request.getBody(), requestFormat);
        property =
            dataProvider.processBoundActionComplexCollection(action.getName(), parameters, entitySet, 
                uriResourceEntitySet.getKeyPredicates());
      }
    } else {
      action = ((UriResourceAction) resourcePaths.get(0))
          .getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      property =
          dataProvider.processActionComplexCollection(action.getName(), parameters);
    }
    
    if (property == null || property.isNull()) {
      // Collection Propertys must never be null
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    } else if (property.asCollection().contains(null) && !action.getReturnType().isNullable()) {
      // Not nullable return type but array contains a null value
      throw new ODataApplicationException("The action could not be executed.",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
    final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
    if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
      final EdmComplexType type = (EdmComplexType) action.getReturnType().getType();
      final ContextURL contextURL = ContextURL.with().type(type).asCollection().build();
      final ComplexSerializerOptions options = ComplexSerializerOptions.with().contextURL(contextURL).build();
      final SerializerResult result =
          odata.createSerializer(responseFormat).complexCollection(serviceMetadata, type, property, options);
      response.setContent(result.getContent());
      response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
      response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    } else {
      response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    }
    if (returnPreference != null) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
    }
  }

  @Override
  public void processActionComplex(final ODataRequest request, ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat, final ContentType responseFormat)
      throws ODataApplicationException, ODataLibraryException {
    EdmAction action = null;
    Map<String, Parameter> parameters = null;
    Property property = null;
    final List<UriResource> resourcePaths = uriInfo.asUriInfoResource().getUriResourceParts();
    if (resourcePaths.size() > 1) {
      if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet entitySet = uriResourceEntitySet.getEntitySet();
        action = ((UriResourceAction) resourcePaths.get(resourcePaths.size() - 1))
            .getAction();
        parameters = readParameters(action, request.getBody(), requestFormat);
        property =
            dataProvider.processBoundActionComplex(action.getName(), parameters, entitySet, 
                uriResourceEntitySet.getKeyPredicates());
      }
    } else {
      action = ((UriResourceAction) resourcePaths.get(0))
          .getAction();
      parameters = readParameters(action, request.getBody(), requestFormat);
      property = dataProvider.processActionComplex(action.getName(), parameters);
    }
    
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
      final Return returnPreference = odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).getReturn();
      if (returnPreference == null || returnPreference == Return.REPRESENTATION) {
        final ContextURL contextURL = ContextURL.with().type(type).build();
        final ComplexSerializerOptions options = ComplexSerializerOptions.with().contextURL(contextURL).build();
        final SerializerResult result =
            odata.createSerializer(responseFormat).complex(serviceMetadata, type, property, options);
        response.setContent(result.getContent());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
      } else {
        response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
      }
      if (returnPreference != null) {
        response.setHeader(HttpHeader.PREFERENCE_APPLIED,
            PreferencesApplied.with().returnRepresentation(returnPreference).build().toValueString());
      }
    }
  }

  @Override
  public void processActionVoid(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestFormat) throws ODataApplicationException, ODataLibraryException {
    final UriResourceAction resource =
        ((UriResourceAction) uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1));
    final EdmAction action = resource.getAction();
    readParameters(action, request.getBody(), requestFormat);
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  private Map<String, Parameter> readParameters(final EdmAction action, final InputStream body, 
      final ContentType requestFormat) throws ODataApplicationException, DeserializerException {
    if (action.getParameterNames().size() - (action.isBound() ? 1 : 0) > 0) {
      checkRequestFormat(requestFormat);
      return odata.createDeserializer(requestFormat, serviceMetadata).
          actionParameters(body, action).getActionParameters();
    }
    return Collections.<String, Parameter> emptyMap();
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
