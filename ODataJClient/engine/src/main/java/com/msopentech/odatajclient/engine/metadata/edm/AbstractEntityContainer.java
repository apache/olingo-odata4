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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = EntityContainerDeserializer.class)
public abstract class AbstractEntityContainer<FI extends AbstractFunctionImport> extends AbstractEdm {

    private static final long serialVersionUID = 4121974387552855032L;

    private String name;

    private String _extends;

    private boolean lazyLoadingEnabled;

    private boolean defaultEntityContainer;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getExtends() {
        return _extends;
    }

    public void setExtends(final String _extends) {
        this._extends = _extends;
    }

    public boolean isLazyLoadingEnabled() {
        return lazyLoadingEnabled;
    }

    public void setLazyLoadingEnabled(final boolean lazyLoadingEnabled) {
        this.lazyLoadingEnabled = lazyLoadingEnabled;
    }

    public boolean isDefaultEntityContainer() {
        return defaultEntityContainer;
    }

    public void setDefaultEntityContainer(final boolean defaultEntityContainer) {
        this.defaultEntityContainer = defaultEntityContainer;
    }

    public abstract List<? extends AbstractEntitySet> getEntitySets();

    public abstract AbstractEntitySet getEntitySet(String name);

    /**
     * Gets the first function import with given name.
     *
     * @param name name.
     * @return function import.
     */
    public FI getFunctionImport(final String name) {
        final List<FI> funcImps = getFunctionImports(name);
        return funcImps.isEmpty()
                ? null
                : funcImps.get(0);
    }

    /**
     * Gets all function imports with given name.
     *
     * @param name name.
     * @return function imports.
     */
    public List<FI> getFunctionImports(final String name) {
        final List<FI> result = new ArrayList<FI>();
        for (FI functionImport : getFunctionImports()) {
            if (name.equals(functionImport.getName())) {
                result.add(functionImport);
            }
        }
        return result;
    }

    public abstract List<FI> getFunctionImports();
}
