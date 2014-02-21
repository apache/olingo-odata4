/*
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
package org.apache.olingo.odata4.client.core.edm.v4;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.v4.Annotation;
import org.apache.olingo.odata4.client.core.edm.AbstractEntityContainer;

public class EntityContainerImpl extends AbstractEntityContainer<FunctionImportImpl> implements AnnotatedEdmItem {

    private static final long serialVersionUID = 2526002525927260320L;

    private final List<EntitySetImpl> entitySets = new ArrayList<EntitySetImpl>();

    private final List<SingletonImpl> singletons = new ArrayList<SingletonImpl>();

    private final List<ActionImportImpl> actionImports = new ArrayList<ActionImportImpl>();

    private final List<FunctionImportImpl> functionImports = new ArrayList<FunctionImportImpl>();

    private Annotation annotation;

    @Override
    public void setDefaultEntityContainer(final boolean defaultEntityContainer) {
        // no action: a single entity container MUST be available as per OData 4.0
    }

    @Override
    public boolean isDefaultEntityContainer() {
        return true;
    }

    @Override
    public List<EntitySetImpl> getEntitySets() {
        return entitySets;
    }

    @Override
    public EntitySetImpl getEntitySet(final String name) {
        EntitySetImpl result = null;
        for (EntitySetImpl entitySet : getEntitySets()) {
            if (name.equals(entitySet.getName())) {
                result = entitySet;
            }
        }
        return result;
    }

    public List<SingletonImpl> getSingletons() {
        return singletons;
    }

    public SingletonImpl getSingleton(final String name) {
        SingletonImpl result = null;
        for (SingletonImpl singleton : getSingletons()) {
            if (name.equals(singleton.getName())) {
                result = singleton;
            }
        }
        return result;
    }

    /**
     * Gets the first action import with given name.
     *
     * @param name name.
     * @return action import.
     */
    public ActionImportImpl getActionImport(final String name) {
        final List<ActionImportImpl> actImps = getActionImports(name);
        return actImps.isEmpty()
                ? null
                : actImps.get(0);
    }

    /**
     * Gets all action imports with given name.
     *
     * @param name name.
     * @return action imports.
     */
    public List<ActionImportImpl> getActionImports(final String name) {
        final List<ActionImportImpl> result = new ArrayList<ActionImportImpl>();
        for (ActionImportImpl actionImport : getActionImports()) {
            if (name.equals(actionImport.getName())) {
                result.add(actionImport);
            }
        }
        return result;
    }

    public List<ActionImportImpl> getActionImports() {
        return actionImports;
    }

    @Override
    public List<FunctionImportImpl> getFunctionImports() {
        return functionImports;
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
