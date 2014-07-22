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
package org.apache.olingo.client.core.communication.request.cud.v4;

import java.net.URI;

import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.communication.request.cud.AbstractCUDRequestFactory;
import org.apache.olingo.commons.api.domain.v4.ODataSingleton;

public class CUDRequestFactoryImpl extends AbstractCUDRequestFactory<UpdateType> implements CUDRequestFactory {

  public CUDRequestFactoryImpl(final ODataClient client) {
    super(client);
  }

  @Override
  public ODataEntityUpdateRequest<ODataSingleton> getSingletonUpdateRequest(
          final UpdateType type, final ODataSingleton entity) {

    return super.getEntityUpdateRequest(type, entity);
  }

  @Override
  public ODataEntityUpdateRequest<ODataSingleton> getSingletonUpdateRequest(
          final URI targetURI, final UpdateType type, final ODataSingleton changes) {

    return super.getEntityUpdateRequest(targetURI, type, changes);
  }

}
