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

import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonEdmEnabledODataClient;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.ODataStreamedRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchStreamManager;
import org.apache.olingo.client.api.communication.request.batch.CommonODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataMediaFormat;
import org.apache.olingo.ext.proxy.EntityContainerFactory;
import org.apache.olingo.ext.proxy.api.Container;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.context.AttachedEntity;
import org.apache.olingo.ext.proxy.context.AttachedEntityStatus;
import org.apache.olingo.ext.proxy.context.EntityLinkDesc;
import org.apache.olingo.ext.proxy.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ContainerImpl implements Container {

  private static final long serialVersionUID = -3320312269235907501L;

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ContainerImpl.class);

  private final CommonEdmEnabledODataClient<?> client;

  private final EntityContainerFactory factory;

  ContainerImpl(final CommonEdmEnabledODataClient<?> client, final EntityContainerFactory factory) {
    this.client = client;
    this.factory = factory;
  }

  /**
   * Transactional changes commit.
   */
  @Override
  public void flush() {
    final CommonODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(client.getServiceRoot());

    final BatchStreamManager streamManager = (BatchStreamManager) ((ODataStreamedRequest) request).execute();

    final ODataChangeset changeset = streamManager.addChangeset();

    final TransactionItems items = new TransactionItems();
    final List<EntityLinkDesc> delayedUpdates = new ArrayList<EntityLinkDesc>();

    int pos = 0;

    for (AttachedEntity attachedEntity : EntityContainerFactory.getContext().entityContext()) {
      final AttachedEntityStatus status = attachedEntity.getStatus();
      if (((status != AttachedEntityStatus.ATTACHED
              && status != AttachedEntityStatus.LINKED) || attachedEntity.getEntity().isChanged())
              && !items.contains(attachedEntity.getEntity())) {
        pos++;
        pos = processEntityContext(attachedEntity.getEntity(), pos, items, delayedUpdates, changeset);
      }
    }

    processDelayedUpdates(delayedUpdates, pos, items, changeset);

    final ODataBatchResponse response = streamManager.getResponse();

    if (response.getStatusCode() != 202) {
      throw new IllegalStateException("Operation failed");
    }

    if (!items.isEmpty()) {
      final Iterator<ODataBatchResponseItem> iter = response.getBody();
      if (!iter.hasNext()) {
        throw new IllegalStateException("Unexpected operation result");
      }

      final ODataBatchResponseItem item = iter.next();
      if (!(item instanceof ODataChangesetResponseItem)) {
        throw new IllegalStateException("Unexpected batch response item " + item.getClass().getSimpleName());
      }

      final ODataChangesetResponseItem chgres = (ODataChangesetResponseItem) item;

      for (Integer changesetItemId : items.sortedValues()) {
        LOG.debug("Expected changeset item {}", changesetItemId);
        final ODataResponse res = chgres.next();
        if (res.getStatusCode() >= 400) {
          throw new IllegalStateException("Transaction failed: " + res.getStatusMessage());
        }

        final EntityTypeInvocationHandler<?> handler = items.get(changesetItemId);

        if (handler != null) {
          if (res instanceof ODataEntityCreateResponse) {
            LOG.debug("Upgrade created object '{}'", handler);
            handler.setEntity(((ODataEntityCreateResponse) res).getBody());
          } else if (res instanceof ODataEntityUpdateResponse) {
            LOG.debug("Upgrade updated object '{}'", handler);
            handler.setEntity(((ODataEntityUpdateResponse) res).getBody());
          }
        }
      }
    }

    EntityContainerFactory.getContext().detachAll();
  }

  private void batch(
          final EntityTypeInvocationHandler<?> handler,
          final CommonODataEntity entity,
          final ODataChangeset changeset) {

    switch (EntityContainerFactory.getContext().entityContext().getStatus(handler)) {
      case NEW:
        batchCreate(handler, entity, changeset);
        break;

      case CHANGED:
        batchUpdate(handler, entity, changeset);
        break;

      case DELETED:
        batchDelete(handler, entity, changeset);
        break;

      default:
        if (handler.isChanged()) {
          batchUpdate(handler, entity, changeset);
        }
    }
  }

  private void batchCreate(
          final EntityTypeInvocationHandler<?> handler,
          final CommonODataEntity entity,
          final ODataChangeset changeset) {

    LOG.debug("Create '{}'", handler);

    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(factory.getServiceRoot()).
            appendEntitySetSegment(handler.getEntitySetName());
    changeset.addRequest(client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), entity));
  }

  private void batchUpdateMediaEntity(
          final EntityTypeInvocationHandler<?> handler,
          final URI uri,
          final InputStream input,
          final ODataChangeset changeset) {

    LOG.debug("Update media entity '{}'", uri);

    final ODataMediaEntityUpdateRequest<?> req =
            client.getStreamedRequestFactory().getMediaEntityUpdateRequest(uri, input);

    req.setContentType(StringUtils.isBlank(handler.getEntity().getMediaContentType())
            ? ODataMediaFormat.WILDCARD.toString()
            : ODataMediaFormat.fromFormat(handler.getEntity().getMediaContentType()).toString());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addRequest(req);
  }

  private void batchUpdateMediaResource(
          final EntityTypeInvocationHandler<?> handler,
          final URI uri,
          final InputStream input,
          final ODataChangeset changeset) {

    LOG.debug("Update media entity '{}'", uri);

    final ODataStreamUpdateRequest req = client.getStreamedRequestFactory().getStreamUpdateRequest(uri, input);

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addRequest(req);
  }

  private void batchUpdate(
          final EntityTypeInvocationHandler<?> handler,
          final CommonODataEntity changes,
          final ODataChangeset changeset) {

    LOG.debug("Update '{}'", changes.getEditLink());

    final ODataEntityUpdateRequest<CommonODataEntity> req =
            client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0
            ? ((org.apache.olingo.client.api.v3.EdmEnabledODataClient) client).getCUDRequestFactory().
            getEntityUpdateRequest(org.apache.olingo.client.api.communication.request.cud.v3.UpdateType.PATCH, changes)
            : ((org.apache.olingo.client.api.v4.EdmEnabledODataClient) client).getCUDRequestFactory().
            getEntityUpdateRequest(org.apache.olingo.client.api.communication.request.cud.v4.UpdateType.PATCH, changes);

    req.setPrefer(new ODataPreferences(client.getServiceVersion()).returnContent());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addRequest(req);
  }

  private void batchUpdate(
          final EntityTypeInvocationHandler<?> handler,
          final URI uri,
          final CommonODataEntity changes,
          final ODataChangeset changeset) {

    LOG.debug("Update '{}'", uri);

    final ODataEntityUpdateRequest<CommonODataEntity> req =
            client.getServiceVersion().compareTo(ODataServiceVersion.V30) <= 0
            ? ((org.apache.olingo.client.api.v3.EdmEnabledODataClient) client).getCUDRequestFactory().
            getEntityUpdateRequest(
                    uri, org.apache.olingo.client.api.communication.request.cud.v3.UpdateType.PATCH, changes)
            : ((org.apache.olingo.client.api.v4.EdmEnabledODataClient) client).getCUDRequestFactory().
            getEntityUpdateRequest(
                    uri, org.apache.olingo.client.api.communication.request.cud.v4.UpdateType.PATCH, changes);

    req.setPrefer(new ODataPreferences(client.getServiceVersion()).returnContent());

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addRequest(req);
  }

  private void batchDelete(
          final EntityTypeInvocationHandler<?> handler,
          final CommonODataEntity entity,
          final ODataChangeset changeset) {

    LOG.debug("Delete '{}'", entity.getEditLink());

    final ODataDeleteRequest req = client.getCUDRequestFactory().getDeleteRequest(URIUtils.getURI(
            factory.getServiceRoot(), entity.getEditLink().toASCIIString()));

    if (StringUtils.isNotBlank(handler.getETag())) {
      req.setIfMatch(handler.getETag());
    }

    changeset.addRequest(req);
  }

  private int processEntityContext(
          final EntityTypeInvocationHandler<?> handler,
          int pos,
          final TransactionItems items,
          final List<EntityLinkDesc> delayedUpdates,
          final ODataChangeset changeset) {

    LOG.debug("Process '{}'", handler);

    items.put(handler, null);

    final CommonODataEntity entity = handler.getEntity();
    entity.getNavigationLinks().clear();

    final AttachedEntityStatus currentStatus = EntityContainerFactory.getContext().entityContext().
            getStatus(handler);

    if (AttachedEntityStatus.DELETED != currentStatus) {
      entity.getProperties().clear();
      CoreUtils.addProperties(client, handler.getPropertyChanges(), entity);
    }

    for (Map.Entry<NavigationProperty, Object> property : handler.getLinkChanges().entrySet()) {
      final ODataLinkType type = Collection.class.isAssignableFrom(property.getValue().getClass())
              ? ODataLinkType.ENTITY_SET_NAVIGATION
              : ODataLinkType.ENTITY_NAVIGATION;

      final Set<EntityTypeInvocationHandler<?>> toBeLinked = new HashSet<EntityTypeInvocationHandler<?>>();
      final String serviceRoot = factory.getServiceRoot();

      for (Object proxy : type == ODataLinkType.ENTITY_SET_NAVIGATION
              ? (Collection) property.getValue() : Collections.singleton(property.getValue())) {

        final EntityTypeInvocationHandler<?> target =
                (EntityTypeInvocationHandler) Proxy.getInvocationHandler(proxy);

        final AttachedEntityStatus status =
                EntityContainerFactory.getContext().entityContext().getStatus(target);

        final URI editLink = target.getEntity().getEditLink();

        if ((status == AttachedEntityStatus.ATTACHED || status == AttachedEntityStatus.LINKED)
                && !target.isChanged()) {
          entity.addLink(buildNavigationLink(
                  property.getKey().name(),
                  URIUtils.getURI(serviceRoot, editLink.toASCIIString()), type));
        } else {
          if (!items.contains(target)) {
            pos = processEntityContext(target, pos, items, delayedUpdates, changeset);
            pos++;
          }

          final Integer targetPos = items.get(target);
          if (targetPos == null) {
            // schedule update for the current object
            LOG.debug("Schedule '{}' from '{}' to '{}'", type.name(), handler, target);
            toBeLinked.add(target);
          } else if (status == AttachedEntityStatus.CHANGED) {
            entity.addLink(buildNavigationLink(
                    property.getKey().name(),
                    URIUtils.getURI(serviceRoot, editLink.toASCIIString()), type));
          } else {
            // create the link for the current object
            LOG.debug("'{}' from '{}' to (${}) '{}'", type.name(), handler, targetPos, target);

            entity.addLink(
                    buildNavigationLink(property.getKey().name(), URI.create("$" + targetPos), type));
          }
        }
      }

      if (!toBeLinked.isEmpty()) {
        delayedUpdates.add(new EntityLinkDesc(property.getKey().name(), handler, toBeLinked, type));
      }
    }

    // insert into the batch
    LOG.debug("{}: Insert '{}' into the batch", pos, handler);
    batch(handler, entity, changeset);

    items.put(handler, pos);

    int startingPos = pos;

    if (handler.getEntity().isMediaEntity()) {

      // update media properties
      if (!handler.getPropertyChanges().isEmpty()) {
        final URI targetURI = currentStatus == AttachedEntityStatus.NEW
                ? URI.create("$" + startingPos)
                : URIUtils.getURI(factory.getServiceRoot(), handler.getEntity().getEditLink().toASCIIString());
        batchUpdate(handler, targetURI, entity, changeset);
        pos++;
        items.put(handler, pos);
      }

      // update media content
      if (handler.getStreamChanges() != null) {
        final URI targetURI = currentStatus == AttachedEntityStatus.NEW
                ? URI.create("$" + startingPos + "/$value")
                : URIUtils.getURI(
                        factory.getServiceRoot(), handler.getEntity().getEditLink().toASCIIString() + "/$value");

        batchUpdateMediaEntity(handler, targetURI, handler.getStreamChanges(), changeset);

        // update media info (use null key)
        pos++;
        items.put(null, pos);
      }
    }

    for (Map.Entry<String, InputStream> streamedChanges : handler.getStreamedPropertyChanges().entrySet()) {
      final URI targetURI = currentStatus == AttachedEntityStatus.NEW
              ? URI.create("$" + startingPos) : URIUtils.getURI(
                      factory.getServiceRoot(),
                      CoreUtils.getEditMediaLink(streamedChanges.getKey(), entity).toASCIIString());

      batchUpdateMediaResource(handler, targetURI, streamedChanges.getValue(), changeset);

      // update media info (use null key)
      pos++;
      items.put(handler, pos);
    }

    return pos;
  }

  private ODataLink buildNavigationLink(final String name, final URI uri, final ODataLinkType type) {
    ODataLink result;

    switch (type) {
      case ENTITY_NAVIGATION:
        result = client.getObjectFactory().newEntityNavigationLink(name, uri);
        break;

      case ENTITY_SET_NAVIGATION:
        result = client.getObjectFactory().newEntitySetNavigationLink(name, uri);
        break;

      default:
        throw new IllegalArgumentException("Invalid link type " + type.name());
    }

    return result;
  }

  private void processDelayedUpdates(
          final List<EntityLinkDesc> delayedUpdates,
          int pos,
          final TransactionItems items,
          final ODataChangeset changeset) {

    for (EntityLinkDesc delayedUpdate : delayedUpdates) {
      pos++;
      items.put(delayedUpdate.getSource(), pos);

      final CommonODataEntity changes =
              client.getObjectFactory().newEntity(delayedUpdate.getSource().getEntity().getTypeName());

      AttachedEntityStatus status =
              EntityContainerFactory.getContext().entityContext().getStatus(delayedUpdate.getSource());

      final URI sourceURI;
      if (status == AttachedEntityStatus.CHANGED) {
        sourceURI = URIUtils.getURI(
                factory.getServiceRoot(),
                delayedUpdate.getSource().getEntity().getEditLink().toASCIIString());
      } else {
        int sourcePos = items.get(delayedUpdate.getSource());
        sourceURI = URI.create("$" + sourcePos);
      }

      for (EntityTypeInvocationHandler<?> target : delayedUpdate.getTargets()) {
        status = EntityContainerFactory.getContext().entityContext().getStatus(target);

        final URI targetURI;
        if (status == AttachedEntityStatus.CHANGED) {
          targetURI = URIUtils.getURI(
                  factory.getServiceRoot(), target.getEntity().getEditLink().toASCIIString());
        } else {
          int targetPos = items.get(target);
          targetURI = URI.create("$" + targetPos);
        }

        changes.addLink(delayedUpdate.getType() == ODataLinkType.ENTITY_NAVIGATION
                ? client.getObjectFactory().newEntityNavigationLink(delayedUpdate.getSourceName(), targetURI)
                : client.getObjectFactory().newEntitySetNavigationLink(delayedUpdate.getSourceName(), targetURI));

        LOG.debug("'{}' from {} to {}", delayedUpdate.getType().name(), sourceURI, targetURI);
      }

      batchUpdate(delayedUpdate.getSource(), sourceURI, changes, changeset);
    }
  }

  private class TransactionItems {

    private final List<EntityTypeInvocationHandler<?>> keys = new ArrayList<EntityTypeInvocationHandler<?>>();

    private final List<Integer> values = new ArrayList<Integer>();

    public EntityTypeInvocationHandler<?> get(final Integer value) {
      if (value != null && values.contains(value)) {
        return keys.get(values.indexOf(value));
      } else {
        return null;
      }
    }

    public Integer get(final EntityTypeInvocationHandler<?> key) {
      if (key != null && keys.contains(key)) {
        return values.get(keys.indexOf(key));
      } else {
        return null;
      }
    }

    public void remove(final EntityTypeInvocationHandler<?> key) {
      if (keys.contains(key)) {
        values.remove(keys.indexOf(key));
        keys.remove(key);
      }
    }

    public void put(final EntityTypeInvocationHandler<?> key, final Integer value) {
      // replace just in case of null current value; otherwise add the new entry
      if (key != null && keys.contains(key) && values.get(keys.indexOf(key)) == null) {
        remove(key);
      }
      keys.add(key);
      values.add(value);
    }

    public List<Integer> sortedValues() {
      final List<Integer> sortedValues = new ArrayList<Integer>(values);
      Collections.<Integer>sort(sortedValues);
      return sortedValues;
    }

    public boolean contains(final EntityTypeInvocationHandler<?> key) {
      return keys.contains(key);
    }

    public int size() {
      return keys.size();
    }

    public boolean isEmpty() {
      return keys.isEmpty();
    }
  }
}
