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

import org.apache.olingo.client.core.edm.xml.AbstractEdmDeserializer;

public class AssociationSetDeserializer extends AbstractEdmDeserializer<AssociationSetImpl> {

  @Override
  protected AssociationSetImpl doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AssociationSetImpl associationSet = new AssociationSetImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Name".equals(jp.getCurrentName())) {
          associationSet.setName(jp.nextTextValue());
        } else if ("Association".equals(jp.getCurrentName())) {
          associationSet.setAssociation(jp.nextTextValue());
        } else if ("End".equals(jp.getCurrentName())) {
          jp.nextToken();
          associationSet.getEnds().add(jp.readValueAs( AssociationSetEndImpl.class));
        }
      }
    }

    return associationSet;
  }
}
