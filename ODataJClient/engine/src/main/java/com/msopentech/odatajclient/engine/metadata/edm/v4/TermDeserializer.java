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
package com.msopentech.odatajclient.engine.metadata.edm.v4;

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdmDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class TermDeserializer extends AbstractEdmDeserializer<Term> {

    @Override
    protected Term doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final Term term = new Term();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Name".equals(jp.getCurrentName())) {
                    term.setName(jp.nextTextValue());
                } else if ("Type".equals(jp.getCurrentName())) {
                    term.setType(jp.nextTextValue());
                } else if ("BaseTerm".equals(jp.getCurrentName())) {
                    term.setBaseTerm(jp.nextTextValue());
                } else if ("DefaultValue".equals(jp.getCurrentName())) {
                    term.setDefaultValue(jp.nextTextValue());
                } else if ("Nullable".equals(jp.getCurrentName())) {
                    term.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("MaxLength".equals(jp.getCurrentName())) {
                    term.setMaxLength(jp.nextTextValue());
                } else if ("Precision".equals(jp.getCurrentName())) {
                    term.setPrecision(BigInteger.valueOf(jp.nextLongValue(0L)));
                } else if ("Scale".equals(jp.getCurrentName())) {
                    term.setScale(BigInteger.valueOf(jp.nextLongValue(0L)));
                } else if ("SRID".equals(jp.getCurrentName())) {
                    term.setSrid(jp.nextTextValue());
                } else if ("AppliesTo".equals(jp.getCurrentName())) {
                    for (String split : StringUtils.split(jp.nextTextValue())) {
                        term.getAppliesTo().add(CSDLElement.valueOf(split));
                    }
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    term.setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return term;
    }

}
