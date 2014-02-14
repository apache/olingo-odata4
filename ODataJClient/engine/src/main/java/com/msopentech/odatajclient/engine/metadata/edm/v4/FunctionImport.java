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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractFunctionImport;

public class FunctionImport extends AbstractFunctionImport implements AnnotatedEdm {

    private static final long serialVersionUID = 3023813358471000019L;

    @JsonProperty(value = "Name", required = true)
    private String name;

    @JsonProperty(value = "Function", required = true)
    private String function;

    @JsonProperty(value = "EntitySet")
    private String entitySet;

    @JsonProperty(value = "IncludeInServiceDocument")
    private boolean includeInServiceDocument = false;

    @JsonProperty(value = "Annotation")
    private Annotation annotation;

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(final String function) {
        this.function = function;
    }

    @Override
    public String getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(final String entitySet) {
        this.entitySet = entitySet;
    }

    public boolean isIncludeInServiceDocument() {
        return includeInServiceDocument;
    }

    public void setIncludeInServiceDocument(final boolean includeInServiceDocument) {
        this.includeInServiceDocument = includeInServiceDocument;
    }

    @Override
    public Annotation getAnnotation() {
        return annotation;
    }

    @Override
    public void setAnnotation(final Annotation annotation) {
        this.annotation = annotation;
    }

}
