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

import java.util.NoSuchElementException;

import org.apache.commons.io.LineIterator;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;

/**
 * Batch line iterator class.
 */
public class ODataBatchLineIteratorImpl implements ODataBatchLineIterator {

  /**
   * Stream line iterator.
   */
  private final LineIterator batchLineIterator;

  /**
   * Last cached line.
   */
  private String current;

  /**
   * Constructor.
   *
   * @param batchLineIterator stream line iterator.
   */
  public ODataBatchLineIteratorImpl(final LineIterator batchLineIterator) {
    this.batchLineIterator = batchLineIterator;
    this.current = null;
  }

  /**
   * Checks if batch has next line.
   *
   * @return 'TRUE' if has next line; 'FALSE' otherwise.
   */
  @Override
  public boolean hasNext() {
    return batchLineIterator.hasNext();
  }

  /**
   * Gets next line.
   *
   * @return next line.
   */
  @Override
  public String next() {
    if(!hasNext()){
      throw new NoSuchElementException();
    }
    return nextLine();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String nextLine() {
    current = batchLineIterator.nextLine();
    return current;
  }

  /**
   * Unsupported operation.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Unsupported operation");
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public String getCurrent() {
    return current;
  }
}
