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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ClientCsdlReturnType.ReturnTypeDeserializer.class)
class ClientCsdlReturnType extends CsdlReturnType implements Serializable {

  private static final long serialVersionUID = 6261092793901735110L;

  static class ReturnTypeDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlReturnType> {
    @Override
    protected ClientCsdlReturnType doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientCsdlReturnType returnType = new ClientCsdlReturnType();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Type".equals(jp.getCurrentName())) {
            String metadataTypeName = jp.nextTextValue();
            if (metadataTypeName.startsWith("Collection(")) {
              returnType.setType(metadataTypeName.substring(metadataTypeName.indexOf("(") + 1,
                      metadataTypeName.length() - 1));
              returnType.setCollection(true);
            } else {
              returnType.setType(metadataTypeName);
              returnType.setCollection(false);
            }
          } else if ("Nullable".equals(jp.getCurrentName())) {
            returnType.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
          } else if ("MaxLength".equals(jp.getCurrentName())) {
            final String maxLenght = jp.nextTextValue();
            returnType.setMaxLength("max".equalsIgnoreCase(maxLenght) ? Integer.MAX_VALUE : Integer.valueOf(maxLenght));
          } else if ("Precision".equals(jp.getCurrentName())) {
            returnType.setPrecision(Integer.valueOf(jp.nextTextValue()));
          } else if ("Scale".equals(jp.getCurrentName())) {
            final String scale = jp.nextTextValue();
            returnType.setScale("variable".equalsIgnoreCase(scale) ? 0 : Integer.valueOf(scale));
          } else if ("SRID".equals(jp.getCurrentName())) {
            final String srid = jp.nextTextValue();
            if (srid != null) {
              returnType.setSrid(SRID.valueOf(srid));
            }
          }
        }
      }

      return returnType;
    }
  }
}
