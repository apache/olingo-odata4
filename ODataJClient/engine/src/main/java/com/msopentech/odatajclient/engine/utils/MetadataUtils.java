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

import com.msopentech.odatajclient.engine.metadata.AbstractEdmMetadata;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractEntitySet;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractSchema;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Association;
import com.msopentech.odatajclient.engine.metadata.edm.v3.AssociationSet;
import com.msopentech.odatajclient.engine.metadata.edm.v3.AssociationSetEnd;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Schema;
import com.msopentech.odatajclient.engine.metadata.edm.v4.NavigationPropertyBinding;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metadata utilities.
 */
public class MetadataUtils {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetadataUtils.class);

    public static AssociationSetBindingDetails getAssociationSetBindingDetails(
            final Association association, final String associationNamespace, final EdmV3Metadata metadata) {

        final StringBuilder associationName = new StringBuilder();
        associationName.append(associationNamespace).append('.').append(association.getName());

        for (Schema schema : metadata.getSchemas()) {
            for (EntityContainer container : schema.getEntityContainers()) {
                final AssociationSet associationSet = getAssociationSet(associationName.toString(), container);
                if (associationSet != null) {
                    return new AssociationSetBindingDetails(associationSet, container, schema);
                }
            }
        }

        throw new IllegalStateException("Association set not found");
    }

    public static Association getAssociation(final Schema schema, final String relationship) {
        return schema.getAssociation(relationship.substring(relationship.lastIndexOf('.') + 1));
    }

    public static AssociationSet getAssociationSet(final String association, final EntityContainer container) {
        LOG.debug("Search for association set {}", association);

        for (AssociationSet associationSet : container.getAssociationSets()) {
            LOG.debug("Retrieved association set '{}:{}'", associationSet.getName(), associationSet.getAssociation());
            if (associationSet.getAssociation().equals(association)) {
                return associationSet;
            }
        }

        return null;
    }

    public static AbstractSchema<?, ?, ?, ?> getSchemaByNamespaceOrAlias(
            final AbstractEdmMetadata<?, ?, ?, ?, ?, ?, ?> metadata,
            final String name) {

        if (StringUtils.isNotBlank(name)) {
            for (AbstractSchema<?, ?, ?, ?> schema : metadata.getSchemas()) {
                if (name.equals(schema.getNamespace()) || name.equals(schema.getAlias())) {
                    return schema;
                }
            }
        }

        return null;
    }

    public static AbstractEntityContainer<?> getContainerByName(
            final AbstractSchema<?, ?, ?, ?> schema,
            final String name) {

        for (AbstractEntityContainer<?> container : schema.getEntityContainers()) {
            if (container.getName().equals(name)) {
                return container;
            }
        }

        return null;
    }

    public static AbstractEntitySet getEntitySet(
            final AssociationSetBindingDetails associationSetBindingDetails, final String name) {

        final String entitySetName = getEntitySetName(associationSetBindingDetails.getAssociationSet(), name);

        for (AbstractEntitySet entitySet : associationSetBindingDetails.getContainer().getEntitySets()) {
            if (entitySet.getName().equals(entitySetName)) {
                return entitySet;
            }
        }

        return null;
    }

    public static AbstractEntitySet getEntitySet(
            final AbstractEdmMetadata<?, ?, ?, ?, ?, ?, ?> metadata,
            final String namespace,
            final String containerName,
            final String name) {

        for (AbstractSchema<?, ?, ?, ?> schema : metadata.getSchemas()) {
            if (schema.getNamespace().equals(namespace)) {
                for (AbstractEntityContainer<?> container : schema.getEntityContainers()) {
                    if (containerName.equals(container.getName())) {
                        for (AbstractEntitySet entitySet : container.getEntitySets()) {
                            if (entitySet.getName().equals(name)) {
                                return entitySet;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static AbstractEntitySet getEntitySet(final AbstractEntityContainer<?> container, final String name) {

        for (AbstractEntitySet entitySet : container.getEntitySets()) {
            if (entitySet.getName().equalsIgnoreCase(name)) {
                return entitySet;
            }
        }

        return null;
    }

    public static NavigationPropertyBinding getNavigationBindingByPath(
            final List<NavigationPropertyBinding> bindings,
            final String path) {
        for (NavigationPropertyBinding binding : bindings) {
            if (binding.getPath().equals(path)) {
                return binding;
            }
        }

        return null;
    }

    public static String getEntitySetName(final AssociationSet associationSet, final String role) {
        for (AssociationSetEnd end : associationSet.getEnds()) {
            if (end.getRole().equals(role)) {
                return end.getEntitySet();
            }
        }
        return null;
    }
}
