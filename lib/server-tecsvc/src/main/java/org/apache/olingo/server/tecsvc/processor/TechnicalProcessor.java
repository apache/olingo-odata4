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
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.tecsvc.data.DataProvider;

/**
 * Technical Processor base.
 */
public abstract class TechnicalProcessor implements Processor {

  protected OData odata;
  protected DataProvider dataProvider;

  protected TechnicalProcessor(final DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
  }

  protected EdmEntitySet getEdmEntitySet(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    // first must be entity set
    if (!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
      throw new ODataApplicationException("Invalid resource type.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }

    final UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);
    if (uriResource.getTypeFilterOnCollection() != null || uriResource.getTypeFilterOnEntry() != null) {
      throw new ODataApplicationException("Type filters are not supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
    EdmEntitySet entitySet = uriResource.getEntitySet();

    int navigationCount = 0;
    while (++navigationCount < resourcePaths.size()
        && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
      final UriResourceNavigation uriNavigationResource = (UriResourceNavigation) resourcePaths.get(navigationCount);
      if (uriNavigationResource.getTypeFilterOnCollection() != null
          || uriNavigationResource.getTypeFilterOnEntry() != null) {
        throw new ODataApplicationException("Type filters are not supported.",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
      if (uriNavigationResource.getProperty().containsTarget()) {
        throw new ODataApplicationException("Containment navigation is not supported.",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
      final EdmBindingTarget target = entitySet.getRelatedBindingTarget(uriNavigationResource.getProperty().getName());
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
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    final UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);
    Entity entity = dataProvider.read(uriResource.getEntitySet(), uriResource.getKeyPredicates());
    if (entity == null) {
      throw new ODataApplicationException("Nothing found.", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ROOT);
    }

    int navigationCount = 0;
    while (++navigationCount < resourcePaths.size()
        && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
      final UriResourceNavigation uriNavigationResource = (UriResourceNavigation) resourcePaths.get(navigationCount);
      final EdmNavigationProperty navigationProperty = uriNavigationResource.getProperty();
      final List<UriParameter> key = uriNavigationResource.getKeyPredicates();
      if (navigationProperty.isCollection() && key.isEmpty()) {
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

  protected EntitySet readEntityCollection(final UriInfoResource uriInfo) throws ODataApplicationException {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    if (resourcePaths.size() > 1 && resourcePaths.get(1) instanceof UriResourceNavigation) {
      final Entity entity = readEntity(uriInfo);
      final Link link = entity.getNavigationLink(getLastNavigation(uriInfo).getProperty().getName());
      return link == null ? null : link.getInlineEntitySet();
    } else {
      return dataProvider.readAll(((UriResourceEntitySet) resourcePaths.get(0)).getEntitySet());
    }
  }

  private UriResourceNavigation getLastNavigation(final UriInfoResource uriInfo) {
    final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    int navigationCount = 1;
    while (navigationCount < resourcePaths.size()
        && resourcePaths.get(navigationCount) instanceof UriResourceNavigation) {
      navigationCount++;
    }

    return (UriResourceNavigation) resourcePaths.get(--navigationCount);
  }

  protected void validateOptions(final UriInfoResource uriInfo) throws ODataApplicationException {
    if (uriInfo.getIdOption() != null
        || uriInfo.getSearchOption() != null) {
      throw new ODataApplicationException("Not all of the specified options are supported.",
          HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
    }
  }
}
