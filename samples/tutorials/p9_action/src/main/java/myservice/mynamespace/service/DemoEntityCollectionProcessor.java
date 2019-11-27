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
package myservice.mynamespace.service;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;

import myservice.mynamespace.data.Storage;
import myservice.mynamespace.util.Util;

public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {


  private OData odata;
  private ServiceMetadata serviceMetadata;
  // our database-mock
  private Storage storage;

  public DemoEntityCollectionProcessor(Storage storage) {
    this.storage = storage;
  }

  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  /*
   * This method is invoked when a collection of entities has to be read.
   * In our example, this can be either a "normal" read operation, or a navigation:
   * 
   * Example for "normal" read entity set operation:
   * http://localhost:8080/DemoService/DemoService.svc/Categories
   * 
   * Example for navigation
   * http://localhost:8080/DemoService/DemoService.svc/Categories(3)/Products
   */
  public void readEntityCollection(ODataRequest request, ODataResponse response,
      UriInfo uriInfo, ContentType responseFormat)
      throws ODataApplicationException, SerializerException {
    
    final UriResource firstResourceSegment = uriInfo.getUriResourceParts().get(0);
    
    if(firstResourceSegment instanceof UriResourceEntitySet) {
      readEntityCollectionInternal(request, response, uriInfo, responseFormat);
    } else if(firstResourceSegment instanceof UriResourceFunction) {
      readFunctionImportCollection(request, response, uriInfo, responseFormat);
    } else {
      throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
          Locale.ENGLISH);
    }
  }

  private void readFunctionImportCollection(final ODataRequest request, final ODataResponse response, 
      final UriInfo uriInfo, final ContentType responseFormat) throws ODataApplicationException, SerializerException {
    
    // 1st step: Analyze the URI and fetch the entity collection returned by the function import
    // Function Imports are always the first segment of the resource path
    final UriResource firstSegment = uriInfo.getUriResourceParts().get(0);
    
    if(!(firstSegment instanceof UriResourceFunction)) {
      throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), 
          Locale.ENGLISH);
    }
    
    final UriResourceFunction uriResourceFunction = (UriResourceFunction) firstSegment;
    final EntityCollection entityCol = storage.readFunctionImportCollection(uriResourceFunction, serviceMetadata);
    
    // 2nd step: Serialize the response entity
    final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();
    final ContextURL contextURL = ContextURL.with().asCollection().type(edmEntityType).build();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().contextURL(contextURL).build();
    final ODataSerializer serializer = odata.createSerializer(responseFormat);
    final SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCol, 
        opts); 

    // 3rd configure the response object
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  private void readEntityCollectionInternal(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType responseFormat) throws ODataApplicationException, SerializerException {
    
    EdmEntitySet responseEdmEntitySet = null; // we'll need this to build the ContextURL
    EntityCollection responseEntityCollection = null; // we'll need this to set the response body

    // 1st retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    int segmentCount = resourceParts.size();

    UriResource uriResource = resourceParts.get(0); // in our example, the first segment is the EntitySet
    if (!(uriResource instanceof UriResourceEntitySet)) {
      throw new ODataApplicationException("Only EntitySet is supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
    EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();

    if (segmentCount == 1) { // this is the case for: DemoService/DemoService.svc/Categories
      responseEdmEntitySet = startEdmEntitySet; // the response body is built from the first (and only) entitySet

      // 2nd: fetch the data from backend for this requested EntitySetName and deliver as EntitySet
      responseEntityCollection = storage.readEntitySetData(startEdmEntitySet);
    } else if (segmentCount == 2) { // in case of navigation: DemoService.svc/Categories(3)/Products

      UriResource lastSegment = resourceParts.get(1); // in our example we don't support more complex URIs
      if (lastSegment instanceof UriResourceNavigation) {
        UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) lastSegment;
        EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
        EdmEntityType targetEntityType = edmNavigationProperty.getType();
        // from Categories(1) to Products
        responseEdmEntitySet = Util.getNavigationTargetEntitySet(startEdmEntitySet, edmNavigationProperty);

        // 2nd: fetch the data from backend
        // first fetch the entity where the first segment of the URI points to
        List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
        // e.g. for Categories(3)/Products we have to find the single entity: Category with ID 3
        Entity sourceEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);
        // error handling for e.g. DemoService.svc/Categories(99)/Products
        if (sourceEntity == null) {
          throw new ODataApplicationException("Entity not found.",
              HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
        }
        // then fetch the entity collection where the entity navigates to
        // note: we don't need to check uriResourceNavigation.isCollection(),
        // because we are the EntityCollectionProcessor
        responseEntityCollection = storage.getRelatedEntityCollection(sourceEntity, targetEntityType);
      } else if (lastSegment instanceof UriResourceFunction) {// For bound function
        UriResourceFunction uriResourceFunction = (UriResourceFunction) lastSegment;
        // 2nd: fetch the data from backend
        // first fetch the target entity type 
        String targetEntityType = uriResourceFunction.getFunction().getReturnType().getType().getName();
        // contextURL displays the last segment
        for(EdmEntitySet entitySet : serviceMetadata.getEdm().getEntityContainer().getEntitySets()){
          if(targetEntityType.equals(entitySet.getEntityType().getName())){
            responseEdmEntitySet = entitySet;
            break;
          }
        }
        
        // error handling for null entities
        if (targetEntityType == null || responseEdmEntitySet == null) {
          throw new ODataApplicationException("Entity not found.",
              HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
        }

        // then fetch the entity collection for the target type
        responseEntityCollection = storage.readEntitySetData(targetEntityType);
      }
    } else { // this would be the case for e.g. Products(1)/Category/Products
      throw new ODataApplicationException("Not supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    // 3rd: create and configure a serializer
    ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).build();
    final String id = request.getRawBaseUri() + "/" + responseEdmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
        .contextURL(contextUrl).id(id).build();
    EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();

    ODataSerializer serializer = odata.createSerializer(responseFormat);
    SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType,
        responseEntityCollection, opts);

    // 4th: configure the response object: set the body, headers and status code
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }
}
