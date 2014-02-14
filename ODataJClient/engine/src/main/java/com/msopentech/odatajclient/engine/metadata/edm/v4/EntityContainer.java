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

import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import java.util.ArrayList;
import java.util.List;

public class EntityContainer extends AbstractEntityContainer<FunctionImport> implements AnnotatedEdm {

    private static final long serialVersionUID = 2526002525927260320L;

    private final List<EntitySet> entitySets = new ArrayList<EntitySet>();

    private final List<Singleton> singletons = new ArrayList<Singleton>();

    private final List<ActionImport> actionImports = new ArrayList<ActionImport>();

    private final List<FunctionImport> functionImports = new ArrayList<FunctionImport>();

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

    public List<Singleton> getSingletons() {
        return singletons;
    }

    public Singleton getSingleton(final String name) {
        Singleton result = null;
        for (Singleton singleton : getSingletons()) {
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
    public ActionImport getActionImport(final String name) {
        final List<ActionImport> actImps = getActionImports(name);
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
    public List<ActionImport> getActionImports(final String name) {
        final List<ActionImport> result = new ArrayList<ActionImport>();
        for (ActionImport actionImport : getActionImports()) {
            if (name.equals(actionImport.getName())) {
                result.add(actionImport);
            }
        }
        return result;
    }

    public List<ActionImport> getActionImports() {
        return actionImports;
    }

    @Override
    public List<FunctionImport> getFunctionImports() {
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
