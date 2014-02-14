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

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.cud.CUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataDeleteResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataObjectFactory;
import com.msopentech.odatajclient.engine.data.ODataInlineEntity;
import com.msopentech.odatajclient.engine.data.ODataInlineEntitySet;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.utils.URIUtils;

public class CreateEntityTestITCase extends AbstractTestITCase {

    // test with json full metadata
    @Test
    public void withJSONFullMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 1063;
        final ODataEntity original = getNewCustomer(id, "New Customer", false);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null, "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with atom

    @Test
    public void withATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contenttype = "application/atom+xml";
        final String prefer = "return-no-content";
        final int id = 1064;
        final ODataEntity original = getNewCustomer(id, "New Customer", false);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null, "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // gives null pointer exception when the content type and the accept type is json mo metadata 

    @Test(expected = HttpClientException.class)
    public void withJSONNoMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        final String contenttype = "application/json;odata=nometadata";
        final String prefer = "return-content";
        final int id = 15;
        final ODataEntity original = getNewCustomer(id, "New Customer", false);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null, "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // deep insert

    @Test
    public void createInlineWithATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        final int id = 5777;
        final ODataEntity original = getNewCustomer(id, "New customer", true);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contentType, prefer);
        final ODataEntity actual =
                validateEntities(testDefaultServiceRootURL, format, original, id, Collections.<String>singleton("Info"),
                "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // deep insert

    @Test
    public void createInlineWithJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 67;
        final ODataEntity original = getNewCustomer(id, "New customer", true);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contentType, prefer);
        final ODataEntity actual =
                validateEntities(testDefaultServiceRootURL, format, original, id, Collections.<String>singleton("Info"),
                "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // with a string having special characters, html tags, numbers and characters

    @Test
    public void withSpecialString() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 11;
        final ODataEntity original = getNewCustomer(id, "New 12,345//\\%^&*()<html>customer", false);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null, "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // with atom content type and json accept type. its giving 400 error.

    @Test
    public void createWithATOMReturnJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/atom+xml";
        final String prefer = "return-content";
        final int id = 146;
        try {
            final ODataEntity original = getNewCustomer(id, "New Customer", false);
            createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
            final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null,
                    "Customer");
            delete(format, actual, false, testDefaultServiceRootURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    // with json content type and atom accept type. its giving 400 error.

    @Test
    public void createWithJSONReturnATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 155;
        try {
            final ODataEntity original = getNewCustomer(id, "New Customer", false);
            createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
            final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null,
                    "Customer");
            delete(format, actual, false, testDefaultServiceRootURL);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    // a long String in the Name field

    @Test
    public void withLongString() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 1112;
        final ODataEntity original = getNewCustomer(id,
                "Sampledskjfhsdkjfhsdkfhksdjhfksdhfvjhdfgkjhfdkjghdfkjghkfdhgkdfhgdfhgkjdfghkdfjghkdfjghfkdjghkdfghkdfhgkdfjghdfkjghkfdjghfksdhfkjsdhfdshfhsdfjhsdkfhkdsfhksdfhksdhfksdhfksdhfksdhfsdhfksdhfkjsdhfksdhfksdhfkds",
                false);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null, "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }

    //Test with no Id
    @Test
    public void withNoId() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 0;
        final ODataEntity original = getNewCustomer(id, "New Customer", false);
        createEntity(testDefaultServiceRootURL, format, original, "Customer", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null, "Customer");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test different primitive properties like date, String, decimal

    @Test
    public void differentProperties() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-no-content";
        final int id = 34;
        final ODataEntity original = getComputerDetailsEntity(id, "Computer details", false,
                "2013-12-31T23:59:59.9999999", "-32.4985749");
        createEntity(testDefaultServiceRootURL, format, original, "ComputerDetail", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null,
                "ComputerDetail");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with invalid 

    @Test(expected = IllegalArgumentException.class)
    public void invalidDateTest() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-no-content";
        final int id = 34;
        final ODataEntity original = getComputerDetailsEntity(id, "Computer details", false, "abc", "-32.4985749");
        createEntity(testDefaultServiceRootURL, format, original, "ComputerDetail", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null,
                "ComputerDetail");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with different decimal values. Returns 400 error

    @Test(expected = ODataClientErrorException.class)
    public void testWithDecimal() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-no-content";
        final int id = 35;
        final ODataEntity original = getComputerDetailsEntity(id, "Computer details Test", false,
                "2013-12-31T23:59:59.9999999",
                "-344587543985799834759845798475943759438573495734985739457349857394857894.4985749");
        createEntity(testDefaultServiceRootURL, format, original, "ComputerDetail", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null,
                "ComputerDetail");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // test with date. Unable to parse a date because the property type is datetime and not date

    @Test(expected = IllegalArgumentException.class)
    public void withDate() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contenttype = "application/json;odata=fullmetadata";
        final String prefer = "return-no-content";
        final int id = 35;
        final ODataEntity original = getComputerDetailsEntity(id, "Computer details Test", false, "2013-12-31",
                "-37894.4985749");
        createEntity(testDefaultServiceRootURL, format, original, "ComputerDetail", contenttype, prefer);
        final ODataEntity actual = validateEntities(testDefaultServiceRootURL, format, original, id, null,
                "ComputerDetail");
        delete(format, actual, false, testDefaultServiceRootURL);
    }
    // with multiple key test

    @Test
    public void createWithMultipleKey() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        try {
            final ODataEntity message = client.getObjectFactory().newEntity(
                    "Microsoft.Test.OData.Services.AstoriaDefaultService.Message");

            message.addProperty(client.getObjectFactory().newPrimitiveProperty("MessageId",
                    client.getPrimitiveValueBuilder().setValue(111).setType(EdmSimpleType.Int32).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("FromUsername",
                    client.getPrimitiveValueBuilder().setValue("user").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("ToUsername",
                    client.getPrimitiveValueBuilder().setValue("usernameabc").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("Subject",
                    client.getPrimitiveValueBuilder().setValue("Subject of message").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("Body",
                    client.getPrimitiveValueBuilder().setValue("Body Content").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("IsRead",
                    client.getPrimitiveValueBuilder().setValue(false).setType(EdmSimpleType.Boolean).build()));

            final URIBuilder builder =
                    client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Message");
            final ODataEntityCreateRequest req = client.getCUDRequestFactory().getEntityCreateRequest(builder.build(), message);
            req.setFormat(format);
            req.setContentType(contentType);
            req.setPrefer(prefer);
            final ODataEntityCreateResponse res = req.execute();
            assertNotNull(res);
            assertEquals(201, res.getStatusCode());

            final Map<String, Object> multiKey = new HashMap<String, Object>();
            multiKey.put("FromUsername", "user");
            multiKey.put("MessageId", 111);

            final ODataDeleteResponse deleteRes = client.getCUDRequestFactory().
                    getDeleteRequest(builder.appendKeySegment(multiKey).build()).execute();
            assertEquals(204, deleteRes.getStatusCode());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    // create an entity which has multiple keys by only one of the key is send in the request payload.
    // it creates the entity successfully

    @Test
    public void createNoMultipleKey() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        try {
            final ODataEntity message = client.getObjectFactory().newEntity(
                    "Microsoft.Test.OData.Services.AstoriaDefaultService.Message");

            message.addProperty(client.getObjectFactory().newPrimitiveProperty("MessageId",
                    client.getPrimitiveValueBuilder().setText(String.valueOf(25)).
                    setType(EdmSimpleType.Int32).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("FromUsername",
                    client.getPrimitiveValueBuilder().setText("user").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("ToUsername",
                    client.getPrimitiveValueBuilder().setValue("usernameabc").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("Subject",
                    client.getPrimitiveValueBuilder().setValue("Subject of message").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("Body",
                    client.getPrimitiveValueBuilder().setValue("Body Content").
                    setType(EdmSimpleType.String).build()));
            message.addProperty(client.getObjectFactory().newPrimitiveProperty("IsRead",
                    client.getPrimitiveValueBuilder().setValue(false).setType(EdmSimpleType.Boolean).build()));

            final URIBuilder builder =
                    client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Message");
            final ODataEntityCreateRequest req = client.getCUDRequestFactory().getEntityCreateRequest(builder.build(), message);
            req.setFormat(format);
            req.setContentType(contentType);
            req.setPrefer(prefer);
            final ODataEntityCreateResponse res = req.execute();
            assertNotNull(res);
            assertEquals(201, res.getStatusCode());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    // test open type create Entity, JSON full metadata

    @Test
    public void openTypeCreateJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 11;
        final String gid = "random";
        getOpenTypeEntity(format, contentType, id, prefer, gid);
    }
    // test open type create Entity, ATOM full metadata

    @Test
    public void openTypeCreateATOM() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final String contentType = "application/atom+xml";
        final String prefer = "return-content";
        final int id = 12;
        final String gid = "random";
        getOpenTypeEntity(format, contentType, id, prefer, gid);
    }
    // test open type create Entity, JSON no metadata.

    @Test
    public void openTypeCreateNoMetadata() {
        final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        final String contentType = "application/json;odata=nometadata";
        final String prefer = "return-content";
        final int id = 1333;
        final String gid = "random";
        getOpenTypeEntity(format, contentType, id, prefer, gid);
    }
    // test with invalid guid

    @Test
    public void withInvalidGuid() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullmetadata";
        final String prefer = "return-content";
        final int id = 777;
        final String gid = "00000000-0000-0000-0000-000000000000";
        getOpenTypeEntity(format, contentType, id, prefer, gid);
    }
    // test with guid as string.

    @Test
    public void withStringGuid() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final String contentType = "application/json;odata=fullemtadata";
        final String prefer = "return-content";
        final int id = 777;
        final String gid = "stringguid";
        getOpenTypeEntityWithString(format, contentType, id, prefer, gid);
    }

    // compares links of the newly created entity with the previous 
    public void validateLinks(final Collection<ODataLink> original, final Collection<ODataLink> actual) {
        assertTrue(original.size() <= actual.size());

        for (ODataLink originalLink : original) {
            ODataLink foundOriginal = null;
            ODataLink foundActual = null;

            for (ODataLink actualLink : actual) {

                if (actualLink.getType() == originalLink.getType()
                        && (originalLink.getLink() == null
                        || actualLink.getLink().toASCIIString().endsWith(originalLink.getLink().toASCIIString()))
                        && actualLink.getName().equals(originalLink.getName())) {

                    foundOriginal = originalLink;
                    foundActual = actualLink;
                }
            }

            assertNotNull(foundOriginal);
            assertNotNull(foundActual);

            if (foundOriginal instanceof ODataInlineEntity && foundActual instanceof ODataInlineEntity) {
                final ODataEntity originalInline = ((ODataInlineEntity) foundOriginal).getEntity();
                assertNotNull(originalInline);

                final ODataEntity actualInline = ((ODataInlineEntity) foundActual).getEntity();
                assertNotNull(actualInline);

                checkProperties(originalInline.getProperties(), actualInline.getProperties());
            }
        }
    }
    // compares properties of the newly created entity with the properties that were originally provided

    public void checkProperties(final Collection<ODataProperty> original, final Collection<ODataProperty> actual) {
        assertTrue(original.size() <= actual.size());

        final Map<String, ODataProperty> actualProperties = new HashMap<String, ODataProperty>(actual.size());

        for (ODataProperty prop : actual) {
            assertFalse(actualProperties.containsKey(prop.getName()));
            actualProperties.put(prop.getName(), prop);
        }

        assertTrue(actual.size() <= actualProperties.size());

        for (ODataProperty prop : original) {
            assertNotNull(prop);
            if (actualProperties.containsKey(prop.getName())) {
                final ODataProperty actualProp = actualProperties.get(prop.getName());
                assertNotNull(actualProp);

                if (prop.getValue() != null && actualProp.getValue() != null) {
                    checkPropertyValue(prop.getName(), prop.getValue(), actualProp.getValue());
                }
            }
        }
    }
    // compares property value of the newly created entity with the property value that were originally provided

    public void checkPropertyValue(final String propertyName,
            final ODataValue original, final ODataValue actual) {

        assertNotNull("Null original value for " + propertyName, original);
        assertNotNull("Null actual value for " + propertyName, actual);

        assertEquals("Type mismatch for '" + propertyName + "'",
                original.getClass().getSimpleName(), actual.getClass().getSimpleName());

        if (original.isComplex()) {
            final List<ODataProperty> originalPropertyValue = new ArrayList<ODataProperty>();
            for (ODataProperty prop : original.asComplex()) {
                originalPropertyValue.add(prop);
            }

            final List<ODataProperty> actualPropertyValue = new ArrayList<ODataProperty>();
            for (ODataProperty prop : (ODataComplexValue) actual) {
                actualPropertyValue.add(prop);
            }

            checkProperties(originalPropertyValue, actualPropertyValue);
        } else if (original.isCollection()) {
            assertTrue(original.asCollection().size() <= actual.asCollection().size());

            boolean found = original.asCollection().isEmpty();

            for (ODataValue originalValue : original.asCollection()) {
                for (ODataValue actualValue : actual.asCollection()) {
                    try {
                        checkPropertyValue(propertyName, originalValue, actualValue);
                        found = true;
                    } catch (AssertionError error) {
                    }
                }
            }

            assertTrue("Found " + actual + " and expected " + original, found);
        } else {
            assertTrue("Primitive value for '" + propertyName + "' type mismatch",
                    original.asPrimitive().getTypeName().equals(actual.asPrimitive().getTypeName()));

            assertEquals("Primitive value for '" + propertyName + "' mismatch",
                    original.asPrimitive().toString(), actual.asPrimitive().toString());
        }
    }
    // add Information property

    public ODataEntity getInfo(final int id, final String info) {
        final ODataEntity entity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo");
        entity.setMediaEntity(true);

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Information",
                client.getPrimitiveValueBuilder().setText(info).setType(EdmSimpleType.String).build()));
        return entity;
    }
    // get a Customer entity to be created

    public ODataEntity getNewCustomer(
            final int id, final String name, final boolean withInlineInfo) {

        final ODataEntity entity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");

        // add name attribute
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Name",
                client.getPrimitiveValueBuilder().setText(name).setType(EdmSimpleType.String).build()));

        // add key attribute
        if (id != 0) {
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("CustomerId",
                    client.getPrimitiveValueBuilder().setText(String.valueOf(id)).
                    setType(EdmSimpleType.Int32).build()));
        } else {
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("CustomerId",
                    client.getPrimitiveValueBuilder().setText(String.valueOf(0)).
                    setType(EdmSimpleType.Int32).build()));
        }
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
                client.getPrimitiveValueBuilder().setText("124365426534621534423ttrf").setType(EdmSimpleType.String).
                build()));
        contactDetails.add(client.getObjectFactory().newComplexProperty("HomePhone", homePhone));

        backupContactInfoValue.add(contactDetails);
        entity.addProperty(client.getObjectFactory().newCollectionProperty("BackupContactInfo",
                backupContactInfoValue));
        if (withInlineInfo) {
            final ODataInlineEntity inlineInfo = client.getObjectFactory().newInlineEntity("Info", URI.create("Customer(" + id
                    + ")/Info"), getInfo(id, name + "_Info"));
            inlineInfo.getEntity().setMediaEntity(true);
            entity.addLink(inlineInfo);
        }

        return entity;
    }
    // get a ComputerDetail entity to be created

    public ODataEntity getComputerDetailsEntity(
            final int id, final String sampleName, final boolean withInlineInfo,
            final String purchaseDate, String dimensionValue) {

        final ODataEntity entity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.ComputerDetail");


        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Manufacturer",
                client.getPrimitiveValueBuilder().setText("manufacturer name").
                setType(EdmSimpleType.String).build()));

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("ComputerDetailId",
                client.getPrimitiveValueBuilder().setText(String.valueOf(id)).
                setType(EdmSimpleType.Int32).build()));

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Model",
                client.getPrimitiveValueBuilder().setText("Model Name").setType(EdmSimpleType.String).build()));

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("PurchaseDate",
                client.getPrimitiveValueBuilder().setText(purchaseDate).setType(EdmSimpleType.DateTime).build()));


        // add Dimensions attribute (collection)
        final ODataComplexValue dimensions = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions");
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Width",
                client.getPrimitiveValueBuilder().setText(dimensionValue).setType(EdmSimpleType.Decimal).build()));
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Height",
                client.getPrimitiveValueBuilder().setText(dimensionValue).setType(EdmSimpleType.Decimal).build()));
        dimensions.add(client.getObjectFactory().newPrimitiveProperty("Depth",
                client.getPrimitiveValueBuilder().setText(dimensionValue).setType(EdmSimpleType.Decimal).build()));

        entity.addProperty(client.getObjectFactory().newComplexProperty("Dimensions",
                dimensions));
        return entity;
    }
    // create an entity

    public void createEntity(
            final String serviceRootURL,
            final ODataPubFormat format,
            final ODataEntity original,
            final String entitySetName,
            final String contentType,
            final String prefer) {

        final URIBuilder uriBuilder = client.getURIBuilder(serviceRootURL);
        uriBuilder.appendEntitySetSegment(entitySetName);
        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), original);
        createReq.setFormat(format);
        createReq.setContentType(contentType);
        createReq.setPrefer(prefer);

        final ODataEntityCreateResponse createRes = createReq.execute();

        if (prefer.equals("return-no-content")) {
            assertEquals(204, createRes.getStatusCode());
        } else {
            assertEquals(201, createRes.getStatusCode());
            assertEquals("Created", createRes.getStatusMessage());
            assertTrue(createRes.getHeader("DataServiceVersion").contains("3.0;"));
            final ODataEntity created = createRes.getBody();
            assertNotNull(created);
        }

    }
    // validate newly created entities

    public ODataEntity validateEntities(final String serviceRootURL,
            final ODataPubFormat format,
            final ODataEntity original,
            final int actualObjectId,
            final Collection<String> expands, final String entitySetName) {

        final URIBuilder uriBuilder = client.getURIBuilder(serviceRootURL).
                appendEntityTypeSegment(entitySetName).appendKeySegment(actualObjectId);

        // search expanded
        if (expands != null) {
            for (String expand : expands) {
                uriBuilder.expand(expand);
            }
        }
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());

        final ODataEntity actual = res.getBody();
        assertNotNull(actual);

        validateLinks(original.getAssociationLinks(), actual.getAssociationLinks());
        validateLinks(original.getEditMediaLinks(), actual.getEditMediaLinks());
        validateLinks(original.getNavigationLinks(), actual.getNavigationLinks());

        // validate equalities of properties
        checkProperties(original.getProperties(), actual.getProperties());
        return actual;
    }
    // creates dynamic property for open type entity.

    public void getOpenTypeEntity(final ODataPubFormat format, final String contentType,
            final int id, final String prefer, String uuid) {
        final UUID guid;
        if (uuid.equals("random")) {
            guid = UUID.randomUUID();
        } else {
            guid = UUID.fromString(uuid);
        }
        ODataEntity entity = client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.OpenTypesService.Row");
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Id",
                client.getPrimitiveValueBuilder().setType(EdmSimpleType.Guid).setValue(guid).build()));
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("LongValue",
                client.getPrimitiveValueBuilder().setType(EdmSimpleType.Int64).setValue(44L).build()));
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("DoubleValue",
                client.getPrimitiveValueBuilder().setType(EdmSimpleType.Double).setValue(4.34567D).build()));

        final ODataEntityCreateRequest createReq = client.getCUDRequestFactory().
                getEntityCreateRequest(client.getURIBuilder(testOpenTypeServiceRootURL).
                appendEntityTypeSegment("Row").build(), entity);
        createReq.setFormat(format);
        createReq.setPrefer(prefer);
        createReq.setContentType(contentType);
        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());
        entity = createRes.getBody();
        assertNotNull(entity);
        if (format.equals(ODataPubFormat.JSON_NO_METADATA)) {
            assertEquals(EdmSimpleType.String.toString(), entity.getProperty("Id").getPrimitiveValue().getTypeName());
            assertEquals(EdmSimpleType.String.toString(), entity.getProperty("LongValue").getPrimitiveValue().
                    getTypeName());
            assertEquals(EdmSimpleType.Double.toString(), entity.getProperty("DoubleValue").getPrimitiveValue().
                    getTypeName());
        } else if (format.equals(ODataPubFormat.JSON)) {
            assertEquals(EdmSimpleType.String.toString(), entity.getProperty("Id").getPrimitiveValue().getTypeName());
            assertEquals(EdmSimpleType.Int64.toString(), entity.getProperty("LongValue").getPrimitiveValue().
                    getTypeName());
            assertEquals(EdmSimpleType.Double.toString(), entity.getProperty("DoubleValue").getPrimitiveValue().
                    getTypeName());
        } else {
            assertEquals(EdmSimpleType.Guid.toString(), entity.getProperty("Id").getPrimitiveValue().getTypeName());
            assertEquals(EdmSimpleType.Int64.toString(), entity.getProperty("LongValue").getPrimitiveValue().
                    getTypeName());
            assertEquals(EdmSimpleType.Double.toString(), entity.getProperty("DoubleValue").getPrimitiveValue().
                    getTypeName());
        }
        ODataDeleteResponse deleteRes;
        try {
            deleteRes = client.getCUDRequestFactory().getDeleteRequest(
                    new URI(testOpenTypeServiceRootURL + "/Row(guid'" + guid + "')")).execute();
            assertEquals(204, deleteRes.getStatusCode());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            LOG.error("", e);
        }
    }
    // creates dynamic property for open type entity (Invalid Guid test)

    public void getOpenTypeEntityWithString(final ODataPubFormat format, final String contentType,
            final int id, final String prefer, String uuid) {
        try {
            ODataEntity entity = client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.OpenTypesService.Row");
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Id",
                    client.getPrimitiveValueBuilder().setType(EdmSimpleType.Guid).setValue(uuid).build()));
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("LongValue",
                    client.getPrimitiveValueBuilder().setType(EdmSimpleType.Int64).setValue(44L).build()));
            entity.addProperty(client.getObjectFactory().newPrimitiveProperty("DoubleValue",
                    client.getPrimitiveValueBuilder().setType(EdmSimpleType.Double).setValue(4.34567D).build()));

            final ODataEntityCreateRequest createReq = client.getCUDRequestFactory().
                    getEntityCreateRequest(client.getURIBuilder(testOpenTypeServiceRootURL).
                    appendEntityTypeSegment("Row").build(), entity);
            createReq.setFormat(format);
            createReq.setPrefer(prefer);
            createReq.setContentType(contentType);
            final ODataEntityCreateResponse createRes = createReq.execute();
        } catch (Exception e) {
            if (e.getMessage().equals("Provided value is not compatible with Edm.Guid")) {
                assertTrue(true);
            } else {
                fail(e.getMessage());
            }
        }
    }
    // delete an entity and associated links after creation

    public void delete(final ODataPubFormat format, final ODataEntity created, final boolean includeInline,
            final String baseUri) {

        final Set<URI> toBeDeleted = new HashSet<URI>();
        toBeDeleted.add(created.getEditLink());

        if (includeInline) {
            for (ODataLink link : created.getNavigationLinks()) {
                if (link instanceof ODataInlineEntity) {
                    final ODataEntity inline = ((ODataInlineEntity) link).getEntity();
                    if (inline.getEditLink() != null) {
                        toBeDeleted.add(URIUtils.getURI(baseUri, inline.getEditLink().toASCIIString()));
                    }
                }
                if (link instanceof ODataInlineEntitySet) {
                    final ODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
                    for (ODataEntity entity : inline.getEntities()) {
                        if (entity.getEditLink() != null) {
                            toBeDeleted.add(URIUtils.getURI(baseUri, entity.getEditLink().toASCIIString()));
                        }
                    }
                }
            }
        }
        assertFalse(toBeDeleted.isEmpty());

        for (URI link : toBeDeleted) {
            final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(link);
            deleteReq.setFormat(format);
            final ODataDeleteResponse deleteRes = deleteReq.execute();
            assertEquals(204, deleteRes.getStatusCode());
            assertEquals("No Content", deleteRes.getStatusMessage());

            deleteRes.close();
        }
    }
}
