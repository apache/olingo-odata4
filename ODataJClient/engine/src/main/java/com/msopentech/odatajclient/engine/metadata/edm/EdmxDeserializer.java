/**
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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Reference;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.IOException;

@SuppressWarnings("rawtypes")
public class EdmxDeserializer extends AbstractEdmDeserializer<AbstractEdmx> {

    @Override
    protected AbstractEdmx doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final AbstractEdmx edmx = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.metadata.edm.v3.Edmx()
                : new com.msopentech.odatajclient.engine.metadata.edm.v4.Edmx();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Version".equals(jp.getCurrentName())) {
                    edmx.setVersion(jp.nextTextValue());
                } else if ("DataServices".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (edmx instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.Edmx) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.Edmx) edmx).
                                setDataServices(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.DataServices.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.Edmx) edmx).
                                setDataServices(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.DataServices.class));
                    }
                } else if ("Reference".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.Edmx) edmx).getReferences().
                            add(jp.getCodec().readValue(jp, Reference.class));
                }
            }
        }

        return edmx;
    }
}
