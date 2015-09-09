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
public final class ContextURL {

  private URI serviceRoot;

  private String entitySetOrSingletonOrType;

  private boolean isCollection = false;

  private String derivedEntity;

  private String selectList;

  private String navOrPropertyPath;

  private String keyPath;

  private Suffix suffix;

  private String odataPath;
  
  /**
   * Suffix of the OData Context URL
   */
  public enum Suffix {
    /**
     * Suffix for Entities
     */
    ENTITY("$entity"), 
    /**
     * Suffix for References
     */
    REFERENCE("$ref"),
    /**
     * Suffix for deltas (changes)
     */
    DELTA("$delta"), 
    /**
     * Suffix for deleted entities in deltas
     */
    DELTA_DELETED_ENTITY("$deletedEntity"), 
    /**
     * New links in deltas
     */
    DELTA_LINK("$link"), 
    /**
     * Deleted links in deltas
     */
    DELTA_DELETED_LINK("$deletedLink");

    private final String representation;

    Suffix(final String representation) {
      this.representation = representation;
    }
    
    /**
     * Returns OData representation of the suffix 
     * 
     * @return Representation of the suffix 
     */
    public String getRepresentation() {
      return representation;
    }
  }

  private ContextURL() {}

  /**
   * Get the OData path.
   * @return the OData path
   */
  public String getODataPath() {
    return odataPath;
  }

  /**
   * Get the service root.
   * @return the service root
   */
  public URI getServiceRoot() {
    return serviceRoot;
  }

  /**
   * Get the set entity set / singleton / type.
   * @return the entity set / singleton / type
   */
  public String getEntitySetOrSingletonOrType() {
    return entitySetOrSingletonOrType;
  }

  /**
   * Is context result a collection.
   * @return <code>true</code> for a collection, otherwise <code>false</code>
   */
  public boolean isCollection() {
    return isCollection;
  }

  /**
   * Get the derived entity.
   * @return derived entity
   */
  public String getDerivedEntity() {
    return derivedEntity;
  }

  /**
   * Get the select list.
   * @return the select list
   */
  public String getSelectList() {
    return selectList;
  }

  /**
   * Get the set navigation or property path.
   * @return the set navigation or property path
   */
  public String getNavOrPropertyPath() {
    return navOrPropertyPath;
  }

  /**
   * Get the set key path.
   * @return the set key path
   */
  public String getKeyPath() {
    return keyPath;
  }

  /**
   * Get the set suffix.
   * @return the set suffix
   */
  public Suffix getSuffix() {
    return suffix;
  }

  /**
   * Is context result a entity.
   * @return <code>true</code> for a reference, otherwise <code>false</code>
   */
  public boolean isEntity() {
    return suffix == Suffix.ENTITY;
  }

  /**
   * Is context result a reference.
   * @return <code>true</code> for a reference, otherwise <code>false</code>
   */
  public boolean isReference() {
    return suffix == Suffix.REFERENCE;
  }

  /**
   * Is context result a delta result.
   * @return <code>true</code> for a delta result, otherwise <code>false</code>
   */
  public boolean isDelta() {
    return suffix == Suffix.DELTA;
  }

  /**
   * Is context result a delta deleted entity.
   * @return <code>true</code> for a delta deleted entity, otherwise <code>false</code>
   */
  public boolean isDeltaDeletedEntity() {
    return suffix == Suffix.DELTA_DELETED_ENTITY;
  }

  /**
   * Is context result a delta link.
   * @return <code>true</code> for a delta link, otherwise <code>false</code>
   */
  public boolean isDeltaLink() {
    return suffix == Suffix.DELTA_LINK;
  }

  /**
   * Is context result a delta deleted link.
   * @return <code>true</code> for a delta deleted link, otherwise <code>false</code>
   */
  public boolean isDeltaDeletedLink() {
    return suffix == Suffix.DELTA_DELETED_LINK;
  }

  /**
   * Start building a ContextURL instance.
   * @return builder for building a ContextURL instance
   */
  public static Builder with() {
    return new Builder();
  }

  /**
   * Builder for a ContextURL instance.
   */
  public static final class Builder {

    private final ContextURL contextURL = new ContextURL();

    /**
     * Set the OData path.
     * @param oDataPath the OData path
     * @return Builder
     */
    public Builder oDataPath(String oDataPath) {
      contextURL.odataPath = oDataPath;
      return this;
    }

    /**
     * Set the service root.
     * @param serviceRoot the service root
     * @return Builder
     */
    public Builder serviceRoot(final URI serviceRoot) {
      contextURL.serviceRoot = serviceRoot;
      return this;
    }

    /**
     * Set the edm entity set.
     * @param entitySet the edm entity set
     * @return Builder
     */
    public Builder entitySet(final EdmEntitySet entitySet) {
      contextURL.entitySetOrSingletonOrType = entitySet.getName();
      return this;
    }

    /**
     * Set the key path.
     * @param keyPath the key path
     * @return Builder
     */
    public Builder keyPath(final String keyPath) {
      contextURL.keyPath = keyPath;
      return this;
    }

    /**
     * Set the entity set / singleton / type name.
     * @param entitySetOrSingletonOrType the entity set / singleton / type name
     * @return Builder
     */
    public Builder entitySetOrSingletonOrType(final String entitySetOrSingletonOrType) {
      contextURL.entitySetOrSingletonOrType = entitySetOrSingletonOrType;
      return this;
    }

    /**
     * Set the edm entity type.
     * @param type the edm entity type
     * @return Builder
     */
    public Builder type(final EdmType type) {
      contextURL.entitySetOrSingletonOrType = type.getFullQualifiedName().toString();
      return this;
    }

    /**
     * Define the result as a collection.
     * @return Builder
     */
    public Builder asCollection() {
      contextURL.isCollection = true;
      return this;
    }

    /**
     * Set the derived edm entity type.
     * @param derivedType the derived edm entity type
     * @return Builder
     */
    public Builder derived(final EdmEntityType derivedType) {
      contextURL.derivedEntity = derivedType.getFullQualifiedName().getFullQualifiedNameAsString();
      return this;
    }

    /**
     * Set the derived entity name.
     * @param derivedEntity the derived entity name
     * @return Builder
     */
    public Builder derivedEntity(final String derivedEntity) {
      contextURL.derivedEntity = derivedEntity;
      return this;
    }

    /**
     * Set the navigation or property path.
     * @param navOrPropertyPath the navigation or property path
     * @return Builder
     */
    public Builder navOrPropertyPath(final String navOrPropertyPath) {
      contextURL.navOrPropertyPath = navOrPropertyPath;
      return this;
    }

    /**
     * Set the select list.
     * @param selectList the select list
     * @return Builder
     */
    public Builder selectList(final String selectList) {
      contextURL.selectList = selectList;
      return this;
    }

    /**
     * Set the suffix.
     * @param suffix the suffix
     * @return Builder
     */
    public Builder suffix(final Suffix suffix) {
      contextURL.suffix = suffix;
      return this;
    }

    /**
     * Create the ContextURL instance based on set values.
     * @return the according ContextURL
     */
    public ContextURL build() {
      return contextURL;
    }
  }
}
