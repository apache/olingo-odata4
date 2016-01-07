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

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;

public class OrderBySystemQueryITCase extends AbstractParamTecSvcITCase {

  private static final String ES_TWO_PRIM = "ESTwoPrim";
  private static final String ES_ALL_PRIM = "ESAllPrim";

  @Test
  public void simpleOrderBy() {
    ODataRetrieveResponse<ClientEntitySet> response = null;

    response = sendRequest(ES_ALL_PRIM, "PropertyDate");
    assertEquals(3, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertEquals("0", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(1);
    assertEquals("32767", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(2);
    assertEquals("-32768", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void simpleOrderByDescending() {
    ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyDate desc");
    assertEquals(3, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(1);
    assertEquals("32767", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(2);
    assertEquals("0", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void multipleOrderBy() {
    final ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_ALL_PRIM, "PropertyByte,PropertyInt16");
    assertEquals(3, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertEquals("-32768", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(1);
    assertEquals("0", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(2);
    assertEquals("32767", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void multipleOrderByDescending() {
    final ODataRetrieveResponse<ClientEntitySet> response =
        sendRequest(ES_ALL_PRIM, "PropertyByte,PropertyInt16 desc");
    assertEquals(3, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertEquals("0", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(1);
    assertEquals("-32768", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(2);
    assertEquals("32767", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void orderByWithNull() {
    final ODataRetrieveResponse<ClientEntitySet> response = sendRequest(ES_TWO_PRIM, "PropertyString");
    assertEquals(4, response.getBody().getEntities().size());

    ClientEntity clientEntity = response.getBody().getEntities().get(0);
    assertEquals("-32766", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(1);
    assertEquals("32766", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(2);
    assertEquals("-365", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());

    clientEntity = response.getBody().getEntities().get(3);
    assertEquals("32767", ((ClientValuable) clientEntity.getProperty("PropertyInt16")).getValue().toString());
  }

  @Test
  public void orderByInvalidExpression() {
    fail(ES_TWO_PRIM, "PropertyString add 10", HttpStatusCode.BAD_REQUEST);
  }

  private ODataRetrieveResponse<ClientEntitySet> sendRequest(final String entitySet, final String orderByString) {
    final URI uri =
        getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(entitySet)
        .orderBy(orderByString)
        .build();

    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
    setCookieHeader(request);

    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    return response;
  }

  private void fail(final String entitySet, final String filterString, final HttpStatusCode errorCode) {
    try {
      sendRequest(entitySet, filterString);
      Assert.fail();
    } catch (ODataClientErrorException e) {
      assertEquals(errorCode.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
}
