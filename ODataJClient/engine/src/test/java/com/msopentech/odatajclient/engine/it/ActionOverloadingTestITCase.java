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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.engine.communication.response.ODataInvokeResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataNoContent;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;

public class ActionOverloadingTestITCase extends AbstractTestITCase {

    @Test
    public void retrieveProducts() {
        final EdmV3Metadata metadata = client.getRetrieveRequestFactory().
                getMetadataRequest(testActionOverloadingServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        assertNotNull(container);

        int execs = 0;
        for (FunctionImport funcImp : container.getFunctionImports("RetrieveProduct")) {
            final ODataInvokeResponse<ODataProperty> res;
            if (funcImp.getParameters().isEmpty()) {
                final URIBuilder funcImpBuilder = client.getURIBuilder(testActionOverloadingServiceRootURL).
                        appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

                res = client.getInvokeRequestFactory().<ODataProperty>getInvokeRequest(
                        funcImpBuilder.build(), metadata, funcImp).execute();
            } else if ("Microsoft.Test.OData.Services.AstoriaDefaultService.Product".
                    equals(funcImp.getParameters().get(0).getType())) {

                final ODataEntity product = client.getRetrieveRequestFactory().getEntityRequest(
                        client.getURIBuilder(testActionOverloadingServiceRootURL).
                        appendEntityTypeSegment("Product").appendKeySegment(-10).build()).
                        execute().getBody();
                assertNotNull(product);

                res = client.getInvokeRequestFactory().<ODataProperty>getInvokeRequest(
                        product.getOperation("RetrieveProduct").getTarget(), metadata, funcImp).execute();
            } else if ("Microsoft.Test.OData.Services.AstoriaDefaultService.OrderLine".
                    equals(funcImp.getParameters().get(0).getType())) {

                final Map<String, Object> key = new LinkedHashMap<String, Object>(2);
                key.put("OrderId", -10);
                key.put("ProductId", -10);
                final ODataEntity orderLine = client.getRetrieveRequestFactory().getEntityRequest(
                        client.getURIBuilder(testActionOverloadingServiceRootURL).
                        appendEntityTypeSegment("OrderLine").appendKeySegment(key).build()).
                        execute().getBody();
                assertNotNull(orderLine);

                res = client.getInvokeRequestFactory().<ODataProperty>getInvokeRequest(
                        orderLine.getOperation("RetrieveProduct").getTarget(), metadata, funcImp).execute();
            } else {
                res = null;
            }

            assertNotNull(res);
            assertEquals(200, res.getStatusCode());
            assertEquals(Integer.valueOf(-10), res.getBody().getPrimitiveValue().<Integer>toCastValue());
            execs++;
        }
        assertEquals(3, execs);
    }

    @Test
    public void increaseSalaries() {
        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testActionOverloadingServiceRootURL).execute().
                getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        assertNotNull(container);

        int execs = 0;
        for (FunctionImport funcImp : container.getFunctionImports("IncreaseSalaries")) {
            final Map<String, ODataValue> parameters = new LinkedHashMap<String, ODataValue>(1);
            parameters.put("n",
                    client.getPrimitiveValueBuilder().setType(EdmSimpleType.Int32).setValue(5).build());

            final ODataInvokeResponse<ODataNoContent> res;
            if ("Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Employee)".
                    equals(funcImp.getParameters().get(0).getType())) {

                final URIBuilder builder = client.getURIBuilder(testActionOverloadingServiceRootURL).
                        appendEntitySetSegment("Person").
                        appendStructuralSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");

                final ODataEntitySet employees = client.getRetrieveRequestFactory().getEntitySetRequest(
                        builder.build()).execute().getBody();
                assertNotNull(employees);

                res = client.getInvokeRequestFactory().<ODataNoContent>getInvokeRequest(
                        builder.appendFunctionImportSegment(funcImp.getName()).build(), metadata, funcImp, parameters).
                        execute();
            } else if ("Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee)".
                    equals(funcImp.getParameters().get(0).getType())) {

                final URIBuilder builder = client.getURIBuilder(testActionOverloadingServiceRootURL).
                        appendEntitySetSegment("Person").
                        appendStructuralSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee");

                final ODataEntitySet specialEmployees = client.getRetrieveRequestFactory().getEntitySetRequest(
                        builder.build()).execute().getBody();
                assertNotNull(specialEmployees);

                res = client.getInvokeRequestFactory().<ODataNoContent>getInvokeRequest(
                        builder.appendFunctionImportSegment(funcImp.getName()).build(), metadata, funcImp, parameters).
                        execute();
            } else {
                res = null;
            }

            assertNotNull(res);
            assertEquals(204, res.getStatusCode());
            execs++;
        }
        assertEquals(2, execs);
    }
}
