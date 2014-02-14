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

import com.msopentech.odatajclient.engine.utils.NavigationPropertyBindingDetails;
import com.msopentech.odatajclient.engine.metadata.AbstractEdmMetadata;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityType;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractNavigationProperty;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractProperty;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.PropertyRef;
import com.msopentech.odatajclient.engine.utils.MetadataUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractUtility {

    protected static final String FC_TARGET_PATH = "fcTargetPath";

    protected static final String FC_SOURCE_PATH = "fcSourcePath";

    protected static final String FC_KEEP_IN_CONTENT = "fcKeepInContent";

    protected static final String FC_CONTENT_KIND = "fcContentKind";

    protected static final String FC_NS_PREFIX = "fcNSPrefix";

    protected static final String FC_NS_URI = "fcNSURI";

    protected static final String TYPE_SUB_PKG = "types";

    protected final String basePackage;

    protected final String schemaName;

    protected final String namespace;

    protected final Map<String, List<AbstractEntityType>> allEntityTypes =
            new HashMap<String, List<AbstractEntityType>>();

    public AbstractUtility(final String namespace, final String schemaName, final String basePackage) {
        this.basePackage = basePackage;
        this.schemaName = schemaName == null ? getNameFromNS(namespace) : schemaName;
        this.namespace = namespace;
    }

    public abstract EdmType getEdmType(
            final AbstractEdmMetadata<?, ?, ?, ?, ?, ?, ?> metadata, final String expression);

    protected abstract AbstractEdmMetadata<?, ?, ?, ?, ?, ?, ?> getMetadata();

    protected abstract AbstractSchema<?, ?, ?, ?> getSchema();

    protected void collectEntityTypes() {
        for (AbstractSchema<?, ?, ?, ?> _schema : getMetadata().getSchemas()) {
            allEntityTypes.put(_schema.getNamespace(), new ArrayList<AbstractEntityType>(_schema.getEntityTypes()));
            if (StringUtils.isNotBlank(_schema.getAlias())) {
                allEntityTypes.put(_schema.getAlias(), new ArrayList<AbstractEntityType>(_schema.getEntityTypes()));
            }
        }
    }

    public String getJavaType(final String typeExpression) {
        final StringBuilder res = new StringBuilder();

        final EdmType edmType = getEdmType(getMetadata(), typeExpression);

        if (edmType.isCollection() && !edmType.isEntityType()) {
            res.append("Collection<");
        }

        if ("Edm.Stream".equals(typeExpression)) {
            res.append(InputStream.class.getName());
        } else if (edmType.isSimpleType()) {
            res.append(edmType.getSimpleType().javaType().getSimpleName());
        } else if (edmType.isComplexType()) {
            res.append(basePackage).append('.').append(edmType.getNamespaceOrAlias().toLowerCase()).append('.').
                    append(TYPE_SUB_PKG).append('.').append(capitalize(edmType.getComplexType().getName()));
        } else if (edmType.isEntityType()) {
            res.append(basePackage).append('.').append(edmType.getNamespaceOrAlias().toLowerCase()).append('.').
                    append(TYPE_SUB_PKG).append('.').append(capitalize(edmType.getEntityType().getName()));
        } else if (edmType.isEnumType()) {
            res.append(basePackage).append('.').append(edmType.getNamespaceOrAlias().toLowerCase()).
                    append('.').append(TYPE_SUB_PKG).append('.').append(capitalize(edmType.getEnumType().getName()));
        } else {
            throw new IllegalArgumentException("Invalid type expression '" + typeExpression + "'");
        }

        if (edmType.isCollection()) {
            if (edmType.isEntityType()) {
                res.append("Collection");
            } else {
                res.append(">");
            }
        }

        return res.toString();
    }

    public EdmSimpleType getEdmSimpleType(final String expression) {
        return EdmSimpleType.fromValue(expression);
    }

    public EdmType getEdmType(final AbstractEntitySet entitySet) {
        return getEdmType(getMetadata(), entitySet.getEntityType());
    }

    public Map<String, String> getEntityKeyType(final AbstractEntitySet entitySet) {
        return getEntityKeyType(getEdmType(entitySet).getEntityType());
    }

    public Map<String, String> getEntityKeyType(final AbstractEntityType entityType) {
        AbstractEntityType baseType = entityType;
        while (baseType.getKey() == null && baseType.getBaseType() != null) {
            baseType = getEdmType(getMetadata(), baseType.getBaseType()).getEntityType();
        }

        final List<String> properties = new ArrayList<String>();
        for (PropertyRef pref : baseType.getKey().getPropertyRefs()) {
            properties.add(pref.getName());
        }
        final Map<String, String> res = new HashMap<String, String>();

        for (AbstractProperty prop : baseType.getProperties()) {
            if (properties.contains(prop.getName())) {
                res.put(prop.getName(), getJavaType(prop.getType()));
            }
        }
        return res;
    }

    public final String getNameInNamespace(final String name) {
        return getSchema().getNamespace() + "." + name;
    }

    public final String getNameInNamespace(final EdmType entityType) {
        return entityType.getNamespaceOrAlias() + "." + entityType.getEntityType().getName();
    }

    public boolean isSameType(
            final String entityTypeExpression, final String fullTypeExpression, final boolean collection) {

        final Set<String> types = new HashSet<String>(2);

        types.add((collection ? "Collection(" : StringUtils.EMPTY)
                + getNameInNamespace(entityTypeExpression)
                + (collection ? ")" : StringUtils.EMPTY));
        if (StringUtils.isNotBlank(getSchema().getAlias())) {
            types.add((collection ? "Collection(" : StringUtils.EMPTY)
                    + getSchema().getAlias() + "." + entityTypeExpression
                    + (collection ? ")" : StringUtils.EMPTY));
        }

        return types.contains(fullTypeExpression);
    }

    private void populateDescendants(final EdmType base, final List<String> descendants) {
        for (Map.Entry<String, List<AbstractEntityType>> entry : allEntityTypes.entrySet()) {
            for (AbstractEntityType type : entry.getValue()) {
                if (StringUtils.isNotBlank(type.getBaseType())
                        && base.getEntityType().getName().equals(getNameFromNS(type.getBaseType()))) {

                    final EdmType entityType = getEdmType(getMetadata(), entry.getKey() + "." + type.getName());

                    descendants.add(getNameInNamespace(entityType));
                    populateDescendants(entityType, descendants);
                }
            }
        }
    }

    public List<String> getDescendantsOrSelf(final EdmType entityType) {
        final List<String> descendants = new ArrayList<String>();

        descendants.add(getNameInNamespace(entityType));
        populateDescendants(entityType, descendants);

        return descendants;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getNamespace() {
        return namespace;
    }

    public String capitalize(final String str) {
        return StringUtils.capitalize(str);
    }

    public String uncapitalize(final String str) {
        return StringUtils.uncapitalize(str);
    }

    public Map<String, String> getFcProperties(final AbstractProperty property) {
        final Map<String, String> fcProps = new HashMap<String, String>();

        if (StringUtils.isNotBlank(property.getFcTargetPath())) {
            fcProps.put(FC_TARGET_PATH, property.getFcTargetPath());
        }
        if (StringUtils.isNotBlank(property.getFcSourcePath())) {
            fcProps.put(FC_SOURCE_PATH, property.getFcSourcePath());
        }
        if (StringUtils.isNotBlank(property.getFcNSPrefix())) {
            fcProps.put(FC_NS_PREFIX, property.getFcNSPrefix());
        }
        if (StringUtils.isNotBlank(property.getFcNSURI())) {
            fcProps.put(FC_NS_URI, property.getFcNSURI());
        }
        fcProps.put(FC_CONTENT_KIND, property.getFcContentKind().name());
        fcProps.put(FC_KEEP_IN_CONTENT, Boolean.toString(property.isFcKeepInContent()));

        return fcProps;
    }

    public final String getNameFromNS(final String ns) {
        return getNameFromNS(ns, false);
    }

    public final String getNameFromNS(final String ns, final boolean toLowerCase) {
        String res = null;

        if (StringUtils.isNotBlank(ns)) {
            final int lastpt = ns.lastIndexOf('.');
            res = ns.substring(lastpt < 0 ? 0 : lastpt + 1);
            res = toLowerCase ? res.toLowerCase() : res;
        }

        return res;
    }

    protected NavigationPropertyBindingDetails getNavigationBindingDetails(
            final AbstractNavigationProperty property,
            final String targetPath,
            final AbstractSchema<?, ?, ?, ?> bindingInfoSchema,
            final AbstractEntityContainer<?> bindingInfoContainer) {

        final String[] target = targetPath.split("/");
        final AbstractEntityContainer<?> targetContainer;
        final AbstractSchema<?, ?, ?, ?> targetSchema;
        final AbstractEntitySet targetES;

        if (target.length > 1) {
            int lastDot = target[0].lastIndexOf(".");
            final String targetSchemaNamespace = target[0].substring(0, lastDot);
            final String containerName = target[0].substring(lastDot + 1);

            targetSchema = MetadataUtils.getSchemaByNamespaceOrAlias(getMetadata(), targetSchemaNamespace);
            targetContainer = MetadataUtils.getContainerByName(targetSchema, containerName);
            targetES = MetadataUtils.getEntitySet(targetContainer, target[1]);
        } else {
            targetContainer = bindingInfoContainer;
            targetSchema = bindingInfoSchema;
            targetES = MetadataUtils.getEntitySet(targetContainer, target[0]);
        }
        return new NavigationPropertyBindingDetails(property, targetES, targetContainer, targetSchema);
    }

    public abstract String getNavigationType(final AbstractNavigationProperty property);
}
