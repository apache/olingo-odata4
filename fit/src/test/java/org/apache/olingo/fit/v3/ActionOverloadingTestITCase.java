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
package org.apache.olingo.fit.v3;

import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ActionOverloadingTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveProduct() throws EdmPrimitiveTypeException {
    final String actionImportName = "RetrieveProduct";

    // 1. unbound
    final URIBuilder builder = getClient().newURIBuilder(testActionOverloadingServiceRootURL).
        appendOperationCallSegment(actionImportName);
    final ODataInvokeResponse<ODataProperty> unboundRes = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(builder.build(), ODataProperty.class).execute();
    assertNotNull(unboundRes);
    assertEquals(200, unboundRes.getStatusCode());
    assertEquals(Integer.valueOf(-10), unboundRes.getBody().getPrimitiveValue().toCastValue(Integer.class));

    // 2. bound to Product
    final ODataEntity product = getClient().getRetrieveRequestFactory().getEntityRequest(
        getClient().newURIBuilder(testActionOverloadingServiceRootURL).
            appendEntitySetSegment("Product").appendKeySegment(-10).build()).
        execute().getBody();
    assertNotNull(product);

    final ODataInvokeResponse<ODataProperty> productBoundRes = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(product.getOperation(actionImportName).getTarget(), ODataProperty.class).
        execute();
    assertNotNull(productBoundRes);
    assertEquals(200, productBoundRes.getStatusCode());
    assertEquals(Integer.valueOf(-10), productBoundRes.getBody().getPrimitiveValue().toCastValue(Integer.class));

    // 3. bound to OrderLine
    final Map<String, Object> key = new LinkedHashMap<String, Object>(2);
    key.put("OrderId", -10);
    key.put("ProductId", -10);
    final ODataEntity orderLine = getClient().getRetrieveRequestFactory().getEntityRequest(
        getClient().newURIBuilder(testActionOverloadingServiceRootURL).
            appendEntitySetSegment("OrderLine").appendKeySegment(key).build()).
        execute().getBody();
    assertNotNull(orderLine);

    final ODataInvokeResponse<ODataProperty> orderLineBoundRes = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(orderLine.getOperation(actionImportName).getTarget(), ODataProperty.class).
        execute();
    assertNotNull(orderLineBoundRes);
    assertEquals(200, orderLineBoundRes.getStatusCode());
    assertEquals(Integer.valueOf(-10), orderLineBoundRes.getBody().getPrimitiveValue().toCastValue(Integer.class));
  }

  @Test
  public void increaseSalaries() {
    final String actionImportName = "IncreaseSalaries";

    final Map<String, ODataValue> parameters = new LinkedHashMap<String, ODataValue>(1);
    parameters.put("n", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(5));

    // 1. bound to employees
    final URIBuilder employeeBuilder = getClient().newURIBuilder(testActionOverloadingServiceRootURL).
        appendEntitySetSegment("Person").
        appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
    final ODataEntitySet employees = getClient().getRetrieveRequestFactory().getEntitySetRequest(
        employeeBuilder.build()).execute().getBody();
    assertNotNull(employees);

    final ODataInvokeResponse<ODataNoContent> employeeRes = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(employeeBuilder.appendOperationCallSegment(actionImportName).build(),
            ODataNoContent.class, parameters).execute();
    assertNotNull(employeeRes);
    assertEquals(204, employeeRes.getStatusCode());

    // 2. bound to special employees
    final URIBuilder specEmpBuilder = getClient().newURIBuilder(testActionOverloadingServiceRootURL).
        appendEntitySetSegment("Person").
        appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee");
    final ODataEntitySet specEmps = getClient().getRetrieveRequestFactory().getEntitySetRequest(
        specEmpBuilder.build()).execute().getBody();
    assertNotNull(specEmps);

    final ODataInvokeResponse<ODataNoContent> specEmpsRes = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(specEmpBuilder.appendOperationCallSegment(actionImportName).build(),
            ODataNoContent.class, parameters).execute();
    assertNotNull(specEmpsRes);
    assertEquals(204, specEmpsRes.getStatusCode());
  }
}
