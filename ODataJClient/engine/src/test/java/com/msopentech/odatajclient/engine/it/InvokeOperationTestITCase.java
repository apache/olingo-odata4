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

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.invoke.ODataInvokeRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataDeleteResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataInvokeResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataNoContent;
import com.msopentech.odatajclient.engine.data.ODataOperation;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.EdmType;
import com.msopentech.odatajclient.engine.metadata.EdmV3Metadata;
import com.msopentech.odatajclient.engine.metadata.EdmV3Type;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.metadata.edm.v3.EntityContainer;
import com.msopentech.odatajclient.engine.metadata.edm.v3.FunctionImport;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.utils.URIUtils;

public class InvokeOperationTestITCase extends AbstractTestITCase {
    // get operation with no parameters

    private void invokeOperationWithNoParameters(final ODataPubFormat format,
            final String contentType,
            final String prefer) {

        ODataProperty property;
        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        //get primitive value property
        FunctionImport funcImp = container.getFunctionImport("GetPrimitiveString");

        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        ODataInvokeRequest<ODataProperty> req =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        ODataInvokeResponse<ODataProperty> res = req.execute();
        if (prefer.equals("return-content")) {
            assertNotNull(res);
            property = res.getBody();
            assertNotNull(property);
            assertEquals("Foo", property.getPrimitiveValue().<String>toCastValue());
        } else {
            assertEquals(204, res.getStatusCode());
        }

        //get collection of complex type property
        funcImp = container.getFunctionImport("EntityProjectionReturnsCollectionOfComplexTypes");

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, funcImp));

        req = client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, funcImp);
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        res = req.execute();
        if (prefer.equals("return-content")) {
            assertNotNull(res);
            property = res.getBody();
            assertNotNull(property);
            assertTrue(property.hasCollectionValue());
        } else {
            assertEquals(204, res.getStatusCode());
        }
    }

    // get operation with no parameters and format as JSON full metadata
    @Test
    public void invokeNoParamWithJSON() {
        ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        String contentType = "application/json;odata=fullmetadata";
        String prefer = "return-content";
        try {
            invokeOperationWithNoParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with no parameters and format as ATOM

    @Test
    public void invokeNoParamWithATOM() {
        ODataPubFormat format = ODataPubFormat.ATOM;
        String contentType = "application/atom+xml";
        String prefer = "return-content";
        try {
            invokeOperationWithNoParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with no parameters and format as JSON minimal metadata

    @Test
    public void invokeNoParamWithJSONMinimal() {
        ODataPubFormat format = ODataPubFormat.JSON;
        String contentType = "application/json";
        String prefer = "return-content";
        try {
            invokeOperationWithNoParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with no parameters and format as JSON no metadata

    @Test
    public void invokeNoParamWithJSONNoMetadata() {
        ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        String contentType = "application/json;odata=nometadata";
        String prefer = "return-content";
        try {
            invokeOperationWithNoParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with no parameters, format as JSON and content type header as ATOM

    @Test
    public void invokeNoParamWithJSONAndATOM() {
        ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        String contentType = "application/atom+xml";
        String prefer = "return-content";
        try {
            invokeOperationWithNoParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with no parameters, format as ATOM and content type header as JSON

    @Test
    public void invokeNoParamWithATOMAndJSON() {
        ODataPubFormat format = ODataPubFormat.ATOM;
        String contentType = "application/json;odata=fullmetadata";
        String prefer = "return-content";
        try {
            invokeOperationWithNoParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with no parameters

    private void invokeOperationWithParameters(
            final ODataPubFormat format,
            final String contentType,
            final String prefer) {
        // primitive result
        EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        FunctionImport imp = container.getFunctionImport("GetArgumentPlusOne");

        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, imp));

        EdmType type = new EdmV3Type(imp.getParameters().get(0).getType());
        ODataPrimitiveValue argument = client.getPrimitiveValueBuilder().
                setType(type.getSimpleType()).
                setValue(33).
                build();
        Map<String, ODataValue> parameters = new HashMap<String, ODataValue>();
        parameters.put(imp.getParameters().get(0).getName(), argument);

        final ODataInvokeRequest<ODataProperty> primitiveReq =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, imp,
                        parameters);
        primitiveReq.setFormat(format);
        primitiveReq.setContentType(contentType);
        primitiveReq.setPrefer(prefer);
        final ODataInvokeResponse<ODataProperty> primitiveRes = primitiveReq.execute();
        assertNotNull(primitiveRes);

        final ODataProperty property = primitiveRes.getBody();
        assertNotNull(property);
        assertEquals(Integer.valueOf(34), property.getPrimitiveValue().<Integer>toCastValue());

        // feed operation
        metadata = client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        container = metadata.getSchema(0).getEntityContainers().get(0);
        imp = container.getFunctionImport("GetSpecificCustomer");

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendFunctionImportSegment(URIUtils.rootFunctionImportURISegment(container, imp));

        type = new EdmV3Type(imp.getParameters().get(0).getType());
        argument = client.getPrimitiveValueBuilder().
                setType(type.getSimpleType()).
                setText(StringUtils.EMPTY).
                build();
        parameters = new LinkedHashMap<String, ODataValue>();
        parameters.put(imp.getParameters().get(0).getName(), argument);

        final ODataInvokeRequest<ODataEntitySet> feedReq =
                client.getInvokeRequestFactory().getInvokeRequest(builder.build(), metadata, imp, parameters);
        feedReq.setFormat(format);
        feedReq.setContentType(contentType);
        feedReq.setPrefer(prefer);
        final ODataInvokeResponse<ODataEntitySet> feedRes = feedReq.execute();
        assertNotNull(feedRes);

        final ODataEntitySet feed = feedRes.getBody();
        assertNotNull(feed);

        final ODataProperty id = feed.getEntities().get(0).getProperty("CustomerId");
        assertNotNull(id);
    }
    // get operation with parameters, format as JSON full metadata

    @Test
    public void invokeParamWithJSON() {
        ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        String contentType = "application/json;odata=fullmetadata";
        String prefer = "return-content";
        try {
            invokeOperationWithParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // get operation with parameters, format as ATOM 

    @Test
    public void invokeParamWithATOM() {
        ODataPubFormat format = ODataPubFormat.ATOM;
        String contentType = "application/atom+xml";
        String prefer = "return-content";
        try {
            invokeOperationWithParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }

    // create a product
    private ODataEntity createProduct(
            final ODataPubFormat format,
            final String contentType, final int id) {
        final ODataEntity product = client.getObjectFactory().newEntity(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Product");
        product.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId", client.
                getPrimitiveValueBuilder().
                setValue(id).setType(EdmSimpleType.Int32).build()));
        product.addProperty(client.getObjectFactory().newPrimitiveProperty("Description", client.
                getPrimitiveValueBuilder().
                setText("Test Product").build()));
        product.addProperty(client.getObjectFactory().newPrimitiveProperty("BaseConcurrency",
                client.getPrimitiveValueBuilder().
                setText("Test Base Concurrency").setType(EdmSimpleType.String).build()));

        final ODataComplexValue dimensions = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions");
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Width",
                client.getPrimitiveValueBuilder().setText("10.11").setType(EdmSimpleType.Decimal).build()));
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Height",
                client.getPrimitiveValueBuilder().setText("10.11").setType(EdmSimpleType.Decimal).build()));
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Depth",
                client.getPrimitiveValueBuilder().setText("10.11").setType(EdmSimpleType.Decimal).build()));

        product.addProperty(client.getObjectFactory().newComplexProperty("Dimensions",
                dimensions));

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product");

        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), product);
        createReq.setFormat(format);
        createReq.setContentType(contentType);

        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());

        return createRes.getBody();
    }
    // delete the created feed

    private void delete(final ODataPubFormat format, final String contentType, final Integer id, final String tag,
            final String feed) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment(feed).appendKeySegment(id);

        final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(uriBuilder.build());
        deleteReq.setFormat(format);
        deleteReq.setContentType(contentType);
        if (StringUtils.isNotBlank(tag)) {
            deleteReq.setIfMatch(tag);
        }
        final ODataDeleteResponse deleteRes = deleteReq.execute();
        assertEquals(204, deleteRes.getStatusCode());
    }

    // post operation with parameters
    private void boundPostWithParameters(
            final ODataPubFormat format,
            final String contentType,
            final String prefer) {
        final ODataEntity created = createProduct(format, contentType, 1905);
        assertNotNull(created);
        final Integer createdId = created.getProperty("ProductId").getPrimitiveValue().<Integer>toCastValue();
        assertNotNull(createdId);
        final ODataOperation action = created.getOperations().get(0);

        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);
        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        final FunctionImport funcImp = container.getFunctionImport(action.getTitle());
        final ODataComplexValue dimensions = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions");
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Width",
                client.getPrimitiveValueBuilder().setType(EdmSimpleType.Decimal).setText("99.11").build()));
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Height",
                client.getPrimitiveValueBuilder().setType(EdmSimpleType.Decimal).setText("99.11").build()));
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Depth",
                client.getPrimitiveValueBuilder().setType(EdmSimpleType.Decimal).setText("99.11").build()));

        Map<String, ODataValue> parameters = new LinkedHashMap<String, ODataValue>();
        parameters.put(funcImp.getParameters().get(1).getName(), dimensions);

        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
                action.getTarget(), metadata, funcImp, parameters);
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);

        final ODataInvokeResponse<ODataNoContent> res = req.execute();
        assertNotNull(res);
        assertEquals(204, res.getStatusCode());

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(createdId);
        final ODataEntityRequest retrieveRes = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        retrieveRes.setFormat(format);
        retrieveRes.setContentType(contentType);
        final ODataEntity read = retrieveRes.execute().getBody();

        ODataComplexValue value = read.getProperty("Dimensions").getComplexValue();
        assertEquals(dimensions.get("Depth").getValue(), value.get("Depth").getValue());
        assertEquals(dimensions.get("Width").getValue(), value.get("Width").getValue());
        assertEquals(dimensions.get("Height").getValue(), value.get("Height").getValue());
        delete(format, contentType, createdId, created.getETag(), "Product");

    }
    // test post operation with parameters and with JSON header

    @Test
    public void invokeOperationParamWithJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        try {
            boundPostWithParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // test post operation with parameters and with ATOM header

    @Test
    public void invokeOperationParamWithATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        try {
            boundPostWithParameters(format, contentType, prefer);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // create an employee

    private ODataEntity createEmployee(final ODataPubFormat format, final String contentType, final String prefer,
            final int id) {
        final ODataEntity employee = client.getObjectFactory().newEntity(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee");
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("PersonId", client.
                getPrimitiveValueBuilder().
                setValue(id).setType(EdmSimpleType.Int32).build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("Name", client.getPrimitiveValueBuilder().
                setText("Test employee").build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("ManagersPersonId", client.
                getPrimitiveValueBuilder().
                setText("1111").setType(EdmSimpleType.Int32).build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("Salary", client.getPrimitiveValueBuilder().
                setText("5999").setType(EdmSimpleType.Int32).build()));
        employee.addProperty(client.getObjectFactory().newPrimitiveProperty("Title", client.getPrimitiveValueBuilder().
                setText("Developer").build()));

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Person");

        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), employee);
        createReq.setFormat(format);
        createReq.setContentType(contentType);
        createReq.setPrefer(prefer);
        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());
        return createRes.getBody();
    }
    // post operation  

    private void boundPost(
            final ODataPubFormat format,
            final String contentType,
            final String prefer,
            final int id) {
        final ODataEntity created = createEmployee(format, contentType, prefer, id);
        assertNotNull(created);
        final Integer createdId = created.getProperty("PersonId").getPrimitiveValue().<Integer>toCastValue();
        assertNotNull(createdId);

        final ODataOperation action = created.getOperations().get(0);

        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        final FunctionImport funcImp = container.getFunctionImport(action.getTitle());

        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
                action.getTarget(), metadata, funcImp);
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        final ODataInvokeResponse<ODataNoContent> res = req.execute();
        assertNotNull(res);
        assertEquals(204, res.getStatusCode());

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Person").appendKeySegment(createdId);
        final ODataEntityRequest retrieveRes = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        retrieveRes.setFormat(format);
        retrieveRes.setContentType(contentType);
        retrieveRes.setPrefer(prefer);
        final ODataEntity read = retrieveRes.execute().getBody();
        assertEquals("0", read.getProperty("Salary").getPrimitiveValue().toString());
        assertTrue(read.getProperty("Title").getPrimitiveValue().toString().endsWith("[Sacked]"));

        delete(format, contentType, createdId, created.getETag(), "Person");
    }

    // test post operation without parameters and with JSON header
    @Test
    public void invokeOperationWithJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        try {
            boundPost(format, contentType, prefer, 2222);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // test post operation without parameters and with ATOM header

    @Test
    public void invokeOperationWithATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        try {
            boundPost(format, contentType, prefer, 2223);
        } catch (Exception e) {
            if (e.getMessage().equals("Unsupported media type requested. [HTTP/1.1 415 Unsupported Media Type]")) {
                assertTrue(true);
            } else {
                fail(e.getMessage());
            }
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // create an entity under feed 'Computer detail'

    private ODataEntity createComputerDetail(final ODataPubFormat format, final String contentType, final String prefer,
            final int id) {
        final ODataEntity entity =
                client.getObjectFactory().
                newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.ComputerDetail");
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Manufacturer",
                client.getPrimitiveValueBuilder().setText("manufacturer name").setType(EdmSimpleType.String).build()));

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ComputerDetailId",
                client.getPrimitiveValueBuilder().setText(String.valueOf(id)).setType(EdmSimpleType.Int32).build()));

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Model",
                client.getPrimitiveValueBuilder().setText("Model Name").setType(EdmSimpleType.String).build()));

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("ComputerDetail");

        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), entity);
        createReq.setFormat(format);
        createReq.setContentType(contentType);
        createReq.setPrefer(prefer);
        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());
        return createRes.getBody();
    }
    // post operation with parameters

    private void boundPostWithParametersComputer(
            final ODataPubFormat format,
            final String contentType,
            final String prefer,
            final int id) {
        final ODataEntity created = createComputerDetail(format, contentType, prefer, id);
        assertNotNull(created);
        final Integer createdId = created.getProperty("ComputerDetailId").getPrimitiveValue().<Integer>toCastValue();
        assertNotNull(createdId);

        final ODataOperation action = created.getOperations().get(0);

        final EdmV3Metadata metadata =
                client.getRetrieveRequestFactory().getMetadataRequest(testDefaultServiceRootURL).execute().getBody();
        assertNotNull(metadata);

        final EntityContainer container = metadata.getSchema(0).getEntityContainers().get(0);
        final FunctionImport funcImp = container.getFunctionImport(action.getTitle());
        final ODataCollectionValue specification = new ODataCollectionValue(("Collection(Edm.String)"));
        specification.add(client.getPrimitiveValueBuilder().setType(EdmSimpleType.String).setText("specification1").
                build());
        specification.add(client.getPrimitiveValueBuilder().setType(EdmSimpleType.String).setText("specification2").
                build());
        specification.add(client.getPrimitiveValueBuilder().setType(EdmSimpleType.String).setText("specification3").
                build());

        ODataValue argument = client.getPrimitiveValueBuilder().
                setType(EdmSimpleType.DateTime).
                setText("2011-11-11T23:59:59.9999999").
                build();
        Map<String, ODataValue> parameters = new LinkedHashMap<String, ODataValue>();
        parameters.put(funcImp.getParameters().get(1).getName(), specification);
        parameters.put(funcImp.getParameters().get(2).getName(), argument);

        final ODataInvokeRequest<ODataNoContent> req = client.getInvokeRequestFactory().getInvokeRequest(
                action.getTarget(), metadata, funcImp, parameters);
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        final ODataInvokeResponse<ODataNoContent> res = req.execute();
        assertNotNull(res);
        assertEquals(204, res.getStatusCode());

        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("ComputerDetail").appendKeySegment(createdId);
        final ODataEntityRequest retrieveRes = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        retrieveRes.setFormat(format);
        retrieveRes.setContentType(contentType);
        retrieveRes.setPrefer(prefer);
        final ODataEntity read = retrieveRes.execute().getBody();
        assertEquals("2011-11-11T23:59:59.9999999", read.getProperty("PurchaseDate").getValue().toString());
        delete(format, contentType, createdId, created.getETag(), "ComputerDetail");
    }
    // test with json

    @Test
    public void boundPostComputerDetailWithJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        try {
            boundPostWithParametersComputer(format, contentType, prefer, 2235);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // test with atom

    @Test
    public void boundPostComputerDetailWithATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        try {
            boundPostWithParametersComputer(format, contentType, prefer, 2235);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
}
