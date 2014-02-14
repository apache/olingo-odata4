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
package com.msopentech.odatajclient.engine.format;

import org.apache.http.entity.ContentType;

/**
 * Available formats to be used in various contexts.
 */
public enum ODataFormat {

    /**
     * JSON format with no metadata.
     */
    JSON_NO_METADATA(ContentType.APPLICATION_JSON.getMimeType() + ";odata=nometadata"),
    /**
     * JSON format with minimal metadata (default).
     */
    JSON(ContentType.APPLICATION_JSON.getMimeType() + ";odata=minimalmetadata"),
    /**
     * JSON format with no metadata.
     */
    JSON_FULL_METADATA(ContentType.APPLICATION_JSON.getMimeType() + ";odata=fullmetadata"),
    /**
     * XML format.
     */
    XML(ContentType.APPLICATION_XML.getMimeType());

    private final String format;

    ODataFormat(final String format) {
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
     * Gets OData format from its string representation.
     *
     * @param format string representation of the format.
     * @return OData format.
     */
    public static ODataFormat fromString(final String format) {
        ODataFormat result = null;

        final StringBuffer _format = new StringBuffer();

        final String[] parts = format.split(";");
        _format.append(parts[0]);
        if (ContentType.APPLICATION_JSON.getMimeType().equals(parts[0])) {
            if (parts.length > 1) {
                _format.append(';').append(parts[1]);
            } else {
                result = ODataFormat.JSON;
            }
        }

        if (result == null) {
            final String candidate = _format.toString();
            for (ODataFormat value : values()) {
                if (candidate.equals(value.toString())) {
                    result = value;
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        return result;
    }
}
