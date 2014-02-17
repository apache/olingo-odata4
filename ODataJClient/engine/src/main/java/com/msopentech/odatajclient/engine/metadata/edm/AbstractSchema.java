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
import java.util.List;

@JsonDeserialize(using = SchemaDeserializer.class)
public abstract class AbstractSchema<EC extends AbstractEntityContainer<
        FI>, E extends AbstractEntityType, C extends AbstractComplexType, FI extends AbstractFunctionImport>
        extends AbstractEdm {

    private static final long serialVersionUID = -1356392748971378455L;

    private String namespace;

    private String alias;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public abstract List<E> getEntityTypes();

    public abstract List<? extends AbstractEnumType> getEnumTypes();

    public abstract AbstractEnumType getEnumType(String name);

    public abstract List<? extends AbstractAnnotations> getAnnotationsList();

    public abstract AbstractAnnotations getAnnotationsList(String target);

    public abstract List<C> getComplexTypes();

    public abstract List<EC> getEntityContainers();

    /**
     * Gets default entity container.
     *
     * @return default entity container.
     */
    public abstract EC getDefaultEntityContainer();

    /**
     * Gets entity container with the given name.
     *
     * @param name name.
     * @return entity container.
     */
    public abstract EC getEntityContainer(String name);

    /**
     * Gets entity type with the given name.
     *
     * @param name name.
     * @return entity type.
     */
    public E getEntityType(final String name) {
        E result = null;
        for (E type : getEntityTypes()) {
            if (name.equals(type.getName())) {
                result = type;
            }
        }
        return result;
    }

    /**
     * Gets complex type with the given name.
     *
     * @param name name.
     * @return complex type.
     */
    public C getComplexType(final String name) {
        C result = null;
        for (C type : getComplexTypes()) {
            if (name.equals(type.getName())) {
                result = type;
            }
        }
        return result;
    }
}
