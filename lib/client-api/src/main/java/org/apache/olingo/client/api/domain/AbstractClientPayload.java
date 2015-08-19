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
package org.apache.olingo.client.api.domain;

import java.net.URI;

/**
 * OData entity.
 */
public abstract class AbstractClientPayload extends ClientItem {

  /**
   * Context URL.
   */
  private URI contextURL;

  public AbstractClientPayload(final String name) {
    super(name);
  }

  /**
   * The context URL describes the content of the payload. It consists of the canonical metadata document URL and a
   * fragment identifying the relevant portion of the metadata document.
   * 
   * @return context URL.
   */
  public URI getContextURL() {
    return contextURL;
  }

  public void setContextURL(final URI contextURL) {
    this.contextURL = contextURL;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((contextURL == null) ? 0 : contextURL.hashCode());
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
    if (!(obj instanceof AbstractClientPayload)) {
      return false;
    }
    AbstractClientPayload other = (AbstractClientPayload) obj;
    if (contextURL == null) {
      if (other.contextURL != null) {
        return false;
      }
    } else if (!contextURL.equals(other.contextURL)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "AbstractClientPayload [contextURL=" + contextURL + "super[" + super.toString() + "]]";
  }
}
