/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.olingo.client.core.edm.json;

import org.apache.olingo.client.api.edm.xml.Include;
import org.apache.olingo.client.api.edm.xml.IncludeAnnotations;
import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ClientJsonCsdlReference implements Reference {

    private URI uri;
    private final List<Include> includes = new ArrayList<Include>();
    private final List<IncludeAnnotations> includeAnnotations = new ArrayList<IncludeAnnotations>();
    private final List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

    @Override
    public List<CsdlAnnotation> getAnnotations() {
        return annotations;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public void setUri(final URI uri) {
        this.uri = uri;
    }

    @Override
    public List<Include> getIncludes() {
        return includes;
    }

    @Override
    public List<IncludeAnnotations> getIncludeAnnotations() {
        return includeAnnotations;
    }

}
