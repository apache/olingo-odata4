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
import com.msopentech.odatajclient.engine.metadata.edm.AbstractNavigationProperty;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = NavigationPropertyDeserializer.class)
public class NavigationProperty extends AbstractNavigationProperty implements AnnotatedEdm {

    private static final long serialVersionUID = -2889417442815563307L;

    private String type;

    private boolean nullable = true;

    private String partner;

    private boolean containsTarget = false;

    private final List<ReferentialConstraint> referentialConstraints = new ArrayList<ReferentialConstraint>();

    private OnDelete onDelete;

    private Annotation annotation;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(final boolean nullable) {
        this.nullable = nullable;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(final String partner) {
        this.partner = partner;
    }

    public boolean isContainsTarget() {
        return containsTarget;
    }

    public void setContainsTarget(final boolean containsTarget) {
        this.containsTarget = containsTarget;
    }

    public List<ReferentialConstraint> getReferentialConstraints() {
        return referentialConstraints;
    }

    public OnDelete getOnDelete() {
        return onDelete;
    }

    public void setOnDelete(final OnDelete onDelete) {
        this.onDelete = onDelete;
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
