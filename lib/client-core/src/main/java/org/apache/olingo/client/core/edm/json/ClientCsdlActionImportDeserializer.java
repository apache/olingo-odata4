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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;

import java.io.IOException;

public class ClientCsdlActionImportDeserializer extends JsonDeserializer<CsdlActionImport> {

    private String name;

    public ClientCsdlActionImportDeserializer(String name) {
        this.name = name;
    }

    @Override
    public CsdlActionImport deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        CsdlActionImport actionImport = new CsdlActionImport();
        actionImport.setName(name);
        if (tree.has("action")) {
            actionImport.setAction(tree.get("action").asText());
        }
        return actionImport;
    }
}