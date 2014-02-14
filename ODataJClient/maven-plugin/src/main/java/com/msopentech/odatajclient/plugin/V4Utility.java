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
package com.msopentech.odatajclient.plugin;

import com.msopentech.odatajclient.engine.metadata.AbstractEdmMetadata;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.EdmV4Metadata;
import com.msopentech.odatajclient.engine.metadata.EdmV4Type;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractNavigationProperty;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Action;
import com.msopentech.odatajclient.engine.metadata.edm.v4.EntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Function;
import com.msopentech.odatajclient.engine.metadata.edm.v4.NavigationProperty;
import com.msopentech.odatajclient.engine.metadata.edm.v4.NavigationPropertyBinding;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Schema;
import com.msopentech.odatajclient.engine.metadata.edm.v4.Singleton;
import com.msopentech.odatajclient.engine.utils.MetadataUtils;
import com.msopentech.odatajclient.engine.utils.NavigationPropertyBindingDetails;
import com.msopentech.odatajclient.engine.utils.QualifiedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class V4Utility extends AbstractUtility {

    private final EdmV4Metadata metadata;

    private final Schema schema;

    public V4Utility(final EdmV4Metadata metadata, final Schema schema, final String basePackage) {
        super(schema.getNamespace(), schema.getAlias(), basePackage);
        this.metadata = metadata;
        this.schema = schema;

        collectEntityTypes();
    }

    @Override
    public EdmType getEdmType(final AbstractEdmMetadata<?, ?, ?, ?, ?, ?, ?> metadata, final String expression) {
        return new EdmV4Type((EdmV4Metadata) metadata, expression);
    }

    public EdmType getEdmType(final Singleton singleton) {
        return getEdmType(getMetadata(), singleton.getType());
    }

    public Map<String, String> getEntityKeyType(final Singleton singleton) {
        return getEntityKeyType(getEdmType(metadata, singleton.getType()).getEntityType());
    }

    @Override
    protected EdmV4Metadata getMetadata() {
        return metadata;
    }

    @Override
    protected Schema getSchema() {
        return schema;
    }

    @Override
    public String getNavigationType(final AbstractNavigationProperty property) {
        return ((NavigationProperty) property).getType();
    }

    public NavigationPropertyBindingDetails getNavigationBindingDetails(
            final String sourceEntityType, final AbstractNavigationProperty property) {

        NavigationPropertyBindingDetails bindingDetails = null;
        final List<Schema> schemas = getMetadata().getSchemas();
        for (int i = 0; bindingDetails == null && i < schemas.size(); i++) {
            final Schema sc = schemas.get(i);
            if (sc.getEntityContainer() != null) {
                bindingDetails = getNavigationBindingDetails(sc, sourceEntityType, property);
            }
        }
        return bindingDetails;
    }

    public Function getFunctionByName(final String name) {
        final QualifiedName qname = new QualifiedName(name);

        final Schema targetSchema =
                (Schema) MetadataUtils.getSchemaByNamespaceOrAlias(metadata, qname.getNamespace());

        if (targetSchema != null) {
            for (Function function : targetSchema.getFunctions()) {
                if (function.getName().equals(qname.getName())) {
                    return function;
                }
            }
        }

        return null;
    }

    public Action getActionByName(final String name) {
        final QualifiedName qname = new QualifiedName(name);

        final Schema targetSchema =
                (Schema) MetadataUtils.getSchemaByNamespaceOrAlias(metadata, qname.getNamespace());

        if (targetSchema != null) {
            for (Action action : targetSchema.getActions()) {
                if (action.getName().equals(qname.getName())) {
                    return action;
                }
            }
        }

        return null;
    }

    public List<Function> getFunctionsBoundTo(final String typeExpression, final boolean collection) {

        final List<Function> result = new ArrayList<Function>();

        for (Schema sch : getMetadata().getSchemas()) {
            for (Function function : sch.getFunctions()) {
                if (function.isBound()) {
                    if (!function.getParameters().isEmpty() && isSameType(
                            new QualifiedName(namespace, typeExpression).toString(),
                            function.getParameters().get(0).getType(), collection)) {
                        result.add(function);
                    }
                }
            }
        }

        return result;
    }

    public List<Action> getActionsBoundTo(final String typeExpression, final boolean collection) {

        final List<Action> result = new ArrayList<Action>();

        for (Schema sch : getMetadata().getSchemas()) {
            for (Action action : sch.getActions()) {
                if (action.isBound()) {
                    if (!action.getParameters().isEmpty()
                            && isSameType(typeExpression, action.getParameters().get(0).getType(), collection)) {
                        result.add(action);
                    }
                }
            }
        }

        return result;
    }

    private NavigationPropertyBindingDetails getNavigationBindingDetails(
            final AbstractSchema<?, ?, ?, ?> schema,
            final String sourceEntityType,
            final AbstractNavigationProperty property) {

        for (AbstractEntityContainer<?> container : schema.getEntityContainers()) {
            for (AbstractEntitySet es : container.getEntitySets()) {
                if (es.getEntityType().equals(sourceEntityType)) {
                    final NavigationPropertyBinding binding =
                            MetadataUtils.getNavigationBindingByPath(((EntitySet) es).getNavigationPropertyBindings(),
                            property.getName());
                    if (binding != null) {
                        return getNavigationBindingDetails(
                                property, binding.getTarget(), schema, container);
                    }
                }
            }
        }

        return null;
    }
}
