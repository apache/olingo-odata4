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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataPropertyType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

class AtomPropertyDeserializer extends AbstractAtomDealer {

  private final AtomGeoValueDeserializer geoDeserializer;

  public AtomPropertyDeserializer(final ODataServiceVersion version) {
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

    return value;
  }

  private ComplexValue fromComplex(final XMLEventReader reader, final StartElement start)
          throws XMLStreamException {

    final ComplexValue value = new ComplexValueImpl();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        value.get().add(deserialize(reader, event.asStartElement()));
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
        switch (guessPropertyType(reader)) {
          case COMPLEX:
            value.get().add(fromComplex(reader, event.asStartElement()));
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

  private ODataPropertyType guessPropertyType(final XMLEventReader reader) throws XMLStreamException {
    XMLEvent child = null;
    while (reader.hasNext() && child == null) {
      final XMLEvent event = reader.peek();
      if (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
        reader.nextEvent();
      } else {
        child = event;
      }
    }

    ODataPropertyType type = null;
    if (child == null) {
      type = ODataPropertyType.PRIMITIVE;
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
        type = ODataPropertyType.PRIMITIVE;
      } else {
        type = ODataPropertyType.EMPTY;
      }
    }

    return type;
  }

  public AtomPropertyImpl deserialize(final XMLEventReader reader, final StartElement start)
          throws XMLStreamException {

    final AtomPropertyImpl property = new AtomPropertyImpl();
    property.setName(start.getName().getLocalPart());

    final Attribute typeAttr = start.getAttributeByName(this.typeQName);
    if (typeAttr != null) {
      property.setType(typeAttr.getValue());
    }

    Value value;
    final Attribute nullAttr = start.getAttributeByName(this.nullQName);
    if (nullAttr == null) {
      final EdmTypeInfo typeInfo = StringUtils.isBlank(property.getType())
              ? null
              : new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build();

      final ODataPropertyType propType = typeInfo == null
              ? guessPropertyType(reader)
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
          value = fromComplex(reader, start);
          break;

        case PRIMITIVE:
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
}
