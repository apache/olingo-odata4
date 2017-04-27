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
package org.apache.olingo.server.tecsvc.processor.queryoptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.CountHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.FilterHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.OrderByHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.SkipHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.TopHandler;

public class ExpandSystemQueryOptionHandler {

  public void applyExpandQueryOptions(final EntityCollection entitySet, final EdmEntitySet edmEntitySet,
      final ExpandOption expandOption, final UriInfoResource uriInfo, final Edm edm) throws ODataApplicationException {
    if (expandOption == null) {
      return;
    }

    for (final Entity entity : entitySet.getEntities()) {
      applyExpandOptionToEntity(entity, edmEntitySet, expandOption, uriInfo, edm);
    }
  }

  public void applyExpandQueryOptions(final Entity entity, final EdmEntitySet edmEntitySet,
      final ExpandOption expandOption, final UriInfoResource uriInfo, final Edm edm) throws ODataApplicationException {
    if (expandOption == null) {
      return;
    }

    applyExpandOptionToEntity(entity, edmEntitySet, expandOption, uriInfo, edm);
  }

  private void applyExpandOptionToEntity(final Entity entity, final EdmBindingTarget edmBindingTarget,
      final ExpandOption expandOption, final UriInfoResource uriInfo, final Edm edm) throws ODataApplicationException {

    final EdmEntityType entityType = edmBindingTarget.getEntityType();

    for (ExpandItem item : expandOption.getExpandItems()) {
      if(item.getLevelsOption() != null) {
        throw new ODataApplicationException("$levels is not implemented", 
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
      
      List<EdmNavigationProperty> navigationProperties = new ArrayList<EdmNavigationProperty>();
      if(item.isStar()) {
        List<EdmNavigationPropertyBinding> bindings = edmBindingTarget.getNavigationPropertyBindings();
        for (EdmNavigationPropertyBinding binding : bindings) {
          EdmElement property = entityType.getProperty(binding.getPath());
          if(property instanceof EdmNavigationProperty) {
            navigationProperties.add((EdmNavigationProperty) property);
          }
        }
      } else {
        final List<UriResource> uriResourceParts = item.getResourcePath().getUriResourceParts();
        if (uriResourceParts.get(0) instanceof UriResourceNavigation) {
          navigationProperties.add(((UriResourceNavigation) uriResourceParts.get(0)).getProperty());
        }
      }

      for (EdmNavigationProperty navigationProperty: navigationProperties) {
        final String navPropertyName = navigationProperty.getName();
        final EdmBindingTarget targetEdmEntitySet = edmBindingTarget.getRelatedBindingTarget(navPropertyName);

        final Link link = entity.getNavigationLink(navPropertyName);
        if (link != null && entityType.getNavigationProperty(navPropertyName).isCollection()) {
          applyOptionsToEntityCollection(link.getInlineEntitySet(),
              targetEdmEntitySet,
              item.getFilterOption(),
              item.getOrderByOption(),
              item.getCountOption(),
              item.getSkipOption(),
              item.getTopOption(),
              item.getExpandOption(),
              uriInfo, edm);
        }
      }
    }
  }

  private void applyOptionsToEntityCollection(final EntityCollection entitySet,
      final EdmBindingTarget edmBindingTarget,
      final FilterOption filterOption, final OrderByOption orderByOption, final CountOption countOption,
      final SkipOption skipOption, final TopOption topOption, final ExpandOption expandOption,
      final UriInfoResource uriInfo, final Edm edm)
      throws ODataApplicationException {

    FilterHandler.applyFilterSystemQuery(filterOption, entitySet, uriInfo, edm);
    OrderByHandler.applyOrderByOption(orderByOption, entitySet, uriInfo, edm);
    CountHandler.applyCountSystemQueryOption(countOption, entitySet);
    SkipHandler.applySkipSystemQueryHandler(skipOption, entitySet);
    TopHandler.applyTopSystemQueryOption(topOption, entitySet);

    // Apply nested expand system query options to remaining entities
    if (expandOption != null) {
      for (final Entity entity : entitySet.getEntities()) {
        applyExpandOptionToEntity(entity, edmBindingTarget, expandOption, uriInfo, edm);
      }
    }
  }

  public EntityCollection transformEntitySetGraphToTree(final EntityCollection entitySet,
      final EdmBindingTarget edmBindingTarget, final ExpandOption expand, 
      final ExpandItem expandItem) throws ODataApplicationException {

    final EntityCollection newEntitySet = newEntitySet(entitySet);

    for (final Entity entity : entitySet.getEntities()) {
      newEntitySet.getEntities().add(transformEntityGraphToTree(entity, edmBindingTarget, expand, expandItem));
    }
    if (expandItem != null && expandItem.hasCountPath()) {
      newEntitySet.setCount(entitySet.getEntities().size());
    }
    return newEntitySet;
  }

  public Entity transformEntityGraphToTree(final Entity entity, final EdmBindingTarget edmEntitySet,
      final ExpandOption expand, final ExpandItem parentExpandItem) throws ODataApplicationException {
    final Entity newEntity = newEntity(entity);
    if (hasExpandItems(expand)) {
      final boolean expandAll = expandAll(expand);
      final Set<String> expanded = expandAll ? null : getExpandedPropertyNames(expand.getExpandItems());
      final EdmEntityType edmType = edmEntitySet.getEntityType();

      for (final Link link : entity.getNavigationLinks()) {
        final String propertyName = link.getTitle();

        if (expandAll || expanded.contains(propertyName)) {
          final EdmNavigationProperty edmNavigationProperty = edmType.getNavigationProperty(propertyName);
          final EdmBindingTarget edmBindingTarget = edmEntitySet.getRelatedBindingTarget(propertyName);
          final Link newLink = newLink(link);
          newEntity.getNavigationLinks().add(newLink);
          final ExpandItem expandItem = getInnerExpandItem(expand, propertyName);

          if (edmNavigationProperty.isCollection()) {
            newLink.setInlineEntitySet(transformEntitySetGraphToTree(link.getInlineEntitySet(),
                edmBindingTarget, expandItem.getExpandOption(), expandItem));
          } else {
            newLink.setInlineEntity(transformEntityGraphToTree(link.getInlineEntity(),
                edmBindingTarget,expandItem.getExpandOption(), expandItem));
          }
        }
      }

    }
    return newEntity;
  }

  public EntityCollection newEntitySet(final EntityCollection entitySet) {
    final EntityCollection newEntitySet = new EntityCollection();
    newEntitySet.setCount(entitySet.getCount());
    newEntitySet.setDeltaLink(entitySet.getDeltaLink());
    newEntitySet.setNext(entitySet.getNext());
    newEntitySet.setId(entitySet.getId());
    newEntitySet.setBaseURI(entitySet.getBaseURI());
    newEntitySet.getOperations().addAll(entitySet.getOperations());
    return newEntitySet;
  }

  private Entity newEntity(final Entity entity) {
    Entity newEntity = new Entity();

    newEntity.getProperties().addAll(entity.getProperties());
    newEntity.getAnnotations().addAll(entity.getAnnotations());
    newEntity.setId(entity.getId());
    newEntity.setBaseURI(entity.getBaseURI());
    newEntity.setType(entity.getType());
    newEntity.setETag(entity.getETag());
    newEntity.setMediaContentSource(entity.getMediaContentSource());
    newEntity.setMediaContentType(entity.getMediaContentType());
    newEntity.setMediaETag(entity.getMediaETag());
    newEntity.setSelfLink(entity.getSelfLink());
    newEntity.setEditLink(entity.getEditLink());
    newEntity.getAssociationLinks().addAll(entity.getAssociationLinks());
    newEntity.getNavigationBindings().addAll(entity.getNavigationBindings());
    newEntity.getOperations().addAll(entity.getOperations());
    newEntity.getNavigationLinks().addAll(entity.getNavigationLinks());
    return newEntity;
  }

  private Link newLink(final Link link) {
    final Link newLink = new Link();
    newLink.setMediaETag(link.getMediaETag());
    newLink.setTitle(link.getTitle());
    newLink.setType(link.getType());
    newLink.setRel(link.getRel());
    newLink.setHref(link.getHref());
    return newLink;
  }

  private boolean hasExpandItems(final ExpandOption expand) {
    return expand != null && expand.getExpandItems() != null && !expand.getExpandItems().isEmpty();
  }

  private boolean expandAll(final ExpandOption expand) {
    for (final ExpandItem item : expand.getExpandItems()) {
      if (item.isStar()) {
        return true;
      }
    }
    return false;
  }

  private Set<String> getExpandedPropertyNames(final List<ExpandItem> expandItems) throws ODataApplicationException {
    Set<String> expanded = new HashSet<String>();
    for (final ExpandItem item : expandItems) {
      final List<UriResource> resourceParts = item.getResourcePath().getUriResourceParts();
      final UriResource resource = resourceParts.get(0);
      if (resource instanceof UriResourceNavigation) {
        expanded.add(((UriResourceNavigation) resource).getProperty().getName());
      }
    }
    return expanded;
  }

  private ExpandItem getInnerExpandItem(final ExpandOption expand, final String propertyName) {
    for (final ExpandItem item : expand.getExpandItems()) {
      if(item.isStar()) {
        return item;
      }

      final UriResource resource = item.getResourcePath().getUriResourceParts().get(0);
      if (resource instanceof UriResourceNavigation
          && propertyName.equals(((UriResourceNavigation) resource).getProperty().getName())) {
        return item;
      }
    }

    return null;
  }
}
