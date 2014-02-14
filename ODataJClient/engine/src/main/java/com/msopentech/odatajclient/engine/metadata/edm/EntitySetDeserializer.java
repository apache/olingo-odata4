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
import com.msopentech.odatajclient.engine.metadata.edm.v4.NavigationPropertyBinding;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.IOException;
import org.apache.commons.lang3.BooleanUtils;

public class EntitySetDeserializer extends AbstractEdmDeserializer<AbstractEntitySet> {

    @Override
    protected AbstractEntitySet doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final AbstractEntitySet entitySet = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.metadata.edm.v3.EntitySet()
                : new com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Name".equals(jp.getCurrentName())) {
                    entitySet.setName(jp.nextTextValue());
                } else if ("EntityType".equals(jp.getCurrentName())) {
                    entitySet.setEntityType(jp.nextTextValue());
                } else if ("IncludeInServiceDocument".equals(jp.getCurrentName())) {
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet) entitySet).
                            setIncludeInServiceDocument(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("NavigationPropertyBinding".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet) entitySet).
                            getNavigationPropertyBindings().add(
                                    jp.getCodec().readValue(jp, NavigationPropertyBinding.class));
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet) entitySet).
                            setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return entitySet;
    }

}
