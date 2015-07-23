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
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClientCsdlActionDeserializer extends JsonDeserializer<CsdlAction> {

    private CsdlSchema schema;

    public ClientCsdlActionDeserializer(CsdlSchema schema) {
        this.schema = schema;
    }

    @Override
    public CsdlAction deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        CsdlAction action = new CsdlAction();

        if (tree.has("name")) {
            action.setName(tree.get("name").asText());
        }
        if (tree.has("entitySetPath")) {
            action.setEntitySetPath(tree.get("entitySetPath").asText());
        }
        if (tree.has("isBound")) {
            action.setBound(tree.get("isBound").asBoolean());
        }

        if (tree.has("returnType")) {
            CsdlReturnType returnType = new CsdlReturnType();
            if (tree.get("returnType").has("type")) {
                String fullQualifiedName = tree.get("returnType").get("type").asText();
                fullQualifiedName = fullQualifiedName.replace(schema.getAlias(), schema.getNamespace());
                String typeName = "";
                if (fullQualifiedName.contains("Collection")) {
                    returnType.setCollection(true);
                    typeName = fullQualifiedName.substring("Collection(".length(), fullQualifiedName.length() - 2);
                } else {
                    typeName = fullQualifiedName;
                }
                returnType.setType(new FullQualifiedName(typeName));
            }
            if (tree.get("returnType").has("scale")) {
                returnType.setScale(tree.get("returnType").get("scale").asInt());
            }
            if (tree.get("returnType").has("precision")) {
                returnType.setPrecision(tree.get("returnType").get("precision").asInt());
            }
            if (tree.get("returnType").has("nullable")) {
                returnType.setNullable(tree.get("returnType").get("nullable").asBoolean());
            }
            if (tree.get("returnType").has("maxLength")) {
                returnType.setMaxLength(tree.get("returnType").get("maxLength").asInt());
            }
            action.setReturnType(returnType);
        }

        if (tree.has("parameters")) {
            List<CsdlParameter> parameterList = new ArrayList<CsdlParameter>();
            Iterator<Map.Entry<String, JsonNode>> iterator = tree.get("parameters").fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                CsdlParameter parameter = new CsdlParameter();
                parameter.setName(entry.getKey());
                if (entry.getValue().has("type")) {
                    String fullQualifiedName = entry.getValue().get("type").asText();
                    fullQualifiedName = fullQualifiedName.replace(schema.getAlias(), schema.getNamespace());
                    String typeName = "";
                    if (fullQualifiedName.contains("Collection")) {
                        parameter.setCollection(true);
                        typeName = fullQualifiedName.substring("Collection(".length(), fullQualifiedName.length() - 2);
                    } else {
                        typeName = fullQualifiedName;
                    }
                    parameter.setType(new FullQualifiedName(typeName));
                }
                if (entry.getValue().has("scale")) {
                    parameter.setScale(entry.getValue().get("scale").asInt());
                }
                if (entry.getValue().has("precision")) {
                    parameter.setPrecision(entry.getValue().get("precision").asInt());
                }
                if (entry.getValue().has("nullable")) {
                    parameter.setNullable(entry.getValue().get("nullable").asBoolean());
                }
                if (entry.getValue().has("maxLength")) {
                    parameter.setMaxLength(entry.getValue().get("maxLength").asInt());
                }
                parameterList.add(parameter);
            }
            action.setParameters(parameterList);
        }

        return action;
    }
}
