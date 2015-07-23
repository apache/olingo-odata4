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

import java.io.IOException;
import java.net.URI;
import java.util.*;

public class ClientCsdlReferencesDeserializer extends JsonDeserializer<ClientJsonSchemaCsdl> {

    private ClientJsonSchemaCsdl jsonCsdl;

    public ClientCsdlReferencesDeserializer(ClientJsonSchemaCsdl jsonCsdl){
        this.jsonCsdl=jsonCsdl;
    }

    @Override
    public ClientJsonSchemaCsdl deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        Iterator<Map.Entry<String,JsonNode>> iterator = tree.fields();
        while(iterator.hasNext()){
            Map.Entry<String,JsonNode> referenceEntry = iterator.next();
            ClientJsonCsdlReference reference = new ClientJsonCsdlReference();
            jsonCsdl.getReferences().add(reference);
            reference.setUri(URI.create(referenceEntry.getKey()));

            if(referenceEntry.getValue().has("includes")){
                JsonNode includes = referenceEntry.getValue().get("includes");
                Iterator<Map.Entry<String,JsonNode>> includeElements = includes.fields();
                while(includeElements.hasNext()){
                    Map.Entry<String,JsonNode> includeNode = includeElements.next();
                    ClientJsonCsdlInclude csdlInclude = new ClientJsonCsdlInclude();
                    csdlInclude.setNamespace(includeNode.getKey());
                    if(includeNode.getValue().has("alias")){
                        csdlInclude.setAlias(includeNode.getValue().get("alias").asText());
                    }
                    reference.getIncludes().add(csdlInclude);
                }
            }

            if(referenceEntry.getValue().has("includeAnnotations")){
                JsonNode includesAnnotations = referenceEntry.getValue().get("includeAnnotations");
                Iterator<JsonNode> annotationEntry = includesAnnotations.elements();
                while(annotationEntry.hasNext()){
                    JsonNode annotationNode = annotationEntry.next();
                    ClientJsonCsdlIncludeAnnotation csdlIncludeAnnotation = new ClientJsonCsdlIncludeAnnotation();
                    if(annotationNode.has("targetNamespace")){
                        csdlIncludeAnnotation.setTargetNamespace(annotationNode.get("targetNamespace").asText());
                    }
                    if(annotationNode.has("termNamespace")){
                        csdlIncludeAnnotation.setTermNamespace(annotationNode.get("termNamespace").asText());
                    }
                    if(annotationNode.has("qualifier")){
                        csdlIncludeAnnotation.setQualifier(annotationNode.get("qualifier").asText());
                    }
                    reference.getIncludeAnnotations().add(csdlIncludeAnnotation);
                }
            }
        }
        return jsonCsdl;
    }
}
