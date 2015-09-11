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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.serialization.ClientODataDeserializer;
import org.apache.olingo.client.api.serialization.ODataDeserializer;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.data.JSONServiceDocumentDeserializer;
import org.apache.olingo.client.core.data.XMLServiceDocumentDeserializer;
import org.apache.olingo.client.core.edm.ClientCsdlXMLMetadata;
import org.apache.olingo.client.core.edm.xml.ClientCsdlEdmx;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.format.ContentType;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class ClientODataDeserializerImpl implements ClientODataDeserializer {

  private final ODataDeserializer deserializer;
  private final ContentType contentType;

  public ClientODataDeserializerImpl(final boolean serverMode, final ContentType contentType) {
    this.contentType = contentType;
    if (contentType.isCompatible(ContentType.APPLICATION_ATOM_SVC)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML)
        || contentType.isCompatible(ContentType.APPLICATION_XML)) {
      deserializer = new AtomDeserializer();
    } else {
      deserializer = new JsonDeserializer(serverMode);
    }
  }

  @Override
  public ResWrap<EntityCollection> toEntitySet(final InputStream input) throws ODataDeserializerException {
    return deserializer.toEntitySet(input);
  }

  @Override
  public ResWrap<Entity> toEntity(final InputStream input) throws ODataDeserializerException {
    return deserializer.toEntity(input);
  }

  @Override
  public ResWrap<Property> toProperty(final InputStream input) throws ODataDeserializerException {
    return deserializer.toProperty(input);
  }

  @Override
  public ODataError toError(final InputStream input) throws ODataDeserializerException {
    return deserializer.toError(input);
  }

  protected XmlMapper getXmlMapper() {
    final XmlMapper xmlMapper = new XmlMapper(
        new XmlFactory(new InputFactoryImpl(), new OutputFactoryImpl()), new JacksonXmlModule());

    xmlMapper.setInjectableValues(new InjectableValues.Std().addValue(Boolean.class, Boolean.FALSE));

    xmlMapper.addHandler(new DeserializationProblemHandler() {
      @Override
      public boolean handleUnknownProperty(final DeserializationContext ctxt, final JsonParser jp,
          final com.fasterxml.jackson.databind.JsonDeserializer<?> deserializer,
          final Object beanOrClass, final String propertyName)
          throws IOException, JsonProcessingException {

        // skip any unknown property
        ctxt.getParser().skipChildren();
        return true;
      }
    });
    return xmlMapper;
  }

  @Override
  public XMLMetadata toMetadata(final InputStream input) {
    try {
      return new ClientCsdlXMLMetadata(getXmlMapper().readValue(input, ClientCsdlEdmx.class));
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not parse as Edmx document", e);
    }
  }

  @Override
  public ResWrap<ServiceDocument> toServiceDocument(final InputStream input) throws ODataDeserializerException {
    return contentType.isCompatible(ContentType.APPLICATION_XML) ?
        new XMLServiceDocumentDeserializer(false).toServiceDocument(input) :
        new JSONServiceDocumentDeserializer(false).toServiceDocument(input);
  }

  @Override
  public ResWrap<Delta> toDelta(final InputStream input) throws ODataDeserializerException {
    try {
      return contentType.isCompatible(ContentType.APPLICATION_ATOM_SVC)
          || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML) ?
          new AtomDeserializer().delta(input) :
          new JsonDeltaDeserializer(false).toDelta(input);
    } catch (final XMLStreamException e) {
      throw new ODataDeserializerException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataDeserializerException(e);
    }
  }
}
