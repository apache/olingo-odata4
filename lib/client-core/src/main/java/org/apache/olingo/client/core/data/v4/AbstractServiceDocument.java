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
package org.apache.olingo.client.core.data.v4;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.data.ServiceDocumentItem;

public abstract class AbstractServiceDocument
        extends org.apache.olingo.client.core.data.AbstractServiceDocument {

  private URI baseURI;

  private String metadataContext;

  private String metadataETag;

  private List<ServiceDocumentItem> functionImports = new ArrayList<ServiceDocumentItem>();

  private List<ServiceDocumentItem> singletons = new ArrayList<ServiceDocumentItem>();

  private List<ServiceDocumentItem> relatedServiceDocuments = new ArrayList<ServiceDocumentItem>();

  @Override
  public URI getBaseURI() {
    return this.baseURI;
  }

  /**
   * Sets base URI.
   *
   * @param baseURI base URI.
   */
  public void setBaseURI(final URI baseURI) {
    this.baseURI = baseURI;
  }

  @Override
  public String getMetadataContext() {
    return metadataContext;
  }

  public void setMetadataContext(final String metadataContext) {
    this.metadataContext = metadataContext;
  }

  @Override
  public String getMetadataETag() {
    return metadataETag;
  }

  public void setMetadataETag(final String metadataETag) {
    this.metadataETag = metadataETag;
  }

  @Override
  public List<ServiceDocumentItem> getFunctionImports() {
    return functionImports;
  }

  @Override
  public List<ServiceDocumentItem> getSingletons() {
    return singletons;
  }

  @Override
  public List<ServiceDocumentItem> getRelatedServiceDocuments() {
    return relatedServiceDocuments;
  }

}
