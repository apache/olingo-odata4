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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ODataCollectionValue;
import org.apache.olingo.client.api.domain.ODataComplexValue;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.domain.ODataPrimitiveValue;
import org.apache.olingo.client.api.domain.ODataProperty;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.junit.Test;

public class PropertyRetrieveTestITCase extends AbstractV3TestITCase {

  private void retreivePropertyTest(final ODataFormat format, String entitySegment, String structuralSegment) {
    final URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment(entitySegment).appendPropertySegment(structuralSegment);
    final ODataPropertyRequest req = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
    req.setFormat(format);
    try {
      final ODataProperty property = req.execute().getBody();
      assertNotNull(property);
      if (property.hasNullValue()) {
        assertNull(property.getValue());
      } else if (property.hasPrimitiveValue()) {
        final ODataPrimitiveValue value = property.getPrimitiveValue();
        assertTrue(value.isPrimitive());
      } else if (property.hasComplexValue()) {
        final ODataComplexValue value = property.getComplexValue();
        assertTrue(value.isComplex());
      } else if (property.hasCollectionValue()) {
        final ODataCollectionValue value = property.getCollectionValue();
        assertTrue(value.isCollection());
      }
    } catch (ODataClientErrorException e) {
      if (e.getStatusLine().getStatusCode() != 404
              && e.getStatusLine().getStatusCode() != 400) {
        fail(e.getMessage());
      }
    }
  }
  //test with json header

  @Test
  public void jsonRetrieveProperty() {
    //Primitive types
    retreivePropertyTest(ODataFormat.JSON, "Customer(-10)", "Name");
    retreivePropertyTest(ODataFormat.JSON, "Customer(-10)", "CustomerId");
    retreivePropertyTest(ODataFormat.JSON, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retreivePropertyTest(ODataFormat.JSON, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    //Collection of Complex types
    retreivePropertyTest(ODataFormat.JSON, "Customer(-10)", "BackupContactInfo");
    //Collection of primitives
    retreivePropertyTest(ODataFormat.JSON, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    //complex types
    retreivePropertyTest(ODataFormat.JSON, "Order(-9)", "Concurrency");
  }
  //test with json full metadata

  @Test
  public void jsonFullMetadataRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)", "Name");
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)", "CustomerId");
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    //Collection of Complex types
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)", "BackupContactInfo");
    //Collection of primitives		
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    //Complex types
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Order(-9)", "Concurrency");
  }
  // json with no metadata

  @Test
  public void jsonNoMetadataRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "Name");
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "CustomerId");
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    //Collection of Complex types
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "BackupContactInfo");
    //Collection of Primitives
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    //Complex types
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Order(-9)", "Concurrency");

  }
  // json with minimla metadata

  @Test
  public void jsonmininalRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "Name");
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "CustomerId");
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    //Collection of complex types
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)", "BackupContactInfo");
    //Collection of primitives
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    //Complex types
    retreivePropertyTest(ODataFormat.JSON_NO_METADATA, "Order(-9)", "Concurrency");
  }
  // with xml header

  @Test
  public void xmlRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)", "Name");
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)", "CustomerId");
    retreivePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retreivePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    //Collection of Complex types
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)", "BackupContactInfo");
    //Collection of primitives
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    //Complex types
    retreivePropertyTest(ODataFormat.XML, "Order(-9)", "Concurrency");
  }
  // with atom header

  @Test
  public void atomRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)", "Name");
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)", "CustomerId");
    retreivePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "Sent");
    retreivePropertyTest(ODataFormat.XML, "Message(FromUsername='1',MessageId=-10)", "IsRead");
    //Collection of Complex types 
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)", "BackupContactInfo");
    //Collection of primitives
    retreivePropertyTest(ODataFormat.XML, "Customer(-10)/PrimaryContactInfo", "EmailBag");
    //complex types
    retreivePropertyTest(ODataFormat.XML, "Order(-9)", "Concurrency");
  }
  // with invalid structural segment

  @Test
  public void invalidSegmentRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.XML, "Customers(-10)", "Name");

  }
  // with null pub format

  @Test
  public void nullSegmentRetrieveProperty() {
    //primitive types
    retreivePropertyTest(null, "Customers(-10)", "Name");

  }
  // with null accept header format

  @Test
  public void nullAcceptRetrieveProperty() {
    //primitive types
    retreivePropertyTest(ODataFormat.XML, "Customers(-10)", "Name");

  }
  // with json pub format and atom accept format

  @Test
  public void differentFormatAndAcceptRetrieveProperty() {
    //
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Customers(-10)", "Name");

  }
  //bad request 400 error. Message takes two keys

  @Test
  public void badRequestTest() {
    //primitive types
    retreivePropertyTest(ODataFormat.JSON_FULL_METADATA, "Message(FromUsername='1')", "Sent");
  }
  //navigation link of stream

  @Test
  public void navigationMediaLink() {
    URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendNavigationSegment("Product").appendKeySegment(-7).appendLinksSegment("Photos");
    ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setAccept("application/json");
    ODataRetrieveResponse<ODataEntitySet> res = req.execute();
    assertEquals(200, res.getStatusCode());
    ODataEntitySet entitySet = res.getBody();
    assertNotNull(entitySet);
    List<ODataEntity> entity = entitySet.getEntities();
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
  //navigation link of stream, Bad Request(404 error). 'Photo' is not a valid navigation link

  @Test
  public void navigationMediaLinkInvalidQuery() {
    URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendNavigationSegment("Product").appendKeySegment(-7).appendLinksSegment("Photo");
    ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setAccept("application/json");
    try {
      ODataRetrieveResponse<ODataEntitySet> res = req.execute();
      assertEquals(200, res.getStatusCode());
      ODataEntitySet entitySet = res.getBody();
      assertNotNull(entitySet);
      List<ODataEntity> entity = entitySet.getEntities();
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
    URIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendNavigationSegment("Product").appendKeySegment(-7).appendLinksSegment("Photos");
    ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setAccept("application/atom+xml");
    try {
      ODataRetrieveResponse<ODataEntitySet> res = req.execute();
      assertEquals(200, res.getStatusCode());
      ODataEntitySet entitySet = res.getBody();
      assertNotNull(entitySet);
      List<ODataEntity> entity = entitySet.getEntities();
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
