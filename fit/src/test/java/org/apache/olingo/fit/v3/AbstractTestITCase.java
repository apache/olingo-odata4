/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v3.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.junit.BeforeClass;

public abstract class AbstractTestITCase extends AbstractBaseTestITCase {

  protected static final FullQualifiedName TEST_PRODUCT_TYPE =
      new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService.Product");

  protected static ODataClient client;

  protected static String testStaticServiceRootURL;

  protected static String testKeyAsSegmentServiceRootURL;

  protected static String testActionOverloadingServiceRootURL;

  protected static String testOpenTypeServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static String testAuthServiceRootURL;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/stub/StaticService/V30/Static.svc";
    testKeyAsSegmentServiceRootURL = "http://localhost:9080/stub/StaticService/V30/KeyAsSegment.svc";
    testActionOverloadingServiceRootURL = "http://localhost:9080/stub/StaticService/V30/ActionOverloading.svc";
    testOpenTypeServiceRootURL = "http://localhost:9080/stub/StaticService/V30/OpenType.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/stub/StaticService/V30/Static.svc/large";
    testAuthServiceRootURL = "http://localhost:9080/stub/DefaultService.svc/V30/Static.svc";

    client.getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
  }

  @BeforeClass
  public static void setClientInstance() {
    client = ODataClientFactory.getV3();
  }

  @Override
  protected ODataClient getClient() {
    return client;
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
        final CommonODataEntity originalInline = ((ODataInlineEntity) foundOriginal).getEntity();
        assertNotNull(originalInline);

        final CommonODataEntity actualInline = ((ODataInlineEntity) foundActual).getEntity();
        assertNotNull(actualInline);

        checkProperties(originalInline.getProperties(), actualInline.getProperties());
      }
    }
  }

  protected void checkProperties(final Collection<? extends CommonODataProperty> original,
      final Collection<? extends CommonODataProperty> actual) {

    assertTrue(original.size() <= actual.size());

    // re-organize actual properties into a Map<String, ODataProperty>
    final Map<String, CommonODataProperty> actualProps = new HashMap<String, CommonODataProperty>(actual.size());

    for (CommonODataProperty prop : actual) {
      assertFalse(actualProps.containsKey(prop.getName()));
      actualProps.put(prop.getName(), prop);
    }

    assertTrue(actual.size() <= actualProps.size());

    for (CommonODataProperty prop : original) {
      assertNotNull(prop);
      if (actualProps.containsKey(prop.getName())) {
        final CommonODataProperty actualProp = actualProps.get(prop.getName());
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

    assertEquals("Type mismatch for '" + propertyName + "': "
        + original.getClass().getSimpleName() + "-" + actual.getClass().getSimpleName(),
        original.getClass().getSimpleName(), actual.getClass().getSimpleName());

    if (original.isComplex()) {
      final List<ODataProperty> originalFileds = new ArrayList<ODataProperty>();
      for (ODataProperty prop : original.<ODataProperty> asComplex()) {
        originalFileds.add(prop);
      }

      final List<ODataProperty> actualFileds = new ArrayList<ODataProperty>();
      for (ODataProperty prop : actual.<ODataProperty> asComplex()) {
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
      assertTrue("Primitive value for '" + propertyName + "' type mismatch: " + original.asPrimitive().
          getTypeKind() + "-" + actual.asPrimitive().getTypeKind(),
          original.asPrimitive().getTypeKind().equals(actual.asPrimitive().getTypeKind()));

      assertEquals("Primitive value for '" + propertyName + "' mismatch: " + original.asPrimitive().toString()
          + "-" + actual.asPrimitive().toString(),
          original.asPrimitive().toString(), actual.asPrimitive().toString());
    }
  }

  protected ODataEntity getSampleCustomerInfo(final String sampleinfo) {
    final ODataEntity entity = getClient().getObjectFactory().newEntity(new FullQualifiedName(
        "Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo"));
    entity.setMediaEntity(true);

    getClient().getBinder().add(entity,
        getClient().getObjectFactory().newPrimitiveProperty("Information",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString(sampleinfo)));

    return entity;
  }

  protected ODataEntity getSampleCustomerProfile(
      final int id, final String sampleName, final boolean withInlineInfo) {

    final ODataEntity entity = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer"));

    // add name attribute
    getClient().getBinder().add(entity,
        getClient().getObjectFactory().newPrimitiveProperty("Name",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString(sampleName)));

    // add key attribute
    getClient().getBinder().add(entity,
        getClient().getObjectFactory().newPrimitiveProperty("CustomerId",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));

    // add BackupContactInfo attribute (collection)
    final ODataCollectionValue<ODataValue> backupContactInfoValue = getClient().getObjectFactory().newCollectionValue(
        "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");
    getClient().getBinder().add(entity,
        getClient().getObjectFactory().newCollectionProperty("BackupContactInfo", backupContactInfoValue));

    // add BackupContactInfo.ContactDetails attribute (complex)
    final ODataComplexValue<ODataProperty> contactDetails = getClient().getObjectFactory().newComplexValue(
        "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");
    backupContactInfoValue.add(contactDetails);

    // add BackupContactInfo.ContactDetails.AlternativeNames attribute (collection)
    final ODataCollectionValue<ODataValue> altNamesValue = getClient().getObjectFactory().
        newCollectionValue("Collection(Edm.String)");
    altNamesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("myname"));
    contactDetails.add(getClient().getObjectFactory().newCollectionProperty("AlternativeNames", altNamesValue));

    // add BackupContactInfo.ContactDetails.EmailBag attribute (collection)
    final ODataCollectionValue<ODataValue> emailBagValue = getClient().getObjectFactory().
        newCollectionValue("Collection(Edm.String)");
    emailBagValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("myname@mydomain.com"));
    contactDetails.add(getClient().getObjectFactory().newCollectionProperty("EmailBag", emailBagValue));

    // add BackupContactInfo.ContactDetails.ContactAlias attribute (complex)
    final ODataComplexValue<ODataProperty> contactAliasValue = getClient().getObjectFactory().newComplexValue(
        "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
    contactDetails.add(getClient().getObjectFactory().newComplexProperty("ContactAlias", contactAliasValue));

    // add BackupContactInfo.ContactDetails.ContactAlias.AlternativeNames attribute (collection)
    final ODataCollectionValue<ODataValue> aliasAltNamesValue = getClient().getObjectFactory().
        newCollectionValue("Collection(Edm.String)");
    aliasAltNamesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("myAlternativeName"));
    contactAliasValue.add(getClient().getObjectFactory().newCollectionProperty("AlternativeNames", aliasAltNamesValue));

    if (withInlineInfo) {
      final ODataInlineEntity inlineInfo = getClient().getObjectFactory().newDeepInsertEntity(
          "Info",
          getSampleCustomerInfo(sampleName + "_Info"));
      inlineInfo.getEntity().setMediaEntity(true);
      entity.addLink(inlineInfo);
    }

    return entity;
  }

  protected String getETag(final URI uri) {
    final ODataRetrieveResponse<ODataEntity> res = getClient().getRetrieveRequestFactory().
        getEntityRequest(uri).execute();
    try {
      return res.getETag();
    } finally {
      res.close();
    }
  }

  protected ODataEntity read(final ODataFormat format, final URI editLink) {
    final ODataEntityRequest<ODataEntity> req = getClient().getRetrieveRequestFactory().
        getEntityRequest(editLink);
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);

    if (ODataFormat.JSON_FULL_METADATA == format || ODataFormat.ATOM == format) {
      assertEquals(req.getURI(), entity.getEditLink());
    }

    return entity;
  }

  protected ODataEntity createEntity(
      final String serviceRootURL,
      final ODataFormat format,
      final ODataEntity original,
      final String entitySetName) {

    final URIBuilder uriBuilder = getClient().newURIBuilder(serviceRootURL).
        appendEntitySetSegment(entitySetName);

    debugODataEntity(original, "About to create");

    final ODataEntityCreateRequest<ODataEntity> createReq =
        getClient().getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), original);
    createReq.setFormat(format);

    final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());
    assertEquals("Created", createRes.getStatusMessage());

    final ODataEntity created = createRes.getBody();
    assertNotNull(created);

    debugODataEntity(created, "Just created");

    return created;
  }

  protected ODataEntity compareEntities(final String serviceRootURL,
      final ODataFormat format,
      final ODataEntity original,
      final int actualObjectId,
      final Collection<String> expands) {

    final URIBuilder uriBuilder = getClient().newURIBuilder(serviceRootURL).
        appendEntitySetSegment("Customer").appendKeySegment(actualObjectId);

    // search expanded
    if (expands != null) {
      for (String expand : expands) {
        uriBuilder.expand(expand);
      }
    }

    final ODataEntityRequest<ODataEntity> req = getClient().getRetrieveRequestFactory().
        getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final ODataEntity actual = res.getBody();
    assertNotNull(actual);

    // check defined links
    checkLinks(original.getAssociationLinks(), actual.getAssociationLinks());
    checkLinks(original.getMediaEditLinks(), actual.getMediaEditLinks());
    checkLinks(original.getNavigationLinks(), actual.getNavigationLinks());

    // check defined properties equality
    checkProperties(original.getProperties(), actual.getProperties());

    return actual;
  }

  protected void cleanAfterCreate(
      final ODataFormat format,
      final ODataEntity created,
      final boolean includeInline,
      final String baseUri) {

    final Set<URI> toBeDeleted = new HashSet<URI>();
    toBeDeleted.add(created.getEditLink());

    if (includeInline) {
      for (ODataLink link : created.getNavigationLinks()) {
        if (link instanceof ODataInlineEntity) {
          final CommonODataEntity inline = ((ODataInlineEntity) link).getEntity();
          if (inline.getEditLink() != null) {
            toBeDeleted.add(URIUtils.getURI(baseUri, inline.getEditLink().toASCIIString()));
          }
        }

        if (link instanceof ODataInlineEntitySet) {
          final CommonODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
          for (CommonODataEntity entity : inline.getEntities()) {
            if (entity.getEditLink() != null) {
              toBeDeleted.add(URIUtils.getURI(baseUri, entity.getEditLink().toASCIIString()));
            }
          }
        }
      }
    }

    assertFalse(toBeDeleted.isEmpty());

    for (URI link : toBeDeleted) {
      final ODataDeleteRequest deleteReq = getClient().getCUDRequestFactory().getDeleteRequest(link);
      final ODataDeleteResponse deleteRes = deleteReq.execute();

      assertEquals(204, deleteRes.getStatusCode());
      assertEquals("No Content", deleteRes.getStatusMessage());

      deleteRes.close();

      final ODataEntityRequest<ODataEntity> retrieveReq = getClient().getRetrieveRequestFactory().
          getEntityRequest(link);
      // bug that needs to be fixed on the SampleService - cannot get entity not found with header
      // Accept: application/json;odata=minimalmetadata
      retrieveReq.setFormat(format == ODataFormat.JSON_FULL_METADATA ? ODataFormat.JSON : format);

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
      final ODataFormat format, final ODataEntity changes, final UpdateType type) {

    updateEntityDescription(format, changes, type, null);
  }

  protected void updateEntityDescription(
      final ODataFormat format, final ODataEntity changes, final UpdateType type, final String etag) {

    updateEntityStringProperty("Description", format, changes, type, etag);
  }

  protected void updateEntityStringProperty(final String propertyName,
      final ODataFormat format, final ODataEntity changes, final UpdateType type, final String etag) {

    final URI editLink = changes.getEditLink();

    final String newm = "New " + propertyName + "(" + System.currentTimeMillis() + ")";

    ODataProperty propertyValue = changes.getProperty(propertyName);

    final String oldm;
    if (propertyValue == null) {
      oldm = null;
    } else {
      oldm = propertyValue.getValue().toString();
      changes.getProperties().remove(propertyValue);
    }

    assertNotEquals(newm, oldm);

    getClient().getBinder().add(changes,
        getClient().getObjectFactory().newPrimitiveProperty(propertyName,
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString(newm)));

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
      final UpdateType type, final ODataEntity changes, final ODataFormat format, final String etag) {

    final ODataEntityUpdateRequest<ODataEntity> req =
        getClient().getCUDRequestFactory().getEntityUpdateRequest(type, changes);

    if (getClient().getConfiguration().isUseXHTTPMethod()) {
      assertEquals(HttpMethod.POST, req.getMethod());
    } else {
      assertEquals(type.getMethod(), req.getMethod());
    }
    req.setFormat(format);

    if (StringUtils.isNotBlank(etag)) {
      req.setIfMatch(etag); // Product include ETag header into the response .....
    }

    final ODataEntityUpdateResponse<ODataEntity> res = req.execute();
    assertEquals(204, res.getStatusCode());
  }
}
