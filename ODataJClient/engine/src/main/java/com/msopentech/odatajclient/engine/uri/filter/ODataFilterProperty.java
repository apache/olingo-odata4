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
package com.msopentech.odatajclient.engine.uri.filter;

/**
 * Filter property path; obtain instances via <tt>ODataFilterArgFactory</tt>.
 *
 * @see com.msopentech.odatajclient.engine.uri.filter.ODataFilterArgFactory
 */
public class ODataFilterProperty implements ODataFilterArg {

    private final String propertyPath;

    ODataFilterProperty(final String value) {
        this.propertyPath = value;
    }

    @Override
    public String build() {
        return propertyPath;
    }
}
