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
package org.apache.olingo.client.api.communication.request.batch;

import java.util.Iterator;

import org.apache.olingo.client.api.communication.response.ODataResponse;

/**
 * Abstract representation of a response item about a batch request.
 */
public interface ODataBatchResponseItem extends Iterator<ODataResponse> {

  /**
   * Adds the given OData response template to the current OData batch response item.
   *
   * @param contentId changeset contentId in case of changeset; '__RETRIEVE__' in case of retrieve item.
   * @param res OData response template to be added.
   */
  void addResponse(final String contentId, final ODataResponse res);

  /**
   * Initializes ODataResponse template from batch response item part.
   *
   * @param batchLineIterator batch response line iterator.
   * @param boundary batch response boundary.
   */
  void initFromBatch(final ODataBatchLineIterator batchLineIterator, final String boundary);

  /**
   * Checks if the current batch response item is a changeset.
   *
   * @return 'TRUE' if the item is a changeset; 'FALSE' otherwise.
   */
  boolean isChangeset();

  /**
   * Checks if the current item is a breaking item like as error item or asynchronous response part.
   *
   * @return 'TRUE' if breaking; 'FALSE' otherwise.
   */
  boolean isBreaking();

  /**
   * Closes the current batch responses item including all wrapped OData responses.
   */
  void close();
}
