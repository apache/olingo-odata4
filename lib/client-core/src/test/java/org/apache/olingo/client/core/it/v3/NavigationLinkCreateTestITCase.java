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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
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
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Ignore;
import org.junit.Test;

public class NavigationLinkCreateTestITCase extends AbstractTestITCase {

  // create navigation link with ATOM
  @Test
  public void createNavWithAtom() {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final String contentType = "application/atom+xml";
    final String prefer = "return-content";
    final ODataEntity actual = createNavigation(format, 20, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // create navigation link with JSON full metadata

  @Test
  public void createNavWithJSONFullMetadata() {
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final String contentType = "application/json;odata=fullmetadata";
    final String prefer = "return-content";
    final ODataEntity actual = createNavigation(format, 21, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // throws Null pointer exception when the format is JSON No metadata

  @Test(expected = HttpClientException.class)
  public void createNavWithJSONNoMetadata() {
    final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
    final String contentType = "application/json;odata=nometadata";
    final String prefer = "return-content";
    final ODataEntity actual = createNavigation(format, 22, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // test with JSON accept and atom content type

  @Test
  @Ignore
  public void createNavWithJSONAndATOM() {
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final String contentType = "application/atom+xml";
    final String prefer = "return-content";
    final ODataEntity actual = createNavigation(format, 23, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // test with JSON full metadata in format and json no metadata in content type

  @Test
  public void createNavWithDiffJSON() {
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final String contentType = "application/json;odata=nometadata";
    final String prefer = "return-content";
    final ODataEntity actual = createNavigation(format, 24, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // test with JSON no metadata format and json no metadata in content type

  @Test(expected = HttpClientException.class)
  public void createNavWithNoMetadata() {
    final ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
    final String contentType = "application/json;odata=fullmetadata";
    final String prefer = "return-content";
    final ODataEntity actual = createNavigation(format, 25, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // create collection navigation link with ATOM

  @Test
  public void createCollectionNavWithAtom() throws EdmPrimitiveTypeException {
    final ODataPubFormat format = ODataPubFormat.ATOM;
    final String contentType = "application/atom+xml";
    final String prefer = "return-content";
    final ODataEntity actual = createCollectionNavigation(format, 55, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }
  // create collection navigation link with JSON

  @Test
  public void createCollectionNavWithJSON() throws EdmPrimitiveTypeException {
    final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
    final String contentType = "application/json;odata=fullmetadata";
    final String prefer = "return-content";
    final ODataEntity actual = createCollectionNavigation(format, 77, contentType, prefer);
    delete(format, actual, false, testStaticServiceRootURL);
  }

  // create a navigation link
  public ODataEntity createNavigation(final ODataPubFormat format, final int id, final String contenttype,
          final String prefer) {
    final String name = "Customer Navigation test";

    final ODataEntity original = getNewCustomer(id, name, false);
    original.addLink(client.getObjectFactory().newEntityNavigationLink(
            "Info", URI.create(testStaticServiceRootURL + "/CustomerInfo(11)")));
    final ODataEntity created = createNav(testStaticServiceRootURL, format, original, "Customer", contenttype,
            prefer);

    final ODataEntity actual = validateEntities(testStaticServiceRootURL, format, created, id, null, "Customer");

    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL);
    uriBuilder.appendEntitySetSegment("Customer").appendKeySegment(id).appendEntitySetSegment("Info");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);
    req.setContentType(contenttype);
    req.setPrefer(prefer);
    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());
    assertTrue(res.getHeader("DataServiceVersion").contains("3.0;"));
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    for (ODataProperty prop : entity.getProperties()) {
      if ("CustomerInfoId".equals(prop.getName())) {
        assertEquals("11", prop.getValue().toString());
      }
    }
    return actual;
  }

  // create a navigation link
  public ODataEntity createNav(final String url, final ODataPubFormat format, final ODataEntity original,
          final String entitySetName, final String contentType, final String prefer) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(url);
    uriBuilder.appendEntitySetSegment(entitySetName);
    final ODataEntityCreateRequest<ODataEntity> createReq =
            client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), original);
    createReq.setFormat(format);
    createReq.setContentType(contentType);
    createReq.setPrefer(prefer);
    final ODataEntityCreateResponse<ODataEntity> createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());

    assertEquals("Created", createRes.getStatusMessage());

    final ODataEntity created = createRes.getBody();
    assertNotNull(created);
    return created;
  }
  // create collection navigation link

  public ODataEntity createCollectionNavigation(final ODataPubFormat format, final int id,
          final String contentType, final String prefer) throws EdmPrimitiveTypeException {
    {
      final String name = "Collection Navigation Key Customer";
      final ODataEntity original = getNewCustomer(id, name, false);

      final Set<Integer> navigationKeys = new HashSet<Integer>();
      navigationKeys.add(-118);
      navigationKeys.add(-119);

      for (Integer key : navigationKeys) {
        final ODataEntity orderEntity =
                client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Order");

        getClient().getBinder().add(orderEntity,
                client.getObjectFactory().newPrimitiveProperty("OrderId",
                        client.getObjectFactory().newPrimitiveValueBuilder().setValue(key).
                        setType(EdmPrimitiveTypeKind.Int32).build()));
        getClient().getBinder().add(orderEntity,
                client.getObjectFactory().newPrimitiveProperty("CustomerId",
                        client.getObjectFactory().newPrimitiveValueBuilder().setValue(id).
                        setType(EdmPrimitiveTypeKind.Int32).build()));

        final ODataEntityCreateRequest<ODataEntity> createReq = client.getCUDRequestFactory().getEntityCreateRequest(
                client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Order").build(),
                orderEntity);
        createReq.setFormat(format);
        createReq.setContentType(contentType);
        original.addLink(client.getObjectFactory().newEntitySetNavigationLink(
                "Orders",
                createReq.execute().getBody().getEditLink()));
      }
      final ODataEntity createdEntity = createNav(testStaticServiceRootURL, format, original, "Customer",
              contentType, prefer);
      final ODataEntity actualEntity =
              validateEntities(testStaticServiceRootURL, format, createdEntity, id, null, "Customer");

      final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL);
      uriBuilder.appendEntitySetSegment("Customer").appendKeySegment(id).appendEntitySetSegment("Orders");

      final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
              getEntitySetRequest(uriBuilder.build());
      req.setFormat(format);

      final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
      assertEquals(200, res.getStatusCode());

      final ODataEntitySet entitySet = res.getBody();
      assertNotNull(entitySet);

      assertEquals(2, entitySet.getCount());

      for (ODataEntity entity : entitySet.getEntities()) {
        final Integer key = entity.getProperty("OrderId").getPrimitiveValue().toCastValue(Integer.class);
        final Integer customerId = entity.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class);
        assertTrue(navigationKeys.contains(key));
        assertEquals(Integer.valueOf(id), customerId);
        navigationKeys.remove(key);
        final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(
                URIUtils.getURI(testStaticServiceRootURL, entity.getEditLink().toASCIIString()));

        deleteReq.setFormat(format);
        assertEquals(204, deleteReq.execute().getStatusCode());
      }

      return actualEntity;
    }
  }
  // get a Customer entity to be created

  public ODataEntity getNewCustomer(
          final int id, final String name, final boolean withInlineInfo) {

    final ODataEntity entity =
            client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer");

    // add name attribute
    getClient().getBinder().add(entity,
            client.getObjectFactory().newPrimitiveProperty("Name",
                    client.getObjectFactory().newPrimitiveValueBuilder().setText(name).
                    setType(EdmPrimitiveTypeKind.String).build()));

    // add key attribute
    if (id != 0) {
      getClient().getBinder().add(entity,
              client.getObjectFactory().newPrimitiveProperty("CustomerId",
                      client.getObjectFactory().newPrimitiveValueBuilder().setText(String.valueOf(id)).
                      setType(EdmPrimitiveTypeKind.Int32).build()));
    }
    final ODataCollectionValue<ODataValue> backupContactInfoValue = getClient().getObjectFactory().newCollectionValue(
            "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)");

    final ODataComplexValue<ODataProperty> contactDetails = getClient().getObjectFactory().newComplexValue(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");

    final ODataCollectionValue<ODataValue> altNamesValue = getClient().getObjectFactory().
            newCollectionValue("Collection(Edm.String)");
    altNamesValue.add(client.getObjectFactory().newPrimitiveValueBuilder().
            setText("My Alternative name").setType(EdmPrimitiveTypeKind.String).build());
    contactDetails.add(client.getObjectFactory().newCollectionProperty("AlternativeNames", altNamesValue));

    final ODataCollectionValue<ODataValue> emailBagValue = getClient().getObjectFactory().
            newCollectionValue("Collection(Edm.String)");
    emailBagValue.add(client.getObjectFactory().newPrimitiveValueBuilder().
            setText("altname@mydomain.com").setType(EdmPrimitiveTypeKind.String).build());
    contactDetails.add(client.getObjectFactory().newCollectionProperty("EmailBag", emailBagValue));

    final ODataComplexValue<ODataProperty> contactAliasValue = getClient().getObjectFactory().newComplexValue(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases");
    contactDetails.add(client.getObjectFactory().newComplexProperty("ContactAlias", contactAliasValue));

    final ODataCollectionValue<ODataValue> aliasAltNamesValue = getClient().getObjectFactory().
            newCollectionValue("Collection(Edm.String)");
    aliasAltNamesValue.add(client.getObjectFactory().newPrimitiveValueBuilder().
            setText("myAlternativeName").setType(EdmPrimitiveTypeKind.String).build());
    contactAliasValue.add(client.getObjectFactory().newCollectionProperty("AlternativeNames", aliasAltNamesValue));

    final ODataComplexValue<ODataProperty> homePhone = getClient().getObjectFactory().newComplexValue(
            "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone");
    homePhone.add(client.getObjectFactory().newPrimitiveProperty("PhoneNumber",
            client.getObjectFactory().newPrimitiveValueBuilder().setText("8437568356834568").
            setType(EdmPrimitiveTypeKind.String).build()));
    homePhone.add(client.getObjectFactory().newPrimitiveProperty("Extension",
            client.getObjectFactory().newPrimitiveValueBuilder().setText("124365426534621534423ttrf").
            setType(EdmPrimitiveTypeKind.String).
            build()));
    contactDetails.add(client.getObjectFactory().newComplexProperty("HomePhone", homePhone));

    backupContactInfoValue.add(contactDetails);
    getClient().getBinder().add(entity,
            client.getObjectFactory().newCollectionProperty("BackupContactInfo", backupContactInfoValue));
    if (withInlineInfo) {
      final ODataInlineEntity inlineInfo = client.getObjectFactory().newInlineEntity("Info", URI.create(
              "Customer(" + id
              + ")/Info"), getInfo(id, name + "_Info"));
      inlineInfo.getEntity().setMediaEntity(true);
      entity.addLink(inlineInfo);
    }

    return entity;
  }
  //delete an entity and associated links after creation

  public void delete(final ODataPubFormat format, final ODataEntity created, final boolean includeInline,
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
      final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(link);
      final ODataDeleteResponse deleteRes = deleteReq.execute();

      assertEquals(204, deleteRes.getStatusCode());
      assertEquals("No Content", deleteRes.getStatusMessage());

      deleteRes.close();
    }
  }
  // add Information property

  public ODataEntity getInfo(final int id, final String info) {
    final ODataEntity entity =
            client.getObjectFactory().newEntity("Microsoft.Test.OData.Services.AstoriaDefaultService.CustomerInfo");
    entity.setMediaEntity(true);

    getClient().getBinder().add(entity, client.getObjectFactory().newPrimitiveProperty("Information",
            client.getObjectFactory().newPrimitiveValueBuilder().setText(info).
            setType(EdmPrimitiveTypeKind.String).build()));
    return entity;
  }
  // validate newly created entities

  public ODataEntity validateEntities(final String serviceRootURL,
          final ODataPubFormat format,
          final ODataEntity original,
          final int actualObjectId,
          final Collection<String> expands, final String entitySetName) {

    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(serviceRootURL).
            appendEntitySetSegment(entitySetName).appendKeySegment(actualObjectId);

    if (expands != null) {
      for (String expand : expands) {
        uriBuilder.expand(expand);
      }
    }
    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final ODataEntity actual = res.getBody();
    assertNotNull(actual);

    validateLinks(original.getAssociationLinks(), actual.getAssociationLinks());
    validateLinks(original.getEditMediaLinks(), actual.getEditMediaLinks());
    validateLinks(original.getNavigationLinks(), actual.getNavigationLinks());

    checkProperties(original.getProperties(), actual.getProperties());
    return actual;
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
        final CommonODataEntity originalInline = ((ODataInlineEntity) foundOriginal).getEntity();
        assertNotNull(originalInline);

        final CommonODataEntity actualInline = ((ODataInlineEntity) foundActual).getEntity();
        assertNotNull(actualInline);

        checkProperties(originalInline.getProperties(), actualInline.getProperties());
      }
    }
  }
  // compares properties of the newly created entity with the properties that were originally provided

  @Override
  public void checkProperties(final Collection<? extends CommonODataProperty> original,
          final Collection<? extends CommonODataProperty> actual) {

    assertTrue(original.size() <= actual.size());

    final Map<String, CommonODataProperty> actualProperties = new HashMap<String, CommonODataProperty>(actual.size());

    for (CommonODataProperty prop : actual) {
      assertFalse(actualProperties.containsKey(prop.getName()));
      actualProperties.put(prop.getName(), prop);
    }

    assertTrue(actual.size() <= actualProperties.size());

    for (CommonODataProperty prop : original) {
      assertNotNull(prop);
      if (actualProperties.containsKey(prop.getName())) {
        final CommonODataProperty actualProp = actualProperties.get(prop.getName());
        assertNotNull(actualProp);

        if (prop.getValue() != null && actualProp.getValue() != null) {
          checkPropertyValue(prop.getName(), prop.getValue(), actualProp.getValue());
        }
      }
    }
  }
  // compares property value of the newly created entity with the property value that were originally provided

  @Override
  public void checkPropertyValue(final String propertyName,
          final ODataValue original, final ODataValue actual) {

    assertNotNull("Null original value for " + propertyName, original);
    assertNotNull("Null actual value for " + propertyName, actual);

    assertEquals("Type mismatch for '" + propertyName + "'",
            original.getClass().getSimpleName(), actual.getClass().getSimpleName());

    if (original.isComplex()) {
      final List<CommonODataProperty> originalPropertyValue = new ArrayList<CommonODataProperty>();
      for (CommonODataProperty prop : original.asComplex()) {
        originalPropertyValue.add(prop);
      }

      final List<CommonODataProperty> actualPropertyValue = new ArrayList<CommonODataProperty>();
      for (CommonODataProperty prop : actual.asComplex()) {
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
              original.asPrimitive().getTypeKind() == actual.asPrimitive().getTypeKind());

      assertEquals("Primitive value for '" + propertyName + "' mismatch",
              original.asPrimitive().toString(), actual.asPrimitive().toString());
    }
  }
}
