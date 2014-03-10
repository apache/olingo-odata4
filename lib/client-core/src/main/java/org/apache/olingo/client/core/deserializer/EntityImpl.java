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
package org.apache.olingo.client.core.deserializer;

import java.util.Map;

import org.apache.olingo.client.api.deserializer.AnnotationProperty;
import org.apache.olingo.client.api.deserializer.Entity;
import org.apache.olingo.client.api.deserializer.NavigationProperty;
import org.apache.olingo.client.api.deserializer.Property;
import org.apache.olingo.client.api.deserializer.StructuralProperty;

public class EntityImpl extends PropertyCollection implements Entity {

  public EntityImpl() {
  }

  public EntityImpl(final Map<String, AnnotationProperty> annotationProperties,
          final Map<String, NavigationProperty> navigationProperties,
          final Map<String, StructuralProperty> structuralProperties) {

    super(annotationProperties, navigationProperties, structuralProperties);
  }

  @Override
  public String getODataMetaDataEtag() {
    return getAnnotationValue("odata.metadataEtag");
  }

  @Override
  public String getODataType() {
    return getAnnotationValue("odata.type");
  }

  @Override
  public Long getODataCount() {
    return Long.valueOf(getAnnotationValue("odata.count"));
  }

  @Override
  public String getODataNextLink() {
    return getAnnotationValue("odata.nextLink");
  }

  @Override
  public String getODataDeltaLink() {
    return getAnnotationValue("odata.deltaLink");
  }

  @Override
  public String getODataReadLink() {
    return getAnnotationValue("odata.readLink");
  }

  @Override
  public String getODataContext() {
    return getAnnotationValue("odata.context");
  }

  @Override
  public String getODataId() {
    return getAnnotationValue("odata.id");
  }

  @Override
  public String getODataETag() {
    return getAnnotationValue("odata.etag");
  }

  @Override
  public String getODataEditLink() {
    return getAnnotationValue("odata.editLink");
  }

  @Override
  public Object getPropertyContent(final String name) {
    final StructuralProperty property = structuralProperties.get(name);
    if (property != null) {
      return property.getValue().getContent();
    }
    return null;
  }

  @Override
  public Property getProperty(final String name) {
    Property property = structuralProperties.get(name);
    if (property == null) {
      property = annotationProperties.get(name);
    }
    if (property == null) {
      property = navigationProperties.get(name);
    }
    return property;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Property> T getProperty(final String name, final Class<T> clazz) {
    final Property property = getProperty(name);
    return (T) property;
  }

  private String getAnnotationValue(final String key) {
    final AnnotationProperty property = annotationProperties.get(key);
    if (property == null) {
      return null;
    }
    return property.getValue();
  }
}
