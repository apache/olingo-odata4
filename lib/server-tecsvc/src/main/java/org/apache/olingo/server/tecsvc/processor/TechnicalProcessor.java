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
package org.apache.olingo.server.tecsvc.processor;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.DeletedEntity;
import org.apache.olingo.commons.api.data.DeltaLink;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceSingleton;
import org.apache.olingo.server.api.uri.queryoption.expression.Binary;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.tecsvc.data.DataProvider;

/**
 * Technical Processor base.
 */
public abstract class TechnicalProcessor implements Processor {

  protected final DataProvider dataProvider;
  protected OData odata;
  protected ServiceMetadata serviceMetadata;

  protected TechnicalProcessor(final DataProvider dataProvider) {
    this(dataProvider, null);
  }

  protected TechnicalProcessor(final DataProvider dataProvider, final ServiceMetadata serviceMetadata) {
    this.dataProvider = dataProvider;
    this.serviceMetadata = serviceMetadata;
  }

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  protected EdmEntitySet getEdmEntitySet(final UriInfoResource uriInfo) throws ODataApplicationException {
    EdmEntitySet entitySet = null;
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    EdmSingleton singleton = null;
    
    // First must be an entity, an entity collection, a function import, or an action import.
    blockTypeFilters(resourcePaths.get(0));
    if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
      entitySet = getEntitySetBasedOnTypeCast(((UriResourceEntitySet)resourcePaths.get(0)));
    } else if (resourcePaths.get(0) instanceof UriResourceFunction) {
      entitySet = ((UriResourceFunction) resourcePaths.get(0)).getFunctionImport().getReturnedEntitySet();
    } else if (resourcePaths.get(0) instanceof UriResourceAction) {
      entitySet = ((UriResourceAction) resourcePaths.get(0)).getActionImport().getReturnedEntitySet();
    }else if (resourcePaths.get(0) instanceof UriResourceSingleton ) {      
      singleton =((UriResourceSingleton) resourcePaths.get(0)).getSingleton();
    } else {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    entitySet = (EdmEntitySet) getEntitySetForNavigation(entitySet, singleton, resourcePaths);

    return entitySet;
  }
  
  private EdmBindingTarget getEntitySetForNavigation(EdmEntitySet entitySet, EdmSingleton singleton,
      List<UriResource> resourcePaths) throws ODataApplicationException {
    int navigationCount = 0;
      while ((entitySet != null || singleton!=null)
          && ++navigationCount < resourcePaths.size()
          && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
        final UriResourceNavigation uriResourceNavigation = 
            (UriResourceNavigation) resourcePaths.get(navigationCount);
        blockTypeFilters(uriResourceNavigation);
        if (uriResourceNavigation.getProperty().containsTarget()) {
          return entitySet;
        }
        EdmBindingTarget target = null ;
        if(entitySet!=null){
          target = entitySet.getRelatedBindingTarget(uriResourceNavigation.getProperty().getName());
        }else if(singleton != null){
          target = singleton.getRelatedBindingTarget(uriResourceNavigation.getProperty().getName());
        }
        if (target instanceof EdmEntitySet) {
          entitySet = (EdmEntitySet) target;
        }
      }
    return entitySet;
  }

  /**
   * Reads an entity as specified in the resource path, including navigation.
   * If there is navigation and the navigation ends on an entity collection,
   * returns the entity before the final navigation segment.
   */
  protected Entity readEntity(final UriInfoResource uriInfo) throws ODataApplicationException {
    return readEntity(uriInfo, false);
  }

  /**
   * If ignoreLastNavigation is set to false see {@link #readEntity(UriInfoResource)}
   * otherwise returns the second last entity (Ignores the last navigation) 
   * If no such entity exists throws an ODataApplicationException
   */
  protected Entity readEntity(final UriInfoResource uriInfo, final boolean ignoreLastNavigation)
      throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();

