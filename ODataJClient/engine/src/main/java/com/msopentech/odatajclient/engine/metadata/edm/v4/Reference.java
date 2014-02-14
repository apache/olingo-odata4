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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = ReferenceDeserializer.class)
public class Reference extends AbstractEdm {

    private static final long serialVersionUID = -5600031479702563436L;

    private URI uri;

    private final List<Include> includes = new ArrayList<Include>();

    private final List<IncludeAnnotations> includeAnnotations = new ArrayList<IncludeAnnotations>();

    private final List<Annotation> annotations = new ArrayList<Annotation>();

    public URI getUri() {
        return uri;
    }

    public void setUri(final URI uri) {
        this.uri = uri;
    }

    public List<Include> getIncludes() {
        return includes;
    }

    public List<IncludeAnnotations> getIncludeAnnotations() {
        return includeAnnotations;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

}
