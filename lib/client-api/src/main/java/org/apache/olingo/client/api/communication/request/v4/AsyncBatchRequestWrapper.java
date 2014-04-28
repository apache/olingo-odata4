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
package org.apache.olingo.client.api.communication.request.v4;

import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.batch.ODataRetrieve;
import org.apache.olingo.client.api.communication.request.batch.v4.ODataOutsideUpdate;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;

public interface AsyncBatchRequestWrapper extends AsyncRequestWrapper<ODataBatchResponse> {

  /**
   * Gets a changeset batch item instance. A changeset can be submitted embedded into a batch request only.
   *
   * @return ODataChangeset instance.
   */
  ODataChangeset addChangeset();

  /**
   * Gets a retrieve batch item instance. A retrieve item can be submitted embedded into a batch request only.
   *
   * @return ODataRetrieve instance.
   */
  ODataRetrieve addRetrieve();

  /**
   * Gets an outside change batch item instance. An outside item can be submitted embedded into a batch request only.
   *
   * @return ODataOutsideUpdate instance.
   */
  ODataOutsideUpdate addOutsideUpdate();
}
