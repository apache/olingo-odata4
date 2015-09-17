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

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import myservice.mynamespace.data.Storage;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
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
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;

public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {

  private OData odata;
  private ServiceMetadata serviceMetadata;
  private Storage storage;

  public DemoEntityCollectionProcessor(Storage storage) {
    this.storage = storage;
  }

  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
      ContentType responseFormat) throws ODataApplicationException, SerializerException {

    // 1st retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // in our example, the first segment is the EntitySet
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

    // 2nd: fetch the data from backend for this requested EntitySetName and deliver as EntitySet
    EntityCollection entityCollection = storage.readEntitySetData(edmEntitySet);

    // 3rd: apply System Query Options
    // modify the result set according to the query options, specified by the end user
    List<Entity> entityList = entityCollection.getEntities();
    EntityCollection returnEntityCollection = new EntityCollection();

    // handle $count: always return the original number of entities, without considering $top and $skip
    CountOption countOption = uriInfo.getCountOption();
    if (countOption != null) {
      boolean isCount = countOption.getValue();
      if (isCount) {
        returnEntityCollection.setCount(entityList.size());
      }
    }
    
    // handle $skip
    SkipOption skipOption = uriInfo.getSkipOption();
    if (skipOption != null) {
      int skipNumber = skipOption.getValue();
      if (skipNumber >= 0) {
        if(skipNumber <= entityList.size()) {
          entityList = entityList.subList(skipNumber, entityList.size());
        } else {
          // The client skipped all entities
          entityList.clear();
        }
      } else {
        throw new ODataApplicationException("Invalid value for $skip", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }
    
    // handle $top
    TopOption topOption = uriInfo.getTopOption();
    if (topOption != null) {
      int topNumber = topOption.getValue();
      if (topNumber >= 0) {
        if(topNumber <= entityList.size()) {
          entityList = entityList.subList(0, topNumber);
        }  // else the client has requested more entities than available => return what we have
      } else {
        throw new ODataApplicationException("Invalid value for $top", HttpStatusCode.BAD_REQUEST.getStatusCode(),
            Locale.ROOT);
      }
    }

    // after applying the system query options, create the EntityCollection based on the reduced list
    for (Entity entity : entityList) {
      returnEntityCollection.getEntities().add(entity);
    }

    // 4th: create a serializer based on the requested format (json)
    ODataSerializer serializer = odata.createSerializer(responseFormat);

    // and serialize the content: transform from the EntitySet object to InputStream
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

    final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
    EntityCollectionSerializerOptions opts =
        EntityCollectionSerializerOptions.with().contextURL(contextUrl).id(id).count(countOption).build();
    SerializerResult serializerResult =
        serializer.entityCollection(serviceMetadata, edmEntityType, returnEntityCollection, opts);
    InputStream serializedContent = serializerResult.getContent();

    // 5th: configure the response object: set the body, headers and status code
    response.setContent(serializedContent);
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }
}
