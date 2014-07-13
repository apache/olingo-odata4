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

import java.util.Map;
import org.apache.olingo.client.api.communication.request.ODataBasicRequest;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.ext.proxy.Service;

/**
 * {@link org.apache.olingo.ext.proxy.api.PersistenceManager} implementation not using OData batch requests: any
 * read-write operation will be sent separately to the OData service when calling <tt>flush()</tt>; any intermediate
 * error will be logged and ignored.
 */
public class NonTransactionalPersistenceManagerImpl extends AbstractPersistenceManager {

  private static final long serialVersionUID = 5082907388513308752L;

  public NonTransactionalPersistenceManagerImpl(final Service<?> factory) {
    super(factory);
  }

  @Override
  protected void doFlush(final PersistenceChanges changes, final TransactionItems items) {
    for (Map.Entry<ODataBatchableRequest, EntityInvocationHandler> entry : changes.getChanges().entrySet()) {
      try {
        final ODataResponse response = ((ODataBasicRequest<?>) entry.getKey()).execute();

        if (response instanceof ODataEntityCreateResponse && response.getStatusCode() == 201) {
          entry.getValue().setEntity(((ODataEntityCreateResponse<?>) response).getBody());
          LOG.debug("Upgrade created object '{}'", entry.getValue());
        } else if (response instanceof ODataEntityUpdateResponse && response.getStatusCode() == 200) {
          entry.getValue().setEntity(((ODataEntityUpdateResponse<?>) response).getBody());
          LOG.debug("Upgrade updated object '{}'", entry.getValue());
        }
      } catch (Exception e) {
        LOG.error("While performing {}", entry.getKey().getURI(), e);
      }
    }
  }
}
