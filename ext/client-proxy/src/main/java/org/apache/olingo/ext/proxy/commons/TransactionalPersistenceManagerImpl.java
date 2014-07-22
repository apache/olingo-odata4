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
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
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
import org.apache.olingo.client.core.communication.header.ODataErrorResponseChecker;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.ext.proxy.AbstractService;

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
  protected List<ODataRuntimeException> doFlush(final PersistenceChanges changes, final TransactionItems items) {
    final CommonODataBatchRequest request =
            service.getClient().getBatchRequestFactory().getBatchRequest(service.getClient().getServiceRoot());
    ((ODataRequest) request).setAccept(
            service.getClient().getConfiguration().getDefaultBatchAcceptFormat().toContentTypeString());

    final BatchManager batchManager = (BatchManager) ((ODataStreamedRequest) request).payloadManager();

    final ODataChangeset changeset = batchManager.addChangeset();
    for (Map.Entry<ODataBatchableRequest, EntityInvocationHandler> entry : changes.getChanges().entrySet()) {
      changeset.addRequest(entry.getKey());
    }

    final ODataBatchResponse response = batchManager.getResponse();

    // This should be 202 for service version <= 3.0 and 200 for service version >= 4.0 but it seems that
    // many service implementations are not fully compliant in this respect.
    if (response.getStatusCode() != 202 && response.getStatusCode() != 200) {
      throw new IllegalStateException("Operation failed");
    }

    final List<ODataRuntimeException> result = new ArrayList<ODataRuntimeException>();

    if (!items.isEmpty()) {
      final Iterator<ODataBatchResponseItem> batchResItor = response.getBody();
      if (!batchResItor.hasNext()) {
        throw new IllegalStateException("Unexpected operation result");
      }

      final ODataBatchResponseItem item = batchResItor.next();
      if (!(item instanceof ODataChangesetResponseItem)) {
        throw new IllegalStateException("Unexpected batch response item " + item.getClass().getSimpleName());
      }

      final ODataChangesetResponseItem chgres = (ODataChangesetResponseItem) item;

      for (final Iterator<Integer> itor = items.sortedValues().iterator(); itor.hasNext();) {
        final Integer changesetItemId = itor.next();
        LOG.debug("Expected changeset item {}", changesetItemId);

        final ODataResponse res = chgres.next();
        if (res.getStatusCode() >= 400) {
          if (service.getClient().getConfiguration().isContinueOnError()) {
            result.add(ODataErrorResponseChecker.checkResponse(
                    service.getClient(),
                    new StatusLine() {
                      @Override
                      public ProtocolVersion getProtocolVersion() {
                        return null;
                      }

                      @Override
                      public int getStatusCode() {
                        return res.getStatusCode();
                      }

                      @Override
                      public String getReasonPhrase() {
                        return res.getStatusMessage();
                      }
                    },
                    res.getRawResponse(),
                    ((ODataRequest) request).getAccept()));
          } else {
            throw new IllegalStateException("Transaction failed: " + res.getStatusMessage());
          }
        } else {
          result.add(null);
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
    response.close();

    return result;
  }
}
