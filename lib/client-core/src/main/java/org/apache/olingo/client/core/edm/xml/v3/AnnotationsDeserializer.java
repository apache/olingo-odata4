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
package org.apache.olingo.client.core.edm.xml.v3;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

import org.apache.olingo.client.core.op.AbstractEdmDeserializer;

public class AnnotationsDeserializer extends AbstractEdmDeserializer<AnnotationsImpl> {

  @Override
  protected AnnotationsImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AnnotationsImpl annotations = new AnnotationsImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Target".equals(jp.getCurrentName())) {
          annotations.setTarget(jp.nextTextValue());
        } else if ("Qualifier".equals(jp.getCurrentName())) {
          annotations.setQualifier(jp.nextTextValue());
        } else if ("typeAnnotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          annotations.getTypeAnnotations().add(jp.readValueAs( TypeAnnotationImpl.class));
        } else if ("ValueAnnotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          annotations.getValueAnnotations().add(jp.readValueAs( ValueAnnotationImpl.class));
        }
      }
    }

    return annotations;
  }
}
