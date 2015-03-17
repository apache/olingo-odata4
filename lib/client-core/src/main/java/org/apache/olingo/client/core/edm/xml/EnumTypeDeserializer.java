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
package org.apache.olingo.client.core.edm.xml;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class EnumTypeDeserializer extends AbstractEdmDeserializer<EnumTypeImpl> {

  @Override
  protected EnumTypeImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
      throws IOException, JsonProcessingException {

    final EnumTypeImpl enumType = new EnumTypeImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          enumType.setName(jp.nextTextValue());
        } else if ("UnderlyingType".equals(jp.getCurrentName())) {
          enumType.setUnderlyingType(jp.nextTextValue());
        } else if ("IsFlags".equals(jp.getCurrentName())) {
          enumType.setFlags(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("Member".equals(jp.getCurrentName())) {
          jp.nextToken();
          enumType.getMembers().add(jp.readValueAs(EnumMemberImpl.class));
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          enumType.getAnnotations().add(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return enumType;
  }
}
