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

import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class PersistenceChanges {

  private final Map<ODataBatchableRequest, EntityInvocationHandler> requests =
          new LinkedHashMap<ODataBatchableRequest, EntityInvocationHandler>();

  public void addChange(final ODataBatchableRequest request, final EntityInvocationHandler handler) {
    this.requests.put(request, handler);
  }

  public Map<ODataBatchableRequest, EntityInvocationHandler> getChanges() {
    return requests;
  }
}
