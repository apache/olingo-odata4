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
package org.apache.olingo.client.core.edm.xml;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.client.core.edm.xml.v4.AnnotationImpl;
import org.apache.olingo.commons.api.edm.geo.SRID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class ParameterDeserializer extends AbstractEdmDeserializer<AbstractParameter> {

  @Override
  protected AbstractParameter doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractParameter parameter = new org.apache.olingo.client.core.edm.xml.v4.ParameterImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          parameter.setName(jp.nextTextValue());
        } else if ("Type".equals(jp.getCurrentName())) {
          parameter.setType(jp.nextTextValue());
        } else if ("Nullable".equals(jp.getCurrentName())) {
          parameter.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("MaxLength".equals(jp.getCurrentName())) {
          final String maxLenght = jp.nextTextValue();
          parameter.setMaxLength(maxLenght.equalsIgnoreCase("max") ? Integer.MAX_VALUE : Integer.valueOf(maxLenght));
        } else if ("Precision".equals(jp.getCurrentName())) {
          parameter.setPrecision(Integer.valueOf(jp.nextTextValue()));
        } else if ("Scale".equals(jp.getCurrentName())) {
          final String scale = jp.nextTextValue();
          parameter.setScale(scale.equalsIgnoreCase("variable") ? 0 : Integer.valueOf(scale));
        } else if ("SRID".equals(jp.getCurrentName())) {
          final String srid = jp.nextTextValue();
          if (srid != null) {
            ((org.apache.olingo.client.core.edm.xml.v4.ParameterImpl) parameter).setSrid(SRID.valueOf(srid));
          }
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.client.core.edm.xml.v4.ParameterImpl) parameter).getAnnotations().
                  add(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return parameter;
  }

}
