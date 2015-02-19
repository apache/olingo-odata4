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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataValuable;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class OrderBySystemQueryITCase extends AbstractBaseTestITCase {

  private static final String ES_TWO_PRIM = "ESTwoPrim";
  private static final String ES_ALL_PRIM = "ESAllPrim";

  private static final String SERVICE_URI = TecSvcConst.BASE_URI;

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Test
  public void testSimpleOrderBy() {
    ODataRetrieveResponse<ODataEntitySet> response = null;

    response = sendRequest(ES_ALL_PRIM, "PropertyDate");
    assertEquals(3, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(2);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testSimpleOrderByDecending() {
    ODataRetrieveResponse<ODataEntitySet> response = null;

    response = sendRequest(ES_ALL_PRIM, "PropertyDate desc");
    assertEquals(3, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(2);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testMultipleOrderBy() {
    final ODataRetrieveResponse<ODataEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyByte, PropertyInt16");
    assertEquals(3, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(2);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testMultipleOrderByDecending() {
    final ODataRetrieveResponse<ODataEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyByte, PropertyInt16 desc");
    assertEquals(3, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("0", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("-32768", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(2);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testOrderByWithNull() {
    final ODataRetrieveResponse<ODataEntitySet> response = sendRequest(ES_TWO_PRIM, "PropertyString");
    assertEquals(4, response.getBody().getEntities().size());

    ODataEntity oDataEntity = response.getBody().getEntities().get(0);
    assertEquals("-32766", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(1);
    assertEquals("32766", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(2);
    assertEquals("-365", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());

    oDataEntity = response.getBody().getEntities().get(3);
    assertEquals("32767", ((ODataValuable) oDataEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void testOrderByInvalidExpression() {
    fail(ES_TWO_PRIM, "PropertyString add 10", HttpStatusCode.BAD_REQUEST);   
  }

  private ODataRetrieveResponse<ODataEntitySet> sendRequest(String entitySet, String orderByString) {
    final ODataClient client = getClient();
    String escapedFilterString = escapeFilterString(orderByString);

    final URI uri =
        client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(entitySet)
            .orderBy(escapedFilterString)
            .build();

    ODataEntitySetRequest<ODataEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);

    return request.execute();
  }

  private void fail(String entitySet, String filterString, HttpStatusCode errorCode) {
    try {
      sendRequest(entitySet, filterString);
      Assert.fail();
    } catch (ODataClientErrorException e) {
      assertEquals(errorCode.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  private String escapeFilterString(String filterString) {
    return filterString.replace(" ", "%20");
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getV4();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
