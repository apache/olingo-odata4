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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

class AtomPropertySerializer extends AbstractAtomDealer {

  private final AtomGeoValueSerializer geoSerializer;

  public AtomPropertySerializer(final ODataServiceVersion version) {
    super(version);
    this.geoSerializer = new AtomGeoValueSerializer();
  }

  private void collection(final XMLStreamWriter writer, final CollectionValue value) throws XMLStreamException {
    for (Value item : value.get()) {
      writer.writeStartElement(Constants.PREFIX_DATASERVICES, Constants.ELEM_ELEMENT,
              version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));
      value(writer, item);
      writer.writeEndElement();
    }
  }

  private void value(final XMLStreamWriter writer, final Value value) throws XMLStreamException {
    if (value.isSimple()) {
      writer.writeCharacters(value.asSimple().get());
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

    writer.writeStartElement(Constants.PREFIX_DATASERVICES, property.getName(),
            version.getNamespaceMap().get(ODataServiceVersion.NS_DATASERVICES));
    if (standalone) {
      namespaces(writer);
    }
    if (StringUtils.isNotBlank(property.getType())) {
      writer.writeAttribute(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.ATTR_TYPE, property.getType());
    }

    if (property.getValue().isNull()) {
      writer.writeAttribute(Constants.PREFIX_METADATA, version.getNamespaceMap().get(ODataServiceVersion.NS_METADATA),
              Constants.ATTR_NULL, Boolean.TRUE.toString());
    } else {
      value(writer, property.getValue());
    }

    writer.writeEndElement();
  }

  public void property(final XMLStreamWriter writer, final Property property) throws XMLStreamException {
    property(writer, property, true);
  }
}
