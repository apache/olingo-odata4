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

import myservice.mynamespace.data.Storage;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
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
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

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
   * This method is invoked when a collection of entities has to be read. In
   * our example, this can be either a "normal" read operation, or a
   * navigation:
   * 
   * Example for "normal" read entity set operation:
   * http://localhost:8080/DemoService/DemoService.svc/Categories
   * 
   * Example for navigation
   * http://localhost:8080/DemoService/DemoService.svc/Categories(3)/Products
   */
  public void readEntityCollection(ODataRequest request,
      ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
      throws ODataApplicationException, SerializerException {

    // 1st retrieve the requested EdmEntitySet from the uriInfo
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // in our example, the first segment is the EntitySet
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

    // 2nd: fetch the data from backend for this requested EntitySetName
    EntityCollection entityCollection = storage.readEntitySetData(edmEntitySet);

    // 3rd: apply system query options
    SelectOption selectOption = uriInfo.getSelectOption();
    ExpandOption expandOption = uriInfo.getExpandOption();

    // handle $expand
    // in our example: http://localhost:8080/DemoService/DemoService.svc/Categories/$expand=Products
    // or http://localhost:8080/DemoService/DemoService.svc/Products?$expand=Category
    if (expandOption != null) {
      // retrieve the EdmNavigationProperty from the expand expression
      // Note: in our example, we have only one NavigationProperty, so we can directly access it
      EdmNavigationProperty edmNavigationProperty = null;
      ExpandItem expandItem = expandOption.getExpandItems().get(0);
      if(expandItem.isStar()) {
        List<EdmNavigationPropertyBinding> bindings = edmEntitySet.getNavigationPropertyBindings();
        // we know that there are navigation bindings
        // however normally in this case a check if navigation bindings exists is done
        if(!bindings.isEmpty()) {
          // can in our case only be 'Category' or 'Products', so we can take the first
          EdmNavigationPropertyBinding binding = bindings.get(0);
          EdmElement property = edmEntitySet.getEntityType().getProperty(binding.getPath());
          // we don't need to handle error cases, as it is done in the Olingo library
          if(property instanceof EdmNavigationProperty) {
            edmNavigationProperty = (EdmNavigationProperty) property;
          }
        }
      } else {
        // can be 'Category' or 'Products', no path supported
        UriResource uriResource = expandItem.getResourcePath().getUriResourceParts().get(0);
        // we don't need to handle error cases, as it is done in the Olingo library
        if(uriResource instanceof UriResourceNavigation) {
          edmNavigationProperty = ((UriResourceNavigation) uriResource).getProperty();
        }
      }

      // can be 'Category' or 'Products', no path supported
      // we don't need to handle error cases, as it is done in the Olingo library
      if(edmNavigationProperty != null) {
        String navPropName = edmNavigationProperty.getName();
        EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();

        List<Entity> entityList = entityCollection.getEntities();
        for (Entity entity : entityList) {
          Link link = new Link();
          link.setTitle(navPropName);
          link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
          link.setRel(Constants.NS_ASSOCIATION_LINK_REL + navPropName);

          if (edmNavigationProperty.isCollection()) { // in case of Categories/$expand=Products
            // fetch the data for the $expand (to-many navigation) from backend
            EntityCollection expandEntityCollection = storage.getRelatedEntityCollection(entity, expandEdmEntityType);
            link.setInlineEntitySet(expandEntityCollection);
            link.setHref(expandEntityCollection.getId().toASCIIString());
          } else { // in case of Products?$expand=Category
            // fetch the data for the $expand (to-one navigation) from backend
            // here we get the data for the expand
            Entity expandEntity = storage.getRelatedEntity(entity, expandEdmEntityType);
            link.setInlineEntity(expandEntity);
            link.setHref(expandEntity.getId().toASCIIString());
          }

          // set the link - containing the expanded data - to the current entity
          entity.getNavigationLinks().add(link);
        }
      }
    }

    // 4th: serialize
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    // we need the property names of the $select, in order to build the context URL
    String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, expandOption, selectOption);
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).selectList(selectList).build();

    // adding the selectOption to the serializerOpts will actually tell the lib to do the job
    final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
        .contextURL(contextUrl)
        .select(selectOption)
        .expand(expandOption)
        .id(id)
        .build();

    ODataSerializer serializer = odata.createSerializer(responseFormat);
    SerializerResult serializerResult = serializer.entityCollection(srvMetadata, edmEntityType, entityCollection, opts);

    // 5th: configure the response object: set the body, headers and status code
    response.setContent(serializerResult.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

}
