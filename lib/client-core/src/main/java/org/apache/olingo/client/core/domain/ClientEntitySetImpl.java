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

import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;

public class ClientEntitySetImpl extends AbstractClientEntitySet implements ClientEntitySet {

  private URI deltaLink;

  private final List<ClientEntity> entities = new ArrayList<ClientEntity>();

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  public ClientEntitySetImpl() {
    super();
  }

  public ClientEntitySetImpl(final URI next) {
    super(next);
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
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
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof ClientEntitySetImpl)) {
      return false;
    }
    ClientEntitySetImpl other = (ClientEntitySetImpl) obj;
    if (annotations == null) {
      if (other.annotations != null) {
        return false;
      }
    } else if (!annotations.equals(other.annotations)) {
      return false;
    }
    if (deltaLink == null) {
      if (other.deltaLink != null) {
        return false;
      }
    } else if (!deltaLink.equals(other.deltaLink)) {
      return false;
    }
    if (entities == null) {
      if (other.entities != null) {
        return false;
      }
    } else if (!entities.equals(other.entities)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientEntitySetImpl [deltaLink=" + deltaLink + ", entities=" + entities + ", annotations=" + annotations
        + "super[" + super.toString() + "]]";
  }
}
