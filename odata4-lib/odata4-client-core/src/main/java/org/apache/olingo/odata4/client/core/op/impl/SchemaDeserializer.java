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
package org.apache.olingo.odata4.client.core.op.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import org.apache.olingo.odata4.client.core.edm.AbstractSchema;
import org.apache.olingo.odata4.client.core.edm.v3.AssociationImpl;
import org.apache.olingo.odata4.client.core.edm.v3.UsingImpl;
import org.apache.olingo.odata4.client.core.edm.v3.ValueTermImpl;
import org.apache.olingo.odata4.client.core.edm.v4.ActionImpl;
import org.apache.olingo.odata4.client.core.edm.v4.AnnotationImpl;
import org.apache.olingo.odata4.client.core.edm.v4.FunctionImpl;
import org.apache.olingo.odata4.client.core.edm.v4.TypeDefinitionImpl;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;

@SuppressWarnings("rawtypes")
public class SchemaDeserializer extends AbstractEdmDeserializer<AbstractSchema> {

  @Override
  protected AbstractSchema doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

    final AbstractSchema schema = ODataServiceVersion.V30 == client.getServiceVersion()
            ? new org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl()
            : new org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl();

    for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
      final JsonToken token = jp.getCurrentToken();
      if (token == JsonToken.FIELD_NAME) {
        if ("Namespace".equals(jp.getCurrentName())) {
          schema.setNamespace(jp.nextTextValue());
        } else if ("Alias".equals(jp.getCurrentName())) {
          schema.setAlias(jp.nextTextValue());
        } else if ("Using".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                  getUsings().add(jp.readValueAs( UsingImpl.class));
        } else if ("Association".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                  getAssociations().add(jp.readValueAs( AssociationImpl.class));
        } else if ("ComplexType".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (schema instanceof org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) {
            ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                    getComplexTypes().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v3.ComplexTypeImpl.class));
          } else {
            ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).
                    getComplexTypes().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v4.ComplexTypeImpl.class));
          }
        } else if ("EntityType".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (schema instanceof org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) {
            ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                    getEntityTypes().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v3.EntityTypeImpl.class));
          } else {
            ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).
                    getEntityTypes().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v4.EntityTypeImpl.class));
          }
        } else if ("EnumType".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (schema instanceof org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) {
            ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                    getEnumTypes().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v3.EnumTypeImpl.class));
          } else {
            ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).
                    getEnumTypes().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v4.EnumTypeImpl.class));
          }
        } else if ("ValueTerm".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                  getValueTerms().add(jp.readValueAs( ValueTermImpl.class));
        } else if ("EntityContainer".equals(jp.getCurrentName())) {
          jp.nextToken();

          if (schema instanceof org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) {
            ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).
                    getEntityContainers().add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v3.EntityContainerImpl.class));
          } else {
            org.apache.olingo.odata4.client.core.edm.v4.EntityContainerImpl entityContainer
                    = jp.readValueAs(
                            org.apache.olingo.odata4.client.core.edm.v4.EntityContainerImpl.class);
            entityContainer.setDefaultEntityContainer(true);
            ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).
                    setEntityContainer(entityContainer);
          }
        } else if ("Annotations".equals(jp.getCurrentName())) {
          jp.nextToken();
          if (schema instanceof org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) {
            ((org.apache.olingo.odata4.client.core.edm.v3.SchemaImpl) schema).getAnnotationsList().
                    add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v3.AnnotationsImpl.class));
          } else {
            ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).getAnnotationsList().
                    add(jp.readValueAs(
                                    org.apache.olingo.odata4.client.core.edm.v4.AnnotationsImpl.class));
          }
        } else if ("Action".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).getActions().
                  add(jp.readValueAs( ActionImpl.class));
        } else if ("Annotation".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).getAnnotations().
                  add(jp.readValueAs( AnnotationImpl.class));
        } else if ("Function".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).getFunctions().
                  add(jp.readValueAs( FunctionImpl.class));
        } else if ("TypeDefinition".equals(jp.getCurrentName())) {
          jp.nextToken();
          ((org.apache.olingo.odata4.client.core.edm.v4.SchemaImpl) schema).
                  getTypeDefinitions().add(jp.readValueAs( TypeDefinitionImpl.class));
        }
      }
    }

    return schema;
  }
}
