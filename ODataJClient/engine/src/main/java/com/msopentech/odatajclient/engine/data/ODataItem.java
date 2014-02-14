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
package com.msopentech.odatajclient.engine.data;

import java.io.Serializable;
import java.net.URI;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of OData entities and links.
 */
public abstract class ODataItem implements Serializable {

    private static final long serialVersionUID = -2600707722689304686L;

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ODataItem.class);

    /**
     * OData item self link.
     */
    protected URI link;

    /**
     * OData entity name/type.
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param name OData entity name.
     */
    public ODataItem(final String name) {
        this.name = name;
    }

    /**
     * Returns self link.
     *
     * @return entity edit link.
     */
    public URI getLink() {
        return link;
    }

    /**
     * Sets self link.
     *
     * @param link link.
     */
    public void setLink(final URI link) {
        this.link = link;
    }

    /**
     * Returns OData entity name.
     *
     * @return entity name.
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
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
