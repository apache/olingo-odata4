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
package org.apache.olingo.client.core.communication.request.batch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of a response item about a batch request.
 */
public abstract class AbstractODataBatchResponseItem implements ODataBatchResponseItem {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(ODataBatchResponseItem.class);

  /**
   * Expected OData responses for the current batch response item.
   */
  protected final Map<String, ODataResponse> responses = new HashMap<String, ODataResponse>();

  /**
   * Expected OData responses iterator.
   */
  protected Iterator<ODataResponse> expectedItemsIterator;

  /**
   * Changeset controller. Gives more information about the type of batch item.
   */
  private final boolean changeset;

  /**
   * Batch response line iterator.
   */
  protected ODataBatchLineIterator batchLineIterator;

  /**
   * Batch boundary.
   */
  protected String boundary;

  /**
   * Gives information about the batch response item status.
   */
  protected boolean closed = false;

  /**
   * Last cached OData response.
   */
  protected ODataResponse current;

  protected boolean breaking = false;

  /**
   * Constructor.
   *
   * @param isChangeset 'TRUE' if the current batch response item is a changeset.
   */
  public AbstractODataBatchResponseItem(boolean isChangeset) {
    this.changeset = isChangeset;
    this.current = null;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void addResponse(final String contentId, final ODataResponse res) {
    if (closed) {
      throw new IllegalStateException("Invalid batch item because explicitely closed");
    }
    responses.put(contentId, res);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void initFromBatch(final ODataBatchLineIterator batchLineIterator, final String boundary) {
    if (closed) {
      throw new IllegalStateException("Invalid batch item because explicitely closed");
    }
    LOG.debug("Init from batch - boundary '{}'", boundary);
    this.batchLineIterator = batchLineIterator;
    this.boundary = boundary;
  }

  /**
   * Gets response about the given contentId.
   *
   * @param contentId response identifier (a specific contentId in case of changeset item).
   * @return ODataResponse corresponding to the given contentId.
   */
  protected ODataResponse getResponse(final String contentId) {
    if (closed) {
      throw new IllegalStateException("Invalid batch item because explicitely closed");
    }
    return responses.get(contentId);
  }

  /**
   * Gets OData responses iterator.
   *
   * @return OData responses iterator.
   */
  protected Iterator<ODataResponse> getResponseIterator() {
    if (closed) {
      throw new IllegalStateException("Invalid batch item because explicitely closed");
    }
    return responses.values().iterator();
  }

  @Override
  public boolean hasNext() {
    if (closed) {
      throw new IllegalStateException("Invalid request - the item has been closed");
    }

    if (expectedItemsIterator == null) {
      expectedItemsIterator = responses.values().iterator();
    }

    return !breaking && expectedItemsIterator.hasNext();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public boolean isBreaking() {
    return breaking;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public final boolean isChangeset() {
    return changeset;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public void close() {
    for (ODataResponse response : responses.values()) {
      response.close();
    }
    closed = true;
  }
}
