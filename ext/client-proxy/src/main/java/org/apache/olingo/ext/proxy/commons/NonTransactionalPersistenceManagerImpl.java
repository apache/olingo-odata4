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

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.olingo.client.api.communication.request.ODataBasicRequest;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.ODataStreamedRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.ext.proxy.AbstractService;
import org.apache.olingo.ext.proxy.api.ODataFlushException;
import org.apache.olingo.ext.proxy.api.ODataResponseError;

/**
 * {@link org.apache.olingo.ext.proxy.api.PersistenceManager} implementation not using OData batch requests: any
 * read-write operation will be sent separately to the OData service when calling <tt>flush()</tt>.
 */
public class NonTransactionalPersistenceManagerImpl extends AbstractPersistenceManager {

  private static final long serialVersionUID = 5082907388513308752L;

  public NonTransactionalPersistenceManagerImpl(final AbstractService<?> factory) {
    super(factory);
  }

  @Override
  protected void doFlush(final PersistenceChanges changes, final TransactionItems items) {
    final Map<Integer, URI> responses = new HashMap<Integer, URI>();

    int index = 0;
    for (Map.Entry<ODataBatchableRequest, EntityInvocationHandler> entry : changes.getChanges().entrySet()) {
      index++;

      final ODataRequest request = ODataRequest.class.cast(entry.getKey());
      final ODataResponse response;
      try {
        String uri = request.getURI().toASCIIString();
        if (uri.startsWith("$")) {
          int slashIndex = uri.indexOf('/');
          final Integer toBeReplaced = Integer.valueOf(uri.substring(1, slashIndex < 0 ? uri.length() : slashIndex));
          if (responses.containsKey(toBeReplaced)) {
            uri = uri.replace("$" + toBeReplaced, responses.get(toBeReplaced).toASCIIString());
            request.setURI(URI.create(uri));
          }
        }

        if (ODataStreamedRequest.class.isAssignableFrom(request.getClass())) {
          response = ((ODataStreamedRequest<?, ?>) request).payloadManager().getResponse();
        } else {
          response = ((ODataBasicRequest<?>) request).execute();
        }

        if (entry.getValue() != null
            && response instanceof ODataEntityCreateResponse && (response.getStatusCode() == 201 || response
                .getStatusCode() == 204)) {
          if (response.getStatusCode() == 201) {
            entry.getValue().setEntity(((ODataEntityCreateResponse<?>) response).getBody());
            responses.put(index, entry.getValue().getEntityURI());
            LOG.debug("Upgrade created object '{}'", entry.getValue());
          } else {
            entry.getValue().applyChanges();
            responses.put(index, null);
          }
        } else if (entry.getValue() != null
            && response instanceof ODataEntityUpdateResponse && (response.getStatusCode() == 200 || response
                .getStatusCode() == 204)) {
          if (response.getStatusCode() == 200) {
            entry.getValue().setEntity(((ODataEntityUpdateResponse<?>) response).getBody());
            responses.put(index, entry.getValue().getEntityURI());
            LOG.debug("Upgrade updated object '{}'", entry.getValue());
          } else {
            entry.getValue().applyChanges();
            responses.put(index, null);
          }
        } else {
          responses.put(index, null);
        }
      } catch (ODataRuntimeException e) {
        LOG.error("While performing {}", entry.getKey().getURI(), e);

        throw new ODataFlushException(0, Collections.singletonList(new ODataResponseError(e, index, request)));
      }
    }
  }
}
