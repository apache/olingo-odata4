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
package org.apache.olingo.server.core.deserializer.xml;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractODataObject;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.AbstractGeospatialType;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.DeserializerResultImpl;

public class ODataXmlDeserializer implements ODataDeserializer {

  private static final XMLInputFactory FACTORY = XMLInputFactory.newFactory();

  private static final QName propertiesQName = new QName(Constants.NS_METADATA, Constants.PROPERTIES);
  private static final QName propertyValueQName = new QName(Constants.NS_METADATA, Constants.VALUE);
  private static final QName contextQName = new QName(Constants.NS_METADATA, Constants.CONTEXT);
  private static final QName nullQName = new QName(Constants.NS_METADATA, Constants.ATTR_NULL);
  private static final QName inlineQName = new QName(Constants.NS_METADATA, Constants.ATOM_ELEM_INLINE);
  private static final QName entryRefQName = new QName(Constants.NS_METADATA, Constants.ATOM_ELEM_ENTRY_REF);
  private static final QName etagQName = new QName(Constants.NS_METADATA, Constants.ATOM_ATTR_ETAG);
  private static final QName countQName = new QName(Constants.NS_METADATA, Constants.ATOM_ELEM_COUNT);
  private static final QName parametersQName = new QName(Constants.NS_METADATA, "parameters");
  private static final QName typeQName = new QName(Constants.NS_METADATA, Constants.ATTR_TYPE);
  
  private ServiceMetadata serviceMetadata;

  public ODataXmlDeserializer() {
  }

  public ODataXmlDeserializer(final ServiceMetadata serviceMetadata) {
    this.serviceMetadata = serviceMetadata;
  }
  
  public void setMetadata(ServiceMetadata metadata) {
    this.serviceMetadata = metadata;
  }
  
  protected XMLEventReader getReader(final InputStream input) throws XMLStreamException {
    return FACTORY.createXMLEventReader(input);
  }

  private Object primitive(final XMLEventReader reader, final StartElement start,
      final EdmType type, final boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final boolean isUnicode) throws XMLStreamException, EdmPrimitiveTypeException,
      DeserializerException {

    Object value = null;

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        if (type instanceof AbstractGeospatialType<?>) {
          throw new DeserializerException("geo types support not implemented",
              DeserializerException.MessageKeys.NOT_IMPLEMENTED);
        }
        final EdmPrimitiveType primitiveType = (EdmPrimitiveType) type;
        final String stringValue = event.asCharacters().getData();
        value = primitiveType.valueOfString(stringValue,
            isNullable,
            maxLength,
            precision,
            scale,
            isUnicode,
            primitiveType.getDefaultType());
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    return value;
  }

