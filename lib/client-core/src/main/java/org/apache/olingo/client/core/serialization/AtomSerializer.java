/*
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
 */
package org.apache.olingo.client.core.serialization;

import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.serialization.ODataSerializer;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractODataObject;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

import com.fasterxml.aalto.stax.OutputFactoryImpl;

public class AtomSerializer implements ODataSerializer {

  private static final String TYPE_TEXT = "text";

  private static final XMLOutputFactory FACTORY = new OutputFactoryImpl();

  private final AtomGeoValueSerializer geoSerializer;
  private final boolean serverMode;

  public AtomSerializer() {
    this(false);
  }

  public AtomSerializer(final boolean serverMode) {
    geoSerializer = new AtomGeoValueSerializer();
    this.serverMode = serverMode;
  }

  protected void namespaces(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeNamespace("", Constants.NS_ATOM);
    writer.writeNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
    writer.writeNamespace(Constants.PREFIX_METADATA, Constants.NS_METADATA);
    writer.writeNamespace(Constants.PREFIX_DATASERVICES, Constants.NS_DATASERVICES);
    writer.writeNamespace(Constants.PREFIX_GML, Constants.NS_GML);
    writer.writeNamespace(Constants.PREFIX_GEORSS, Constants.NS_GEORSS);
  }

