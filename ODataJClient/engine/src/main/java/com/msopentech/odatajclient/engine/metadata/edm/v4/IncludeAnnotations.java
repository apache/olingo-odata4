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
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;

public class IncludeAnnotations extends AbstractEdm {

    private static final long serialVersionUID = -5600031479702563436L;

    @JsonProperty(value = "TermNamespace", required = true)
    private String termNamespace;

    @JsonProperty(value = "Qualifier")
    private String qualifier;

    @JsonProperty(value = "TargetNamespace")
    private String targeyNamespace;

    public String getTermNamespace() {
        return termNamespace;
    }

    public void setTermNamespace(final String termNamespace) {
        this.termNamespace = termNamespace;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public String getTargeyNamespace() {
        return targeyNamespace;
    }

    public void setTargeyNamespace(final String targeyNamespace) {
        this.targeyNamespace = targeyNamespace;
    }

}
