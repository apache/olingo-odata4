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
package com.msopentech.odatajclient.engine.data;

import java.io.Serializable;
import java.net.URI;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Representation of an OData operation (legacy, action or function).
 */
public class ODataOperation implements Serializable {

    private static final long serialVersionUID = -5784652334334645128L;

    private String metadataAnchor;

    private String title;

    private URI target;

    /**
     * Gets metadata anchor.
     *
     * @return metadata anchor.
     */
    public String getMetadataAnchor() {
        return metadataAnchor;
    }

    /**
     * Sets metadata anchor.
     *
     * @param metadataAnchor metadata anchor.
     */
    public void setMetadataAnchor(final String metadataAnchor) {
        this.metadataAnchor = metadataAnchor;
    }

    /**
     * Gets title.
     *
     * @return title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title title.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets target.
     *
     * @return target.
     */
    public URI getTarget() {
        return target;
    }

    /**
     * Sets target.
     *
     * @param target target.
     */
    public void setTarget(final URI target) {
        this.target = target;
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
