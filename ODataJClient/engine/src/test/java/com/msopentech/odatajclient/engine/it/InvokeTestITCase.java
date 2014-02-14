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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.invoke.ODataInvokeRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataDeleteResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataInvokeResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataNoContent;
import com.msopentech.odatajclient.engine.data.ODataOperation;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.EdmV3Type;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class InvokeTestITCase extends AbstractTestITCase {

    private void getWithNoParams(final ODataPubFormat format) {
        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);

        // 1. get primitive value property
        FunctionImport funcImp = container.getFunctionImport("GetPrimitiveString");

        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        ODataInvokeRequest<ODataProperty> req =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
        req.setFormat(format);
        ODataInvokeResponse<ODataProperty> res = req.execute();
        assertNotNull(res);

        ODataProperty property = res.getBody();
        assertNotNull(property);
        assertEquals("Foo", property.getPrimitiveValue().<String>toCastValue());

        // 2. get collection of complex type property
        funcImp = container.getFunctionImport("EntityProjectionReturnsCollectionOfComplexTypes");

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        req = client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
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

    private void getWithParams(final ODataPubFormat format) {
        // 1. primitive result
        EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        FunctionImport funcImp = container.getFunctionImport("GetArgumentPlusOne");

        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        EdmType type = new EdmV3Type(funcImp.getParameters().get(0).getType());
        ODataPrimitiveValue argument = client.getPrimitiveValueBuilder().
                setType(type.getSimpleType()).
                setValue(154).
                build();
        Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
        parameters.put(funcImp.getParameters().get(0).getName(), argument);

        final ODataInvokeRequest<ODataProperty> primitiveReq =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp,
                        parameters);
        primitiveReq.setFormat(format);

        final ODataInvokeResponse<ODataProperty> primitiveRes = primitiveReq.execute();
        assertNotNull(primitiveRes);

        final ODataProperty property = primitiveRes.getBody();
        assertNotNull(property);
        assertEquals(Integer.valueOf(155), property.getPrimitiveValue().<Integer>toCastValue());

        // 2. feed result
        metadata = client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        container = metadata.getSchema(0).getEntityContainers().get(0);
        funcImp = container.getFunctionImport("GetSpecificCustomer");

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        type = new EdmV3Type(funcImp.getParameters().get(0).getType());
        argument = client.getPrimitiveValueBuilder().
                setType(type.getSimpleType()).
                setText(StringUtils.EMPTY).
                build();
        parameters = new LinkedHashMap<String, ODataValue>();
        parameters.put(funcImp.getParameters().get(0).getName(), argument);

        final ODataInvokeRequest<ODataEntitySet> feedReq =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp, parameters);
        feedReq.setFormat(format);

        final ODataInvokeResponse<ODataEntitySet> feedRes = feedReq.execute();
        assertNotNull(feedRes);

        final ODataEntitySet feed = feedRes.getBody();
        assertNotNull(feed);

        final Set<Integer> customerIds = new HashSet<Integer>(feed.getEntities().size());
        for (ODataEntity entity : feed.getEntities()) {
            customerIds.add(entity.getProperty("CustomerId").getPrimitiveValue().<Integer>toCastValue());
        }
        assertTrue(customerIds.contains(-8));
    }

    @Test
    public void getWithParamsAsAtom() {
        getWithParams(ODataPubFormat.ATOM);
    }

    @Test
    public void getWithParamsAsJSON() {
        getWithParams(ODataPubFormat.JSON);
    }

    private ODataEntity createEmployee(final ODataPubFormat format) {
        final ODataEntity employee = client.getObjectFactory().newEntity(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");

        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("PersonId", client.
                getPrimitiveValueBuilder().
                setText("1244").setType(EdmSimpleType.Int32).build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("Name", client.getPrimitiveValueBuilder().
                setText("Test employee").build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("ManagersPersonId", client.
                getPrimitiveValueBuilder().
                setText("3777").setType(EdmSimpleType.Int32).build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("Salary", client.getPrimitiveValueBuilder().
                setText("1000").setType(EdmSimpleType.Int32).build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("Title", client.getPrimitiveValueBuilder().
                setText("CEO").build()));

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Person");

        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), employee);
        createReq.setFormat(format);
        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());

        return createRes.getBody();
    }

    private void deleteEmployee(final ODataPubFormat format, final Integer id) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Person").appendKeySegment(id);

        final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(uriBuilder.build());
        deleteReq.setFormat(format);
        final ODataDeleteResponse deleteRes = deleteReq.execute();
        assertEquals(204, deleteRes.getStatusCode());
    }

    @Test
    public void boundPost() {
        // 0. create an employee
        final ODataEntity created = createEmployee(ODataPubFormat.JSON_FULL_METADATA);
        assertNotNull(created);
        final Integer createdId = created.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue();
        assertNotNull(createdId);

        // 1. invoke action bound with the employee just created
        final ODataOperation action = created.getOperations().get(0);

        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        final FunctionImport funcImp = container.getFunctionImport(action.getTitle());

        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
                action.getTarget(), metadata, funcImp);
        req.setFormat(ODataPubFormat.JSON_FULL_METADATA);
        final ODataInvokeResponse<ODataNoContent> res = req.execute();
        assertNotNull(res);
        assertEquals(204, res.getStatusCode());

        // 2. check that invoked action has effectively run
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Person").appendKeySegment(createdId);
        final ODataEntityRequest retrieveRes = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        retrieveRes.setFormat(ODataPubFormat.JSON_FULL_METADATA);
        final ODataEntity read = retrieveRes.execute().getBody();
        assertEquals("0", read.getProperty("Salary").getPrimitiveValue().toString());
        assertTrue(read.getProperty("Title").getPrimitiveValue().toString().endsWith("[Sacked]"));

        // 3. remove the test employee
        deleteEmployee(ODataPubFormat.JSON_FULL_METADATA, createdId);
    }

    @Test
    public void boundPostWithParams() {
        // 1. read employees and store their current salary
        final URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Person").
                appendEntityTypeSegment("Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
        final URI employeesURI = builder.build();
        ODataEntitySet employees = client.getRetrieveRequestFactory().getEntitySetRequest(employeesURI).execute().
                getBody();
        assertFalse(employees.getEntities().isEmpty());
        final Map<Integer, Integer> preSalaries = new HashMap<Integer, Integer>(employees.getCount());
        for (ODataEntity employee : employees.getEntities()) {
            preSalaries.put(employee.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue(),
                    employee.getProperty("Salary").getPrimitiveValue().<Integer>toCastValue());
        }
        assertFalse(preSalaries.isEmpty());

        // 2. invoke action bound, with additional parameter
        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        final FunctionImport funcImp = container.getFunctionImport("IncreaseSalaries");

        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
                builder.appendStructuralSegment(funcImp.getName()).build(), metadata, funcImp,
                Collections.<String, ODataValue>singletonMap(
                        "n", client.getPrimitiveValueBuilder().setValue(1).setType(EdmSimpleType.Int32).build()));
        final ODataInvokeResponse<ODataNoContent> res = req.execute();
        assertNotNull(res);
        assertEquals(204, res.getStatusCode());

        // 3. check whether salaries were incremented
        employees = client.getRetrieveRequestFactory().getEntitySetRequest(employeesURI).execute().getBody();
        assertFalse(employees.getEntities().isEmpty());
        for (ODataEntity employee : employees.getEntities()) {
            assertTrue(
                    preSalaries.get(employee.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue())
                    < employee.getProperty("Salary").getPrimitiveValue().<Integer>toCastValue());
        }
    }
}
