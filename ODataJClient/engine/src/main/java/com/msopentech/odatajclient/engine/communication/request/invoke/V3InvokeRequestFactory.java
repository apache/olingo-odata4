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
package com.msopentech.odatajclient.engine.communication.request.invoke;

import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataInvokeResult;
import com.msopentech.odatajclient.engine.data.ODataNoContent;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.EdmV3Type;
import com.msopentech.odatajclient.engine.metadata.edm.v3.ComplexType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.DataServices;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Edmx;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.metadata.edm.v3.Schema;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;

public class V3InvokeRequestFactory extends AbstractInvokeRequestFactory<
        EdmV3Metadata, Edmx, DataServices, Schema, EntityContainer, EntityType, ComplexType, FunctionImport> {

    private static final long serialVersionUID = -659256862901915496L;

    public V3InvokeRequestFactory(final ODataV3Client client) {
        super(client);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RES extends ODataInvokeResult> ODataInvokeRequest<RES> getInvokeRequest(
            final URI uri, final EdmV3Metadata metadata, final FunctionImport functionImport) {

        HttpMethod method = null;
        if (HttpMethod.GET.name().equals(functionImport.getHttpMethod())) {
            method = HttpMethod.GET;
        } else if (HttpMethod.POST.name().equals(functionImport.getHttpMethod())) {
            method = HttpMethod.POST;
        } else if (functionImport.getHttpMethod() == null) {
            if (functionImport.isSideEffecting()) {
                method = HttpMethod.POST;
            } else {
                method = HttpMethod.GET;
            }
        }

        ODataInvokeRequest<RES> result;
        if (StringUtils.isBlank(functionImport.getReturnType())) {
            result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataNoContent>(
                    client, ODataNoContent.class, method, uri);
        } else {
            final EdmV3Type returnType = new EdmV3Type(metadata, functionImport.getReturnType());

            if (returnType.isCollection() && returnType.isEntityType()) {
                result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataEntitySet>(
                        client, ODataEntitySet.class, method, uri);
            } else if (!returnType.isCollection() && returnType.isEntityType()) {
                result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataEntity>(
                        client, ODataEntity.class, method, uri);
            } else {
                result = (ODataInvokeRequest<RES>) new ODataInvokeRequest<ODataProperty>(
                        client, ODataProperty.class, method, uri);
            }
        }

        return result;
    }
}
