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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class PropertyRetrieveTestITCase extends AbstractTestITCase {

  private void
      retrievePropertyTest(final ODataFormat format, final String entitySegment, final String structuralSegment) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment(entitySegment).appendPropertySegment(structuralSegment);
    final ODataPropertyRequest<ODataProperty> req = client.getRetrieveRequestFactory().
        getPropertyRequest(uriBuilder.build());
    req.setFormat(format);
    try {
      final ODataProperty property = req.execute().getBody();
      assertNotNull(property);
      if (property.hasNullValue()) {
        assertTrue(property.getValue() == null
            || property.getValue().isPrimitive() && property.getValue().asPrimitive().toValue() == null);
      } else if (property.hasPrimitiveValue()) {
        final ODataPrimitiveValue value = property.getPrimitiveValue();
        assertTrue(value.isPrimitive());
      } else if (property.hasComplexValue()) {
        final ODataComplexValue<?> value = property.getComplexValue();
        assertTrue(value.isComplex());
      } else if (property.hasCollectionValue()) {
        final ODataCollectionValue<?> value = property.getCollectionValue();
        assertTrue(value.isCollection());
      }
    } catch (ODataClientErrorException e) {
      if (e.getStatusLine().getStatusCode() != 404 && e.getStatusLine().getStatusCode() != 400) {
        fail(e.getMessage());
      }
    }
  }

  // test with json header

  @Test
  public void jsonRetrieveProperty() {
    // Primitive types
    retrievePropertyTest(ODataFormat.JSON, "Customer(-10)", "Name");
    retrievePropertyTest(ODataFormat.JSON, "Customer(-10)", "CustomerId");
    retrievePropertyTest(ODataFormat.JSON, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retrievePropertyTest(ODataFormat.JSON, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    // Collection of Complex types
    retrievePropertyTest(ODataFormat.JSON, "Customer(-10)", "BackupContactInfo");
    // Collection of primitives
    retrievePropertyTest(ODataFormat.JSON, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    // complex types
    retrievePropertyTest(ODataFormat.JSON, "Order(-9)", "Concurrency");
  }

  // test with json full metadata

  @Test
  public void jsonFullMetadataRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)", "Name");
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)", "CustomerId");
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    // Collection of Complex types
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)", "BackupContactInfo");
    // Collection of primitives
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    // Complex types
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Order(-9)", "Concurrency");
  }

  // json with no metadata

  @Test
  public void jsonNoMetadataRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "Name");
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "CustomerId");
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    // Collection of Complex types
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "BackupContactInfo");
    // Collection of Primitives
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    // Complex types
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Order(-9)", "Concurrency");

  }

  // json with minimla metadata

  @Test
  public void jsonmininalRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "Name");
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "CustomerId");
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    // Collection of complex types
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "BackupContactInfo");
    // Collection of primitives
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    // Complex types
    retrievePropertyTest(ODataFormat.JSON_NO_METADATA, "Order(-9)", "Concurrency");
  }

  // with xml header

  @Test
  public void xmlRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)", "Name");
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)", "CustomerId");
    retrievePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retrievePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    // Collection of Complex types
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)", "BackupContactInfo");
    // Collection of primitives
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    // Complex types
    retrievePropertyTest(ODataFormat.XML, "Order(-9)", "Concurrency");
  }

  // with atom header

  @Test
  public void atomRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)", "Name");
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)", "CustomerId");
    retrievePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retrievePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    // Collection of Complex types
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)", "BackupContactInfo");
    // Collection of primitives
    retrievePropertyTest(ODataFormat.XML, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    // complex types
    retrievePropertyTest(ODataFormat.XML, "Order(-9)", "Concurrency");
  }

  // with invalid structural segment

  @Test
  public void invalidSegmentRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.XML, "Customers(-10)", "Name");

  }

  // with null pub format

  @Test
  public void nullSegmentRetrieveProperty() {
    // primitive types
    retrievePropertyTest(null, "Customers(-10)", "Name");

  }

  // with null accept header format

  @Test
  public void nullAcceptRetrieveProperty() {
    // primitive types
    retrievePropertyTest(ODataFormat.XML, "Customers(-10)", "Name");

  }

  // with json pub format and atom accept format

  @Test
  public void differentFormatAndAcceptRetrieveProperty() {
    //
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customers(-10)", "Name");

  }

  // bad request 400 error. Message takes two keys

  @Test
  public void badRequestTest() {
    // primitive types
    retrievePropertyTest(ODataFormat.JSON_FULL_METADATA, "Message(FromUsername='1')", "Sent");
  }

  // navigation link of stream

  @Test
  public void navigationMediaLink() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendNavigationSegment("Product").appendKeySegment(-7).appendLinksSegment("Photos");
    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.build());
    req.setAccept("application/json");
    final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
    assertEquals(200, res.getStatusCode());
    final ODataEntitySet entitySet = res.getBody();
    assertNotNull(entitySet);
    final List<? extends CommonODataEntity> entity = entitySet.getEntities();
    assertNotNull(entity);
    assertEquals(entity.size(), 2);
    assertEquals(testStaticServiceRootURL + "/ProductPhoto(PhotoId=-3,ProductId=-3)",
        entity.get(0).getProperties().get(0).getValue().toString());
    assertEquals(testStaticServiceRootURL + "/ProductPhoto(PhotoId=-2,ProductId=-2)",
        entity.get(1).getProperties().get(0).getValue().toString());
    for (int i = 0; i < entity.size(); i++) {
      assertNotNull(entity.get(0).getProperties().get(0).getValue());
    }
  }

  // navigation link of stream, Bad Request(404 error). 'Photo' is not a valid navigation link

  @Test
  public void navigationMediaLinkInvalidQuery() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendNavigationSegment("Product").appendKeySegment(-7).appendLinksSegment("Photo");
    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.build());
    req.setAccept("application/json");
    try {
      final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
      assertEquals(200, res.getStatusCode());
      ODataEntitySet entitySet = res.getBody();
      assertNotNull(entitySet);
      final List<? extends CommonODataEntity> entity = entitySet.getEntities();
      assertNotNull(entity);
      assertEquals(entity.size(), 2);
      assertEquals(testStaticServiceRootURL + "/ProductPhoto(PhotoId=-3,ProductId=-3)", entity.get(0).
          getProperties().get(0).getValue().toString());
      assertEquals(testStaticServiceRootURL + "/ProductPhoto(PhotoId=-2,ProductId=-2)", entity.get(1).
          getProperties().get(0).getValue().toString());
    } catch (ODataClientErrorException e) {
      assertEquals(404, e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void navigationMediaLinkInvalidFormat() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendNavigationSegment("Product").appendKeySegment(-7).appendLinksSegment("Photos");
    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.build());
    req.setAccept("application/atom+xml");
    try {
      final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
      assertEquals(200, res.getStatusCode());
      final ODataEntitySet entitySet = res.getBody();
      assertNotNull(entitySet);
      final List<ODataEntity> entity = entitySet.getEntities();
      assertNotNull(entity);
      assertEquals(entity.size(), 2);
      assertEquals(testStaticServiceRootURL + "/ProductPhoto(PhotoId=-3,ProductId=-3)", entity.get(0).
          getProperties().get(0).getValue().toString());
      assertEquals(testStaticServiceRootURL + "/ProductPhoto(PhotoId=-2,ProductId=-2)", entity.get(1).
          getProperties().get(0).getValue().toString());
    } catch (ODataClientErrorException e) {
      assertEquals(415, e.getStatusLine().getStatusCode());
    }
  }
}
