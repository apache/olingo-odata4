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

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.FilterHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.OrderByHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.SkipHandler;
import org.apache.olingo.server.tecsvc.processor.queryoptions.options.TopHandler;

public class ExpandSystemQueryOptionHandler {
  private IdentityHashMap<Entity, Entity> copiedEntities = new IdentityHashMap<Entity, Entity>();
  private IdentityHashMap<EntitySet, EntitySet> copiedEntitySets = new IdentityHashMap<EntitySet, EntitySet>();

  public void applyExpandQueryOptions(final EntitySet entitySet, final EdmEntitySet edmEntitySet,
      final ExpandOption expandOption) throws ODataApplicationException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    if (expandOption == null) {
      return;
    }

    for (ExpandItem item : expandOption.getExpandItems()) {
      final List<UriResource> uriResourceParts = item.getResourcePath().getUriResourceParts();
      if (uriResourceParts.size() == 1 && uriResourceParts.get(0) instanceof UriResourceNavigation) {
        final String navPropertyName = ((UriResourceNavigation) uriResourceParts.get(0)).getProperty().getName();
        final EdmEntitySet targetEdmEntitySet = (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(navPropertyName);

        for (final Entity entity : entitySet.getEntities()) {
          final Link link = entity.getNavigationLink(navPropertyName);
          if (link != null && entityType.getNavigationProperty(navPropertyName).isCollection()) {
            applyOptionsToEntityCollection(link.getInlineEntitySet(), targetEdmEntitySet, item.getFilterOption(),
                item.getOrderByOption(), item.getCountOption(), item.getSkipOption(), item.getTopOption());
          }
        }
      } else {
        throw new ODataApplicationException("Not supported resource part in expand system query option",
            HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ROOT);
      }
    }
  }

  private void applyOptionsToEntityCollection(final EntitySet entitySet, final EdmEntitySet edmEntitySet,
      final FilterOption filterOption, final OrderByOption orderByOption, final CountOption countOption,
      final SkipOption skipOption, final TopOption topOption) throws ODataApplicationException {

    FilterHandler.applyFilterSystemQuery(filterOption, entitySet, edmEntitySet);
    OrderByHandler.applyOrderByOption(orderByOption, entitySet, edmEntitySet);
    SkipHandler.applySkipSystemQueryHandler(skipOption, entitySet);
    TopHandler.applyTopSystemQueryOption(topOption, entitySet);
  }

  public EntitySet copyEntitySetShallowRekursive(final EntitySet entitySet) {
    if (!copiedEntitySets.containsKey(entitySet)) {
      final EntitySet copiedEntitySet = new EntitySetImpl();
      copiedEntitySet.setCount(entitySet.getCount());
      copiedEntitySet.setDeltaLink(entitySet.getDeltaLink());
      copiedEntitySet.setNext(entitySet.getNext());

      copiedEntitySets.put(entitySet, copiedEntitySet);
      copiedEntitySets.put(copiedEntitySet, copiedEntitySet);
      
      for (Entity entity : entitySet.getEntities()) {
        copiedEntitySet.getEntities().add(copyEntityShallowRekursive(entity));
      }
      return copiedEntitySet;
    }
    return copiedEntitySets.get(entitySet);
  }

  private Entity copyEntityShallowRekursive(final Entity entity) {
    if (!copiedEntities.containsKey(entity)) {
      final Entity copiedEntity = new EntityImpl();
      copiedEntity.getProperties().addAll(entity.getProperties());
      copiedEntity.getAnnotations().addAll(entity.getAnnotations());
      copiedEntity.getAssociationLinks().addAll(entity.getAssociationLinks());
      copiedEntity.setEditLink(entity.getEditLink());
      copiedEntity.setId(entity.getId());
      copiedEntity.setMediaContentSource(entity.getMediaContentSource());
      copiedEntity.setMediaContentType(entity.getMediaContentType());
      copiedEntity.setMediaETag(entity.getMediaETag());
      copiedEntity.getOperations().addAll(entity.getOperations());
      copiedEntity.setSelfLink(entity.getSelfLink());
      copiedEntity.setType(entity.getType());
      copiedEntity.getNavigationBindings().addAll(entity.getNavigationBindings());
      
      copiedEntities.put(entity, copiedEntity);
      copiedEntities.put(copiedEntity, copiedEntity);

      // The system query options change the amount and sequence of inline entities (feeds)
      // So we have to make a shallow copy of all navigation link lists
      // Make sure, that each entity is only copied once.
      // Otherwise an infinite loop can occur caused by cyclic navigation relationships.

      for (final Link link : entity.getNavigationLinks()) {
        final Link newLink = new LinkImpl();
        newLink.setMediaETag(link.getMediaETag());
        newLink.setTitle(link.getTitle());
        newLink.setType(link.getType());
        newLink.setRel(link.getRel());

        final EntitySet inlineEntitySet = link.getInlineEntitySet();
        if (inlineEntitySet != null) {
          newLink.setInlineEntitySet(copyEntitySetShallowRekursive(inlineEntitySet));
        } else if (link.getInlineEntity() != null) {
          newLink.setInlineEntity(copyEntityShallowRekursive(link.getInlineEntity()));
        }
        copiedEntity.getNavigationLinks().add(newLink);
      }

      return copiedEntity;
    }
    return copiedEntities.get(entity);
  }
}
