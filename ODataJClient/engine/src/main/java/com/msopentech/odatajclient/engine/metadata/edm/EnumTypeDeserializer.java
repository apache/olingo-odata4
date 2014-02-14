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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Annotation;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.IOException;
import org.apache.commons.lang3.BooleanUtils;

public class EnumTypeDeserializer extends AbstractEdmDeserializer<AbstractEnumType> {

    @Override
    protected AbstractEnumType doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final AbstractEnumType enumType = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.metadata.edm.v3.EnumType()
                : new com.msopentech.odatajclient.engine.metadata.edm.v4.EnumType();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Name".equals(jp.getCurrentName())) {
                    enumType.setName(jp.nextTextValue());
                } else if ("UnderlyingType".equals(jp.getCurrentName())) {
                    enumType.setUnderlyingType(jp.nextTextValue());
                } else if ("IsFlags".equals(jp.getCurrentName())) {
                    enumType.setFlags(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("Member".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (enumType instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.EnumType) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.EnumType) enumType).
                                getMembers().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.Member.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.EnumType) enumType).
                                getMembers().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.Member.class));
                    }
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EnumType) enumType).
                            setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return enumType;
    }
}
