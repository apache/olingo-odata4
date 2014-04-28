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

import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.domain.ODataPropertyType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.data.v3.XMLLinkCollectionImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class AtomDeserializer extends AbstractAtomDealer {

  protected static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

  private final AtomGeoValueDeserializer geoDeserializer;

  protected XMLEventReader getReader(final InputStream input) throws XMLStreamException {
    return FACTORY.createXMLEventReader(input);
  }

  public AtomDeserializer(final ODataServiceVersion version) {
    super(version);
    this.geoDeserializer = new AtomGeoValueDeserializer();
  }

  private Value fromPrimitive(final XMLEventReader reader, final StartElement start,
          final EdmTypeInfo typeInfo) throws XMLStreamException {

    Value value = null;

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && typeInfo != null && typeInfo.getPrimitiveTypeKind().isGeospatial()) {
        final EdmPrimitiveTypeKind geoType = EdmPrimitiveTypeKind.valueOfFQN(
                version, typeInfo.getFullQualifiedName().toString());
        value = new GeospatialValueImpl(this.geoDeserializer.deserialize(reader, event.asStartElement(), geoType));
      }

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()
              && (typeInfo == null || !typeInfo.getPrimitiveTypeKind().isGeospatial())) {

        value = new PrimitiveValueImpl(event.asCharacters().getData());
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return value == null ? new PrimitiveValueImpl(StringUtils.EMPTY) : value;
  }

  private Value fromComplexOrEnum(final XMLEventReader reader, final StartElement start)
          throws XMLStreamException {

    Value value = null;

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        if (value == null) {
          value = version.compareTo(ODataServiceVersion.V40) < 0
                  ? new ComplexValueImpl()
                  : new LinkedComplexValueImpl();
        }

        if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
          final LinkImpl link = new LinkImpl();
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
            link.setHref(href.getValue());
          }
          final Attribute type = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
          if (type != null) {
            link.setType(type.getValue());
          }

          if (link.getRel().startsWith(
                  version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL))) {

            value.asLinkedComplex().getNavigationLinks().add(link);
            inline(reader, event.asStartElement(), link);
          } else if (link.getRel().startsWith(
                  version.getNamespaceMap().get(ODataServiceVersion.ASSOCIATION_LINK_REL))) {

            value.asLinkedComplex().getAssociationLinks().add(link);
          }
        } else {
          value.asComplex().get().add(property(reader, event.asStartElement()));
        }
      }

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        value = new EnumValueImpl(event.asCharacters().getData());
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return value;
  }

  private CollectionValue fromCollection(final XMLEventReader reader, final StartElement start,
          final EdmTypeInfo typeInfo) throws XMLStreamException {

    final CollectionValueImpl value = new CollectionValueImpl();

    final EdmTypeInfo type = typeInfo == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(typeInfo.getFullQualifiedName().toString()).build();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        switch (guessPropertyType(reader, typeInfo)) {
          case COMPLEX:
          case ENUM:
            value.get().add(fromComplexOrEnum(reader, event.asStartElement()));
            break;

          case PRIMITIVE:
            value.get().add(fromPrimitive(reader, event.asStartElement(), type));
            break;

          default:
          // do not add null or empty values
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return value;
  }

  private ODataPropertyType guessPropertyType(final XMLEventReader reader, final EdmTypeInfo typeInfo)
          throws XMLStreamException {

    XMLEvent child = null;
    while (reader.hasNext() && child == null) {
      final XMLEvent event = reader.peek();
      if (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
        reader.nextEvent();
      } else {
        child = event;
      }
    }

    final ODataPropertyType type;
    if (child == null) {
      type = typeInfo == null || typeInfo.isPrimitiveType()
              ? ODataPropertyType.PRIMITIVE
              : ODataPropertyType.ENUM;
    } else {
      if (child.isStartElement()) {
        if (Constants.NS_GML.equals(child.asStartElement().getName().getNamespaceURI())) {
          type = ODataPropertyType.PRIMITIVE;
        } else if (elementQName.equals(child.asStartElement().getName())) {
          type = ODataPropertyType.COLLECTION;
        } else {
          type = ODataPropertyType.COMPLEX;
        }
      } else if (child.isCharacters()) {
        type = typeInfo == null || typeInfo.isPrimitiveType()
                ? ODataPropertyType.PRIMITIVE
                : ODataPropertyType.ENUM;
      } else {
        type = ODataPropertyType.EMPTY;
      }
    }

    return type;
  }

  private AtomPropertyImpl property(final XMLEventReader reader, final StartElement start)
          throws XMLStreamException {

    final AtomPropertyImpl property = new AtomPropertyImpl();

    if (ODataServiceVersion.V40 == version && propertyValueQName.equals(start.getName())) {
      // retrieve name from context
      final Attribute context = start.getAttributeByName(contextQName);
      if (context != null) {
        property.setName(StringUtils.substringAfterLast(context.getValue(), "/"));
      }
    } else {
      property.setName(start.getName().getLocalPart());
    }

    final Attribute nullAttr = start.getAttributeByName(this.nullQName);

    Value value;
    if (nullAttr == null) {
      final Attribute typeAttr = start.getAttributeByName(this.typeQName);
      final String typeAttrValue = typeAttr == null ? null : typeAttr.getValue();

      final EdmTypeInfo typeInfo = StringUtils.isBlank(typeAttrValue)
              ? null
              : new EdmTypeInfo.Builder().setTypeExpression(typeAttrValue).build();

      if (typeInfo != null) {
        property.setType(typeInfo.internal());
      }

      final ODataPropertyType propType = typeInfo == null
              ? guessPropertyType(reader, typeInfo)
              : typeInfo.isCollection()
              ? ODataPropertyType.COLLECTION
              : typeInfo.isPrimitiveType()
              ? ODataPropertyType.PRIMITIVE
              : ODataPropertyType.COMPLEX;

      switch (propType) {
        case COLLECTION:
          value = fromCollection(reader, start, typeInfo);
          break;

        case COMPLEX:
          value = fromComplexOrEnum(reader, start);
          break;

        case PRIMITIVE:
          // No type specified? Defaults to Edm.String          
          if (typeInfo == null) {
            property.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName().toString());
          }
          value = fromPrimitive(reader, start, typeInfo);
          break;

        case EMPTY:
        default:
          value = new PrimitiveValueImpl(StringUtils.EMPTY);
      }
    } else {
      value = new NullValueImpl();
    }

    property.setValue(value);

    return property;
  }

  private ResWrap<AtomPropertyImpl> property(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = getReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, property(reader, start));
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

  private void inline(final XMLEventReader reader, final StartElement start, final LinkImpl link)
          throws XMLStreamException {

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && inlineQName.equals(event.asStartElement().getName())) {
        StartElement inline = null;
        while (reader.hasNext() && inline == null) {
          final XMLEvent innerEvent = reader.peek();
          if (innerEvent.isCharacters() && innerEvent.asCharacters().isWhiteSpace()) {
            reader.nextEvent();
          } else if (innerEvent.isStartElement()) {
            inline = innerEvent.asStartElement();
          }
        }
        if (inline != null) {
          if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(inline.getName())) {
            link.setInlineEntity(entity(reader, inline));
          }
          if (Constants.QNAME_ATOM_ELEM_FEED.equals(inline.getName())) {
            link.setInlineEntitySet(entitySet(reader, inline));
          }
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }
  }

  private ResWrap<AtomDeltaImpl> delta(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = getReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, delta(reader, start));
  }

  private AtomDeltaImpl delta(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    if (!Constants.QNAME_ATOM_ELEM_FEED.equals(start.getName())) {
      return null;
    }
    final AtomDeltaImpl delta = new AtomDeltaImpl();
    final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
    if (xmlBase != null) {
      delta.setBaseURI(xmlBase.getValue());
    }

    boolean foundEndFeed = false;
    while (reader.hasNext() && !foundEndFeed) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        if (countQName.equals(event.asStartElement().getName())) {
          count(reader, event.asStartElement(), delta);
        } else if (Constants.QNAME_ATOM_ELEM_ID.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), delta, "id");
        } else if (Constants.QNAME_ATOM_ELEM_TITLE.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), delta, "title");
        } else if (Constants.QNAME_ATOM_ELEM_SUMMARY.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), delta, "summary");
        } else if (Constants.QNAME_ATOM_ELEM_UPDATED.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), delta, "updated");
        } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
          final Attribute rel = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REL));
          if (rel != null) {
            if (Constants.NEXT_LINK_REL.equals(rel.getValue())) {
              final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
              if (href != null) {
                delta.setNext(URI.create(href.getValue()));
              }
            }
            if (Constants.DELTA_LINK_REL.equals(rel.getValue())) {
              final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
              if (href != null) {
                delta.setDeltaLink(URI.create(href.getValue()));
              }
            }
          }
        } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(event.asStartElement().getName())) {
          delta.getEntities().add(entity(reader, event.asStartElement()));
        } else if (deletedEntryQName.equals(event.asStartElement().getName())) {
          final DeletedEntityImpl deletedEntity = new DeletedEntityImpl();

          final Attribute ref = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REF));
          if (ref != null) {
            deletedEntity.setId(ref.getValue());
          }
          final Attribute reason = event.asStartElement().getAttributeByName(reasonQName);
          if (reason != null) {
            deletedEntity.setReason(reason.getValue());
          }

          delta.getDeletedEntities().add(deletedEntity);
        } else if (linkQName.equals(event.asStartElement().getName())
                || deletedLinkQName.equals(event.asStartElement().getName())) {

          final DeltaLinkImpl link = new DeltaLinkImpl();

          final Attribute source = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_SOURCE));
          if (source != null) {
            link.setSource(URI.create(source.getValue()));
          }
          final Attribute relationship = 
                  event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_RELATIONSHIP));
          if (relationship != null) {
            link.setRelationship(relationship.getValue());
          }
          final Attribute target = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TARGET));
          if (target != null) {
            link.setTarget(URI.create(target.getValue()));
          }

          if (linkQName.equals(event.asStartElement().getName())) {
            delta.getAddedLinks().add(link);
          } else {
            delta.getDeletedLinks().add(link);
          }
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndFeed = true;
      }
    }

    return delta;
  }

  private ResWrap<XMLLinkCollectionImpl> linkCollection(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = getReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, linkCollection(reader, start));
  }

  private XMLLinkCollectionImpl linkCollection(final XMLEventReader reader, final StartElement start)
          throws XMLStreamException {

    final XMLLinkCollectionImpl linkCollection = new XMLLinkCollectionImpl();

    boolean isURI = false;
    boolean isNext = false;
    while (reader.hasNext()) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        isURI = uriQName.equals(event.asStartElement().getName());
        isNext = nextQName.equals(event.asStartElement().getName());
      }

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        if (isURI) {
          linkCollection.getLinks().add(URI.create(event.asCharacters().getData()));
          isURI = false;
        } else if (isNext) {
          linkCollection.setNext(URI.create(event.asCharacters().getData()));
          isNext = false;
        }
      }
    }

    return linkCollection;
  }

  private void properties(final XMLEventReader reader, final StartElement start, final AtomEntityImpl entity)
          throws XMLStreamException {
    boolean foundEndProperties = false;
    while (reader.hasNext() && !foundEndProperties) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        entity.getProperties().add(property(reader, event.asStartElement()));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperties = true;
      }
    }
  }

  private AtomEntityImpl entityRef(final StartElement start) throws XMLStreamException {
    final AtomEntityImpl entity = new AtomEntityImpl();

    final Attribute entityRefId = start.getAttributeByName(Constants.QNAME_ATOM_ATTR_ID);
    if (entityRefId != null) {
      entity.setId(entityRefId.getValue());
    }

    return entity;
  }

  private AtomEntityImpl entity(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    final AtomEntityImpl entity;
    if (entryRefQName.equals(start.getName())) {
      entity = entityRef(start);
    } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(start.getName())) {
      entity = new AtomEntityImpl();
      final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
      if (xmlBase != null) {
        entity.setBaseURI(xmlBase.getValue());
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
          } else if (Constants.QNAME_ATOM_ELEM_TITLE.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entity, "title");
          } else if (Constants.QNAME_ATOM_ELEM_SUMMARY.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entity, "summary");
          } else if (Constants.QNAME_ATOM_ELEM_UPDATED.equals(event.asStartElement().getName())) {
            common(reader, event.asStartElement(), entity, "updated");
          } else if (Constants.QNAME_ATOM_ELEM_CATEGORY.equals(event.asStartElement().getName())) {
            final Attribute term = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_TERM));
            if (term != null) {
              entity.setType(new EdmTypeInfo.Builder().setTypeExpression(term.getValue()).build().internal());
            }
          } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
            final LinkImpl link = new LinkImpl();
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
              link.setHref(href.getValue());
            }
            final Attribute type = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
            if (type != null) {
              link.setType(type.getValue());
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
            } else if (link.getRel().startsWith(
                    version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL))) {
              entity.getNavigationLinks().add(link);
              inline(reader, event.asStartElement(), link);
            } else if (link.getRel().startsWith(
                    version.getNamespaceMap().get(ODataServiceVersion.ASSOCIATION_LINK_REL))) {

              entity.getAssociationLinks().add(link);
            } else if (link.getRel().startsWith(
                    version.getNamespaceMap().get(ODataServiceVersion.MEDIA_EDIT_LINK_REL))) {

              final Attribute metag = event.asStartElement().getAttributeByName(etagQName);
              if (metag != null) {
                link.setMediaETag(metag.getValue());
              }
              entity.getMediaEditLinks().add(link);
            }
          } else if (actionQName.equals(event.asStartElement().getName())) {
            final ODataOperation operation = new ODataOperation();
            final Attribute metadata =
                    event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_METADATA));
            if (metadata != null) {
              operation.setMetadataAnchor(metadata.getValue());
            }
            final Attribute title = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TITLE));
            if (title != null) {
              operation.setTitle(title.getValue());
            }
            final Attribute target = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TARGET));
            if (target != null) {
              operation.setTarget(URI.create(target.getValue()));
            }

            entity.getOperations().add(operation);
          } else if (Constants.QNAME_ATOM_ELEM_CONTENT.equals(event.asStartElement().getName())) {
            final Attribute type = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_TYPE));
            if (type == null || ContentType.APPLICATION_XML.equals(type.getValue())) {
              properties(reader, skipBeforeFirstStartElement(reader), entity);
            } else {
              entity.setMediaContentType(type.getValue());
              final Attribute src = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATOM_ATTR_SRC));
              if (src != null) {
                entity.setMediaContentSource(src.getValue());
              }
            }
          } else if (propertiesQName.equals(event.asStartElement().getName())) {
            properties(reader, event.asStartElement(), entity);
          }
        }

        if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
          foundEndEntry = true;
        }
      }

      return entity;
    } else {
      entity = null;
    }

    return entity;
  }

  private ResWrap<AtomEntityImpl> entity(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = getReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, entity(reader, start));
  }

  private void count(final XMLEventReader reader, final StartElement start, final EntitySet entitySet)
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

  private AtomEntitySetImpl entitySet(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    if (!Constants.QNAME_ATOM_ELEM_FEED.equals(start.getName())) {
      return null;
    }
    final AtomEntitySetImpl entitySet = new AtomEntitySetImpl();
    final Attribute xmlBase = start.getAttributeByName(Constants.QNAME_ATTR_XML_BASE);
    if (xmlBase != null) {
      entitySet.setBaseURI(xmlBase.getValue());
    }

    boolean foundEndFeed = false;
    while (reader.hasNext() && !foundEndFeed) {
      final XMLEvent event = reader.nextEvent();
      if (event.isStartElement()) {
        if (countQName.equals(event.asStartElement().getName())) {
          count(reader, event.asStartElement(), entitySet);
        } else if (Constants.QNAME_ATOM_ELEM_ID.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), entitySet, "id");
        } else if (Constants.QNAME_ATOM_ELEM_TITLE.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), entitySet, "title");
        } else if (Constants.QNAME_ATOM_ELEM_SUMMARY.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), entitySet, "summary");
        } else if (Constants.QNAME_ATOM_ELEM_UPDATED.equals(event.asStartElement().getName())) {
          common(reader, event.asStartElement(), entitySet, "updated");
        } else if (Constants.QNAME_ATOM_ELEM_LINK.equals(event.asStartElement().getName())) {
          final Attribute rel = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_REL));
          if (rel != null && Constants.NEXT_LINK_REL.equals(rel.getValue())) {
            final Attribute href = event.asStartElement().getAttributeByName(QName.valueOf(Constants.ATTR_HREF));
            if (href != null) {
              entitySet.setNext(URI.create(href.getValue()));
            }
          }
        } else if (Constants.QNAME_ATOM_ELEM_ENTRY.equals(event.asStartElement().getName())) {
          entitySet.getEntities().add(entity(reader, event.asStartElement()));
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

  private ResWrap<AtomEntitySetImpl> entitySet(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = getReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, entitySet(reader, start));
  }

  private XMLODataErrorImpl error(final XMLEventReader reader, final StartElement start) throws XMLStreamException {
    final XMLODataErrorImpl error = new XMLODataErrorImpl();

    boolean setCode = false;
    boolean codeSet = false;
    boolean setMessage = false;
    boolean messageSet = false;
    boolean setTarget = false;
    boolean targetSet = false;

    boolean foundEndElement = false;
    while (reader.hasNext() && !foundEndElement) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        if (errorCodeQName.equals(event.asStartElement().getName())) {
          setCode = true;
        } else if (errorMessageQName.equals(event.asStartElement().getName())) {
          setMessage = true;
        } else if (errorTargetQName.equals(event.asStartElement().getName())) {
          setTarget = true;
        }
      }

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        if (setCode && !codeSet) {
          error.setCode(event.asCharacters().getData());
          setCode = false;
          codeSet = true;
        }
        if (setMessage && !messageSet) {
          error.setMessage(event.asCharacters().getData());
          setMessage = false;
          messageSet = true;
        }
        if (setTarget && !targetSet) {
          error.setTarget(event.asCharacters().getData());
          setTarget = false;
          targetSet = true;
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndElement = true;
      }
    }

    return error;
  }

  private ResWrap<XMLODataErrorImpl> error(final InputStream input) throws XMLStreamException {
    final XMLEventReader reader = getReader(input);
    final StartElement start = skipBeforeFirstStartElement(reader);
    return getContainer(start, error(reader, start));
  }

  private <T> ResWrap<T> getContainer(final StartElement start, final T object) {
    final Attribute context = start.getAttributeByName(contextQName);
    final Attribute metadataETag = start.getAttributeByName(metadataEtagQName);

    return new ResWrap<T>(
            context == null ? null : URI.create(context.getValue()),
            metadataETag == null ? null : metadataETag.getValue(),
            object);
  }

  @SuppressWarnings("unchecked")
  public <T, V extends T> ResWrap<T> read(final InputStream input, final Class<V> reference)
          throws XMLStreamException {

    if (XMLODataErrorImpl.class.equals(reference)) {
      return (ResWrap<T>) error(input);
    } else if (AtomEntitySetImpl.class.equals(reference)) {
      return (ResWrap<T>) entitySet(input);
    } else if (AtomEntityImpl.class.equals(reference)) {
      return (ResWrap<T>) entity(input);
    } else if (AtomPropertyImpl.class.equals(reference)) {
      return (ResWrap<T>) property(input);
    } else if (XMLLinkCollectionImpl.class.equals(reference)) {
      return (ResWrap<T>) linkCollection(input);
    } else if (AtomDeltaImpl.class.equals(reference)) {
      return (ResWrap<T>) delta(input);
    }
    return null;
  }
}
