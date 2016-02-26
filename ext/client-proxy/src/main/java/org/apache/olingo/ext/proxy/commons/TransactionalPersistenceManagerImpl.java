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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.ODataStreamedRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.core.communication.header.ODataErrorResponseChecker;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.ODataFlushException;
import org.apache.olingo.ext.proxy.api.ODataResponseError;

/**
 * {@link org.apache.olingo.ext.proxy.api.PersistenceManager} implementation using OData batch requests to implement
 * high-level user transactions: all read-write operations will be packed in a batch request to the OData service when
 * calling <tt>flush()</tt>.
 */
public class TransactionalPersistenceManagerImpl extends AbstractPersistenceManager {

  private static final long serialVersionUID = -3320312269235907501L;

  public TransactionalPersistenceManagerImpl(final AbstractService<?> factory) {
    super(factory);
  }

  /**
   * Transactional changes commit.
   */
  @Override
  protected void doFlush(final PersistenceChanges changes, final TransactionItems items) {
    final ODataBatchRequest request =
            service.getClient().getBatchRequestFactory().getBatchRequest(service.getClient().getServiceRoot());
    ((ODataRequest) request).setAccept(
            service.getClient().getConfiguration().getDefaultBatchAcceptFormat().toContentTypeString());

    final BatchManager batchManager = (BatchManager) ((ODataStreamedRequest<?,?>) request).payloadManager();

    final List<ODataRequest> requests = new ArrayList<ODataRequest>(changes.getChanges().size());
    final ODataChangeset changeset = batchManager.addChangeset();
    for (Map.Entry<ODataBatchableRequest, EntityInvocationHandler> entry : changes.getChanges().entrySet()) {
      changeset.addRequest(entry.getKey());
      requests.add(entry.getKey());
    }

    final ODataBatchResponse response = batchManager.getResponse();

    // This should be 202 for service version <= 3.0 and 200 for service version >= 4.0 but it seems that
    // many service implementations are not fully compliant in this respect.
    if (response.getStatusCode() != 202 && response.getStatusCode() != 200) {
      throw new ODataServerErrorException(new ResponseStatusLine(response));
    }

    if (!items.isEmpty()) {
      final List<ODataResponseError> errors = new ArrayList<ODataResponseError>();

      final Iterator<ODataBatchResponseItem> batchResItor = response.getBody();
      if (!batchResItor.hasNext()) {
        throw new IllegalStateException("Unexpected operation result");
      }

      final ODataBatchResponseItem item = batchResItor.next();
      if (!(item instanceof ODataChangesetResponseItem)) {
        throw new IllegalStateException("Unexpected batch response item " + item.getClass().getSimpleName());
      }

      final ODataChangesetResponseItem chgres = (ODataChangesetResponseItem) item;

      int index = 0;
      for (final Iterator<Integer> itor = items.sortedValues().iterator(); itor.hasNext(); index++) {
        final Integer changesetItemId = itor.next();
        LOG.debug("Expected changeset item {}", changesetItemId);

        final ODataResponse res = chgres.next();
        if (res.getStatusCode() >= 400) {
          errors.add(new ODataResponseError(ODataErrorResponseChecker.checkResponse(
                  service.getClient(),
                  new ResponseStatusLine(res),
                  res.getRawResponse(),
                  ((ODataRequest) request).getAccept()), index, requests.get(index)));
          if (!service.getClient().getConfiguration().isContinueOnError()) {
            throw new ODataFlushException(response.getStatusCode(), errors);
          }
        }

        final EntityInvocationHandler handler = items.get(changesetItemId);

        if (handler != null) {
          if (res instanceof ODataEntityCreateResponse && (res.getStatusCode() == 201 || res
              .getStatusCode() == 204)) {
            if (res.getStatusCode() == 201) {
              handler.setEntity(((ODataEntityCreateResponse<?>) res).getBody());
              LOG.debug("Upgrade created object '{}'", handler);
            } else {
              handler.applyChanges();
            }
          } else if (res instanceof ODataEntityUpdateResponse && (res.getStatusCode() == 200 || res
              .getStatusCode() == 204)) {
            if (res.getStatusCode() == 201) {
              handler.setEntity(((ODataEntityUpdateResponse<?>) res).getBody());
              LOG.debug("Upgrade updated object '{}'", handler);
            } else {
              handler.applyChanges();
            }
          }
        }
      }

      if (!errors.isEmpty()) {
        throw new ODataFlushException(response.getStatusCode(), errors);
      }
    }
    response.close();
  }
}
