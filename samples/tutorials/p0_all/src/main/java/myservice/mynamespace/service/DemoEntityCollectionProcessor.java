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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
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

    // 1st: retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // in our example, the first segment is the EntitySet
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

    // 2nd: fetch the data from backend for this requested EntitySetName and deliver as EntitySet
    EntityCollection entityCollection = storage.readEntitySetData(edmEntitySet);
    EntityCollection modifiedEntityCollection = new EntityCollection();
    List<Entity> modifiedEntityList = new ArrayList<Entity>();
    modifiedEntityList.addAll(entityCollection.getEntities());
		
		// 3rd: Apply system query option
		// The system query options have to be applied in a defined order
		// 3.1.) $filter
    modifiedEntityList = applyFilterQueryOption(modifiedEntityList, uriInfo.getFilterOption());
    // 3.2.) $orderby
    modifiedEntityList = applyOrderQueryOption(modifiedEntityList, uriInfo.getOrderByOption());
    // 3.3.) $count
    modifiedEntityList = applyCountQueryOption(modifiedEntityCollection, modifiedEntityList, uriInfo.getCountOption());
    // 3.4.) $skip
    modifiedEntityList = applySkipQueryOption(modifiedEntityList, uriInfo.getSkipOption());
    // 3.5.) $top
    modifiedEntityList = applyTopQueryOption(modifiedEntityList, uriInfo.getTopOption());
    // 3.6.) Server driven paging (not part of this tutorial)
    // 3.7.) $expand
    modifiedEntityList = applyExpandQueryOption(modifiedEntityList, edmEntitySet, uriInfo.getExpandOption());
    // 3.8.) $select
    SelectOption selectOption = uriInfo.getSelectOption();
    
    // Set the (may) modified entityList to the new entity collection
    modifiedEntityCollection.getEntities().addAll(modifiedEntityList);
    
    // 4th: create a serializer based on the requested format (json)
    ODataSerializer serializer = odata.createSerializer(responseFormat);
		
		// we need the property names of the $select, in order to build the context URL
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    String selectList = odata.createUriHelper()
                             .buildContextURLSelectList(edmEntityType, uriInfo.getExpandOption(), selectOption);
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).selectList(selectList).build();

    // adding the selectOption to the serializerOpts will actually tell the lib to do the job
    final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
    EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
                                                                              .contextURL(contextUrl)
                                                                              .count(uriInfo.getCountOption())
                                                                              .select(selectOption)
                                                                              .expand(uriInfo.getExpandOption())
                                                                              .id(id)
                                                                              .build();

    // and serialize the content: transform from the EntitySet object to InputStream
    SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType,
                                                                    modifiedEntityCollection, opts);
    InputStream serializedContent = serializerResult.getContent();

    // 5th: configure the response object: set the body, headers and status code
    response.setContent(serializedContent);
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

  private List<Entity> applyExpandQueryOption(List<Entity> modifiedEntityList,
      EdmEntitySet edmEntitySet, ExpandOption expandOption) {

    // in our example: http://localhost:8080/DemoService/DemoService.svc/Categories/$expand=Products
    // or http://localhost:8080/DemoService/DemoService.svc/Products?$expand=Category
    if (expandOption != null) {
      // retrieve the EdmNavigationProperty from the expand expression
      // Note: in our example, we have only one NavigationProperty, so we can directly access it
      EdmNavigationProperty edmNavigationProperty = null;
      ExpandItem expandItem = expandOption.getExpandItems().get(0);
      if (expandItem.isStar()) {
        List<EdmNavigationPropertyBinding> bindings = edmEntitySet.getNavigationPropertyBindings();
        // we know that there are navigation bindings
        // however normally in this case a check if navigation bindings exists is done
        if (!bindings.isEmpty()) {
          // can in our case only be 'Category' or 'Products', so we can take the first
          EdmNavigationPropertyBinding binding = bindings.get(0);
          EdmElement property = edmEntitySet.getEntityType().getProperty(binding.getPath());
          // we don't need to handle error cases, as it is done in the Olingo library
          if (property instanceof EdmNavigationProperty) {
            edmNavigationProperty = (EdmNavigationProperty) property;
          }
        }
      } else {
        // can be 'Category' or 'Products', no path supported
        UriResource uriResource = expandItem.getResourcePath().getUriResourceParts().get(0);
        // we don't need to handle error cases, as it is done in the Olingo library
        if (uriResource instanceof UriResourceNavigation) {
          edmNavigationProperty = ((UriResourceNavigation) uriResource).getProperty();
        }
      }

      // can be 'Category' or 'Products', no path supported
      // we don't need to handle error cases, as it is done in the Olingo library
      if (edmNavigationProperty != null) {
        String navPropName = edmNavigationProperty.getName();
        EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();

        for (Entity entity : modifiedEntityList) {
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

    return modifiedEntityList;
  }

  private List<Entity> applyTopQueryOption(List<Entity> entityList, TopOption topOption)
      throws ODataApplicationException {

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
    
    return entityList;
  }

  private List<Entity> applySkipQueryOption(List<Entity> entityList, SkipOption skipOption)
      throws ODataApplicationException {

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
    
    return entityList;
  }

  private List<Entity> applyCountQueryOption(EntityCollection entityCollection, List<Entity> modifiedEntityList, 
      CountOption countOption) {

    // handle $count: always return the original number of entities, without considering $top and $skip
    if (countOption != null) {
      boolean isCount = countOption.getValue();
      if (isCount) {
        entityCollection.setCount(modifiedEntityList.size());
      }
    }
    
    return modifiedEntityList;
  }

  private List<Entity> applyOrderQueryOption(List<Entity> entityList, OrderByOption orderByOption) {

    if (orderByOption != null) {
      List<OrderByItem> orderItemList = orderByOption.getOrders();
      final OrderByItem orderByItem = orderItemList.get(0); // in our example we support only one
      Expression expression = orderByItem.getExpression();
      if (expression instanceof Member) {
        UriInfoResource resourcePath = ((Member) expression).getResourcePath();
        UriResource uriResource = resourcePath.getUriResourceParts().get(0);
        if (uriResource instanceof UriResourcePrimitiveProperty) {
          EdmProperty edmProperty = ((UriResourcePrimitiveProperty) uriResource).getProperty();
          final String sortPropertyName = edmProperty.getName();

          // do the sorting for the list of entities  
          Collections.sort(entityList, new Comparator<Entity>() {

            // we delegate the sorting to the native sorter of Integer and String
            public int compare(Entity entity1, Entity entity2) {
              int compareResult = 0;

              if (sortPropertyName.equals("ID")) {
                Integer integer1 = (Integer) entity1.getProperty(sortPropertyName).getValue();
                Integer integer2 = (Integer) entity2.getProperty(sortPropertyName).getValue();

                compareResult = integer1.compareTo(integer2);
              } else {
                String propertyValue1 = (String) entity1.getProperty(sortPropertyName).getValue();
                String propertyValue2 = (String) entity2.getProperty(sortPropertyName).getValue();

                compareResult = propertyValue1.compareTo(propertyValue2);
              }

              // if 'desc' is specified in the URI, change the order of the list 
              if (orderByItem.isDescending()) {
                return -compareResult; // just convert the result to negative value to change the order
              }

              return compareResult;
            }
          });
        }
      }
    }
    
    return entityList;
  }

  private List<Entity> applyFilterQueryOption(List<Entity> entityList, FilterOption filterOption)
      throws ODataApplicationException {

    if (filterOption != null) {
      try {
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
          if (visitorResult instanceof Boolean) {
            if (!Boolean.TRUE.equals(visitorResult)) {
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
    
    return entityList;
  }
}
