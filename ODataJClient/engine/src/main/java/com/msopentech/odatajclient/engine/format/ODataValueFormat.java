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
package com.msopentech.odatajclient.engine.format;

import org.apache.http.entity.ContentType;

/**
 * Available formats for property values.
 */
public enum ODataValueFormat {

    /**
     * Application octet stream.
     */
    STREAM(ContentType.APPLICATION_OCTET_STREAM.getMimeType()),
    /**
     * Plain text format.
     */
    TEXT(ContentType.TEXT_PLAIN.getMimeType());

    private final String format;

    ODataValueFormat(final String format) {
        this.format = format;
    }

    /**
     * Gets format as a string.
     *
     * @return format as a string.
     */
    @Override
    public String toString() {
        return format;
    }

    /**
     * Gets format from its string representation.
     *
     * @param format string representation of the format.
     * @return OData format.
     */
    public static ODataValueFormat fromString(final String format) {
        final String _format = format.split(";")[0];

        ODataValueFormat result = null;

        for (ODataValueFormat value : values()) {
            if (_format.equals(value.toString())) {
                result = value;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        return result;
    }
}
