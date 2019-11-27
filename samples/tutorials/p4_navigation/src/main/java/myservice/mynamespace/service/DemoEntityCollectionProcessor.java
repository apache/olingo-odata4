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

import myservice.mynamespace.data.Storage;
import myservice.mynamespace.util.Util;

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
import org.apache.olingo.server.api.uri.UriResourceNavigation;

public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {

  private OData odata;
  private ServiceMetadata srvMetadata;
  // our database-mock
  private Storage storage;

  public DemoEntityCollectionProcessor(Storage storage) {
    this.storage = storage;
  }

  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.srvMetadata = serviceMetadata;
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

    EdmEntitySet responseEdmEntitySet = null; // we'll need this to build the ContextURL
    EntityCollection responseEntityCollection = null; // we'll need this to set the response body
    EdmEntityType responseEdmEntityType = null;

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
        if (!edmNavigationProperty.containsTarget()) {
       // from Categories(1) to Products
          responseEdmEntitySet = Util.getNavigationTargetEntitySet(startEdmEntitySet, edmNavigationProperty);
        } else {
          responseEdmEntitySet = startEdmEntitySet;
          responseEdmEntityType = targetEntityType;
        }

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
      }
    } else { // this would be the case for e.g. Products(1)/Category/Products
      throw new ODataApplicationException("Not supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    ContextURL contextUrl = null;
    EdmEntityType edmEntityType = null;
    // 3rd: create and configure a serializer
    if (isContNav(uriInfo)) {
      contextUrl = ContextURL.with().entitySetOrSingletonOrType(request.getRawODataPath()).build();
      edmEntityType = responseEdmEntityType;
    } else {
      contextUrl = ContextURL.with().entitySet(responseEdmEntitySet).build(); 
      edmEntityType = responseEdmEntitySet.getEntityType();
    }
    final String id = request.getRawBaseUri() + "/" + responseEdmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
        .contextURL(contextUrl).id(id).build();
    
    ODataSerializer serializer = odata.createSerializer(responseFormat);
    SerializerResult serializerResult = serializer.entityCollection(this.srvMetadata, edmEntityType,
        responseEntityCollection, opts);

    // 4th: configure the response object: set the body, headers and status code
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  private boolean isContNav(UriInfo uriInfo) {
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    for (UriResource resourcePart : resourceParts) {
      if (resourcePart instanceof UriResourceNavigation) {
        UriResourceNavigation navResource = (UriResourceNavigation) resourcePart;
        if (navResource.getProperty().containsTarget()) {
          return true;
        }
      }
    }
    return false;
  }

}
