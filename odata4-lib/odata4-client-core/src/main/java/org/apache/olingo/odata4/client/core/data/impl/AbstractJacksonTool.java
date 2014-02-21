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
package org.apache.olingo.odata4.client.core.data.impl;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import org.apache.olingo.odata4.client.api.ODataClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractJacksonTool {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractJacksonTool.class);

  protected final ODataClient client;

  protected AbstractJacksonTool(final ODataClient client) {
    this.client = client;
  }

  protected ObjectMapper getObjectMapper() {
    final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    mapper.setInjectableValues(new InjectableValues.Std().addValue(ODataClient.class, client));

    mapper.setSerializerProvider(new InjectableSerializerProvider(mapper.getSerializerProvider(),
            mapper.getSerializationConfig().withAttribute(ODataClient.class, client),
            mapper.getSerializerFactory()));

    return mapper;
  }

  protected XmlMapper getXmlMapper() {
    final XmlMapper xmlMapper = new XmlMapper(
            new XmlFactory(new InputFactoryImpl(), new OutputFactoryImpl()), new JacksonXmlModule());

    xmlMapper.setInjectableValues(new InjectableValues.Std().addValue(ODataClient.class, client));

    xmlMapper.addHandler(new DeserializationProblemHandler() {

      @Override
      public boolean handleUnknownProperty(final DeserializationContext ctxt, final JsonParser jp,
              final JsonDeserializer<?> deserializer, final Object beanOrClass, final String propertyName)
              throws IOException, JsonProcessingException {

        // skip any unknown property
        LOG.warn("Skipping unknown property {}", propertyName);
        ctxt.getParser().skipChildren();
        return true;
      }
    });
    return xmlMapper;
  }

}
