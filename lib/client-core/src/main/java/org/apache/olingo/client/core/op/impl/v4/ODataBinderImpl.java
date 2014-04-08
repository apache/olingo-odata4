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
package org.apache.olingo.client.core.op.impl.v4;

import java.net.URI;
import java.util.List;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.data.ServiceDocumentItem;
import org.apache.olingo.client.api.op.v4.ODataBinder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.op.AbstractODataBinder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.core.data.EnumValueImpl;
import org.apache.olingo.commons.core.data.LinkedComplexValueImpl;
import org.apache.olingo.commons.core.domain.v4.ODataPropertyImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.op.ResourceFactory;

public class ODataBinderImpl extends AbstractODataBinder implements ODataBinder {

  private static final long serialVersionUID = -6371110655960799393L;

  public ODataBinderImpl(final ODataClient client) {
    super(client);
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
  public ODataServiceDocument getODataServiceDocument(final ServiceDocument resource) {
    final ODataServiceDocument serviceDocument = super.getODataServiceDocument(resource);

    for (ServiceDocumentItem functionImport : resource.getFunctionImports()) {
      serviceDocument.getFunctionImports().put(
              functionImport.getName() == null ? functionImport.getHref() : functionImport.getName(),
              URIUtils.getURI(resource.getBaseURI(), functionImport.getHref()));
    }
    for (ServiceDocumentItem singleton : resource.getSingletons()) {
      serviceDocument.getSingletons().put(
              singleton.getName() == null ? singleton.getHref() : singleton.getName(),
              URIUtils.getURI(resource.getBaseURI(), singleton.getHref()));
    }
    for (ServiceDocumentItem sdoc : resource.getRelatedServiceDocuments()) {
      serviceDocument.getRelatedServiceDocuments().put(
              sdoc.getName() == null ? sdoc.getHref() : sdoc.getName(),
              URIUtils.getURI(resource.getBaseURI(), sdoc.getHref()));
    }

    return serviceDocument;
  }

  @Override
  public Entry getEntry(final CommonODataEntity entity, final Class<? extends Entry> reference) {
    final Entry entry = super.getEntry(entity, reference);
    entry.setId(((ODataEntity) entity).getReference());
    return entry;
  }

  @Override
  public Property getProperty(final CommonODataProperty property, final Class<? extends Entry> reference) {
    final ODataProperty _property = (ODataProperty) property;

    final Property propertyResource = ResourceFactory.newProperty(reference);
    propertyResource.setName(_property.getName());
    propertyResource.setValue(getValue(_property.getValue(), reference));

    if (_property.hasPrimitiveValue()) {
      propertyResource.setType(_property.getPrimitiveValue().getTypeName());
    } else if (_property.hasEnumValue()) {
      propertyResource.setType(_property.getEnumValue().getTypeName());
    } else if (_property.hasComplexValue()) {
      propertyResource.setType(_property.getComplexValue().getTypeName());
    } else if (_property.hasCollectionValue()) {
      propertyResource.setType(_property.getCollectionValue().getTypeName());
    }

    return propertyResource;
  }

  @Override
  protected Value getValue(final ODataValue value, final Class<? extends Entry> reference) {
    Value valueResource;
    if (value instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
            && ((org.apache.olingo.commons.api.domain.v4.ODataValue) value).isEnum()) {

      valueResource = new EnumValueImpl(
              ((org.apache.olingo.commons.api.domain.v4.ODataValue) value).asEnum().getValue());
    } else {
      valueResource = super.getValue(value, reference);

      if (value instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
              && ((org.apache.olingo.commons.api.domain.v4.ODataValue) value).isLinkedComplex()) {

        final LinkedComplexValue lcValueResource = new LinkedComplexValueImpl();
        lcValueResource.get().addAll(valueResource.asComplex().get());

        final ODataLinkedComplexValue linked =
                ((org.apache.olingo.commons.api.domain.v4.ODataValue) value).asLinkedComplex();
        links(linked, lcValueResource, reference);

        valueResource = lcValueResource;
      }
    }
    return valueResource;
  }

  @Override
  public ODataEntitySet getODataEntitySet(final Feed resource) {
    return (ODataEntitySet) super.getODataEntitySet(resource);
  }

  @Override
  public ODataEntitySet getODataEntitySet(final Feed resource, final URI defaultBaseURI) {
    return (ODataEntitySet) super.getODataEntitySet(resource, defaultBaseURI);
  }

  @Override
  protected void copyProperties(final List<Property> src, final CommonODataEntity dst, final URI base) {
    for (Property property : src) {
      add(dst, getODataProperty(property, base));
    }
  }

  @Override
  public ODataEntity getODataEntity(final Entry resource) {
    return (ODataEntity) super.getODataEntity(resource);
  }

  @Override
  public ODataEntity getODataEntity(final Entry resource, final URI defaultBaseURI) {
    final ODataEntity entity = (ODataEntity) super.getODataEntity(resource, defaultBaseURI);
    entity.setReference(resource.getId());
    return entity;
  }

  @Override
  public ODataProperty getODataProperty(final Property property) {
    return getODataProperty(property, null);
  }

  @Override
  public ODataProperty getODataProperty(final Property property, final URI base) {
    return new ODataPropertyImpl(property.getName(), getODataValue(property, base));
  }

  @Override
  protected ODataValue getODataValue(final Property resource, final URI base) {
    final EdmTypeInfo typeInfo = resource.getType() == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(resource.getType()).build();

    ODataValue value;
    if (resource.getValue().isEnum()) {
      value = ((ODataClient) client).getObjectFactory().newEnumValue(
              typeInfo == null ? null : typeInfo.getFullQualifiedName().toString(),
              resource.getValue().asEnum().get());
    } else if (resource.getValue().isLinkedComplex()) {
      final ODataLinkedComplexValue lcValue = ((ODataClient) client).getObjectFactory().
              newLinkedComplexValue(typeInfo == null ? null : typeInfo.getFullQualifiedName().toString());

      for (Property property : resource.getValue().asComplex().get()) {
        lcValue.add(getODataProperty(property));
      }

      odataLinks(resource.getValue().asLinkedComplex(), lcValue, base);

      value = lcValue;
    } else {
      value = super.getODataValue(resource, base);
    }

    return value;
  }
}
