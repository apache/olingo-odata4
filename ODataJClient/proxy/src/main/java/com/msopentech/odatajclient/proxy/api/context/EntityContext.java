/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.proxy.api.context;

import com.msopentech.odatajclient.proxy.api.impl.EntityTypeInvocationHandler;
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
     * <p>
     * This map have to be used to search for entities by key.
     */
    private final Map<EntityUUID, EntityTypeInvocationHandler> searchableEntities =
            new HashMap<EntityUUID, EntityTypeInvocationHandler>();

    /**
     * All attached entities (new entities included).
     * <p>
     * Attachment order will be maintained.
     */
    private final Map<EntityTypeInvocationHandler, AttachedEntityStatus> allAttachedEntities =
            new LinkedHashMap<EntityTypeInvocationHandler, AttachedEntityStatus>();

    /**
     * Attaches an entity with status <tt>NEW</tt>.
     * <p>
     * Use this method to attach a new created entity.
     *
     * @see AttachedEntityStatus
     * @param entity entity to be attached.
     */
    public void attachNew(final EntityTypeInvocationHandler entity) {
        if (allAttachedEntities.containsKey(entity)) {
            throw new IllegalStateException("An entity with the same key has already been attached");
        }
        allAttachedEntities.put(entity, AttachedEntityStatus.NEW);
    }

    /**
     * Attaches an existing entity with status <tt>ATTACHED</tt>.
     * <p>
     * Use this method to attach an existing entity.
     *
     * @see AttachedEntityStatus
     * @param entity entity to be attached.
     */
    public void attach(final EntityTypeInvocationHandler entity) {
        attach(entity, AttachedEntityStatus.ATTACHED);
    }

    /**
     * Attaches an entity with specified status.
     * <p>
     * Use this method to attach an existing entity.
     *
     * @see AttachedEntityStatus
     * @param entity entity to be attached.
     * @param status status.
     */
    public void attach(final EntityTypeInvocationHandler entity, final AttachedEntityStatus status) {
        if (isAttached(entity)) {
            throw new IllegalStateException("An entity with the same profile has already been attached");
        }

        allAttachedEntities.put(entity, status);

        if (entity.getUUID().getKey() != null) {
            searchableEntities.put(entity.getUUID(), entity);
        }
    }

    /**
     * Detaches entity.
     *
     * @param entity entity to be detached.
     */
    public void detach(final EntityTypeInvocationHandler entity) {
        if (searchableEntities.containsKey(entity.getUUID())) {
            searchableEntities.remove(entity.getUUID());
        }
        allAttachedEntities.remove(entity);
    }

    /**
     * Detaches all attached entities.
     * <p>
     * Use this method to clears the entity context.
     */
    public void detachAll() {
        allAttachedEntities.clear();
        searchableEntities.clear();
    }

    /**
     * Searches an entity with the specified key.
     *
     * @param uuid entity key.
     * @return retrieved entity.
     */
    public EntityTypeInvocationHandler getEntity(final EntityUUID uuid) {
        return searchableEntities.get(uuid);
    }

    /**
     * Gets entity status.
     *
     * @param entity entity to be retrieved.
     * @return attached entity status.
     */
    public AttachedEntityStatus getStatus(final EntityTypeInvocationHandler entity) {
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
    public void setStatus(final EntityTypeInvocationHandler entity, final AttachedEntityStatus status) {
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
    public boolean isAttached(final EntityTypeInvocationHandler entity) {
        return allAttachedEntities.containsKey(entity)
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
        for (Map.Entry<EntityTypeInvocationHandler, AttachedEntityStatus> attachedEntity : allAttachedEntities.
                entrySet()) {
            res.add(new AttachedEntity(attachedEntity.getKey(), attachedEntity.getValue()));
        }
        return res.iterator();
    }
}
