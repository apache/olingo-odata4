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

public class ComplexTypeDeserializer extends AbstractEdmDeserializer<AbstractComplexType> {

    @Override
    protected AbstractComplexType doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final AbstractComplexType complexType = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.metadata.edm.v3.ComplexType()
                : new com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Name".equals(jp.getCurrentName())) {
                    complexType.setName(jp.nextTextValue());
                } else if ("Abstract".equals(jp.getCurrentName())) {
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType) complexType).
                            setAbstractEntityType(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("BaseType".equals(jp.getCurrentName())) {
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType) complexType).
                            setBaseType(jp.nextTextValue());
                } else if ("OpenType".equals(jp.getCurrentName())) {
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType) complexType).
                            setOpenType(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("Property".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (complexType instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.ComplexType) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.ComplexType) complexType).
                                getProperties().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.Property.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType) complexType).
                                getProperties().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.Property.class));
                    }
                } else if ("NavigationProperty".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType) complexType).
                            getNavigationProperties().add(jp.getCodec().readValue(jp,
                                            com.msopentech.odatajclient.engine.metadata.edm.v4.NavigationProperty.class));
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.ComplexType) complexType).
                            setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return complexType;
    }
}
