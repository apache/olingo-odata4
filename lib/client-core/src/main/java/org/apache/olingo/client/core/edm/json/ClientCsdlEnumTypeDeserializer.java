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
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;

import java.io.IOException;
import java.util.Iterator;

public class ClientCsdlEnumTypeDeserializer extends JsonDeserializer<CsdlEnumType> {

    private String typeName;
    private String nameSpace;

    public ClientCsdlEnumTypeDeserializer(String nameSpace,String typeName){
        this.nameSpace = nameSpace;
        this.typeName = typeName;
    }

    @Override
    public CsdlEnumType deserialize(final JsonParser parser, final DeserializationContext ctxt)
            throws IOException {
        final ObjectNode tree = parser.getCodec().readTree(parser);
        final CsdlEnumType enumType = new CsdlEnumType();
        enumType.setName(typeName);
        Iterator<JsonNode> itr=tree.get("enum").elements();
        while(itr.hasNext()){
            JsonNode node = itr.next();
            CsdlEnumMember member=new CsdlEnumMember();
            member.setName(node.asText());
            member.setValue(tree.get(node.asText() + "@odata.value").asText());
            enumType.getMembers().add(member);
        }
        return enumType;

    }
}
