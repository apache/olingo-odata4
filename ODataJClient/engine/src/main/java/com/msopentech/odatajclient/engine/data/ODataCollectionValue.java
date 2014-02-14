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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * OData collection property value.
 */
public class ODataCollectionValue extends ODataValue implements Iterable<ODataValue> {

    private static final long serialVersionUID = -3665659846001987187L;

    /**
     * Type name;
     */
    private final String typeName;

    /**
     * Values.
     */
    private final List<ODataValue> values = new ArrayList<ODataValue>();

    /**
     * Constructor.
     *
     * @param typeName type name.
     */
    public ODataCollectionValue(final String typeName) {
        this.typeName = typeName;
    }

    /**
     * Adds a value to the collection.
     *
     * @param value value to be added.
     */
    public void add(final ODataValue value) {
        if (value.isPrimitive() || value.isComplex()) {
            values.add(value);
        }
    }

    /**
     * Value iterator.
     *
     * @return value iterator.
     */
    @Override
    public Iterator<ODataValue> iterator() {
        return values.iterator();
    }

    /**
     * Gets value type name.
     *
     * @return value type name.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Gets collection size.
     *
     * @return collection size.
     */
    public int size() {
        return values.size();
    }

    /**
     * Checks if collection is empty.
     *
     * @return 'TRUE' if empty; 'FALSE' otherwise.
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }
}
