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
package com.msopentech.odatajclient.proxy.api.context;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EntityUUID implements Serializable {

    private static final long serialVersionUID = 4855025769803086495L;

    private final int tempKey;

    private final String schemaName;

    private final String containerName;

    private final String entitySetName;

    private final String name;

    private final Object key;

    public EntityUUID(
            final String schemaName,
            final String containerName,
            final String entitySetName,
            final String name) {
        this(schemaName, containerName, entitySetName, name, null);
    }

    public EntityUUID(
            final String schemaName,
            final String containerName,
            final String entitySetName,
            final String name,
            final Object key) {
        this.schemaName = schemaName;
        this.containerName = containerName;
        this.entitySetName = entitySetName;
        this.name = name;
        this.key = key;
        this.tempKey = (int) (Math.random() * 1000000);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getEntitySetName() {
        return entitySetName;
    }

    public String getName() {
        return name;
    }

    public Object getKey() {
        return key;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        return key == null
                ? EqualsBuilder.reflectionEquals(this, obj)
                : EqualsBuilder.reflectionEquals(this, obj, "tempKey");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "tempKey");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return schemaName + ":" + containerName + ":" + entitySetName + ":" + name
                + "(" + (key == null ? null : key.toString()) + ")";
    }
}
