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

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.core.communication.response.AsyncResponseImpl;
import org.apache.olingo.client.core.communication.response.batch.ODataBatchErrorResponse;

/**
 * Retrieve response wrapper for the corresponding batch item.
 */
public class ODataSingleResponseItem extends AbstractODataBatchResponseItem {

  public static final String SINGLE_CONTENT_ID = "__SINGLE__";

  /**
   * Constructor.
   */
  public ODataSingleResponseItem() {
    super(false);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataResponse next() {
    if (closed) {
      throw new IllegalStateException("Invalid request - the item has been closed");
    }

    if (!hasNext()) {
      throw new NoSuchElementException("No item found");
    }

    final Map.Entry<Integer, String> responseLine = ODataBatchUtilities.readResponseLine(batchLineIterator);
    LOG.debug("Retrieved item response {}", responseLine);

    final Map<String, Collection<String>> headers = ODataBatchUtilities.readHeaders(batchLineIterator);
    LOG.debug("Retrieved item headers {}", headers);

    if (responseLine.getKey() == 202) {
      // generate async response
      current = new AsyncResponseImpl(responseLine, headers, batchLineIterator, boundary);
      breaking = true;
    } else if (responseLine.getKey() >= 400) {
      // generate error response
      current = new ODataBatchErrorResponse(responseLine, headers, batchLineIterator, boundary);
      breaking = true;
    } else {
      if (!hasNext()) {
        throw new NoSuchElementException("No item found");
      }
      current = expectedItemsIterator.next().initFromBatch(responseLine, headers, batchLineIterator, boundary);
    }

    return current;
  }

  /**
   * Unsupported operation.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Operation not supported.");
  }

  @Override
  public void close() {
    super.close();
    if (current != null && !(current instanceof AsyncResponseImpl) && !(current instanceof ODataBatchErrorResponse)) {
      current.close();
    }
  }

}
