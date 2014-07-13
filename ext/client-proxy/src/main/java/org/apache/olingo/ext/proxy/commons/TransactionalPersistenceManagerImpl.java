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

import java.util.Iterator;
import java.util.Map;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.ODataStreamedRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.CommonODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.ext.proxy.Service;

/**
 * {@link org.apache.olingo.ext.proxy.api.PersistenceManager} implementation using OData batch requests to implement
 * high-level user transactions: all read-write operations will be packed in a batch request to the OData service when
 * calling <tt>flush()</tt>.
 */
public class TransactionalPersistenceManagerImpl extends AbstractPersistenceManager {

  private static final long serialVersionUID = -3320312269235907501L;

  public TransactionalPersistenceManagerImpl(final Service<?> factory) {
    super(factory);
  }

  /**
   * Transactional changes commit.
   */
  @Override
  protected void doFlush(final PersistenceChanges changes, final TransactionItems items) {
    final CommonODataBatchRequest request =
            factory.getClient().getBatchRequestFactory().getBatchRequest(factory.getClient().getServiceRoot());
    String accept = factory.getClient().getConfiguration().getDefaultBatchAcceptFormat().toContentTypeString();
    ((ODataRequest) request).setAccept(accept);

    final BatchManager streamManager = (BatchManager) ((ODataStreamedRequest) request).payloadManager();

    final ODataChangeset changeset = streamManager.addChangeset();
    for (Map.Entry<ODataBatchableRequest, EntityInvocationHandler> entry : changes.getChanges().entrySet()) {
      changeset.addRequest(entry.getKey());
    }

    final ODataBatchResponse response = streamManager.getResponse();

    // This should be 202 for service version <= 3.0 and 200 for service version >= 4.0 but it seems that
    // many service implementations are not fully compliant in this respect.
    if (response.getStatusCode() != 202 && response.getStatusCode() != 200) {
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

        final EntityInvocationHandler handler = items.get(changesetItemId);

        if (handler != null) {
          if (res instanceof ODataEntityCreateResponse && res.getStatusCode() == 201) {
            handler.setEntity(((ODataEntityCreateResponse) res).getBody());
            LOG.debug("Upgrade created object '{}'", handler);
          } else if (res instanceof ODataEntityUpdateResponse && res.getStatusCode() == 200) {
            handler.setEntity(((ODataEntityUpdateResponse) res).getBody());
            LOG.debug("Upgrade updated object '{}'", handler);
          }
        }
      }
    }
  }
}