  private Object complex(final XMLEventReader reader, final StartElement start, final EdmComplexType edmComplex)
      throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {
    ComplexValue value = new ComplexValue();
    EdmType resolvedType = edmComplex;
    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();
      
      if (event.isStartElement()) {        
        //Get the derived type from the element tag
        final Attribute attrType = start.getAttributeByName(typeQName);
        if (attrType != null ) {
          String type = new EdmTypeInfo.Builder().setTypeExpression(attrType.getValue()).build().internal();
          if (type.startsWith("Collection(") && type.endsWith(")")) {
            type = type.substring(11, type.length()-1);
          }
          resolvedType = getDerivedType(edmComplex, type);
        }
        
        
        StartElement se = event.asStartElement();
        EdmProperty p = (EdmProperty) ((EdmComplexType)resolvedType).getProperty(se.getName().getLocalPart());
        value.getValue().add(property(reader, se, p.getType(), p.isNullable(), p.getMaxLength(),
            p.getPrecision(), p.getScale(), p.isUnicode(), p.isCollection()));
        value.setTypeName(resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
      }
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    return value;
  }

  private void collection(final Valuable valuable, final XMLEventReader reader, final StartElement start,
      final EdmType edmType, final boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final boolean isUnicode) throws XMLStreamException, EdmPrimitiveTypeException,
      DeserializerException {

    List<Object> values = new ArrayList<Object>();
    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        if (edmType instanceof EdmPrimitiveType) {
          values.add(primitive(reader, event.asStartElement(), edmType, isNullable,
              maxLength, precision, scale, isUnicode));
        } else if (edmType instanceof EdmComplexType) {
          values.add(complex(reader, event.asStartElement(), (EdmComplexType) edmType));
        }
        // do not add null or empty values
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    valuable.setValue(getValueType(edmType, true), values);
  }

  private Property property(final XMLEventReader reader, final StartElement start, final EdmType edmType,
      final boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final boolean isUnicode, final boolean isCollection)
          throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {

    final Property property = new Property();

    if (propertyValueQName.equals(start.getName())) {
      // retrieve name from context
      final Attribute context = start.getAttributeByName(contextQName);
      if (context != null && context.getValue() != null) {
        final int pos = context.getValue().lastIndexOf('/');
        property.setName(pos == -1 ? "" : context.getValue().substring(pos + 1));
      }
    } else {
      property.setName(start.getName().getLocalPart());
    }
    
    EdmType resolvedType = edmType;
    final Attribute attrType = start.getAttributeByName(typeQName);
    if (attrType != null && (edmType instanceof EdmComplexType)) {
      String type = new EdmTypeInfo.Builder().setTypeExpression(attrType.getValue()).build().internal();
      if (type.startsWith("Collection(") && type.endsWith(")")) {
        type = type.substring(11, type.length()-1);
      }
      resolvedType = getDerivedType((EdmComplexType)edmType, type);
    }
    valuable(property, reader, start, resolvedType, isNullable, maxLength, precision, scale, isUnicode, isCollection);
    return property;
  }

  private ValueType getValueType(final EdmType edmType, final boolean isCollection) {
    if (edmType instanceof EdmPrimitiveType) {
      if (edmType instanceof EdmEnumType) {
        return isCollection ? ValueType.COLLECTION_ENUM : ValueType.ENUM;
      } else {
        return isCollection ? ValueType.COLLECTION_PRIMITIVE : ValueType.PRIMITIVE;
      }
    } else if (edmType instanceof EdmComplexType) {
      return isCollection ? ValueType.COLLECTION_COMPLEX : ValueType.COMPLEX;
    } else {
      return ValueType.PRIMITIVE;
    }
  }

  private void valuable(final Valuable valuable, final XMLEventReader reader, final StartElement start,
      final EdmType edmType, final boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final boolean isUnicode, final boolean isCollection) throws XMLStreamException,
      EdmPrimitiveTypeException, DeserializerException {

    final Attribute nullAttr = start.getAttributeByName(nullQName);
    if (nullAttr != null) {
      // found null
      boolean foundEndProperty = false;
      while (reader.hasNext() && !foundEndProperty) {
        final XMLEvent event = reader.nextEvent();
        if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
          foundEndProperty = true;
        }
      }
      valuable.setValue(getValueType(edmType, false), null);
      return;
    }

    final String typeName = edmType.getFullQualifiedName().getFullQualifiedNameAsString();
    valuable.setType(isCollection ? ("Collection(" + typeName + ")") : typeName);
    if (isCollection) {
      collection(valuable, reader, start, edmType, isNullable, maxLength, precision, scale, isUnicode);
    } else if (edmType instanceof EdmPrimitiveType) {
      valuable.setValue(getValueType(edmType, false),
          primitive(reader, start, edmType, isNullable, maxLength, precision, scale, isUnicode));
    } else if (edmType instanceof EdmComplexType) {
      valuable.setValue(ValueType.COMPLEX, complex(reader, start, (EdmComplexType) edmType));
    } else if (edmType instanceof EdmEntityType) {
      valuable.setValue(ValueType.ENTITY, entity(reader, start, (EdmEntityType) edmType));
    }
    // do not add null or empty values
  }

