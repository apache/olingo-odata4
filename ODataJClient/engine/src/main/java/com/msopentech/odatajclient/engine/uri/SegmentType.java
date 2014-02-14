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
package com.msopentech.odatajclient.engine.uri;

import org.apache.commons.lang3.StringUtils;

/**
 * URI Segment types.
 */
public enum SegmentType {

    ENTITYSET,
    ENTITYTYPE,
    KEY,
    KEY_AS_SEGMENT,
    NAVIGATION,
    STRUCTURAL,
    VALUE("$value"),
    FUNCTIONIMPORT,
    METADATA("$metadata"),
    BATCH("$batch"),
    LINKS("$links"),
    COUNT("$count"),
    SERVICEROOT;

    private String value;

    private SegmentType() {
        this.value = StringUtils.EMPTY;
    }

    private SegmentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
