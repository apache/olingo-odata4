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

import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Schema extends AbstractSchema<EntityContainer, EntityType, ComplexType, FunctionImport>
        implements AnnotatedEdm {

    private static final long serialVersionUID = 4453992249818796144L;

    private final List<Action> actions = new ArrayList<Action>();

    private final List<Annotations> annotationsList = new ArrayList<Annotations>();

    private final List<Annotation> annotations = new ArrayList<Annotation>();

    private final List<ComplexType> complexTypes = new ArrayList<ComplexType>();

    private EntityContainer entityContainer;

    private final List<EnumType> enumTypes = new ArrayList<EnumType>();

    private final List<EntityType> entityTypes = new ArrayList<EntityType>();

    private final List<Function> functions = new ArrayList<Function>();

    private final List<Term> terms = new ArrayList<Term>();

    private final List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();

    private Annotation annotation;

    public List<Action> getActions() {
        return actions;
    }

    public List<Action> getActions(final String name) {
        final List<Action> result = new ArrayList<Action>();
        for (Action action : getActions()) {
            if (name.equals(action.getName())) {
                result.add(action);
            }
        }
        return result;
    }

    @Override
    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    @Override
    public Annotations getAnnotationsList(final String target) {
        Annotations result = null;
        for (Annotations annots : getAnnotationsList()) {
            if (target.equals(annots.getTarget())) {
                result = annots;
            }
        }
        return result;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public List<EnumType> getEnumTypes() {
        return enumTypes;
    }

    @Override
    public EnumType getEnumType(final String name) {
        EnumType result = null;
        for (EnumType type : getEnumTypes()) {
            if (name.equals(type.getName())) {
                result = type;
            }
        }
        return result;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public List<Function> getFunctions(final String name) {
        final List<Function> result = new ArrayList<Function>();
        for (Function function : getFunctions()) {
            if (name.equals(function.getName())) {
                result.add(function);
            }
        }
        return result;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }

    public EntityContainer getEntityContainer() {
        return entityContainer;
    }

    public void setEntityContainer(final EntityContainer entityContainer) {
        this.entityContainer = entityContainer;
    }

    @Override
    public List<EntityContainer> getEntityContainers() {
        return entityContainer == null
                ? Collections.<EntityContainer>emptyList() : Collections.singletonList(entityContainer);
    }

    @Override
    public EntityContainer getDefaultEntityContainer() {
        return entityContainer;
    }

    @Override
    public EntityContainer getEntityContainer(final String name) {
        if (entityContainer != null && name.equals(entityContainer.getName())) {
            return entityContainer;
        }
        throw new IllegalArgumentException("No EntityContainer found with name " + name);
    }

    @Override
    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    @Override
    public List<ComplexType> getComplexTypes() {
        return complexTypes;
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
