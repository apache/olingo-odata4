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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataDeleteResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.msopentech.odatajclient.engine.data.Entry;
import com.msopentech.odatajclient.engine.data.Feed;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataInlineEntity;
import com.msopentech.odatajclient.engine.data.ODataLink;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomEntry;
import com.msopentech.odatajclient.engine.data.impl.v3.JSONEntry;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataInlineEntitySet;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.client.ODataClientFactory;
import com.msopentech.odatajclient.engine.client.ODataV3Client;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestITCase {

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractTestITCase.class);

    protected static final String TEST_PRODUCT_TYPE = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product";

    protected static final String servicesODataServiceRootURL =
            "http://services.odata.org/V3/(S(csquyjnoaywmz5xcdbfhlc1p))/OData/OData.svc/";

    protected static ODataV3Client client;

    protected static String testDefaultServiceRootURL;

    protected static String testStaticServiceRootURL;

    protected static String testActionOverloadingServiceRootURL;

    protected static String testKeyAsSegmentServiceRootURL;

//    protected static String testODataWriterDefaultServiceRootURL;

    protected static String testOpenTypeServiceRootURL;

    protected static String testPrimitiveKeysServiceRootURL;

    protected static String testLargeModelServiceRootURL;

    protected static String testAuthServiceRootURL;

    @BeforeClass
    public static void setUpODataServiceRoot() throws IOException {
        String testBaseURL = null;

        InputStream propStream = null;
        try {
            propStream = AbstractTestITCase.class.getResourceAsStream("/test.properties");
            final Properties props = new Properties();
            props.load(propStream);

            testBaseURL = props.getProperty("test.base.url");
        } catch (Exception e) {
            LOG.error("Could not load test.properties", e);
        } finally {
            if (propStream != null) {
                propStream.close();
            }
        }
        assertNotNull("Check value for the 'test.base.url' property", testBaseURL);

        testDefaultServiceRootURL = testBaseURL + "/DefaultService.svc";
        testStaticServiceRootURL = "http://localhost:9080/StaticService/V3/Static.svc";
        testActionOverloadingServiceRootURL = testBaseURL + "/ActionOverloadingService.svc";
        testKeyAsSegmentServiceRootURL = testBaseURL + "/KeyAsSegmentService.svc";
//        testODataWriterDefaultServiceRootURL = testBaseURL + "/ODataWriterDefaultService.svc";
        testOpenTypeServiceRootURL = testBaseURL + "/OpenTypeService.svc";
        testPrimitiveKeysServiceRootURL = "http://localhost:9080/StaticService/V3/Static.svc";
        testLargeModelServiceRootURL = "http://localhost:9080/StaticService/V3/Static.svc/large";
        testAuthServiceRootURL = "http://localhost:9080/DefaultService.svc";
    }

    /**
     * This is needed for correct number handling (Double, for example).
     */
    @BeforeClass
    public static void setEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @BeforeClass
    public static void setClientInstance() {
        client = ODataClientFactory.getV3();
    }

    protected void checkLinks(final Collection<ODataLink> original, final Collection<ODataLink> actual) {
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

    protected void checkProperties(final Collection<ODataProperty> original, final Collection<ODataProperty> actual) {
        assertTrue(original.size() <= actual.size());

        // re-organize actual properties into a Map<String, ODataProperty>
        final Map<String, ODataProperty> actualProps = new HashMap<String, ODataProperty>(actual.size());

        for (ODataProperty prop : actual) {
            assertFalse(actualProps.containsKey(prop.getName()));
            actualProps.put(prop.getName(), prop);
        }

        assertTrue(actual.size() <= actualProps.size());

        for (ODataProperty prop : original) {
            assertNotNull(prop);
            if (actualProps.containsKey(prop.getName())) {
                final ODataProperty actualProp = actualProps.get(prop.getName());
                assertNotNull(actualProp);

                if (prop.getValue() != null && actualProp.getValue() != null) {
                    checkPropertyValue(prop.getName(), prop.getValue(), actualProp.getValue());
                }
            } else {
                // nothing ... maybe :FC_KeepInContent="false"
                // ..... no assert can be done ....
            }
        }
    }

    protected void checkPropertyValue(final String propertyName,
            final ODataValue original, final ODataValue actual) {

        assertNotNull("Null original value for " + propertyName, original);
        assertNotNull("Null actual value for " + propertyName, actual);

        assertEquals("Type mismatch for '" + propertyName + "'",
                original.getClass().getSimpleName(), actual.getClass().getSimpleName());

        if (original.isComplex()) {
            final List<ODataProperty> originalFileds = new ArrayList<ODataProperty>();
            for (ODataProperty prop : original.asComplex()) {
                originalFileds.add(prop);
            }

            final List<ODataProperty> actualFileds = new ArrayList<ODataProperty>();
            for (ODataProperty prop : (ODataComplexValue) actual) {
                actualFileds.add(prop);
            }

            checkProperties(originalFileds, actualFileds);
        } else if (original.isCollection()) {
            assertTrue(original.asCollection().size() <= actual.asCollection().size());

            boolean found = original.asCollection().isEmpty();

            for (ODataValue originalValue : original.asCollection()) {
                for (ODataValue actualValue : actual.asCollection()) {
                    try {
                        checkPropertyValue(propertyName, originalValue, actualValue);
                        found = true;
                    } catch (AssertionError ignore) {
                        // ignore
                    }
                }
            }

            assertTrue("Found " + actual + " but expected " + original, found);
        } else {
            assertTrue("Primitive value for '" + propertyName + "' type mismatch",
                    original.asPrimitive().getTypeName().equals(actual.asPrimitive().getTypeName()));

            assertEquals("Primitive value for '" + propertyName + "' mismatch",
                    original.asPrimitive().toString(), actual.asPrimitive().toString());
        }
    }

    protected ODataEntity getSampleCustomerInfo(final int id, final String sampleinfo) {
        final ODataEntity entity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo");
        entity.setMediaEntity(true);

        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Information",
                client.getPrimitiveValueBuilder().setText(sampleinfo).setType(
                EdmSimpleType.String).build()));

        return entity;
    }

    protected ODataEntity getSampleCustomerProfile(
            final int id, final String sampleName, final boolean withInlineInfo) {

        final ODataEntity entity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");

        // add name attribute
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("Name",
                client.getPrimitiveValueBuilder().setText(sampleName).setType(
                EdmSimpleType.String).build()));

        // add key attribute
        entity.addProperty(client.getObjectFactory().newPrimitiveProperty("CustomerId",
                client.getPrimitiveValueBuilder().setText(String.valueOf(id)).setType(
                EdmSimpleType.Int32).build()));

        // add BackupContactInfo attribute (collection)
        final ODataCollectionValue backupContactInfoValue = new ODataCollectionValue(
                "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");
        entity.addProperty(client.getObjectFactory().newCollectionProperty("BackupContactInfo",
                backupContactInfoValue));

        // add BackupContactInfo.ContactDetails attribute (complex)
        final ODataComplexValue contactDetails = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");
        backupContactInfoValue.add(contactDetails);

        // add BackupContactInfo.ContactDetails.AlternativeNames attribute (collection)
        final ODataCollectionValue altNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        altNamesValue.add(client.getPrimitiveValueBuilder().
                setText("myname").setType(EdmSimpleType.String).build());
        contactDetails.add(client.getObjectFactory().newCollectionProperty("AlternativeNames", altNamesValue));

        // add BackupContactInfo.ContactDetails.EmailBag attribute (collection)
        final ODataCollectionValue emailBagValue = new ODataCollectionValue("Collection(Edm.String)");
        emailBagValue.add(client.getPrimitiveValueBuilder().
                setText("myname@mydomain.com").setType(EdmSimpleType.String).build());
        contactDetails.add(client.getObjectFactory().newCollectionProperty("EmailBag", emailBagValue));

        // add BackupContactInfo.ContactDetails.ContactAlias attribute (complex)
        final ODataComplexValue contactAliasValue = new ODataComplexValue(
                "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
        contactDetails.add(client.getObjectFactory().newComplexProperty("ContactAlias", contactAliasValue));

        // add BackupContactInfo.ContactDetails.ContactAlias.AlternativeNames attribute (collection)
        final ODataCollectionValue aliasAltNamesValue = new ODataCollectionValue("Collection(Edm.String)");
        aliasAltNamesValue.add(client.getPrimitiveValueBuilder().
                setText("myAlternativeName").setType(EdmSimpleType.String).build());
        contactAliasValue.add(client.getObjectFactory().newCollectionProperty("AlternativeNames", aliasAltNamesValue));

        if (withInlineInfo) {
            final ODataInlineEntity inlineInfo = client.getObjectFactory().newInlineEntity(
                    "Info",
                    URI.create("Customer(" + id + ")/Info"),
                    getSampleCustomerInfo(id, sampleName + "_Info"));
            inlineInfo.getEntity().setMediaEntity(true);
            entity.addLink(inlineInfo);
        }

        return entity;
    }

    protected void debugEntry(final Entry entry, final String message) {
        if (LOG.isDebugEnabled()) {
            final StringWriter writer = new StringWriter();
            client.getSerializer().entry(entry, writer);
            writer.flush();
            LOG.debug(message + "\n{}", writer.toString());
        }
    }

    protected void debugFeed(final Feed feed, final String message) {
        if (LOG.isDebugEnabled()) {
            final StringWriter writer = new StringWriter();
            client.getSerializer().feed(feed, writer);
            writer.flush();
            LOG.debug(message + "\n{}", writer.toString());
        }
    }

    protected void debugODataProperty(final ODataProperty property, final String message) {
        LOG.debug(message + "\n{}", property.toString());
    }

    protected void debugODataValue(final ODataValue value, final String message) {
        LOG.debug(message + "\n{}", value.toString());
    }

    protected void debugODataEntity(final ODataEntity entity, final String message) {
        if (LOG.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            client.getSerializer().entry(client.getBinder().getEntry(entity, AtomEntry.class), writer);
            writer.flush();
            LOG.debug(message + " (Atom)\n{}", writer.toString());

            writer = new StringWriter();
            client.getSerializer().entry(client.getBinder().getEntry(entity, JSONEntry.class), writer);
            writer.flush();
            LOG.debug(message + " (JSON)\n{}", writer.toString());
        }
    }

    protected void debugInputStream(final InputStream input, final String message) {
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug(message + "\n{}", IOUtils.toString(input));
            } catch (IOException e) {
                LOG.error("Error writing stream", e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
    }

    protected String getETag(final URI uri) {
        final ODataRetrieveResponse<ODataEntity> res = client.getRetrieveRequestFactory().
                getEntityRequest(uri).execute();
        try {
            return res.getEtag();
        } finally {
            res.close();
        }
    }

    protected ODataEntity read(final ODataPubFormat format, final URI editLink) {
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(editLink);
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        final ODataEntity entity = res.getBody();

        assertNotNull(entity);

        if (ODataPubFormat.JSON_FULL_METADATA == format || ODataPubFormat.ATOM == format) {
            assertEquals(req.getURI(), entity.getEditLink());
        }

        return entity;
    }

    protected ODataEntity createEntity(
            final String serviceRootURL,
            final ODataPubFormat format,
            final ODataEntity original,
            final String entitySetName) {

        final URIBuilder uriBuilder = client.getURIBuilder(serviceRootURL);
        uriBuilder.appendEntitySetSegment(entitySetName);

        debugODataEntity(original, "About to create");

        final ODataEntityCreateRequest createReq =
                client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), original);
        createReq.setFormat(format);

        final ODataEntityCreateResponse createRes = createReq.execute();
        assertEquals(201, createRes.getStatusCode());
        assertEquals("Created", createRes.getStatusMessage());

        final ODataEntity created = createRes.getBody();
        assertNotNull(created);

        debugODataEntity(created, "Just created");

        return created;
    }

    protected ODataEntity compareEntities(final String serviceRootURL,
            final ODataPubFormat format,
            final ODataEntity original,
            final int actualObjectId,
            final Collection<String> expands) {

        final URIBuilder uriBuilder = client.getURIBuilder(serviceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(actualObjectId);

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

        // check defined links
        checkLinks(original.getAssociationLinks(), actual.getAssociationLinks());
        checkLinks(original.getEditMediaLinks(), actual.getEditMediaLinks());
        checkLinks(original.getNavigationLinks(), actual.getNavigationLinks());

        // check defined properties equality
        checkProperties(original.getProperties(), actual.getProperties());

        return actual;
    }

    protected void cleanAfterCreate(
            final ODataPubFormat format,
            final ODataEntity created,
            final boolean includeInline,
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
            final ODataDeleteResponse deleteRes = deleteReq.execute();

            assertEquals(204, deleteRes.getStatusCode());
            assertEquals("No Content", deleteRes.getStatusMessage());

            deleteRes.close();

            final ODataEntityRequest retrieveReq = client.getRetrieveRequestFactory().getEntityRequest(link);
            // bug that needs to be fixed on the SampleService - cannot get entity not found with header
            // Accept: application/json;odata=minimalmetadata
            retrieveReq.setFormat(format == ODataPubFormat.JSON_FULL_METADATA ? ODataPubFormat.JSON : format);

            Exception exception = null;
            try {
                retrieveReq.execute();
                fail();
            } catch (ODataClientErrorException e) {
                exception = e;
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
            assertNotNull(exception);
        }
    }

    protected void updateEntityDescription(
            final ODataPubFormat format, final ODataEntity changes, final UpdateType type) {

        updateEntityDescription(format, changes, type, null);
    }

    protected void updateEntityDescription(
            final ODataPubFormat format, final ODataEntity changes, final UpdateType type, final String etag) {

        updateEntityStringProperty("Description", format, changes, type, etag);
    }

    protected void updateEntityStringProperty(final String propertyName,
            final ODataPubFormat format, final ODataEntity changes, final UpdateType type, final String etag) {

        final URI editLink = changes.getEditLink();

        final String newm = "New " + propertyName + "(" + System.currentTimeMillis() + ")";

        ODataProperty propertyValue = changes.getProperty(propertyName);

        final String oldm;
        if (propertyValue == null) {
            oldm = null;
        } else {
            oldm = propertyValue.getValue().toString();
            changes.removeProperty(propertyValue);
        }

        assertNotEquals(newm, oldm);

        changes.addProperty(client.getObjectFactory().newPrimitiveProperty(propertyName,
                client.getPrimitiveValueBuilder().setText(newm).build()));

        update(type, changes, format, etag);

        final ODataEntity actual = read(format, editLink);

        propertyValue = null;

        for (ODataProperty prop : actual.getProperties()) {
            if (prop.getName().equals(propertyName)) {
                propertyValue = prop;
            }
        }

        assertNotNull(propertyValue);
        assertEquals(newm, propertyValue.getValue().toString());
    }

    protected void update(
            final UpdateType type, final ODataEntity changes, final ODataPubFormat format, final String etag) {
        final ODataEntityUpdateRequest req = client.getCUDRequestFactory().getEntityUpdateRequest(type, changes);

        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, req.getMethod());
        } else {
            assertEquals(type.getMethod(), req.getMethod());
        }
        req.setFormat(format);

        if (StringUtils.isNotBlank(etag)) {
            req.setIfMatch(etag); // Product include ETag header into the response .....
        }

        final ODataEntityUpdateResponse res = req.execute();
        assertEquals(204, res.getStatusCode());
    }
}
