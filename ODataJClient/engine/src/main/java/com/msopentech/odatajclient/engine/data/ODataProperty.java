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
 * OData entity property.
 */
public class ODataProperty implements Serializable, ODataInvokeResult {

    /**
     * Property type.
     */
    public enum PropertyType {

        /**
         * Primitive.
         */
        PRIMITIVE,
        /**
         * Collection
         */
        COLLECTION,
        /**
         * Complex.
         */
        COMPLEX,
        /**
         * Empty type (possibly, no type information could be retrieved).
         */
        EMPTY

    }

    private static final long serialVersionUID = 926939448778950450L;

    /**
     * Property name.
     */
    private final String name;

    /**
     * Property value.
     */
    private ODataValue value;

    /**
     * Constructor.
     *
     * @param name property name.
     * @param value property value.
     */
    ODataProperty(final String name, final ODataValue value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns property name.
     *
     * @return property name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns property value.
     *
     * @return property value.
     */
    public ODataValue getValue() {
        return value;
    }

    /**
     * Updates property value.
     *
     * @param value property value that replaces current.
     */
    public void setValue(final ODataValue value) {
        this.value = value;
    }

    /**
     * Checks if has null value.
     *
     * @return 'TRUE' if has null value; 'FALSE' otherwise.
     */
    public boolean hasNullValue() {
        return this.value == null;
    }

    /**
     * Checks if has primitive value.
     *
     * @return 'TRUE' if has primitive value; 'FALSE' otherwise.
     */
    public boolean hasPrimitiveValue() {
        return !hasNullValue() && this.value.isPrimitive();
    }

    /**
     * Gets primitive value.
     *
     * @return primitive value if exists; null otherwise.
     */
    public ODataPrimitiveValue getPrimitiveValue() {
        return hasPrimitiveValue() ? this.value.asPrimitive() : null;
    }

    /**
     * Checks if has complex value.
     *
     * @return 'TRUE' if has complex value; 'FALSE' otherwise.
     */
    public boolean hasComplexValue() {
        return !hasNullValue() && this.value.isComplex();
    }

    /**
     * Gets complex value.
     *
     * @return complex value if exists; null otherwise.
     */
    public ODataComplexValue getComplexValue() {
        return hasComplexValue() ? this.value.asComplex() : null;
    }

    /**
     * Checks if has collection value.
     *
     * @return 'TRUE' if has collection value; 'FALSE' otherwise.
     */
    public boolean hasCollectionValue() {
        return !hasNullValue() && this.value.isCollection();
    }

    /**
     * Gets collection value.
     *
     * @return collection value if exists; null otherwise.
     */
    public ODataCollectionValue getCollectionValue() {
        return hasCollectionValue() ? this.value.asCollection() : null;
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
