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
package org.apache.olingo.odata4.client.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.odata4.client.api.data.ServiceDocument;
import org.apache.olingo.odata4.client.api.data.ServiceDocumentItem;

public abstract class AbstractServiceDocument implements ServiceDocument {

  private String title;

  private final List<ServiceDocumentItem> entitySets = new ArrayList<ServiceDocumentItem>();

  @Override
  public String getMetadataContext() {
    return null;
  }

  @Override
  public String getMetadataETag() {
    return null;
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

  protected ServiceDocumentItem getByTitle(final List<ServiceDocumentItem> elements, final String title) {
    ServiceDocumentItem result = null;
    for (ServiceDocumentItem element : elements) {
      if (title.equals(element.getTitle())) {
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
  public ServiceDocumentItem getEntitySetByTitle(final String title) {
    return getByTitle(getEntitySets(), title);
  }

  @Override
  public List<ServiceDocumentItem> getFunctionImports() {
    return Collections.<ServiceDocumentItem>emptyList();
  }

  @Override
  public ServiceDocumentItem getFunctionImportByName(final String name) {
    return getByName(getFunctionImports(), name);
  }

  @Override
  public ServiceDocumentItem getFunctionImportByTitle(final String title) {
    return getByTitle(getFunctionImports(), title);
  }

  @Override
  public List<ServiceDocumentItem> getSingletons() {
    return Collections.<ServiceDocumentItem>emptyList();
  }

  @Override
  public ServiceDocumentItem getSingletonByName(final String name) {
    return getByName(getSingletons(), name);
  }

  @Override
  public ServiceDocumentItem getSingletonByTitle(final String title) {
    return getByTitle(getSingletons(), title);
  }

  @Override
  public List<ServiceDocumentItem> getRelatedServiceDocuments() {
    return Collections.<ServiceDocumentItem>emptyList();
  }

  @Override
  public ServiceDocumentItem getRelatedServiceDocumentByTitle(final String title) {
    return getByTitle(getRelatedServiceDocuments(), title);
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
