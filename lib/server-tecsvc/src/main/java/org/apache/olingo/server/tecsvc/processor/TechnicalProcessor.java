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

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
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

    // First must be an entity, an entity collection, a function import, or an action import.
    blockTypeFilters(resourcePaths.get(0));
    if (resourcePaths.get(0) instanceof UriResourceEntitySet) {
      entitySet = ((UriResourceEntitySet) resourcePaths.get(0)).getEntitySet();
    } else if (resourcePaths.get(0) instanceof UriResourceFunction) {
      entitySet = ((UriResourceFunction) resourcePaths.get(0)).getFunctionImport().getReturnedEntitySet();
    } else if (resourcePaths.get(0) instanceof UriResourceAction) {
      entitySet = ((UriResourceAction) resourcePaths.get(0)).getActionImport().getReturnedEntitySet();
    } else {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    int navigationCount = 0;
    while (entitySet != null
        && ++navigationCount < resourcePaths.size()
        && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
      final UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) resourcePaths.get(navigationCount);
      blockTypeFilters(uriResourceNavigation);
      if (uriResourceNavigation.getProperty().containsTarget()) {
        throw new ODataApplicationException("Containment navigation is not supported.",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
      final EdmBindingTarget target = entitySet.getRelatedBindingTarget(uriResourceNavigation.getProperty().getName());
      if (target instanceof EdmEntitySet) {
        entitySet = (EdmEntitySet) target;
      } else {
        throw new ODataApplicationException("Singletons are not supported.",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
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
      entity = dataProvider.read(uriResource.getEntitySet(), uriResource.getKeyPredicates());
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
      if (entity == null) {
        throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
      }
    }

    return entity;
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
        return dataProvider.readAll(((UriResourceEntitySet) resourcePaths.get(0)).getEntitySet());
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
    if (uriResource instanceof UriResourceEntitySet
        && (((UriResourceEntitySet) uriResource).getTypeFilterOnCollection() != null
        || ((UriResourceEntitySet) uriResource).getTypeFilterOnEntry() != null)
        || uriResource instanceof UriResourceFunction
        && (((UriResourceFunction) uriResource).getTypeFilterOnCollection() != null
        || ((UriResourceFunction) uriResource).getTypeFilterOnEntry() != null)
        || uriResource instanceof UriResourceNavigation
        && (((UriResourceNavigation) uriResource).getTypeFilterOnCollection() != null
        || ((UriResourceNavigation) uriResource).getTypeFilterOnEntry() != null)) {
      throw new ODataApplicationException("Type filters are not supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
  }

  protected void validateOptions(final UriInfoResource uriInfo) throws ODataApplicationException {
    if (uriInfo.getIdOption() != null || uriInfo.getApplyOption() != null) {
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
