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

import com.msopentech.odatajclient.engine.data.ODataLinkType;
import com.msopentech.odatajclient.proxy.api.impl.EntityTypeInvocationHandler;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EntityLinkDesc implements Serializable {

    private static final long serialVersionUID = 704670372070370762L;

    private final String sourceName;

    private final EntityTypeInvocationHandler source;

    private final Collection<EntityTypeInvocationHandler> targets;

    private final ODataLinkType type;

    public EntityLinkDesc(
            final String sourceName,
            final EntityTypeInvocationHandler source,
            final Collection<EntityTypeInvocationHandler> target,
            final ODataLinkType type) {
        this.sourceName = sourceName;
        this.source = source;
        this.targets = target;
        this.type = type;
    }

    public EntityLinkDesc(
            final String sourceName,
            final EntityTypeInvocationHandler source,
            final EntityTypeInvocationHandler target,
            final ODataLinkType type) {
        this.sourceName = sourceName;
        this.source = source;
        this.targets = Collections.<EntityTypeInvocationHandler>singleton(target);
        this.type = type;
    }

    public String getSourceName() {
        return sourceName;
    }

    public EntityTypeInvocationHandler getSource() {
        return source;
    }

    public Collection<EntityTypeInvocationHandler> getTargets() {
        return targets;
    }

    public ODataLinkType getType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
