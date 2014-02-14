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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Abstract representation of an OData entity property value.
 */
public abstract class ODataValue implements Serializable {

    private static final long serialVersionUID = 7445422004232581877L;

    /**
     * Check is is a primitive value.
     *
     * @return 'TRUE' if primitive; 'FALSE' otherwise.
     */
    public boolean isPrimitive() {
        return (this instanceof ODataPrimitiveValue);
    }

    /**
     * Casts to primitive value.
     *
     * @return primitive value.
     */
    public ODataPrimitiveValue asPrimitive() {
        return isPrimitive() ? (ODataPrimitiveValue) this : null;
    }

    /**
     * Check is is a complex value.
     *
     * @return 'TRUE' if complex; 'FALSE' otherwise.
     */
    public boolean isComplex() {
        return (this instanceof ODataComplexValue);
    }

    /**
     * Casts to complex value.
     *
     * @return complex value.
     */
    public ODataComplexValue asComplex() {
        return isComplex() ? (ODataComplexValue) this : null;
    }

    /**
     * Check is is a collection value.
     *
     * @return 'TRUE' if collection; 'FALSE' otherwise.
     */
    public boolean isCollection() {
        return (this instanceof ODataCollectionValue);
    }

    /**
     * Casts to collection value.
     *
     * @return collection value.
     */
    public ODataCollectionValue asCollection() {
        return isCollection() ? (ODataCollectionValue) this : null;
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
