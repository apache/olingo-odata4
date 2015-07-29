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
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;

import java.io.IOException;
import java.util.*;

public class ClientCsdlDefinitionsDeserializer extends JsonDeserializer<ClientJsonSchemaCsdl> {

   private ClientJsonSchemaCsdl jsonCsdl;

   public ClientCsdlDefinitionsDeserializer(ClientJsonSchemaCsdl jsonCsdl){
        this.jsonCsdl=jsonCsdl;
    }

    @Override
    public ClientJsonSchemaCsdl deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        Iterator<Map.Entry<String, JsonNode>> iterator = tree.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> typeObject;
            typeObject = iterator.next();
            String combinedNamespaceType = typeObject.getKey();
            FullQualifiedName fullQualifiedTypeName = new FullQualifiedName(combinedNamespaceType);
            String typeName = fullQualifiedTypeName.getName();
            String nameSpace = fullQualifiedTypeName.getNamespace();
            if(typeObject.getValue().has("enum")){
                final CsdlEnumType enumType = new ClientCsdlEnumTypeDeserializer(nameSpace,typeName).deserialize(
                        tree.get(typeObject.getKey()).traverse(parser.getCodec()), ctxt);
                if (getSchemaByNsOrAlias().get(nameSpace)!=null){
                    getSchemaByNsOrAlias().get(nameSpace).getEnumTypes().add(enumType);
                }
            }else if(typeObject.getValue().has("type")&&"object".equals(typeObject.getValue().get("type").asText())){
                for(CsdlEntitySet entitySet : getSchemaByNsOrAlias().get(nameSpace)
                        .getEntityContainer().getEntitySets()){
                    if(entitySet.getType().equals(combinedNamespaceType)){
                        final CsdlEntityType type = new ClientCsdlEntityTypeDeserializer(nameSpace,typeName)
                                .deserialize(tree.get(typeObject.getKey()).traverse(parser.getCodec()), ctxt);
                        getSchemaByNsOrAlias().get(nameSpace).getEntityTypes().add(type);
                    }
                }
                //toDo Complex Type
            } else if (typeObject.getValue().has("type") &&
                    !("object".equals(typeObject.getValue().get("type").asText()))) {
                final CsdlTypeDefinition typeDefinition = new ClientCsdlTypeDefinitionDeserializer(nameSpace, typeName)
                        .deserialize(tree.get(typeObject.getKey()).traverse(parser.getCodec()), ctxt);
                if (getSchemaByNsOrAlias().get(nameSpace)!=null){
                    getSchemaByNsOrAlias().get(nameSpace).getTypeDefinitions().add(typeDefinition);
                }
            }
        }
        return jsonCsdl;
    }

    public Map<String, CsdlSchema> getSchemaByNsOrAlias() {
        final Map<String, CsdlSchema> schemaByNsOrAlias = new HashMap<String, CsdlSchema>();
        for (CsdlSchema schema : jsonCsdl.getSchemas()) {
            schemaByNsOrAlias.put(schema.getNamespace(), schema);
            if (StringUtils.isNotBlank(schema.getAlias())) {
                schemaByNsOrAlias.put(schema.getAlias(), schema);
            }
        }
        return schemaByNsOrAlias;
    }
}
