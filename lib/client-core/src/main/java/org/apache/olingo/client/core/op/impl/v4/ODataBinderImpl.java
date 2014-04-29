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
import org.apache.olingo.client.api.op.v4.ODataBinder;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.op.AbstractODataBinder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.v4.ODataDeletedEntity;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.domain.v4.ODataDeltaLink;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v4.ODataDelta;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataLinkedComplexValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.core.domain.v4.ODataDeletedEntityImpl;
import org.apache.olingo.commons.core.domain.v4.ODataDeltaLinkImpl;
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
  public EntitySet getEntitySet(final CommonODataEntitySet odataEntitySet, final Class<? extends EntitySet> reference) {
    final EntitySet entitySet = super.getEntitySet(odataEntitySet, reference);
    entitySet.setDeltaLink(((ODataEntitySet) odataEntitySet).getDeltaLink());
    return entitySet;
  }

  @Override
  public Entity getEntity(final CommonODataEntity odataEntity, final Class<? extends Entity> reference) {
    final Entity entity = super.getEntity(odataEntity, reference);
    entity.setId(((ODataEntity) odataEntity).getReference());
    return entity;
  }

  @Override
  public Property getProperty(final CommonODataProperty property, final Class<? extends Entity> reference) {
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
  protected Value getValue(final ODataValue value, final Class<? extends Entity> reference) {
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
  public ODataEntitySet getODataEntitySet(final ResWrap<EntitySet> resource) {
    final ODataEntitySet entitySet = (ODataEntitySet) super.getODataEntitySet(resource);

    if (resource.getPayload().getDeltaLink() != null) {
      final URI base = resource.getContextURL() == null
              ? resource.getPayload().getBaseURI() : resource.getContextURL().getServiceRoot();
      entitySet.setDeltaLink(URIUtils.getURI(base, resource.getPayload().getDeltaLink()));
    }

    return entitySet;
  }

  @Override
  public ODataEntity getODataEntity(final ResWrap<Entity> resource) {
    final ODataEntity entity = (ODataEntity) super.getODataEntity(resource);
    entity.setReference(resource.getPayload().getId());
    return entity;
  }

  @Override
  public ODataProperty getODataProperty(final ResWrap<Property> property) {
    return new ODataPropertyImpl(property.getPayload().getName(), getODataValue(property));
  }

  @Override
  protected ODataValue getODataValue(final ResWrap<Property> resource) {
    final EdmTypeInfo typeInfo = buildTypeInfo(resource);

    ODataValue value;
    if (resource.getPayload().getValue().isEnum()) {
      value = ((ODataClient) client).getObjectFactory().newEnumValue(
              typeInfo == null ? null : typeInfo.getFullQualifiedName().toString(),
              resource.getPayload().getValue().asEnum().get());
    } else if (resource.getPayload().getValue().isLinkedComplex()) {
      final ODataLinkedComplexValue lcValue = ((ODataClient) client).getObjectFactory().
              newLinkedComplexValue(typeInfo == null ? null : typeInfo.getFullQualifiedName().toString());

      for (Property property : resource.getPayload().getValue().asComplex().get()) {
        lcValue.add(getODataProperty(
                new ResWrap<Property>(resource.getContextURL(), resource.getMetadataETag(), property)));
      }

      EdmComplexType edmType = null;
      if (client instanceof EdmEnabledODataClient && typeInfo != null) {
        edmType = ((EdmEnabledODataClient) client).getEdm(resource.getMetadataETag()).
                getComplexType(typeInfo.getFullQualifiedName());
      }

      odataNavigationLinks(edmType, resource.getPayload().getValue().asLinkedComplex(), lcValue,
              resource.getMetadataETag(), resource.getContextURL() == null ? null : resource.getContextURL().getURI());

      value = lcValue;
    } else {
      value = super.getODataValue(resource);
    }

    return value;
  }

  @Override
  public ODataDelta getODataDelta(final ResWrap<Delta> resource) {
    final URI base = resource.getContextURL() == null
            ? resource.getPayload().getBaseURI() : resource.getContextURL().getServiceRoot();

    final URI next = resource.getPayload().getNext();

    final ODataDelta delta = next == null
            ? ((ODataClient) client).getObjectFactory().newDelta()
            : ((ODataClient) client).getObjectFactory().newDelta(URIUtils.getURI(base, next.toASCIIString()));

    if (resource.getPayload().getCount() != null) {
      delta.setCount(resource.getPayload().getCount());
    }

    if (resource.getPayload().getDeltaLink() != null) {
      delta.setDeltaLink(URIUtils.getURI(base, resource.getPayload().getDeltaLink()));
    }

    for (Entity entityResource : resource.getPayload().getEntities()) {
      add(delta, getODataEntity(
              new ResWrap<Entity>(resource.getContextURL(), resource.getMetadataETag(), entityResource)));
    }
    for (ODataDeletedEntity deletedEntity : resource.getPayload().getDeletedEntities()) {
      final ODataDeletedEntityImpl impl = new ODataDeletedEntityImpl();
      impl.setId(URIUtils.getURI(base, deletedEntity.getId()));
      impl.setReason(deletedEntity.getReason());

      delta.getDeletedEntities().add(impl);
    }

    for (ODataDeltaLink link : resource.getPayload().getAddedLinks()) {
      final ODataDeltaLinkImpl impl = new ODataDeltaLinkImpl();
      impl.setRelationship(link.getRelationship());
      impl.setSource(URIUtils.getURI(base, link.getSource()));
      impl.setTarget(URIUtils.getURI(base, link.getTarget()));

      delta.getAddedLinks().add(impl);
    }
    for (ODataDeltaLink link : resource.getPayload().getDeletedLinks()) {
      final ODataDeltaLinkImpl impl = new ODataDeltaLinkImpl();
      impl.setRelationship(link.getRelationship());
      impl.setSource(URIUtils.getURI(base, link.getSource()));
      impl.setTarget(URIUtils.getURI(base, link.getTarget()));

      delta.getDeletedLinks().add(impl);
    }

    return delta;
  }

}
