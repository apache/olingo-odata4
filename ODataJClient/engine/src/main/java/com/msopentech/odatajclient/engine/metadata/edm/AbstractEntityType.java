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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;

@JsonDeserialize(using = EntityTypeDeserializer.class)
public abstract class AbstractEntityType extends AbstractComplexType {

    private static final long serialVersionUID = -1579462552966168139L;

    private boolean abstractEntityType = false;

    private String baseType;

    private boolean openType = false;

    private boolean hasStream = false;

    private EntityKey key;

    public boolean isAbstractEntityType() {
        return abstractEntityType;
    }

    public void setAbstractEntityType(final boolean abstractEntityType) {
        this.abstractEntityType = abstractEntityType;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(final String baseType) {
        this.baseType = baseType;
    }

    public boolean isOpenType() {
        return openType;
    }

    public void setOpenType(final boolean openType) {
        this.openType = openType;
    }

    public EntityKey getKey() {
        return key;
    }

    public void setKey(final EntityKey key) {
        this.key = key;
    }

    public boolean isHasStream() {
        return hasStream;
    }

    public void setHasStream(final boolean hasStream) {
        this.hasStream = hasStream;
    }

    public abstract List<? extends AbstractNavigationProperty> getNavigationProperties();

    public abstract AbstractNavigationProperty getNavigationProperty(String name);

}
