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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

import org.apache.olingo.client.core.edm.xml.v4.annotation.DynExprConstructImpl;
import org.apache.olingo.client.core.edm.xml.AbstractEdmDeserializer;

public class AnnotationDeserializer extends AbstractEdmDeserializer<AnnotationImpl> {

  @Override
  protected AnnotationImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AnnotationImpl annotation = new AnnotationImpl();

    for (; jp.getCurrentToken() != null && jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Term".equals(jp.getCurrentName())) {
          annotation.setTerm(jp.nextTextValue());
        } else if ("Qualifier".equals(jp.getCurrentName())) {
          annotation.setQualifier(jp.nextTextValue());
        } else if (isAnnotationConstExprConstruct(jp)) {
          // Constant Expressions
          annotation.setConstExpr(parseAnnotationConstExprConstruct(jp));
        } else {
          // Dynamic Expressions
          annotation.setDynExpr(jp.readValueAs(DynExprConstructImpl.class));
        }
      }
    }

    return annotation;
  }

}
