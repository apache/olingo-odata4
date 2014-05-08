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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

public class DerivedTypeTestITCase extends AbstractTestITCase {

  private void read(final ODataPubFormat format) {
    // 1. entity set
    URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").
            appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.ODataWCFService.Customer");
    ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
            getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);

    for (ODataEntity customer : req.execute().getBody().getEntities()) {
      assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", customer.getTypeName().toString());
    }

    // 2. contained entity set
    uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(101).
            appendNavigationSegment("MyPaymentInstruments").
            appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.ODataWCFService.CreditCardPI");
    req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);

    for (ODataEntity customer : req.execute().getBody().getEntities()) {
      assertEquals("Microsoft.Test.OData.Services.ODataWCFService.CreditCardPI", customer.getTypeName().toString());
    }
  }

  @Test
  public void readfromAtom() {
    read(ODataPubFormat.ATOM);
  }

  @Test
  public void readfromJSON() {
    read(ODataPubFormat.JSON_FULL_METADATA);
  }
}
