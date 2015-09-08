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

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
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
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

import myservice.mynamespace.data.Storage;
import myservice.mynamespace.util.Util;

public class DemoEntityProcessor implements EntityProcessor {

  private OData odata;
  private ServiceMetadata serviceMetadata;
  private Storage storage;

  public DemoEntityProcessor(Storage storage) {
    this.storage = storage;
  }

  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
					throws ODataApplicationException, SerializerException {

	  EdmEntityType responseEdmEntityType = null; // we'll need this to build the ContextURL
    Entity responseEntity = null; // required for serialization of the response body
    EdmEntitySet responseEdmEntitySet = null; // we need this for building the contextUrl

    // 1st step: retrieve the requested Entity: can be "normal" read operation, or navigation (to-one)
    List<UriResource> resourceParts = uriInfo.getUriResourceParts();
    int segmentCount = resourceParts.size();

    UriResource uriResource = resourceParts.get(0); // in our example, the first segment is the EntitySet
    if (!(uriResource instanceof UriResourceEntitySet)) {
      throw new ODataApplicationException("Only EntitySet is supported",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
    }

    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResource;
    EdmEntitySet startEdmEntitySet = uriResourceEntitySet.getEntitySet();

    // Analyze the URI segments
    if (segmentCount == 1) { // no navigation
      responseEdmEntityType = startEdmEntitySet.getEntityType();
      responseEdmEntitySet = startEdmEntitySet; // since we have only one segment

      // 2. step: retrieve the data from backend
      List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
      responseEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);
    } else if (segmentCount == 2) { // navigation
      UriResource navSegment = resourceParts.get(1); // in our example we don't support more complex URIs
      if (navSegment instanceof UriResourceNavigation) {
        UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) navSegment;
        EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
        responseEdmEntityType = edmNavigationProperty.getType();
        // contextURL displays the last segment
        responseEdmEntitySet = Util.getNavigationTargetEntitySet(startEdmEntitySet, edmNavigationProperty);

        // 2nd: fetch the data from backend.
        // e.g. for the URI: Products(1)/Category we have to find the correct Category entity
        List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
        // e.g. for Products(1)/Category we have to find first the Products(1)
        Entity sourceEntity = storage.readEntityData(startEdmEntitySet, keyPredicates);

        // now we have to check if the navigation is
        // a) to-one: e.g. Products(1)/Category
        // b) to-many with key: e.g. Categories(3)/Products(5)
        // the key for nav is used in this case: Categories(3)/Products(5)
        List<UriParameter> navKeyPredicates = uriResourceNavigation.getKeyPredicates();

        if (navKeyPredicates.isEmpty()) { // e.g. DemoService.svc/Products(1)/Category
          responseEntity = storage.getRelatedEntity(sourceEntity, responseEdmEntityType);
        } else { // e.g. DemoService.svc/Categories(3)/Products(5)
          responseEntity = storage.getRelatedEntity(sourceEntity, responseEdmEntityType, navKeyPredicates);
        }
      }
    } else {
      // this would be the case for e.g. Products(1)/Category/Products(1)/Category
      throw new ODataApplicationException("Not supported", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    if (responseEntity == null) {
      // this is the case for e.g. DemoService.svc/Categories(4) or DemoService.svc/Categories(3)/Products(999)
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    }

		// 3. apply system query options

		// handle $select
		SelectOption selectOption = uriInfo.getSelectOption();
		// in our example, we don't have performance issues, so we can rely upon the handling in the Olingo lib
		// nothing else to be done

		// handle $expand
		ExpandOption expandOption = uriInfo.getExpandOption();
		// in our example: http://localhost:8080/DemoService/DemoService.svc/Categories(1)/$expand=Products
		// or http://localhost:8080/DemoService/DemoService.svc/Products(1)?$expand=Category
		if(expandOption != null) {
			// retrieve the EdmNavigationProperty from the expand expression
			// Note: in our example, we have only one NavigationProperty, so we can directly access it
			EdmNavigationProperty edmNavigationProperty = null;
			ExpandItem expandItem = expandOption.getExpandItems().get(0);
			if(expandItem.isStar()) {
				List<EdmNavigationPropertyBinding> bindings = responseEdmEntitySet.getNavigationPropertyBindings();
				// we know that there are navigation bindings
				// however normally in this case a check if navigation bindings exists is done
				if(!bindings.isEmpty()) {
					// can in our case only be 'Category' or 'Products', so we can take the first
					EdmNavigationPropertyBinding binding = bindings.get(0);
					EdmElement property = responseEdmEntitySet.getEntityType().getProperty(binding.getPath());
					// we don't need to handle error cases, as it is done in the Olingo library
					if(property instanceof EdmNavigationProperty) {
						edmNavigationProperty = (EdmNavigationProperty) property;
					}
				}
			} else {
				// can be 'Category' or 'Products', no path supported
				UriResource expandUriResource = expandItem.getResourcePath().getUriResourceParts().get(0);
				// we don't need to handle error cases, as it is done in the Olingo library
				if(expandUriResource instanceof UriResourceNavigation) {
					edmNavigationProperty = ((UriResourceNavigation) expandUriResource).getProperty();
				}
			}

			// can be 'Category' or 'Products', no path supported
			// we don't need to handle error cases, as it is done in the Olingo library
			if(edmNavigationProperty != null) {
				EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();
				String navPropName = edmNavigationProperty.getName();

				// build the inline data
				Link link = new Link();
				link.setTitle(navPropName);
				link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);

				if(edmNavigationProperty.isCollection()){ // in case of Categories(1)/$expand=Products
					// fetch the data for the $expand (to-many navigation) from backend
					// here we get the data for the expand
					EntityCollection expandEntityCollection = storage.getRelatedEntityCollection(responseEntity, expandEdmEntityType);
					link.setInlineEntitySet(expandEntityCollection);
				} else {  // in case of Products(1)?$expand=Category
					// fetch the data for the $expand (to-one navigation) from backend
					// here we get the data for the expand
					Entity expandEntity = storage.getRelatedEntity(responseEntity, expandEdmEntityType);
					link.setInlineEntity(expandEntity);
				}

				// set the link - containing the expanded data - to the current entity
				responseEntity.getNavigationLinks().add(link);
			}
		}


		// 4. serialize
		EdmEntityType edmEntityType = responseEdmEntitySet.getEntityType();
		// we need the property names of the $select, in order to build the context URL
		String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, expandOption, selectOption);
		ContextURL contextUrl = ContextURL.with().entitySet(responseEdmEntitySet)
						.selectList(selectList)
						.suffix(Suffix.ENTITY).build();

