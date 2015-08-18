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
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlReferentialConstraint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ClientCsdlNavigationPropertyDeserializer extends JsonDeserializer<CsdlNavigationProperty> {

    private static final String DEFAULT_SCHEMA = "http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#";
    private static final String CONSTANT_DEFINITION_REFERENCE = DEFAULT_SCHEMA + "/definitions/";

    private String propertyName;


    public ClientCsdlNavigationPropertyDeserializer(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public CsdlNavigationProperty deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        CsdlNavigationProperty property = new CsdlNavigationProperty();
        property.setName(propertyName);

        if (tree.has("$ref")) {
            String fqnAsString = tree.get("$ref").asText().replace(CONSTANT_DEFINITION_REFERENCE, "");
            fqnAsString = fqnAsString.trim();
            property.setType(new FullQualifiedName(fqnAsString));
            property.setNullable(false);
            property.setCollection(false);
        }

        if (tree.has("anyOf")) {
            Iterator<JsonNode> iterator = tree.get("anyOf").elements();
            while (iterator.hasNext()) {
                JsonNode node = iterator.next();
                if (node.has("$ref")) {
                    String fqnAsString = node.get("$ref").asText().replace(CONSTANT_DEFINITION_REFERENCE, "");
                    fqnAsString = fqnAsString.trim();
                    property.setType(new FullQualifiedName(fqnAsString));
                    property.setCollection(false);
                } else if (node.has("type")) {
                    if (node.get("type").asText().equals(null)) {
                        property.setNullable(true);
                    }
                }
            }
        }


        if (tree.has("type")) {
            if (tree.get("type").asText().equals("array")) {
                if (tree.has("items")) {
                    if (tree.get("items").has("$ref")) {
                        String fqnAsString = tree.get("items").get("$ref").asText()
                                .replace(CONSTANT_DEFINITION_REFERENCE, "");
                        fqnAsString = fqnAsString.trim();
                        property.setType(new FullQualifiedName(fqnAsString));
                        property.setNullable(false);
                        property.setCollection(true);
                    } else if (tree.get("items").has("anyOf")) {
                        Iterator<JsonNode> iterator = tree.get("items").get("anyOf").elements();
                        while (iterator.hasNext()) {
                            JsonNode node = iterator.next();
                            if (node.has("$ref")) {
                                String fqnAsString = node.get("$ref").asText()
                                        .replace(CONSTANT_DEFINITION_REFERENCE, "");
                                fqnAsString = fqnAsString.trim();
                                property.setType(new FullQualifiedName(fqnAsString));
                                property.setCollection(true);
                            } else if (node.has("type")) {
                                if (node.get("type").asText().equals(null)) {
                                    property.setNullable(true);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (tree.has("relationship")) {
            JsonNode relationship = tree.get("relationship");
            if (relationship.has("partner")) {
                property.setPartner(relationship.get("partner").asText());
            }
            if (relationship.has("containsTarget")) {
                property.setContainsTarget(relationship.get("containsTarget").asBoolean());
            }
            if (relationship.has("referentialConstraints")) {
                Iterator<Map.Entry<String, JsonNode>> iterator = relationship.get("referentialConstraints").fields();
                ArrayList<CsdlReferentialConstraint> constraintsList = new ArrayList<CsdlReferentialConstraint>();

                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> entry = iterator.next();
                    CsdlReferentialConstraint constraint = new CsdlReferentialConstraint();
                    constraint.setProperty(entry.getKey());
                    if (entry.getValue().has("referencedProperty")) {
                        constraint.setReferencedProperty(entry.getValue().get("referencedProperty").asText());
                    }
                    constraintsList.add(constraint);
                }
                property.setReferentialConstraints(constraintsList);
            }
        }
        return property;
    }
}