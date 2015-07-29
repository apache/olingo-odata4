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

package org.apache.olingo.client.core.edm.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


public class ClientCsdlSchemasDeserializer extends JsonDeserializer<ClientJsonSchemaCsdl> {
    private ClientJsonSchemaCsdl jsonCsdl;

    public ClientCsdlSchemasDeserializer(ClientJsonSchemaCsdl jsonCsdl) {
        this.jsonCsdl = jsonCsdl;
    }

    @Override
    public ClientJsonSchemaCsdl deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {

        final ObjectNode tree = parser.getCodec().readTree(parser);
        Iterator<Map.Entry<String, JsonNode>> iterator = tree.fields();
        while (iterator.hasNext()) {
            CsdlSchema schema = new CsdlSchema();
            jsonCsdl.getSchemas().add(schema);
            Map.Entry<String, JsonNode> schemaNode = iterator.next();
            String nameSpace = schemaNode.getKey();
            schema.setNamespace(nameSpace);
            schema.setAlias(schemaNode.getValue().get("alias").asText());

            JsonNode entityContainer = schemaNode.getValue().get("entityContainer");
            if (entityContainer != null) {
                CsdlEntityContainer container = new CsdlEntityContainer();
                schema.setEntityContainer(container);
                container.setName(entityContainer.get("name").asText());
                if(entityContainer.has("extend")){
                    container.setExtendsContainer(entityContainer.get("extend").asText());
                }
                Iterator<Map.Entry<String, JsonNode>> itr = entityContainer.get("entitySets").fields();
                while (itr.hasNext()) {
                    Map.Entry<String, JsonNode> entitySetEntry = itr.next();
                    JsonNode entitySetNode = entitySetEntry.getValue();
                    CsdlEntitySet entitySet = new ClientCsdlEntitySetDeserializer(schema, entitySetEntry.getKey())
                            .deserialize(entitySetNode.traverse(parser.getCodec()), ctxt);
                    container.getEntitySets().add(entitySet);
                }
                if (entityContainer.has("singletons")) {
                    Iterator<Map.Entry<String, JsonNode>> itr2 = entityContainer.get("singletons").fields();
                    while (itr.hasNext()) {
                        Map.Entry<String, JsonNode> singletonEntry = itr2.next();
                        JsonNode singletonNode = singletonEntry.getValue();
                        CsdlSingleton singleton = new ClientCsdlSingletonDeserializer(singletonEntry.getKey())
                                .deserialize(singletonNode.traverse(parser.getCodec()), ctxt);
                        container.getSingletons().add(singleton);
                    }
                }
                if (entityContainer.has("functionImports")) {
                    Iterator<Map.Entry<String, JsonNode>> itr3 = entityContainer.get("functionImports").fields();
                    while (itr.hasNext()) {
                        Map.Entry<String, JsonNode> functionImportEntry = itr3.next();
                        JsonNode functionImportNode = functionImportEntry.getValue();
                        CsdlFunctionImport functionImport = new ClientCsdlFunctionImportDeserializer
                                (functionImportEntry.getKey())
                                .deserialize(functionImportNode.traverse(parser.getCodec()), ctxt);
                        container.getFunctionImports().add(functionImport);
                    }
                }
                if (entityContainer.has("actionImports")) {
                    Iterator<Map.Entry<String, JsonNode>> itr4 = entityContainer.get("actionImports").fields();
                    while (itr.hasNext()) {
                        Map.Entry<String, JsonNode> actionImportEntry = itr4.next();
                        JsonNode actionImportNode = actionImportEntry.getValue();
                        CsdlActionImport actionImport = new ClientCsdlActionImportDeserializer
                                (actionImportEntry.getKey())
                                .deserialize(actionImportNode.traverse(parser.getCodec()), ctxt);
                        container.getActionImports().add(actionImport);
                    }
                }
            }

            JsonNode actions = schemaNode.getValue().get("actions");
            if (actions != null) {
                Iterator<JsonNode> itr = actions.elements();
                while (itr.hasNext()) {
                    JsonNode actionNode = itr.next();
                    CsdlAction action = new ClientCsdlActionDeserializer()
                            .deserialize(actionNode.traverse(parser.getCodec()), ctxt);
                    schema.getActions().add(action);
                }
            }

            JsonNode functions = schemaNode.getValue().get("functions");
            if (functions != null) {
                Iterator<JsonNode> itr = functions.elements();
                while (itr.hasNext()) {
                    JsonNode functionNode = itr.next();
                    CsdlFunction function = new ClientCsdlFunctionDeserializer()
                            .deserialize(functionNode.traverse(parser.getCodec()), ctxt);
                    schema.getFunctions().add(function);
                }
            }
        }
        return jsonCsdl;
    }
}
