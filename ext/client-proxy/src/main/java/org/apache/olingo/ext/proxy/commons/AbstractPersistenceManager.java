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
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataReferenceAddingRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.context.AttachedEntity;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityLinkDesc;
import org.apache.olingo.ext.proxy.utils.ClassUtils;
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
  public Future<Void> flushAsync() {
    return service.getClient().getConfiguration().getExecutor().submit(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        flush();
        return ClassUtils.returnVoid();
      }
    });
  }

  protected abstract void doFlush(PersistenceChanges changes, TransactionItems items);

  @Override
  public void flush() {
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

    if (!items.isEmpty()) {
      doFlush(changes, items);
    }

    service.getContext().detachAll();
  }

  private ClientLink buildNavigationLink(final String name, final URI uri, final ClientLinkType type) {
    ClientLink result;

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
    int posNumber = pos;
    items.put(handler, null);

    final ClientEntity entity = handler.getEntity();
    entity.getNavigationLinks().clear();

    final AttachedEntityStatus currentStatus = service.getContext().entityContext().getStatus(handler);
    LOG.debug("Process '{}({})'", handler, currentStatus);

    if (AttachedEntityStatus.DELETED != currentStatus) {
      entity.getProperties().clear();
      CoreUtils.addProperties(service.getClient(), handler.getPropertyChanges(), entity);

      entity.getAnnotations().clear();
      CoreUtils.addAnnotations(service.getClient(), handler.getAnnotations(), entity);

      for (Map.Entry<String, AnnotatableInvocationHandler> entry : handler.getPropAnnotatableHandlers().entrySet()) {
        CoreUtils.addAnnotations(service.getClient(),
            entry.getValue().getAnnotations(), entity.getProperty(entry.getKey()));
      }
    }

    for (Map.Entry<NavigationProperty, Object> property : handler.getLinkChanges().entrySet()) {
      final ClientLinkType type = Collection.class.isAssignableFrom(property.getValue().getClass())
          ? ClientLinkType.ENTITY_SET_NAVIGATION
          : ClientLinkType.ENTITY_NAVIGATION;

      final Set<EntityInvocationHandler> toBeLinked = new HashSet<EntityInvocationHandler>();

      for (Object proxy : type == ClientLinkType.ENTITY_SET_NAVIGATION
          ? (Collection<?>) property.getValue() : Collections.singleton(property.getValue())) {

        final EntityInvocationHandler target = (EntityInvocationHandler) Proxy.getInvocationHandler(proxy);

        final AttachedEntityStatus status;
        if (!service.getContext().entityContext().isAttached(target)) {
          status = resolveNavigationLink(property.getKey(), target);
        } else {
          status = service.getContext().entityContext().getStatus(target);
        }

        LOG.debug("Found link to '{}({})'", target, status);

        final URI editLink = target.getEntity().getEditLink();

        if ((status == AttachedEntityStatus.ATTACHED || status == AttachedEntityStatus.LINKED) && !target.isChanged()) {
          LOG.debug("Add link to '{}'", target);
          entity.addLink(buildNavigationLink(
              property.getKey().name(),
              URIUtils.getURI(service.getClient().getServiceRoot(), editLink.toASCIIString()), type));
        } else {
          if (!items.contains(target)) {
            posNumber = processEntityContext(target, posNumber, items, delayedUpdates, changeset);
            posNumber++;
          }

          final Integer targetPos = items.get(target);
          if (targetPos == null) {
            // schedule update for the current object
            LOG.debug("Schedule '{}' from '{}' to '{}'", type.name(), handler, target);
            toBeLinked.add(target);
          } else if (status == AttachedEntityStatus.CHANGED) {
            LOG.debug("Changed: '{}' from '{}' to (${}) '{}'", type.name(), handler, targetPos, target);
            entity.addLink(buildNavigationLink(
                property.getKey().name(),
                URIUtils.getURI(service.getClient().getServiceRoot(), editLink.toASCIIString()), type));
          } else {
            // create the link for the current object
            LOG.debug("'{}' from '{}' to (${}) '{}'", type.name(), handler, targetPos, target);

            entity.addLink(buildNavigationLink(property.getKey().name(), URI.create("$" + targetPos), type));
          }
        }
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

    for (Map.Entry<String, AnnotatableInvocationHandler> entry : handler.getNavPropAnnotatableHandlers().entrySet()) {

      CoreUtils.addAnnotations(service.getClient(),
          entry.getValue().getAnnotations(),
          entity.getNavigationLink(entry.getKey()));
    }

    final AttachedEntityStatus processedStatus = queue(handler, entity, changeset);
    if (processedStatus != null) {
      // insert into the process queue
      LOG.debug("{}: Insert '{}' into the process queue", posNumber, handler);
      items.put(handler, posNumber);
    } else {
      posNumber--;
    }

    if (processedStatus != AttachedEntityStatus.DELETED) {
      int startingPos = posNumber;

      if (handler.getEntity().isMediaEntity() && handler.isChanged()) {
        // update media properties
        if (!handler.getPropertyChanges().isEmpty()) {
          final URI targetURI = currentStatus == AttachedEntityStatus.NEW
              ? URI.create("$" + startingPos)
              : URIUtils.getURI(
                  service.getClient().getServiceRoot(), handler.getEntity().getEditLink().toASCIIString());
          queueUpdate(handler, targetURI, entity, changeset);
          posNumber++;
          items.put(handler, posNumber);
          LOG.debug("{}: Update media properties for '{}' into the process queue", posNumber, handler);
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
          posNumber++;
          items.put(null, posNumber);
          LOG.debug("{}: Update media info for '{}' into the process queue", posNumber, handler);
        }
      }

      for (Map.Entry<String, EdmStreamValue> streamedChanges : handler.getStreamedPropertyChanges().entrySet()) {
        final URI targetURI = currentStatus == AttachedEntityStatus.NEW
            ? URI.create("$" + startingPos) : URIUtils.getURI(
                service.getClient().getServiceRoot(),
                CoreUtils.getMediaEditLink(streamedChanges.getKey(), entity).toASCIIString());

        queueUpdateMediaResource(handler, targetURI, streamedChanges.getValue(), changeset);

        // update media info (use null key)
        posNumber++;
        items.put(handler, posNumber);
        LOG.debug("{}: Update media info (null key) for '{}' into the process queue", posNumber, handler);
      }
    }

    return posNumber;
  }

  protected void processDelayedUpdates(
      final List<EntityLinkDesc> delayedUpdates,
      int pos,
      final TransactionItems items,
      final PersistenceChanges changeset) {
    int posNumber = pos;
    for (EntityLinkDesc delayedUpdate : delayedUpdates) {
      if (StringUtils.isBlank(delayedUpdate.getReference())) {

        posNumber++;
        items.put(delayedUpdate.getSource(), posNumber);

        final ClientEntity changes =
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

          changes.addLink(delayedUpdate.getType() == ClientLinkType.ENTITY_NAVIGATION
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
          posNumber++;
          items.put(delayedUpdate.getSource(), posNumber);
        }
      }
    }
  }

  private AttachedEntityStatus queue(
      final EntityInvocationHandler handler,
      final ClientEntity entity,
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
      final ClientEntity entity,
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
      final ClientEntity changes,
      final PersistenceChanges changeset) {

    LOG.debug("Update '{}'", handler.getEntityURI());

    final ODataEntityUpdateRequest<ClientEntity> req =
        ((EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getEntityUpdateRequest(handler.getEntityURI(),
                org.apache.olingo.client.api.communication.request.cud.UpdateType.PATCH, changes);

    req.setPrefer(new ODataPreferences().returnContent());

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
    URI sericeRoot = handler.getClient().newURIBuilder(handler.getClient().getServiceRoot()).build();

    final ODataReferenceAddingRequest req =
        ((org.apache.olingo.client.api.EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getReferenceAddingRequest(sericeRoot, source, targetRef);

    req.setPrefer(new ODataPreferences().returnContent());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addChange(req, handler);
    return true;
  }

  private void queueUpdate(
      final EntityInvocationHandler handler,
      final URI uri,
      final ClientEntity changes,
      final PersistenceChanges changeset) {

    LOG.debug("Update '{}'", uri);

    final ODataEntityUpdateRequest<ClientEntity> req =
        ((EdmEnabledODataClient) service.getClient()).getCUDRequestFactory().
            getEntityUpdateRequest(uri,
                org.apache.olingo.client.api.communication.request.cud.UpdateType.PATCH, changes);

    req.setPrefer(new ODataPreferences().returnContent());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addChange(req, handler);
  }

  private void queueDelete(
      final EntityInvocationHandler handler,
      final ClientEntity entity,
      final PersistenceChanges changeset) {
    final URI deleteURI = entity.getEditLink() == null ? handler.getEntityURI() : entity.getEditLink();
    changeset.addChange(buildDeleteRequest(deleteURI, handler.getETag()), handler);
  }

  private void queueDelete(
      final URI deleteURI,
      final String etag,
      final PersistenceChanges changeset) {
    changeset.addChange(buildDeleteRequest(deleteURI, etag), null);
  }

  private ODataDeleteRequest buildDeleteRequest(final URI deleteURI, final String etag) {

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
      //Load key
      CoreUtils.getKey(service.getClient(), handler, handler.getTypeRef(), handler.getEntity());
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
