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

import org.apache.olingo.client.core.edm.xml.OnDeleteImpl;
import org.apache.olingo.client.core.op.impl.AbstractEdmDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

import org.apache.commons.lang3.BooleanUtils;

public class NavigationPropertyDeserializer extends AbstractEdmDeserializer<NavigationPropertyImpl> {

  @Override
  protected NavigationPropertyImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final NavigationPropertyImpl property = new NavigationPropertyImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          property.setName(jp.nextTextValue());
        } else if ("Type".equals(jp.getCurrentName())) {
          property.setType(jp.nextTextValue());
        } else if ("Nullable".equals(jp.getCurrentName())) {
          property.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("Partner".equals(jp.getCurrentName())) {
          property.setPartner(jp.nextTextValue());
        } else if ("ContainsTarget".equals(jp.getCurrentName())) {
          property.setContainsTarget(BooleanUtils.toBoolean(jp.nextTextValue()));
        } else if ("ReferentialConstraint".equals(jp.getCurrentName())) {
          jp.nextToken();
          property.getReferentialConstraints().add(jp.readValueAs(ReferentialConstraintImpl.class));
        } else if ("OnDelete".equals(jp.getCurrentName())) {
          jp.nextToken();
          property.setOnDelete(jp.readValueAs(OnDeleteImpl.class));
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          property.setAnnotation(jp.readValueAs(AnnotationImpl.class));
        }
      }
    }

    return property;
  }

}
