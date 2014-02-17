/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
