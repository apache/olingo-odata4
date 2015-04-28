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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.annotation.DynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.IsOf;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = ClientCsdlIsOf.IsOfDeserializer.class)
class ClientCsdlIsOf extends AbstractClientCsdlAnnotatableDynamicAnnotationExpression implements IsOf {

  private static final long serialVersionUID = -893355856129761174L;

  private String type;

  private Integer maxLength;

  private Integer precision;

  private Integer scale;

  private SRID srid;

  private DynamicAnnotationExpression value;

  @Override
  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public Integer getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(final Integer maxLength) {
    this.maxLength = maxLength;
  }

  @Override
  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(final Integer precision) {
    this.precision = precision;
  }

  @Override
  public Integer getScale() {
    return scale;
  }

  public void setScale(final Integer scale) {
    this.scale = scale;
  }

  @Override
  public SRID getSrid() {
    return srid;
  }

  public void setSrid(final SRID srid) {
    this.srid = srid;
  }

  @Override
  public DynamicAnnotationExpression getValue() {
    return value;
  }

  public void setValue(final DynamicAnnotationExpression value) {
    this.value = value;
  }

  static class IsOfDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlIsOf> {
    @Override
    protected ClientCsdlIsOf doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientCsdlIsOf isof = new ClientCsdlIsOf();
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Type".equals(jp.getCurrentName())) {
            isof.setType(jp.nextTextValue());
          } else if ("Annotation".equals(jp.getCurrentName())) {
            isof.getAnnotations().add(jp.readValueAs(ClientCsdlAnnotation.class));
          } else if ("MaxLength".equals(jp.getCurrentName())) {
            final String maxLenght = jp.nextTextValue();
            isof.setMaxLength(maxLenght.equalsIgnoreCase("max") ? Integer.MAX_VALUE : Integer.valueOf(maxLenght));
          } else if ("Precision".equals(jp.getCurrentName())) {
            isof.setPrecision(Integer.valueOf(jp.nextTextValue()));
          } else if ("Scale".equals(jp.getCurrentName())) {
            final String scale = jp.nextTextValue();
            isof.setScale(scale.equalsIgnoreCase("variable") ? 0 : Integer.valueOf(scale));
          } else if ("SRID".equals(jp.getCurrentName())) {
            final String srid = jp.nextTextValue();
            if (srid != null) {
              isof.setSrid(SRID.valueOf(srid));
            }
          } else {
            isof.setValue(jp.readValueAs(AbstractClientCsdlDynamicAnnotationExpression.class));
          }
        }
      }
      return isof;
    }
  }
}
