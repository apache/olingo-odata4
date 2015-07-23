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

package org.apache.olingo.client.core.edm.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.olingo.client.api.edm.json.EdmJsonSchema;
import org.apache.olingo.client.api.edm.xml.Reference;

import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ClientJsonSchemaCsdl.JsonCsdlDeserializer.class)
public class ClientJsonSchemaCsdl implements EdmJsonSchema {

    private final List<Reference> references = new ArrayList<Reference>();

    private final List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();

    @Override
    public List<CsdlSchema> getSchemas() {
        return schemas;
    }

    @Override
    public List<Reference> getReferences() {
        return references;
    }

    static class JsonCsdlDeserializer extends JsonDeserializer<ClientJsonSchemaCsdl> {

        @Override
        public ClientJsonSchemaCsdl deserialize(final JsonParser parser, final DeserializationContext ctxt)
                throws IOException {
            final ClientJsonSchemaCsdl jsonSchemaCsdl = new ClientJsonSchemaCsdl();
            final ObjectNode tree = parser.getCodec().readTree(parser);
            JsonNode definitionsNode=tree.get("definitions");
            //Dependency exists schemas should be de serialized first
            JsonNode schemasNode=tree.get("schemas");
            new ClientCsdlSchemasDeserializer(jsonSchemaCsdl)
                    .deserialize(schemasNode.traverse(parser.getCodec()),ctxt);
            JsonNode referencesNode=tree.get("references");
            new ClientCsdlReferencesDeserializer(jsonSchemaCsdl)
                    .deserialize(referencesNode.traverse(parser.getCodec()), ctxt);
            return new ClientCsdlDefinitionsDeserializer(jsonSchemaCsdl)
                    .deserialize(definitionsNode.traverse(parser.getCodec()), ctxt);
        }
    }
}
