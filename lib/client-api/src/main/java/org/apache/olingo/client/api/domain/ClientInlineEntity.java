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
 * OData in-line entity.
 */
public class ClientInlineEntity extends ClientLink {

  private final ClientEntity entity;

  /**
   * Constructor.
   * 
   * @param uri edit link.
   * @param type type.
   * @param title title.
   * @param entity entity.
   */
  public ClientInlineEntity(final URI uri, final ClientLinkType type, final String title, final ClientEntity entity) {

    super(uri, type, title);
    this.entity = entity;
  }

  /**
   * Constructor.
   * 
   * @param baseURI base URI.
   * @param href href.
   * @param type type.
   * @param title title.
   * @param entity entity.
   */
  public ClientInlineEntity(final URI baseURI, final String href, final ClientLinkType type, final String title,
                            final ClientEntity entity) {

    super(baseURI, href, type, title);
    this.entity = entity;
  }

  /**
   * Gets wrapped entity.
   * 
   * @return wrapped entity.
   */
  public ClientEntity getEntity() {
    return entity;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
    if (!(obj instanceof ClientInlineEntity)) {
      return false;
    }
    ClientInlineEntity other = (ClientInlineEntity) obj;
    if (entity == null) {
      if (other.entity != null) {
        return false;
      }
    } else if (!entity.equals(other.entity)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ClientInlineEntity [entity=" + entity + "super[" + super.toString() + "]]";
  }
}
