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
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URI;
import java.util.Iterator;

/**
 * Data representation for a collection of single entities.
 */
public abstract class EntityIterator extends AbstractEntityCollection implements Iterator<Entity> {

  public abstract boolean hasNext();
  public abstract Entity next();

  @Override
  public void remove() {
    //"Remove is not supported for iteration over Entities."
    throw new ODataNotSupportedException("Entity Iterator does not support remove()");
  }

  @Override
  public Iterator<Entity> iterator() {
    return this;
  }

  public Integer getCount() {
    throw new ODataNotSupportedException("Entity Iterator does not support getCount()");
  }

  public URI getNext() {
    throw new ODataNotSupportedException("Entity Iterator does not support getNext()");
  }

  public URI getDeltaLink() {
    throw new ODataNotSupportedException("Entity Iterator does not support getDeltaLink()");
  }
}
