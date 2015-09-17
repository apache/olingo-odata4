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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import myservice.mynamespace.data.Storage;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
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
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;

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
		List<Entity> entityList = entityCollection.getEntities();
		
		// 3rd apply $orderby
		OrderByOption orderByOption = uriInfo.getOrderByOption();
		if (orderByOption != null) {
			List<OrderByItem> orderItemList = orderByOption.getOrders();
			final OrderByItem orderByItem = orderItemList.get(0); // in our example we support only one
			Expression expression = orderByItem.getExpression();
			if(expression instanceof Member){
				UriInfoResource resourcePath = ((Member)expression).getResourcePath();
				UriResource uriResource = resourcePath.getUriResourceParts().get(0);
				if (uriResource instanceof UriResourcePrimitiveProperty) {
					EdmProperty edmProperty = ((UriResourcePrimitiveProperty)uriResource).getProperty();
					final String sortPropertyName = edmProperty.getName();

					// do the sorting for the list of entities  
					Collections.sort(entityList, new Comparator<Entity>() {

						// we delegate the sorting to the native sorter of Integer and String
						public int compare(Entity entity1, Entity entity2) {
							int compareResult = 0;

							if(sortPropertyName.equals("ID")){
								Integer integer1 = (Integer) entity1.getProperty(sortPropertyName).getValue();
								Integer integer2 = (Integer) entity2.getProperty(sortPropertyName).getValue();
								
								compareResult = integer1.compareTo(integer2);
							}else{
								String propertyValue1 = (String) entity1.getProperty(sortPropertyName).getValue();
								String propertyValue2 = (String) entity2.getProperty(sortPropertyName).getValue();
								
								compareResult = propertyValue1.compareTo(propertyValue2);
							}

							// if 'desc' is specified in the URI, change the order of the list 
							if(orderByItem.isDescending()){
								return - compareResult; // just convert the result to negative value to change the order
							}
							
							return compareResult;
						}
					});
				}
			}
		}
		
		
		// 4th: create a serializer based on the requested format (json)
		ODataSerializer serializer = odata.createSerializer(responseFormat);

		// and serialize the content: transform from the EntitySet object to InputStream
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts =
				EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
    SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, 
                                                                    entityCollection, opts);
		InputStream serializedContent = serializerResult.getContent();

		// 5th: configure the response object: set the body, headers and status code
		response.setContent(serializedContent);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}
}