  private void collection(final XMLStreamWriter writer,
      final ValueType valueType, final EdmPrimitiveTypeKind kind, final List<?> value)
          throws XMLStreamException, EdmPrimitiveTypeException {
    for (Object item : value) {
      writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ELEM_ELEMENT, Constants.NS_METADATA);
      value(writer, valueType, kind, item);
      writer.writeEndElement();
    }
  }

  private void value(final XMLStreamWriter writer,
      final ValueType valueType, final EdmPrimitiveTypeKind kind, final Object value)
          throws XMLStreamException, EdmPrimitiveTypeException {
    if (value == null || (valueType == ValueType.COMPLEX && ((ComplexValue) value).getValue().isEmpty())) {
      writer.writeAttribute(Constants.PREFIX_METADATA, Constants.NS_METADATA,
          Constants.ATTR_NULL, Boolean.TRUE.toString());
      return;
    }
    switch (valueType) {
    case PRIMITIVE:
      final EdmPrimitiveTypeKind valueKind = kind == null ? EdmTypeInfo.determineTypeKind(value) : kind;
      if (valueKind == null) {
        if (serverMode) {
          throw new EdmPrimitiveTypeException("The primitive type could not be determined.");
        } else {
          writer.writeCharacters(value.toString()); // This might not be valid OData.
        }
      } else {
        writer.writeCharacters(EdmPrimitiveTypeFactory.getInstance(valueKind) // TODO: add facets
            .valueToString(value, null, null, Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null));
      }
      break;
    case ENUM:
      writer.writeCharacters(value.toString());
      break;
    case GEOSPATIAL:
      geoSerializer.serialize(writer, (Geospatial) value);
      break;
    case COLLECTION_PRIMITIVE:
    case COLLECTION_GEOSPATIAL:
    case COLLECTION_ENUM:
    case COLLECTION_COMPLEX:
      collection(writer, valueType.getBaseType(), kind, (List<?>) value);
      break;
    case COMPLEX:
      if (((ComplexValue) value).getTypeName() != null) {
        EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().
            setTypeExpression(((ComplexValue) value).getTypeName()).build();
        writer.writeAttribute(Constants.PREFIX_METADATA, Constants.NS_METADATA,
            Constants.ATTR_TYPE, typeInfo.external());
      }
      for (Property property : ((ComplexValue) value).getValue()) {
        property(writer, property, false);
      }
      break;
    case ENTITY:
    case COLLECTION_ENTITY:
      throw new ODataRuntimeException("Entities cannot appear in this payload");
    }
  }

  public void property(final XMLStreamWriter writer, final Property property, final boolean standalone)
      throws XMLStreamException, EdmPrimitiveTypeException {

    if (standalone) {
      writer.writeStartElement(Constants.PREFIX_METADATA, Constants.VALUE, Constants.NS_DATASERVICES);
      namespaces(writer);
    } else {
      writer.writeStartElement(Constants.PREFIX_DATASERVICES, property.getName(), Constants.NS_DATASERVICES);
    }

    EdmTypeInfo typeInfo = null;
    if (property.getType() != null && !"COMPLEX".equalsIgnoreCase(property.getValueType().name())) {
      typeInfo = new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build();
      if (!EdmPrimitiveTypeKind.String.getFullQualifiedName().toString().equals(typeInfo.internal())) {
        writer.writeAttribute(Constants.PREFIX_METADATA, Constants.NS_METADATA,
            Constants.ATTR_TYPE, typeInfo.external());
      }
    }

    value(writer, property.getValueType(),
        typeInfo == null ? null : typeInfo.getPrimitiveTypeKind(),
        property.getValue());
    if (!property.isNull() && property.isComplex() && !property.isCollection()) {
      links(writer, property.asComplex().getAssociationLinks());
      if (serverMode) {
        links(writer, property.asComplex().getNavigationLinks());
      } else {
        writeNavigationLinks(writer, property.asComplex().getNavigationLinks());
      }
    }

    writer.writeEndElement();

    for (Annotation annotation : property.getAnnotations()) {
      annotation(writer, annotation, property.getName());
    }
  }

  private void property(final XMLStreamWriter writer, final Property property)
      throws XMLStreamException, EdmPrimitiveTypeException {
    property(writer, property, true);
  }

  private void startDocument(final XMLStreamWriter writer, final String rootElement) throws XMLStreamException {
    writer.writeStartDocument();
    writer.setDefaultNamespace(Constants.NS_ATOM);
    writer.writeStartElement(rootElement);

    namespaces(writer);
  }

  private void property(final Writer outWriter, final Property property)
      throws XMLStreamException, EdmPrimitiveTypeException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    writer.writeStartDocument();

    property(writer, property);

    writer.writeEndDocument();
    writer.flush();
  }
  
  private boolean isEntitySetNavigation(final Link link) {
    return Constants.ENTITY_SET_NAVIGATION_LINK_TYPE.equals(link.getType());
  }
  
  private void writeNavigationLinks(final XMLStreamWriter writer, final List<Link> links)
      throws XMLStreamException, EdmPrimitiveTypeException {
    final Map<String, List<String>> entitySetLinks = new HashMap<String, List<String>>();

    for (Link link : links) {
    
      if (link.getInlineEntity() != null || link.getInlineEntitySet() != null) {
        writeLink(writer, link, new ExtraContent() {
          @Override
          public void write(XMLStreamWriter writer, Link link) throws XMLStreamException, EdmPrimitiveTypeException {
            writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ATOM_ELEM_INLINE, Constants.NS_METADATA);
            if (link.getInlineEntity() != null) {
              writer.writeStartElement(Constants.NS_ATOM, Constants.ATOM_ELEM_ENTRY);
              entity(writer, link.getInlineEntity());
              writer.writeEndElement();
            }
            if (link.getInlineEntitySet() != null) {
              writer.writeStartElement(Constants.NS_ATOM, Constants.ATOM_ELEM_FEED);
              entitySet(writer, link.getInlineEntitySet());
              writer.writeEndElement();
            }
            writer.writeEndElement(); // inline    
          }
        });
        
      } else if (link.getBindingLink() != null) {
        writeLink(writer, link, new ExtraContent() {
          @Override
          public void write(XMLStreamWriter writer, Link link) throws XMLStreamException, EdmPrimitiveTypeException {
            writer.writeAttribute(Constants.ATTR_HREF, link.getBindingLink());
          }
        });
      } else if (link.getBindingLinks() != null && !link.getBindingLinks().isEmpty()) {
        writeLink(writer, link, new ExtraContent() {
          @Override
          public void write(XMLStreamWriter writer, Link link) throws XMLStreamException, EdmPrimitiveTypeException {
            writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ATOM_ELEM_INLINE, Constants.NS_METADATA);
            writer.writeStartElement(Constants.NS_ATOM, Constants.ATOM_ELEM_FEED);
            for (String binding:link.getBindingLinks()) {            
              Entity entity = new Entity();
              entity.setId(URI.create(binding));
              inlineEntityRef(writer, entity);                      
            }
            writer.writeEndElement(); //feed            
            writer.writeEndElement(); //inline
          }
        });
      } else {
        if (isEntitySetNavigation(link)) {
          final List<String> uris;
          if (entitySetLinks.containsKey(link.getTitle())) {
            uris = entitySetLinks.get(link.getTitle());
          } else {
            uris = new ArrayList<String>();
            entitySetLinks.put(link.getTitle(), uris);
          }
          if (link.getHref() != null) {
            uris.add(link.getHref());
          }
        } else {
          writeLink(writer, link, new ExtraContent() {
            @Override
            public void write(XMLStreamWriter writer, Link link) 
                throws XMLStreamException, EdmPrimitiveTypeException {
            }
          });
        }
      }
    }
    for (Entry<String, List<String>> entry : entitySetLinks.entrySet()) {
      final List<String>entitySetLink = entry.getValue();
      if (!entitySetLink.isEmpty()) {
        Link link = new Link();
        link.setTitle(entry.getKey());
        link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
        link.setRel(Constants.NS_NAVIGATION_LINK_REL+entry.getKey());

        writeLink(writer, link, new ExtraContent() {
          @Override
          public void write(XMLStreamWriter writer, Link link) throws XMLStreamException, EdmPrimitiveTypeException {
            writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ATOM_ELEM_INLINE, Constants.NS_METADATA);
            writer.writeStartElement(Constants.NS_ATOM, Constants.ATOM_ELEM_FEED);
            for (String binding:entitySetLink) {            
              Entity entity = new Entity();
              entity.setId(URI.create(binding));
              inlineEntityRef(writer, entity);                      
            }
            writer.writeEndElement();    
            writer.writeEndElement();
          }
        });                
      }
    }   
  }

  private void links(final XMLStreamWriter writer, final List<Link> links)
      throws XMLStreamException, EdmPrimitiveTypeException {
    for (Link link : links) {
      writeLink(writer, link, new ExtraContent() {
        @Override
        public void write(XMLStreamWriter writer, Link link) 
            throws XMLStreamException, EdmPrimitiveTypeException {
        }
      });
    }
  }
  
  interface ExtraContent {
    void write(final XMLStreamWriter writer, final Link link) 
        throws XMLStreamException, EdmPrimitiveTypeException;
  }
  
  private void writeLink(final XMLStreamWriter writer, final Link link, final ExtraContent content)
      throws XMLStreamException, EdmPrimitiveTypeException {
    writer.writeStartElement(Constants.ATOM_ELEM_LINK);

    if (link.getRel() != null) {
      writer.writeAttribute(Constants.ATTR_REL, link.getRel());
    }
    if (link.getTitle() != null) {
      writer.writeAttribute(Constants.ATTR_TITLE, link.getTitle());
    }
    if (link.getHref() != null) {
      writer.writeAttribute(Constants.ATTR_HREF, link.getHref());
    }
    if (link.getType() != null) {
      writer.writeAttribute(Constants.ATTR_TYPE, link.getType());
    }

    content.write(writer, link);

    for (Annotation annotation : link.getAnnotations()) {
      annotation(writer, annotation, null);
    }
    writer.writeEndElement();    
  }  

  private void common(final XMLStreamWriter writer, final AbstractODataObject object) throws XMLStreamException {
    if (object.getTitle() != null) {
      writer.writeStartElement(Constants.ATOM_ELEM_TITLE);
      writer.writeAttribute(Constants.ATTR_TYPE, TYPE_TEXT);
      writer.writeCharacters(object.getTitle());
      writer.writeEndElement();
    }
  }

  private void properties(final XMLStreamWriter writer, final List<Property> properties)
      throws XMLStreamException, EdmPrimitiveTypeException {
    for (Property property : properties) {
      property(writer, property, false);
    }
  }

  private void annotation(final XMLStreamWriter writer, final Annotation annotation, final String target)
      throws XMLStreamException, EdmPrimitiveTypeException {

    writer.writeStartElement(Constants.PREFIX_METADATA, Constants.ANNOTATION, Constants.NS_METADATA);

    writer.writeAttribute(Constants.ATOM_ATTR_TERM, annotation.getTerm());

    if (target != null) {
      writer.writeAttribute(Constants.ATTR_TARGET, target);
    }

    EdmTypeInfo typeInfo = null;
    if (annotation.getType() != null) {
      typeInfo = new EdmTypeInfo.Builder().setTypeExpression(annotation.getType()).build();
      if (!EdmPrimitiveTypeKind.String.getFullQualifiedName().toString().equals(typeInfo.internal())) {
        writer.writeAttribute(Constants.PREFIX_METADATA, Constants.NS_METADATA,
            Constants.ATTR_TYPE, typeInfo.external());
      }
    }

    value(writer, annotation.getValueType(),
        typeInfo == null ? EdmTypeInfo.determineTypeKind(annotation.getValue()) : typeInfo.getPrimitiveTypeKind(),
        annotation.getValue());

    writer.writeEndElement();
  }

  private void entity(final XMLStreamWriter writer, final Entity entity)
      throws XMLStreamException, EdmPrimitiveTypeException {
    if (entity.getBaseURI() != null) {
      writer.writeAttribute(XMLConstants.XML_NS_URI, Constants.ATTR_XML_BASE, entity.getBaseURI().toASCIIString());
    }

    if (serverMode && entity.getETag() != null) {
      writer.writeAttribute(Constants.NS_METADATA, Constants.ATOM_ATTR_ETAG, entity.getETag());
    }

    if (entity.getId() != null) {
      writer.writeStartElement(Constants.ATOM_ELEM_ID);
      writer.writeCharacters(entity.getId().toASCIIString());
      writer.writeEndElement();
    }

    writer.writeStartElement(Constants.ATOM_ELEM_CATEGORY);
    writer.writeAttribute(Constants.ATOM_ATTR_SCHEME, Constants.NS_SCHEME);
    if (entity.getType() != null) {
      writer.writeAttribute(Constants.ATOM_ATTR_TERM,
          new EdmTypeInfo.Builder().setTypeExpression(entity.getType()).build().external());
    }
    writer.writeEndElement();

    common(writer, entity);

    if (serverMode) {
      if (entity.getEditLink() != null) {
        links(writer, Collections.singletonList(entity.getEditLink()));
      }

      if (entity.getSelfLink() != null) {
        links(writer, Collections.singletonList(entity.getSelfLink()));
      }
    }

    links(writer, entity.getAssociationLinks());
    if (serverMode) {
      links(writer, entity.getNavigationLinks());
    } else {
      writeNavigationLinks(writer, entity.getNavigationLinks());
      writeNavigationLinks(writer, entity.getNavigationBindings());
    }
    links(writer, entity.getMediaEditLinks());

    if (serverMode) {
      for (Operation operation : entity.getOperations()) {
        writer.writeStartElement(Constants.NS_METADATA, Constants.ATOM_ELEM_ACTION);
        writer.writeAttribute(Constants.ATTR_METADATA, operation.getMetadataAnchor());
        writer.writeAttribute(Constants.ATTR_TITLE, operation.getTitle());
        writer.writeAttribute(Constants.ATTR_TARGET, operation.getTarget().toASCIIString());
        writer.writeEndElement();
      }
    }

    writer.writeStartElement(Constants.ATOM_ELEM_CONTENT);
    if (entity.isMediaEntity()) {
      if (entity.getMediaContentType() != null) {
        writer.writeAttribute(Constants.ATTR_TYPE, entity.getMediaContentType());
      }
      if (entity.getMediaContentSource() != null) {
        writer.writeAttribute(Constants.ATOM_ATTR_SRC, entity.getMediaContentSource().toASCIIString());
      }
      writer.writeEndElement();

      writer.writeStartElement(Constants.NS_METADATA, Constants.PROPERTIES);
      properties(writer, entity.getProperties());
    } else {
      writer.writeAttribute(Constants.ATTR_TYPE, ContentType.APPLICATION_XML.toContentTypeString());
      writer.writeStartElement(Constants.NS_METADATA, Constants.PROPERTIES);
      properties(writer, entity.getProperties());
      writer.writeEndElement();
    }
    writer.writeEndElement();

    for (Annotation annotation : entity.getAnnotations()) {
      annotation(writer, annotation, null);
    }
  }

  private void inlineEntityRef(final XMLStreamWriter writer, final Entity entity) throws XMLStreamException {
    writer.writeStartElement(Constants.NS_METADATA, Constants.ATOM_ELEM_ENTRY_REF);
    writer.writeAttribute(Constants.ATOM_ATTR_ID, entity.getId().toASCIIString());
    writer.writeEndElement();
  }
  
  private void entityRef(final XMLStreamWriter writer, final Entity entity) throws XMLStreamException {
    writer.writeStartElement(Constants.ATOM_ELEM_ENTRY_REF);
    writer.writeDefaultNamespace(Constants.NS_METADATA);
    writer.writeAttribute(Constants.ATOM_ATTR_ID, entity.getId().toASCIIString());
    writer.writeEndElement();
  }

  private void entityRef(final XMLStreamWriter writer, final ResWrap<Entity> container) throws XMLStreamException {
    writer.writeStartElement(Constants.ATOM_ELEM_ENTRY_REF);
    writer.writeDefaultNamespace(Constants.NS_METADATA);
    addContextInfo(writer, container);
    writer.writeAttribute(Constants.ATOM_ATTR_ID, container.getPayload().getId().toASCIIString());
  }

  private void entity(final Writer outWriter, final Entity entity)
      throws XMLStreamException, EdmPrimitiveTypeException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    if (entity.getType() == null && entity.getProperties().isEmpty()) {
      writer.writeStartDocument();
      writer.setDefaultNamespace(Constants.NS_METADATA);
      entityRef(writer, entity);
    } else {
      startDocument(writer, Constants.ATOM_ELEM_ENTRY);
      entity(writer, entity);
    }
    writer.writeEndDocument();
    writer.flush();
  }

  private void entity(final Writer outWriter, final ResWrap<Entity> container)
      throws XMLStreamException, EdmPrimitiveTypeException {
    final Entity entity = container.getPayload();

    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    if (entity.getType() == null && entity.getProperties().isEmpty()) {
      writer.writeStartDocument();
      writer.setDefaultNamespace(Constants.NS_METADATA);

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

  private void entitySet(final XMLStreamWriter writer, final EntityCollection entitySet)
      throws XMLStreamException, EdmPrimitiveTypeException {
    if (entitySet.getBaseURI() != null) {
      writer.writeAttribute(XMLConstants.XML_NS_URI, Constants.ATTR_XML_BASE, entitySet.getBaseURI().toASCIIString());
    }

    if (entitySet.getCount() != null) {
      writer.writeStartElement(Constants.NS_METADATA, Constants.ATOM_ELEM_COUNT);
      writer.writeCharacters(Integer.toString(entitySet.getCount()));
      writer.writeEndElement();
    }

    if (entitySet.getId() != null) {
      writer.writeStartElement(Constants.ATOM_ELEM_ID);
      writer.writeCharacters(entitySet.getId().toASCIIString());
      writer.writeEndElement();
    }

    common(writer, entitySet);

    for (Entity entity : entitySet) {
      if (entity.getType() == null && entity.getProperties().isEmpty()) {
        entityRef(writer, entity);
      } else {
        writer.writeStartElement(Constants.ATOM_ELEM_ENTRY);
        entity(writer, entity);
        writer.writeEndElement();
      }
    }

    if (serverMode) {
      if (entitySet.getNext() != null) {
        final Link next = new Link();
        next.setRel(Constants.NEXT_LINK_REL);
        next.setHref(entitySet.getNext().toASCIIString());

        links(writer, Collections.<Link> singletonList(next));
      }
      if (entitySet.getDeltaLink() != null) {
        final Link next = new Link();
        next.setRel(Constants.NS_DELTA_LINK_REL);
        next.setHref(entitySet.getDeltaLink().toASCIIString());

        links(writer, Collections.<Link> singletonList(next));
      }
    }
  }

  private void entitySet(final Writer outWriter, final EntityCollection entitySet)
      throws XMLStreamException, EdmPrimitiveTypeException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    startDocument(writer, Constants.ATOM_ELEM_FEED);

    entitySet(writer, entitySet);

    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
  }

  private void entitySet(final Writer outWriter, final ResWrap<EntityCollection> entitySet)
      throws XMLStreamException, EdmPrimitiveTypeException {
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
    writer.writeDefaultNamespace(Constants.NS_DATASERVICES);

    writer.writeStartElement(Constants.ELEM_URI);
    writer.writeCharacters(link.getHref());
    writer.writeEndElement();

    writer.writeEndElement();

    writer.writeEndDocument();
    writer.flush();
  }

  @Override
  public <T> void write(final Writer writer, final T obj) throws ODataSerializerException {
    try {
      if (obj instanceof EntityCollection) {
        entitySet(writer, (EntityCollection) obj);
      } else if (obj instanceof Entity) {
        entity(writer, (Entity) obj);
      } else if (obj instanceof Property) {
        property(writer, (Property) obj);
      } else if (obj instanceof Link) {
        link(writer, (Link) obj);
      }
    } catch (final XMLStreamException e) {
      throw new ODataSerializerException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataSerializerException(e);
    }
  }

  private void reference(final Writer outWriter, final ResWrap<URI> container) throws XMLStreamException {
    final XMLStreamWriter writer = FACTORY.createXMLStreamWriter(outWriter);

    writer.writeStartDocument();

    writer.writeStartElement(Constants.ATTR_METADATA, Constants.ATTR_REF, Constants.NS_METADATA);
    writer.writeNamespace(Constants.ATTR_METADATA, Constants.NS_METADATA);
    writer.writeAttribute(Constants.ATTR_METADATA, Constants.NS_METADATA, Constants.CONTEXT, 
        container.getContextURL().toASCIIString());
    writer.writeAttribute(Constants.ATOM_ATTR_ID, container.getPayload().toASCIIString());
    writer.writeEndElement();

    writer.writeEndDocument();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> void write(final Writer writer, final ResWrap<T> container) throws ODataSerializerException {
    final T obj = container == null ? null : container.getPayload();

    try {
      if (obj instanceof EntityCollection) {
        this.entitySet(writer, (ResWrap<EntityCollection>) container);
      } else if (obj instanceof Entity) {
        entity(writer, (ResWrap<Entity>) container);
      } else if (obj instanceof Property) {
        property(writer, (Property) obj);
      } else if (obj instanceof Link) {
        link(writer, (Link) obj);
      } else if (obj instanceof URI) {
        reference(writer, (ResWrap<URI>) container);
      }
    } catch (final XMLStreamException e) {
      throw new ODataSerializerException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataSerializerException(e);
    }
  }

  private <T> void addContextInfo(
      final XMLStreamWriter writer, final ResWrap<T> container) throws XMLStreamException {

    if (container.getContextURL() != null) {
      final ContextURL contextURL = ContextURLParser.parse(container.getContextURL());
      final URI base = contextURL.getServiceRoot();
      if (container.getPayload() instanceof EntityCollection) {
        ((EntityCollection) container.getPayload()).setBaseURI(base);
      }
      if (container.getPayload() instanceof Entity) {
        ((Entity) container.getPayload()).setBaseURI(base);
      }

      writer.writeAttribute(Constants.NS_METADATA, Constants.CONTEXT,
          container.getContextURL().toASCIIString());
    }

    if (container.getMetadataETag() != null) {
      writer.writeAttribute(Constants.NS_METADATA, Constants.ATOM_ATTR_METADATAETAG,
          container.getMetadataETag());
    }
  }
}
