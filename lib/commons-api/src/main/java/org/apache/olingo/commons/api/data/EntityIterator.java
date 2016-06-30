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
package org.apache.olingo.commons.api.data;

import org.apache.olingo.commons.api.ex.ODataNotSupportedException;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

/**
 * Data representation as an Iterator for a collection of single entities.
 */
public abstract class EntityIterator extends AbstractEntityCollection implements Iterator<Entity> {

  /**
   * {@inheritDoc}
   */
  public abstract boolean hasNext();
  /**
   * {@inheritDoc}
   * <p/>
   * Which is an Entity for this type of iterator.
   */
  public abstract Entity next();

  /**
   * {@inheritDoc}
   * <p/>
   * <b>ATTENTION:</b> <code>remove</code> is not supported by default.
   */
  @Override
  public void remove() {
    //"Remove is not supported for iteration over Entities."
    throw new ODataNotSupportedException("Entity Iterator does not support remove()");
  }

  /**
   * {@inheritDoc}
   * <p/>
   * <b>ATTENTION:</b> <code>getOperations</code> is not supported by default.
   */
  @Override
  public List<Operation> getOperations() {
    //"Remove is not supported for iteration over Entities."
    throw new ODataNotSupportedException("Entity Iterator does not support getOperations() by default");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<Entity> iterator() {
    return this;
  }

  /**
   * {@inheritDoc}
   * <p/>
   * <b>ATTENTION:</b> <code>getCount</code> is not supported by default.
   */
  public Integer getCount() {
    throw new ODataNotSupportedException("Entity Iterator does not support getCount()");
  }

  /**
   * {@inheritDoc}
   * <p/>
   * <b>ATTENTION:</b> <code>getNext</code> is not supported by default.
   */
  public URI getNext() {
    throw new ODataNotSupportedException("Entity Iterator does not support getNext()");
  }

  /**
   * {@inheritDoc}
   * <p/>
   * <b>ATTENTION:</b> <code>getDeltaLink</code> is not supported by default.
   */
  public URI getDeltaLink() {
    throw new ODataNotSupportedException("Entity Iterator does not support getDeltaLink()");
  }
}
