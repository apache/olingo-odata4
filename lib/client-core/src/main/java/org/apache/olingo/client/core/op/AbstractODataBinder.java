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
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Feed;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
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

  protected final CommonODataClient client;

  protected AbstractODataBinder(final CommonODataClient client) {
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
  public Feed getFeed(final CommonODataEntitySet entitySet, final Class<? extends Feed> reference) {
    final Feed feed = ResourceFactory.newFeed(reference);

    feed.setCount(entitySet.getCount());

    final URI next = entitySet.getNext();
    if (next != null) {
      feed.setNext(next);
    }

    for (CommonODataEntity entity : entitySet.getEntities()) {
      feed.getEntries().add(getEntry(entity, ResourceFactory.entryClassForFeed(reference)));
    }

    return feed;
  }

  @Override
  public Entry getEntry(final CommonODataEntity entity, final Class<? extends Entry> reference) {
    return getEntry(entity, reference, true);
  }

  @Override
  public Entry getEntry(final CommonODataEntity entity, final Class<? extends Entry> reference, final boolean setType) {
    final Entry entry = ResourceFactory.newEntry(reference);

    entry.setType(entity.getName());

    // -------------------------------------------------------------
    // Add edit and self link
    // -------------------------------------------------------------
    final URI editLink = entity.getEditLink();
    if (editLink != null) {
      final LinkImpl entryEditLink = new LinkImpl();
      entryEditLink.setTitle(entity.getName());
      entryEditLink.setHref(editLink.toASCIIString());
      entryEditLink.setRel(Constants.EDIT_LINK_REL);
      entry.setEditLink(entryEditLink);
    }

    if (entity.isReadOnly()) {
      final LinkImpl entrySelfLink = new LinkImpl();
      entrySelfLink.setTitle(entity.getName());
      entrySelfLink.setHref(entity.getLink().toASCIIString());
      entrySelfLink.setRel(Constants.SELF_LINK_REL);
      entry.setSelfLink(entrySelfLink);
    }
    // -------------------------------------------------------------

    // -------------------------------------------------------------
    // Append navigation links (handling inline entry / feed as well)
    // -------------------------------------------------------------
    // handle navigation links
    for (ODataLink link : entity.getNavigationLinks()) {
      // append link 
      LOG.debug("Append navigation link\n{}", link);
      entry.getNavigationLinks().add(getLink(link,
              ResourceFactory.formatForEntryClass(reference) == ODataPubFormat.ATOM));
    }
    // -------------------------------------------------------------

    // -------------------------------------------------------------
    // Append edit-media links
    // -------------------------------------------------------------
    for (ODataLink link : entity.getEditMediaLinks()) {
      LOG.debug("Append edit-media link\n{}", link);
      entry.getMediaEditLinks().add(getLink(link,
              ResourceFactory.formatForEntryClass(reference) == ODataPubFormat.ATOM));
    }
    // -------------------------------------------------------------

    // -------------------------------------------------------------
    // Append association links
    // -------------------------------------------------------------
    for (ODataLink link : entity.getAssociationLinks()) {
      LOG.debug("Append association link\n{}", link);
      entry.getAssociationLinks().add(getLink(link,
              ResourceFactory.formatForEntryClass(reference) == ODataPubFormat.ATOM));
    }
    // -------------------------------------------------------------

    if (entity.isMediaEntity()) {
      entry.setMediaContentSource(entity.getMediaContentSource());
      entry.setMediaContentType(entity.getMediaContentType());
    }

    for (CommonODataProperty property : entity.getProperties()) {
      entry.getProperties().add(getProperty(property, reference, setType));
    }

    return entry;
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

      linkResource.setInlineEntry(getEntry(inlineEntity, ResourceFactory.entryClassForFormat(isXML)));
    } else if (link instanceof ODataInlineEntitySet) {
      // append inline feed
      final CommonODataEntitySet InlineFeed = ((ODataInlineEntitySet) link).getEntitySet();
      LOG.debug("Append in-line feed\n{}", InlineFeed);

      linkResource.setInlineFeed(getFeed(InlineFeed, ResourceFactory.feedClassForFormat(isXML)));
    }

    return linkResource;
  }

  protected Value getValue(final ODataValue value, final Class<? extends Entry> reference, final boolean setType) {
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
        valueResource.asComplex().get().add(getProperty(itor.next(), reference, setType));
      }
    } else if (value.isCollection()) {
      final ODataCollectionValue<? extends ODataValue> _value = value.asCollection();
      valueResource = new CollectionValueImpl();

      for (final Iterator<? extends ODataValue> itor = _value.iterator(); itor.hasNext();) {
        valueResource.asCollection().get().add(getValue(itor.next(), reference, setType));
      }
    }

    return valueResource;
  }

  @Override
  public CommonODataEntitySet getODataEntitySet(final Feed resource) {
    return getODataEntitySet(resource, null);
  }

  protected abstract boolean add(CommonODataEntitySet entitySet, CommonODataEntity entity);

  @Override
  public CommonODataEntitySet getODataEntitySet(final Feed resource, final URI defaultBaseURI) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      client.getSerializer().feed(resource, writer);
      writer.flush();
      LOG.debug("Feed -> ODataEntitySet:\n{}", writer.toString());
    }

    final URI base = defaultBaseURI == null ? resource.getBaseURI() : defaultBaseURI;

    final URI next = resource.getNext();

    final CommonODataEntitySet entitySet = next == null
            ? client.getObjectFactory().newEntitySet()
            : client.getObjectFactory().newEntitySet(URIUtils.getURI(base, next.toASCIIString()));

    if (resource.getCount() != null) {
      entitySet.setCount(resource.getCount());
    }

    for (Entry entryResource : resource.getEntries()) {
      add(entitySet, getODataEntity(entryResource));
    }

    return entitySet;
  }

  @Override
  public CommonODataEntity getODataEntity(final Entry resource) {
    return getODataEntity(resource, null);
  }

  @Override
  public CommonODataEntity getODataEntity(final Entry resource, final URI defaultBaseURI) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      client.getSerializer().entry(resource, writer);
      writer.flush();
      LOG.debug("EntryResource -> ODataEntity:\n{}", writer.toString());
    }

    final URI base = defaultBaseURI == null ? resource.getBaseURI() : defaultBaseURI;

    final CommonODataEntity entity = resource.getSelfLink() == null
            ? client.getObjectFactory().newEntity(resource.getType())
            : client.getObjectFactory().newEntity(resource.getType(),
                    URIUtils.getURI(base, resource.getSelfLink().getHref()));

    if (StringUtils.isNotBlank(resource.getETag())) {
      entity.setETag(resource.getETag());
    }

    if (resource.getEditLink() != null) {
      entity.setEditLink(URIUtils.getURI(base, resource.getEditLink().getHref()));
    }

    for (Link link : resource.getAssociationLinks()) {
      entity.addLink(client.getObjectFactory().newAssociationLink(link.getTitle(), base, link.getHref()));
    }

    for (Link link : resource.getNavigationLinks()) {
      final Entry inlineEntry = link.getInlineEntry();
      final Feed inlineFeed = link.getInlineFeed();

      if (inlineEntry == null && inlineFeed == null) {
        final ODataLinkType linkType = link.getType() == null
                ? ODataLinkType.ENTITY_NAVIGATION
                : ODataLinkType.fromString(client.getServiceVersion(), link.getRel(), link.getType());
        entity.addLink(linkType == ODataLinkType.ENTITY_NAVIGATION
                ? client.getObjectFactory().newEntityNavigationLink(link.getTitle(), base, link.getHref())
                : client.getObjectFactory().newEntitySetNavigationLink(link.getTitle(), base, link.getHref()));
      } else if (inlineEntry != null) {
        entity.addLink(client.getObjectFactory().newInlineEntity(
                link.getTitle(), base, link.getHref(),
                getODataEntity(inlineEntry,
                        inlineEntry.getBaseURI() == null ? base : inlineEntry.getBaseURI())));
      } else {
        entity.addLink(client.getObjectFactory().newInlineEntitySet(
                link.getTitle(), base, link.getHref(),
                getODataEntitySet(inlineFeed,
                        inlineFeed.getBaseURI() == null ? base : inlineFeed.getBaseURI())));
      }
    }

    for (Link link : resource.getMediaEditLinks()) {
      entity.addLink(client.getObjectFactory().newMediaEditLink(link.getTitle(), base, link.getHref()));
    }

    for (ODataOperation operation : resource.getOperations()) {
      operation.setTarget(URIUtils.getURI(base, operation.getTarget()));
      entity.getOperations().add(operation);
    }

    if (resource.isMediaEntry()) {
      entity.setMediaEntity(true);
      entity.setMediaContentSource(resource.getMediaContentSource());
      entity.setMediaContentType(resource.getMediaContentType());
    }

    for (Property property : resource.getProperties()) {
      add(entity, getODataProperty(property));
    }

    return entity;
  }

  protected ODataValue getODataValue(final Property resource) {
    ODataValue value = null;

    final EdmTypeInfo typeInfo = resource.getType() == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(resource.getType()).build();
    if (resource.getValue().isPrimitive()) {
      value = client.getObjectFactory().newPrimitiveValueBuilder().
              setText(resource.getValue().asPrimitive().get()).
              setType(typeInfo == null
                      ? null
                      : EdmPrimitiveTypeKind.valueOfFQN(
                              client.getServiceVersion(), typeInfo.getFullQualifiedName().toString())).build();
    } else if (resource.getValue().isGeospatial()) {
      value = client.getObjectFactory().newPrimitiveValueBuilder().
              setValue(resource.getValue().asGeospatial().get()).
              setType(typeInfo == null
                      || EdmPrimitiveTypeKind.Geography.getFullQualifiedName().equals(typeInfo.getFullQualifiedName())
                      || EdmPrimitiveTypeKind.Geometry.getFullQualifiedName().equals(typeInfo.getFullQualifiedName())
                      ? resource.getValue().asGeospatial().get().getEdmPrimitiveTypeKind()
                      : EdmPrimitiveTypeKind.valueOfFQN(
                              client.getServiceVersion(), typeInfo.getFullQualifiedName().toString())).build();
    } else if (resource.getValue().isComplex()) {
      value = client.getObjectFactory().newComplexValue(typeInfo == null
              ? null : typeInfo.getFullQualifiedName().toString());

      for (Property property : resource.getValue().asComplex().get()) {
        value.asComplex().add(getODataProperty(property));
      }
    } else if (resource.getValue().isCollection()) {
      value = client.getObjectFactory().newCollectionValue(typeInfo == null
              ? null : "Collection(" + typeInfo.getFullQualifiedName().toString() + ")");

      for (Value _value : resource.getValue().asCollection().get()) {
        final JSONPropertyImpl fake = new JSONPropertyImpl();
        fake.setValue(_value);
        value.asCollection().add(getODataValue(fake));
      }
    }

    return value;
  }
}
