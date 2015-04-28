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
package org.apache.olingo.client.core.edm.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = ClientFunctionImport.FunctionImportDeserializer.class)
class ClientFunctionImport extends CsdlFunctionImport {

  private static final long serialVersionUID = -1686801084142932402L;

  static class FunctionImportDeserializer extends AbstractClientEdmDeserializer<ClientFunctionImport> {
    @Override
    protected ClientFunctionImport doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {

      final ClientFunctionImport functImpImpl = new ClientFunctionImport();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Name".equals(jp.getCurrentName())) {
            functImpImpl.setName(jp.nextTextValue());
          } else if ("Function".equals(jp.getCurrentName())) {
            functImpImpl.setFunction(jp.nextTextValue());
          } else if ("EntitySet".equals(jp.getCurrentName())) {
            functImpImpl.setEntitySet(jp.nextTextValue());
          } else if ("IncludeInServiceDocument".equals(jp.getCurrentName())) {
            functImpImpl.setIncludeInServiceDocument(BooleanUtils.toBoolean(jp.nextTextValue()));
          } else if ("Annotation".equals(jp.getCurrentName())) {
            jp.nextToken();
            functImpImpl.getAnnotations().add(jp.readValueAs(ClientAnnotation.class));
          }
        }
      }

      return functImpImpl;
    }
  }
}
