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
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.edm.xml.v4.CSDLElement;
import org.apache.olingo.client.core.op.impl.AbstractEdmDeserializer;

public class TermDeserializer extends AbstractEdmDeserializer<TermImpl> {

  @Override
  protected TermImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final TermImpl term = new TermImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          term.setName(jp.nextTextValue());
        } else if ("Type".equals(jp.getCurrentName())) {
          term.setType(jp.nextTextValue());
        } else if ("BaseTerm".equals(jp.getCurrentName())) {
          term.setBaseTerm(jp.nextTextValue());
        } else if ("DefaultValue".equals(jp.getCurrentName())) {
          term.setDefaultValue(jp.nextTextValue());
        } else if ("Nullable".equals(jp.getCurrentName())) {
          term.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("MaxLength".equals(jp.getCurrentName())) {
          final String maxLenght = jp.nextTextValue();
          term.setMaxLength(maxLenght.equalsIgnoreCase("max") ? Integer.MAX_VALUE : Integer.valueOf(maxLenght));
        } else if ("Precision".equals(jp.getCurrentName())) {
          term.setPrecision(Integer.valueOf(jp.nextTextValue()));
        } else if ("Scale".equals(jp.getCurrentName())) {
          term.setScale(Integer.valueOf(jp.nextTextValue()));
        } else if ("SRID".equals(jp.getCurrentName())) {
          term.setSrid(jp.nextTextValue());
        } else if ("AppliesTo".equals(jp.getCurrentName())) {
          for (String split : StringUtils.split(jp.nextTextValue())) {
            term.getAppliesTo().add(CSDLElement.valueOf(split));
          }
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          term.setAnnotation(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return term;
  }

}
