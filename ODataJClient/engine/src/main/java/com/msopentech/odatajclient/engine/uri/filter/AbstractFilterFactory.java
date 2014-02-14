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

public abstract class AbstractFilterFactory implements FilterFactory {

    private static final long serialVersionUID = -6141317149802621836L;

    protected static final String NULL = "null";

    @Override
    public ODataFilter match(final ODataFilterArg arg) {
        return new MatchFilter(arg);
    }

    @Override
    public ODataFilter eq(final String key, final Object value) {
        return new EqFilter(ODataFilterArgFactory.property(key), ODataFilterArgFactory.literal(value));
    }

    @Override
    public ODataFilter eq(final ODataFilterArg left, final ODataFilterArg right) {
        return new EqFilter(left, right);
    }

    @Override
    public ODataFilter ne(final String key, final Object value) {
        return new NeFilter(ODataFilterArgFactory.property(key), ODataFilterArgFactory.literal(value));
    }

    @Override
    public ODataFilter ne(final ODataFilterArg left, final ODataFilterArg right) {
        return new NeFilter(left, right);
    }

    @Override
    public ODataFilter gt(final String key, final Object value) {
        return new GtFilter(ODataFilterArgFactory.property(key), ODataFilterArgFactory.literal(value));
    }

    @Override
    public ODataFilter gt(final ODataFilterArg left, final ODataFilterArg right) {
        return new GtFilter(left, right);
    }

    @Override
    public ODataFilter ge(final String key, final Object value) {
        return new GeFilter(ODataFilterArgFactory.property(key), ODataFilterArgFactory.literal(value));
    }

    @Override
    public ODataFilter ge(final ODataFilterArg left, final ODataFilterArg right) {
        return new GeFilter(left, right);
    }

    @Override
    public ODataFilter lt(final String key, final Object value) {
        return new LtFilter(ODataFilterArgFactory.property(key), ODataFilterArgFactory.literal(value));
    }

    @Override
    public ODataFilter lt(final ODataFilterArg left, final ODataFilterArg right) {
        return new LtFilter(left, right);
    }

    @Override
    public ODataFilter le(final String key, final Object value) {
        return new LeFilter(ODataFilterArgFactory.property(key), ODataFilterArgFactory.literal(value));
    }

    @Override
    public ODataFilter le(final ODataFilterArg left, final ODataFilterArg right) {
        return new LeFilter(left, right);
    }

    @Override
    public ODataFilter and(final ODataFilter left, final ODataFilter right) {
        return new AndFilter(left, right);
    }

    @Override
    public ODataFilter or(final ODataFilter left, final ODataFilter right) {
        return new OrFilter(left, right);
    }

    @Override
    public ODataFilter not(final ODataFilter filter) {
        return new NotFilter(filter);
    }
}