		// make sure that $expand and $select are considered by the serializer
		// adding the selectOption to the serializerOpts will actually tell the lib to do the job
		EntitySerializerOptions opts = EntitySerializerOptions.with()
						.contextURL(contextUrl)
						.select(selectOption)
						.expand(expandOption)
						.build();

		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, edmEntityType, responseEntity, opts);

		// 5. configure the response object
		response.setContent(serializerResult.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}
	
	 /*
   * Example request:
   * 
   * POST URL: http://localhost:8080/DemoService/DemoService.svc/Products
   * Header: Content-Type: application/json; odata.metadata=minimal
   * Request body:
    {
      "ID":3,
      "Name":"Ergo Screen",
      "Description":"17 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960"
    }
   * */
  public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
                           ContentType requestFormat, ContentType responseFormat)
        throws ODataApplicationException, DeserializerException, SerializerException {
    
    // 1. Retrieve the entity type from the URI 
    EdmEntitySet edmEntitySet = Util.getEdmEntitySet(uriInfo);
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    // 2. create the data in backend 
    // 2.1. retrieve the payload from the POST request for the entity to create and deserialize it
    InputStream requestInputStream = request.getBody();
    ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
    DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
    Entity requestEntity = result.getEntity();
    // 2.2 do the creation in backend, which returns the newly created entity
    Entity createdEntity = storage.createEntityData(edmEntitySet, requestEntity);
    
    // 3. serialize the response (we have to return the created entity)
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build(); 
    EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build(); // expand and select currently not supported 
    
    ODataSerializer serializer = this.odata.createSerializer(responseFormat);
    SerializerResult serializedResponse = serializer.entity(serviceMetadata, edmEntityType, createdEntity, options);
    
    //4. configure the response object
    response.setContent(serializedResponse.getContent());
    response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  
  public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
                           ContentType requestFormat, ContentType responseFormat)
              throws ODataApplicationException, DeserializerException, SerializerException {
    
    // 1. Retrieve the entity set which belongs to the requested entity 
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // Note: only in our example we can assume that the first segment is the EntitySet
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); 
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    // 2. update the data in backend
    // 2.1. retrieve the payload from the PUT request for the entity to be updated 
    InputStream requestInputStream = request.getBody();
    ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
    DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
    Entity requestEntity = result.getEntity();
    // 2.2 do the modification in backend
    List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
    // Note that this updateEntity()-method is invoked for both PUT or PATCH operations
    HttpMethod httpMethod = request.getMethod();
    storage.updateEntityData(edmEntitySet, keyPredicates, requestEntity, httpMethod);
    
    //3. configure the response object
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }

  
  public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
          throws ODataApplicationException {
    
    // 1. Retrieve the entity set which belongs to the requested entity 
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // Note: only in our example we can assume that the first segment is the EntitySet
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); 
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

    // 2. delete the data in backend
    List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
    storage.deleteEntityData(edmEntitySet, keyPredicates);
    
    //3. configure the response object
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
  }
}
