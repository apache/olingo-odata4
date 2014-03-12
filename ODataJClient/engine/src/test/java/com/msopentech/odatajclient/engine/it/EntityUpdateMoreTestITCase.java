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

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataInlineEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;

public class EntityUpdateMoreTestITCase extends AbstractTestITCase {
    // update an entity

    private void updateEntity(
            final ODataPubFormat format,
            final String contentType,
            final String prefer,
            final UpdateType type,
            final boolean inlineInfo) {
        // create an entity to be updated
        final ODataEntity entity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");
        // add name attribute
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Name",
                client.getPrimitiveValueBuilder().setText("Updated Customer name").setType(EdmSimpleType.String).build()));
        // add key attribute
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("CustomerId",
                client.getPrimitiveValueBuilder().setText(String.valueOf(-10)).setType(EdmSimpleType.Int32).build()));

        final ODataCollectionValue backupContactInfoValue = new ODataCollectionValue(
                "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");
        final ODataComplexValue contactDetails = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");
        final ODataCollectionValue altNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        altNamesValue.add(client.getPrimitiveValueBuilder().
                setText("My Alternative name").setType(EdmSimpleType.String).build());
        contactDetails.add(client.getObjectFactory().newCollectionProperty("AlternativeNames", altNamesValue));

        final ODataCollectionValue emailBagValue = new ODataCollectionValue("Collection(Edm.String)");
        emailBagValue.add(client.getPrimitiveValueBuilder().
                setText("altname@mydomain.com").setType(EdmSimpleType.String).build());
        contactDetails.add(client.getObjectFactory().newCollectionProperty("EmailBag", emailBagValue));

        final ODataComplexValue contactAliasValue = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
        contactDetails.add(client.getObjectFactory().newComplexProperty("ContactAlias", contactAliasValue));

        final ODataCollectionValue aliasAltNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        aliasAltNamesValue.add(client.getPrimitiveValueBuilder().
                setText("myAlternativeName").setType(EdmSimpleType.String).build());
        contactAliasValue.add(client.getObjectFactory().newCollectionProperty("AlternativeNames", aliasAltNamesValue));

        final ODataComplexValue homePhone = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone");
        homePhone.add(client.getObjectFactory().newPrimitiveProperty("PhoneNumber",
                client.getPrimitiveValueBuilder().setText("8437568356834568").setType(EdmSimpleType.String).build()));
        homePhone.add(client.getObjectFactory().newPrimitiveProperty("Extension",
                client.getPrimitiveValueBuilder().setText("1243654265346267651534423ttrf").setType(EdmSimpleType.String).
                build()));
        contactDetails.add(client.getObjectFactory().newComplexProperty("HomePhone", homePhone));

        backupContactInfoValue.add(contactDetails);
        entity.addProperty(client.getObjectFactory().newCollectionProperty("BackupContactInfo",
                backupContactInfoValue));

        if (inlineInfo) {
            final ODataInlineEntity info = client.getObjectFactory().newInlineEntity(
                    "Info",
                    URI.create("Customer(-10)/Info"),
                    getSampleCustomerInfo(-10, "Updated customer information_Info"));
            info.getEntity().setMediaEntity(true);
            entity.addLink(info);
        }
        final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).build();
        final String etag = getETag(uri);
        entity.setEditLink(uri);
        // update code
        update(format, contentType, prefer, type, entity, etag);
        final ODataEntityUpdateRequest req = client.getCUDRequestFactory().getEntityUpdateRequest(type, entity);
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, req.getMethod());
        } else {
            assertEquals(UpdateType.REPLACE.getMethod(), req.getMethod());
        }

        if (StringUtils.isNotBlank(etag)) {
            req.setIfMatch(etag);
        }
        final ODataEntityUpdateResponse res = req.execute();
        if (prefer.equals("return-content")) {
            assertEquals(200, res.getStatusCode());
            if (inlineInfo) {
                final ODataEntity actual = compareEntities(testStaticServiceRootURL, format, entity, -10, Collections.
                        <String>singleton("Info"));
                assertNotNull(actual);
            } else {
                final ODataEntity actual = compareEntities(testStaticServiceRootURL, format, entity, -10, null);
                assertNotNull(actual);
            }
        } else {
            assertEquals(204, res.getStatusCode());
        }
    }
    // update entity string property

    private void updateEntityStringProperty(
            final ODataPubFormat format,
            final String propertyName,
            final ODataEntity entitySetName,
            final UpdateType type,
            final String etag) {
        String newValue = "";
        newValue = "New " + propertyName + "(" + System.currentTimeMillis() + ")";

        ODataProperty propertyValue = entitySetName.getProperty(propertyName);
        final String oldValue;
        if (propertyValue == null) {
            oldValue = null;
        } else {
            oldValue = propertyValue.getValue().toString();
            entitySetName.removeProperty(propertyValue);
        }
        assertNotEquals(newValue, oldValue);
        entitySetName.addProperty(client.getObjectFactory().newPrimitiveProperty(propertyName,
                client.getPrimitiveValueBuilder().setText(newValue).build()));

        update(type, entitySetName, format, etag);
        propertyValue = null;

        for (ODataProperty prop : entitySetName.getProperties()) {
            if (prop.getName().equals(propertyName)) {
                propertyValue = prop;
            }
        }
        assertNotNull(propertyValue);
        assertEquals(newValue, propertyValue.getValue().toString());
    }
    // date property update operation

    private void updateEntityDateProperty(
            final ODataPubFormat format,
            final String propertyName,
            final ODataEntity entitySetName,
            final UpdateType type,
            final String etag) {
        String newValue = "2013-11-11T23:59:59.9999999";

        ODataProperty propertyValue = entitySetName.getProperty(propertyName);
        final String oldValue;
        if (propertyValue == null) {
            oldValue = null;
        } else {
            oldValue = propertyValue.getValue().toString();
            entitySetName.removeProperty(propertyValue);
        }
        assertNotEquals(newValue, oldValue);
        entitySetName.addProperty(client.getObjectFactory().newPrimitiveProperty(propertyName,
                client.getPrimitiveValueBuilder().setText(newValue).setType(EdmSimpleType.DateTime).build()));
        update(type, entitySetName, format, etag);
        propertyValue = null;
        for (ODataProperty prop : entitySetName.getProperties()) {
            if (prop.getName().equals(propertyName)) {
                propertyValue = prop;
            }
        }
        assertNotNull(propertyValue);
        assertEquals("2013-11-11T23:59:59.9999999", propertyValue.getValue().toString());
    }
    // Integer property update operation

    private void updateEntityIntProperty(
            final ODataPubFormat format,
            final String propertyName,
            final ODataEntity entitySetName,
            final UpdateType type,
            final String etag) {
        int newValue = 10323232;
        ODataProperty propertyValue = entitySetName.getProperty(propertyName);
        final int oldValue;
        if (Integer.parseInt(propertyValue.getValue().toString()) == 0) {
            oldValue = 0;
        } else {
            oldValue = Integer.parseInt(propertyValue.getValue().toString());
            entitySetName.removeProperty(propertyValue);
        }
        assertNotEquals(newValue, oldValue);

        entitySetName.addProperty(client.getObjectFactory().newPrimitiveProperty(propertyName,
                client.getPrimitiveValueBuilder().setValue(newValue).setType(EdmSimpleType.Int32).build()));
        update(type, entitySetName, format, etag);
        propertyValue = null;
        for (ODataProperty prop : entitySetName.getProperties()) {
            if (prop.getName().equals(propertyName)) {
                propertyValue = prop;
            }
        }
        assertNotNull(propertyValue);
        assertEquals(newValue, Integer.parseInt(propertyValue.getValue().toString()));

        // replace the old value
        ODataProperty replaceProperty = entitySetName.getProperty(propertyName);
        entitySetName.removeProperty(replaceProperty);

        entitySetName.addProperty(client.getObjectFactory().newPrimitiveProperty(propertyName,
                client.getPrimitiveValueBuilder().setValue(oldValue).setType(EdmSimpleType.Int32).build()));

        update(type, entitySetName, format, etag);
    }

    // update operation 
    protected void update(
            final ODataPubFormat format,
            final String contentType,
            final String prefer,
            final UpdateType type,
            final ODataEntity entitySetName,
            String etag) {
        final ODataEntityUpdateRequest req = client.getCUDRequestFactory().getEntityUpdateRequest(type, entitySetName);
        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, req.getMethod());
        } else {
            assertEquals(type.getMethod(), req.getMethod());
        }
        req.setFormat(format);
        req.setContentType(contentType);
        req.setPrefer(prefer);
        etag = entitySetName.getETag();
        if (StringUtils.isNotBlank(etag)) {
            req.setIfMatch(etag);
        }
        final ODataEntityUpdateResponse res = req.execute();

        if (prefer.equals("return-content")) {
            assertEquals(200, res.getStatusCode());
        } else {
            assertEquals(204, res.getStatusCode());
        }
    }

    //update integer with JSON full meta data header 
    @Test
    public void updateIntPropertyAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-no-content";
        final String entitySet = "OrderLine";
        final String propertyType = "Quantity";
        final UpdateType replace = UpdateType.REPLACE;
        final UpdateType merge = UpdateType.MERGE;
        final UpdateType patch = UpdateType.PATCH;
        final HashMap<String, Object> multiKey = new HashMap<String, Object>();
        multiKey.put("OrderId", -10);
        multiKey.put("ProductId", -10);
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                appendEntityTypeSegment(entitySet).appendKeySegment(multiKey);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(contentType);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        final ODataEntity entity = res.getBody();

        final String etag = res.getEtag();
        updateEntityIntProperty(format, propertyType, entity, replace, etag);
        updateEntityIntProperty(format, propertyType, entity, merge, etag);
        updateEntityIntProperty(format, propertyType, entity, patch, etag);
    }
    // update integer property with json minimal metadata. Its throws Illegal Argument Exception 

    @Test
    public void updateIntPropertyAsJSONMinimal() {
        final ODataPubFormat format = ODataPubFormat.JSON;
        final String contentType = "application/json";
        final String prefer = "return-content";
        final String entitySet = "OrderLine";
        final String propertyType = "Quantity";
        final UpdateType replace = UpdateType.REPLACE;
        final UpdateType merge = UpdateType.MERGE;
        final UpdateType patch = UpdateType.PATCH;
        try {
            final HashMap<String, Object> multiKey = new HashMap<String, Object>();
            multiKey.put("OrderId", -10);
            multiKey.put("ProductId", -10);
            final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(multiKey);
            final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
            req.setFormat(format);
            req.setAccept(contentType);

            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            final ODataEntity entity = res.getBody();

            final String etag = res.getEtag();
            updateEntityIntProperty(format, propertyType, entity, replace, etag);
            updateEntityIntProperty(format, propertyType, entity, merge, etag);
            updateEntityIntProperty(format, propertyType, entity, patch, etag);
        } catch (Exception e) {
            if (e.getMessage().equals("No edit link found")) {
                assertTrue(true);
            } else {
                fail(e.getMessage());
            }
        }
    }
    //update date with JSON header

    @Test
    public void updateDatePropertyAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String entitySet = "ComputerDetail";
        final String propertyType = "PurchaseDate";
        final UpdateType replace = UpdateType.REPLACE;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(
                    "Microsoft.Test.OData.Services.AstoriaDefaultService.ComputerDetail");
            entity.setEditLink(uri);
            updateEntityDateProperty(format, propertyType, entity, replace, etag);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // update base concurrency property which is tagged as ETag.	

    @Test
    public void updateConcurrencyPropertyAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String entitySet = "Product";
        final String propertyType = "BaseConcurrency";
        final UpdateType replace = UpdateType.REPLACE;
        final UpdateType merge = UpdateType.MERGE;
        final UpdateType patch = UpdateType.PATCH;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
            entity.setEditLink(uri);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId",
                    client.getPrimitiveValueBuilder().setValue(-10).setType(EdmSimpleType.Int32).build()));
            updateEntityStringProperty(format, propertyType, entity, replace, etag);
            updateEntityStringProperty(format, propertyType, entity, merge, etag);
            updateEntityStringProperty(format, propertyType, entity, patch, etag);
        } catch (ODataClientErrorException e) {
            assertEquals(412, e.getStatusLine().getStatusCode());
        }

    }
    // test property update with JSON 

    @Test
    public void updatePropertyAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String entitySet = "Product";
        final String propertyType = "Description";
        final UpdateType replace = UpdateType.REPLACE;
        final UpdateType merge = UpdateType.MERGE;
        final UpdateType patch = UpdateType.PATCH;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
            entity.setEditLink(uri);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId",
                    client.getPrimitiveValueBuilder().setValue(-10).setType(EdmSimpleType.Int32).build()));
            updateEntityStringProperty(format, propertyType, entity, replace, etag);
            updateEntityStringProperty(format, propertyType, entity, merge, etag);
            updateEntityStringProperty(format, propertyType, entity, patch, etag);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // test property update with ATOM 

    @Test
    public void updatePropertyAsATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String entitySet = "Product";
        final String propertyType = "Description";
        final UpdateType replace = UpdateType.REPLACE;
        final UpdateType merge = UpdateType.MERGE;
        final UpdateType patch = UpdateType.PATCH;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
            entity.setEditLink(uri);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId",
                    client.getPrimitiveValueBuilder().setValue(-10).setType(EdmSimpleType.Int32).build()));
            updateEntityStringProperty(format, propertyType, entity, replace, etag);
            updateEntityStringProperty(format, propertyType, entity, merge, etag);
            updateEntityStringProperty(format, propertyType, entity, patch, etag);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // test property update with JSON minimal metadata 

    @Test
    public void updatePropertyAsJSONMinimal() {
        final ODataPubFormat format = ODataPubFormat.JSON;
        final String entitySet = "Product";
        final String propertyType = "Description";
        final UpdateType replace = UpdateType.REPLACE;
        final UpdateType merge = UpdateType.MERGE;
        final UpdateType patch = UpdateType.PATCH;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
            entity.setEditLink(uri);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId",
                    client.getPrimitiveValueBuilder().setValue(-10).setType(EdmSimpleType.Int32).build()));
            updateEntityStringProperty(format, propertyType, entity, replace, etag);
            updateEntityStringProperty(format, propertyType, entity, merge, etag);
            updateEntityStringProperty(format, propertyType, entity, patch, etag);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // updating an entity which is not nullable should return 400 status

    @Test
    public void updateNonNullableProperty() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String entitySet = "Product";
        final String propertyType = "ProductId";
        final UpdateType replace = UpdateType.REPLACE;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(entitySet).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
            entity.setEditLink(uri);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId",
                    client.getPrimitiveValueBuilder().setValue(-10).setType(EdmSimpleType.Int32).build()));
            updateEntityStringProperty(format, propertyType, entity, replace, etag);
        } catch (Exception e) {
            if (e.getMessage().equals("An error occurred while processing this request. [HTTP/1.1 400 Bad Request]")) {
                assertTrue(true);
            } else {
                fail(e.getMessage());
            }
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // test property update with JSON no metadata 

    @Test
    public void updatePropertyAsJSONNoMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        final String propertyType = "Product";
        final UpdateType replace = UpdateType.REPLACE;
        try {
            final URI uri = client.getURIBuilder(testStaticServiceRootURL).
                    appendEntityTypeSegment(propertyType).appendKeySegment(-10).build();
            final String etag = getETag(uri);
            final ODataEntity entity = client.getObjectFactory().newEntity(TEST_PRODUCT_TYPE);
            entity.setEditLink(uri);
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ProductId",
                    client.getPrimitiveValueBuilder().setValue(-10).setType(EdmSimpleType.Int32).build()));
            updateEntityStringProperty(format, "Description", entity, replace, etag);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // update an entity with JSON full metadata

    @Test
    public void updateAsJSONWithReplace() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final UpdateType type = UpdateType.REPLACE;
        try {
            updateEntity(format, contentType, prefer, type, false);
        } catch (Exception e) {
            if (e.getMessage().equals(
                    "Error processing request stream. Deep updates are not supported in PUT, MERGE, or PATCH operations. [HTTP/1.1 400 Bad Request]")) {
                assertTrue(true);
            } else {
                fail(e.getMessage());
            }
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // update an entity with ATOM

    @Test
    public void updateAsATOMWithReplace() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        final UpdateType type = UpdateType.REPLACE;
        try {
            updateEntity(format, contentType, prefer, type, true);
        } catch (Exception e) {
            if (e.getMessage().equals(
                    "Error processing request stream. Deep updates are not supported in PUT, MERGE, or PATCH operations. [HTTP/1.1 400 Bad Request]")) {
                assertTrue(true);
            } else {
                fail(e.getMessage());
            }
        }
    }
}
