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
package org.apache.olingo.commons.core.serialization;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Writes out JSON string from <tt>PropertyImpl</tt>.
 */
public class JsonPropertySerializer extends JsonSerializer {

  public JsonPropertySerializer(final ODataServiceVersion version, final boolean serverMode) {
    super(version, serverMode);
  }

  protected void doSerialize(final Property property, final JsonGenerator jgen) throws IOException {
    doContainerSerialize(new ResWrap<Property>((URI) null, null, property), jgen);
  }

  protected void doContainerSerialize(final ResWrap<Property> container, final JsonGenerator jgen)
          throws IOException {

    final Property property = container.getPayload();

    jgen.writeStartObject();

    if (serverMode && container.getContextURL() != null) {
      jgen.writeStringField(version.compareTo(ODataServiceVersion.V40) >= 0
              ? Constants.JSON_CONTEXT : Constants.JSON_METADATA,
              container.getContextURL().getURI().toASCIIString());
    }

    if (StringUtils.isNotBlank(property.getType())) {
      jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_TYPE),
              new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build().external(version));
    }

    for (Annotation annotation : property.getAnnotations()) {
      valuable(jgen, annotation, "@" + annotation.getTerm());
    }

    if (property.getValue().isNull()) {
      jgen.writeBooleanField(Constants.JSON_NULL, true);
    } else if (property.getValue().isPrimitive()) {
      final EdmTypeInfo typeInfo = property.getType() == null
              ? null
              : new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build();

      jgen.writeFieldName(Constants.VALUE);
      primitiveValue(jgen, typeInfo, property.getValue().asPrimitive());
    } else if (property.getValue().isEnum()) {
      jgen.writeStringField(Constants.VALUE, property.getValue().asEnum().get());
    } else if (property.getValue().isGeospatial() || property.getValue().isCollection()) {
      valuable(jgen, property, Constants.VALUE);
    } else if (property.getValue().isComplex()) {
      for (Property cproperty : property.getValue().asComplex().get()) {
        valuable(jgen, cproperty, cproperty.getName());
      }
    }

    jgen.writeEndObject();
  }
}
