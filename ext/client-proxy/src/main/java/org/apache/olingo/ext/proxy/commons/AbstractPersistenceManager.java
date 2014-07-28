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
package org.apache.olingo.ext.proxy.commons;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.ODataReferenceAddingRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.context.AttachedEntity;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityLinkDesc;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractPersistenceManager implements PersistenceManager {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractPersistenceManager.class);

  private static final long serialVersionUID = 2065240290461241515L;

  protected final AbstractService<?> service;

  AbstractPersistenceManager(final AbstractService<?> factory) {
    this.service = factory;
  }

  @Override
  public Future<List<ODataRuntimeException>> flushAsync() {
    return service.getClient().getConfiguration().getExecutor().submit(new Callable<List<ODataRuntimeException>>() {
      @Override
      public List<ODataRuntimeException> call() throws Exception {
        return flush();
      }
    });
  }

  protected abstract List<ODataRuntimeException> doFlush(PersistenceChanges changes, TransactionItems items);

  @Override
  public List<ODataRuntimeException> flush() {
    final PersistenceChanges changes = new PersistenceChanges();
    final TransactionItems items = new TransactionItems();

    int pos = 0;
    final List<EntityLinkDesc> delayedUpdates = new ArrayList<EntityLinkDesc>();
    for (AttachedEntity attachedEntity : service.getContext().entityContext()) {
      final AttachedEntityStatus status = attachedEntity.getStatus();
      if (((status != AttachedEntityStatus.ATTACHED
              && status != AttachedEntityStatus.LINKED) || attachedEntity.getEntity().isChanged())
              && !items.contains(attachedEntity.getEntity())) {
        pos++;
        pos = processEntityContext(attachedEntity.getEntity(), pos, items, delayedUpdates, changes);
      }
    }

    processDelayedUpdates(delayedUpdates, pos, items, changes);

    // remove null values
    items.normalize();

    for (URI uri : service.getContext().entityContext().getFurtherDeletes()) {
      pos++;
      queueDelete(uri, null, changes);
      items.put(null, pos);
    }

    final List<ODataRuntimeException> result = new ArrayList<ODataRuntimeException>();
    if (!items.isEmpty()) {
      result.addAll(doFlush(changes, items));
    }

    service.getContext().detachAll();
    return result;
  }

  private ODataLink buildNavigationLink(final String name, final URI uri, final ODataLinkType type) {
    ODataLink result;

    switch (type) {
      case ENTITY_NAVIGATION:
        result = service.getClient().getObjectFactory().newEntityNavigationLink(name, uri);
        break;

      case ENTITY_SET_NAVIGATION:
        result = service.getClient().getObjectFactory().newEntitySetNavigationLink(name, uri);
        break;

      default:
        throw new IllegalArgumentException("Invalid link type " + type.name());
    }

    return result;
  }

  protected int processEntityContext(
          final EntityInvocationHandler handler,
          int pos,
          final TransactionItems items,
          final List<EntityLinkDesc> delayedUpdates,
          final PersistenceChanges changeset) {

    items.put(handler, null);

    final CommonODataEntity entity = handler.getEntity();
    entity.getNavigationLinks().clear();

    final AttachedEntityStatus currentStatus = service.getContext().entityContext().getStatus(handler);
    LOG.debug("Process '{}({})'", handler, currentStatus);

    if (AttachedEntityStatus.DELETED != currentStatus) {
      entity.getProperties().clear();
      CoreUtils.addProperties(service.getClient(), handler.getPropertyChanges(), entity);

      if (entity instanceof ODataEntity) {
        ((ODataEntity) entity).getAnnotations().clear();
        CoreUtils.addAnnotations(service.getClient(), handler.getAnnotations(), (ODataEntity) entity);

        for (Map.Entry<String, AnnotatableInvocationHandler> entry : handler.getPropAnnotatableHandlers().entrySet()) {
          CoreUtils.addAnnotations(service.getClient(),
                  entry.getValue().getAnnotations(), ((ODataEntity) entity).getProperty(entry.getKey()));
        }
      }
    }

    for (Map.Entry<NavigationProperty, Object> property : handler.getLinkChanges().entrySet()) {
      final ODataLinkType type = Collection.class.isAssignableFrom(property.getValue().getClass())
              ? ODataLinkType.ENTITY_SET_NAVIGATION
              : ODataLinkType.ENTITY_NAVIGATION;

      final Set<EntityInvocationHandler> toBeLinked = new HashSet<EntityInvocationHandler>();
      for (Object proxy : type == ODataLinkType.ENTITY_SET_NAVIGATION
              ? (Collection<?>) property.getValue() : Collections.singleton(property.getValue())) {

        final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(proxy);

        toBeLinked.addAll(processLinkChanges(
                handler, target, property.getKey(), type, pos, items, delayedUpdates, changeset));
      }

      if (!toBeLinked.isEmpty()) {
        delayedUpdates.add(new EntityLinkDesc(property.getKey().name(), handler, toBeLinked, type));
      }

      if (property.getValue() instanceof Proxy) {
        final InvocationHandler target = Proxy.getInvocationHandler(property.getValue());

        if (target instanceof EntityCollectionInvocationHandler) {
          for (String ref : ((EntityCollectionInvocationHandler<?>) target).referenceItems) {
            delayedUpdates.add(new EntityLinkDesc(property.getKey().name(), handler, ref));
          }
        }
      }
    }

    // Required by linking provided on existent object. Say: 
    //                    container.getCustomers().getByKey(1).getOrders().add(order)
    // Required by linking provided via entity reference ID. Say: 
    //                    container.getCustomers().getByKey(1).getOrders().addRef(order)
    for (Map.Entry<NavigationProperty, Object> property : handler.linkCache.entrySet()) {
      if (property.getValue() instanceof Proxy) {
        final InvocationHandler target = Proxy.getInvocationHandler(property.getValue());

        if (target instanceof EntityCollectionInvocationHandler
                && ((EntityCollectionInvocationHandler) target).isChanged()) {

          final ODataLinkType type = Collection.class.isAssignableFrom(property.getValue().getClass())
                  ? ODataLinkType.ENTITY_SET_NAVIGATION
                  : ODataLinkType.ENTITY_NAVIGATION;

          final Set<EntityInvocationHandler> toBeLinked = new HashSet<EntityInvocationHandler>();

          for (Object proxy : ((EntityCollectionInvocationHandler) target).newest) {
            final EntityInvocationHandler targetEntity = (EntityInvocationHandler) Proxy.getInvocationHandler(proxy);

            toBeLinked.addAll(processLinkChanges(
                    handler, targetEntity, property.getKey(), type, pos, items, delayedUpdates, changeset));
          }

          if (!toBeLinked.isEmpty()) {
            delayedUpdates.add(new EntityLinkDesc(property.getKey().name(), handler, toBeLinked, type));
          }

          for (String ref : ((EntityCollectionInvocationHandler<?>) target).referenceItems) {
            delayedUpdates.add(new EntityLinkDesc(property.getKey().name(), handler, ref));
          }
        }
      }
    }

    if (entity instanceof ODataEntity) {
      for (Map.Entry<String, AnnotatableInvocationHandler> entry : handler.getNavPropAnnotatableHandlers().entrySet()) {

        CoreUtils.addAnnotations(service.getClient(),
                entry.getValue().getAnnotations(),
                (org.apache.olingo.commons.api.domain.v4.ODataLink) entity.getNavigationLink(entry.getKey()));
      }
    }

    final AttachedEntityStatus processedStatus = queue(handler, entity, changeset);
    if (processedStatus != null) {
      // insert into the process queue
      LOG.debug("{}: Insert '{}' into the process queue", pos, handler);
      items.put(handler, pos);
    } else {
      pos--;
    }

    if (processedStatus != AttachedEntityStatus.DELETED) {
      int startingPos = pos;

      if (handler.getEntity().isMediaEntity() && handler.isChanged()) {
        // update media properties
        if (!handler.getPropertyChanges().isEmpty()) {
          final URI targetURI = currentStatus == AttachedEntityStatus.NEW
                  ? URI.create("$" + startingPos)
                  : URIUtils.getURI(
                  service.getClient().getServiceRoot(), handler.getEntity().getEditLink().toASCIIString());
          queueUpdate(handler, targetURI, entity, changeset);
          pos++;
          items.put(handler, pos);
          LOG.debug("{}: Update media properties for '{}' into the process queue", pos, handler);
        }

        // update media content
        if (handler.getStreamChanges() != null) {
          final URI targetURI = currentStatus == AttachedEntityStatus.NEW
                  ? URI.create("$" + startingPos + "/$value")
                  : URIUtils.getURI(
                  service.getClient().getServiceRoot(),
                  handler.getEntity().getEditLink().toASCIIString() + "/$value");

          queueUpdateMediaEntity(handler, targetURI, handler.getStreamChanges(), changeset);

          // update media info (use null key)
          pos++;
          items.put(null, pos);
          LOG.debug("{}: Update media info for '{}' into the process queue", pos, handler);
        }
      }

      for (Map.Entry<String, EdmStreamValue> streamedChanges : handler.getStreamedPropertyChanges().entrySet()) {
        final URI targetURI = currentStatus == AttachedEntityStatus.NEW
                ? URI.create("$" + startingPos) : URIUtils.getURI(
                service.getClient().getServiceRoot(),
                CoreUtils.getMediaEditLink(streamedChanges.getKey(), entity).toASCIIString());

        queueUpdateMediaResource(handler, targetURI, streamedChanges.getValue(), changeset);

        // update media info (use null key)
        pos++;
        items.put(handler, pos);
        LOG.debug("{}: Update media info (null key) for '{}' into the process queue", pos, handler);
      }
    }

    return pos;
  }

  protected Set<EntityInvocationHandler> processLinkChanges(
          final EntityInvocationHandler source,
          final EntityInvocationHandler target,
          final NavigationProperty property,
          final ODataLinkType type,
          int pos,
          final TransactionItems items,
          final List<EntityLinkDesc> delayedUpdates,
          final PersistenceChanges changeset) {

    final Set<EntityInvocationHandler> toBeLinked = new HashSet<EntityInvocationHandler>();

    final AttachedEntityStatus status;
    if (!service.getContext().entityContext().isAttached(target)) {
      status = resolveNavigationLink(property, target);
    } else {
      status = service.getContext().entityContext().getStatus(target);
    }

    LOG.debug("Found link to '{}({})'", target, status);

    final URI editLink = target.getEntity().getEditLink();

    if ((status == AttachedEntityStatus.ATTACHED || status == AttachedEntityStatus.LINKED) && !target.isChanged()) {
      LOG.debug("Add link to '{}'", target);
      source.getEntity().addLink(buildNavigationLink(
              property.name(),
              URIUtils.getURI(service.getClient().getServiceRoot(), editLink.toASCIIString()), type));
    } else {
      if (!items.contains(target)) {
        pos = processEntityContext(target, pos, items, delayedUpdates, changeset);
        pos++;
      }

      final Integer targetPos = items.get(target);
      if (targetPos == null) {
        // schedule update for the current object
        LOG.debug("Schedule '{}' from '{}' to '{}'", type.name(), source, target);
        toBeLinked.add(target);
      } else if (status == AttachedEntityStatus.CHANGED) {
        LOG.debug("Changed: '{}' from '{}' to (${}) '{}'", type.name(), source, targetPos, target);
        source.getEntity().addLink(buildNavigationLink(
                property.name(),
                URIUtils.getURI(service.getClient().getServiceRoot(), editLink.toASCIIString()), type));
      } else {
        // create the link for the current object
        LOG.debug("'{}' from '{}' to (${}) '{}'", type.name(), source, targetPos, target);

        source.getEntity().addLink(
                buildNavigationLink(property.name(), URI.create("$" + targetPos), type));
      }
    }

    return toBeLinked;
  }

  protected void processDelayedUpdates(
          final List<EntityLinkDesc> delayedUpdates,
          int pos,
          final TransactionItems items,
          final PersistenceChanges changeset) {

    for (EntityLinkDesc delayedUpdate : delayedUpdates) {
      if (StringUtils.isBlank(delayedUpdate.getReference())) {

        pos++;
        items.put(delayedUpdate.getSource(), pos);

        final CommonODataEntity changes =
                service.getClient().getObjectFactory().newEntity(delayedUpdate.getSource().getEntity().getTypeName());

        AttachedEntityStatus status = service.getContext().entityContext().getStatus(delayedUpdate.getSource());

        final URI sourceURI;
        if (status == AttachedEntityStatus.CHANGED) {
          sourceURI = URIUtils.getURI(
                  service.getClient().getServiceRoot(),
                  delayedUpdate.getSource().getEntity().getEditLink().toASCIIString());
        } else {
          int sourcePos = items.get(delayedUpdate.getSource());
          sourceURI = URI.create("$" + sourcePos);
        }

        for (EntityInvocationHandler target : delayedUpdate.getTargets()) {
          status = service.getContext().entityContext().getStatus(target);

          final URI targetURI;
          if (status == AttachedEntityStatus.CHANGED) {
            targetURI = URIUtils.getURI(
                    service.getClient().getServiceRoot(), target.getEntity().getEditLink().toASCIIString());
          } else {
            int targetPos = items.get(target);
            targetURI = URI.create("$" + targetPos);
          }

          changes.addLink(delayedUpdate.getType() == ODataLinkType.ENTITY_NAVIGATION
                  ? service.getClient().getObjectFactory().
                  newEntityNavigationLink(delayedUpdate.getSourceName(), targetURI)
                  : service.getClient().getObjectFactory().
                  newEntitySetNavigationLink(delayedUpdate.getSourceName(), targetURI));

          LOG.debug("'{}' from {} to {}", delayedUpdate.getType().name(), sourceURI, targetURI);
        }

        queueUpdate(delayedUpdate.getSource(), sourceURI, changes, changeset);
      } else {
        URI sourceURI = URIUtils.getURI(
                service.getClient().getServiceRoot(),
                delayedUpdate.getSource().getEntity().getEditLink().toASCIIString()
                + "/" + delayedUpdate.getSourceName() + "/$ref");

        if (queueUpdateLinkViaRef(
                delayedUpdate.getSource(), sourceURI, URI.create(delayedUpdate.getReference()), changeset)) {
          pos++;
          items.put(delayedUpdate.getSource(), pos);
        }
      }
    }
  }

  private AttachedEntityStatus queue(
          final EntityInvocationHandler handler,
          final CommonODataEntity entity,
          final PersistenceChanges changeset) {

    switch (service.getContext().entityContext().getStatus(handler)) {
      case NEW:
        queueCreate(handler, entity, changeset);
        return AttachedEntityStatus.NEW;

      case DELETED:
        queueDelete(handler, entity, changeset);
        return AttachedEntityStatus.DELETED;

      default:
        if (handler.isChanged(false)) {
          queueUpdate(handler, entity, changeset);
          return AttachedEntityStatus.CHANGED;
        } else {
          return null;
        }
    }
  }

  private void queueCreate(
          final EntityInvocationHandler handler,
          final CommonODataEntity entity,
          final PersistenceChanges changeset) {

    LOG.debug("Create '{}'", handler);

    changeset.addChange(service.getClient().getCUDRequestFactory().
            getEntityCreateRequest(handler.getEntitySetURI(), entity), handler);
  }

  private void queueUpdateMediaEntity(
          final EntityInvocationHandler handler,
          final URI uri,
          final EdmStreamValue input,
          final PersistenceChanges changeset) {

    LOG.debug("Update media entity '{}'", uri);

    final ODataMediaEntityUpdateRequest<?> req =
            service.getClient().getCUDRequestFactory().getMediaEntityUpdateRequest(uri, input.getStream());

    if (StringUtils.isNotBlank(input.getContentType())) {
      req.setContentType(input.getContentType());
    }

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addChange(req, handler);
  }

  private void queueUpdateMediaResource(
          final EntityInvocationHandler handler,
          final URI uri,
          final EdmStreamValue input,
          final PersistenceChanges changeset) {

    LOG.debug("Update media entity '{}'", uri);

    final ODataStreamUpdateRequest req =
            service.getClient().getCUDRequestFactory().getStreamUpdateRequest(uri, input.getStream());

    if (StringUtils.isNotBlank(input.getContentType())) {
      req.setContentType(input.getContentType());
    }

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addChange(req, handler);
  }

  private void queueUpdate(
          final EntityInvocationHandler handler,
          final CommonODataEntity changes,
          final PersistenceChanges changeset) {

    LOG.debug("Update '{}'", handler.getEntityURI());

    final ODataEntityUpdateRequest<CommonODataEntity> req =
            service.getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0
            ? ((org.apache.olingo.client.api.v3.EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getEntityUpdateRequest(handler.getEntityURI(),
            org.apache.olingo.client.api.communication.request.cud.v3.UpdateType.PATCH, changes)
            : ((org.apache.olingo.client.api.v4.EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getEntityUpdateRequest(handler.getEntityURI(),
            org.apache.olingo.client.api.communication.request.cud.v4.UpdateType.PATCH, changes);

    req.setPrefer(new ODataPreferences(service.getClient().getServiceVersion()).returnContent());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addChange(req, handler);
  }

  private boolean queueUpdateLinkViaRef(
          final EntityInvocationHandler handler,
          final URI source,
          final URI targetRef,
          final PersistenceChanges changeset) {

    LOG.debug("Update '{}'", targetRef);
    if (service.getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) >= 1) {
      final ODataReferenceAddingRequest req =
              ((org.apache.olingo.client.api.v4.EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
              getReferenceAddingRequest(source, targetRef);

      req.setPrefer(new ODataPreferences(service.getClient().getServiceVersion()).returnContent());

      if (StringUtils.isNotBlank(handler.getETag())) {
        req.setIfMatch(handler.getETag());
      }

      changeset.addChange(req, handler);
      return true;
    }
    return false;
  }

  private void queueUpdate(
          final EntityInvocationHandler handler,
          final URI uri,
          final CommonODataEntity changes,
          final PersistenceChanges changeset) {

    LOG.debug("Update '{}'", uri);

    final ODataEntityUpdateRequest<CommonODataEntity> req =
            service.getClient().getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0
            ? ((org.apache.olingo.client.api.v3.EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getEntityUpdateRequest(uri,
            org.apache.olingo.client.api.communication.request.cud.v3.UpdateType.PATCH, changes)
            : ((org.apache.olingo.client.api.v4.EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getEntityUpdateRequest(uri,
            org.apache.olingo.client.api.communication.request.cud.v4.UpdateType.PATCH, changes);

    req.setPrefer(new ODataPreferences(service.getClient().getServiceVersion()).returnContent());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addChange(req, handler);
  }

  private void queueDelete(
          final EntityInvocationHandler handler,
          final CommonODataEntity entity,
          final PersistenceChanges changeset) {
    final URI deleteURI = entity.getEditLink() == null ? handler.getEntityURI() : entity.getEditLink();
    changeset.addChange(buildDeleteRequest(deleteURI, handler.getETag(), changeset), handler);
  }

  private void queueDelete(
          final URI deleteURI,
          final String etag,
          final PersistenceChanges changeset) {
    changeset.addChange(buildDeleteRequest(deleteURI, etag, changeset), null);
  }

  private ODataDeleteRequest buildDeleteRequest(
          final URI deleteURI,
          final String etag,
          final PersistenceChanges changeset) {

    LOG.debug("Delete '{}'", deleteURI);

    final ODataDeleteRequest req = service.getClient().getCUDRequestFactory().getDeleteRequest(deleteURI);

    if (StringUtils.isNotBlank(etag)) {
      req.setIfMatch(etag);
    }

    return req;
  }

  private AttachedEntityStatus resolveNavigationLink(
          final NavigationProperty property, final EntityInvocationHandler handler) {
    if (handler.getUUID().getEntitySetURI() == null) {
      final Object key = CoreUtils.getKey(service.getClient(), handler, handler.getTypeRef(), handler.getEntity());
      handler.updateUUID(CoreUtils.getTargetEntitySetURI(service.getClient(), property), handler.getTypeRef(), null);
      service.getContext().entityContext().attach(handler, AttachedEntityStatus.NEW);
      return AttachedEntityStatus.NEW;
    } else {
      // existent object
      service.getContext().entityContext().attach(handler, AttachedEntityStatus.LINKED);
      return AttachedEntityStatus.LINKED;
    }
  }
}
