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
package org.apache.olingo.odata4.client.api.domain;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ODataServiceDocument {

  private URI metadataContext;

  private String metadataETag;

  private final Map<String, URI> entitySets = new HashMap<String, URI>();

  private final Map<String, URI> functionImports = new HashMap<String, URI>();

  private final Map<String, URI> singletons = new HashMap<String, URI>();

  private final Map<String, URI> relatedServiceDocuments = new HashMap<String, URI>();

  public URI getMetadataContext() {
    return metadataContext;
  }

  public void setMetadataContext(final URI metadataContext) {
    this.metadataContext = metadataContext;
  }

  public String getMetadataETag() {
    return metadataETag;
  }

  public void setMetadataETag(final String metadataETag) {
    this.metadataETag = metadataETag;
  }

  public Map<String, URI> getEntitySets() {
    return entitySets;
  }

  /**
   * Gets entity set titles.
   *
   * @return entity set titles.
   */
  public Collection<String> getEntitySetTitles() {
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
   * @param title title.
   * @return URI.
   */
  public URI getEntitySetURI(final String title) {
    return entitySets.get(title);
  }

  public Map<String, URI> getFunctionImports() {
    return functionImports;
  }

  /**
   * Gets function import titles.
   *
   * @return function import titles.
   */
  public Collection<String> getFunctionImportTitles() {
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
   * @param title title.
   * @return URI.
   */
  public URI getFunctionImportURI(final String title) {
    return functionImports.get(title);
  }

  public Map<String, URI> getSingletons() {
    return singletons;
  }

  /**
   * Gets singleton titles.
   *
   * @return singleton titles.
   */
  public Collection<String> getSingletonTitles() {
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
   * @param title title.
   * @return URI.
   */
  public URI getSingletonURI(final String title) {
    return singletons.get(title);
  }

  public Map<String, URI> getRelatedServiceDocuments() {
    return relatedServiceDocuments;
  }

  /**
   * Gets related service documents titles.
   *
   * @return related service documents titles.
   */
  public Collection<String> getRelatedServiceDocumentsTitles() {
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
   * @param title title.
   * @return URI.
   */
  public URI getRelatedServiceDocumentURI(final String title) {
    return relatedServiceDocuments.get(title);
  }
}
