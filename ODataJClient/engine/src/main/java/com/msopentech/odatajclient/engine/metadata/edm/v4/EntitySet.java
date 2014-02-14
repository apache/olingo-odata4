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

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntitySet;
import java.util.ArrayList;
import java.util.List;

public class EntitySet extends AbstractEntitySet implements AnnotatedEdm {

    private static final long serialVersionUID = 5570833733884884012L;

    private boolean includeInServiceDocument = true;

    private Annotation annotation;

    private final List<NavigationPropertyBinding> navigationPropertyBindings =
            new ArrayList<NavigationPropertyBinding>();

    public boolean isIncludeInServiceDocument() {
        return includeInServiceDocument;
    }

    public void setIncludeInServiceDocument(final boolean includeInServiceDocument) {
        this.includeInServiceDocument = includeInServiceDocument;
    }

    public List<NavigationPropertyBinding> getNavigationPropertyBindings() {
        return navigationPropertyBindings;
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
