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

import org.apache.olingo.client.api.domain.ClientDeletedEntity;
import org.apache.olingo.client.api.domain.ClientItem;

public class ClientDeletedEntityImpl extends ClientItem implements ClientDeletedEntity {

  private URI id;

  private Reason reason;

  public ClientDeletedEntityImpl() {
    super(null);
  }

  @Override
  public URI getId() {
    return id;
  }

  public void setId(final URI id) {
    this.id = id;
  }

  @Override
  public Reason getReason() {
    return reason;
  }

  public void setReason(final Reason reason) {
    this.reason = reason;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
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
    if (!(obj instanceof ClientDeletedEntityImpl)) {
      return false;
    }
    ClientDeletedEntityImpl other = (ClientDeletedEntityImpl) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (reason != other.reason) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientDeletedEntityImpl [id=" + id + ", reason=" + reason + "super[" + super.toString() + "]]";
  }

}
