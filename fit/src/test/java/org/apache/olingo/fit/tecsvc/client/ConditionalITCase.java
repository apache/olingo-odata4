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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.ODataError;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public final class ConditionalITCase extends AbstractBaseTestITCase {

  @Test
  public void readWithWrongIfMatch() throws Exception {
    final ODataClient client = getClient();
    ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory().getEntityRequest(
        client.newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESCompAllPrim").appendKeySegment(0).build());
    request.setIfMatch("W/\"1\"");
    assertNotNull(request);

    try {
      request.execute();
      fail("Expected Exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.PRECONDITION_FAILED.getStatusCode(), e.getStatusLine().getStatusCode());
      final ODataError error = e.getODataError();
      assertThat(error.getMessage(), containsString("condition"));
    }
  }

  @Test
  public void readNotModified() throws Exception {
    final ODataClient client = getClient();
    ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory().getEntityRequest(
        client.newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESCompAllPrim").appendKeySegment(0).build());
    request.setIfNoneMatch("W/\"0\"");
    assertNotNull(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NOT_MODIFIED.getStatusCode(), response.getStatusCode());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
