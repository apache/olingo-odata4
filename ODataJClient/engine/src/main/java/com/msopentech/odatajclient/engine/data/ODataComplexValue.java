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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OData complex property value.
 */
public class ODataComplexValue extends ODataValue implements Iterable<ODataProperty> {

    private static final long serialVersionUID = -1878555027714020431L;

    /**
     * Type name.
     */
    private final String typeName;

    /**
     * Complex type fields.
     */
    private final Map<String, ODataProperty> fields = new LinkedHashMap<String, ODataProperty>();

    /**
     * Constructor.
     *
     * @param typeName type name.
     */
    public ODataComplexValue(final String typeName) {
        this.typeName = typeName;
    }

    /**
     * Adds field to the complex type.
     *
     * @param field field to be added.
     */
    public void add(final ODataProperty field) {
        fields.put(field.getName(), field);
    }

    /**
     * Gets field.
     *
     * @param name name of the field to be retrieved.
     * @return requested field.
     */
    public ODataProperty get(final String name) {
        return fields.get(name);
    }

    /**
     * Complex property fields iterator.
     *
     * @return fields iterator.
     */
    @Override
    public Iterator<ODataProperty> iterator() {
        return fields.values().iterator();
    }

    /**
     * Gest value type name.
     *
     * @return value type name.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Gets number of fields.
     *
     * @return number of fields.
     */
    public int size() {
        return fields.size();
    }
}
