/**
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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.msopentech.odatajclient.engine.metadata.edm.v3.ValueTerm;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Association;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Using;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Action;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Annotation;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Function;
import com.msopentech.odatajclient.engine.metadata.edm.v4.TypeDefinition;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.IOException;

@SuppressWarnings("rawtypes")
public class SchemaDeserializer extends AbstractEdmDeserializer<AbstractSchema> {

    @Override
    protected AbstractSchema doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final AbstractSchema schema = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.metadata.edm.v3.Schema()
                : new com.msopentech.odatajclient.engine.metadata.edm.v4.Schema();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Namespace".equals(jp.getCurrentName())) {
                    schema.setNamespace(jp.nextTextValue());
                } else if ("Alias".equals(jp.getCurrentName())) {
                    schema.setAlias(jp.nextTextValue());
                } else if ("Using".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                            getUsings().add(jp.getCodec().readValue(jp, Using.class));
                } else if ("Association".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                            getAssociations().add(jp.getCodec().readValue(jp, Association.class));
                } else if ("ComplexType".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (schema instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                                getComplexTypes().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.ComplexType.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).
                                getComplexTypes().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType.class));
                    }
                } else if ("EntityType".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (schema instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                                getEntityTypes().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.EntityType.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).
                                getEntityTypes().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.EntityType.class));
                    }
                } else if ("EnumType".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (schema instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                                getEnumTypes().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.EnumType.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).
                                getEnumTypes().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.EnumType.class));
                    }
                } else if ("ValueTerm".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                            getValueTerms().add(jp.getCodec().readValue(jp, ValueTerm.class));
                } else if ("EntityContainer".equals(jp.getCurrentName())) {
                    jp.nextToken();

                    if (schema instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).
                                getEntityContainers().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer.class));
                    } else {
                        com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer entityContainer =
                                jp.getCodec().readValue(jp,
                                        com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer.class);
                        entityContainer.setDefaultEntityContainer(true);
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).
                                setEntityContainer(entityContainer);
                    }
                } else if ("Annotations".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (schema instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.Schema) schema).getAnnotationsList().
                                add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.Annotations.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).getAnnotationsList().
                                add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.Annotations.class));
                    }
                } else if ("Action".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).getActions().
                            add(jp.getCodec().readValue(jp, Action.class));
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).getAnnotations().
                            add(jp.getCodec().readValue(jp, Annotation.class));
                } else if ("Function".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).getFunctions().
                            add(jp.getCodec().readValue(jp, Function.class));
                } else if ("TypeDefinition".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.Schema) schema).
                            getTypeDefinitions().add(jp.getCodec().readValue(jp, TypeDefinition.class));
                }
            }
        }

        return schema;
    }
}
