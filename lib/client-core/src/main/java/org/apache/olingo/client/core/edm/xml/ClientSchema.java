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
import org.apache.olingo.commons.api.edm.provider.Schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = ClientSchema.SchemaDeserializer.class)
public class ClientSchema extends Schema {

  private static final long serialVersionUID = 1911087363912024939L;

  static class SchemaDeserializer extends AbstractClientEdmDeserializer<ClientSchema> {
    @Override
    protected ClientSchema doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientSchema schema = new ClientSchema();

      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if ("Namespace".equals(jp.getCurrentName())) {
            schema.setNamespace(jp.nextTextValue());
          } else if ("Alias".equals(jp.getCurrentName())) {
            schema.setAlias(jp.nextTextValue());
          } else if ("ComplexType".equals(jp.getCurrentName())) {
            jp.nextToken();
            schema.getComplexTypes().add(jp.readValueAs(ClientComplexType.class));
          } else if ("EntityType".equals(jp.getCurrentName())) {
            jp.nextToken();
            schema.getEntityTypes().add(jp.readValueAs(ClientEntityType.class));
          } else if ("EnumType".equals(jp.getCurrentName())) {
            jp.nextToken();
            schema.getEnumTypes().add(jp.readValueAs(ClientEnumType.class));
          } else if ("EntityContainer".equals(jp.getCurrentName())) {
            jp.nextToken();
            ClientEntityContainer entityContainer = jp.readValueAs(ClientEntityContainer.class);
            schema.setEntityContainer(entityContainer);
          } else if ("Action".equals(jp.getCurrentName())) {
            jp.nextToken();
            schema.getActions().add(jp.readValueAs(ClientAction.class));
          } else if ("Function".equals(jp.getCurrentName())) {
            jp.nextToken();
            schema.getFunctions().add(jp.readValueAs(ClientFunction.class));
          } else if ("TypeDefinition".equals(jp.getCurrentName())) {
            jp.nextToken();
            schema.getTypeDefinitions().add(jp.readValueAs(ClientTypeDefinition.class));
          }
        } else if ("Annotations".equals(jp.getCurrentName())) {
          jp.nextToken();
          schema.getAnnotationGroups().add(jp.readValueAs(ClientAnnotations.class));
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          schema.getAnnotations().add(jp.readValueAs(ClientAnnotation.class));
        } else if ("Term".equals(jp.getCurrentName())) {
          jp.nextToken();
          schema.getTerms().add(jp.readValueAs(ClientTerm.class));
        }
      }

      return schema;
    }
  }
}
