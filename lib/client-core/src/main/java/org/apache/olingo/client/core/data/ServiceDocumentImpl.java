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
package org.apache.olingo.client.core.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.data.ServiceDocumentItem;
import org.apache.olingo.commons.api.Constants;

public final class ServiceDocumentImpl implements ServiceDocument {

  private String title;

  private final List<ServiceDocumentItem> entitySets = new ArrayList<ServiceDocumentItem>();
  private final List<ServiceDocumentItem> functionImports = new ArrayList<ServiceDocumentItem>();
  private final List<ServiceDocumentItem> singletons = new ArrayList<ServiceDocumentItem>();
  private final List<ServiceDocumentItem> relatedServiceDocuments = new ArrayList<ServiceDocumentItem>();

  private String metadata;

  @Override
  public URI getBaseURI() {
    URI baseURI = null;
    if (metadata != null) {
      final String metadataURI = getMetadata();
      baseURI = URI.create(metadataURI.substring(0, metadataURI.indexOf(Constants.METADATA)));
    }

    return baseURI;
  }

  /**
   * Gets the metadata URI.
   *
   * @return the metadata URI
   */
  public String getMetadata() {
    return metadata;
  }

  /**
   * Sets the metadata URI.
   *
   * @param metadata metadata URI.
   */
  public void setMetadata(final String metadata) {
    this.metadata = metadata;
  }

  @Override
  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  protected ServiceDocumentItem getByName(final List<ServiceDocumentItem> elements, final String name) {
    ServiceDocumentItem result = null;
    for (ServiceDocumentItem element : elements) {
      if (name.equals(element.getName())) {
        result = element;
      }
    }
    return result;
  }

  @Override
  public List<ServiceDocumentItem> getEntitySets() {
    return entitySets;
  }

  @Override
  public ServiceDocumentItem getEntitySetByName(final String name) {
    return getByName(getEntitySets(), name);
  }

  @Override
  public List<ServiceDocumentItem> getFunctionImports() {
    return functionImports;
  }

  @Override
  public ServiceDocumentItem getFunctionImportByName(final String name) {
    return getByName(getFunctionImports(), name);
  }

  @Override
  public List<ServiceDocumentItem> getSingletons() {
    return singletons;
  }

  @Override
  public ServiceDocumentItem getSingletonByName(final String name) {
    return getByName(getSingletons(), name);
  }

  @Override
  public List<ServiceDocumentItem> getRelatedServiceDocuments() {
    return relatedServiceDocuments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ServiceDocumentImpl that = (ServiceDocumentImpl) o;

    if (title != null ? !title.equals(that.title) : that.title != null) {
      return false;
    }
    if (!entitySets.equals(that.entitySets)) {
      return false;
    }
    if (!functionImports.equals(that.functionImports)) {
      return false;
    }
    if (!singletons.equals(that.singletons)) {
      return false;
    }
    if (!relatedServiceDocuments.equals(that.relatedServiceDocuments)) {
      return false;
    }
    return !(metadata != null ? !metadata.equals(that.metadata) : that.metadata != null);

  }

  @Override
  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (entitySets.hashCode());
    result = 31 * result + (functionImports.hashCode());
    result = 31 * result + (singletons.hashCode());
    result = 31 * result + (relatedServiceDocuments.hashCode());
    result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ServiceDocumentImpl{" +
        "title='" + title + '\'' +
        ", entitySets=" + entitySets +
        ", functionImports=" + functionImports +
        ", singletons=" + singletons +
        ", relatedServiceDocuments=" + relatedServiceDocuments +
        ", metadata='" + metadata + '\'' +
        '}';
  }
}
