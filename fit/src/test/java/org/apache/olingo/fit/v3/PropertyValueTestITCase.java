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

import java.io.IOException;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class PropertyValueTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveIntPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-10).appendPropertySegment("ProductId");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    final ODataValue value = req.execute().getBody();
    assertNotNull(value);
    assertEquals(-10, Integer.parseInt(value.toString()));
  }

  @Test
  public void retrieveBooleanPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-10).appendPropertySegment("ProductId");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    final ODataValue value = req.execute().getBody();
    assertNotNull(value);
    assertEquals(-10, Integer.parseInt(value.toString()));
  }

  @Test
  public void retrieveStringPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-6).appendPropertySegment("Description");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    final ODataValue value = req.execute().getBody();
    assertNotNull(value);
    assertEquals("expdybhclurfobuyvzmhkgrnrajhamqmkhqpmiypittnp", value.toString());
  }

  @Test
  public void retrieveDatePropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-7).appendPropertySegment(
            "NestedComplexConcurrency/ModifiedDate");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    final ODataValue value = req.execute().getBody();
    assertNotNull(value);
    assertEquals("7866-11-16T22:25:52.747755", value.toString());
  }

  @Test
  public void retrieveDecimalPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-6).appendPropertySegment("Dimensions/Height");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    final ODataValue value = req.execute().getBody();
    assertNotNull(value);
    assertEquals("-79228162514264337593543950335", value.toString());
  }

  @Test
  public void retrieveBinaryPropertyValueTest() throws IOException {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendNavigationSegment("ProductPhoto(PhotoId=-3,ProductId=-3)").appendPropertySegment("Photo");
    ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setAccept("application/json");
    ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());
    ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals("fi653p3+MklA/LdoBlhWgnMTUUEo8tEgtbMXnF0a3CUNL9BZxXpSRiD9ebTnmNR0zWPjJ"
        + "VIDx4tdmCnq55XrJh+RW9aI/b34wAogK3kcORw=",
        entity.getProperties().get(0).getValue().toString());
  }

  @Test(expected = ODataClientErrorException.class)
  public void retrieveBinaryPropertyValueTestWithAtom() throws IOException {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendNavigationSegment("ProductPhoto(PhotoId=-3,ProductId=-3)").appendPropertySegment("Photo");
    ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setAccept("application/atom+xml");
    ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());
    res.getBody();
  }

  @Test(expected = IllegalArgumentException.class)
  public void retrieveBinaryPropertyValueTestWithXML() throws IOException {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendNavigationSegment("ProductPhoto(PhotoId=-3,ProductId=-3)").appendPropertySegment("Photo");
    ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setAccept("application/xml");
    ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());
    res.getBody();
  }

  @Test
  public void retrieveCollectionPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-7).appendPropertySegment(
            "ComplexConcurrency/QueriedDateTime");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ODataFormat.TEXT_PLAIN);
    final ODataValue value = req.execute().getBody();
    if (value.isPrimitive()) {
      assertNotNull(value);
      assertEquals("2013-09-18T00:44:43.6196168", value.toString());
    }
  }

  @Test
  public void retrieveNullPropertyValueTest() {
    CommonURIBuilder<?> uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-10).appendPropertySegment(
            "ComplexConcurrency/Token");
    final ODataValueRequest req = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    try {
      req.execute().getBody();
    } catch (ODataClientErrorException e) {
      assertEquals(404, e.getStatusLine().getStatusCode());
    }
  }
}
