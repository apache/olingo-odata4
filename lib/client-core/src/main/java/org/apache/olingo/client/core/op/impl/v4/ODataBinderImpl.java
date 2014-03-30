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
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.data.ServiceDocumentItem;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.client.api.op.v4.ODataBinder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.op.AbstractODataBinder;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.core.data.EnumValueImpl;
import org.apache.olingo.commons.core.domain.v4.ODataPropertyImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

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

    serviceDocument.setMetadataContext(URIUtils.getURI(resource.getBaseURI(), resource.getMetadataContext()));
    serviceDocument.setMetadataETag(resource.getMetadataETag());

    for (ServiceDocumentItem functionImport : resource.getFunctionImports()) {
      serviceDocument.getFunctionImports().put(functionImport.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), functionImport.getHref()));
    }
    for (ServiceDocumentItem singleton : resource.getSingletons()) {
      serviceDocument.getSingletons().put(singleton.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), singleton.getHref()));
    }
    for (ServiceDocumentItem sdoc : resource.getRelatedServiceDocuments()) {
      serviceDocument.getRelatedServiceDocuments().put(sdoc.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), sdoc.getHref()));
    }

    return serviceDocument;
  }

  @Override
  public Entry getEntry(final CommonODataEntity entity, final Class<? extends Entry> reference, final boolean setType) {
    final Entry entry = super.getEntry(entity, reference, setType);
    entry.setId(((ODataEntity) entity).getReference());
    return entry;
  }

  @Override
  public Property getProperty(final CommonODataProperty property, final Class<? extends Entry> reference,
          final boolean setType) {

    final Property propertyResource = super.getProperty(property, reference, setType);
    if (property instanceof ODataProperty && ((ODataProperty) property).hasEnumValue() && setType) {
      propertyResource.setType(((ODataProperty) property).getEnumValue().getTypeName());
    }
    return propertyResource;
  }

  @Override
  protected Value getValue(final ODataValue value, final Class<? extends Entry> reference, final boolean setType) {
    Value valueResource;
    if (value instanceof org.apache.olingo.commons.api.domain.v4.ODataValue
            && ((org.apache.olingo.commons.api.domain.v4.ODataValue) value).isEnum()) {

      valueResource = new EnumValueImpl(
              ((org.apache.olingo.commons.api.domain.v4.ODataValue) value).asEnum().getValue());
    } else {
      valueResource = super.getValue(value, reference, setType);
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
    return new ODataPropertyImpl(property.getName(), getODataValue(property));
  }

  @Override
  protected ODataValue getODataValue(final Property resource) {
    ODataValue value;
    if (resource.getValue().isEnum()) {
      final EdmTypeInfo typeInfo = resource.getType() == null
              ? null
              : new EdmTypeInfo.Builder().setTypeExpression(resource.getType()).build();
      value = ((ODataClient) client).getObjectFactory().newEnumValue(
              typeInfo == null ? null : typeInfo.getFullQualifiedName().toString(),
              resource.getValue().asEnum().get());
    } else {
      value = super.getODataValue(resource);
    }

    return value;
  }
}
