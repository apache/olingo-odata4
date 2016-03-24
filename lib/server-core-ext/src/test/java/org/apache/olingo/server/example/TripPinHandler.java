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
package org.apache.olingo.server.example;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.requests.ActionRequest;
import org.apache.olingo.server.core.requests.DataRequest;
import org.apache.olingo.server.core.requests.FunctionRequest;
import org.apache.olingo.server.core.requests.MediaRequest;
import org.apache.olingo.server.core.requests.MetadataRequest;
import org.apache.olingo.server.core.requests.ServiceDocumentRequest;
import org.apache.olingo.server.core.responses.CountResponse;
import org.apache.olingo.server.core.responses.EntityResponse;
import org.apache.olingo.server.core.responses.EntitySetResponse;
import org.apache.olingo.server.core.responses.ErrorResponse;
import org.apache.olingo.server.core.responses.MetadataResponse;
import org.apache.olingo.server.core.responses.NoContentResponse;
import org.apache.olingo.server.core.responses.PrimitiveValueResponse;
import org.apache.olingo.server.core.responses.PropertyResponse;
import org.apache.olingo.server.core.responses.ServiceDocumentResponse;
import org.apache.olingo.server.core.responses.ServiceResponse;
import org.apache.olingo.server.core.responses.ServiceResponseVisior;
import org.apache.olingo.server.core.responses.StreamResponse;

public class TripPinHandler implements ServiceHandler {
  private OData odata;
  private ServiceMetadata serviceMetadata;
  private final TripPinDataModel dataModel;

  public TripPinHandler(TripPinDataModel datamodel) {
    this.dataModel = datamodel;
  }

  @Override
  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  @Override
  public void readMetadata(MetadataRequest request, MetadataResponse response)
      throws ODataLibraryException, ODataApplicationException {
    response.writeMetadata();
  }

  @Override
  public void readServiceDocument(ServiceDocumentRequest request, ServiceDocumentResponse response)
      throws ODataLibraryException, ODataApplicationException {
    response.writeServiceDocument(request.getODataRequest().getRawBaseUri());
  }

  static class EntityDetails {
    EntityCollection entitySet = null;
    Entity entity = null;
    EdmEntityType entityType;
    String navigationProperty;
    Entity parentEntity = null;
  }

  private EntityDetails process(final DataRequest request) throws ODataApplicationException {
    EntityCollection entitySet = null;
    Entity entity = null;
    EdmEntityType entityType;
    Entity parentEntity = null;

    if (request.isSingleton()) {
      EdmSingleton singleton = request.getUriResourceSingleton().getSingleton();
      entityType = singleton.getEntityType();
      if (singleton.getName().equals("Me")) {
        entitySet = this.dataModel.getEntitySet("People");
        entity = entitySet.getEntities().get(0);
      }
    } else {
      final EdmEntitySet edmEntitySet = request.getEntitySet();
      entityType = edmEntitySet.getEntityType();
      List<UriParameter> keys = request.getKeyPredicates();

      // TODO: This example so far ignores all system options; but a real
      // service should not
      if (keys != null && !keys.isEmpty()) {
        entity = this.dataModel.getEntity(edmEntitySet.getName(), keys);
      } else {
        int skip = 0;
        if (request.getUriInfo().getSkipTokenOption() != null) {
          skip = Integer.parseInt(request.getUriInfo().getSkipTokenOption().getValue());
        }
        int pageSize = getPageSize(request);
        entitySet = this.dataModel.getEntitySet(edmEntitySet.getName(), skip, pageSize);
        if (entitySet.getEntities().size() == pageSize) {
          try {
            entitySet.setNext(new URI(request.getODataRequest().getRawRequestUri() + "?$skiptoken="
                + (skip + pageSize)));
          } catch (URISyntaxException e) {
            throw new ODataApplicationException(e.getMessage(), 500, Locale.getDefault());
          }
        }
      }
    }
    EntityDetails details = new EntityDetails();

    if (!request.getNavigations().isEmpty() && entity != null) {
      UriResourceNavigation lastNavigation = request.getNavigations().getLast();
      for (UriResourceNavigation nav : request.getNavigations()) {
        entityType = nav.getProperty().getType();
        if (nav.isCollection()) {
          entitySet = this.dataModel.getNavigableEntitySet(entity, nav);
        } else {
          parentEntity = entity;
          entity = this.dataModel.getNavigableEntity(parentEntity, nav);
        }
      }
      details.navigationProperty = lastNavigation.getProperty().getName();
    }

    details.entity = entity;
    details.entitySet = entitySet;
    details.entityType = entityType;
    details.parentEntity = parentEntity;
    return details;
  }

