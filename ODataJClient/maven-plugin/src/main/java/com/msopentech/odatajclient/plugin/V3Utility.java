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
package com.msopentech.odatajclient.plugin;

import com.msopentech.odatajclient.engine.utils.NavigationPropertyBindingDetails;
import com.msopentech.odatajclient.engine.metadata.AbstractEdmMetadata;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.EdmV3Type;
import com.msopentech.odatajclient.engine.metadata.edm.AbstractNavigationProperty;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Association;
import com.msopentech.odatajclient.engine.metadata.edm.v3.AssociationEnd;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.metadata.edm.v3.NavigationProperty;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Schema;
import com.msopentech.odatajclient.engine.utils.AssociationSetBindingDetails;
import com.msopentech.odatajclient.engine.utils.MetadataUtils;
import java.util.ArrayList;
import java.util.List;

public class V3Utility extends AbstractUtility {

    private final EdmV3Metadata metadata;

    private final Schema schema;

    public V3Utility(final EdmV3Metadata metadata, final Schema schema, final String basePackage) {
        super(schema.getNamespace(), schema.getAlias(), basePackage);
        this.metadata = metadata;
        this.schema = schema;

        collectEntityTypes();
    }

    @Override
    public EdmType getEdmType(final AbstractEdmMetadata<?, ?, ?, ?, ?, ?, ?> metadata, final String expression) {
        return new EdmV3Type((EdmV3Metadata) metadata, expression);
    }

    @Override
    protected EdmV3Metadata getMetadata() {
        return metadata;
    }

    @Override
    protected Schema getSchema() {
        return schema;
    }

    @Override
    public String getNavigationType(final AbstractNavigationProperty property) {
        final NavigationProperty navigationProperty = (NavigationProperty) property;

        final String name = getNameFromNS(navigationProperty.getRelationship());
        final Association association = schema.getAssociation(name);
        if (association != null) {
            for (AssociationEnd end : association.getEnds()) {
                if (end.getRole().equalsIgnoreCase(navigationProperty.getToRole())) {
                    return "*".equals(end.getMultiplicity())
                            ? "Collection(" + end.getType() + ")"
                            : end.getType();
                }
            }
        }

        return navigationProperty.getToRole();
    }

    public NavigationPropertyBindingDetails getNavigationBindingDetails(final AbstractNavigationProperty property) {
        final NavigationProperty navProperty = (NavigationProperty) property;

        // 1) get association
        final Association association = MetadataUtils.getAssociation(schema, navProperty.getRelationship());

        // 2) get schema,  entity container and association set
        final AssociationSetBindingDetails associationSetDetails =
                MetadataUtils.getAssociationSetBindingDetails(association, schema.getNamespace(), metadata);

        // 3) get navigation property binding details
        return getNavigationBindingDetails(
                property,
                MetadataUtils.getEntitySetName(associationSetDetails.getAssociationSet(), navProperty.getToRole()),
                associationSetDetails.getSchema(),
                associationSetDetails.getContainer());
    }

    public List<FunctionImport> getFunctionImportsBoundTo(
            final String typeExpression, final boolean collection) {

        final List<FunctionImport> result = new ArrayList<FunctionImport>();

        for (EntityContainer entityContainer : schema.getEntityContainers()) {
            for (FunctionImport functionImport : entityContainer.getFunctionImports()) {
                if (functionImport.isBindable()) {
                    for (int i = 0; i < functionImport.getParameters().size(); i++) {
                        if (isSameType(typeExpression, functionImport.getParameters().get(i).getType(), collection)) {
                            result.add(functionImport);
                        }
                    }
                }
            }
        }

        return result;
    }
}
