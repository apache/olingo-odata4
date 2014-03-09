/*
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
package org.apache.olingo.odata4.client.core.op.impl;

import org.apache.olingo.odata4.client.core.ODataV3ClientImpl;
import org.apache.olingo.odata4.client.core.op.impl.AbstractODataBinder;

public class ODataV3BinderImpl extends AbstractODataBinder {

    private static final long serialVersionUID = 8970843539708952308L;

    public ODataV3BinderImpl(final ODataV3ClientImpl client) {
        super(client);
    }

//    @Override
//    protected EdmType newEdmType(final String expression) {
//        return new EdmV3Type(expression);
//    }

}
