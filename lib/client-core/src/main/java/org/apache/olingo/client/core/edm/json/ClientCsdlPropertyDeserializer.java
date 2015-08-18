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
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

import java.io.IOException;
import java.util.Iterator;

public class ClientCsdlPropertyDeserializer extends JsonDeserializer<CsdlProperty> {

    private static final String DEFAULT_SCHEMA = "http://docs.oasis-open.org/odata/odata-json-csdl/v4.0/edm.json#";
    private static final String CONSTANT_DEFINITION_REFERENCE = DEFAULT_SCHEMA + "/definitions/";

    private String propertyName;


    public ClientCsdlPropertyDeserializer(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public CsdlProperty deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        CsdlProperty property = new CsdlProperty();
        property.setName(propertyName);

        if (tree.has("type") && !tree.get("type").asText().equals("array")) {
            property.setCollection(false);
            if (!tree.get("type").isArray()) {
                if (tree.get("type").asText().equals("string")) {
                    property.setType(EdmPrimitiveTypeFactory
                            .getInstance(EdmPrimitiveTypeKind.String).getFullQualifiedName());

                } else if (tree.get("type").asText().equals("number")) {
                    property.setType(EdmPrimitiveTypeFactory
                            .getInstance(EdmPrimitiveTypeKind.Decimal).getFullQualifiedName());

                } else if (tree.get("type").asText().equals("boolean")) {
                    property.setType(EdmPrimitiveTypeFactory
                            .getInstance(EdmPrimitiveTypeKind.Boolean).getFullQualifiedName());
                }
            } else {
                Iterator<JsonNode> iterator = tree.get("type").elements();
                while (iterator.hasNext()) {
                    JsonNode booleanOrString = iterator.next();
                    if (booleanOrString.asText().equals("string")) {
                        property.setType(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.String).getFullQualifiedName());

                    } else if (booleanOrString.asText().equals("boolean")) {
                        property.setType(EdmPrimitiveTypeFactory
                                .getInstance(EdmPrimitiveTypeKind.Boolean).getFullQualifiedName());
                    } else if (booleanOrString.asText().equals(null)) {
                        property.setNullable(true);
                    }
                }
            }
        }

        if (tree.has("$ref") && tree.has("type") && !tree.get("type").asText().equals("array")) {
            String fqnAsString = tree.get("$ref").asText().replace(CONSTANT_DEFINITION_REFERENCE, "");
            fqnAsString = fqnAsString.trim();
            property.setType(new FullQualifiedName(fqnAsString));
            property.setCollection(false);
        }

        this.deserializePropertyFacets(tree, property);

        if (tree.has("items") && tree.has("type") && tree.get("type").asText().equals("array")) {
            property.setCollection(true);
            JsonNode collectionProperty = tree.get("items");
            if (collectionProperty.has("type")) {
                if (collectionProperty.get("type").asText().equals("string")) {
                    property.setType(EdmPrimitiveTypeFactory
                            .getInstance(EdmPrimitiveTypeKind.String).getFullQualifiedName());

                } else if (collectionProperty.get("type").asText().equals("number")) {
                    property.setType(EdmPrimitiveTypeFactory
                            .getInstance(EdmPrimitiveTypeKind.Decimal).getFullQualifiedName());

                } else if (collectionProperty.get("type").asText().equals("boolean")) {
                    property.setType(EdmPrimitiveTypeFactory
                            .getInstance(EdmPrimitiveTypeKind.Boolean).getFullQualifiedName());
                }
                this.deserializePropertyFacets(collectionProperty, property);
            }
        }

        return property;
    }

    private void deserializePropertyFacets(JsonNode tree, CsdlProperty property) {

        if (tree.has("unicode")) {
            property.setUnicode(tree.get("unicode").asBoolean());
        }

        if (tree.has("defaultValue")) {
            property.setDefaultValue(tree.get("defaultValue").asText());
        }

        if (tree.has("maxLength")) {
            property.setMaxLength(tree.get("maxLength").asInt());
        }

        if (tree.has("multipleOf")) {
            int unstructured = tree.get("maxLength").asInt();
            String temp = String.valueOf(unstructured);
            if (temp.equals("1")) {
                property.setScale(0);
            } else {
                temp = temp.replace("1e-", "");
                property.setScale(Integer.valueOf(temp));
            }
        }

        if (tree.has("minimum") && tree.has("maximum")) {
            int maximum = tree.get("maximum").asInt();
            int scale;
            if (tree.has("multipleOf")) {
                int unstructured = tree.get("maxLength").asInt();
                String temp = String.valueOf(unstructured);
                if (temp.equals("1")) {
                    scale = 0;
                } else {
                    temp = temp.replace("1e-", "");
                    scale = Integer.valueOf(temp);
                }
            } else {
                scale = 0;
            }

            String strMax = String.valueOf(maximum);
            String[] arrayStr = strMax.split("\\.");

            char[] charArray = arrayStr[0].toCharArray();
            int precision;
            precision = scale + charArray.length;
            property.setPrecision(precision);
        }
    }
}