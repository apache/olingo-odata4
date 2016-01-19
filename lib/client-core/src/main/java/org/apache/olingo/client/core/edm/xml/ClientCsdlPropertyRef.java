/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;

import java.io.IOException;
import java.io.Serializable;

@JsonDeserialize(using = ClientCsdlPropertyRef.PropertyRefDeserializer.class)
class ClientCsdlPropertyRef extends CsdlPropertyRef implements Serializable {

  private static final long serialVersionUID = 1504095609268590326L;

  static class PropertyRefDeserializer extends AbstractClientCsdlEdmDeserializer<CsdlPropertyRef> {
    @Override
    protected CsdlPropertyRef doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final CsdlPropertyRef propertyRef = new ClientCsdlPropertyRef();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Name".equals(jp.getCurrentName())) {
            propertyRef.setName(jp.nextTextValue());
          } else if ("Alias".equals(jp.getCurrentName())) {
            propertyRef.setAlias(jp.nextTextValue());
          }
        }
      }
      return propertyRef;
    }
  }
}
