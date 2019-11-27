/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request.batch;

import java.nio.charset.Charset;

import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequestItem;
import org.apache.olingo.client.core.communication.util.PipedOutputStream;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamer;

/**
 * Abstract representation of a batch request item.
 */
public abstract class AbstractODataBatchRequestItem extends AbstractODataStreamer
    implements ODataBatchRequestItem {

  /**
   * Stream started check.
   */
  protected boolean hasStreamedSomething = false;
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  /**
   * Stream open check.
   */
  private boolean open = false;

  /**
   * OData batch request.
   */
  protected ODataBatchRequest req;

  /**
   * Constructor.
   *
   * @param req OData batch request.
   */
  public AbstractODataBatchRequestItem(final ODataBatchRequest req) {
    super((PipedOutputStream) req.getOutputStream());
    this.open = true;
    this.req = req;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isOpen() {
    return open;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    closeItem();
    open = false;
  }

  /**
   * Stream the given request header.
   * <p>
   * Use this method to stream changeset items.
   *
   * @param contentId changeset item id.
   */
  protected void streamRequestHeader(final String contentId) {
    // stream batch content type
    stream(ODataBatchConstants.ITEM_CONTENT_TYPE_LINE.getBytes(DEFAULT_CHARSET));
    newLine();
    stream(ODataBatchConstants.ITEM_TRANSFER_ENCODING_LINE.getBytes(DEFAULT_CHARSET));
    newLine();
    stream((ODataBatchConstants.CHANGESET_CONTENT_ID_NAME + ":" + contentId).getBytes(DEFAULT_CHARSET));
    newLine();
    newLine();
  }

  /**
   * Stream the given request header.
   *
   * @param request request to be batched.
   */
  protected void streamRequestHeader(final ODataBatchableRequest request) {
    // stream batch content type
    stream(ODataBatchConstants.ITEM_CONTENT_TYPE_LINE.getBytes(DEFAULT_CHARSET));
    newLine();
    stream(ODataBatchConstants.ITEM_TRANSFER_ENCODING_LINE.getBytes(DEFAULT_CHARSET));
    newLine();
    newLine();

    stream(request.toByteArray());
    newLine();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasStreamedSomething() {
    return hasStreamedSomething;
  }

  /**
   * Closes the current item.
   */
  protected abstract void closeItem();
}
