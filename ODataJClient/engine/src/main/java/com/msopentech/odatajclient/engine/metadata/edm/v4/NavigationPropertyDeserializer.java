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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdmDeserializer;
import java.io.IOException;
import org.apache.commons.lang3.BooleanUtils;

public class NavigationPropertyDeserializer extends AbstractEdmDeserializer<NavigationProperty> {

    @Override
    protected NavigationProperty doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final NavigationProperty property = new NavigationProperty();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Name".equals(jp.getCurrentName())) {
                    property.setName(jp.nextTextValue());
                } else if ("Type".equals(jp.getCurrentName())) {
                    property.setType(jp.nextTextValue());
                } else if ("Nullable".equals(jp.getCurrentName())) {
                    property.setNullable(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("Partner".equals(jp.getCurrentName())) {
                    property.setPartner(jp.nextTextValue());
                } else if ("ContainsTarget".equals(jp.getCurrentName())) {
                    property.setContainsTarget(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("ReferentialConstraint".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    property.getReferentialConstraints().add(jp.getCodec().readValue(jp, ReferentialConstraint.class));
                } else if ("OnDelete".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    property.setOnDelete(jp.getCodec().readValue(jp, OnDelete.class));
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    property.setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return property;
    }

}
