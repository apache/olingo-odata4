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
import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.client.api.edm.StoreGeneratedPattern;
import org.apache.olingo.client.core.edm.xml.v4.AnnotationImpl;
import org.apache.olingo.client.core.op.impl.AbstractEdmDeserializer;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class PropertyDeserializer extends AbstractEdmDeserializer<AbstractProperty> {

  @Override
  protected AbstractProperty doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractProperty property = ODataServiceVersion.V30 == client.getServiceVersion()
            ? new org.apache.olingo.client.core.edm.xml.v3.PropertyImpl()
            : new org.apache.olingo.client.core.edm.xml.v4.PropertyImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          property.setName(jp.nextTextValue());
        } else if ("Type".equals(jp.getCurrentName())) {
          property.setType(jp.nextTextValue());
        } else if ("Nullable".equals(jp.getCurrentName())) {
          property.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("DefaultValue".equals(jp.getCurrentName())) {
          property.setDefaultValue(jp.nextTextValue());
        } else if ("MaxLength".equals(jp.getCurrentName())) {
          final String maxLenght = jp.nextTextValue();
          property.setMaxLength(maxLenght.equalsIgnoreCase("max") ? Integer.MAX_VALUE : Integer.valueOf(maxLenght));
        } else if ("FixedLength".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFixedLength(BooleanUtils.toBoolean(jp.nextTextValue()));
          }
        } else if ("Precision".equals(jp.getCurrentName())) {
          property.setPrecision(Integer.valueOf(jp.nextTextValue()));
        } else if ("Scale".equals(jp.getCurrentName())) {
          final String scale = jp.nextTextValue();
          property.setScale(scale.equalsIgnoreCase("variable") ? 0 : Integer.valueOf(scale));
        } else if ("Unicode".equals(jp.getCurrentName())) {
          property.setUnicode(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("Collation".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setCollation(jp.nextTextValue());
          }
        } else if ("SRID".equals(jp.getCurrentName())) {
          property.setSrid(jp.nextTextValue());
        } else if ("ConcurrencyMode".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setConcurrencyMode(ConcurrencyMode.valueOf(jp.nextTextValue()));
          }
        } else if ("StoreGeneratedPattern".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setStoreGeneratedPattern(StoreGeneratedPattern.valueOf(jp.nextTextValue()));
          }
        } else if ("FC_SourcePath".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFcSourcePath(jp.nextTextValue());
          }
        } else if ("FC_TargetPath".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFcTargetPath(jp.nextTextValue());
          }
        } else if ("FC_ContentKind".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFcContentKind(EdmContentKind.valueOf(jp.nextTextValue()));
          }
        } else if ("FC_NsPrefix".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFcNSPrefix(jp.nextTextValue());
          }
        } else if ("FC_NsUri".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFcNSURI(jp.nextTextValue());
          }
        } else if ("FC_KeepInContent".equals(jp.getCurrentName())) {
          if (property instanceof org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) {
            ((org.apache.olingo.client.core.edm.xml.v3.PropertyImpl) property).
                    setFcKeepInContent(BooleanUtils.toBoolean(jp.nextTextValue()));
          }
        } else if ("Annotation".equals(jp.getCurrentName())) {
          ((org.apache.olingo.client.core.edm.xml.v4.PropertyImpl) property).
                  setAnnotation(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return property;
  }

}
