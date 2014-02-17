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
package com.msopentech.odatajclient.engine.metadata.edm.v3;

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import java.util.ArrayList;
import java.util.List;

public class EntityContainer extends AbstractEntityContainer<FunctionImport> {

    private static final long serialVersionUID = 8934431875078180370L;

    private final List<EntitySet> entitySets = new ArrayList<EntitySet>();

    private final List<AssociationSet> associationSets = new ArrayList<AssociationSet>();

    private final List<FunctionImport> functionImports = new ArrayList<FunctionImport>();

    @Override
    public List<EntitySet> getEntitySets() {
        return entitySets;
    }

    @Override
    public EntitySet getEntitySet(final String name) {
        EntitySet result = null;
        for (EntitySet entitySet : getEntitySets()) {
            if (name.equals(entitySet.getName())) {
                result = entitySet;
            }
        }
        return result;
    }

    public List<AssociationSet> getAssociationSets() {
        return associationSets;
    }

    @Override
    public List<FunctionImport> getFunctionImports() {
        return functionImports;
    }

}
