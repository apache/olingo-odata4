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
package org.apache.olingo.client.core.edm.xml.annotation;

import java.io.IOException;
import java.io.Serializable;

import org.apache.olingo.client.core.edm.xml.AbstractClientCsdlEdmDeserializer;
import org.apache.olingo.client.core.edm.xml.ClientCsdlAnnotation;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.annotation.CsdlCast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ClientCsdlCast.CastDeserializer.class)
class ClientCsdlCast extends CsdlCast implements Serializable {

  private static final long serialVersionUID = 3312415984116005313L;

  static class CastDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlCast> {

    @Override
    protected ClientCsdlCast doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientCsdlCast cast = new ClientCsdlCast();
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Type".equals(jp.getCurrentName())) {
            cast.setType(jp.nextTextValue());
          } else if ("Annotation".equals(jp.getCurrentName())) {
            cast.getAnnotations().add(jp.readValueAs(ClientCsdlAnnotation.class));
          } else if ("MaxLength".equals(jp.getCurrentName())) {
            final String maxLenght = jp.nextTextValue();
            cast.setMaxLength("max".equalsIgnoreCase(maxLenght) ? Integer.MAX_VALUE : Integer.valueOf(maxLenght));
          } else if ("Precision".equals(jp.getCurrentName())) {
            cast.setPrecision(Integer.valueOf(jp.nextTextValue()));
          } else if ("Scale".equals(jp.getCurrentName())) {
            final String scale = jp.nextTextValue();
            cast.setScale("variable".equalsIgnoreCase(scale) ? 0 : Integer.valueOf(scale));
          } else if ("SRID".equals(jp.getCurrentName())) {
            final String srid = jp.nextTextValue();
            if (srid != null) {
              cast.setSrid(SRID.valueOf(srid));
            }
          } else {
            cast.setValue(jp.readValueAs(ClientCsdlDynamicExpression.class));
          }
        }
      }
      return cast;
    }
  }
}
