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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractODataObject;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.AbstractGeospatialType;
import org.apache.olingo.commons.core.edm.primitivetype.SingletonPrimitiveType;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.DeserializerResultImpl;

import com.fasterxml.aalto.stax.InputFactoryImpl;

public class ODataXmlDeserializer implements ODataDeserializer {

  private static final XMLInputFactory FACTORY = new InputFactoryImpl();
  private static final String ATOM = "a";
  private static final String NS_ATOM = "http://www.w3.org/2005/Atom";  
  private static final QName REF_ELEMENT = new QName("http://docs.oasis-open.org/odata/ns/metadata", "ref");
//  private static final QName FEED_ELEMENT = new QName("http://www.w3.org/2005/Atom", "feed");
  private static final QName ID_ATTR = new QName(NS_ATOM, ATOM);

  private final QName propertiesQName = new QName(Constants.NS_METADATA, Constants.PROPERTIES);
  private final QName propertyValueQName = new QName(Constants.NS_METADATA, Constants.VALUE);
  private final QName contextQName = new QName(Constants.NS_METADATA, Constants.CONTEXT);
  private final QName nullQName = new QName(Constants.NS_METADATA, Constants.ATTR_NULL);
  private final QName inlineQName = new QName(Constants.NS_METADATA, Constants.ATOM_ELEM_INLINE);
  private final QName entryRefQName = new QName(Constants.NS_METADATA, Constants.ATOM_ELEM_ENTRY_REF);
  private final QName etagQName = new QName(Constants.NS_METADATA, Constants.ATOM_ATTR_ETAG); 
  private final QName countQName = new QName(Constants.NS_METADATA, Constants.ATOM_ELEM_COUNT);
  
//  private void namespaces(final XMLStreamWriter writer) throws XMLStreamException {
//    writer.writeNamespace(StringUtils.EMPTY, Constants.NS_ATOM);
//    writer.writeNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
//    writer.writeNamespace(Constants.PREFIX_METADATA, Constants.NS_METADATA);
//    writer.writeNamespace(Constants.PREFIX_DATASERVICES, Constants.NS_DATASERVICES);
//    writer.writeNamespace(Constants.PREFIX_GML, Constants.NS_GML);
//    writer.writeNamespace(Constants.PREFIX_GEORSS, Constants.NS_GEORSS);
//  }
  
  protected XMLEventReader getReader(final InputStream input) throws XMLStreamException {
    return FACTORY.createXMLEventReader(input);
  }

  private Object primitive(final XMLEventReader reader, final StartElement start,
      final EdmProperty edmProperty) throws XMLStreamException, EdmPrimitiveTypeException, 
      DeserializerException {

    Object value = null;

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        if (edmProperty.getType() instanceof AbstractGeospatialType<?>) {
          throw new DeserializerException("geo types support not implemented",
              DeserializerException.MessageKeys.NOT_IMPLEMENTED);
        }
        final String stringValue = event.asCharacters().getData();
        value = ((EdmPrimitiveType)edmProperty.getType()).valueOfString(stringValue, 
            edmProperty.isNullable(), 
            edmProperty.getMaxLength(), 
            edmProperty.getPrecision(), 
            edmProperty.getScale(), 
            edmProperty.isUnicode(), 
            ((EdmPrimitiveType)edmProperty.getType()).getDefaultType());
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    return value;
  }

