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
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import java.io.IOException;

public class ClientCsdlEntitySetDeserializer extends JsonDeserializer<CsdlEntitySet> {

    private String name;
    private CsdlSchema schema;

    public ClientCsdlEntitySetDeserializer(CsdlSchema schema,String name){
        this.schema =schema;
        this.name = name;
    }


    @Override
    public CsdlEntitySet deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        CsdlEntitySet entitySet = new CsdlEntitySet();
        entitySet.setName(name);
        String entityTypeName=tree.get("entityType").asText();
        String aliasReplaced = entityTypeName.replace(schema.getAlias(), schema.getNamespace());
        FullQualifiedName typeName = new FullQualifiedName(aliasReplaced);
        entitySet.setType(typeName);
        //toDo navigational property bindings
        return entitySet;
    }
}
