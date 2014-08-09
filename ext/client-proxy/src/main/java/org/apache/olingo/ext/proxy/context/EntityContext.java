/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.ext.proxy.context;

import java.net.URI;
import org.apache.olingo.ext.proxy.commons.EntityInvocationHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity context.
 */
public class EntityContext implements Iterable<AttachedEntity> {

  /**
   * Attached entities with not null key.
   * <br/>
   * This map have to be used to search for entities by key.
   */
  private final Map<EntityUUID, EntityInvocationHandler> searchableEntities =
          new HashMap<EntityUUID, EntityInvocationHandler>();

  /**
   * All attached entities (new entities included).
   * <br/>
   * Attachment order will be maintained.
   */
  private final Map<EntityInvocationHandler, AttachedEntityStatus> allAttachedEntities =
          new LinkedHashMap<EntityInvocationHandler, AttachedEntityStatus>();

  /**
   * Deletes to be performed excluding entities.
   * <br/>
   * Attachment order will be maintained.
   */
  private final List<URI> furtherDeletes = new ArrayList<URI>();

  /**
   * Attaches an entity with status <tt>NEW</tt>.
   * <br/>
   * Use this method to attach a new created entity.
   *
   * @see AttachedEntityStatus
   * @param entity entity to be attached.
   */
  public void attachNew(final EntityInvocationHandler entity) {
    if (allAttachedEntities.containsKey(entity)) {
      throw new IllegalStateException("An entity with the same key has already been attached");
    }
    allAttachedEntities.put(entity, AttachedEntityStatus.NEW);
  }

  /**
   * Attaches an existing entity with status <tt>ATTACHED</tt>.
   * <br/>
   * Use this method to attach an existing entity.
   *
   * @see AttachedEntityStatus
   * @param entity entity to be attached.
   */
  public void attach(final EntityInvocationHandler entity) {
    attach(entity, AttachedEntityStatus.ATTACHED);
  }

  /**
   * Attaches an entity with specified status.
   * <br/>
   * Use this method to attach an existing entity.
   *
   * @see AttachedEntityStatus
   * @param entity entity to be attached.
   * @param status status.
   */
  public void attach(final EntityInvocationHandler entity, final AttachedEntityStatus status) {
    attach(entity, status, false);
  }

  /**
   * Attaches an entity with specified status.
   * <br/>
   * Use this method to attach an existing entity.
   *
   * @param entity entity to be attached.
   * @param status status.
   * @param force force attach.
   */
  public void attach(final EntityInvocationHandler entity, final AttachedEntityStatus status, final boolean force) {
    if (isAttached(entity)) {
      throw new IllegalStateException("An entity with the same profile has already been attached");
    }

    if (force || entity.getUUID().getEntitySetURI() != null) {
      allAttachedEntities.put(entity, status);

      if (entity.getUUID().getKey() != null) {
        searchableEntities.put(entity.getUUID(), entity);
      }
    }
  }

  /**
   * Detaches entity.
   *
   * @param entity entity to be detached.
   */
  public void detach(final EntityInvocationHandler entity) {
    searchableEntities.remove(entity.getUUID());
    allAttachedEntities.remove(entity);
  }

  /**
   * Detaches all attached entities.
   * <br/>
   * Use this method to clears the entity context.
   */
  public void detachAll() {
    allAttachedEntities.clear();
    searchableEntities.clear();
    furtherDeletes.clear();
  }

  /**
   * Searches an entity with the specified key.
   *
   * @param uuid entity key.
   * @return retrieved entity.
   */
  public EntityInvocationHandler getEntity(final EntityUUID uuid) {
    return searchableEntities.get(uuid);
  }

  /**
   * Gets entity status.
   *
   * @param entity entity to be retrieved.
   * @return attached entity status.
   */
  public AttachedEntityStatus getStatus(final EntityInvocationHandler entity) {
    if (!isAttached(entity)) {
      throw new IllegalStateException("Entity is not in the context");
    }

    return allAttachedEntities.get(entity);
  }

  /**
   * Changes attached entity status.
   *
   * @param entity attached entity to be modified.
   * @param status new status.
   */
  public void setStatus(final EntityInvocationHandler entity, final AttachedEntityStatus status) {
    if (!isAttached(entity)) {
      throw new IllegalStateException("Entity is not in the context");
    }

    final AttachedEntityStatus current = allAttachedEntities.get(entity);

    // Previously deleted object cannot be modified anymore.
    if (current == AttachedEntityStatus.DELETED) {
      throw new IllegalStateException("Entity has been previously deleted");
    }

    if (status == AttachedEntityStatus.NEW || status == AttachedEntityStatus.ATTACHED) {
      throw new IllegalStateException("Entity status has already been initialized");
    }

    if ((status == AttachedEntityStatus.LINKED && current == AttachedEntityStatus.ATTACHED)
            || (status == AttachedEntityStatus.CHANGED && current == AttachedEntityStatus.ATTACHED)
            || (status == AttachedEntityStatus.CHANGED && current == AttachedEntityStatus.LINKED)
            || (status == AttachedEntityStatus.DELETED)) {
      allAttachedEntities.put(entity, status);
    }
  }

  /**
   * Checks if an entity is already attached.
   *
   * @param entity entity.
   * @return <tt>true</tt> if is attached; <tt>false</tt> otherwise.
   */
  public boolean isAttached(final EntityInvocationHandler entity) {
    return entity == null // avoid attach for null entities (coming from complexes created from container ...)
            || allAttachedEntities.containsKey(entity)
            || (entity.getUUID().getKey() != null && searchableEntities.containsKey(entity.getUUID()));
  }

  /**
   * Iterator.
   *
   * @return attached entities iterator.
   */
  @Override
  public Iterator<AttachedEntity> iterator() {
    final List<AttachedEntity> res = new ArrayList<AttachedEntity>();
    for (Map.Entry<EntityInvocationHandler, AttachedEntityStatus> entity : allAttachedEntities.entrySet()) {
      res.add(new AttachedEntity(entity.getKey(), entity.getValue()));
    }
    return res.iterator();
  }

  public List<URI> getFurtherDeletes() {
    return furtherDeletes;
  }

  public void addFurtherDeletes(final URI uri) {
    furtherDeletes.add(uri);
  }
}
