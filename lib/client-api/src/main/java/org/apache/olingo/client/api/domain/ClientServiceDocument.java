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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientServiceDocument {

  private final Map<String, URI> entitySets = new HashMap<String, URI>();

  private final Map<String, URI> functionImports = new HashMap<String, URI>();

  private final Map<String, URI> singletons = new HashMap<String, URI>();

  private final Map<String, URI> relatedServiceDocuments = new HashMap<String, URI>();

  public Map<String, URI> getEntitySets() {
    return entitySets;
  }

  /**
   * Gets entity set names.
   * 
   * @return entity set names.
   */
  public Collection<String> getEntitySetNames() {
    return entitySets.keySet();
  }

  /**
   * Gets entity set URIs.
   * 
   * @return entity set URIs.
   */
  public Collection<URI> getEntitySetURIs() {
    return entitySets.values();
  }

  /**
   * Gets URI about the given entity set.
   * 
   * @param name name.
   * @return URI.
   */
  public URI getEntitySetURI(final String name) {
    return entitySets.get(name);
  }

  public Map<String, URI> getFunctionImports() {
    return functionImports;
  }

  /**
   * Gets function import names.
   * 
   * @return function import names.
   */
  public Collection<String> getFunctionImportNames() {
    return functionImports.keySet();
  }

  /**
   * Gets function import URIs.
   * 
   * @return function import URIs.
   */
  public Collection<URI> getFunctionImportURIs() {
    return functionImports.values();
  }

  /**
   * Gets URI of the given function import.
   * 
   * @param name name.
   * @return URI.
   */
  public URI getFunctionImportURI(final String name) {
    return functionImports.get(name);
  }

  public Map<String, URI> getSingletons() {
    return singletons;
  }

  /**
   * Gets singleton names.
   * 
   * @return singleton names.
   */
  public Collection<String> getSingletonNames() {
    return singletons.keySet();
  }

  /**
   * Gets singleton URIs.
   * 
   * @return singleton URIs.
   */
  public Collection<URI> getSingletonURIs() {
    return singletons.values();
  }

  /**
   * Gets URI of the given singleton.
   * 
   * @param name name.
   * @return URI.
   */
  public URI getSingletonURI(final String name) {
    return singletons.get(name);
  }

  public Map<String, URI> getRelatedServiceDocuments() {
    return relatedServiceDocuments;
  }

  /**
   * Gets related service documents names.
   * 
   * @return related service documents names.
   */
  public Collection<String> getRelatedServiceDocumentsNames() {
    return relatedServiceDocuments.keySet();
  }

  /**
   * Gets related service documents URIs.
   * 
   * @return related service documents URIs.
   */
  public Collection<URI> getRelatedServiceDocumentsURIs() {
    return relatedServiceDocuments.values();
  }

  /**
   * Gets URI of the given related service documents.
   * 
   * @param name name.
   * @return URI.
   */
  public URI getRelatedServiceDocumentURI(final String name) {
    return relatedServiceDocuments.get(name);
  }
}
