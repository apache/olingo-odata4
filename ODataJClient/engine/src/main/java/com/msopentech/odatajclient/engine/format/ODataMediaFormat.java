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
 * Available formats for media.
 */
public enum ODataMediaFormat {

    CHARSET_PARAMETER("charset"),
    MEDIA_TYPE_WILDCARD("*"),
    WILDCARD("*/*"),
    APPLICATION_XML(ContentType.APPLICATION_XML.getMimeType()),
    APPLICATION_ATOM_XML(ContentType.APPLICATION_ATOM_XML.getMimeType()),
    APPLICATION_XHTML_XML(ContentType.APPLICATION_XHTML_XML.getMimeType()),
    APPLICATION_SVG_XML(ContentType.APPLICATION_SVG_XML.getMimeType()),
    APPLICATION_JSON(ContentType.APPLICATION_JSON.getMimeType()),
    APPLICATION_FORM_URLENCODED(ContentType.APPLICATION_FORM_URLENCODED.getMimeType()),
    MULTIPART_FORM_DATA(ContentType.MULTIPART_FORM_DATA.getMimeType()),
    APPLICATION_OCTET_STREAM(ContentType.APPLICATION_OCTET_STREAM.getMimeType()),
    TEXT_PLAIN(ContentType.TEXT_PLAIN.getMimeType()),
    TEXT_XML(ContentType.TEXT_XML.getMimeType()),
    TEXT_HTML(ContentType.TEXT_HTML.getMimeType());

    private final String format;

    private ODataMediaFormat(final String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }

    public static ODataMediaFormat fromFormat(final String format) {
        final String _format = format.split(";")[0];

        ODataMediaFormat result = null;

        for (ODataMediaFormat value : values()) {
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
