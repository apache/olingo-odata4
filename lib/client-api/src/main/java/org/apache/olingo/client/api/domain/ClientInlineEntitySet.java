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
 * OData in-line entity set.
 */
public class ClientInlineEntitySet extends ClientLink {

  private ClientEntitySet entitySet;

  /**
   * Constructor.
   * 
   * @param uri edit link.
   * @param type type.
   * @param title title.
   * @param entitySet entity set.
   */
  public ClientInlineEntitySet(final URI uri, final ClientLinkType type,
      final String title, final ClientEntitySet entitySet) {

    super(uri, type, title);
    this.entitySet = entitySet;
  }

  /**
   * Constructor.
   * 
   * @param baseURI base URI.
   * @param href href.
   * @param type type.
   * @param title title.
   * @param entitySet entity set.
   */
  public ClientInlineEntitySet(final URI baseURI, final String href,
      final ClientLinkType type, final String title, final ClientEntitySet entitySet) {

    super(baseURI, href, type, title);
    this.entitySet = entitySet;
  }

  /**
   * Gets wrapped entity set.
   * 
   * @return wrapped entity set.
   */
  public ClientEntitySet getEntitySet() {
    return entitySet;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((entitySet == null) ? 0 : entitySet.hashCode());
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
    if (!(obj instanceof ClientInlineEntitySet)) {
      return false;
    }
    ClientInlineEntitySet other = (ClientInlineEntitySet) obj;
    if (entitySet == null) {
      if (other.entitySet != null) {
        return false;
      }
    } else if (!entitySet.equals(other.entitySet)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientInlineEntitySet [entitySet=" + entitySet + "super[" + super.toString() + "]]";
  }

}
