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

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;

class EntitySetIterator<T extends EntityType<?>, KEY extends Serializable, EC extends EntityCollection<T, ?, ?>>
        implements Iterator<T> {

  private final EntitySetInvocationHandler<T, KEY, EC> esi;

  private URI next;

  private Iterator<T> current;

  EntitySetIterator(final URI uri, EntitySetInvocationHandler<T, KEY, EC> esi) {
    this.esi = esi;
    this.next = uri;
    this.current = Collections.<T>emptyList().iterator();
  }

  @Override
  public boolean hasNext() {
    final boolean res;
    if (this.current.hasNext()) {
      res = true;
    } else if (this.next == null) {
      res = false;
    } else {
      goOn();
      res = current.hasNext();
    }
    return res;
  }

  @Override
  public T next() {
    T res;
    try {
      res = this.current.next();
    } catch (NoSuchElementException e) {
      if (this.next == null) {
        throw e;
      }
      goOn();
      res = next();
    }

    return res;
  }

  @Override
  public void remove() {
    this.current.remove();
  }

  private void goOn() {
    final Triple<List<T>, URI, List<ClientAnnotation>> entitySet = esi.fetchPartial(this.next, this.esi.getTypeRef());
    this.current = entitySet.getLeft().iterator();
    this.next = entitySet.getMiddle();
  }
}