    Entity entity = null;
    if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
      final UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);
      EdmEntitySet entitySet = getEntitySetBasedOnTypeCast(uriResource);
      entity = dataProvider.read(entitySet, uriResource.getKeyPredicates());
    }else if (resourcePaths.get(0) instanceof UriResourceSingleton) {
      final UriResourceSingleton uriResource = (UriResourceSingleton) resourcePaths.get(0);
      entity = dataProvider.read( uriResource.getSingleton());
    } else if (resourcePaths.get(0) instanceof UriResourceFunction) {
      final UriResourceFunction uriResource = (UriResourceFunction) resourcePaths.get(0);
      final EdmFunction function = uriResource.getFunction();
      if (function.getReturnType().getType() instanceof EdmEntityType) {
        final List<UriParameter> key = uriResource.getKeyPredicates();
        if (key.isEmpty()) {
          if (uriResource.isCollection()) { // handled in readEntityCollection()
            return null;
          } else {
            entity = dataProvider.readFunctionEntity(function, uriResource.getParameters(), uriInfo);
          }
        } else {
          entity = dataProvider.read((EdmEntityType) function.getReturnType().getType(),
              dataProvider.readFunctionEntityCollection(function, uriResource.getParameters(), uriInfo),
              key);
        }
      } else {
        return null;
      }
    }
    if (entity == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    }

    int readAtMostNavigations = resourcePaths.size();
    if (ignoreLastNavigation) {
      readAtMostNavigations = 0;
      for (int i = 1; i < resourcePaths.size(); i++) {
        if (resourcePaths.get(i) instanceof UriResourceNavigation) {
          readAtMostNavigations++;
        } else {
          break;
        }
      }
    }

    int navigationCount = 0;
	int navigationResCount = getNavigationResourceCount(resourcePaths);
    Link previous = null;
    while (++navigationCount < readAtMostNavigations
        && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
      final UriResourceNavigation uriNavigationResource = (UriResourceNavigation) resourcePaths.get(navigationCount);
      final EdmNavigationProperty navigationProperty = uriNavigationResource.getProperty();
      final List<UriParameter> key = uriNavigationResource.getKeyPredicates();
      if (navigationProperty.isCollection() && key.isEmpty()) { // handled in readEntityCollection()
        return entity;
      }
      final Link link = entity.getNavigationLink(navigationProperty.getName());
      entity = link == null ? null :
          key.isEmpty() ?
              link.getInlineEntity() :
              dataProvider.read(navigationProperty.getType(), link.getInlineEntitySet(), key);
      EdmEntityType edmEntityType = getEntityTypeBasedOnNavPropertyTypeCast(uriNavigationResource);
      entity = edmEntityType != null ? dataProvider.readDataFromEntity(edmEntityType, key) : entity;
      if (entity == null) {
        if (key.isEmpty() && (previous != null || navigationResCount == 1)) {
          throw new ODataApplicationException("No Content", HttpStatusCode.NO_CONTENT.getStatusCode(), Locale.ROOT);
        } else {
          throw new ODataApplicationException("Not Found", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
        }
      }
	  previous = link;
    }

    return entity;
  }
  
  private int getNavigationResourceCount(List<UriResource> resourcePaths) {
    int count = 0;
    for (UriResource resource : resourcePaths) {
      if (resource instanceof UriResourceNavigation) {
        count ++;
      }
    }
    return count;
  }
  
  private EdmEntityType getEntityTypeBasedOnNavPropertyTypeCast(UriResourceNavigation uriNavigationResource) {
    if (uriNavigationResource.getTypeFilterOnCollection() != null) {
      return (EdmEntityType) uriNavigationResource.getTypeFilterOnCollection();
    } else if (uriNavigationResource.getTypeFilterOnEntry() != null) {
      return (EdmEntityType) uriNavigationResource.getTypeFilterOnEntry();
    }
    return null;
    
  }

  protected EdmEntitySet getEntitySetBasedOnTypeCast(UriResourceEntitySet uriResource) {
    EdmEntitySet entitySet = null;
    EdmEntityContainer container = this.serviceMetadata.getEdm().getEntityContainer();
    if (uriResource.getTypeFilterOnEntry() != null ||
        uriResource.getTypeFilterOnCollection() != null) {
      List<EdmEntitySet> entitySets = container.getEntitySets();
      for (EdmEntitySet entitySet1 : entitySets) {
        EdmEntityType entityType = entitySet1.getEntityType();
        if ((uriResource.getTypeFilterOnEntry() != null && 
            entityType.getName().equalsIgnoreCase(uriResource.getTypeFilterOnEntry().getName())) ||
            (uriResource.getTypeFilterOnCollection() != null && 
            entityType.getName().equalsIgnoreCase(uriResource.getTypeFilterOnCollection().getName()))) {
          entitySet = entitySet1;
          break;
        }
      }
    } else {
      entitySet = uriResource.getEntitySet();
    }
    return entitySet;
  }
  
  protected List<DeletedEntity> readDeletedEntities(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    return dataProvider.readDeletedEntities(((UriResourceEntitySet) resourcePaths.get(0)).getEntitySet());
  }


  protected List<DeltaLink> readAddedLinks(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    return dataProvider.readAddedLinks(((UriResourceEntitySet) resourcePaths.get(0)).getEntitySet());
  }
  
  protected List<DeltaLink> readDeletedLinks(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    return dataProvider.readDeletedLinks(((UriResourceEntitySet) resourcePaths.get(0)).getEntitySet());
  }
  
  
  protected EntityCollection readEntityCollection(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    if (resourcePaths.size() > 1 && resourcePaths.get(1) instanceof UriResourceNavigation) {
      final Entity entity = readEntity(uriInfo);
      final Link link = entity.getNavigationLink(getLastNavigation(uriInfo).getProperty().getName());
      return link == null ? null : link.getInlineEntitySet();
    } else {
      if (resourcePaths.get(0) instanceof UriResourceFunction) {
        final UriResourceFunction uriResource = (UriResourceFunction) resourcePaths.get(0);
        return dataProvider.readFunctionEntityCollection(uriResource.getFunction(), uriResource.getParameters(),
            uriInfo);
      } else {
        if (uriInfo.getFilterOption() != null) {
          if (uriInfo.getFilterOption().getExpression() instanceof Binary) {
            Binary expression = (Binary) uriInfo.getFilterOption().getExpression();
            if (expression.getLeftOperand() instanceof Member) {
              Member member = (Member) expression.getLeftOperand();
              if (member.getStartTypeFilter() != null) {
                EdmEntityType entityType = (EdmEntityType) member.getStartTypeFilter();
                EdmEntityContainer container = this.serviceMetadata.getEdm().getEntityContainer();
                List<EdmEntitySet> entitySets = container.getEntitySets();
                for (EdmEntitySet entitySet : entitySets) {
                  if (entityType.getName().equals(entitySet.getEntityType().getName())) {
                    return dataProvider.readAll(entitySet);
                  }
                }
              }
            }
          }
        }
        EdmEntitySet entitySet = getEntitySetBasedOnTypeCast(((UriResourceEntitySet)resourcePaths.get(0)));
        return dataProvider.readAll(entitySet);
      }
    }
  }

  protected UriResourceNavigation getLastNavigation(final UriInfoResource uriInfo) {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    int navigationCount = 1;
    while (navigationCount < resourcePaths.size()
        && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
      navigationCount++;
    }
    
    final UriResource lastSegment = resourcePaths.get(--navigationCount);
    return (lastSegment instanceof UriResourceNavigation) ? (UriResourceNavigation) lastSegment : null;
  }

  private void blockTypeFilters(final UriResource uriResource) throws ODataApplicationException {
    if (uriResource instanceof UriResourceFunction
        && (((UriResourceFunction) uriResource).getTypeFilterOnCollection() != null
        || ((UriResourceFunction) uriResource).getTypeFilterOnEntry() != null)) {
      throw new ODataApplicationException("Type filters are not supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
  }

  protected void validateOptions(final UriInfoResource uriInfo) throws ODataApplicationException {
    if (uriInfo.getApplyOption() != null) {
      throw new ODataApplicationException("Not all of the specified options are supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
  }

  protected void blockBoundActions(final UriInfo uriInfo) throws ODataApplicationException {
    final List<UriResource> uriResourceParts = uriInfo.asUriInfoResource().getUriResourceParts();
    if (uriResourceParts.size() > 1
        && uriResourceParts.get(uriResourceParts.size() - 1) instanceof UriResourceAction) {
      throw new ODataApplicationException("Bound actions are not supported yet.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
  }

  protected void checkRequestFormat(final ContentType requestFormat) throws ODataApplicationException {
    if (requestFormat == null) {
      throw new ODataApplicationException("The content type has not been set in the request.",
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
    }
  }

  protected boolean isODataMetadataNone(final ContentType contentType) {
    return contentType.isCompatible(ContentType.APPLICATION_JSON)
        && ContentType.VALUE_ODATA_METADATA_NONE.equalsIgnoreCase(
            contentType.getParameter(ContentType.PARAMETER_ODATA_METADATA));
  }
}
