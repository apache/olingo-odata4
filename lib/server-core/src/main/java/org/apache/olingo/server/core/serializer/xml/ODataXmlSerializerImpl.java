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
package org.apache.olingo.server.core.serializer.xml;

import java.io.InputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODataXmlSerializerImpl implements ODataSerializer {

  /** The default character set is UTF-8. */
  public static final String DEFAULT_CHARSET = "UTF-8";

  private static final Logger log = LoggerFactory.getLogger(ODataXmlSerializerImpl.class);

  @Override
  public InputStream serviceDocument(final Edm edm, final String serviceRoot) throws SerializerException {
    throw new SerializerException("Service Document not implemented for XML format",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream metadataDocument(final ServiceMetadata serviceMetadata) throws SerializerException {
    CircleStreamBuffer buffer;
    XMLStreamWriter xmlStreamWriter = null;

    // TODO: move stream initialization into separate method
    try {
      buffer = new CircleStreamBuffer();
      xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(buffer.getOutputStream(), DEFAULT_CHARSET);
      MetadataDocumentXmlSerializer serializer = new MetadataDocumentXmlSerializer(serviceMetadata);
      serializer.writeMetadataDocument(xmlStreamWriter);
      xmlStreamWriter.flush();
      xmlStreamWriter.close();

      return buffer.getInputStream();
    } catch (final XMLStreamException e) {
      log.error(e.getMessage(), e);
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    } finally {
      if (xmlStreamWriter != null) {
        try {
          xmlStreamWriter.close();
        } catch (XMLStreamException e) {
          throw new SerializerException("An I/O exception occurred.", e,
              SerializerException.MessageKeys.IO_EXCEPTION);
        }
      }
    }
  }

  @Override
  public InputStream entity(final EdmEntityType entityType, final Entity entity,
      final EntitySerializerOptions options) throws SerializerException {
    throw new SerializerException("Entity serialization not implemented for XML format",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream entityCollection(final EdmEntityType entityType, final EntitySet entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {
    throw new SerializerException("Entityset serialization not implemented for XML format",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream error(ODataServerError error) throws SerializerException {
    throw new SerializerException("error serialization not implemented for XML format",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream primitive(final EdmPrimitiveType type, final Property property,
      final PrimitiveSerializerOptions options) throws SerializerException {
    throw new SerializerException("Serialization not implemented for XML format.",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream complex(final EdmComplexType type, final Property property,
      final ComplexSerializerOptions options) throws SerializerException {
    throw new SerializerException("Serialization not implemented for XML format.",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream primitiveCollection(final EdmPrimitiveType type, final Property property,
      final PrimitiveSerializerOptions options) throws SerializerException {
    throw new SerializerException("Serialization not implemented for XML format.",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Override
  public InputStream complexCollection(final EdmComplexType type, final Property property,
      final ComplexSerializerOptions options) throws SerializerException {
    throw new SerializerException("Serialization not implemented for XML format.",
        SerializerException.MessageKeys.NOT_IMPLEMENTED);
  }
}