  @Override
  public <T extends ServiceResponse> void read(final DataRequest request, final T response)
      throws ODataLibraryException, ODataApplicationException {

    final EntityDetails details = process(request);

    response.accepts(new ServiceResponseVisior() {
      @Override
      public void visit(CountResponse response) throws ODataLibraryException, ODataApplicationException {
        response.writeCount(details.entitySet.getCount());
      }

      @Override
      public void visit(PrimitiveValueResponse response) throws ODataLibraryException,
          ODataApplicationException {
        EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
        Property property = details.entity.getProperty(edmProperty.getName());
        response.write(property.getValue());
      }

      @Override
      public void visit(PropertyResponse response) throws ODataLibraryException,
          ODataApplicationException {
        EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
        Property property = details.entity.getProperty(edmProperty.getName());
        response.writeProperty(edmProperty.getType(), property);
      }

      @Override
      public void visit(StreamResponse response) throws ODataLibraryException,
          ODataApplicationException {
        // stream property response
        response.writeStreamResponse(new ByteArrayInputStream("dummy".getBytes()),
            ContentType.APPLICATION_OCTET_STREAM);
      }

      @Override
      public void visit(EntitySetResponse response) throws ODataLibraryException,
          ODataApplicationException {
        if (request.getPreference("odata.maxpagesize") != null) {
          response.writeHeader("Preference-Applied", "odata.maxpagesize="+request.getPreference("odata.maxpagesize"));
        }
        if (details.entity == null && !request.getNavigations().isEmpty()) {
          response.writeReadEntitySet(details.entityType, new EntityCollection());
        } else {
          response.writeReadEntitySet(details.entityType, details.entitySet);
        }
      }

      @Override
      public void visit(EntityResponse response) throws ODataLibraryException,
          ODataApplicationException {
        if (details.entity == null && !request.getNavigations().isEmpty()) {
          response.writeNoContent(true);
        } else {
          response.writeReadEntity(details.entityType, details.entity);
        }
      }
    });
  }

  private int getPageSize(DataRequest request) {
    String size = request.getPreference("odata.maxpagesize");
    if (size == null) {
      return 8;
    }
    return Integer.parseInt(size);
  }

  @Override
  public void createEntity(DataRequest request, Entity entity, EntityResponse response)
      throws ODataLibraryException, ODataApplicationException {
    EdmEntitySet edmEntitySet = request.getEntitySet();

    Entity created = this.dataModel.createEntity(edmEntitySet, entity, request.getODataRequest().getRawBaseUri());

    try {
      // create references, they come in "@odata.bind" value
      List<Link> bindings = entity.getNavigationBindings();
      if (bindings != null & !bindings.isEmpty()) {
        for (Link link : bindings) {
          String navigationProperty = link.getTitle();
          String uri = link.getBindingLink();
          if (uri != null) {
            DataRequest bindingRequest = request.parseLink(new URI(uri));

            Entity reference = this.dataModel.getEntity(bindingRequest.getEntitySet().getName(),
                bindingRequest.getKeyPredicates());

            this.dataModel.addNavigationLink(navigationProperty, created, reference);

          } else {
            for (String binding : link.getBindingLinks()) {
              DataRequest bindingRequest = request.parseLink(new URI(binding));

              Entity reference = this.dataModel.getEntity(bindingRequest.getEntitySet().getName(),
                  bindingRequest.getKeyPredicates());

              this.dataModel.addNavigationLink(navigationProperty, created, reference);
            }
          }
        }
      }
    } catch (URISyntaxException e) {
      throw new ODataApplicationException(e.getMessage(), 500, Locale.getDefault());
    }

    response.writeCreatedEntity(edmEntitySet, created);
  }

  @Override
  public void updateEntity(DataRequest request, Entity entity, boolean merge, String eTag,
      EntityResponse response) throws ODataLibraryException, ODataApplicationException {
    EdmEntitySet edmEntitySet = request.getEntitySet();
    
    Entity currentEntity = this.dataModel.getEntity(edmEntitySet.getName(), request.getKeyPredicates());
    if (currentEntity == null) {
      response.writeNotFound(true);
      return;
    }
    String key = edmEntitySet.getEntityType().getKeyPredicateNames().get(0);
    String baseURL = request.getODataRequest().getRawBaseUri();
    boolean updated = this.dataModel.updateEntity(edmEntitySet, eTag, key, currentEntity
        .getProperty(key).getValue(), merge, entity, baseURL);

    if (updated) {
      response.writeUpdatedEntity();
    } else {
      response.writeNotModified();
    }
  }

