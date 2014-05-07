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
package org.apache.olingo.client.api;

import org.apache.olingo.client.api.communication.request.cud.CommonUpdateType;
import org.apache.olingo.commons.api.edm.Edm;

/**
 * Client interface that caches the Edm metadata information for a given service root.
 * <br/>
 * Be aware that any request generated via this client instance will be performed against the given service root.
 *
 * @param <UT> concrete update type, depending on the protocol version
 */
public interface CommonEdmEnabledODataClient<UT extends CommonUpdateType> extends CommonODataClient<UT> {

  String getServiceRoot();

  /**
   * Checks if the cached Edm matadata information matches the argument and, if not, updates the cache against the
   * configured service root.
   *
   * @param metadataETag metadata ETag to be compared against the cache
   * @return Edm
   */
  Edm getEdm(String metadataETag);

  /**
   * Return the cached Edm matadata information.
   *
   * @return Edm
   */
  Edm getCachedEdm();
}
