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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriParameter;

public class TripPinHandler implements ServiceHandler {
  private OData odata;
  private ServiceMetadata serviceMetadata;
  private final HashMap<String, EntitySet> entitySetMap;

  public TripPinHandler(HashMap<String, EntitySet> map) {
    this.entitySetMap = map;
  }

  @Override
  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  @Override
  public void readMetadata(MetadataRequest request, MetadataResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    response.writeMetadata(request.getServiceMetaData());
  }

  @Override
  public void readServiceDocument(ServiceDocumentRequest request, ServiceDocumentResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    response.writeServiceDocument(request.getServiceMetaData(), request.getODataRequest()
        .getRawBaseUri());
  }

  @Override
  public <T extends ServiceResponse> void read(final DataRequest request, final T response)
      throws ODataTranslatedException, ODataApplicationException {

    EntitySet data = null;
    Entity entityData = null;
    final EdmEntityType edmEntityType;

    if (request.isSingleton()) {
      EdmSingleton singleton = request.getUriResourceSingleton().getSingleton();
      edmEntityType = singleton.getEntityType();
      if (singleton.getName().equals("Me")) {
        data = getEntitySetData("People", -1, -1);
        entityData = data.getEntities().get(0);
      }
    } else {
      final EdmEntitySet edmEntitySet = request.getEntitySet();
      edmEntityType = edmEntitySet.getEntityType();
      List<UriParameter> keys = request.getKeyPredicates();

      // TODO: This example so far ignores all system options; but a real
      // service should not
      if (keys != null && !keys.isEmpty()) {
        entityData = getEntity(edmEntitySet, keys);
      } else {
        int skip = 0;
        if (request.getUriInfo().getSkipTokenOption() != null) {
          skip = Integer.parseInt(request.getUriInfo().getSkipTokenOption().getValue());
        }
        int pageSize = getPageSize(request);
        data = getEntitySetData(edmEntitySet.getName(), skip, pageSize);
        if (data.getEntities().size() == pageSize) {
          try {
            data.setNext(new URI(request.getODataRequest().getRawRequestUri() + "?$skiptoken="
                + (skip + pageSize)));
          } catch (URISyntaxException e) {
            throw new ODataApplicationException(e.getMessage(), 500, Locale.getDefault());
          }
        }
      }
    }

    final EntitySet dataES = data;
    final Entity dataEntity = entityData;

    response.accepts(new ServiceResponseVisior() {
      @Override
      void visit(CountResponse response) throws ODataTranslatedException, ODataApplicationException {
        response.writeCount(dataES.getCount());
      }

      @Override
      void visit(PrimitiveValueResponse response) throws ODataTranslatedException,
          ODataApplicationException {
        EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
        Property property = dataEntity.getProperty(edmProperty.getName());
        response.write(property.getValue());
      }

      @Override
      void visit(PropertyResponse response) throws ODataTranslatedException,
          ODataApplicationException {
        EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
        Property property = dataEntity.getProperty(edmProperty.getName());
        response.writeProperty(edmProperty.getType(), property);
      }

      @Override
      public void visit(StreamResponse streamResponse) throws ODataTranslatedException,
          ODataApplicationException {
        // no stream properties in example.
      }

      @Override
      public void visit(EntitySetResponse response) throws ODataTranslatedException,
          ODataApplicationException {
        response.writeReadEntitySet(edmEntityType, dataES);
      }

      @Override
      public void visit(EntityResponse response) throws ODataTranslatedException,
          ODataApplicationException {
        response.writeReadEntity(edmEntityType, dataEntity);
      }
    });
  }

  private int getPageSize(DataRequest request) {
    String size = request.getPreferences().get("odata.maxpagesize");
    if (size == null) {
      return 8;
    }
    return Integer.parseInt(size);
  }

  @Override
  public void createEntity(DataRequest request, Entity entity, EntityResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    EdmEntitySet entitySet = request.getEntitySet();
    EntitySet set = this.entitySetMap.get(entitySet.getName());
    set.getEntities().add(entity);

    String id = buildLocation(entity, entitySet);
    String location = request.getODataRequest().getRawBaseUri() + id;
    try {
      entity.setId(new URI(id));
      ((EntityImpl) entity).setETag(UUID.randomUUID().toString());
    } catch (URISyntaxException e) {
      throw new ODataApplicationException("Failed to create ID for entity", 500,
          Locale.getDefault());
    }
    response.writeCreatedEntity(entitySet.getEntityType(), entity, location);
  }

  static String buildLocation(Entity entity, EdmEntitySet entitySet) {
    String location = "/" + entitySet.getName() + "(";
    int i = 0;
    boolean usename = entitySet.getEntityType().getKeyPredicateNames().size() > 1;

    for (String key : entitySet.getEntityType().getKeyPredicateNames()) {
      if (i > 0) {
        location += ",";
      }
      i++;
      if (usename) {
        location += (key + "=");
      }
      location = location + "'" + entity.getProperty(key).getValue().toString() + "'";
    }
    location += ")";
    return location;
  }

