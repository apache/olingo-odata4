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
package org.apache.olingo.client.core.it.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.junit.Test;

public class ActionOverloadingTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveProduct() throws EdmPrimitiveTypeException {
    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(testActionOverloadingServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    int execs = 0;
    for (EdmActionImport actImp : container.getActionImports()) {
      if ("RetrieveProduct".equals(actImp.getName())) {
        // 1. unbound
        final EdmAction unbound = actImp.getUnboundAction();
        assertNotNull(unbound);
        assertEquals(EdmInt32.getInstance(), unbound.getReturnType().getType());

        final URIBuilder unboundBuilder = getClient().getURIBuilder(testActionOverloadingServiceRootURL).
                appendOperationCallSegment(URIUtils.operationImportURISegment(container, actImp.getName()));
        final ODataInvokeResponse<ODataProperty> unboundRes = getClient().getInvokeRequestFactory().
                <ODataProperty>getInvokeRequest(unboundBuilder.build(), unbound).execute();
        assertNotNull(unboundRes);
        assertEquals(200, unboundRes.getStatusCode());
        assertEquals(Integer.valueOf(-10), unboundRes.getBody().getPrimitiveValue().toCastValue(Integer.class));
        execs++;

        // 2. bound to Product
        final EdmAction productBound = edm.getBoundAction(
                new FullQualifiedName(container.getNamespace(), actImp.getName()),
                new FullQualifiedName(container.getNamespace(), "Product"), false);
        assertNotNull(productBound);
        assertEquals(EdmInt32.getInstance(), productBound.getReturnType().getType());

        final ODataEntity product = getClient().getRetrieveRequestFactory().getEntityRequest(
                getClient().getURIBuilder(testActionOverloadingServiceRootURL).
                appendEntitySetSegment("Product").appendKeySegment(-10).build()).
                execute().getBody();
        assertNotNull(product);

        final ODataInvokeResponse<ODataProperty> productBoundRes = getClient().getInvokeRequestFactory().
                <ODataProperty>getInvokeRequest(product.getOperation(actImp.getName()).getTarget(), unbound).
                execute();
        assertNotNull(productBoundRes);
        assertEquals(200, productBoundRes.getStatusCode());
        assertEquals(Integer.valueOf(-10), productBoundRes.getBody().getPrimitiveValue().toCastValue(Integer.class));
        execs++;

        // 3. bound to OrderLine
        final EdmAction orderLineBound = edm.getBoundAction(
                new FullQualifiedName(container.getNamespace(), actImp.getName()),
                new FullQualifiedName(container.getNamespace(), "OrderLine"), false);
        assertNotNull(orderLineBound);
        assertEquals(EdmInt32.getInstance(), orderLineBound.getReturnType().getType());

        final Map<String, Object> key = new LinkedHashMap<String, Object>(2);
        key.put("OrderId", -10);
        key.put("ProductId", -10);
        final ODataEntity orderLine = getClient().getRetrieveRequestFactory().getEntityRequest(
                getClient().getURIBuilder(testActionOverloadingServiceRootURL).
                appendEntitySetSegment("OrderLine").appendKeySegment(key).build()).
                execute().getBody();
        assertNotNull(orderLine);

        final ODataInvokeResponse<ODataProperty> orderLineBoundRes = getClient().getInvokeRequestFactory().
                <ODataProperty>getInvokeRequest(orderLine.getOperation(actImp.getName()).getTarget(), unbound).
                execute();
        assertNotNull(orderLineBoundRes);
        assertEquals(200, orderLineBoundRes.getStatusCode());
        assertEquals(Integer.valueOf(-10), orderLineBoundRes.getBody().getPrimitiveValue().toCastValue(Integer.class));
        execs++;
      }
    }
    assertEquals(3, execs);
  }

  @Test
  public void increaseSalaries() {
    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(testActionOverloadingServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    int execs = 0;
    for (EdmActionImport actImp : container.getActionImports()) {
      if ("IncreaseSalaries".equals(actImp.getName())) {
        final Map<String, ODataValue> parameters = new LinkedHashMap<String, ODataValue>(1);
        parameters.put("n", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(5));

        // 1. bound to employees
        final EdmAction employeeBound = edm.getBoundAction(
                new FullQualifiedName(container.getNamespace(), actImp.getName()),
                new FullQualifiedName(container.getNamespace(), "Employee"), true);
        assertNotNull(employeeBound);
        assertNull(employeeBound.getReturnType());

        final URIBuilder employeeBuilder = getClient().getURIBuilder(testActionOverloadingServiceRootURL).
                appendEntitySetSegment("Person").
                appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
        final ODataEntitySet employees = getClient().getRetrieveRequestFactory().getEntitySetRequest(
                employeeBuilder.build()).execute().getBody();
        assertNotNull(employees);

        final ODataInvokeResponse<ODataNoContent> employeeRes = getClient().getInvokeRequestFactory().
                <ODataNoContent>getInvokeRequest(employeeBuilder.appendOperationCallSegment(actImp.getName()).build(),
                        employeeBound, parameters).execute();
        assertNotNull(employeeRes);
        assertEquals(204, employeeRes.getStatusCode());
        execs++;

        // 1. bound to special employees
        final EdmAction specEmpBound = edm.getBoundAction(
                new FullQualifiedName(container.getNamespace(), actImp.getName()),
                new FullQualifiedName(container.getNamespace(), "SpecialEmployee"), true);
        assertNotNull(specEmpBound);
        assertNull(specEmpBound.getReturnType());

        final URIBuilder specEmpBuilder = getClient().getURIBuilder(testActionOverloadingServiceRootURL).
                appendEntitySetSegment("Person").
                appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee");
        final ODataEntitySet specEmps = getClient().getRetrieveRequestFactory().getEntitySetRequest(
                specEmpBuilder.build()).execute().getBody();
        assertNotNull(specEmps);

        final ODataInvokeResponse<ODataNoContent> specEmpsRes = getClient().getInvokeRequestFactory().
                <ODataNoContent>getInvokeRequest(specEmpBuilder.appendOperationCallSegment(actImp.getName()).build(),
                        specEmpBound, parameters).execute();
        assertNotNull(specEmpsRes);
        assertEquals(204, specEmpsRes.getStatusCode());
        execs++;
      }
    }
    assertEquals(2, execs);
  }
}
