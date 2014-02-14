/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.metadata.edm.v4.annotation;

import com.msopentech.odatajclient.engine.metadata.edm.v4.Annotation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdmDeserializer;
import java.io.IOException;
import java.math.BigInteger;

public class IsOfDeserializer extends AbstractEdmDeserializer<IsOf> {

    @Override
    protected IsOf doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final IsOf isof = new IsOf();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Type".equals(jp.getCurrentName())) {
                    isof.setType(jp.nextTextValue());
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    isof.setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                } else if ("MaxLength".equals(jp.getCurrentName())) {
                    isof.setMaxLength(jp.nextTextValue());
                } else if ("Precision".equals(jp.getCurrentName())) {
                    isof.setPrecision(BigInteger.valueOf(jp.nextLongValue(0L)));
                } else if ("Scale".equals(jp.getCurrentName())) {
                    isof.setScale(BigInteger.valueOf(jp.nextLongValue(0L)));
                } else if ("SRID".equals(jp.getCurrentName())) {
                    isof.setSrid(jp.nextTextValue());
                } else {
                    isof.setValue(jp.getCodec().readValue(jp, DynExprConstruct.class));
                }
            }
        }

        return isof;
    }

}
