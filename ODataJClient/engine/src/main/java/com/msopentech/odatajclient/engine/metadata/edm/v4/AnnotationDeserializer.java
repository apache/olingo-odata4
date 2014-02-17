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
package com.msopentech.odatajclient.engine.metadata.edm.v4;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdmDeserializer;
import com.msopentech.odatajclient.engine.metadata.edm.v4.annotation.DynExprConstruct;
import java.io.IOException;

public class AnnotationDeserializer extends AbstractEdmDeserializer<Annotation> {

    @Override
    protected Annotation doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final Annotation annotation = new Annotation();

        for (; jp.getCurrentToken() != null && jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Term".equals(jp.getCurrentName())) {
                    annotation.setTerm(jp.nextTextValue());
                } else if ("Qualifier".equals(jp.getCurrentName())) {
                    annotation.setQualifier(jp.nextTextValue());
                } // Constant Expressions
                else if (isAnnotationConstExprConstruct(jp)) {
                    annotation.setConstExpr(parseAnnotationConstExprConstruct(jp));
                } // Dynamic Expressions
                else {
                    annotation.setDynExpr(jp.getCodec().readValue(jp, DynExprConstruct.class));
                }
            }
        }

        return annotation;
    }

}
