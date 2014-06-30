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
package org.apache.olingo.client.core.serialization.v3;

import org.apache.olingo.client.api.domain.v3.ODataLinkCollection;
import org.apache.olingo.client.api.serialization.v3.ODataBinder;
import org.apache.olingo.client.core.serialization.AbstractODataBinder;
import org.apache.olingo.client.core.v3.ODataClientImpl;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.data.v3.LinkCollection;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.domain.v3.ODataPropertyImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class ODataBinderImpl extends AbstractODataBinder implements ODataBinder {

  public ODataBinderImpl(final ODataClientImpl client) {
    super(client);
  }

  @Override
  public void add(final ODataComplexValue<CommonODataProperty> complex, final CommonODataProperty property) {
    complex.add(property);
  }

  @Override
  public boolean add(final CommonODataEntity entity, final CommonODataProperty property) {
    return ((ODataEntity) entity).getProperties().add((ODataProperty) property);
  }

  @Override
  protected boolean add(final CommonODataEntitySet entitySet, final CommonODataEntity entity) {
    return ((ODataEntitySet) entitySet).getEntities().add((ODataEntity) entity);
  }

  @Override
  public Property getProperty(final CommonODataProperty property) {
    final Property propertyResource = new PropertyImpl();
    propertyResource.setName(property.getName());

    final Object propertyValue = getValue(property.getValue());
    if (property.hasPrimitiveValue()) {
      propertyResource.setType(property.getPrimitiveValue().getTypeName());
      propertyResource.setValue(
          propertyValue instanceof Geospatial ? ValueType.GEOSPATIAL : ValueType.PRIMITIVE,
          propertyValue);
    } else if (property.hasComplexValue()) {
      propertyResource.setType(((ODataProperty) property).getComplexValue().getTypeName());
      propertyResource.setValue(ValueType.COMPLEX, propertyValue);
    } else if (property.hasCollectionValue()) {
      final ODataCollectionValue<ODataValue> collectionValue = ((ODataProperty) property).getCollectionValue();
      propertyResource.setType(collectionValue.getTypeName());
      final ODataValue value = collectionValue.iterator().hasNext() ? collectionValue.iterator().next() : null;
      ValueType valueType = ValueType.COLLECTION_PRIMITIVE;
      if (value == null) {
        valueType = ValueType.COLLECTION_PRIMITIVE;
      } else if (value.isPrimitive()) { 
        valueType = value.asPrimitive().toValue() instanceof Geospatial ?
            ValueType.COLLECTION_GEOSPATIAL : ValueType.COLLECTION_PRIMITIVE;
      } else if (value.isComplex()) {
        valueType = ValueType.COLLECTION_COMPLEX;
      }
      propertyResource.setValue(valueType, propertyValue);
    }

    return propertyResource;
  }

  @Override
  public ODataEntitySet getODataEntitySet(final ResWrap<EntitySet> resource) {
    return (ODataEntitySet) super.getODataEntitySet(resource);
  }

  @Override
  public ODataEntity getODataEntity(final ResWrap<Entity> resource) {
    return (ODataEntity) super.getODataEntity(resource);
  }

  @Override
  public ODataProperty getODataProperty(final ResWrap<Property> property) {
    final EdmTypeInfo typeInfo = buildTypeInfo(property.getContextURL(), property.getMetadataETag(),
            property.getPayload().getName(), property.getPayload().getType());

    return new ODataPropertyImpl(property.getPayload().getName(),
            getODataValue(typeInfo == null ? null : typeInfo.getFullQualifiedName(),
                    property.getPayload(), property.getContextURL(), property.getMetadataETag()));
  }

  @Override
  protected ODataProperty getODataProperty(final EdmType type, final Property resource) {
    final EdmTypeInfo typeInfo = buildTypeInfo(type == null ? null : type.getFullQualifiedName(), resource.getType());

    return new ODataPropertyImpl(resource.getName(),
        getODataValue(typeInfo == null ? null : typeInfo.getFullQualifiedName(),
            resource, null, null));
  }

  @Override
  public ODataLinkCollection getLinkCollection(final LinkCollection linkCollection) {
    final ODataLinkCollection collection = new ODataLinkCollection(linkCollection.getNext());
    collection.setLinks(linkCollection.getLinks());
    return collection;
  }
}
