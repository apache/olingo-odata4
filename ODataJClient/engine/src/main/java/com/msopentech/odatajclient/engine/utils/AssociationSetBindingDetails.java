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
package com.msopentech.odatajclient.engine.utils;

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;
import com.msopentech.odatajclient.engine.metadata.edm.v3.AssociationSet;

/**
 * Association set binding details bean.
 */
public class AssociationSetBindingDetails {

    private final AssociationSet associationSet;

    private final AbstractEntityContainer<?> container;

    private final AbstractSchema<?, ?, ?, ?> schema;

    public AssociationSetBindingDetails(
            final AssociationSet associationSet,
            final AbstractEntityContainer<?> container,
            final AbstractSchema<?, ?, ?, ?> schema) {
        this.associationSet = associationSet;
        this.container = container;
        this.schema = schema;
    }

    public AssociationSet getAssociationSet() {
        return associationSet;
    }

    public AbstractEntityContainer<?> getContainer() {
        return container;
    }

    public AbstractSchema<?, ?, ?, ?> getSchema() {
        return schema;
    }
}
