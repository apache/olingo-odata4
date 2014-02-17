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
package com.msopentech.odatajclient.engine.uri.filter;

import java.io.Serializable;

/**
 * OData filter factory.
 */
public interface FilterFactory extends Serializable {

    ODataFilter match(ODataFilterArg arg);

    ODataFilter eq(String key, Object value);

    ODataFilter eq(ODataFilterArg left, ODataFilterArg right);

    ODataFilter ne(String key, Object value);

    ODataFilter ne(ODataFilterArg left, ODataFilterArg right);

    ODataFilter gt(String key, Object value);

    ODataFilter gt(ODataFilterArg left, ODataFilterArg right);

    ODataFilter ge(String key, Object value);

    ODataFilter ge(ODataFilterArg left, ODataFilterArg right);

    ODataFilter lt(String key, Object value);

    ODataFilter lt(ODataFilterArg left, ODataFilterArg right);

    ODataFilter le(String key, Object value);

    ODataFilter le(ODataFilterArg left, ODataFilterArg right);

    ODataFilter and(ODataFilter left, ODataFilter right);

    ODataFilter or(ODataFilter left, ODataFilter right);

    ODataFilter not(ODataFilter filter);
}
