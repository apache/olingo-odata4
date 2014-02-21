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
package org.apache.olingo.odata4.client.core.edm.v4.annotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import org.apache.olingo.odata4.client.core.data.impl.AbstractEdmDeserializer;
import org.apache.olingo.odata4.client.core.edm.v4.AnnotationImpl;

public class ApplyDeserializer extends AbstractEdmDeserializer<Apply> {

  @Override
  protected Apply doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final Apply apply = new Apply();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Function".equals(jp.getCurrentName())) {
          apply.setFunction(jp.nextTextValue());
        } else if ("Annotation".equals(jp.getCurrentName())) {
          apply.setAnnotation(jp.getCodec().readValue(jp, AnnotationImpl.class));
        } else if (isAnnotationConstExprConstruct(jp)) {
          apply.getParameters().add(parseAnnotationConstExprConstruct(jp));
        } else {
          apply.getParameters().add(jp.getCodec().readValue(jp, DynExprConstruct.class));
        }
      }
    }

    return apply;
  }

}
