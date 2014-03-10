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
package org.apache.olingo.client.core.edm.xml.v4;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.client.core.op.impl.AbstractEdmDeserializer;

public class TypeDefinitionDeserializer extends AbstractEdmDeserializer<TypeDefinitionImpl> {

  @Override
  protected TypeDefinitionImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final TypeDefinitionImpl typeDefinition = new TypeDefinitionImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          typeDefinition.setName(jp.nextTextValue());
        } else if ("UnderlyingType".equals(jp.getCurrentName())) {
          typeDefinition.setUnderlyingType(jp.nextTextValue());
        } else if ("MaxLength".equals(jp.getCurrentName())) {
          typeDefinition.setMaxLength(jp.nextIntValue(0));
        } else if ("Unicode".equals(jp.getCurrentName())) {
          typeDefinition.setUnicode(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("Precision".equals(jp.getCurrentName())) {
          typeDefinition.setPrecision(jp.nextIntValue(0));
        } else if ("Scale".equals(jp.getCurrentName())) {
          typeDefinition.setScale(jp.nextIntValue(0));
        } else if ("SRID".equals(jp.getCurrentName())) {
          typeDefinition.setSrid(jp.nextTextValue());
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          typeDefinition.getAnnotations().add(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return typeDefinition;
  }

}
