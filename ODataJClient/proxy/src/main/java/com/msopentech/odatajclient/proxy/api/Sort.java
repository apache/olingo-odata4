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
package com.msopentech.odatajclient.proxy.api;

import java.util.Map;

/**
 * Sort option for queries.
 */
public class Sort implements Map.Entry<String, Sort.Direction> {

    /**
     * Enumeration for sort directions.
     */
    public enum Direction {

        ASC,
        DESC;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private final String key;

    private Direction value;

    public Sort(final String key, final Direction value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Direction getValue() {
        return this.value;
    }

    @Override
    public Direction setValue(final Direction value) {
        this.value = value;
        return this.value;
    }
}
