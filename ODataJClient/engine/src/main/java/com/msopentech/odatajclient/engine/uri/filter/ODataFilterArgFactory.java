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
 * OData filter arguments factory.
 */
public final class ODataFilterArgFactory {

    private ODataFilterArgFactory() {
        // Empty private constructor for static utility classes
    }

    public static ODataFilterArg property(final String propertyPath) {
        return new ODataFilterProperty(propertyPath);
    }

    public static ODataFilterArg literal(final Object value) {
        return new ODataFilterLiteral(value);
    }

    public static ODataFilterArg add(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterOp("add", first, second);
    }

    public static ODataFilterArg sub(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterOp("add", first, second);
    }

    public static ODataFilterArg mul(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterOp("mul", first, second);
    }

    public static ODataFilterArg div(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterOp("div", first, second);
    }

    public static ODataFilterArg mod(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterOp("mod", first, second);
    }

    public static ODataFilterArg substringof(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterFunction("substringof", first, second);
    }

    public static ODataFilterArg endswith(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterFunction("endswith", first, second);
    }

    public static ODataFilterArg startswith(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterFunction("startswith", first, second);
    }

    public static ODataFilterArg length(final ODataFilterArg param) {
        return new ODataFilterFunction("length", param);
    }

    public static ODataFilterArg indexof(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterFunction("indexof", first, second);
    }

    public static ODataFilterArg replace(
            final ODataFilterArg first, final ODataFilterArg second, final ODataFilterArg third) {

        return new ODataFilterFunction("replace", first, second, third);
    }

    public static ODataFilterArg substring(final ODataFilterArg arg, final ODataFilterArg pos) {
        return new ODataFilterFunction("substring", arg, pos);
    }

    public static ODataFilterArg substring(
            final ODataFilterArg arg, final ODataFilterArg pos, final ODataFilterArg length) {

        return new ODataFilterFunction("substring", arg, pos, length);
    }

    public static ODataFilterArg tolower(final ODataFilterArg param) {
        return new ODataFilterFunction("tolower", param);
    }

    public static ODataFilterArg toupper(final ODataFilterArg param) {
        return new ODataFilterFunction("toupper", param);
    }

    public static ODataFilterArg trim(final ODataFilterArg param) {
        return new ODataFilterFunction("trim", param);
    }

    public static ODataFilterArg concat(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterFunction("concat", first, second);
    }

    public static ODataFilterArg day(final ODataFilterArg param) {
        return new ODataFilterFunction("day", param);
    }

    public static ODataFilterArg hour(final ODataFilterArg param) {
        return new ODataFilterFunction("hour", param);
    }

    public static ODataFilterArg minute(final ODataFilterArg param) {
        return new ODataFilterFunction("minute", param);
    }

    public static ODataFilterArg month(final ODataFilterArg param) {
        return new ODataFilterFunction("month", param);
    }

    public static ODataFilterArg second(final ODataFilterArg param) {
        return new ODataFilterFunction("second", param);
    }

    public static ODataFilterArg year(final ODataFilterArg param) {
        return new ODataFilterFunction("year", param);
    }

    public static ODataFilterArg round(final ODataFilterArg param) {
        return new ODataFilterFunction("round", param);
    }

    public static ODataFilterArg floor(final ODataFilterArg param) {
        return new ODataFilterFunction("floor", param);
    }

    public static ODataFilterArg ceiling(final ODataFilterArg param) {
        return new ODataFilterFunction("ceiling", param);
    }

    public static ODataFilterArg isof(final ODataFilterArg param) {
        return new ODataFilterFunction("isof", param);
    }

    public static ODataFilterArg isof(final ODataFilterArg first, final ODataFilterArg second) {
        return new ODataFilterFunction("isof", first, second);
    }
}
