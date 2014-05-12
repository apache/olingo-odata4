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
package org.apache.olingo.commons.core.data;

import com.fasterxml.aalto.stax.OutputFactoryImpl;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class AtomSerializer extends AbstractAtomDealer {

  private static final XMLOutputFactory FACTORY = new OutputFactoryImpl();

  private final AtomGeoValueSerializer geoSerializer;

  private final boolean serverMode;

  public AtomSerializer(final ODataServiceVersion version) {
    this(version, false);
  }

  public AtomSerializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version);
    this.geoSerializer = new AtomGeoValueSerializer();
    this.serverMode = serverMode;
  }

  private void collection(final XMLStreamWriter writer, final CollectionValue value) throws XMLStreamException {
    for (Value item : value.get()) {
      if (version.compareTo(ODataServiceVersion.V40) < 0) {
        writer.writeStartElement(Constants.PREFIX_DATASERVICES, Constants.ELEM_ELEMENT,
                version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));
      } else {
        writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ELEM_ELEMENT,
                version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));
      }
      value(writer, item);
      writer.writeEndElement();
    }
  }

  private void value(final XMLStreamWriter writer, final Value value) throws XMLStreamException {
    if (value.isPrimitive()) {
      writer.writeCharacters(value.asPrimitive().get());
    } else if (value.isEnum()) {
      writer.writeCharacters(value.asEnum().get());
    } else if (value.isGeospatial()) {
      this.geoSerializer.serialize(writer, value.asGeospatial().get());
    } else if (value.isCollection()) {
      collection(writer, value.asCollection());
    } else if (value.isComplex()) {
      for (Property property : value.asComplex().get()) {
        property(writer, property, false);
      }
    }
  }

  public void property(final XMLStreamWriter writer, final Property property, final boolean standalone)
          throws XMLStreamException {

    if (version.compareTo(ODataServiceVersion.V40) >= 0 && standalone) {
      writer.writeStartElement(Constants.PREFIX_METADATA, Constants.VALUE,
              version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));
    } else {
      writer.writeStartElement(Constants.PREFIX_DATASERVICES, property.getName(),
              version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));
    }

    if (standalone) {
      namespaces(writer);
    }

    if (StringUtils.isNotBlank(property.getType())) {
      final EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build();
      if (!EdmPrimitiveTypeKind.String.getFullQualifiedName().toString().equals(typeInfo.internal())) {
        writer.writeAttribute(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
                Constants.ATTR_TYPE, typeInfo.external(version));
      }
    }

    if (property.getValue().isNull()) {
      writer.writeAttribute(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.ATTR_NULL, Boolean.TRUE.toString());
    } else {
      value(writer, property.getValue());
      if (property.getValue().isLinkedComplex()) {
        links(writer, property.getValue().asLinkedComplex().getAssociationLinks());
        links(writer, property.getValue().asLinkedComplex().getNavigationLinks());
      }
    }

    writer.writeEndElement();

    for (Annotation annotation : property.getAnnotations()) {
      annotation(writer, annotation, property.getName());
    }
  }

  private void property(final XMLStreamWriter writer, final Property property) throws XMLStreamException {
    property(writer, property, true);
  }

  private void startDocument(final XMLStreamWriter writer, final String rootElement) throws XMLStreamException {
    writer.writeStartDocument();
    writer.setDefaultNamespace(Constants.NS_ATOM);

    writer.writeStartElement(rootElement);

    namespaces(writer);
  }

  private void property(final Writer outWriter, final Property property) throws XMLStreamException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    writer.writeStartDocument();

    property(writer, property);

    writer.writeEndDocument();
    writer.flush();
  }

  private void links(final XMLStreamWriter writer, final List<Link> links) throws XMLStreamException {
    for (Link link : links) {
      writer.writeStartElement(Constants.ATOM_ELEM_LINK);

      if (StringUtils.isNotBlank(link.getRel())) {
        writer.writeAttribute(Constants.ATTR_REL, link.getRel());
      }
      if (StringUtils.isNotBlank(link.getTitle())) {
        writer.writeAttribute(Constants.ATTR_TITLE, link.getTitle());
      }
      if (StringUtils.isNotBlank(link.getHref())) {
        writer.writeAttribute(Constants.ATTR_HREF, link.getHref());
      }
      if (StringUtils.isNotBlank(link.getType())) {
        writer.writeAttribute(Constants.ATTR_TYPE, link.getType());
      }

      if (link.getInlineEntity() != null || link.getInlineEntitySet() != null) {
        writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ATOM_ELEM_INLINE,
                version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));

        if (link.getInlineEntity() != null) {
          writer.writeStartElement(Constants.ATOM_ELEM_ENTRY);
          entity(writer, link.getInlineEntity());
          writer.writeEndElement();
        }
        if (link.getInlineEntitySet() != null) {
          writer.writeStartElement(Constants.ATOM_ELEM_FEED);
          entitySet(writer, link.getInlineEntitySet());
          writer.writeEndElement();
        }

        writer.writeEndElement();
      }

      for (Annotation annotation : link.getAnnotations()) {
        annotation(writer, annotation, null);
      }

      writer.writeEndElement();
    }
  }

  private void common(final XMLStreamWriter writer, final AbstractODataObject object) throws XMLStreamException {
    if (StringUtils.isNotBlank(object.getTitle())) {
      writer.writeStartElement(Constants.ATOM_ELEM_TITLE);
      writer.writeAttribute(Constants.ATTR_TYPE, TYPE_TEXT);
      writer.writeCharacters(object.getTitle());
      writer.writeEndElement();
    }

    if (StringUtils.isNotBlank(object.getSummary())) {
      writer.writeStartElement(Constants.ATOM_ELEM_SUMMARY);
      writer.writeAttribute(Constants.ATTR_TYPE, "text");
      writer.writeCharacters(object.getSummary());
      writer.writeEndElement();
    }
  }

  private void properties(final XMLStreamWriter writer, final List<Property> properties) throws XMLStreamException {
    for (Property property : properties) {
      property(writer, property, false);
    }
  }

  private void annotation(final XMLStreamWriter writer, final Annotation annotation, final String target)
          throws XMLStreamException {

    writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ANNOTATION,
            version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));

    writer.writeAttribute(Constants.ATOM_ATTR_TERM, annotation.getTerm());

    if (target != null) {
      writer.writeAttribute(Constants.ATTR_TARGET, target);
    }

    if (StringUtils.isNotBlank(annotation.getType())) {
      final EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setTypeExpression(annotation.getType()).build();
      if (!EdmPrimitiveTypeKind.String.getFullQualifiedName().toString().equals(typeInfo.internal())) {
        writer.writeAttribute(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
                Constants.ATTR_TYPE, typeInfo.external(version));
      }
    }

    if (annotation.getValue().isNull()) {
      writer.writeAttribute(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.ATTR_NULL, Boolean.TRUE.toString());
    } else {
      value(writer, annotation.getValue());
    }

    writer.writeEndElement();
  }

  private void entity(final XMLStreamWriter writer, final Entity entity) throws XMLStreamException {
    if (entity.getBaseURI() != null) {
      writer.writeAttribute(XMLConstants.XML_NS_URI, Constants.ATTR_XML_BASE, entity.getBaseURI().toASCIIString());
    }

    if (serverMode && StringUtils.isNotBlank(entity.getETag())) {
      writer.writeAttribute(
              version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.ATOM_ATTR_ETAG, entity.getETag());
    }

    if (StringUtils.isNotBlank(entity.getId())) {
      writer.writeStartElement(Constants.ATOM_ELEM_ID);
      writer.writeCharacters(entity.getId());
      writer.writeEndElement();
    }

    writer.writeStartElement(Constants.ATOM_ELEM_CATEGORY);
    writer.writeAttribute(Constants.ATOM_ATTR_SCHEME, version.getNamespaceMap().get(ODataServiceVersion.NS_SCHEME));
    if (StringUtils.isNotBlank(entity.getType())) {
      writer.writeAttribute(Constants.ATOM_ATTR_TERM,
              new EdmTypeInfo.Builder().setTypeExpression(entity.getType()).build().external(version));
    }
    writer.writeEndElement();

    if (entity instanceof AbstractODataObject) {
      common(writer, (AbstractODataObject) entity);
    }

    if (serverMode) {
      if (entity.getEditLink() != null) {
        links(writer, Collections.singletonList(entity.getEditLink()));
      }

      if (entity.getSelfLink() != null) {
        links(writer, Collections.singletonList(entity.getSelfLink()));
      }
    }

    links(writer, entity.getAssociationLinks());
    links(writer, entity.getNavigationLinks());
    links(writer, entity.getMediaEditLinks());

    if (serverMode) {
      for (ODataOperation operation : entity.getOperations()) {
        writer.writeStartElement(
                version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_ACTION);
        writer.writeAttribute(Constants.ATTR_METADATA, operation.getMetadataAnchor());
        writer.writeAttribute(Constants.ATTR_TITLE, operation.getTitle());
        writer.writeAttribute(Constants.ATTR_TARGET, operation.getTarget().toASCIIString());
        writer.writeEndElement();
      }
    }

    writer.writeStartElement(Constants.ATOM_ELEM_CONTENT);
    if (entity.isMediaEntity()) {
      if (StringUtils.isNotBlank(entity.getMediaContentType())) {
        writer.writeAttribute(Constants.ATTR_TYPE, entity.getMediaContentType());
      }
      if (entity.getMediaContentSource() != null) {
        writer.writeAttribute(Constants.ATOM_ATTR_SRC, entity.getMediaContentSource().toASCIIString());
      }
      writer.writeEndElement();

      writer.writeStartElement(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.PROPERTIES);
      properties(writer, entity.getProperties());
    } else {
      writer.writeAttribute(Constants.ATTR_TYPE, ContentType.APPLICATION_XML);
      writer.writeStartElement(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.PROPERTIES);
      properties(writer, entity.getProperties());
      writer.writeEndElement();
    }
    writer.writeEndElement();

    for (Annotation annotation : entity.getAnnotations()) {
      annotation(writer, annotation, null);
    }
  }

  private void entityRef(final XMLStreamWriter writer, final Entity entity) throws XMLStreamException {
    writer.writeStartElement(Constants.ATOM_ELEM_ENTRY_REF);
    writer.writeNamespace(StringUtils.EMPTY, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));
    writer.writeAttribute(Constants.ATOM_ATTR_ID, entity.getId());
  }

  private void entityRef(final XMLStreamWriter writer, final ResWrap<Entity> container) throws XMLStreamException {
    writer.writeStartElement(Constants.ATOM_ELEM_ENTRY_REF);
    writer.writeNamespace(StringUtils.EMPTY, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));
    addContextInfo(writer, container);
    writer.writeAttribute(Constants.ATOM_ATTR_ID, container.getPayload().getId());
  }

  private void entity(final Writer outWriter, final Entity entity) throws XMLStreamException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    if (entity.getType() == null && entity.getProperties().isEmpty()) {
      writer.writeStartDocument();
      writer.setDefaultNamespace(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));

      entityRef(writer, entity);
    } else {
      startDocument(writer, Constants.ATOM_ELEM_ENTRY);

      entity(writer, entity);
    }

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
  }

  private void entity(final Writer outWriter, final ResWrap<Entity> container) throws XMLStreamException {
    final Entity entity = container.getPayload();

    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    if (entity.getType() == null && entity.getProperties().isEmpty()) {
      writer.writeStartDocument();
      writer.setDefaultNamespace(version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA));

      entityRef(writer, container);
    } else {
      startDocument(writer, Constants.ATOM_ELEM_ENTRY);

      addContextInfo(writer, container);

      entity(writer, entity);
    }

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
  }

  private void entitySet(final XMLStreamWriter writer, final EntitySet entitySet) throws XMLStreamException {
    if (entitySet.getBaseURI() != null) {
      writer.writeAttribute(XMLConstants.XML_NS_URI, Constants.ATTR_XML_BASE, entitySet.getBaseURI().toASCIIString());
    }

    if (entitySet.getCount() != null) {
      writer.writeStartElement(
              version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA), Constants.ATOM_ELEM_COUNT);
      writer.writeCharacters(Integer.toString(entitySet.getCount()));
      writer.writeEndElement();
    }

    if (StringUtils.isNotBlank(entitySet.getId())) {
      writer.writeStartElement(Constants.ATOM_ELEM_ID);
      writer.writeCharacters(entitySet.getId());
      writer.writeEndElement();
    }

    if (entitySet instanceof AbstractODataObject) {
      common(writer, (AbstractODataObject) entitySet);
    }

    for (Entity entity : entitySet.getEntities()) {
      if (entity.getType() == null && entity.getProperties().isEmpty()) {
        entityRef(writer, entity);
        writer.writeEndElement();
      } else {
        writer.writeStartElement(Constants.ATOM_ELEM_ENTRY);
        entity(writer, entity);
        writer.writeEndElement();
      }
    }

    if (serverMode) {
      if (entitySet.getNext() != null) {
        final LinkImpl next = new LinkImpl();
        next.setRel(Constants.NEXT_LINK_REL);
        next.setHref(entitySet.getNext().toASCIIString());

        links(writer, Collections.<Link>singletonList(next));
      }
      if (entitySet.getDeltaLink() != null) {
        final LinkImpl next = new LinkImpl();
        next.setRel(Constants.DELTA_LINK_REL);
        next.setHref(entitySet.getDeltaLink().toASCIIString());

        links(writer, Collections.<Link>singletonList(next));
      }
    }
  }

  private void entitySet(final Writer outWriter, final EntitySet entitySet) throws XMLStreamException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    startDocument(writer, Constants.ATOM_ELEM_FEED);

    entitySet(writer, entitySet);

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
  }

  private void entitySet(final Writer outWriter, final ResWrap<EntitySet> entitySet) throws XMLStreamException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    startDocument(writer, Constants.ATOM_ELEM_FEED);

    addContextInfo(writer, entitySet);

    entitySet(writer, entitySet.getPayload());

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
  }

  private void link(final Writer outWriter, final Link link) throws XMLStreamException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    writer.writeStartDocument();

    writer.writeStartElement(Constants.ELEM_LINKS);
    writer.writeDefaultNamespace(version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));

    writer.writeStartElement(Constants.ELEM_URI);
    writer.writeCharacters(link.getHref());
    writer.writeEndElement();

    writer.writeEndElement();

    writer.writeEndDocument();
    writer.flush();
  }

  public <T> void write(final Writer writer, final T obj) throws XMLStreamException {
    if (obj instanceof EntitySet) {
      entitySet(writer, (EntitySet) obj);
    } else if (obj instanceof Entity) {
      entity(writer, (Entity) obj);
    } else if (obj instanceof Property) {
      property(writer, (Property) obj);
    } else if (obj instanceof Link) {
      link(writer, (Link) obj);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> void write(final Writer writer, final ResWrap<T> container) throws XMLStreamException {
    final T obj = container == null ? null : container.getPayload();

    if (obj instanceof EntitySet) {
      this.entitySet(writer, (ResWrap<EntitySet>) container);
    } else if (obj instanceof Entity) {
      entity(writer, (ResWrap<Entity>) container);
    } else if (obj instanceof Property) {
      property(writer, (Property) obj);
    } else if (obj instanceof Link) {
      link(writer, (Link) obj);
    }
  }

  private <T> void addContextInfo(
          final XMLStreamWriter writer, final ResWrap<T> container) throws XMLStreamException {

    if (container.getContextURL() != null) {
      String base = container.getContextURL().getServiceRoot().toASCIIString();
      if (container.getPayload() instanceof AtomEntitySetImpl) {
        ((AtomEntitySetImpl) container.getPayload()).setBaseURI(base);
      }
      if (container.getPayload() instanceof AtomEntityImpl) {
        ((AtomEntityImpl) container.getPayload()).setBaseURI(base);
      }

      writer.writeAttribute(
              version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.CONTEXT,
              container.getContextURL().getURI().toASCIIString());
    }

    if (StringUtils.isNotBlank(container.getMetadataETag())) {
      writer.writeAttribute(
              version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.ATOM_ATTR_METADATAETAG,
              container.getMetadataETag());
    }
  }
}
