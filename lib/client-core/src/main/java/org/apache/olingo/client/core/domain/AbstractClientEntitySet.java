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
package org.apache.olingo.client.core.domain;

import java.net.URI;

import org.apache.olingo.client.api.domain.AbstractClientPayload;
import org.apache.olingo.client.api.domain.ClientEntitySet;

public abstract class AbstractClientEntitySet extends AbstractClientPayload implements ClientEntitySet {

  /**
   * Link to the next page.
   */
  private URI next;

  /**
   * Number of ODataEntities contained in this entity set.
   * <br/>
   * If <tt>$inlinecount</tt> was requested, this value comes from there.
   */
  private Integer count;

  /**
   * Constructor.
   */
  public AbstractClientEntitySet() {
    super(null);
  }

  /**
   * Constructor.
   * 
   * @param next next link.
   */
  public AbstractClientEntitySet(final URI next) {
    super(null);
    this.next = next;
  }

  @Override
  public URI getNext() {
    return next;
  }

  @Override
  public Integer getCount() {
    return count;
  }

  @Override
  public void setCount(final int count) {
    this.count = count;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((count == null) ? 0 : count.hashCode());
    result = prime * result + ((next == null) ? 0 : next.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof AbstractClientEntitySet)) {
      return false;
    }
    AbstractClientEntitySet other = (AbstractClientEntitySet) obj;
    if (count == null) {
      if (other.count != null) {
        return false;
      }
    } else if (!count.equals(other.count)) {
      return false;
    }
    if (next == null) {
      if (other.next != null) {
        return false;
      }
    } else if (!next.equals(other.next)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "AbstractClientEntitySet [next=" + next + ", count=" + count + "super[" + super.toString() + "]]";
  }
}
