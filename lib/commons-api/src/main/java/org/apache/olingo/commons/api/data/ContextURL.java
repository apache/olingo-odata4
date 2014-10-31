/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.api.data;

import java.net.URI;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmType;

/**
 * High-level representation of a context URL, built from the string value returned by a service; provides access to the
 * various components of the context URL, defined in the <a
 * href="http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793655">
 * protocol specification</a>.
 */
public class ContextURL {

  private URI serviceRoot;

  private String entitySetOrSingletonOrType;

  private boolean isCollection = false;

  private String derivedEntity;

  private String selectList;

  private String navOrPropertyPath;
  
  private String keyPath;

  private Suffix suffix;

  public enum Suffix {

    ENTITY("$entity"), REFERENCE("$ref"),
    DELTA("$delta"), DELTA_DELETED_ENTITY("$deletedEntity"), DELTA_LINK("$link"), DELTA_DELETED_LINK("$deletedLink");

    private final String representation;

    private Suffix(final String representation) {
      this.representation = representation;
    }

    public String getRepresentation() {
      return representation;
    }
  }


  private ContextURL() {
  }

  public URI getServiceRoot() {
    return serviceRoot;
  }

  public String getEntitySetOrSingletonOrType() {
    return entitySetOrSingletonOrType;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public String getDerivedEntity() {
    return derivedEntity;
  }

  public String getSelectList() {
    return selectList;
  }

  public String getNavOrPropertyPath() {
    return navOrPropertyPath;
  }
  
  public String getKeyPath() {
    return keyPath;
  }  

  public Suffix getSuffix() {
    return suffix;
  }

  public boolean isEntity() {
    return suffix == Suffix.ENTITY;
  }

  public boolean isReference() {
    return suffix == Suffix.REFERENCE;
  }

  public boolean isDelta() {
    return suffix == Suffix.DELTA;
  }

  public boolean isDeltaDeletedEntity() {
    return suffix == Suffix.DELTA_DELETED_ENTITY;
  }

  public boolean isDeltaLink() {
    return suffix == Suffix.DELTA_LINK;
  }

  public boolean isDeltaDeletedLink() {
    return suffix == Suffix.DELTA_DELETED_LINK;
  }

  public static Builder with() {
    return new Builder();
  }

  public static final class Builder {

    private ContextURL contextURL = new ContextURL();

    public Builder serviceRoot(final URI serviceRoot) {
      contextURL.serviceRoot = serviceRoot;
      return this;
    }

    public Builder entitySet(final EdmEntitySet entitySet) {
      contextURL.entitySetOrSingletonOrType = entitySet.getName();
      return this;
    }
    
    public Builder keyPath(final String value) {
      contextURL.keyPath = value;
      return this;
    }  

    public Builder entitySetOrSingletonOrType(final String entitySetOrSingletonOrType) {
      contextURL.entitySetOrSingletonOrType = entitySetOrSingletonOrType;
      return this;
    }

    public Builder type(final EdmType type) {
      contextURL.entitySetOrSingletonOrType = type.getFullQualifiedName().toString();
      return this;
    }

    public Builder asCollection() {
      contextURL.isCollection = true;
      return this;
    }

    public Builder derived(final EdmEntityType derivedType) {
      contextURL.derivedEntity = derivedType.getFullQualifiedName().getFullQualifiedNameAsString();
      return this;
    }

    public Builder derivedEntity(final String derivedEntity) {
      contextURL.derivedEntity = derivedEntity;
      return this;
    }

    public Builder navOrPropertyPath(final String navOrPropertyPath) {
      contextURL.navOrPropertyPath = navOrPropertyPath;
      return this;
    }

    public Builder selectList(final String selectList) {
      contextURL.selectList = selectList;
      return this;
    }

    public Builder suffix(final Suffix suffix) {
      contextURL.suffix = suffix;
      return this;
    }

    public ContextURL build() {
      return contextURL;
    }
  }
}
