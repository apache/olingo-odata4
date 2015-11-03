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
package myservice.mynamespace.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;

public class TransactionalEntityManager {
  
  private Map<String, List<Entity>> entities = new HashMap<String, List<Entity>>();
  private Map<String, List<Entity>> backupEntities = new HashMap<String, List<Entity>>();
  private Map<String, IdentityHashMap<Entity, Entity>> copyMap = new HashMap<String, IdentityHashMap<Entity, Entity>>();
  private boolean isInTransaction = false;
  private Edm edm;
  
  public TransactionalEntityManager(final Edm edm) {
    this.edm = edm;
  }

  public List<Entity> getEntityCollection(final String entitySetName) {
    if(!entities.containsKey(entitySetName)) {
      entities.put(entitySetName, new ArrayList<Entity>());
    }
    
    return entities.get(entitySetName);
  }
  
  public void beginTransaction() {
    if(!isInTransaction) {
      isInTransaction = true;
      copyCurrentState();
    }
  }
  
  public void rollbackTransaction() {
    if(isInTransaction) {
      entities = backupEntities;
      backupEntities = new HashMap<String, List<Entity>>();
      isInTransaction = false;
    } 
  }
  
  public void commitTransaction() {
    if(isInTransaction) {
      backupEntities.clear();
      isInTransaction = false;
    }
  }
  
  public void clear() {
    entities.clear();
    backupEntities.clear();
  }
  
  private void copyCurrentState() {
    copyMap.clear();
    backupEntities.clear();
    
    for(final String entitySetName : entities.keySet()) {
      final List<Entity> entityList = entities.get(entitySetName);
      backupEntities.put(entitySetName, new ArrayList<Entity>());
      final List<Entity> backupEntityList = backupEntities.get(entitySetName);
      
      for(final Entity entity : entityList) {
        final EdmEntitySet entitySet = edm.getEntityContainer().getEntitySet(entitySetName);
        backupEntityList.add(copyEntityRecursively(entitySet, entity));
      }
    }
  }
  
  private Entity copyEntityRecursively(final EdmEntitySet edmEntitySet, final Entity entity) {
    // Check if entity is already copied
    if(containsEntityInCopyMap(edmEntitySet.getName(), entity)) {
      return getEntityFromCopyMap(edmEntitySet.getName(), entity);
    } else {
      final Entity newEntity = copyEntity(entity);
      addEntityToCopyMap(edmEntitySet.getName(), entity, newEntity);
      
      // Create nested entities recursively
      for(final Link link : entity.getNavigationLinks()) {
        newEntity.getNavigationLinks().add(copyLink(edmEntitySet, link));
      }
      
      return newEntity;
    }
  }

  private Link copyLink(final EdmEntitySet edmEntitySet, final Link link) {
    final Link newLink = new Link();
    newLink.setBindingLink(link.getBindingLink());
    newLink.setBindingLinks(new ArrayList<String>(link.getBindingLinks()));
    newLink.setHref(link.getHref());
    newLink.setMediaETag(link.getMediaETag());
    newLink.setRel(link.getRel());
    newLink.setTitle(link.getTitle());
    newLink.setType(link.getType());
    
    // Single navigation link
    if(link.getInlineEntity() != null) {
      final EdmEntitySet linkedEdmEntitySet = (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(link.getTitle());
      newLink.setInlineEntity(copyEntityRecursively(linkedEdmEntitySet, link.getInlineEntity()));
    }      
    
    // Collection navigation link
    if(link.getInlineEntitySet() != null) {
      final EdmEntitySet linkedEdmEntitySet = (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(link.getTitle());
      final EntityCollection inlineEntitySet = link.getInlineEntitySet();
      final EntityCollection newInlineEntitySet = new EntityCollection();
      newInlineEntitySet.setBaseURI(inlineEntitySet.getBaseURI());
      newInlineEntitySet.setCount(inlineEntitySet.getCount());
      newInlineEntitySet.setDeltaLink(inlineEntitySet.getDeltaLink());
      newInlineEntitySet.setId(inlineEntitySet.getId());
      newInlineEntitySet.setNext(inlineEntitySet.getNext());
      
      for(final Entity inlineEntity : inlineEntitySet.getEntities()) {
        newInlineEntitySet.getEntities().add(copyEntityRecursively(linkedEdmEntitySet, inlineEntity));
      }
      
      newLink.setInlineEntitySet(newInlineEntitySet);
    }
    
    return newLink;
  }

  private Entity copyEntity(final Entity entity) {
    final Entity newEntity = new Entity();
    newEntity.setBaseURI(entity.getBaseURI());
    newEntity.setEditLink(entity.getEditLink());
    newEntity.setETag(entity.getETag());
    newEntity.setId(entity.getId());
    newEntity.setMediaContentSource(entity.getMediaContentSource());
    newEntity.setMediaContentType(entity.getMediaContentType());
    newEntity.setSelfLink(entity.getSelfLink());
    newEntity.setMediaETag(entity.getMediaETag());
    newEntity.setType(entity.getType());
    newEntity.getProperties().addAll(entity.getProperties());
    
    return newEntity;
  }

  private void addEntityToCopyMap(final String entitySetName, final Entity srcEntity, final Entity destEntity) {
    if(!copyMap.containsKey(entitySetName)) {
      copyMap.put(entitySetName, new IdentityHashMap<Entity, Entity>());
    }
    
    copyMap.get(entitySetName).put(srcEntity, destEntity);
  }
  
  private boolean containsEntityInCopyMap(final String entitySetName, final Entity srcEntity) {
    return getEntityFromCopyMap(entitySetName, srcEntity) != null;
  }
  
  private Entity getEntityFromCopyMap(final String entitySetName, final Entity srcEntity) {
    if(!copyMap.containsKey(entitySetName)) {
      return null;
    }
    
    return copyMap.get(entitySetName).get(srcEntity);
  }
}
