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
package org.apache.olingo.client.core.data;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.olingo.client.api.data.CollectionValue;
import org.apache.olingo.client.api.data.Property;
import org.apache.olingo.client.api.data.Value;
import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;
import org.apache.olingo.client.api.domain.ODataJClientEdmType;

abstract class AbstractJsonSerializer<T> extends ODataJacksonSerializer<T> {

  private static final ODataJClientEdmPrimitiveType[] NUMBER_TYPES = {
    ODataJClientEdmPrimitiveType.Byte, ODataJClientEdmPrimitiveType.SByte,
    ODataJClientEdmPrimitiveType.Single, ODataJClientEdmPrimitiveType.Double,
    ODataJClientEdmPrimitiveType.Int16, ODataJClientEdmPrimitiveType.Int32, ODataJClientEdmPrimitiveType.Int64
  };

  private final JSONGeoValueSerializer geoSerializer = new JSONGeoValueSerializer();

  private void collection(final JsonGenerator jgen, final String itemType, final CollectionValue value)
          throws IOException {

    jgen.writeStartArray();
    for (Value item : value.get()) {
      value(jgen, itemType, item);
    }
    jgen.writeEndArray();
  }

  private void value(final JsonGenerator jgen, final String type, final Value value) throws IOException {
    final ODataJClientEdmType typeInfo = type == null
            ? null
            : new ODataJClientEdmType(type);

    if (value.isNull()) {
      jgen.writeNull();
    } else if (value.isSimple()) {
      final boolean isNumber = typeInfo == null
              ? NumberUtils.isNumber(value.asSimple().get())
              : ArrayUtils.contains(NUMBER_TYPES, typeInfo.getPrimitiveType());
      final boolean isBoolean = typeInfo == null
              ? (value.asSimple().get().equalsIgnoreCase(Boolean.TRUE.toString())
              || value.asSimple().get().equalsIgnoreCase(Boolean.FALSE.toString()))
              : typeInfo.getPrimitiveType() == ODataJClientEdmPrimitiveType.Boolean;

      if (isNumber) {
        jgen.writeNumber(value.asSimple().get());
      } else if (isBoolean) {
        jgen.writeBoolean(BooleanUtils.toBoolean(value.asSimple().get()));
      } else {
        jgen.writeString(value.asSimple().get());
      }
    } else if (value.isGeospatial()) {
      jgen.writeStartObject();
      geoSerializer.serialize(jgen, value.asGeospatial().get());
      jgen.writeEndObject();
    } else if (value.isCollection()) {
      collection(jgen, typeInfo == null ? null : typeInfo.getBaseType(), value.asCollection());
    } else if (value.isComplex()) {
      jgen.writeStartObject();
      for (Property property : value.asComplex().get()) {
        property(jgen, property, property.getName());
      }
      jgen.writeEndObject();
    }
  }

  protected void property(final JsonGenerator jgen, final Property property, final String name) throws IOException {
    jgen.writeFieldName(name);
    value(jgen, property.getType(), property.getValue());
  }
}