  @Override
  public void updateEntity(DataRequest request, Entity entity, boolean merge, String entityETag,
      EntityResponse response) throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void deleteEntity(DataRequest request, String eTag, EntityResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    EdmEntitySet entitySet = request.getEntitySet();
    EntitySet set = this.entitySetMap.get(entitySet.getName());
    String id = request.getODataRequest().getRawODataPath();
    Iterator<Entity> it = set.getEntities().iterator();
    boolean removed = false;
    while (it.hasNext()) {
      Entity entity = it.next();
      if (entity.getId().toASCIIString().equals(id) && eTag.equals("*")
          || eTag.equals(entity.getETag())) {
        it.remove();
        removed = true;
        break;
      }
    }
    if (removed) {
      response.writeDeletedEntityOrReference();
    } else {
      response.writeNotFound(true);
    }
  }

  @Override
  public void updateProperty(DataRequest request, final Property property, boolean merge,
      String entityETag, PropertyResponse response) throws ODataTranslatedException,
      ODataApplicationException {
    Entity entity = getEntity(request.getEntitySet(), request.getKeyPredicates());
    EdmProperty edmProperty = request.getUriResourceProperty().getProperty();
    if (property.getValue() == null) {
      entity.getProperty(edmProperty.getName()).setValue(null, null);
    } else {
      entity.getProperty(edmProperty.getName()).setValue(property.getValueType(),
          property.getValue());
    }

    if (property.getValue() == null) {
      response.writePropertyDeleted();
    } else {
      response.writePropertyUpdated();
    }
  }

  @Override
  public <T extends ServiceResponse> void invoke(FunctionRequest request, HttpMethod method,
      T response) throws ODataTranslatedException, ODataApplicationException {
    EdmFunction function = request.getFunction();
    if (function.getName().equals("GetNearestAirport")) {

      final EdmEntityType type = serviceMetadata.getEdm().getEntityContainer(null)
          .getEntitySet("Airports").getEntityType();

      EntitySet es = getEntitySetData("Airports", -1, -1);
      int i = new Random().nextInt(es.getEntities().size());
      final Entity entity = es.getEntities().get(i);

      response.accepts(new ServiceResponseVisior() {
        @Override
        void visit(EntityResponse response) throws ODataTranslatedException,
            ODataApplicationException {
          response.writeReadEntity(type, entity);
        }
      });
    }
  }

  @Override
  public <T extends ServiceResponse> void invoke(ActionRequest request, String eTag, T response)
      throws ODataTranslatedException, ODataApplicationException {
    EdmAction action = request.getAction();
    if (action.getName().equals("ResetDataSource")) {
      response.accepts(new ServiceResponseVisior() {
        @Override
        void visit(NoContentResponse response) throws ODataTranslatedException,
            ODataApplicationException {
          response.writeNoContent();
        }
      });
    } else {
      response.writeServerError(true);
    }
  }

  @Override
  public void createMediaEntity(DataRequest request, Entity entity, InputStream media,
      EntityResponse response) throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void readMediaStream(MediaRequest request, StreamResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    try {
      response.writeStreamResponse(new FileInputStream(new File(
          "src/test/resources/OlingoOrangeTM.png")), request.getResponseContentType());
    } catch (FileNotFoundException e) {
      response.writeServerError(true);
    }
  }

  @Override
  public void updateMediaStream(MediaRequest request, String entityETag, StreamResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void deleteMediaStream(MediaRequest request, String eTag, NoContentResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void updateStreamProperty(DataRequest request, InputStream property, String entityETag,
      StreamResponse response) throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void addReference(DataRequest request, String entityETag, URI uri,
      NoContentResponse response) throws ODataTranslatedException, ODataApplicationException {
    // TODO: implement adding a reference.
    response.writeNoContent();
  }

  @Override
  public void updateReference(DataRequest request, String entityETag, URI uri,
      NoContentResponse response) throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void deleteReference(DataRequest request, String entityETag, NoContentResponse response)
      throws ODataTranslatedException, ODataApplicationException {
    response.writeServerError(true);
  }

  @Override
  public void anyUnsupported(ODataRequest request, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException {
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

  private org.apache.olingo.commons.api.data.EntitySet getEntitySetData(String name, int skip,
      int pageSize) {
    EntitySet set = this.entitySetMap.get(name);
    if (set == null) {
      return null;
    }

    EntitySetImpl modifiedES = new EntitySetImpl();
    int i = 0;
    for (Entity e : set.getEntities()) {
      if (skip >= 0 && i >= skip && modifiedES.getEntities().size() < pageSize) {
        modifiedES.getEntities().add(e);
      }
      i++;
    }
    modifiedES.setCount(i);
    set.setCount(i);

    if (skip == -1 && pageSize == -1) {
      return set;
    }
    return modifiedES;
  }

  private List<Entity> getMatch(UriParameter param, List<Entity> es) {
    ArrayList<Entity> list = new ArrayList<Entity>();
    for (Entity entity : es) {
      Property property = entity.getProperty(param.getName());
      Object match = LiteralParser.parseLiteral(param.getText());
      if (match.equals(property.asPrimitive())) {
        list.add(entity);
      }
    }
    return list;
  }

  private Entity getEntity(EdmEntitySet esType, List<UriParameter> keys) {
    EntitySet es = getEntitySetData(esType.getName(), -1, -1);
    List<Entity> search = es.getEntities();
    for (UriParameter param : keys) {
      search = getMatch(param, search);
    }
    if (search.isEmpty()) {
      return null;
    }
    return search.get(0);
  }
}
