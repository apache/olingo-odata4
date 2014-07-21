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
package org.apache.olingo.client.core.serialization;

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
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializer;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.core.serialization.AtomDeserializer;
import org.apache.olingo.commons.core.serialization.JsonDeserializer;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractODataDeserializer {

  protected final ODataServiceVersion version;

  protected final ODataDeserializer deserializer;

  public AbstractODataDeserializer(final ODataServiceVersion version, final boolean serverMode,
          final ODataFormat format) {
    this.version = version;
    if (format == ODataFormat.XML || format == ODataFormat.ATOM) {
      deserializer = new AtomDeserializer(version);
    } else {
      deserializer = new JsonDeserializer(version, serverMode);
    }
  }

  public ResWrap<EntitySet> toEntitySet(final InputStream input) throws ODataDeserializerException {
    return deserializer.toEntitySet(input);
  }

  public ResWrap<Entity> toEntity(final InputStream input) throws ODataDeserializerException {
    return deserializer.toEntity(input);
  }

  public ResWrap<Property> toProperty(final InputStream input) throws ODataDeserializerException {
    return deserializer.toProperty(input);
  }

  public ODataError toError(final InputStream input) throws ODataDeserializerException {
    return deserializer.toError(input);
  }

  protected XmlMapper getXmlMapper() {
    final XmlMapper xmlMapper = new XmlMapper(
            new XmlFactory(new InputFactoryImpl(), new OutputFactoryImpl()), new JacksonXmlModule());

    xmlMapper.setInjectableValues(new InjectableValues.Std().
            addValue(ODataServiceVersion.class, version).
            addValue(Boolean.class, Boolean.FALSE));

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
}
