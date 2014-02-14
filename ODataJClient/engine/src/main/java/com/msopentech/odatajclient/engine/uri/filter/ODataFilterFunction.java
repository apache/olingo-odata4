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

import org.apache.commons.lang3.StringUtils;

public class ODataFilterFunction implements ODataFilterArg {

    private final String function;

    private final ODataFilterArg[] params;

    ODataFilterFunction(final String function, final ODataFilterArg... params) {
        this.function = function;
        this.params = params;
    }

    @Override
    public String build() {
        final String[] strParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            strParams[i] = params[i].build();
        }

        return new StringBuilder(function).
                append('(').
                append(StringUtils.join(strParams, ',')).
                append(')').
                toString();
    }
}
