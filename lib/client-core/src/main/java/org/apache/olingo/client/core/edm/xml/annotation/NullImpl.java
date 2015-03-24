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
package org.apache.olingo.client.core.edm.xml.annotation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.olingo.client.core.edm.xml.AbstractEdmDeserializer;
import org.apache.olingo.client.core.edm.xml.AnnotationImpl;
import org.apache.olingo.commons.api.edm.provider.annotation.Null;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = NullImpl.NullDeserializer.class)
public class NullImpl extends AbstractAnnotatableDynamicAnnotationExpression implements Null {

  private static final long serialVersionUID = -3148516847180393142L;

  static class NullDeserializer extends AbstractEdmDeserializer<NullImpl> {
    @Override
    protected NullImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final NullImpl _null = new NullImpl();
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Annotation".equals(jp.getCurrentName())) {
            _null.getAnnotations().add(jp.readValueAs(AnnotationImpl.class));
          }
        }
      }
      return _null;
    }
  }
}
