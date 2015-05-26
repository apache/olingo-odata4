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

import static org.junit.Assert.*;

import java.net.URI;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class EdmEnabledClientITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String NAV_PROPERTY_ET_KEY_NAV_ONE = "NavPropertyETKeyNavOne";

  @Test
  public void readSingleValuedNavigationPropertyWithNullValue() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(3)
                          .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                          .build();
    
    
    final ODataRetrieveResponse<ClientEntity> response = getClient().getRetrieveRequestFactory()
                                                                    .getEntityRequest(uri)
                                                                    .execute();
    
    assertEquals(0, response.getBody().getNavigationLinks().size());
    assertNull(response.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
  }

  @Override
  protected ODataClient getClient() {
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    return client;
  }
}
