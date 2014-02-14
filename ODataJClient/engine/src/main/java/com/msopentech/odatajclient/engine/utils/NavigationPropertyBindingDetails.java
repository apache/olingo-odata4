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
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractNavigationProperty;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;

/**
 * Navigation property binding details bean.
 */
public class NavigationPropertyBindingDetails {

    private final AbstractNavigationProperty navigationProperty;

    private final AbstractEntitySet entitySet;

    private final AbstractSchema<?, ?, ?, ?> schema;

    private final AbstractEntityContainer<?> container;

    public NavigationPropertyBindingDetails(
            final AbstractNavigationProperty navigationProperty,
            final AbstractEntitySet entitySet,
            final AbstractEntityContainer<?> container,
            final AbstractSchema<?, ?, ?, ?> schema) {
        this.navigationProperty = navigationProperty;
        this.schema = schema;
        this.entitySet = entitySet;
        this.container = container;
    }

    public AbstractNavigationProperty getNavigationProperty() {
        return navigationProperty;
    }

    public AbstractSchema<?, ?, ?, ?> getSchema() {
        return schema;
    }

    public AbstractEntitySet getEntitySet() {
        return entitySet;
    }

    public AbstractEntityContainer<?> getContainer() {
        return container;
    }
}
