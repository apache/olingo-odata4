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
import java.util.Iterator;
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
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;

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

		// 1st: retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		// in our example, the first segment is the EntitySet
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); 
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		// 2nd: fetch the data from backend for this requested EntitySetName and deliver as EntitySet
		EntityCollection entityCollection = storage.readEntitySetData(edmEntitySet);
		
		// 3rd: Check if filter system query option is provided and apply the expression if necessary
		FilterOption filterOption = uriInfo.getFilterOption();
		if(filterOption != null) {
			// Apply $filter system query option
			try {
			      List<Entity> entityList = entityCollection.getEntities();
			      Iterator<Entity> entityIterator = entityList.iterator();
			      
			      // Evaluate the expression for each entity
			      // If the expression is evaluated to "true", keep the entity otherwise remove it from the entityList
			      while (entityIterator.hasNext()) {
			    	  // To evaluate the the expression, create an instance of the Filter Expression Visitor and pass
			    	  // the current entity to the constructor
			    	  Entity currentEntity = entityIterator.next();
			    	  Expression filterExpression = filterOption.getExpression();
			    	  FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(currentEntity);
			    	  
			    	  // Start evaluating the expression
			    	  Object visitorResult = filterExpression.accept(expressionVisitor);
			    	  
			    	  // The result of the filter expression must be of type Edm.Boolean
			    	  if(visitorResult instanceof Boolean) {
			    		  if(!Boolean.TRUE.equals(visitorResult)) {
			    		    // The expression evaluated to false (or null), so we have to remove the currentEntity from entityList
			    		    entityIterator.remove();
			    		  }
			    	  } else {
			    		  throw new ODataApplicationException("A filter expression must evaulate to type Edm.Boolean", 
			    		      HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
			    	  }
			      }

			    } catch (ExpressionVisitException e) {
			      throw new ODataApplicationException("Exception in filter evaluation",
			          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
			    }
		}
		
		// 4th: create a serializer based on the requested format (json)
		ODataSerializer serializer = odata.createSerializer(responseFormat);

		// and serialize the content: transform from the EntitySet object to InputStream
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
				.contextURL(contextUrl).id(id).build();
		SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection,
				opts);

		InputStream serializedContent = serializerResult.getContent();

		// 5th: configure the response object: set the body, headers and status code
		response.setContent(serializedContent);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}
}