  private Object complex(final XMLEventReader reader, final StartElement start, EdmComplexType edmComplex)
      throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {
    ComplexValue value = new ComplexValue();
    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        StartElement se = event.asStartElement();
        value.getValue().add(property(reader, se, (EdmProperty)edmComplex.getProperty(se.getName().getLocalPart())));
      }
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    return value;
  }

  private void collection(final Valuable valuable, final XMLEventReader reader, final StartElement start,
      final EdmProperty edmProperty) throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {

    List<Object> values = new ArrayList<Object>();
    EdmType edmType = edmProperty.getType();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {        
        if (edmType instanceof SingletonPrimitiveType) {
          values.add(primitive(reader, event.asStartElement(), edmProperty));          
        } else if (edmType instanceof EdmComplexType) {
          values.add(complex(reader, event.asStartElement(), (EdmComplexType) edmType));                    
        } else if (edmType instanceof EdmEnumType) {
          values.add(readEnum(reader, event.asStartElement()));          
        } else {
          // do not add null or empty values
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    valuable.setValue(getValueType(edmType, true), values);
  }

  private Object readEnum(XMLEventReader reader, StartElement start) throws XMLStreamException {
    boolean foundEndProperty = false;
    Object value = null;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();
      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        value = event.asCharacters().getData();
      }
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }
    return value;
  }

  private Property property(final XMLEventReader reader, final StartElement start, final EdmProperty edmProperty)
      throws XMLStreamException, EdmPrimitiveTypeException, DeserializerException {

    final Property property = new Property();

    if (propertyValueQName.equals(start.getName())) {
      // retrieve name from context
      final Attribute context = start.getAttributeByName(contextQName);
      if (context != null) {
        property.setName(StringUtils.substringAfterLast(context.getValue(), "/"));
      }
    } else {
      property.setName(start.getName().getLocalPart());
    }
    valuable(property, reader, start, edmProperty);
    return property;
  }

  private ValueType getValueType(EdmType edmType, boolean isCollection) {
    if (edmType instanceof SingletonPrimitiveType) {
      return isCollection? ValueType.COLLECTION_PRIMITIVE:ValueType.PRIMITIVE;
    } else if (edmType instanceof EdmComplexType) {
      return isCollection? ValueType.COLLECTION_COMPLEX:ValueType.COMPLEX;
    } else if (edmType instanceof EdmEnumType) {
      return isCollection?ValueType.COLLECTION_ENUM:ValueType.ENUM;
    } else {
      return ValueType.PRIMITIVE;
    }
  }
  
  private void valuable(final Valuable valuable, final XMLEventReader reader, final StartElement start,
      EdmProperty edmProperty) throws XMLStreamException, EdmPrimitiveTypeException,
      DeserializerException {

    final Attribute nullAttr = start.getAttributeByName(nullQName);
    if (nullAttr != null) {
      //found null
      boolean foundEndProperty = false;
      while (reader.hasNext() && !foundEndProperty) {
        final XMLEvent event = reader.nextEvent();
        if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
          foundEndProperty = true;
        }
      }
      valuable.setValue(getValueType(edmProperty.getType(), false), null);  
      return;
    }

    EdmType edmType = edmProperty.getType();
    if (edmProperty.isCollection()) {
      collection(valuable, reader, start, edmProperty);
      valuable.setType("Collection("+edmType.getFullQualifiedName().getFullQualifiedNameAsString()+")");
    } else if (edmType instanceof SingletonPrimitiveType) {
      valuable.setType(edmType.getFullQualifiedName().getFullQualifiedNameAsString());
      valuable.setValue(ValueType.PRIMITIVE, primitive(reader, start, edmProperty));          
    } else if (edmType instanceof EdmComplexType) {
      valuable.setValue(ValueType.COMPLEX, complex(reader, start, (EdmComplexType) edmType));
      valuable.setType(edmType.getFullQualifiedName().getFullQualifiedNameAsString());
    } else if (edmType instanceof EdmEnumType) {
      valuable.setValue(ValueType.ENUM, readEnum(reader, start));
      valuable.setType(edmType.getFullQualifiedName().getFullQualifiedNameAsString());
    } else {
      // do not add null or empty values
    }
  }

  @Override
  public DeserializerResult property(InputStream input, EdmProperty edmProperty) 
      throws DeserializerException {
    try {
      final XMLEventReader reader = getReader(input);
      final StartElement start = skipBeforeFirstStartElement(reader);
      Property property = property(reader, start, edmProperty);
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
        try {
          object.setCommonProperty(key, event.asCharacters().getData());
        } catch (ParseException e) {
          throw new XMLStreamException("While parsing Atom entry or feed common elements", e);
        }
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
                throw new DeserializerException( "Navigation Property "+ link.getTitle() + 
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
        } else if (REF_ELEMENT.equals(event.asStartElement().getName())) {
          if (navigationProperty.isCollection()) {
            throw new DeserializerException("Binding annotation: " + link.getTitle() + 
                " must be collection of entity refercences",
                DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, link.getTitle());            
          }          
          link.setBindingLink(entityRef(reader, event.asStartElement()));
          link.setType(Constants.ENTITY_BINDING_LINK_TYPE);
        } else if (Constants.QNAME_ATOM_ELEM_FEED.equals(event.asStartElement().getName())) {
          if (navigationProperty.isCollection()) {
            throw new DeserializerException("Binding annotation: " + link.getTitle() + 
                " must be single entity refercences",
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

  private List<String> entityRefCollection(XMLEventReader reader, StartElement start) 
      throws XMLStreamException {
    boolean foundEndElement = false;
    ArrayList<String> references = new ArrayList<String>();
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();
      
      if (event.isStartElement() && REF_ELEMENT.equals(event.asStartElement().getName())) {
          references.add(entityRef(reader, event.asStartElement()));
      }
      
      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
    return references;
  }

  private String entityRef(XMLEventReader reader, StartElement start) throws XMLStreamException {
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
        EdmProperty edmProperty = (EdmProperty)edmEntityType
            .getProperty(event.asStartElement().getName().getLocalPart());
        entity.getProperties().add(property(reader, event.asStartElement(), edmProperty));
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
              entity.setType(new EdmTypeInfo.Builder().setTypeExpression(term.getValue()).build().internal());
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
              inline(reader, event.asStartElement(), link, edmEntityType);
              if (link.getInlineEntity() == null && link.getInlineEntitySet() == null) {
                entity.getNavigationBindings().add(link);
              } else {
                if (link.getInlineEntitySet() != null) {
                  List<String> bindings = new ArrayList<String>();
                  List<Entity> enities = link.getInlineEntitySet().getEntities();
                  
                  for (Entity inlineEntity:enities) {
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
            } else if (link.getRel().startsWith(Constants.NS_MEDIA_EDIT_LINK_REL)) {
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
              properties(reader, skipBeforeFirstStartElement(reader), entity, edmEntityType);
            } else {
              entity.setMediaContentType(contenttype.getValue());
              final Attribute src = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_SRC));
              if (src != null) {
                entity.setMediaContentSource(URI.create(src.getValue()));
              }
            }
          } else if (propertiesQName.equals(event.asStartElement().getName())) {
            properties(reader, event.asStartElement(), entity, edmEntityType);
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
  public DeserializerResult entity(InputStream input, EdmEntityType edmEntityType) 
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
  public DeserializerResult entityCollection(InputStream input, EdmEntityType edmEntityType)
      throws DeserializerException {
    try {
      final XMLEventReader reader = getReader(input);
      final StartElement start = skipBeforeFirstStartElement(reader);
      EntityCollection entityCollection = entitySet(reader, start, edmEntityType);
      for (Entity entity:entityCollection.getEntities()) {
        entity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());
      }
      return DeserializerResultImpl.with().entityCollection(entityCollection)
          .build();      
    } catch (XMLStreamException e) {
      throw new DeserializerException(e.getMessage(), e, DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new DeserializerException(e.getMessage(), e, 
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    }
  }
  
  @Override
  public DeserializerResult entityReferences(InputStream stream) throws DeserializerException {
    try {
      XMLEventReader reader = getReader(stream);
      ArrayList<URI> references = new ArrayList<URI>();

      while (reader.hasNext()) {
        final XMLEvent event = reader.nextEvent();
        if (event.isStartElement()) {
          StartElement start = event.asStartElement();
          if (REF_ELEMENT.equals(start.getName())) {
            Attribute context = start.getAttributeByName(ID_ATTR);
            if (context == null) {
              context = start.getAttributeByName(new QName("id"));
            }
            URI uri = URI.create(context.getValue());
            references.add(uri);
          }
        }
      }
      return DeserializerResultImpl.with().entityReferences(references).build();
    } catch (XMLStreamException e) {
      throw new DeserializerException("An IOException occurred", e,
          DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  @Override
  public DeserializerResult actionParameters(InputStream stream, EdmAction edmAction) 
      throws DeserializerException {
    throw new DeserializerException("Not implemented", DeserializerException.MessageKeys.NOT_IMPLEMENTED);
  } 
}
