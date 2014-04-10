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
package org.apache.olingo.commons.core.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Property;

/**
 * Writes out JSON string from <tt>JSONPropertyImpl</tt>.
 *
 * @see JSONPropertyImpl
 */
public class JSONPropertySerializer extends AbstractJsonSerializer<JSONPropertyImpl> {

  @Override
  protected void doSerialize(final JSONPropertyImpl property, final JsonGenerator jgen,
          final SerializerProvider provider) throws IOException, JsonProcessingException {
    doContainerSerialize(new Container<JSONPropertyImpl>(null, null, property), jgen, provider);
  }

  @Override
  protected void doContainerSerialize(
          final Container<JSONPropertyImpl> container, final JsonGenerator jgen, final SerializerProvider provider)
          throws IOException, JsonProcessingException {

    final Property property = container.getObject();

    jgen.writeStartObject();

    if (property.getValue().isNull()) {
      jgen.writeBooleanField(Constants.JSON_NULL, true);
    } else if (property.getValue().isPrimitive()) {
      jgen.writeStringField(Constants.VALUE, property.getValue().asPrimitive().get());
    } else if (property.getValue().isEnum()) {
      jgen.writeStringField(Constants.VALUE, property.getValue().asEnum().get());
    } else if (property.getValue().isGeospatial() || property.getValue().isCollection()) {
      property(jgen, property, Constants.VALUE);
    } else if (property.getValue().isComplex()) {
      for (Property cproperty : property.getValue().asComplex().get()) {
        property(jgen, cproperty, cproperty.getName());
      }
    }

    jgen.writeEndObject();
  }
}
