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
import org.apache.olingo.client.core.edm.xml.AbstractEdmDeserializer;

import java.io.IOException;
import java.net.URI;

public class ReferenceDeserializer extends AbstractEdmDeserializer<ReferenceImpl> {

  @Override
  protected ReferenceImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final ReferenceImpl reference = new ReferenceImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Uri".equals(jp.getCurrentName())) {
          reference.setUri(URI.create(jp.nextTextValue()));
        } else if ("Include".equals(jp.getCurrentName())) {
          jp.nextToken();
          reference.getIncludes().add(jp.readValueAs( IncludeImpl.class));
        } else if ("IncludeAnnotations".equals(jp.getCurrentName())) {
          jp.nextToken();
          reference.getIncludeAnnotations().add(jp.readValueAs( IncludeAnnotationsImpl.class));
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          reference.getAnnotations().add(jp.readValueAs( AnnotationImpl.class));
        }
      }
    }

    return reference;
  }

}
