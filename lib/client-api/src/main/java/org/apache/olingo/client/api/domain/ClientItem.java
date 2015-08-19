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
 * Abstract representation of OData entities and links.
 */
public abstract class ClientItem {

  /**
   * OData entity name/type.
   */
  private final String name;

  /**
   * OData item self link.
   */
  private URI link;

  /**
   * Constructor.
   * 
   * @param name ODataItem name (it's entity type for {@link ClientEntity}).
   */
  public ClientItem(final String name) {
    this.name = name;
  }

  /**
   * @return ODataItem name (it's entity type for {@link ClientEntity}).
   */
  public String getName() {
    return name;
  }

  /**
   * @return ODataItem link (it's edit link for {@link ClientEntity}).
   */
  public URI getLink() {
    return link;
  }

  /**
   * Sets ODataItem link (it's edit link for {@link ClientEntity}).
   * 
   * @param link link.
   */
  public void setLink(final URI link) {
    this.link = link;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ClientItem)) {
      return false;
    }
    ClientItem other = (ClientItem) obj;
    if (link == null) {
      if (other.link != null) {
        return false;
      }
    } else if (!link.equals(other.link)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((link == null) ? 0 : link.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "ClientItem [name=" + name + ", link=" + link + "]";
  }
}
