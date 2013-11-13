/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.consumer.core.deserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.consumer.api.deserializer.Entity;
import org.apache.olingo.consumer.api.deserializer.EntitySet;

import com.fasterxml.jackson.core.JsonParseException;

public class EntitySetImpl implements EntitySet, Iterator<Entity> {

  private String odataContext;
  private Long odataCount;
  private String odataNextLink;
  private String odataDeltaLink;
  private List<Entity> entities = null;

  private PropertyCollectionBuilder propertyCollectionsBuilder;

  @Override
  public String getODataContext() {
    return odataContext;
  }

  @Override
  public Long getODataCount() {
    return odataCount;
  }

  @Override
  public String getODataNextLink() {
    return odataNextLink;
  }

  @Override
  public String getODataDeltaLink() {
    return odataDeltaLink;
  }

  public void addAnnotation(final String name, final String value) {
    if ("odata.context".equalsIgnoreCase(name)) {
      odataContext = value;
    } else if ("odata.deltaLink".equalsIgnoreCase(name)) {
      odataDeltaLink = value;
    } else if ("odata.count".equalsIgnoreCase(name)) {
      odataCount = Long.parseLong(value);
    } else if ("odata.nextLink".equalsIgnoreCase(name)) {
      odataNextLink = value;
    }
  }

  @Override
  public List<Entity> getEntities() {
    if (entities == null) {
      entities = new ArrayList<Entity>();

      while (propertyCollectionsBuilder.parseNext()) {
        entities.add(propertyCollectionsBuilder.buildEntity());
      }
    }

    return entities;
  }

  public void setPropertyCollectionBuilder(final PropertyCollectionBuilder builder) {
    propertyCollectionsBuilder = builder;
  }

  @Override
  public boolean hasNext() {
    try {
      return propertyCollectionsBuilder.hasNext();
    } catch (JsonParseException e) {} catch (IOException e) {}
    return false;
  }

  @Override
  public Entity next() {
    if (propertyCollectionsBuilder.parseNext()) {
      return propertyCollectionsBuilder.buildEntity();
    }
    return null;
  }

  @Override
  public void remove() {}

  @Override
  public Iterator<Entity> iterator() {
    return this;
  }
}
