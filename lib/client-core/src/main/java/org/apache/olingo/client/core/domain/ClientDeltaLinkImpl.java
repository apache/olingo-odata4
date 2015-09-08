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
import org.apache.olingo.client.api.domain.ClientDeltaLink;
import org.apache.olingo.client.api.domain.ClientItem;

public class ClientDeltaLinkImpl extends ClientItem implements ClientDeltaLink {

  private URI source;

  private String relationship;

  private URI target;

  private final List<ClientAnnotation> annotations = new ArrayList<ClientAnnotation>();

  public ClientDeltaLinkImpl() {
    super(null);
  }

  @Override
  public URI getSource() {
    return source;
  }

  @Override
  public void setSource(final URI source) {
    this.source = source;
  }

  @Override
  public String getRelationship() {
    return relationship;
  }

  @Override
  public void setRelationship(final String relationship) {
    this.relationship = relationship;
  }

  @Override
  public URI getTarget() {
    return target;
  }

  @Override
  public void setTarget(final URI target) {
    this.target = target;
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
    result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result = prime * result + ((target == null) ? 0 : target.hashCode());
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
    if (!(obj instanceof ClientDeltaLinkImpl)) {
      return false;
    }
    ClientDeltaLinkImpl other = (ClientDeltaLinkImpl) obj;
    if (annotations == null) {
      if (other.annotations != null) {
        return false;
      }
    } else if (!annotations.equals(other.annotations)) {
      return false;
    }
    if (relationship == null) {
      if (other.relationship != null) {
        return false;
      }
    } else if (!relationship.equals(other.relationship)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (target == null) {
      if (other.target != null) {
        return false;
      }
    } else if (!target.equals(other.target)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientDeltaLinkImpl [source=" + source + ", relationship=" + relationship + ", target=" + target
        + ", annotations=" + annotations + "super[" + super.toString() + "]]";
  }

}
