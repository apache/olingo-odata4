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
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.domain.AbstractClientPayload;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientOperation;

public class ClientEntitySetImpl extends AbstractClientPayload implements ClientEntitySet {

  /**
   * Link to the next page.
   */
  private final URI next;

  /**
   * Number of ODataEntities contained in this entity set.
   * <br/>
   * If <tt>$count</tt> was requested, this value comes from there.
   */
  private Integer count;

  private URI deltaLink;

  private final List<ClientEntity> entities = new ArrayList<ClientEntity>();

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();
  
  private final List<ClientOperation> operations = new ArrayList<ClientOperation>();

  public ClientEntitySetImpl() {
    super(null);
    next = null;
  }

  public ClientEntitySetImpl(final URI next) {
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
  public List<ClientEntity> getEntities() {
    return entities;
  }

  @Override
  public URI getDeltaLink() {
    return deltaLink;
  }

  @Override
  public void setDeltaLink(final URI deltaLink) {
    this.deltaLink = deltaLink;
  }

  @Override
  public List<ClientAnnotation> getAnnotations() {
    return annotations;
  }

  @Override
  public ClientOperation getOperation(final String title) {
    ClientOperation result = null;
    for (ClientOperation operation : operations) {
      if (title.equals(operation.getTitle())) {
        result = operation;
        break;
      }
    }

    return result;
  }

  /**
   * Gets operations.
   *
   * @return operations.
   */
  @Override
  public List<ClientOperation> getOperations() {
    return operations;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((count == null) ? 0 : count.hashCode());
    result = prime * result + ((next == null) ? 0 : next.hashCode());
    result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
    result = prime * result + ((deltaLink == null) ? 0 : deltaLink.hashCode());
    result = prime * result + ((entities == null) ? 0 : entities.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !(obj instanceof ClientEntitySetImpl)) {
      return false;
    }
    final ClientEntitySetImpl other = (ClientEntitySetImpl) obj;
    return (count == null ? other.count == null : count.equals(other.count))
        && (next == null ? other.next == null : next.equals(other.next))
        && annotations.equals(other.annotations)
        && (deltaLink == null ? other.deltaLink == null : deltaLink.equals(other.deltaLink))
        && entities.equals(other.entities);
  }

  @Override
  public String toString() {
    return "ClientEntitySetImpl [deltaLink=" + deltaLink + ", entities=" + entities + ", annotations=" + annotations
        + ", next=" + next + ", count=" + count + "super[" + super.toString() + "]]";
  }
}
