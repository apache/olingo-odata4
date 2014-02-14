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
package com.msopentech.odatajclient.engine.metadata.edm.v3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEdm;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = AssociationDeserializer.class)
public class Association extends AbstractEdm {

    private static final long serialVersionUID = 73763231919532482L;

    private String name;

    private ReferentialConstraint referentialConstraint;

    private List<AssociationEnd> ends = new ArrayList<AssociationEnd>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ReferentialConstraint getReferentialConstraint() {
        return referentialConstraint;
    }

    public void setReferentialConstraint(final ReferentialConstraint referentialConstraint) {
        this.referentialConstraint = referentialConstraint;
    }

    public List<AssociationEnd> getEnds() {
        return ends;
    }

    public void setEnds(final List<AssociationEnd> ends) {
        this.ends = ends;
    }
}