  @Override
  public void deleteEntity(DataRequest request, String eTag, EntityResponse response)
      throws ODataLibraryException, ODataApplicationException {

    EdmEntitySet edmEntitySet = request.getEntitySet();
    Entity entity = this.dataModel.getEntity(edmEntitySet.getName(), request.getKeyPredicates());
    if (entity == null) {
      response.writeNotFound(true);
      return;
    }
    String key = edmEntitySet.getEntityType().getKeyPredicateNames().get(0);
    boolean removed = this.dataModel.deleteEntity(edmEntitySet.getName(), eTag, key, entity
        .getProperty(key).getValue());

    if (removed) {
      response.writeDeletedEntityOrReference();
    } else {
      response.writeNotFound(true);
    }
  }

  @Override
  public void updateProperty(DataRequest request, final Property property, boolean rawValue, boolean merge,
      String entityETag, PropertyResponse response) throws ODataLibraryException,
      ODataApplicationException {

    if (rawValue && property.getValue() != null) {
      // this is more generic, stricter conversion rules must taken in a real service
      byte[] value = (byte[])property.getValue();
      property.setValue(ValueType.PRIMITIVE, new String(value));
    }
    
    EdmEntitySet edmEntitySet = request.getEntitySet();
    Entity entity = this.dataModel.getEntity(edmEntitySet.getName(), request.getKeyPredicates());
    if (entity == null) {
      response.writeNotFound(true);
      return;
    }

    String key = edmEntitySet.getEntityType().getKeyPredicateNames().get(0);
    boolean replaced = this.dataModel.updateProperty(edmEntitySet.getName(), entityETag, key,
        entity.getProperty(key).getValue(), property);

    if (replaced) {
      if (property.getValue() == null) {
        response.writePropertyDeleted();
      } else {
        response.writePropertyUpdated();
      }
    } else {
      response.writeServerError(true);
    }
  }

  @Override
  public <T extends ServiceResponse> void invoke(FunctionRequest request, HttpMethod method,
      T response) throws ODataLibraryException, ODataApplicationException {
    EdmFunction function = request.getFunction();
    if (function.getName().equals("GetNearestAirport")) {

      final EdmEntityType type = serviceMetadata.getEdm().getEntityContainer(null)
          .getEntitySet("Airports").getEntityType();

      EntityCollection es = this.dataModel.getEntitySet("Airports");
      int i = new Random().nextInt(es.getEntities().size());
      final Entity entity = es.getEntities().get(i);

      response.accepts(new ServiceResponseVisior() {
        @Override
        public void visit(EntityResponse response) throws ODataLibraryException,
            ODataApplicationException {
          response.writeReadEntity(type, entity);
        }
      });
    }
  }

  @Override
  public <T extends ServiceResponse> void invoke(ActionRequest request, String eTag, T response)
      throws ODataLibraryException, ODataApplicationException {
    EdmAction action = request.getAction();
    if (action.getName().equals("ResetDataSource")) {
      try {
        this.dataModel.loadData();
        response.accepts(new ServiceResponseVisior() {
          @Override
          public void visit(NoContentResponse response) throws ODataLibraryException,
              ODataApplicationException {
            response.writeNoContent();
          }
        });
      } catch (Exception e) {
        response.writeServerError(true);
      }
    } else {
      response.writeServerError(true);
    }
  }

  @Override
  public void readMediaStream(MediaRequest request, StreamResponse response)
      throws ODataLibraryException, ODataApplicationException {

    final EdmEntitySet edmEntitySet = request.getEntitySet();
    List<UriParameter> keys = request.getKeyPredicates();
    Entity entity = this.dataModel.getEntity(edmEntitySet.getName(), keys);

    InputStream contents = this.dataModel.readMedia(entity);
    response.writeStreamResponse(contents, request.getResponseContentType());
  }

