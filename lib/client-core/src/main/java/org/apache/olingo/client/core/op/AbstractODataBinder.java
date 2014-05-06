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
package org.apache.olingo.client.core.op;

import java.io.StringWriter;
import java.net.URI;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.data.ServiceDocumentItem;
import org.apache.olingo.client.api.op.CommonODataBinder;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataLinked;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.data.CollectionValueImpl;
import org.apache.olingo.commons.core.data.ComplexValueImpl;
import org.apache.olingo.commons.core.data.GeospatialValueImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.commons.core.data.NullValueImpl;
import org.apache.olingo.commons.core.data.PrimitiveValueImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.op.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractODataBinder implements CommonODataBinder {

  private static final long serialVersionUID = 454285889193689536L;

  /**
   * Logger.
   */
  protected final Logger LOG = LoggerFactory.getLogger(AbstractODataBinder.class);

  protected final CommonODataClient<?> client;

  protected AbstractODataBinder(final CommonODataClient<?> client) {
    this.client = client;
  }

  @Override
  public ODataServiceDocument getODataServiceDocument(final ServiceDocument resource) {
    final ODataServiceDocument serviceDocument = new ODataServiceDocument();

    for (ServiceDocumentItem entitySet : resource.getEntitySets()) {
      // handles V3 JSON format oddities, where title is not contained
      serviceDocument.getEntitySets().put(StringUtils.isBlank(entitySet.getTitle())
              ? entitySet.getName() : entitySet.getTitle(),
              URIUtils.getURI(resource.getBaseURI(), entitySet.getHref()));
    }

    return serviceDocument;
  }

  @Override
  public EntitySet getEntitySet(final CommonODataEntitySet odataEntitySet, final Class<? extends EntitySet> reference) {
    final EntitySet entitySet = ResourceFactory.newEntitySet(reference);

    entitySet.setCount(odataEntitySet.getCount());

    final URI next = odataEntitySet.getNext();
    if (next != null) {
      entitySet.setNext(next);
    }

    for (CommonODataEntity entity : odataEntitySet.getEntities()) {
      entitySet.getEntities().add(getEntity(entity, ResourceFactory.entityClassForEntitySet(reference)));
    }

    return entitySet;
  }

  protected void links(final ODataLinked odataLinked, final Linked linked, final Class<? extends Entity> reference) {
    // -------------------------------------------------------------
    // Append navigation links (handling inline entity / entity set as well)
    // -------------------------------------------------------------
    // handle navigation links
    for (ODataLink link : odataLinked.getNavigationLinks()) {
      // append link 
      LOG.debug("Append navigation link\n{}", link);
      linked.getNavigationLinks().add(getLink(link,
              ResourceFactory.formatForEntityClass(reference) == ODataPubFormat.ATOM));
    }
    // -------------------------------------------------------------

    // -------------------------------------------------------------
    // Append association links
    // -------------------------------------------------------------
    for (ODataLink link : odataLinked.getAssociationLinks()) {
      LOG.debug("Append association link\n{}", link);
      linked.getAssociationLinks().add(getLink(link,
              ResourceFactory.formatForEntityClass(reference) == ODataPubFormat.ATOM));
    }
    // -------------------------------------------------------------
  }

  @Override
  public Entity getEntity(final CommonODataEntity odataEntity, final Class<? extends Entity> reference) {
    final Entity entity = ResourceFactory.newEntity(reference);

    entity.setType(odataEntity.getTypeName() == null ? null : odataEntity.getTypeName().toString());

    // -------------------------------------------------------------
    // Add edit and self link
    // -------------------------------------------------------------
    final URI odataEditLink = odataEntity.getEditLink();
    if (odataEditLink != null) {
      final LinkImpl editLink = new LinkImpl();
      editLink.setTitle(entity.getType());
      editLink.setHref(odataEditLink.toASCIIString());
      editLink.setRel(Constants.EDIT_LINK_REL);
      entity.setEditLink(editLink);
    }

    if (odataEntity.isReadOnly()) {
      final LinkImpl selfLink = new LinkImpl();
      selfLink.setTitle(entity.getType());
      selfLink.setHref(odataEntity.getLink().toASCIIString());
      selfLink.setRel(Constants.SELF_LINK_REL);
      entity.setSelfLink(selfLink);
    }
    // -------------------------------------------------------------

    links(odataEntity, entity, reference);

    // -------------------------------------------------------------
    // Append edit-media links
    // -------------------------------------------------------------
    for (ODataLink link : odataEntity.getEditMediaLinks()) {
      LOG.debug("Append edit-media link\n{}", link);
      entity.getMediaEditLinks().add(getLink(link,
              ResourceFactory.formatForEntityClass(reference) == ODataPubFormat.ATOM));
    }
    // -------------------------------------------------------------

    if (odataEntity.isMediaEntity()) {
      entity.setMediaContentSource(odataEntity.getMediaContentSource());
      entity.setMediaContentType(odataEntity.getMediaContentType());
      entity.setMediaETag(odataEntity.getMediaETag());
    }

    for (CommonODataProperty property : odataEntity.getProperties()) {
      entity.getProperties().add(getProperty(property, reference));
    }

    return entity;
  }

  @Override
  public Link getLink(final ODataLink link, boolean isXML) {
    final Link linkResource = new LinkImpl();
    linkResource.setRel(link.getRel());
    linkResource.setTitle(link.getName());
    linkResource.setHref(link.getLink() == null ? null : link.getLink().toASCIIString());
    linkResource.setType(link.getType().toString());
    linkResource.setMediaETag(link.getMediaETag());

    if (link instanceof ODataInlineEntity) {
      // append inline entity
      final CommonODataEntity inlineEntity = ((ODataInlineEntity) link).getEntity();
      LOG.debug("Append in-line entity\n{}", inlineEntity);

      linkResource.setInlineEntity(getEntity(inlineEntity, ResourceFactory.entityClassForFormat(isXML)));
    } else if (link instanceof ODataInlineEntitySet) {
      // append inline entity set
      final CommonODataEntitySet InlineEntitySet = ((ODataInlineEntitySet) link).getEntitySet();
      LOG.debug("Append in-line entity set\n{}", InlineEntitySet);

      linkResource.setInlineEntitySet(getEntitySet(InlineEntitySet, ResourceFactory.entitySetClassForFormat(isXML)));
    }

    return linkResource;
  }

  protected Value getValue(final ODataValue value, final Class<? extends Entity> reference) {
    Value valueResource = null;

    if (value == null) {
      valueResource = new NullValueImpl();
    } else if (value.isPrimitive()) {
      valueResource = value.asPrimitive().getTypeKind().isGeospatial()
              ? new GeospatialValueImpl((Geospatial) value.asPrimitive().toValue())
              : new PrimitiveValueImpl(value.asPrimitive().toString());
    } else if (value.isComplex()) {
      final ODataComplexValue<? extends CommonODataProperty> _value = value.asComplex();
      valueResource = new ComplexValueImpl();

      for (final Iterator<? extends CommonODataProperty> itor = _value.iterator(); itor.hasNext();) {
        valueResource.asComplex().get().add(getProperty(itor.next(), reference));
      }
    } else if (value.isCollection()) {
      final ODataCollectionValue<? extends ODataValue> _value = value.asCollection();
      valueResource = new CollectionValueImpl();

      for (final Iterator<? extends ODataValue> itor = _value.iterator(); itor.hasNext();) {
        valueResource.asCollection().get().add(getValue(itor.next(), reference));
      }
    }

    return valueResource;
  }

  protected abstract boolean add(CommonODataEntitySet entitySet, CommonODataEntity entity);

  @Override
  public CommonODataEntitySet getODataEntitySet(final ResWrap<EntitySet> resource) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      client.getSerializer().entitySet(resource.getPayload(), writer);
      writer.flush();
      LOG.debug("EntitySet -> ODataEntitySet:\n{}", writer.toString());
    }

    final URI base = resource.getContextURL() == null
            ? resource.getPayload().getBaseURI() : resource.getContextURL().getServiceRoot();

    final URI next = resource.getPayload().getNext();

    final CommonODataEntitySet entitySet = next == null
            ? client.getObjectFactory().newEntitySet()
            : client.getObjectFactory().newEntitySet(URIUtils.getURI(base, next.toASCIIString()));

    if (resource.getPayload().getCount() != null) {
      entitySet.setCount(resource.getPayload().getCount());
    }

    for (Entity entityResource : resource.getPayload().getEntities()) {
      add(entitySet, getODataEntity(
              new ResWrap<Entity>(resource.getContextURL(), resource.getMetadataETag(), entityResource)));
    }

    return entitySet;
  }

  protected void odataNavigationLinks(final EdmStructuredType edmType,
          final Linked linked, final ODataLinked odataLinked, final String metadataETag, final URI base) {

    for (Link link : linked.getNavigationLinks()) {
      final Entity inlineEntity = link.getInlineEntity();
      final EntitySet inlineEntitySet = link.getInlineEntitySet();

      if (inlineEntity == null && inlineEntitySet == null) {
        ODataLinkType linkType = null;
        if (edmType != null) {
          final EdmNavigationProperty navProp = edmType.getNavigationProperty(link.getTitle());
          if (navProp != null) {
            linkType = navProp.isCollection()
                    ? ODataLinkType.ENTITY_SET_NAVIGATION
                    : ODataLinkType.ENTITY_NAVIGATION;
          }
        }
        if (linkType == null) {
          linkType = link.getType() == null
                  ? ODataLinkType.ENTITY_NAVIGATION
                  : ODataLinkType.fromString(client.getServiceVersion(), link.getRel(), link.getType());
        }

        odataLinked.addLink(linkType == ODataLinkType.ENTITY_NAVIGATION
                ? client.getObjectFactory().
                newEntityNavigationLink(link.getTitle(), URIUtils.getURI(base, link.getHref()))
                : client.getObjectFactory().
                newEntitySetNavigationLink(link.getTitle(), URIUtils.getURI(base, link.getHref())));
      } else if (inlineEntity != null) {
        odataLinked.addLink(new ODataInlineEntity(client.getServiceVersion(),
                URIUtils.getURI(base, link.getHref()), ODataLinkType.ENTITY_NAVIGATION, link.getTitle(),
                getODataEntity(new ResWrap<Entity>(
                                inlineEntity.getBaseURI() == null ? base : inlineEntity.getBaseURI(),
                                metadataETag,
                                inlineEntity))));
      } else {
        odataLinked.addLink(new ODataInlineEntitySet(client.getServiceVersion(),
                URIUtils.getURI(base, link.getHref()), ODataLinkType.ENTITY_SET_NAVIGATION, link.getTitle(),
                getODataEntitySet(new ResWrap<EntitySet>(
                                inlineEntitySet.getBaseURI() == null ? base : inlineEntitySet.getBaseURI(),
                                metadataETag,
                                inlineEntitySet))));
      }
    }
  }

  /**
   * Infer type name from various sources of information including Edm and context URL, if available.
   *
   * @param contextURL context URL
   * @param metadataETag metadata ETag
   * @return Edm type information
   */
  private EdmEntityType findEntityType(final ContextURL contextURL, final String metadataETag) {
    EdmEntityType entityType = null;

    if (client instanceof EdmEnabledODataClient && contextURL != null) {
      final Edm edm = ((EdmEnabledODataClient) client).getEdm(metadataETag);

      if (contextURL.getDerivedEntity() == null) {
        for (EdmSchema schema : edm.getSchemas()) {
          final EdmEntityContainer container = schema.getEntityContainer();

          EdmBindingTarget bindingTarget =
                  container.getEntitySet(contextURL.getEntitySetOrSingletonOrType());
          if (bindingTarget == null) {
            bindingTarget = container.getSingleton(contextURL.getEntitySetOrSingletonOrType());
          }
          if (bindingTarget != null) {
            if (contextURL.getNavOrPropertyPath() == null) {
              entityType = bindingTarget.getEntityType();
            } else {
              final EdmNavigationProperty navProp = bindingTarget.getEntityType().
                      getNavigationProperty(contextURL.getNavOrPropertyPath());

              entityType = navProp == null
                      ? bindingTarget.getEntityType()
                      : navProp.getType();
            }
          }
        }
      } else {
        entityType = edm.getEntityType(new FullQualifiedName(contextURL.getDerivedEntity()));
      }
    }

    return entityType;
  }

  @Override
  public CommonODataEntity getODataEntity(final ResWrap<Entity> resource) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      client.getSerializer().entity(resource.getPayload(), writer);
      writer.flush();
      LOG.debug("EntityResource -> ODataEntity:\n{}", writer.toString());
    }

    final URI base = resource.getContextURL() == null
            ? resource.getPayload().getBaseURI() : resource.getContextURL().getServiceRoot();

    final EdmEntityType edmType = findEntityType(resource.getContextURL(), resource.getMetadataETag());
    FullQualifiedName typeName = null;
    if (resource.getPayload().getType() == null) {
      if (edmType != null) {
        typeName = edmType.getFullQualifiedName();
      }
    } else {
      typeName = new FullQualifiedName(resource.getPayload().getType());
    }

    final CommonODataEntity entity = resource.getPayload().getSelfLink() == null
            ? client.getObjectFactory().newEntity(typeName)
            : client.getObjectFactory().newEntity(typeName,
                    URIUtils.getURI(base, resource.getPayload().getSelfLink().getHref()));

    if (StringUtils.isNotBlank(resource.getPayload().getETag())) {
      entity.setETag(resource.getPayload().getETag());
    }

    if (resource.getPayload().getEditLink() != null) {
      entity.setEditLink(URIUtils.getURI(base, resource.getPayload().getEditLink().getHref()));
    }

    for (Link link : resource.getPayload().getAssociationLinks()) {
      entity.addLink(client.getObjectFactory().
              newAssociationLink(link.getTitle(), URIUtils.getURI(base, link.getHref())));
    }

    odataNavigationLinks(edmType, resource.getPayload(), entity, resource.getMetadataETag(), base);

    for (Link link : resource.getPayload().getMediaEditLinks()) {
      entity.addLink(new ODataLink.Builder().setVersion(client.getServiceVersion()).
              setURI(URIUtils.getURI(base, link.getHref())).
              setType(ODataLinkType.MEDIA_EDIT).setTitle(link.getTitle()).build());
    }

    for (ODataOperation operation : resource.getPayload().getOperations()) {
      operation.setTarget(URIUtils.getURI(base, operation.getTarget()));
      entity.getOperations().add(operation);
    }

    if (resource.getPayload().isMediaEntity()) {
      entity.setMediaEntity(true);
      entity.setMediaContentSource(resource.getPayload().getMediaContentSource());
      entity.setMediaContentType(resource.getPayload().getMediaContentType());
      entity.setMediaETag(resource.getPayload().getMediaETag());
    }

    for (Property property : resource.getPayload().getProperties()) {
      add(entity, getODataProperty(
              new ResWrap<Property>(resource.getContextURL(), resource.getMetadataETag(), property)));
    }

    return entity;
  }

  protected EdmTypeInfo buildTypeInfo(final ResWrap<Property> resource) {
    FullQualifiedName typeName = null;
    final EdmType entityType = findEntityType(resource.getContextURL(), resource.getMetadataETag());
    if (entityType instanceof EdmStructuredType) {
      final EdmProperty edmProperty = ((EdmStructuredType) entityType).
              getStructuralProperty(resource.getPayload().getName());
      if (edmProperty != null) {
        typeName = edmProperty.getType().getFullQualifiedName();
      }
    }

    EdmTypeInfo typeInfo = null;
    if (typeName == null) {
      if (resource.getPayload().getType() != null) {
        typeInfo = new EdmTypeInfo.Builder().setTypeExpression(resource.getPayload().getType()).build();
      }
    } else {
      typeInfo = new EdmTypeInfo.Builder().setTypeExpression(typeName.toString()).build();
    }
    return typeInfo;
  }

  protected ODataValue getODataValue(final ResWrap<Property> resource) {
    final EdmTypeInfo typeInfo = buildTypeInfo(resource);

    ODataValue value = null;
    if (resource.getPayload().getValue().isPrimitive()) {
      value = client.getObjectFactory().newPrimitiveValueBuilder().
              setText(resource.getPayload().getValue().asPrimitive().get()).
              setType(typeInfo == null
                      ? null
                      : EdmPrimitiveTypeKind.valueOfFQN(
                              client.getServiceVersion(), typeInfo.getFullQualifiedName().toString())).build();
    } else if (resource.getPayload().getValue().isGeospatial()) {
      value = client.getObjectFactory().newPrimitiveValueBuilder().
              setValue(resource.getPayload().getValue().asGeospatial().get()).
              setType(typeInfo == null
                      || EdmPrimitiveTypeKind.Geography.getFullQualifiedName().equals(typeInfo.getFullQualifiedName())
                      || EdmPrimitiveTypeKind.Geometry.getFullQualifiedName().equals(typeInfo.getFullQualifiedName())
                      ? resource.getPayload().getValue().asGeospatial().get().getEdmPrimitiveTypeKind()
                      : EdmPrimitiveTypeKind.valueOfFQN(
                              client.getServiceVersion(), typeInfo.getFullQualifiedName().toString())).build();
    } else if (resource.getPayload().getValue().isComplex()) {
      value = client.getObjectFactory().newComplexValue(typeInfo == null
              ? null : typeInfo.getFullQualifiedName().toString());

      for (Property property : resource.getPayload().getValue().asComplex().get()) {
        value.asComplex().add(getODataProperty(
                new ResWrap<Property>(resource.getContextURL(), resource.getMetadataETag(), property)));
      }
    } else if (resource.getPayload().getValue().isCollection()) {
      value = client.getObjectFactory().newCollectionValue(typeInfo == null
              ? null : "Collection(" + typeInfo.getFullQualifiedName().toString() + ")");

      for (Value _value : resource.getPayload().getValue().asCollection().get()) {
        final JSONPropertyImpl fake = new JSONPropertyImpl();
        fake.setValue(_value);
        value.asCollection().add(getODataValue(
                new ResWrap<Property>(resource.getContextURL(), resource.getMetadataETag(), fake)));
      }
    }

    return value;
  }
}
