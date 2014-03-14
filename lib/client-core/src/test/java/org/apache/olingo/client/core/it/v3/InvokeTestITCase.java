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

public class InvokeTestITCase extends AbstractV3TestITCase {

//    private void getWithNoParams(final ODataPubFormat format) {
//        final Edm metadata =
//                client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
//        assertNotNull(metadata);
//
//        final EdmEdmEntityContainer container = metadata.getSchemas().get(0).getEntityContainer();
//
//        // 1. get primitive value property
//        EdmFunctionImport funcImp = container.getFunctionImport("GetPrimitiveString");
//
//        URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEdmFunctionImportSegment(URIUtils.rootEdmFunctionImportURISegment(container, funcImp));
//
//        ODataInvokeRequest<ODataProperty> req =
//                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
//        req.setFormat(format);
//        ODataInvokeResponse<ODataProperty> res = req.execute();
//        assertNotNull(res);
//
//        ODataProperty property = res.getBody();
//        assertNotNull(property);
//        assertEquals("Foo", property.getPrimitiveValue().<String>toCastValue());
//
//        // 2. get collection of complex type property
//        funcImp = container.getFunctionImport("EntityProjectionReturnsCollectionOfComplexTypes");
//
//        builder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEdmFunctionImportSegment(URIUtils.rootEdmFunctionImportURISegment(container, funcImp));
//
//        req = client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
//        req.setFormat(format);
//        res = req.execute();
//        assertNotNull(res);
//
//        property = res.getBody();
//        assertNotNull(property);
//        assertTrue(property.hasCollectionValue());
//        assertFalse(property.getCollectionValue().isEmpty());
//    }
//
//    @Test
//    public void getWithNoParamsAsAtom() {
//        getWithNoParams(ODataPubFormat.ATOM);
//    }
//
//    @Test
//    public void getWithNoParamsAsJSON() {
//        getWithNoParams(ODataPubFormat.JSON);
//    }
//
//    private void getWithParams(final ODataPubFormat format) {
//        // 1. primitive result
//        Edm metadata =
//                client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
//        assertNotNull(metadata);
//
//        EdmEntityContainer container = metadata.getSchemas().get(0).getEntityContainer();
//        EdmFunctionImport funcImp = container.getFunctionImport("GetArgumentPlusOne");
//
//        URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEdmFunctionImportSegment(URIUtils.rootEdmFunctionImportURISegment(container, funcImp));
//
//        EdmType type = new EdmV3Type(funcImp.getParameters().get(0).getType());
//        ODataPrimitiveValue argument = client.getPrimitiveValueBuilder().
//                setType(type.getSimpleType()).
//                setValue(154).
//                build();
//        Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
//        parameters.put(funcImp.getParameters().get(0).getName(), argument);
//
//        final ODataInvokeRequest<ODataProperty> primitiveReq =
//                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp,
//                        parameters);
//        primitiveReq.setFormat(format);
//
//        final ODataInvokeResponse<ODataProperty> primitiveRes = primitiveReq.execute();
//        assertNotNull(primitiveRes);
//
//        final ODataProperty property = primitiveRes.getBody();
//        assertNotNull(property);
//        assertEquals(Integer.valueOf(155), property.getPrimitiveValue().<Integer>toCastValue());
//
//        // 2. feed result
//        metadata = 
//  client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
//        assertNotNull(metadata);
//
//        container = metadata.getSchemas().get(0).getEntityContainer();
//        funcImp = container.getFunctionImport("GetSpecificCustomer");
//
//        builder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEdmFunctionImportSegment(URIUtils.rootEdmFunctionImportURISegment(container, funcImp));
//
//        type = new EdmV3Type(funcImp.getParameters().get(0).getType());
//        argument = client.getPrimitiveValueBuilder().
//                setType(type.getSimpleType()).
//                setText(StringUtils.EMPTY).
//                build();
//        parameters = new LinkedHashMap<String, ODataValue>();
//        parameters.put(funcImp.getParameters().get(0).getName(), argument);
//
//        final ODataInvokeRequest<ODataEntitySet> feedReq =
//                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp, parameters);
//        feedReq.setFormat(format);
//
//        final ODataInvokeResponse<ODataEntitySet> feedRes = feedReq.execute();
//        assertNotNull(feedRes);
//
//        final ODataEntitySet feed = feedRes.getBody();
//        assertNotNull(feed);
//
//        final Set<Integer> customerIds = new HashSet<Integer>(feed.getEntities().size());
//        for (ODataEntity entity : feed.getEntities()) {
//            customerIds.add(entity.getProperty("CustomerId").getPrimitiveValue().<Integer>toCastValue());
//        }
//        assertTrue(customerIds.contains(-8));
//    }
//
//    @Test
//    public void getWithParamsAsAtom() {
//        getWithParams(ODataPubFormat.ATOM);
//    }
//
//    @Test
//    public void getWithParamsAsJSON() {
//        getWithParams(ODataPubFormat.JSON);
//    }
//
//    private ODataEntity createEmployee(final ODataPubFormat format) {
//        final ODataEntity employee = client.getObjectFactory().newEntity(
//                "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
//
//        employee.getProperties().add(client.getObjectFactory().newPrimitiveProperty("PersonId", client.
//                getPrimitiveValueBuilder().
//                setText("1244").setType(ODataJClientEdmPrimitiveType.Int32).build()));
//        employee.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
//                "Name", client.getPrimitiveValueBuilder().
//                setText("Test employee").build()));
//        employee.getProperties().add(client.getObjectFactory().newPrimitiveProperty("ManagersPersonId", client.
//                getPrimitiveValueBuilder().
//                setText("3777").setType(ODataJClientEdmPrimitiveType.Int32).build()));
//        employee.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
//                "Salary", client.getPrimitiveValueBuilder().
//                setText("1000").setType(ODataJClientEdmPrimitiveType.Int32).build()));
//        employee.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
//                "Title", client.getPrimitiveValueBuilder().
//                setText("CEO").build()));
//
//        final URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEntitySetSegment("Person");
//
//        final ODataEntityCreateRequest createReq =
//                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), employee);
//        createReq.setFormat(format);
//        final ODataEntityCreateResponse createRes = createReq.execute();
//        assertEquals(201, createRes.getStatusCode());
//
//        return createRes.getBody();
//    }
//
//    private void deleteEmployee(final ODataPubFormat format, final Integer id) {
//        final URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEntitySetSegment("Person").appendKeySegment(id);
//
//        final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(uriBuilder.build());
//        deleteReq.setFormat(format);
//        final ODataDeleteResponse deleteRes = deleteReq.execute();
//        assertEquals(204, deleteRes.getStatusCode());
//    }
//
//    @Test
//    public void boundPost() {
//        // 0. create an employee
//        final ODataEntity created = createEmployee(ODataPubFormat.JSON_FULL_METADATA);
//        assertNotNull(created);
//        final Integer createdId = created.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue();
//        assertNotNull(createdId);
//
//        // 1. invoke action bound with the employee just created
//        final ODataOperation action = created.getOperations().get(0);
//
//        final Edm metadata =
//                client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
//        assertNotNull(metadata);
//
//        final EdmEntityContainer container = metadata.getSchemas().get(0).getEntityContainer();
//        final EdmFunctionImport funcImp = container.getFunctionImport(action.getTitle());
//
//        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
//                action.getTarget(), metadata, funcImp);
//        req.setFormat(ODataPubFormat.JSON_FULL_METADATA);
//        final ODataInvokeResponse<ODataNoContent> res = req.execute();
//        assertNotNull(res);
//        assertEquals(204, res.getStatusCode());
//
//        // 2. check that invoked action has effectively run
//        final URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEntitySetSegment("Person").appendKeySegment(createdId);
//        final ODataEntityRequest retrieveRes = 
//  client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
//        retrieveRes.setFormat(ODataPubFormat.JSON_FULL_METADATA);
//        final ODataEntity read = retrieveRes.execute().getBody();
//        assertEquals("0", read.getProperty("Salary").getPrimitiveValue().toString());
//        assertTrue(read.getProperty("Title").getPrimitiveValue().toString().endsWith("[Sacked]"));
//
//        // 3. remove the test employee
//        deleteEmployee(ODataPubFormat.JSON_FULL_METADATA, createdId);
//    }
//
//    @Test
//    public void boundPostWithParams() {
//        // 1. read employees and store their current salary
//        final URIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
//                appendEntitySetSegment("Person").
//                appendEntitySetSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
//        final URI employeesURI = builder.build();
//        ODataEntitySet employees = client.getRetrieveRequestFactory().getEntitySetRequest(employeesURI).execute().
//                getBody();
//        assertFalse(employees.getEntities().isEmpty());
//        final Map<Integer, Integer> preSalaries = new HashMap<Integer, Integer>(employees.getCount());
//        for (ODataEntity employee : employees.getEntities()) {
//            preSalaries.put(employee.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue(),
//                    employee.getProperty("Salary").getPrimitiveValue().<Integer>toCastValue());
//        }
//        assertFalse(preSalaries.isEmpty());
//
//        // 2. invoke action bound, with additional parameter
//        final Edm metadata =
//                client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
//        assertNotNull(metadata);
//
//        final EdmEntityContainer container = metadata.getSchemas().get(0).getEntityContainer();
//        final EdmFunctionImport funcImp = container.getFunctionImport("IncreaseSalaries");
//
//        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
//                builder.appendOperationCallSegment(funcImp.getName()).build(), metadata, funcImp,
//                Collections.<String, ODataValue>singletonMap(
//                        "n", client.getPrimitiveValueBuilder().setValue(1).
//  setType(ODataJClientEdmPrimitiveType.Int32).build()));
//        final ODataInvokeResponse<ODataNoContent> res = req.execute();
//        assertNotNull(res);
//        assertEquals(204, res.getStatusCode());
//
//        // 3. check whether salaries were incremented
//        employees = client.getRetrieveRequestFactory().getEntitySetRequest(employeesURI).execute().getBody();
//        assertFalse(employees.getEntities().isEmpty());
//        for (ODataEntity employee : employees.getEntities()) {
//            assertTrue(
//                    preSalaries.get(employee.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue())
//                    < employee.getProperty("Salary").getPrimitiveValue().<Integer>toCastValue());
//        }
//    }
}