  @Override
  public void upsertMediaStream(MediaRequest request, String entityETag, InputStream mediaContent,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    final EdmEntitySet edmEntitySet = request.getEntitySet();
    List<UriParameter> keys = request.getKeyPredicates();
    Entity entity = this.dataModel.getEntity(edmEntitySet.getName(), keys);

    if (mediaContent == null) {
      boolean deleted = this.dataModel.deleteMedia(entity);
      if (deleted) {
        response.writeNoContent();
      } else {
        response.writeNotFound();
      }
    } else {
      boolean updated = this.dataModel.updateMedia(entity, mediaContent);
      if (updated) {
        response.writeNoContent();
      } else {
        response.writeServerError(true);
      }
    }
  }

  @Override
  public void upsertStreamProperty(DataRequest request, String entityETag, InputStream streamContent,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    final EdmEntitySet edmEntitySet = request.getEntitySet();
    List<UriParameter> keys = request.getKeyPredicates();
    Entity entity = this.dataModel.getEntity(edmEntitySet.getName(), keys);

    EdmProperty property = request.getUriResourceProperty().getProperty();

    if (streamContent == null) {
      boolean deleted = this.dataModel.deleteStream(entity, property);
      if (deleted) {
        response.writeNoContent();
      } else {
        response.writeNotFound();
      }
    } else {
      boolean updated = this.dataModel.updateStream(entity, property, streamContent);
      if (updated) {
        response.writeNoContent();
      } else {
        response.writeServerError(true);
      }
    }
  }

  @Override
  public void addReference(DataRequest request, String entityETag, URI referenceId,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {

    final EntityDetails details = process(request);

    DataRequest bindingRequest = request.parseLink(referenceId);
    Entity linkEntity = this.dataModel.getEntity(bindingRequest.getEntitySet().getName(),
        bindingRequest.getKeyPredicates());
    this.dataModel.addNavigationLink(details.navigationProperty, details.entity, linkEntity);
    response.writeNoContent();
  }

  @Override
  public void updateReference(DataRequest request, String entityETag, URI updateId,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    // this single valued navigation.
    boolean updated = false;
    final EntityDetails details = process(request);
    DataRequest updateRequest = request.parseLink(updateId);
    Entity updateEntity = this.dataModel.getEntity(updateRequest.getEntitySet().getName(),
        updateRequest.getKeyPredicates());
    if (updateEntity != null) {
      updated = this.dataModel.updateNavigationLink(details.navigationProperty,
        details.parentEntity, updateEntity);
    }

    if (updated) {
      response.writeNoContent();
    } else {
      response.writeServerError(true);
    }
  }

  @Override
  public void deleteReference(DataRequest request, URI deleteId, String entityETag,
      NoContentResponse response) throws ODataLibraryException, ODataApplicationException {
    boolean removed = false;
    if (deleteId != null) {
      final EntityDetails details = process(request);
      DataRequest deleteRequest = request.parseLink(deleteId);
      Entity deleteEntity = this.dataModel.getEntity(deleteRequest.getEntitySet().getName(),
          deleteRequest.getKeyPredicates());
      if (deleteEntity != null) {
        removed = this.dataModel.removeNavigationLink(details.navigationProperty, details.entity,
            deleteEntity);
      }
    } else {
      // this single valued navigation.
      final EntityDetails details = process(request);
      removed = this.dataModel.removeNavigationLink(details.navigationProperty,
          details.parentEntity, details.entity);
    }
    if (removed) {
      response.writeNoContent();
    } else {
      response.writeServerError(true);
    }
  }

  @Override
  public void anyUnsupported(ODataRequest request, ODataResponse response)
      throws ODataLibraryException, ODataApplicationException {
    response.setStatusCode(500);
  }

  @Override
  public String startTransaction() {
    return null;
  }

  @Override
  public void commit(String txnId) {
  }

  @Override
  public void rollback(String txnId) {
  }

  @Override
  public void crossJoin(DataRequest dataRequest, List<String> entitySetNames, ODataResponse response) {
    response.setStatusCode(200);
  }

  @Override
  public void upsertEntity(DataRequest request, Entity entity, boolean merge, String entityETag, 
      EntityResponse response) throws ODataLibraryException, ODataApplicationException {
    EdmEntitySet edmEntitySet = request.getEntitySet();
    Entity currentEntity = this.dataModel.getEntity(edmEntitySet.getName(), request.getKeyPredicates());
    if(currentEntity == null) {
      createEntity(request, entity, response);
    } else {
      updateEntity(request, entity, merge, entityETag, response);
    }
  }

  @Override
  public boolean supportsDataIsolation() {
    return false;
  }
  
  @Override
  public void processError(ODataServerError error, ErrorResponse response) {
    error.getException().printStackTrace();
    response.writeError(error);
  }
}
