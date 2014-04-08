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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.junit.Test;

public class InvokeTestITCase extends AbstractTestITCase {

  private void getWithNoParams(final ODataPubFormat format) {
    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(testStaticServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();

    // 1. get primitive value property
    EdmFunctionImport funcImp = container.getFunctionImport("GetPrimitiveString");
    EdmFunction func = funcImp.getUnboundFunction(null);

    URIBuilder builder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment(URIUtils.operationImportURISegment(container, funcImp.getName()));

    ODataInvokeRequest<ODataProperty> req = getClient().getInvokeRequestFactory().
            getInvokeRequest(builder.build(), func);
    req.setFormat(format);
    ODataInvokeResponse<ODataProperty> res = req.execute();
    assertNotNull(res);

    ODataProperty property = res.getBody();
    assertNotNull(property);
    assertEquals("Foo", property.getPrimitiveValue().toString());

    // 2. get collection of complex type property
    funcImp = container.getFunctionImport("EntityProjectionReturnsCollectionOfComplexTypes");
    func = funcImp.getUnboundFunction(null);

    builder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment(URIUtils.operationImportURISegment(container, funcImp.getName()));

    req = getClient().getInvokeRequestFactory().getInvokeRequest(builder.build(), func);
    req.setFormat(format);
    res = req.execute();
    assertNotNull(res);

    property = res.getBody();
    assertNotNull(property);
    assertTrue(property.hasCollectionValue());
    assertFalse(property.getCollectionValue().isEmpty());
  }

  @Test
  public void getWithNoParamsAsAtom() {
    getWithNoParams(ODataPubFormat.ATOM);
  }

  @Test
  public void getWithNoParamsAsJSON() {
    getWithNoParams(ODataPubFormat.JSON);
  }

  private void getWithParams(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    // 1. primitive result
    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(testStaticServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    EdmFunctionImport funcImp = container.getFunctionImport("GetArgumentPlusOne");

    URIBuilder builder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment(URIUtils.operationImportURISegment(container, funcImp.getName()));

    EdmFunction function = funcImp.getUnboundFunction(Collections.singletonList("arg1"));
    EdmParameter param = function.getParameter(function.getParameterNames().get(0));
    ODataPrimitiveValue paramValue = getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(param.getType()).
            setValue(154).
            build();

    final ODataInvokeRequest<ODataProperty> primitiveReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(builder.build(), function,
                    Collections.<String, ODataValue>singletonMap(param.getName(), paramValue));
    primitiveReq.setFormat(format);

    final ODataInvokeResponse<ODataProperty> primitiveRes = primitiveReq.execute();
    assertNotNull(primitiveRes);

    final ODataProperty property = primitiveRes.getBody();
    assertNotNull(property);
    assertEquals(Integer.valueOf(155), property.getPrimitiveValue().toCastValue(Integer.class));

    // 2. feed result
    funcImp = container.getFunctionImport("GetSpecificCustomer");

    builder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment(URIUtils.operationImportURISegment(container, funcImp.getName()));

    function = funcImp.getUnboundFunction(Collections.singletonList("Name"));
    param = function.getParameter(function.getParameterNames().get(0));
    paramValue = getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(param.getType()).
            setValue(StringUtils.EMPTY).
            build();

    final ODataInvokeRequest<ODataEntitySet> feedReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(builder.build(), function,
                    Collections.<String, ODataValue>singletonMap(param.getName(), paramValue));
    feedReq.setFormat(format);

    final ODataInvokeResponse<ODataEntitySet> feedRes = feedReq.execute();
    assertNotNull(feedRes);

    final ODataEntitySet feed = feedRes.getBody();
    assertNotNull(feed);

    final Set<Integer> customerIds = new HashSet<Integer>(feed.getEntities().size());
    for (ODataEntity entity : feed.getEntities()) {
      customerIds.add(entity.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class));
    }
    assertTrue(customerIds.contains(-8));
  }

  @Test
  public void getWithParamsAsAtom() throws EdmPrimitiveTypeException {
    getWithParams(ODataPubFormat.ATOM);
  }

  @Test
  public void getWithParamsAsJSON() throws EdmPrimitiveTypeException {
    getWithParams(ODataPubFormat.JSON);
  }

  private ODataEntity createEmployee(final ODataPubFormat format) {
    final ODataEntity employee = getClient().getObjectFactory().newEntity(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");

    employee.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("PersonId",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setText("1244").setType(EdmPrimitiveTypeKind.Int32).build()));
    employee.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty(
            "Name", getClient().getObjectFactory().newPrimitiveValueBuilder().
            setText("Test employee").build()));
    employee.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty(
            "ManagersPersonId", getClient().getObjectFactory().newPrimitiveValueBuilder().
            setText("3777").setType(EdmPrimitiveTypeKind.Int32).build()));
    employee.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty(
            "Salary", getClient().getObjectFactory().newPrimitiveValueBuilder().
            setText("1000").setType(EdmPrimitiveTypeKind.Int32).build()));
    employee.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty(
            "Title", getClient().getObjectFactory().newPrimitiveValueBuilder().
            setText("CEO").build()));

    final URIBuilder uriBuilder = getClient().getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Person");

    final ODataEntityCreateRequest<ODataEntity> createReq =
            getClient().getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), employee);
    createReq.setFormat(format);
    final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());

    final ODataEntityRequest<ODataEntity> req =
            getClient().getRetrieveRequestFactory().getEntityRequest(uriBuilder.appendKeySegment(1244).build());
    return req.execute().getBody();
  }

  private void deleteEmployee(final ODataPubFormat format, final Integer id) {
    final URIBuilder uriBuilder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Person").appendKeySegment(id);

    final ODataDeleteRequest deleteReq = getClient().getCUDRequestFactory().getDeleteRequest(uriBuilder.build());
    deleteReq.setFormat(format);
    final ODataDeleteResponse deleteRes = deleteReq.execute();
    assertEquals(204, deleteRes.getStatusCode());
  }

  @Test
  public void boundPost() throws EdmPrimitiveTypeException {
    // 0. create an employee
    final ODataEntity created = createEmployee(ODataPubFormat.JSON_FULL_METADATA);
    assertNotNull(created);
    final Integer createdId = created.getProperty("PersonId").getPrimitiveValue().toCastValue(Integer.class);
    assertNotNull(createdId);

    // 1. invoke action bound with the employee just created
    final ODataOperation operation = created.getOperations().get(0);

    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(testStaticServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    final EdmTypeInfo createdTypeInfo =
            new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(created.getName()).build();
    final EdmAction action = edm.getBoundAction(
            new FullQualifiedName(container.getNamespace(), operation.getTitle()),
            createdTypeInfo.getFullQualifiedName(), createdTypeInfo.isCollection());

    final ODataInvokeRequest<ODataNoContent> req = getClient().getInvokeRequestFactory().
            getInvokeRequest(operation.getTarget(), action);
    req.setFormat(ODataPubFormat.JSON_FULL_METADATA);
    final ODataInvokeResponse<ODataNoContent> res = req.execute();
    assertNotNull(res);
    assertEquals(204, res.getStatusCode());

    // 2. check that invoked action has effectively run
    final URIBuilder uriBuilder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Person").appendKeySegment(createdId);
    final ODataEntityRequest<ODataEntity> retrieveRes =
            getClient().getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    retrieveRes.setFormat(ODataPubFormat.JSON_FULL_METADATA);
    final ODataEntity read = retrieveRes.execute().getBody();
    assertEquals("0", read.getProperty("Salary").getPrimitiveValue().toString());
    assertTrue(read.getProperty("Title").getPrimitiveValue().toString().endsWith("[Sacked]"));

    // 3. remove the test employee
    deleteEmployee(ODataPubFormat.JSON_FULL_METADATA, createdId);
  }

  @Test
  public void boundPostWithParams() throws EdmPrimitiveTypeException {
    // 1. read employees and store their current salary
    final URIBuilder builder = getClient().getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Person").
            appendEntitySetSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
    final URI employeesURI = builder.build();
    ODataEntitySet employees = getClient().getRetrieveRequestFactory().
            getEntitySetRequest(employeesURI).execute().getBody();
    assertFalse(employees.getEntities().isEmpty());
    final Map<Integer, Integer> preSalaries = new HashMap<Integer, Integer>(employees.getCount());
    for (ODataEntity employee : employees.getEntities()) {
      preSalaries.put(employee.getProperty("PersonId").getPrimitiveValue().toCastValue(Integer.class),
              employee.getProperty("Salary").getPrimitiveValue().toCastValue(Integer.class));
    }
    assertFalse(preSalaries.isEmpty());

    // 2. invoke action bound, with additional parameter
    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(testStaticServiceRootURL).execute().getBody();
    assertNotNull(edm);

    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();

    final EdmTypeInfo employeesTypeInfo = new EdmTypeInfo.Builder().setEdm(edm).
            setTypeExpression("Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Employee)").build();
    final EdmAction action = edm.getBoundAction(
            new FullQualifiedName(container.getNamespace(), "IncreaseSalaries"),
            employeesTypeInfo.getFullQualifiedName(), employeesTypeInfo.isCollection());

    final EdmParameter param = action.getParameter(action.getParameterNames().get(1));
    final ODataPrimitiveValue paramValue = getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(param.getType()).
            setValue(1).
            build();

    final ODataInvokeRequest<ODataNoContent> req = getClient().getInvokeRequestFactory().getInvokeRequest(
            builder.appendOperationCallSegment(action.getName()).build(), action,
            Collections.<String, ODataValue>singletonMap(param.getName(), paramValue));
    final ODataInvokeResponse<ODataNoContent> res = req.execute();
    assertNotNull(res);
    assertEquals(204, res.getStatusCode());

    // 3. check whether salaries were incremented
    employees = getClient().getRetrieveRequestFactory().getEntitySetRequest(employeesURI).execute().getBody();
    assertFalse(employees.getEntities().isEmpty());
    for (ODataEntity employee : employees.getEntities()) {
      assertTrue(
              preSalaries.get(employee.getProperty("PersonId").getPrimitiveValue().toCastValue(Integer.class))
              < employee.getProperty("Salary").getPrimitiveValue().toCastValue(Integer.class));
    }
  }
}
