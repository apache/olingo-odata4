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

import org.apache.olingo.client.api.domain.ClientDeletedEntity;
import org.apache.olingo.client.api.domain.ClientDelta;
import org.apache.olingo.client.api.domain.ClientDeltaLink;

public class ClientDeltaImpl extends ClientEntitySetImpl implements ClientDelta {

  private final List<ClientDeletedEntity> deletedEntities = new ArrayList<ClientDeletedEntity>();

  private final List<ClientDeltaLink> addedLinks = new ArrayList<ClientDeltaLink>();

  private final List<ClientDeltaLink> deletedLinks = new ArrayList<ClientDeltaLink>();

  public ClientDeltaImpl() {
    super();
  }

  public ClientDeltaImpl(final URI next) {
    super(next);
  }

  @Override
  public List<ClientDeletedEntity> getDeletedEntities() {
    return deletedEntities;
  }

  @Override
  public List<ClientDeltaLink> getAddedLinks() {
    return addedLinks;
  }

  @Override
  public List<ClientDeltaLink> getDeletedLinks() {
    return deletedLinks;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (addedLinks.hashCode());
    result = prime * result + (deletedEntities.hashCode());
    result = prime * result + (deletedLinks.hashCode());
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
    if (!(obj instanceof ClientDeltaImpl)) {
      return false;
    }
    ClientDeltaImpl other = (ClientDeltaImpl) obj;
    if (addedLinks == null) {
      if (other.addedLinks != null) {
        return false;
      }
    } else if (!addedLinks.equals(other.addedLinks)) {
      return false;
    }
    if (deletedEntities == null) {
      if (other.deletedEntities != null) {
        return false;
      }
    } else if (!deletedEntities.equals(other.deletedEntities)) {
      return false;
    }
    if (deletedLinks == null) {
      if (other.deletedLinks != null) {
        return false;
      }
    } else if (!deletedLinks.equals(other.deletedLinks)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientDeltaImpl [deletedEntities=" + deletedEntities + ", addedLinks=" + addedLinks + ", deletedLinks="
        + deletedLinks + "super[" + super.toString() + "]]";
  }
}