  @Override
  public DeserializerResult property(final InputStream input, final EdmProperty edmProperty)
      throws DeserializerException {
    try {
      final XMLEventReader reader = getReader(input);
      final StartElement start = skipBeforeFirstStartElement(reader);
      Property property = property(reader, start,
          edmProperty.getType(),
          edmProperty.isNullable(),
          edmProperty.getMaxLength(),
          edmProperty.getPrecision(),
          edmProperty.getScale(),
          edmProperty.isUnicode(),
          edmProperty.isCollection());
      return DeserializerResultImpl.with().property(property)
          .build();
    } catch (XMLStreamException e) {
      throw new DeserializerException(e.getMessage(), e, DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new DeserializerException(e.getMessage(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    }
  }

  private StartElement skipBeforeFirstStartElement(final XMLEventReader reader) throws XMLStreamException {
    StartElement startEvent = null;
    while (reader.hasNext() && startEvent == null) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        startEvent = event.asStartElement();
      }
    }
    if (startEvent == null) {
      throw new IllegalArgumentException("Cannot find any XML start element");
    }

    return startEvent;
  }

  private void common(final XMLEventReader reader, final StartElement start,
      final AbstractODataObject object, final String key) throws XMLStreamException {

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        object.setCommonProperty(key, event.asCharacters().getData());
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private void inline(final XMLEventReader reader, final StartElement start, final Link link,
      final EdmEntityType edmEntityType) throws XMLStreamException, EdmPrimitiveTypeException,
      DeserializerException {

    boolean foundEndElement = false;
    EdmNavigationProperty navigationProperty = edmEntityType.getNavigationProperty(link.getTitle());
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        if (inlineQName.equals(event.asStartElement().getName())) {
          StartElement inline = getStartElement(reader);
          if (inline != null) {
            if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(inline.getName())) {
              if (navigationProperty.isCollection()) {
                throw new DeserializerException("Navigation Property " + link.getTitle() +
                    " must be collection entities",
                    DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, link.getTitle());
              }

              link.setInlineEntity(entity(reader, inline, navigationProperty.getType()));
            }
            if (Constants.QNAME_ATOM_ELEM_FEED.equals(inline.getName())) {
              if (!navigationProperty.isCollection()) {
                throw new DeserializerException("Navigation Property " + link.getTitle() +
                    " must be single entity",
                    DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, link.getTitle());
              }
              link.setInlineEntitySet(entitySet(reader, inline, navigationProperty.getType()));
            }
          }
        } else if (entryRefQName.equals(event.asStartElement().getName())) {
          if (navigationProperty.isCollection()) {
            throw new DeserializerException("Binding annotation: " + link.getTitle() +
                " must be collection of entity references",
                DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, link.getTitle());
          }
          link.setBindingLink(entityRef(reader, event.asStartElement()));
          link.setType(Constants.ENTITY_BINDING_LINK_TYPE);
        } else if (Constants.QNAME_ATOM_ELEM_FEED.equals(event.asStartElement().getName())) {
          if (navigationProperty.isCollection()) {
            throw new DeserializerException("Binding annotation: " + link.getTitle() +
                " must be single entity references",
                DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, link.getTitle());
          }
          link.setBindingLinks(entityRefCollection(reader, event.asStartElement()));
          link.setType(Constants.ENTITY_COLLECTION_BINDING_LINK_TYPE);
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private List<String> entityRefCollection(final XMLEventReader reader, final StartElement start)
      throws XMLStreamException {
    boolean foundEndElement = false;
    ArrayList<String> references = new ArrayList<String>();
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && entryRefQName.equals(event.asStartElement().getName())) {
        references.add(entityRef(reader, event.asStartElement()));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
    return references;
  }

  private String entityRef(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    boolean foundEndElement = false;
    final Attribute entityRefId = start.getAttributeByName(Constants.QNAME_ATOM_ATTR_ID);
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
    return entityRefId.getValue();
  }

  private StartElement getStartElement(final XMLEventReader reader) throws XMLStreamException {
    while (reader.hasNext()) {
      final XMLEvent innerEvent = reader.peek();
      if (innerEvent.isCharacters() && innerEvent.asCharacters().isWhiteSpace()) {
        reader.nextEvent();
      } else if (innerEvent.isStartElement()) {
        return innerEvent.asStartElement();
      } else if (innerEvent.isEndElement() && inlineQName.equals(innerEvent.asEndElement().getName())) {
        return null;
      }
    }
    return null;
  }

  private void properties(final XMLEventReader reader, final StartElement start, final Entity entity,
      final EdmEntityType edmEntityType)
          throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {

    boolean foundEndProperties = false;
    while (reader.hasNext() && !foundEndProperties) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        String propertyName = event.asStartElement().getName().getLocalPart();
        EdmProperty edmProperty = (EdmProperty) edmEntityType.getProperty(propertyName);
        if (edmProperty == null) {
          throw new DeserializerException("Invalid Property in payload with name: " + propertyName,
              DeserializerException.MessageKeys.UNKNOWN_CONTENT, propertyName);
        }
        entity.getProperties().add(property(reader, event.asStartElement(),
            edmProperty.getType(),
            edmProperty.isNullable(),
            edmProperty.getMaxLength(),
            edmProperty.getPrecision(),
            edmProperty.getScale(),
            edmProperty.isUnicode(),
            edmProperty.isCollection()));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperties = true;
      }
    }
  }

  private Entity entityRef(final StartElement start) throws XMLStreamException {
    final Entity entity = new Entity();

    final Attribute entityRefId = start.getAttributeByName(Constants.QNAME_ATOM_ATTR_ID);
    if (entityRefId != null) {
      entity.setId(URI.create(entityRefId.getValue()));
    }

    return entity;
  }

  private Entity entity(final XMLEventReader reader, final StartElement start, final EdmEntityType edmEntityType)
      throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {
    Entity entity = null;
    EdmEntityType resolvedType = edmEntityType;
    if (entryRefQName.equals(start.getName())) {
      entity = entityRef(start);
    } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(start.getName())) {
      entity = new Entity();
      final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
      if (xmlBase != null) {
        entity.setBaseURI(URI.create(xmlBase.getValue()));
      }

      final Attribute etag = start.getAttributeByName(etagQName);
      if (etag != null) {
        entity.setETag(etag.getValue());
      }

      boolean foundEndEntry = false;
      while (reader.hasNext() && !foundEndEntry) {
        final XMLEvent event = reader.nextEvent();

        if (event.isStartElement()) {
          if (Constants.QNAME_ATOM_ELEM_ID.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entity, "id");
          } else if (Constants.QNAME_ATOM_ELEM_CATEGORY.equals(event.asStartElement().getName())) {
            final Attribute term = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_TERM));
            if (term != null) {
              String type = new EdmTypeInfo.Builder().setTypeExpression(term.getValue()).build().internal();
              entity.setType(type);
              resolvedType = (EdmEntityType)getDerivedType(edmEntityType, type);
            }
          } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
            final Link link = new Link();
            final Attribute rel = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REL));
            if (rel != null) {
              link.setRel(rel.getValue());
            }
            final Attribute title = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TITLE));
            if (title != null) {
              link.setTitle(title.getValue());
            }
            final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
            if (href != null) {
              link.setBindingLink(href.getValue());
            }
            final Attribute linktype = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
            if (linktype != null) {
              link.setType(linktype.getValue());
            }
            if (Constants.SELF_LINK_REL.equals(link.getRel())) {
              entity.setSelfLink(link);
            } else if (Constants.EDIT_LINK_REL.equals(link.getRel())) {
              entity.setEditLink(link);
            } else if (Constants.EDITMEDIA_LINK_REL.equals(link.getRel())) {
              final Attribute mediaETag = event.asStartElement().getAttributeByName(etagQName);
              if (mediaETag != null) {
                entity.setMediaETag(mediaETag.getValue());
              }
            } else if (link.getRel().startsWith(Constants.NS_NAVIGATION_LINK_REL)) {
              inline(reader, event.asStartElement(), link, resolvedType);
              if (link.getInlineEntity() == null && link.getInlineEntitySet() == null) {
                entity.getNavigationBindings().add(link);
              } else {
                if (link.getInlineEntitySet() != null) {
                  List<String> bindings = new ArrayList<String>();
                  List<Entity> entities = link.getInlineEntitySet().getEntities();

                  for (Entity inlineEntity : entities) {
                    // check if this is reference
                    if (inlineEntity.getId() != null && inlineEntity.getProperties().isEmpty()) {
                      bindings.add(inlineEntity.getId().toASCIIString());
                    }
                  }
                  if (!bindings.isEmpty()) {
                    link.setInlineEntitySet(null);
                    link.setBindingLinks(bindings);
                    entity.getNavigationBindings().add(link);
                  } else {
                    entity.getNavigationLinks().add(link);
                  }
                } else {
                  // add link
                  entity.getNavigationLinks().add(link);
                }
              }
            } else if (link.getRel().startsWith(Constants.NS_ASSOCIATION_LINK_REL)) {
              entity.getAssociationLinks().add(link);
            } else if (link.getRel().startsWith(Constants.NS_MEDIA_EDIT_LINK_REL) ||
                link.getRel().startsWith(Constants.NS_MEDIA_READ_LINK_REL)) {
              final Attribute metag = event.asStartElement().getAttributeByName(etagQName);
              if (metag != null) {
                link.setMediaETag(metag.getValue());
              }
              entity.getMediaEditLinks().add(link);
            }
          } else if (Constants.QNAME_ATOM_ELEM_CONTENT.equals(event.asStartElement().getName())) {
            final Attribute contenttype = event.asStartElement()
                .getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
            if (contenttype == null || ContentType.APPLICATION_XML.toContentTypeString()
                .equals(contenttype.getValue())) {
              properties(reader, skipBeforeFirstStartElement(reader), entity, resolvedType);
            } else {
              entity.setMediaContentType(contenttype.getValue());
              final Attribute src = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_SRC));
              if (src != null) {
                entity.setMediaContentSource(URI.create(src.getValue()));
              }
            }
          } else if (propertiesQName.equals(event.asStartElement().getName())) {
            properties(reader, event.asStartElement(), entity, resolvedType);
          }
        }

        if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
          foundEndEntry = true;
        }
      }
    }
    return entity;
  }

  @Override
  public DeserializerResult entity(final InputStream input, final EdmEntityType edmEntityType)
      throws DeserializerException {
    try {
      final XMLEventReader reader = getReader(input);
      final StartElement start = skipBeforeFirstStartElement(reader);
      final Entity entity = entity(reader, start, edmEntityType);
      if (entity == null) {
        throw new DeserializerException("No entity found!", DeserializerException.MessageKeys.INVALID_ENTITY);
      }
      return DeserializerResultImpl.with().entity(entity)
          .build();
    } catch (XMLStreamException e) {
      throw new DeserializerException(e.getMessage(), e,
          DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new DeserializerException(e.getMessage(), e,
          DeserializerException.MessageKeys.INVALID_ENTITY);
    }
  }

  private void count(final XMLEventReader reader, final StartElement start, final EntityCollection entitySet)
      throws XMLStreamException {

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();
      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        entitySet.setCount(Integer.valueOf(event.asCharacters().getData()));
      }
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private EntityCollection entitySet(final XMLEventReader reader, final StartElement start,
      final EdmEntityType edmEntityType) throws XMLStreamException, EdmPrimitiveTypeException,
      DeserializerException {
    if (!Constants.QNAME_ATOM_ELEM_FEED.equals(start.getName())) {
      return null;
    }
    final EntityCollection entitySet = new EntityCollection();
    final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
    if (xmlBase != null) {
      entitySet.setBaseURI(URI.create(xmlBase.getValue()));
    }

    boolean foundEndFeed = false;
    while (reader.hasNext() && !foundEndFeed) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        if (countQName.equals(event.asStartElement().getName())) {
          count(reader, event.asStartElement(), entitySet);
        } else if (Constants.QNAME_ATOM_ELEM_ID.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), entitySet, "id");
        } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
          final Attribute rel = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REL));
          if (rel != null) {
            if (Constants.NEXT_LINK_REL.equals(rel.getValue())) {
              final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
              if (href != null) {
                entitySet.setNext(URI.create(href.getValue()));
              }
            }
            if (Constants.NS_DELTA_LINK_REL.equals(rel.getValue())) {
              final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
              if (href != null) {
                entitySet.setDeltaLink(URI.create(href.getValue()));
              }
            }
          }
        } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(event.asStartElement().getName())) {
          entitySet.getEntities().add(entity(reader, event.asStartElement(), edmEntityType));
        } else if (entryRefQName.equals(event.asStartElement().getName())) {
          entitySet.getEntities().add(entityRef(event.asStartElement()));
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndFeed = true;
      }
    }
    return entitySet;
  }

  @Override
  public DeserializerResult entityCollection(final InputStream input, final EdmEntityType edmEntityType)
      throws DeserializerException {
    try {
      final XMLEventReader reader = getReader(input);
      final StartElement start = skipBeforeFirstStartElement(reader);
      EntityCollection entityCollection = entitySet(reader, start, edmEntityType);
      if (entityCollection != null) {
        for (Entity entity : entityCollection.getEntities()) {
          entity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());
        }
      }
      return DeserializerResultImpl.with().entityCollection(entityCollection).build();
    } catch (final XMLStreamException e) {
      throw new DeserializerException(e.getMessage(), e, DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new DeserializerException(e.getMessage(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    }
  }

  @Override
  public DeserializerResult entityReferences(final InputStream stream) throws DeserializerException {
    try {
      XMLEventReader reader = getReader(stream);
      ArrayList<URI> references = new ArrayList<URI>();

      while (reader.hasNext()) {
        final XMLEvent event = reader.nextEvent();
        if (event.isStartElement()) {
          StartElement start = event.asStartElement();
          if (entryRefQName.equals(start.getName())) {
            Attribute context = start.getAttributeByName(Constants.QNAME_ATOM_ATTR_ID);
            URI uri = URI.create(context.getValue());
            references.add(uri);
          }
        }
      }
      return DeserializerResultImpl.with().entityReferences(references).build();
    } catch (XMLStreamException e) {
      throw new DeserializerException(e.getMessage(), e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  @Override
  public DeserializerResult actionParameters(final InputStream stream, final EdmAction edmAction)
      throws DeserializerException {
    Map<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();
    if (edmAction.getParameterNames() == null || edmAction.getParameterNames().isEmpty()
        || edmAction.isBound() && edmAction.getParameterNames().size() == 1) {
      return DeserializerResultImpl.with().actionParameters(parameters)
          .build();
    }

    try {
      final XMLEventReader reader = getReader(stream);
      while (reader.hasNext()) {
        final XMLEvent event = reader.nextEvent();
        if (event.isStartElement() && parametersQName.equals(event.asStartElement().getName())) {
          consumeParameters(edmAction, reader, event.asStartElement(), parameters);
        }
      }
      // EDM checks.
      for (final String param : edmAction.getParameterNames()) {
        Parameter parameter = parameters.get(param);
        if (parameter == null) {
          final EdmParameter edmParameter = edmAction.getParameter(param);
          if (!edmParameter.isNullable()) {
            throw new DeserializerException("Non-nullable parameter not present or null: " + param,
                MessageKeys.INVALID_NULL_PARAMETER, param);
          }
          if (edmParameter.isCollection()) {
            throw new DeserializerException("Collection must not be null for parameter: " + param,
                MessageKeys.INVALID_NULL_PARAMETER, param);
          }
          // NULL fill for missing parameters.
          parameter = new Parameter();
          parameter.setName(param);
          parameter.setValue(ValueType.PRIMITIVE, null);
          parameters.put(param, parameter);
        }
      }
      return DeserializerResultImpl.with().actionParameters(parameters)
          .build();
    } catch (XMLStreamException e) {
      throw new DeserializerException(e.getMessage(), e, DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new DeserializerException(e.getMessage(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    }
  }

  private void consumeParameters(final EdmAction edmAction, final XMLEventReader reader,
      final StartElement start, final Map<String, Parameter> parameters) throws DeserializerException,
      EdmPrimitiveTypeException, XMLStreamException {

    List<String> parameterNames = edmAction.getParameterNames();
    if (edmAction.isBound()) {
      // The binding parameter must not occur in the payload.
      parameterNames = parameterNames.subList(1, parameterNames.size());
    }

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        boolean found = false;
        for (String paramName : parameterNames) {
          if (paramName.equals(event.asStartElement().getName().getLocalPart())) {
            found = true;
            Parameter parameter = createParameter(reader, event.asStartElement(), paramName,
                edmAction.getParameter(paramName));
            Parameter previous = parameters.put(paramName, parameter);
            if (previous != null) {
              throw new DeserializerException("Duplicate property detected",
                  DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
            }
            break; // for
          }
        }
        if (!found) {
          throw new DeserializerException("failed to read " + event.asStartElement().getName().getLocalPart(),
              DeserializerException.MessageKeys.UNKNOWN_CONTENT);
        }
      }
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private Parameter createParameter(final XMLEventReader reader, final StartElement start, final String paramName,
      final EdmParameter edmParameter) throws DeserializerException, EdmPrimitiveTypeException, XMLStreamException {

    Parameter parameter = new Parameter();
    parameter.setName(paramName);
    switch (edmParameter.getType().getKind()) {
    case PRIMITIVE:
    case ENUM:
    case DEFINITION:
    case COMPLEX:
      Property property = property(reader, start,
          edmParameter.getType(),
          edmParameter.isNullable(),
          edmParameter.getMaxLength(),
          edmParameter.getPrecision(),
          edmParameter.getScale(),
          true,
          edmParameter.isCollection());
      parameter.setValue(property.getValueType(), property.getValue());
      break;
    case ENTITY:
      if (edmParameter.isCollection()) {
        final EntityCollection entityCollection = entitySet(reader, start, (EdmEntityType) edmParameter.getType());
        parameter.setValue(ValueType.COLLECTION_ENTITY, entityCollection);
      } else {
        final Entity entity = entity(reader, start, (EdmEntityType) edmParameter.getType());
        parameter.setValue(ValueType.ENTITY, entity);
      }
      break;
    default:
      throw new DeserializerException("Invalid type kind " + edmParameter.getType().getKind().toString()
          + " for action parameter: " + paramName, DeserializerException.MessageKeys.INVALID_ACTION_PARAMETER_TYPE,
          paramName);
    }
    return parameter;
  }
  
  private EdmType getDerivedType(final EdmStructuredType edmType, String odataType)
      throws DeserializerException {
    if (odataType != null && !odataType.isEmpty()) {
      
      if (odataType.equalsIgnoreCase(edmType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return edmType;
      } else if (this.serviceMetadata == null) {
        throw new DeserializerException(
            "Failed to resolve Odata type " + odataType + " due to metadata is not available",
            DeserializerException.MessageKeys.UNKNOWN_CONTENT);
      }
      
      EdmStructuredType currentEdmType = null;
      if(edmType instanceof EdmEntityType) {
        currentEdmType = serviceMetadata.getEdm()
            .getEntityType(new FullQualifiedName(odataType));          
      } else {
        currentEdmType = serviceMetadata.getEdm()
            .getComplexType(new FullQualifiedName(odataType));          
      }
      if (!isAssignable(edmType, currentEdmType)) {
        throw new DeserializerException(
            "Odata type " + odataType + " not allowed here",
            DeserializerException.MessageKeys.UNKNOWN_CONTENT);
      }

      return currentEdmType;
    }
    return edmType;
  }

  private boolean isAssignable(final EdmStructuredType edmStructuredType,
      final EdmStructuredType edmStructuredTypeToAssign) {
    if (edmStructuredTypeToAssign == null) {
      return false;
    } else if (edmStructuredType.getFullQualifiedName()
        .equals(edmStructuredTypeToAssign.getFullQualifiedName())) {
      return true;
    } else {
      return isAssignable(edmStructuredType,
          edmStructuredTypeToAssign.getBaseType());
    }
  }  
}
