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

import com.msopentech.odatajclient.engine.metadata.edm.v3.AssociationSet;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.msopentech.odatajclient.engine.metadata.edm.v4.ActionImport;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Annotation;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Singleton;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.io.IOException;
import org.apache.commons.lang3.BooleanUtils;

@SuppressWarnings("rawtypes")
public class EntityContainerDeserializer extends AbstractEdmDeserializer<AbstractEntityContainer> {

    @Override
    protected AbstractEntityContainer doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final AbstractEntityContainer entityContainer = ODataVersion.V3 == client.getWorkingVersion()
                ? new com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer()
                : new com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer();

        for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
            final JsonToken token = jp.getCurrentToken();
            if (token == JsonToken.FIELD_NAME) {
                if ("Name".equals(jp.getCurrentName())) {
                    entityContainer.setName(jp.nextTextValue());
                } else if ("Extends".equals(jp.getCurrentName())) {
                    entityContainer.setExtends(jp.nextTextValue());
                } else if ("LazyLoadingEnabled".equals(jp.getCurrentName())) {
                    entityContainer.setLazyLoadingEnabled(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("IsDefaultEntityContainer".equals(jp.getCurrentName())) {
                    entityContainer.setDefaultEntityContainer(BooleanUtils.toBoolean(jp.nextTextValue()));
                } else if ("EntitySet".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (entityContainer instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer) entityContainer).
                                getEntitySets().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.EntitySet.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer) entityContainer).
                                getEntitySets().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet.class));
                    }
                } else if ("AssociationSet".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer) entityContainer).
                            getAssociationSets().add(jp.getCodec().readValue(jp, AssociationSet.class));
                } else if ("Singleton".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer) entityContainer).
                            getSingletons().add(jp.getCodec().readValue(jp, Singleton.class));
                } else if ("ActionImport".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer) entityContainer).
                            getActionImports().add(jp.getCodec().readValue(jp, ActionImport.class));
                } else if ("FunctionImport".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    if (entityContainer instanceof com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer) {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer) entityContainer).
                                getFunctionImports().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport.class));
                    } else {
                        ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer) entityContainer).
                                getFunctionImports().add(jp.getCodec().readValue(jp,
                                                com.msopentech.odatajclient.engine.metadata.edm.v4.FunctionImport.class));
                    }
                } else if ("Annotation".equals(jp.getCurrentName())) {
                    jp.nextToken();
                    ((com.msopentech.odatajclient.engine.metadata.edm.v4.EntityContainer) entityContainer).
                            setAnnotation(jp.getCodec().readValue(jp, Annotation.class));
                }
            }
        }

        return entityContainer;
    }
}
